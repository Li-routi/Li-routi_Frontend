package com.li_routi.core.data.network.dto.response

data class ChatMessageListResponse(
    val messages: List<ChatMessageItemResponse> = emptyList(),
    val nextCursor: Long?,
    val hasNext: Boolean = false,
    /** 조회에 date 파라미터를 넘겼을 때 그 값을 그대로 echo함. */
    val date: String? = null,
    /** date로 조회했을 때 그 날짜에 채팅이 존재하는지. */
    val hasChat: Boolean = false,
)

data class ChatMessageItemResponse(
    val id: Long,
    val clientMessageId: String?,
    val groupId: Long,
    val sender: ChatSenderResponse,
    val type: String,
    val content: String?,
    val emoticon: EmoticonResponse?,
    /** 답장으로 보낸 메시지면 원본 메시지 미리보기가 채워짐. 일반 메시지는 null. */
    val reply: ChatReplyResponse? = null,
    val createdAt: String,
)

data class ChatSenderResponse(
    val memberId: Long,
    val nickname: String,
)

/** 답장이 인용하는 원본 메시지 미리보기. 단건 조회 API가 없어 서버가 메시지 응답에 함께 실어준다. */
data class ChatReplyResponse(
    val id: Long,
    val sender: ChatSenderResponse,
    val type: String,
    val content: String?,
    val emoticon: EmoticonResponse?,
    val createdAt: String,
)
