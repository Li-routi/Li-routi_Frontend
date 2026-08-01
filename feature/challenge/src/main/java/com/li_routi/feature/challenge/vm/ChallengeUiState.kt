package com.li_routi.feature.challenge.vm

// 챌린지에 딸린 루틴 하나. categories가 여러 개일 수 있음 (하나의 챌린지에 여러 카테고리 매핑 가능).
data class RoutineUiModel(
    val id: Long,
    val title: String,
    val categories: List<String>,
    val description: String,
    val badge: String?,
)

/** "챌린지" 메인 화면 UI 상태. */
data class ChallengeUiState(
    val isLoading: Boolean = true,
    val routines: List<RoutineUiModel> = emptyList(),
)
