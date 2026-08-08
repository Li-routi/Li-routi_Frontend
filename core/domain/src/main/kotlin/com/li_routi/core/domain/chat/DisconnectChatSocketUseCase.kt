package com.li_routi.core.domain.chat

class DisconnectChatSocketUseCase(
    private val repository: ChatRepository,
) {
    suspend operator fun invoke() = repository.disconnectChatSocket()
}
