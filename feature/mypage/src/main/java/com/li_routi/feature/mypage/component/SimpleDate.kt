package com.li_routi.feature.mypage.component

import java.time.LocalDate
import java.util.Calendar
import java.util.Locale
import java.util.TimeZone

/** 연-월-일. [month]는 1..12. */
data class SimpleDate(val year: Int, val month: Int, val day: Int) {
    fun toDisplayLabel(): String = "${year}년 ${month}월 ${day}일"
}

/**
 * 기기 시간대가 아니라 KST 기준 오늘 날짜를 반환한다. `GET /api/members/me/verifications`가 date
 * 생략 시 오늘(KST) 기준으로 조회하는 것과 기준을 맞춰야, 기기 시간대가 KST와 다를 때(예: 해외 로밍)
 * 서버와 다른 날짜를 요청하는 걸 막을 수 있다.
 */
fun todaySimpleDate(): SimpleDate {
    val calendar = Calendar.getInstance(TimeZone.getTimeZone("Asia/Seoul"))
    return SimpleDate(calendar.get(Calendar.YEAR), calendar.get(Calendar.MONTH) + 1, calendar.get(Calendar.DAY_OF_MONTH))
}

/** `GET /api/members/me/verifications`의 `date` 쿼리 파라미터용 "yyyy-MM-dd" 포맷. */
fun SimpleDate.toApiDateString(): String = String.format(Locale.US, "%04d-%02d-%02d", year, month, day)

fun SimpleDate.plusDays(delta: Int): SimpleDate {
    val calendar = Calendar.getInstance().apply {
        set(year, month - 1, day)
        add(Calendar.DAY_OF_MONTH, delta)
    }
    return SimpleDate(calendar.get(Calendar.YEAR), calendar.get(Calendar.MONTH) + 1, calendar.get(Calendar.DAY_OF_MONTH))
}

/** [com.li_routi.core.common.ui.calendar.LiroutiCalendarBottomSheet]는 LocalDate를 쓰므로 호출부 경계에서 변환한다. */
fun SimpleDate.toLocalDate(): LocalDate = LocalDate.of(year, month, day)

fun LocalDate.toSimpleDate(): SimpleDate = SimpleDate(year, monthValue, dayOfMonth)
