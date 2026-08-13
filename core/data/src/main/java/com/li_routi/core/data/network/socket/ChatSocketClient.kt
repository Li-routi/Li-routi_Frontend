package com.li_routi.core.data.network.socket

import com.google.gson.Gson
import com.li_routi.core.common.kotlin.util.ApiException
import com.li_routi.core.data.network.NetworkModule
import com.li_routi.core.data.network.dto.request.SendChatMessageRequest
import com.li_routi.core.data.network.dto.response.ChatMessageItemResponse
import kotlinx.coroutines.CancellationException
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.CoroutineStart
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.SupervisorJob
import kotlinx.coroutines.cancel
import kotlinx.coroutines.delay
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

/** 하트비트 타임아웃 등으로 구독이 끊겼을 때 재연결을 시도하는 최대 횟수(초과하면 조용히 포기). */
private const val MaxResubscribeAttempts = 3

/** 재연결 시도 사이 대기 시간 — 시도 횟수(1부터)를 곱해 선형 백오프를 준다(2초, 4초, 6초...). */
private const val ResubscribeBackoffBaseMillis = 2_000L

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
        // 최초 연결은 그대로 await해서, 실패하면 connect()가 던지는 예외로 호출부(safeApiCall)에
        // ResultState.Error로 전달되게 한다 — 재연결 시도는 이 최초 연결이 성공한 뒤에만 의미가 있다.
        val stompSession = establishSession()
        session = stompSession

        val scope = CoroutineScope(SupervisorJob() + Dispatchers.IO)
        socketScope = scope
        // UNDISPATCHED로 SUBSCRIBE 프레임 전송을 이 함수가 반환하기 전에 최대한 앞당겨,
        // connect() 직후 곧바로 이력 조회를 시작해도 그 사이 온 메시지를 놓치지 않게 한다.
        scope.launch(start = CoroutineStart.UNDISPATCHED) {
            subscribeWithRetry(groupId, stompSession)
        }
    }

    private suspend fun establishSession(): StompSession {
        val accessToken = NetworkModule.authTokenPreference.getAccessTokenBlocking()
        val wsClient = OkHttpWebSocketClient(NetworkModule.socketOkHttpClient)
        val wsConnection = wsClient.connect(
            url = NetworkModule.chatSocketUrl,
            headers = accessToken?.let { mapOf("Authorization" to "Bearer $it") }.orEmpty(),
        )
        return wsConnection.stomp(config = StompConfig())
    }

    // 이 스코프는 CoroutineExceptionHandler가 없는 SupervisorJob이라, 하트비트 타임아웃
    // (MissingHeartBeatException) 같은 STOMP 세션 레벨 예외가 그대로 새면 스레드의 미처리
    // 예외로 앱 전체가 죽는다(채팅방에 "일정 시간" 머물다 튕기는 원인). 소켓 하나가 끊긴 것으로
    // 앱이 죽으면 안 되므로 여기서 흡수하되, 화면을 나갔다 재진입해야만 복구되던 예전과 달리
    // 세션을 다시 세우고 재구독까지 몇 차례 자체적으로 시도한 뒤에야 포기한다.
    private suspend fun subscribeWithRetry(groupId: Long, initialSession: StompSession) {
        var currentSession = initialSession
        var attempt = 0
        while (attempt <= MaxResubscribeAttempts) {
            try {
                currentSession.subscribeText(ChatTopicFormat.format(groupId))
                    .mapNotNull { raw -> runCatching { gson.fromJson(raw, ChatMessageItemResponse::class.java) }.getOrNull() }
                    .collect { incoming.emit(it) }
                return
            } catch (e: CancellationException) {
                throw e
            } catch (e: Exception) {
                attempt++
                if (attempt > MaxResubscribeAttempts) return
                delay(ResubscribeBackoffBaseMillis * attempt)
                currentSession = runCatching { establishSession() }.getOrNull()?.also { session = it }
                    ?: currentSession // 재연결 자체가 실패하면 죽은 세션으로라도 다시 시도 — 다음 루프에서 attempt가 계속 늘어나 결국 위 상한에서 멈춘다.
            }
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
