package com.li_routi.core.domain.chat

data class ChatMessagePage(
    val messages: List<ChatMessage>,
    val nextCursor: Long?,
    val hasNext: Boolean,
    /** date로 조회했을 때 그 조회 기준 날짜("yyyy-MM-dd"). date 없이 조회했으면 null. */
    val date: String? = null,
    /** date로 조회했을 때 그 날짜에 채팅이 존재하는지. */
    val hasChat: Boolean = false,
)
