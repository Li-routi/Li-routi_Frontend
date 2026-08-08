package com.li_routi.core.domain.chat

import com.li_routi.core.common.kotlin.util.ResultState

class ConnectChatSocketUseCase(
    private val repository: ChatRepository,
) {
    suspend operator fun invoke(groupId: Long): ResultState<Unit> = repository.connectChatSocket(groupId)
}
