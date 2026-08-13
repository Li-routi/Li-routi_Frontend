package com.li_routi.feature.mypage.component

import androidx.annotation.DrawableRes
import com.li_routi.core.designsystem.R

/**
 * 업적 코드 → 고유 아이콘 매핑. Figma node `6008:31220`("전체") 기준 — 18개 업적 각각 다른 아이콘을 쓴다.
 *
 * 서버가 아직 이 18개 외의 업적을 내려줄 수 있어서(또는 코드가 바뀔 수 있어서), 매핑에 없는 코드는
 * `null`을 돌려주고 호출부에서 기본 아이콘으로 대체한다.
 */
object AchievementIcons {
    private val byCode: Map<String, Int> = mapOf(
        "FIRST_LIKE" to R.drawable.achv_first_like,
        "LIKE_10" to R.drawable.achv_like_10,
        "LIKE_30" to R.drawable.achv_like_30,
        "FIRST_POKE" to R.drawable.achv_first_poke,
        "POKE_5" to R.drawable.achv_poke_5,
        "POKE_10" to R.drawable.achv_poke_10,
        "FIRST_ROOM" to R.drawable.achv_first_room,
        "ROOM_JOIN_2" to R.drawable.achv_room_join_2,
        "FIRST_INVITE_JOIN" to R.drawable.achv_first_invite_join,
        "HEALTH_START" to R.drawable.achv_health_start,
        "FIRST_GEM_EXCHANGE" to R.drawable.achv_first_gem_exchange,
        "VERIFY_5" to R.drawable.achv_verify_5,
        "VERIFY_10" to R.drawable.achv_verify_10,
        "VERIFY_30" to R.drawable.achv_verify_30,
        "SELF_DEV_START" to R.drawable.achv_self_dev_start,
        "ORGANIZE_START" to R.drawable.achv_organize_start,
        "FIRST_REPORT" to R.drawable.achv_first_report,
        "EXERCISE_START" to R.drawable.achv_exercise_start,
    )

    @DrawableRes
    fun resolve(code: String): Int? = byCode[code]
}
