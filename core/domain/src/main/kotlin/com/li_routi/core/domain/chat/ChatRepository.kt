package com.li_routi.core.domain.chat

import com.li_routi.core.common.kotlin.util.ResultState
import kotlinx.coroutines.flow.Flow

interface ChatRepository {

    /**
     * 커서 기반으로 그룹 채팅 메시지를 조회함. [cursor]가 null이면 최신 메시지부터 조회함.
     * [date]("yyyy-MM-dd")를 주면 그 날짜부터 과거로 조회하며, 이후 [cursor] 페이지네이션도
     * 같은 [date]와 함께 호출해야 함.
     */
    suspend fun getChatMessages(
        groupId: Long,
        cursor: Long?,
        size: Int?,
        date: String? = null,
    ): ResultState<ChatMessagePage>

    /** [from](포함)부터 [to](미포함, "yyyy-MM-dd")까지 채팅이 존재하는 날짜 목록을 조회함(KST 기준). */
    suspend fun getChatDates(groupId: Long, from: String, to: String): ResultState<List<String>>

    /** 마지막으로 읽은 메시지 위치를 서버에 반영함. */
    suspend fun updateReadPosition(groupId: Long, lastReadMessageId: Long): ResultState<Unit>

    /** 채팅에서 쓸 수 있는 이모티콘 전체 목록을 조회함. */
    suspend fun getEmoticons(): ResultState<List<Emoticon>>

    /** 채팅 STOMP 소켓에 연결하고 [groupId] 채팅방 토픽을 구독함. */
    suspend fun connectChatSocket(groupId: Long): ResultState<Unit>

    /** 구독 중인 채팅방으로 브로드캐스트되는 메시지 스트림. 지속적인 이벤트라 ResultState로 감싸지 않음. */
    fun observeChatMessages(): Flow<ChatMessage>

    /** 소켓으로 메시지를 전송함. 실제 화면 반영은 [observeChatMessages]로 돌아오는 브로드캐스트를 통해 이뤄짐. */
    suspend fun sendChatMessage(groupId: Long, message: NewChatMessage): ResultState<Unit>

    /** 채팅 소켓 연결을 해제함. */
    suspend fun disconnectChatSocket()
}
