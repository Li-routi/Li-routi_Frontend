package com.li_routi.core.data.network.dto.request

data class SendChatMessageRequest(
    val clientMessageId: String,
    val type: String,
    val content: String?,
    val emoticonCode: String?,
    val replyToMessageId: Long?,
)
