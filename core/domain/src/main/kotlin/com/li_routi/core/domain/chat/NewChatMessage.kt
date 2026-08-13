package com.li_routi.core.domain.chat

/** 소켓으로 보낼 채팅 메시지. [type]에 따라 [content] 또는 [emoticonCode] 중 하나만 채운다. */
data class NewChatMessage(
    val clientMessageId: String,
    val type: ChatMessageType,
    val content: String?,
    val emoticonCode: String?,
    /** 답장으로 보낼 때만 채운다 — 답장 대상 원본 메시지의 id. */
    val replyToMessageId: Long? = null,
)
