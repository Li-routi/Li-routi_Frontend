package com.li_routi.core.domain.chat

import com.li_routi.core.common.kotlin.util.ResultState

class GetEmoticonsUseCase(
    private val repository: ChatRepository,
) {
    suspend operator fun invoke(): ResultState<List<Emoticon>> = repository.getEmoticons()
}
