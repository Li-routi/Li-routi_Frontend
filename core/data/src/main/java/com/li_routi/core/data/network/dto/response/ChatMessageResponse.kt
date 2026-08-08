package com.li_routi.core.data.network.dto.response

data class ChatMessageListResponse(
    val messages: List<ChatMessageItemResponse> = emptyList(),
    val nextCursor: Long?,
    val hasNext: Boolean = false,
)

data class ChatMessageItemResponse(
    val id: Long,
    val clientMessageId: String?,
    val groupId: Long,
    val sender: ChatSenderResponse,
    val type: String,
    val content: String?,
    val emoticon: EmoticonResponse?,
    val createdAt: String,
)

data class ChatSenderResponse(
    val memberId: Long,
    val nickname: String,
)
