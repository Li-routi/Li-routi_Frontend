package com.li_routi.core.data.network.dto.response

/** GET /api/characters 응답 result. */
data class CharactersResponse(
    val characters: List<CharacterItemResponse>?,
)

data class CharacterItemResponse(
    val id: Long,
    val code: String?,
    val name: String?,
    val unlocked: Boolean,
    val selected: Boolean,
    val imageUrl: String?,
    val unlockedDate: String?,
)
