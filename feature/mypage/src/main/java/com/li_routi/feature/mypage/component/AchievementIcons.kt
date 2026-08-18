package com.li_routi.feature.mypage.component

import androidx.annotation.DrawableRes
import com.li_routi.core.designsystem.R

/**
 * 업적 코드 → 고유 아이콘 매핑. Figma node `6008:31220`("전체") 목업에 있던 18개 항목 이름을 보고 만든
 * 매핑이었으나, 그 이름들이 실제 서버 `code`(`GET /api/achievements`, `ACH-ST-###` 형식)와 전혀 달라
 * 항상 null만 반환하던 걸 실제 코드로 다시 매핑했다.
 *
 * `FIRST_GEM_EXCHANGE`(목업의 "첫 오렌지젬 교환")는 현재 서버 업적 목록 어디에도 없어 제외했다 —
 * 목업에만 있고 실제로 구현되지 않은 기획으로 보인다. `ACH-ST-019`("마음 시작")/`ACH-ST-020`("취미
 * 시작")은 서버엔 있지만 대응하는 아이콘 자산이 아직 없어 기본 아이콘으로 남는다.
 *
 * 서버가 이 17개 외의 업적을 내려줄 수 있어서(또는 코드가 또 바뀔 수 있어서), 매핑에 없는 코드는
 * `null`을 돌려주고 호출부에서 기본 아이콘으로 대체한다.
 */
object AchievementIcons {
    private val byCode: Map<String, Int> = mapOf(
        "ACH-ST-001" to R.drawable.achv_first_like, // 첫 좋아요
        "ACH-ST-010" to R.drawable.achv_like_10, // 좋아요 10회
        "ACH-ST-011" to R.drawable.achv_like_30, // 좋아요 30회
        "ACH-ST-002" to R.drawable.achv_first_poke, // 첫 쿡쿡
        "ACH-ST-012" to R.drawable.achv_poke_5, // 쿡쿡 5회
        "ACH-ST-013" to R.drawable.achv_poke_10, // 쿡쿡 10회
        "ACH-ST-003" to R.drawable.achv_first_room, // 첫 방 만들기
        "ACH-ST-014" to R.drawable.achv_room_join_2, // 방 참여 2개
        "ACH-ST-004" to R.drawable.achv_first_invite_join, // 첫 초대 참여
        "ACH-ST-016" to R.drawable.achv_health_start, // 건강 시작
        "ACH-ST-007" to R.drawable.achv_verify_5, // 인증 5회
        "ACH-ST-008" to R.drawable.achv_verify_10, // 인증 10회
        "ACH-ST-009" to R.drawable.achv_verify_30, // 인증 30회
        "ACH-ST-017" to R.drawable.achv_self_dev_start, // 자기계발 시작
        "ACH-ST-018" to R.drawable.achv_organize_start, // 정리 시작
        "ACH-ST-005" to R.drawable.achv_first_report, // 첫 리포트 확인
        "ACH-ST-015" to R.drawable.achv_exercise_start, // 운동 시작
    )

    @DrawableRes
    fun resolve(code: String): Int? = byCode[code]
}
