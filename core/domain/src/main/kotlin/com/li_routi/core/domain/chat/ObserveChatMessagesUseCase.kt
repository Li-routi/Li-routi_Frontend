package com.li_routi.core.domain.chat

import kotlinx.coroutines.flow.Flow

class ObserveChatMessagesUseCase(
    private val repository: ChatRepository,
) {
    operator fun invoke(): Flow<ChatMessage> = repository.observeChatMessages()
}
