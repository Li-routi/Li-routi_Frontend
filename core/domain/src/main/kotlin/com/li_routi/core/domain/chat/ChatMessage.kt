package com.li_routi.core.domain.chat

data class ChatMessage(
    val id: Long,
    val clientMessageId: String?,
    val groupId: Long,
    val senderId: Long,
    val senderNickname: String,
    val type: ChatMessageType,
    val content: String,
    val emoticon: Emoticon?,
    val createdAt: String,
)

enum class ChatMessageType { TEXT, EMOTICON }
