package com.li_routi.core.data.mapper

import com.li_routi.core.common.kotlin.util.ApiException
import com.li_routi.core.data.network.dto.request.SendChatMessageRequest
import com.li_routi.core.data.network.dto.response.ChatMessageItemResponse
import com.li_routi.core.data.network.dto.response.ChatMessageListResponse
import com.li_routi.core.data.network.dto.response.EmoticonListResponse
import com.li_routi.core.data.network.dto.response.EmoticonResponse
import com.li_routi.core.domain.chat.ChatMessage
import com.li_routi.core.domain.chat.ChatMessagePage
import com.li_routi.core.domain.chat.ChatMessageType
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
    createdAt = createdAt,
)

fun ChatMessageListResponse.toDomain(): ChatMessagePage = ChatMessagePage(
    messages = messages.map { it.toDomain() },
    nextCursor = nextCursor,
    hasNext = hasNext,
)

fun NewChatMessage.toRequest(): SendChatMessageRequest = SendChatMessageRequest(
    clientMessageId = clientMessageId,
    type = type.name,
    content = content,
    emoticonCode = emoticonCode,
)
