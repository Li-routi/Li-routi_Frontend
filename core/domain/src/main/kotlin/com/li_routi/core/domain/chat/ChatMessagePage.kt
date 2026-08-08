package com.li_routi.core.domain.chat

data class ChatMessagePage(
    val messages: List<ChatMessage>,
    val nextCursor: Long?,
    val hasNext: Boolean,
)
