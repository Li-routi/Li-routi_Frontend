package com.li_routi.core.data.repository

import com.li_routi.core.common.kotlin.util.ApiException
import com.li_routi.core.common.kotlin.util.ResultState
import com.li_routi.core.data.mapper.toDomain
import com.li_routi.core.data.network.apiCall
import com.li_routi.core.data.network.dto.request.SelectCharacterRequest
import com.li_routi.core.data.network.safeDataApiCall
import com.li_routi.core.data.network.service.CharacterApiService
import com.li_routi.core.domain.character.Character
import com.li_routi.core.domain.character.CharacterRepository

class CharacterRepositoryImpl(
    private val api: CharacterApiService,
) : CharacterRepository {

    override suspend fun getCharacters(): ResultState<List<Character>> = safeDataApiCall {
        apiCall { api.getCharacters() }.toDomain()
    }

    override suspend fun selectCharacter(characterId: Long): ResultState<Unit> = safeDataApiCall {
        val response = api.selectCharacter(SelectCharacterRequest(characterId))
        if (!response.isSuccess) throw ApiException(response.message)
    }
}
