package com.li_routi.core.data.di

import com.li_routi.core.data.network.NetworkModule
import com.li_routi.core.data.network.socket.ChatSocketClient
import com.li_routi.core.data.repository.ChatRepositoryImpl
import com.li_routi.core.domain.chat.ChatRepository
import com.li_routi.core.domain.chat.ConnectChatSocketUseCase
import com.li_routi.core.domain.chat.DisconnectChatSocketUseCase
import com.li_routi.core.domain.chat.GetChatDatesUseCase
import com.li_routi.core.domain.chat.GetChatMessagesUseCase
import com.li_routi.core.domain.chat.GetEmoticonsUseCase
import com.li_routi.core.domain.chat.ObserveChatMessagesUseCase
import com.li_routi.core.domain.chat.SendChatMessageUseCase
import com.li_routi.core.domain.chat.UpdateChatReadPositionUseCase

/**
 * Hilt 등 DI 프레임워크가 붙기 전까지 사용하는 수동 구성 root.
 * feature 모듈은 여기서 필요한 UseCase만 가져다 쓴다.
 */
object ChatContainer {

    private val repository: ChatRepository by lazy {
        ChatRepositoryImpl(NetworkModule.chatApiService, ChatSocketClient())
    }

    val getChatMessagesUseCase: GetChatMessagesUseCase by lazy {
        GetChatMessagesUseCase(repository)
    }

    val getChatDatesUseCase: GetChatDatesUseCase by lazy {
        GetChatDatesUseCase(repository)
    }

    val updateChatReadPositionUseCase: UpdateChatReadPositionUseCase by lazy {
        UpdateChatReadPositionUseCase(repository)
    }

    val getEmoticonsUseCase: GetEmoticonsUseCase by lazy {
        GetEmoticonsUseCase(repository)
    }

    val connectChatSocketUseCase: ConnectChatSocketUseCase by lazy {
        ConnectChatSocketUseCase(repository)
    }

    val observeChatMessagesUseCase: ObserveChatMessagesUseCase by lazy {
        ObserveChatMessagesUseCase(repository)
    }

    val sendChatMessageUseCase: SendChatMessageUseCase by lazy {
        SendChatMessageUseCase(repository)
    }

    val disconnectChatSocketUseCase: DisconnectChatSocketUseCase by lazy {
        DisconnectChatSocketUseCase(repository)
    }
}
