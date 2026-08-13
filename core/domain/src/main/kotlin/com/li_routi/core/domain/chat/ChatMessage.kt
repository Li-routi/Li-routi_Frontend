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
    /** 답장으로 보낸 메시지면 원본 메시지 미리보기가 채워짐. 일반 메시지는 null. */
    val reply: ChatReply? = null,
    val createdAt: String,
)

enum class ChatMessageType { TEXT, EMOTICON }
