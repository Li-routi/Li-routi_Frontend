package com.li_routi.core.domain.chat

import com.li_routi.core.common.kotlin.util.ResultState

class GetChatDatesUseCase(
    private val repository: ChatRepository,
) {
    /** [from](포함)부터 [to](미포함, "yyyy-MM-dd")까지 채팅이 존재하는 날짜 목록을 조회한다. */
    suspend operator fun invoke(groupId: Long, from: String, to: String): ResultState<List<String>> =
        repository.getChatDates(groupId, from, to)
}
