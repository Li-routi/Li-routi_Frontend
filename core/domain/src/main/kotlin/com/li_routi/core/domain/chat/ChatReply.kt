package com.li_routi.core.domain.chat

/** 답장이 인용하는 원본 메시지 미리보기. 메시지 단건 조회 API가 없어 서버가 메시지 응답에 함께 실어준다. */
data class ChatReply(
    val id: Long,
    val senderId: Long,
    val senderNickname: String,
    val type: ChatMessageType,
    val content: String,
    val emoticon: Emoticon?,
    val createdAt: String,
)
