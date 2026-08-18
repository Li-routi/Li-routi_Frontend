package com.li_routi.core.data.network.service

import com.li_routi.core.data.network.dto.request.SelectCharacterRequest
import com.li_routi.core.data.network.dto.response.ApiResponse
import com.li_routi.core.data.network.dto.response.CharactersResponse
import retrofit2.http.Body
import retrofit2.http.GET
import retrofit2.http.PUT

interface CharacterApiService {

    /** 캐릭터 도감 목록. 노출 순서 그대로 옴 — 안 연 캐릭터는 알, 연 캐릭터는 성체 이미지. */
    @GET("api/characters")
    suspend fun getCharacters(): ApiResponse<CharactersResponse>

    // 응답 result가 항상 비어 있어(성공해도 페이로드 없음) Unit?으로 받는다.
    @PUT("api/characters/selection")
    suspend fun selectCharacter(
        @Body request: SelectCharacterRequest,
    ): ApiResponse<Unit?>
}
