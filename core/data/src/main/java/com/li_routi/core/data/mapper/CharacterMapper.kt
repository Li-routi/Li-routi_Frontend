package com.li_routi.core.data.mapper

import com.li_routi.core.data.network.dto.response.CharacterItemResponse
import com.li_routi.core.data.network.dto.response.CharactersResponse
import com.li_routi.core.domain.character.Character

fun CharactersResponse.toDomain(): List<Character> = characters.orEmpty().map { it.toDomain() }

fun CharacterItemResponse.toDomain(): Character = Character(
    id = id,
    code = code.orEmpty(),
    name = name.orEmpty(),
    unlocked = unlocked,
    selected = selected,
    imageUrl = imageUrl,
    unlockedDate = unlockedDate,
)
