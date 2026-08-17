package com.li_routi.feature.mypage.vm

import androidx.lifecycle.viewModelScope
import com.li_routi.core.common.android.architecture.BaseViewModel
import com.li_routi.core.common.kotlin.util.ResultState
import com.li_routi.core.data.di.AchievementContainer
import com.li_routi.core.domain.achievement.Achievement
import com.li_routi.core.domain.achievement.AchievementCategory
import com.li_routi.core.domain.achievement.AchievementClaimResult
import com.li_routi.core.domain.achievement.ClaimAchievementUseCase
import com.li_routi.core.domain.achievement.GetAchievementsUseCase
import com.li_routi.feature.mypage.component.AchievementBadgeUiModel
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
) : BaseViewModel() {

    private val _uiState = MutableStateFlow(AchievementUiState(isLoading = true))
    val uiState: StateFlow<AchievementUiState> = _uiState.asStateFlow()

    init {
        load()
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
                    _uiState.update { it.copy(isClaiming = false, claimMessage = result.data.toMessage()) }
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
                    val allAchievements = result.data.flatMap { group ->
                        group.achievements.map { it to group.category }
                    }
                    _uiState.update {
                        it.copy(
                            achievements = allAchievements.map { (achievement, category) -> achievement.toUiModel(category) },
                            achievedBadges = allAchievements
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
)

private fun AchievementClaimResult.toMessage(): String =
    if (rewardApplied) "보상을 받았어요!" else "이미 받은 보상이에요."

private fun Achievement.toUiModel(category: AchievementCategory): AchievementUiModel = AchievementUiModel(
    title = name,
    rarity = category.toAchievementRarity(),
    description = conditionDesc,
    // progressTarget이 0인 업적은 진행률 추적이 없는(달성 여부만 있는) 업적이다 — 달성했다면
    // "0/0"이 아니라 꽉 찬 상태로 보여야 한다. 진행 중인데 target이 0인 경우는 없다고 보고, 그
    // 외(진행 중이며 target>0)에는 기존처럼 실제 진행률을 쓴다.
    progressLabel = if (isAchieved && progressTarget <= 0) "1/1" else "$progressCurrent/$progressTarget",
    progress = when {
        isAchieved -> 1f
        progressTarget > 0 -> progressCurrent.toFloat() / progressTarget
        else -> 0f
    },
    rewardText = if (topazReward > 0) "+${topazReward}토파즈" else null,
    isInProgress = isInProgress,
    isAchieved = isAchieved,
    iconRes = AchievementIcons.resolve(code),
    imageUrl = badgeImageUrl,
    achievementId = achievementId,
    isClaimable = isClaimable,
)

private fun AchievementCategory.toAchievementRarity(): AchievementRarity = when (this) {
    AchievementCategory.RARE -> AchievementRarity.Rare
    AchievementCategory.EPIC -> AchievementRarity.Epic
    AchievementCategory.UNIQUE -> AchievementRarity.Unique
    AchievementCategory.EGG -> AchievementRarity.Character
    // 화면에 없는 5번째 등급이 새로 추가된 경우의 폴백 — 유니크로 묶어 최소한 어딘가엔 보이게 한다.
    AchievementCategory.UNKNOWN -> AchievementRarity.Unique
}
