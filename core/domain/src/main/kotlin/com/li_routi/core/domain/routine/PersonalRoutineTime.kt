package com.li_routi.core.domain.routine

import java.time.ZoneId
import java.time.ZonedDateTime

private const val MINUTES_PER_HOUR = 60
private const val HOURS_PER_DAY = 24
private const val KST_ZONE_ID = "Asia/Seoul"

/** 개인 루틴 시간 범위가 서버 생성·수정 규칙을 만족하는지 확인한다. */
fun isValidPersonalRoutineTimeRange(startTime: String?, endTime: String): Boolean {
    val endMinute = endTime.toMinuteOfDay() ?: return false
    val startMinute = startTime?.toMinuteOfDay() ?: return startTime == null
    return startMinute < endMinute
}

/**
 * 개인 루틴을 현재 분에 인증할 수 있는지 확인한다.
 *
 * 서버와 동일하게 시작 시각과 종료 시각이 속한 분을 모두 포함한다. 파싱할 수 없는 시간은
 * 클라이언트에서 허용하지 않고 서버/목록 재조회로 복구하도록 false를 반환한다.
 */
fun isPersonalRoutineVerificationAllowed(
    completedToday: Boolean,
    startTime: String?,
    endTime: String,
    currentMinute: Int = currentKstMinuteOfDay(),
): Boolean {
    if (completedToday) return false
    val endMinute = endTime.toMinuteOfDay() ?: return false
    val startMinute = startTime?.toMinuteOfDay()
    if (startTime != null && startMinute == null) return false
    return (startMinute == null || currentMinute >= startMinute) && currentMinute <= endMinute
}

/** KST 현재 시각을 자정부터 흐른 분으로 반환한다. */
fun currentKstMinuteOfDay(): Int {
    val now = ZonedDateTime.now(ZoneId.of(KST_ZONE_ID))
    return now.hour * MINUTES_PER_HOUR + now.minute
}

/** `HH:mm` 문자열을 자정부터 흐른 분으로 변환한다. */
private fun String.toMinuteOfDay(): Int? {
    val parts = trim().split(':')
    if (parts.size != 2 || parts[0].length != 2 || parts[1].length != 2) return null
    val hour = parts[0].toIntOrNull()?.takeIf { it in 0 until HOURS_PER_DAY } ?: return null
    val minute = parts[1].toIntOrNull()?.takeIf { it in 0 until MINUTES_PER_HOUR } ?: return null
    return hour * MINUTES_PER_HOUR + minute
}
