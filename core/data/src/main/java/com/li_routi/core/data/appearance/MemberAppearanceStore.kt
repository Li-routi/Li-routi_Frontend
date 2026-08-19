package com.li_routi.core.data.appearance

import com.li_routi.core.common.kotlin.util.ResultState
import com.li_routi.core.data.preference.AuthTokenPreference
import com.li_routi.core.domain.character.CharacterRepository
import com.li_routi.core.domain.shop.AvatarEquippedItem
import com.li_routi.core.domain.shop.AvatarLayer
import com.li_routi.core.domain.shop.MemberAvatar
import com.li_routi.core.domain.shop.ShopRepository
import com.li_routi.core.domain.shop.hasCharacterLayer
import com.li_routi.core.domain.shop.withCharacterImageUrl
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.sync.Mutex
import kotlinx.coroutines.sync.withLock

/** 서버/기기에 캐릭터 id가 없을 때(로딩 전/비로그인) 쓰는 값. 실제 캐릭터 id로는 안 옴 */
const val FallbackCharacterId: Long = 0L

/**
 * 홈과 상점이 같이 보는 내 외형.
 *
 * 착장은 `GET /api/members/me/avatar`로, 캐릭터 본체는 `GET /api/characters`(선택된 것)로 받는다.
 */
data class MemberAppearance(
    /** 무엇을 입었는지(보유 표시 등)에만 쓴다 — 순서가 없어 겹쳐 그릴 수 없음 */
    val equipped: List<AvatarEquippedItem> = emptyList(),
    /** 그리는 데 쓴다. 받은 순서대로 겹치면 캐릭터·둥지·착장이 다 맞게 겹침 */
    val layers: List<AvatarLayer> = emptyList(),
    val characterId: Long = FallbackCharacterId,
    /** 서버가 이미 알/성체 중 보여줄 그림을 골라서 내려준 것. null이면 로컬 기본 이미지로 대체한다. */
    val characterImageUrl: String? = null,
)

/**
 * 운학님 설계: 정보가 없을 때만 받아서 앱이 기억하고, 상점에서 저장하면 서버와 로컬을 같이 갱신함.
 *
 * 화면마다 GET 하면 상점에서 갈아입고 홈으로 돌아와도 홈 ViewModel이 옛값을 들고 있음.
 * 그래서 여기서 한 번 받아 두고, 저장/구매 응답으로만 갈아끼운다.
 */
class MemberAppearanceStore(
    private val repository: ShopRepository,
    private val characterRepository: CharacterRepository,
    private val tokenPreference: AuthTokenPreference,
) {
    private val _appearance = MutableStateFlow(MemberAppearance())
    val appearance: StateFlow<MemberAppearance> = _appearance.asStateFlow()

    private val loadMutex = Mutex()
    private var avatarLoaded = false
    private var characterLoaded = false
    private var loadedForToken: String? = null

    /**
     * 착장/캐릭터가 아직 없으면 서버에서 받아 기억함. 이미 있으면 네트워크를 다시 치지 않음.
     */
    suspend fun ensureLoaded() {
        val token = tokenPreference.accessTokenFlow.first()
        loadMutex.withLock {
            if (loadedForToken != token) {
                avatarLoaded = false
                characterLoaded = false
                if (token.isNullOrBlank()) {
                    _appearance.value = MemberAppearance()
                    loadedForToken = token
                    return@withLock
                }
            }
            if (!characterLoaded) {
                fetchCharacterLocked(token)
            }
            if (!avatarLoaded) {
                fetchAvatarLocked(token)
            }
        }
    }

    /**
     * 서버에서 착장/캐릭터를 다시 받아 기억을 맞춤.
     *
     * 상점에 들어올 때·홈으로 돌아올 때 씀. 한 번 받은 뒤로는 GET을 안 치면
     * 저장 실패한 착장이나 빈 캐시가 계속 남음
     */
    suspend fun reloadAvatar() {
        val token = tokenPreference.accessTokenFlow.first()
        loadMutex.withLock {
            if (token.isNullOrBlank()) {
                _appearance.value = MemberAppearance()
                avatarLoaded = false
                characterLoaded = false
                loadedForToken = token
                return@withLock
            }
            fetchCharacterLocked(token)
            fetchAvatarLocked(token)
        }
    }

    private suspend fun fetchAvatarLocked(token: String?) {
        when (val result = repository.getMyAvatar()) {
            is ResultState.Success -> {
                _appearance.update { it.copy(equipped = result.data.equipped, layers = result.data.layers) }
                avatarLoaded = true
                loadedForToken = token
            }
            is ResultState.Error, ResultState.Loading -> Unit
        }
    }

    private suspend fun fetchCharacterLocked(token: String?) {
        when (val result = characterRepository.getCharacters()) {
            is ResultState.Success -> {
                // 가입 직후에도 조건 없는 기본 캐릭터 하나는 항상 selected로 옴(스웨거 문서 기준).
                // 못 찾으면 아무 해금 캐릭터나 추측해서 고르지 않는다 — 서버 목록 순서상 우연히
                // 앞에 온 캐릭터를 잘못 기본값처럼 보여줄 수 있어(실제로 파랑이 대신 노랑이가 뜨는
                // 문제가 있었음), 차라리 폴백 값(로컬 기본 파랑이 실루엣)을 그대로 두는 편이 안전하다.
                val selected = result.data.firstOrNull { it.selected }
                if (selected != null) {
                    _appearance.update {
                        it.copy(characterId = selected.id, characterImageUrl = selected.imageUrl)
                    }
                }
                characterLoaded = true
                loadedForToken = token
            }
            is ResultState.Error, ResultState.Loading -> Unit
        }
    }

    /** 구매/착장 저장 응답으로 로컬 기억을 맞춤. 홈이 이 흐름을 보고 바로 따라옴 */
    fun applyAvatar(avatar: MemberAvatar) {
        avatarLoaded = true
        _appearance.update { current ->
            current.copy(
                equipped = avatar.equipped,
                // 착장 PUT이 캐릭터 선택보다 늦게 반영되면 layers의 CHARACTER가 옛값일 수 있음.
                // 이미 저장한 캐릭터 그림이 있으면 그 URL로 맞춰 둠.
                layers = avatar.layers.withCharacterImageUrl(current.characterImageUrl),
            )
        }
    }

    /**
     * 쓸 캐릭터를 바꿔 서버에 저장하고 홈/상점이 같은 값을 보게 함.
     * 보유(해금)한 캐릭터만 고를 수 있다 — 서버가 그 외엔 거절한다.
     */
    suspend fun saveCharacter(characterId: Long, imageUrl: String?): ResultState<Unit> {
        val result = characterRepository.selectCharacter(characterId)
        if (result is ResultState.Success) {
            characterLoaded = true
            _appearance.update { current ->
                current.copy(
                    characterId = characterId,
                    characterImageUrl = imageUrl,
                    // 홈은 layers로 그리므로 characterImageUrl만 바꾸면 옛 캐릭터가 남음.
                    // 착장 PUT 응답이 오기 전에도 CHARACTER 레이어 URL만 갈아끼움.
                    layers = current.layers.withCharacterImageUrl(imageUrl),
                )
            }
            // CHARACTER가 없던 첫 해금은 클라이언트에서 깊이를 추측하지 않고 서버 순서를 다시 받음
            if (!_appearance.value.layers.hasCharacterLayer()) {
                reloadAvatar()
            }
        }
        return result
    }
}
