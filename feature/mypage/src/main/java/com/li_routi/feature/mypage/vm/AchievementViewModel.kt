package com.li_routi.feature.mypage.vm

import androidx.lifecycle.viewModelScope
import com.li_routi.core.common.android.architecture.BaseViewModel
import com.li_routi.core.common.kotlin.util.ResultState
import com.li_routi.core.data.di.AchievementContainer
import com.li_routi.core.data.di.RoutineContainer
import com.li_routi.core.domain.achievement.Achievement
import com.li_routi.core.domain.achievement.AchievementCategory
import com.li_routi.core.domain.achievement.AchievementClaimResult
import com.li_routi.core.domain.achievement.AchievementConditionProgress
import com.li_routi.core.domain.achievement.ClaimAchievementUseCase
import com.li_routi.core.domain.achievement.ClearRepresentativeAchievementUseCase
import com.li_routi.core.domain.achievement.GetAchievementsUseCase
import com.li_routi.core.domain.achievement.GetSelectableRepresentativeAchievementsUseCase
import com.li_routi.core.domain.achievement.GetWaveRoutineStatusUseCase
import com.li_routi.core.domain.achievement.SelectWaveRoutineUseCase
import com.li_routi.core.domain.achievement.SetRepresentativeAchievementUseCase
import com.li_routi.core.domain.achievement.WaveRoutineStatus
import com.li_routi.core.domain.routine.CreatedRoutine
import com.li_routi.core.domain.routine.GetMemberRoutinesUseCase
import com.li_routi.feature.mypage.component.AchievementBadgeUiModel
import com.li_routi.feature.mypage.component.AchievementConditionUiModel
import com.li_routi.feature.mypage.component.AchievementIcons
import com.li_routi.feature.mypage.component.AchievementRarity
import com.li_routi.feature.mypage.component.AchievementUiModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch

/**
 * 업적 화면 ViewModel. `GET /api/achievements`로 카테고리(rare/epic/unique/egg)별 업적 목록을 조회한다.
 *
 * 문서의 `summary`(획득/진행 중 개수 상단 요약)는 예시 스키마가 챌린지 응답과 뒤섞여 있어 정확한 모양을
 * 알 수 없고, 화면도 총 개수를 목록에서 직접 세고 있어 지금은 매핑하지 않는다
 * ([com.li_routi.core.data.network.dto.response.AchievementsResponse] 참고).
 *
 * "달성" 탭 배지 그리드는 실제로 달성 시점이 찍힌(`isAchieved`, [Achievement] 문서 참고) 업적을
 * 전부 모은다. `badgeYn`은 같이 있는 `limitedOutfitYn`(한정 의상 보상 여부)과 대구를 이루는 "보상
 * 종류" 플래그로 보여 — 이 업적을 달성하면 배지 "종류"의 보상을 주는지를 나타낼 뿐, "달성 화면에
 * 보여줄지"를 결정하는 값이 아니다. 예전엔 `isAchieved && badgeYn`로 걸러서 보상이 배지가 아닌
 * 달성 업적(예: 토파즈만 주는 업적)이 달성 탭에서 통째로 빠지는 문제가 있었다.
 */
class AchievementViewModel(
    private val getAchievementsUseCase: GetAchievementsUseCase = AchievementContainer.getAchievementsUseCase,
    private val claimAchievementUseCase: ClaimAchievementUseCase = AchievementContainer.claimAchievementUseCase,
    private val getWaveRoutineStatusUseCase: GetWaveRoutineStatusUseCase = AchievementContainer.getWaveRoutineStatusUseCase,
    private val selectWaveRoutineUseCase: SelectWaveRoutineUseCase = AchievementContainer.selectWaveRoutineUseCase,
    private val getMemberRoutinesUseCase: GetMemberRoutinesUseCase = RoutineContainer.getMemberRoutinesUseCase,
    private val getSelectableRepresentativeAchievementsUseCase: GetSelectableRepresentativeAchievementsUseCase =
        AchievementContainer.getSelectableRepresentativeAchievementsUseCase,
    private val setRepresentativeAchievementUseCase: SetRepresentativeAchievementUseCase =
        AchievementContainer.setRepresentativeAchievementUseCase,
    private val clearRepresentativeAchievementUseCase: ClearRepresentativeAchievementUseCase =
        AchievementContainer.clearRepresentativeAchievementUseCase,
) : BaseViewModel() {

    private val _uiState = MutableStateFlow(AchievementUiState(isLoading = true))
    val uiState: StateFlow<AchievementUiState> = _uiState.asStateFlow()

    init {
        load()
        loadWaveRoutineStatus()
        loadRepresentativeAchievement()
    }

    /**
     * 현재 대표로 설정된 업적 id를 서버에서 읽어온다. 대표 업적 상태는 서버가 진실의 원천이라(장착
     * 상태가 앱 재시작에도 유지돼야 하는 요구사항) 화면 로컬 상태(remember)로 관리하지 않는다.
     */
    private fun loadRepresentativeAchievement() {
        viewModelScope.launch {
            when (val result = getSelectableRepresentativeAchievementsUseCase()) {
                is ResultState.Success -> {
                    val representativeId = result.data.firstOrNull { it.isRepresentative }?.achievementId
                    _uiState.update { it.copy(representativeAchievementId = representativeId) }
                }
                is ResultState.Error, ResultState.Loading -> Unit
            }
        }
    }

    /**
     * "달성" 탭에서 배지를 탭함 — 이미 대표로 설정된 배지를 다시 탭하면 해제(DELETE)하고, 아니면 그
     * 배지로 새로 설정(PUT, 기존 대표는 자동으로 덮어써짐)한다. 배지 이미지가 없거나 아직 보상을
     * 수령하지 않은 업적을 선택하면 서버가 400/409로 거절하는데, 그 메시지를 그대로 토스트로 보여준다.
     */
    fun onBadgeEquipClick(achievementId: Long) {
        if (_uiState.value.isEquippingBadge) return
        val isUnequip = _uiState.value.representativeAchievementId == achievementId
        viewModelScope.launch {
            _uiState.update { it.copy(isEquippingBadge = true) }
            val result = if (isUnequip) {
                clearRepresentativeAchievementUseCase()
            } else {
                setRepresentativeAchievementUseCase(achievementId)
            }
            when (result) {
                is ResultState.Success -> _uiState.update {
                    it.copy(
                        isEquippingBadge = false,
                        representativeAchievementId = if (isUnequip) null else achievementId,
                    )
                }
                is ResultState.Error -> _uiState.update {
                    it.copy(isEquippingBadge = false, claimMessage = result.message)
                }
                ResultState.Loading -> Unit
            }
        }
    }

    private fun loadWaveRoutineStatus() {
        viewModelScope.launch {
            when (val result = getWaveRoutineStatusUseCase()) {
                is ResultState.Success -> _uiState.update { it.copy(waveRoutineStatus = result.data) }
                is ResultState.Error, ResultState.Loading -> Unit
            }
        }
    }

    /** "루틴 선택"/"변경" 탭 — 시트를 열고, 아직 안 불러왔으면 내 루틴 목록도 같이 불러온다. */
    fun onWaveRoutinePickerOpen() {
        _uiState.update { it.copy(isWaveRoutinePickerVisible = true) }
        if (_uiState.value.myRoutines.isNotEmpty()) return
        viewModelScope.launch {
            when (val result = getMemberRoutinesUseCase()) {
                is ResultState.Success -> _uiState.update { it.copy(myRoutines = result.data.routines) }
                is ResultState.Error -> _uiState.update { it.copy(claimMessage = result.message) }
                ResultState.Loading -> Unit
            }
        }
    }

    fun onWaveRoutinePickerDismiss() {
        _uiState.update { it.copy(isWaveRoutinePickerVisible = false) }
    }

    /** 시트에서 루틴을 고름 — 성공하면 시트를 닫고 상태를 다시 조회해 이름/스트릭을 최신화한다. */
    fun onWaveRoutineChosen(memberRoutineId: Long) {
        if (_uiState.value.isSelectingWaveRoutine) return
        _uiState.update { it.copy(isSelectingWaveRoutine = true) }
        viewModelScope.launch {
            when (val result = selectWaveRoutineUseCase(memberRoutineId)) {
                is ResultState.Success -> {
                    _uiState.update { it.copy(isSelectingWaveRoutine = false, isWaveRoutinePickerVisible = false) }
                    loadWaveRoutineStatus()
                }
                is ResultState.Error -> _uiState.update {
                    it.copy(isSelectingWaveRoutine = false, claimMessage = result.message)
                }
                ResultState.Loading -> Unit
            }
        }
    }

    /**
     * "받기" 버튼 탭 — 달성했지만 미수령인 업적의 보상을 수령한다. 성공하면 서버 상태가
     * `ACHIEVED`→`CLAIMED`로 바뀌므로 [load]로 다시 조회해 화면(버튼 사라짐)과 잔액을 최신화한다.
     */
    fun onClaimClick(achievementId: Long) {
        if (_uiState.value.isClaiming) return
        viewModelScope.launch {
            _uiState.update { it.copy(isClaiming = true) }
            when (val result = claimAchievementUseCase(achievementId)) {
                is ResultState.Success -> {
                    _uiState.update { state ->
                        state.copy(
                            isClaiming = false,
                            claimMessage = result.data.toMessage(),
                            // load()가 새 목록을 받아오기 전까지도 이 항목은 즉시 "받기" 불가로 바꿔둔다.
                            // isClaiming은 이 요청 하나만 막는 전역 가드라, 응답 직후부터 load() 완료
                            // 전까지의 틈에 같은 업적을 다시 탭하면 수령 API가 중복 호출될 수 있었다.
                            achievements = state.achievements.map {
                                if (it.achievementId == achievementId) it.copy(isClaimable = false) else it
                            },
                        )
                    }
                    load()
                }
                is ResultState.Error -> _uiState.update {
                    it.copy(isClaiming = false, claimMessage = result.message)
                }
                ResultState.Loading -> Unit
            }
        }
    }

    fun onClaimMessageDismissed() {
        _uiState.update { it.copy(claimMessage = null) }
    }

    private fun load() {
        viewModelScope.launch {
            _uiState.update { it.copy(isLoading = true, isError = false) }
            when (val result = getAchievementsUseCase()) {
                is ResultState.Success -> {
                    // hiddenYn으로 "달성 전 숨김" 필터를 걸었었으나, 스웨거 문서에 정확한 의미가
                    // 없어(필드명만 보고 추측) dev 서버 응답과 맞지 않아 업적 목록 전체가 사라지는
                    // 회귀가 있었다. 의미가 명확히 확인되기 전까지는 필터링하지 않는다 — 필드 자체는
                    // Achievement.hiddenYn에 남겨둬서 나중에 다시 쓸 수 있게 한다.
                    val visibleAchievements = result.data.flatMap { group ->
                        group.achievements.map { it to group.category }
                    }
                    _uiState.update {
                        it.copy(
                            achievements = visibleAchievements.map { (achievement, category) -> achievement.toUiModel(category) },
                            achievedBadges = visibleAchievements
                                .filter { (achievement, _) -> achievement.isAchieved }
                                .map { (achievement, category) ->
                                    AchievementBadgeUiModel(
                                        id = achievement.achievementId,
                                        title = achievement.name,
                                        rarity = category.toAchievementRarity(),
                                        iconRes = AchievementIcons.resolve(achievement.code),
                                        imageUrl = achievement.badgeImageUrl,
                                    )
                                },
                            isLoading = false,
                        )
                    }
                }
                is ResultState.Error -> _uiState.update { it.copy(isLoading = false, isError = true) }
                ResultState.Loading -> Unit
            }
        }
    }
}

data class AchievementUiState(
    val achievements: List<AchievementUiModel> = emptyList(),
    val achievedBadges: List<AchievementBadgeUiModel> = emptyList(),
    val isLoading: Boolean = false,
    val isError: Boolean = false,
    /** 보상 수령 요청 진행 중 — 중복 탭 방지. */
    val isClaiming: Boolean = false,
    /** 수령 성공/실패 메시지. 토스트로 보여주고 닫으면 지운다. */
    val claimMessage: String? = null,
    /** "파도타기"(연속 기록) 업적이 추적 중인 루틴. null이면 아직 못 불러온 상태 */
    val waveRoutineStatus: WaveRoutineStatus? = null,
    val isWaveRoutinePickerVisible: Boolean = false,
    /** 루틴 선택 시트에 뿌릴 내 루틴 목록. 시트를 처음 열 때 한 번만 불러와 캐싱한다 */
    val myRoutines: List<CreatedRoutine> = emptyList(),
    val isSelectingWaveRoutine: Boolean = false,
    /** 현재 대표로 설정된 업적 id. null이면 대표 업적 없음(또는 아직 서버 조회 전). */
    val representativeAchievementId: Long? = null,
    /** 대표 업적 설정/해제 요청 진행 중 — 중복 탭 방지. */
    val isEquippingBadge: Boolean = false,
)

private fun AchievementClaimResult.toMessage(): String =
    if (rewardApplied) "보상을 받았어요!" else "이미 받은 보상이에요."

private fun Achievement.toUiModel(category: AchievementCategory): AchievementUiModel = AchievementUiModel(
    title = name,
    rarity = category.toAchievementRarity(),
    // 숨김 업적은 서버가 conditionDesc를 아예 안 내려준다(조건을 감추는 의도) — 목록에서 빈 줄로
    // 보이지 않도록 안내 문구로 대체한다.
    description = conditionDesc ?: "달성 조건이 아직 공개되지 않았어요",
    // progressTarget이 0인 업적은 진행률 추적이 없는(달성 여부만 있는) 업적이다 — 달성했다면
    // "0/0"이 아니라 꽉 찬 상태로 보여야 한다. 진행 중인데 target이 0인 경우는 없다고 보고, 그
    // 외(진행 중이며 target>0)에는 기존처럼 실제 진행률을 쓴다.
    progressLabel = if (isAchieved && progressTarget <= 0) "1/1" else "$progressCurrent/$progressTarget",
    progress = when {
        isAchieved -> 1f
        progressTarget > 0 -> progressCurrent.toFloat() / progressTarget
        else -> 0f
    },
    // "토파즈"는 서버 필드명(topazReward)일 뿐, 실제 표기는 상점과 동일하게 "오렌지젬"이다
    // (CurrencyShopViewModel.toDisplayCurrency, Figma node 6389:17225/17281/18526 기준).
    rewardText = if (topazReward > 0) "+${topazReward}오렌지젬" else null,
    isInProgress = isInProgress,
    isAchieved = isAchieved,
    iconRes = AchievementIcons.resolve(code),
    imageUrl = badgeImageUrl,
    achievementId = achievementId,
    isClaimable = isClaimable,
    conditions = conditionProgresses.map { it.toUiModel() },
)

private fun AchievementConditionProgress.toUiModel(): AchievementConditionUiModel = AchievementConditionUiModel(
    label = conditionKey.toDisplayLabel(),
    progressLabel = "$current/$target",
    progress = if (target > 0) current.toFloat() / target else 0f,
)

// 서버 조건 키를 화면 문구로 바꾼다 — 목록에 없는(새로 추가된) 키는 원래 값 그대로 보여줘, 조건 자체가
// 안 보이는 것보단 낫게 한다.
private fun String.toDisplayLabel(): String = when (this) {
    "POKE_COUNT" -> "쿡쿡 수"
    "LIKE_COUNT" -> "좋아요 수"
    else -> this
}

private fun AchievementCategory.toAchievementRarity(): AchievementRarity = when (this) {
    AchievementCategory.RARE -> AchievementRarity.Rare
    AchievementCategory.EPIC -> AchievementRarity.Epic
    AchievementCategory.UNIQUE -> AchievementRarity.Unique
    AchievementCategory.EGG -> AchievementRarity.Character
    // 화면에 없는 5번째 등급이 새로 추가된 경우의 폴백 — 유니크로 묶어 최소한 어딘가엔 보이게 한다.
    AchievementCategory.UNKNOWN -> AchievementRarity.Unique
}
