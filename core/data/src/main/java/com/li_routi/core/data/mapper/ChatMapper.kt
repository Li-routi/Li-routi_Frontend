package com.li_routi.core.data.mapper

import com.li_routi.core.common.kotlin.util.ApiException
import com.li_routi.core.data.network.dto.request.SendChatMessageRequest
import com.li_routi.core.data.network.dto.response.ChatDatesResponse
import com.li_routi.core.data.network.dto.response.ChatMessageItemResponse
import com.li_routi.core.data.network.dto.response.ChatMessageListResponse
import com.li_routi.core.data.network.dto.response.ChatReplyResponse
import com.li_routi.core.data.network.dto.response.EmoticonListResponse
import com.li_routi.core.data.network.dto.response.EmoticonResponse
import com.li_routi.core.domain.chat.ChatMessage
import com.li_routi.core.domain.chat.ChatMessagePage
import com.li_routi.core.domain.chat.ChatMessageType
import com.li_routi.core.domain.chat.ChatReply
import com.li_routi.core.domain.chat.Emoticon
import com.li_routi.core.domain.chat.NewChatMessage

fun EmoticonResponse.toDomain(): Emoticon = Emoticon(
    id = id,
    code = code,
    assetUrl = assetUrl,
    contentType = contentType,
    animated = animated,
)

fun EmoticonListResponse.toDomain(): List<Emoticon> = emoticons.map { it.toDomain() }

fun ChatMessageItemResponse.toDomain(): ChatMessage = ChatMessage(
    id = id,
    clientMessageId = clientMessageId,
    groupId = groupId,
    senderId = sender.memberId,
    senderNickname = sender.nickname,
    type = runCatching { ChatMessageType.valueOf(type) }
        .getOrElse { throw ApiException("지원하지 않는 채팅 메시지 타입: $type") },
    content = content.orEmpty(),
    emoticon = emoticon?.toDomain(),
    reply = reply?.toDomain(),
    createdAt = createdAt,
)

// 답장 미리보기는 본문 메시지 하나를 통째로 못 그리게 막을 정도로 중요하지 않은 부가 정보라,
// 알 수 없는 타입이 와도 예외를 던지는 대신 TEXT로 대체해 메시지 자체는 정상 렌더링되게 한다.
fun ChatReplyResponse.toDomain(): ChatReply = ChatReply(
    id = id,
    senderId = sender.memberId,
    senderNickname = sender.nickname,
    type = runCatching { ChatMessageType.valueOf(type) }.getOrDefault(ChatMessageType.TEXT),
    content = content.orEmpty(),
    emoticon = emoticon?.toDomain(),
    createdAt = createdAt,
)

fun ChatMessageListResponse.toDomain(): ChatMessagePage = ChatMessagePage(
    messages = messages.map { it.toDomain() },
    nextCursor = nextCursor,
    hasNext = hasNext,
    date = date,
    hasChat = hasChat,
)

fun ChatDatesResponse.toDomain(): List<String> = chatDates

fun NewChatMessage.toRequest(): SendChatMessageRequest = SendChatMessageRequest(
    clientMessageId = clientMessageId,
    type = type.name,
    content = content,
    emoticonCode = emoticonCode,
    replyToMessageId = replyToMessageId,
)
