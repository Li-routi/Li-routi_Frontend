package com.li_routi.core.domain.achievement

/**
 * "파도타기"(연속 기록) 업적이 추적할 개인 루틴 지정 상태 (`GET/PUT /api/achievements/wave-routine`).
 *
 * 아직 하나도 지정한 적이 없으면 [memberRoutineId]가 0(미지정)으로 온다 — 스웨거 문서에 그
 * 경우의 응답 모양이 따로 없어, 다른 목록형 API와 같은 관례(값이 없으면 기본값)를 따른다.
 */
data class WaveRoutineStatus(
    val memberRoutineId: Long,
    val routineName: String,
    val currentStreak: Int,
    val targetStreak: Int,
) {
    val isSelected: Boolean get() = memberRoutineId > 0
}
