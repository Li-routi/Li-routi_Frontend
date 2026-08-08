package com.li_routi.core.data.network.socket

import com.google.gson.Gson
import com.li_routi.core.common.kotlin.util.ApiException
import com.li_routi.core.data.network.NetworkModule
import com.li_routi.core.data.network.dto.request.SendChatMessageRequest
import com.li_routi.core.data.network.dto.response.ChatMessageItemResponse
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.CoroutineStart
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.SupervisorJob
import kotlinx.coroutines.cancel
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.MutableSharedFlow
import kotlinx.coroutines.flow.mapNotNull
import kotlinx.coroutines.launch
import org.hildan.krossbow.stomp.StompSession
import org.hildan.krossbow.stomp.config.StompConfig
import org.hildan.krossbow.stomp.sendText
import org.hildan.krossbow.stomp.stomp
import org.hildan.krossbow.stomp.subscribeText
import org.hildan.krossbow.websocket.okhttp.OkHttpWebSocketClient

private const val ChatTopicFormat = "/topic/groups/%d/chat"
private const val SendMessageDestinationFormat = "/app/groups/%d/chat/messages"

/**
 * 채팅 STOMP 소켓의 얇은 래퍼. 연결/구독/전송/해제만 담당하고, 파싱 실패한 프레임은 무시하고
 * 건너뛴다 — 프레임 하나가 깨졌다고 전체 구독 스트림을 죽이지 않기 위함.
 */
class ChatSocketClient(
    private val gson: Gson = Gson(),
) {
    private var session: StompSession? = null
    private var socketScope: CoroutineScope? = null
    private val incoming = MutableSharedFlow<ChatMessageItemResponse>(extraBufferCapacity = 64)

    suspend fun connect(groupId: Long) {
        val accessToken = NetworkModule.authTokenPreference.getAccessTokenBlocking()
        val wsClient = OkHttpWebSocketClient(NetworkModule.socketOkHttpClient)
        val wsConnection = wsClient.connect(
            url = NetworkModule.chatSocketUrl,
            headers = accessToken?.let { mapOf("Authorization" to "Bearer $it") }.orEmpty(),
        )
        val stompSession = wsConnection.stomp(config = StompConfig())
        session = stompSession

        val scope = CoroutineScope(SupervisorJob() + Dispatchers.IO)
        socketScope = scope
        // UNDISPATCHED로 SUBSCRIBE 프레임 전송을 이 함수가 반환하기 전에 최대한 앞당겨,
        // connect() 직후 곧바로 이력 조회를 시작해도 그 사이 온 메시지를 놓치지 않게 한다.
        scope.launch(start = CoroutineStart.UNDISPATCHED) {
            stompSession.subscribeText(ChatTopicFormat.format(groupId))
                .mapNotNull { raw -> runCatching { gson.fromJson(raw, ChatMessageItemResponse::class.java) }.getOrNull() }
                .collect { incoming.emit(it) }
        }
    }

    fun incomingMessages(): Flow<ChatMessageItemResponse> = incoming

    suspend fun send(groupId: Long, request: SendChatMessageRequest) {
        val current = session ?: throw ApiException("채팅 연결이 끊겼습니다. 다시 시도해 주세요.")
        current.sendText(SendMessageDestinationFormat.format(groupId), gson.toJson(request))
    }

    suspend fun disconnect() {
        socketScope?.cancel()
        socketScope = null
        session?.disconnect()
        session = null
    }
}
