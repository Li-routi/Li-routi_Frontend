package com.li_routi.core.data.network.dto.response

/** 리포트 "활동 통계" — 주간 조회여도 그 주가 표시되는 달 기준으로 내려온다(API 문서 참고). */
data class ReportStatsResponse(
    val completedRoutineCount: Int,
    val averageCompletionRate: Int,
    val completedChallengeCount: Int,
    val earnedCoinCount: Int,
)
