package com.li_routi.core.data.network.dto.response

/** 그룹 채팅이 존재하는 날짜 목록(KST 기준, "yyyy-MM-dd" 문자열). */
data class ChatDatesResponse(
    val chatDates: List<String> = emptyList(),
)
