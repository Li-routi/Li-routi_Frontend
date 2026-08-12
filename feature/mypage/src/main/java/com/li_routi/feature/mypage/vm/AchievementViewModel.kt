package com.li_routi.feature.mypage.vm

import androidx.lifecycle.viewModelScope
import com.li_routi.core.common.android.architecture.BaseViewModel
import com.li_routi.core.common.kotlin.util.ResultState
import com.li_routi.core.data.di.AchievementContainer
import com.li_routi.core.domain.achievement.Achievement
import com.li_routi.core.domain.achievement.AchievementCategory
import com.li_routi.core.domain.achievement.GetAchievementsUseCase
import com.li_routi.feature.mypage.component.AchievementBadgeUiModel
import com.li_routi.feature.mypage.component.AchievementRarity
import com.li_routi.feature.mypage.component.AchievementUiModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch

/**
 * 업적 화면 ViewModel. `GET /api/achievements`로 카테고리(rare/epic/unique)별 업적 목록을 조회한다.
 *
 * 문서의 `summary`(획득/진행 중 개수 상단 요약)는 예시 스키마가 챌린지 응답과 뒤섞여 있어 정확한 모양을
 * 알 수 없고, 화면도 총 개수를 목록에서 직접 세고 있어 지금은 매핑하지 않는다
 * ([com.li_routi.core.data.network.dto.response.AchievementsResponse] 참고).
 *
 * "달성" 탭 배지 그리드는 실제로 달성 시점이 찍힌(`isAchieved`, [Achievement] 문서 참고) 업적 중
 * `badgeYn`이 true인 것만 모은다 — `!isInProgress`로 판단하면 문서에 없는 새 상태가 추가됐을 때
 * 그 항목까지 전부 "달성"으로 잘못 집계된다.
 */
class AchievementViewModel(
    private val getAchievementsUseCase: GetAchievementsUseCase = AchievementContainer.getAchievementsUseCase,
) : BaseViewModel() {

    private val _uiState = MutableStateFlow(AchievementUiState(isLoading = true))
    val uiState: StateFlow<AchievementUiState> = _uiState.asStateFlow()

    init {
        load()
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
                                .filter { (achievement, _) -> achievement.isAchieved && achievement.badgeYn }
                                .map { (achievement, category) -> AchievementBadgeUiModel(achievement.name, category.toAchievementRarity()) },
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
)

private fun Achievement.toUiModel(category: AchievementCategory): AchievementUiModel = AchievementUiModel(
    title = name,
    rarity = category.toAchievementRarity(),
    description = conditionDesc,
    progressLabel = "$progressCurrent/$progressTarget",
    progress = if (progressTarget > 0) progressCurrent.toFloat() / progressTarget else 0f,
    rewardText = if (topazReward > 0) "+${topazReward}토파즈" else null,
    isInProgress = isInProgress,
)

// UNIQUE로 취급할 값이 하나뿐이라 UNKNOWN도 같이 묶는다 — 화면에 존재하지 않는 4번째 등급을 만들 수 없다.
private fun AchievementCategory.toAchievementRarity(): AchievementRarity = when (this) {
    AchievementCategory.RARE -> AchievementRarity.Rare
    AchievementCategory.EPIC -> AchievementRarity.Epic
    AchievementCategory.UNIQUE, AchievementCategory.UNKNOWN -> AchievementRarity.Unique
}
