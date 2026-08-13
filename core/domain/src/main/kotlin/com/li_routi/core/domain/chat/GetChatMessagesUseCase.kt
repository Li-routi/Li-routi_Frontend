package com.li_routi.core.domain.chat

import com.li_routi.core.common.kotlin.util.ResultState

class GetChatMessagesUseCase(
    private val repository: ChatRepository,
) {
    suspend operator fun invoke(
        groupId: Long,
        cursor: Long? = null,
        size: Int? = null,
        date: String? = null,
    ): ResultState<ChatMessagePage> = repository.getChatMessages(groupId, cursor, size, date)
}
