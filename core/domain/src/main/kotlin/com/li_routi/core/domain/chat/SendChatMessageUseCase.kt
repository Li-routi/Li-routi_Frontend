package com.li_routi.core.domain.chat

import com.li_routi.core.common.kotlin.util.ResultState

class SendChatMessageUseCase(
    private val repository: ChatRepository,
) {
    suspend operator fun invoke(groupId: Long, message: NewChatMessage): ResultState<Unit> =
        repository.sendChatMessage(groupId, message)
}
