package com.li_routi.core.domain.character

import com.li_routi.core.common.kotlin.util.ResultState

interface CharacterRepository {

    /** 도감 목록을 노출 순서 그대로 조회한다. */
    suspend fun getCharacters(): ResultState<List<Character>>

    /**
     * 쓸 캐릭터를 바꾼다. 보유(해금)한 것만 고를 수 있다 — 알 상태를 고르면 서버가 거절한다.
     * 같은 것을 다시 골라도 성공이다.
     */
    suspend fun selectCharacter(characterId: Long): ResultState<Unit>
}
