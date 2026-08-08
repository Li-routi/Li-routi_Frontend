package com.li_routi.core.data.repository

import com.li_routi.core.common.kotlin.util.ApiException
import com.li_routi.core.common.kotlin.util.ResultState
import com.li_routi.core.common.kotlin.util.safeApiCall
import com.li_routi.core.data.mapper.toDomain
import com.li_routi.core.data.mapper.toRequest
import com.li_routi.core.data.network.apiCall
import com.li_routi.core.data.network.dto.request.UpdateChatReadRequest
import com.li_routi.core.data.network.service.ChatApiService
import com.li_routi.core.data.network.socket.ChatSocketClient
import com.li_routi.core.domain.chat.ChatMessage
import com.li_routi.core.domain.chat.ChatMessagePage
import com.li_routi.core.domain.chat.ChatRepository
import com.li_routi.core.domain.chat.Emoticon
import com.li_routi.core.domain.chat.NewChatMessage
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map

class ChatRepositoryImpl(
    private val api: ChatApiService,
    private val socket: ChatSocketClient,
) : ChatRepository {

    override suspend fun getChatMessages(
        groupId: Long,
        cursor: Long?,
        size: Int?,
    ): ResultState<ChatMessagePage> = safeApiCall {
        apiCall { api.getChatMessages(groupId, cursor, size) }.toDomain()
    }

    // 읽음 처리 응답은 result가 항상 비어 있어(성공해도 페이로드 없음) apiCall() 대신 isSuccess만 직접 확인한다.
    override suspend fun updateReadPosition(
        groupId: Long,
        lastReadMessageId: Long,
    ): ResultState<Unit> = safeApiCall {
        val response = api.updateReadPosition(groupId, UpdateChatReadRequest(lastReadMessageId))
        if (!response.isSuccess) throw ApiException(response.message)
    }

    override suspend fun getEmoticons(): ResultState<List<Emoticon>> = safeApiCall {
        apiCall { api.getEmoticons() }.toDomain()
    }

    override suspend fun connectChatSocket(groupId: Long): ResultState<Unit> = safeApiCall {
        socket.connect(groupId)
    }

    override fun observeChatMessages(): Flow<ChatMessage> = socket.incomingMessages().map { it.toDomain() }

    override suspend fun sendChatMessage(groupId: Long, message: NewChatMessage): ResultState<Unit> = safeApiCall {
        socket.send(groupId, message.toRequest())
    }

    override suspend fun disconnectChatSocket() {
        socket.disconnect()
    }
}
