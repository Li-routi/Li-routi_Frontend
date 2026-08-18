package com.li_routi.core.data.di

import com.li_routi.core.data.network.NetworkModule
import com.li_routi.core.data.repository.CharacterRepositoryImpl
import com.li_routi.core.domain.character.CharacterRepository
import com.li_routi.core.domain.character.GetCharactersUseCase
import com.li_routi.core.domain.character.SelectCharacterUseCase

/** 캐릭터(도감/선택) API 수동 구성 root. */
object CharacterContainer {

    // MemberAppearanceStore(ShopContainer)도 같이 쓰므로 공개함 — 인스턴스를 하나로 유지한다.
    val repository: CharacterRepository by lazy {
        CharacterRepositoryImpl(NetworkModule.characterApiService)
    }

    val getCharactersUseCase: GetCharactersUseCase by lazy {
        GetCharactersUseCase(repository)
    }

    val selectCharacterUseCase: SelectCharacterUseCase by lazy {
        SelectCharacterUseCase(repository)
    }
}
