package com.li_routi.core.domain.chat

import com.li_routi.core.common.kotlin.util.ResultState

class UpdateChatReadPositionUseCase(
    private val repository: ChatRepository,
) {
    suspend operator fun invoke(groupId: Long, lastReadMessageId: Long): ResultState<Unit> =
        repository.updateReadPosition(groupId, lastReadMessageId)
}
