package com.li_routi.feature.challenge.vm

import com.li_routi.core.domain.challenge.ChallengeCategory
import com.li_routi.feature.challenge.component.ChallengeCardUiModel

/** "챌린지 찾아보기" 화면 UI 상태. */
data class FindChallengeUiState(
    val isLoading: Boolean = true,
    val challenges: List<ChallengeCardUiModel> = emptyList(),
    val selectedCategory: ChallengeCategory? = null,
    val errorMessage: String? = null,
)
