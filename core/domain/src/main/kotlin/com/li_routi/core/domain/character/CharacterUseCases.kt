package com.li_routi.core.domain.character

import com.li_routi.core.common.kotlin.util.ResultState

class GetCharactersUseCase(
    private val repository: CharacterRepository,
) {
    suspend operator fun invoke(): ResultState<List<Character>> = repository.getCharacters()
}

class SelectCharacterUseCase(
    private val repository: CharacterRepository,
) {
    suspend operator fun invoke(characterId: Long): ResultState<Unit> = repository.selectCharacter(characterId)
}
