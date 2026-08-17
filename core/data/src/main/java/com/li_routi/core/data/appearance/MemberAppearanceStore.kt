package com.li_routi.core.data.appearance

import com.li_routi.core.common.kotlin.util.ResultState
import com.li_routi.core.data.preference.AuthTokenPreference
import com.li_routi.core.data.preference.SelectedCharacterPreference
import com.li_routi.core.domain.shop.AvatarEquippedItem
import com.li_routi.core.domain.shop.MemberAvatar
import com.li_routi.core.domain.shop.ShopRepository
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.sync.Mutex
import kotlinx.coroutines.sync.withLock

/** 서버/기기에 캐릭터 id가 없을 때 쓰는 값. 상점 카탈로그 `blue_bird`와 같아야 함 */
const val FallbackCharacterId: String = "blue_bird"

/**
 * 홈과 상점이 같이 보는 내 외형.
 *
 * 착장은 [GET /api/members/me/avatar]로 받고, 캐릭터 본체는 아직 서버 필드가 없어 기기에 둔다.
 */
data class MemberAppearance(
    val equipped: List<AvatarEquippedItem> = emptyList(),
    val characterId: String = FallbackCharacterId,
)

/**
 * 운학님 설계: 정보가 없을 때만 받아서 앱이 기억하고, 상점에서 저장하면 서버와 로컬을 같이 갱신함.
 *
 * 화면마다 GET 하면 상점에서 갈아입고 홈으로 돌아와도 홈 ViewModel이 옛값을 들고 있음.
 * 그래서 여기서 한 번 받아 두고, 저장/구매 응답으로만 갈아끼운다.
 */
class MemberAppearanceStore(
    private val repository: ShopRepository,
    private val characterPreference: SelectedCharacterPreference,
    private val tokenPreference: AuthTokenPreference,
) {
    private val _appearance = MutableStateFlow(MemberAppearance())
    val appearance: StateFlow<MemberAppearance> = _appearance.asStateFlow()

    private val loadMutex = Mutex()
    private var avatarLoaded = false
    private var loadedForToken: String? = null

    /**
     * 착장이 아직 없으면 서버에서 받아 기억함. 이미 있으면 네트워크를 다시 치지 않음.
     *
     * 캐릭터 본체 API는 스웨거에 없어서, 기기에 없으면 기본 캐릭터를 기억해 둠
     */
    suspend fun ensureLoaded() {
        val token = tokenPreference.accessTokenFlow.first()
        loadMutex.withLock {
            if (loadedForToken != token) {
                avatarLoaded = false
                if (token.isNullOrBlank()) {
                    _appearance.value = MemberAppearance()
                    loadedForToken = token
                    return@withLock
                }
            }
            // 캐릭터 본체는 기기 로컬(DataStore)에 있어 네트워크보다 훨씬 빨리 읽을 수 있다.
            // 착장 GET(네트워크, 느림)보다 먼저 반영해야 앱 시작 시 기본 캐릭터("파랑이")가
            // 잠깐 보였다가 실제 캐릭터로 바뀌는 깜빡임이 없다 — appearance는 StateFlow라
            // 여기서 갱신하는 즉시 이미 구독 중인 화면에 반영된다.
            hydrateCharacterLocked()
            if (!avatarLoaded) {
                fetchAvatarLocked(token)
            }
        }
    }

    /**
     * 서버에서 착장을 다시 받아 기억을 맞춤.
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
                loadedForToken = token
                return@withLock
            }
            hydrateCharacterLocked()
            fetchAvatarLocked(token)
        }
    }

    private suspend fun fetchAvatarLocked(token: String?) {
        when (val result = repository.getMyAvatar()) {
            is ResultState.Success -> {
                _appearance.update { it.copy(equipped = result.data.equipped) }
                avatarLoaded = true
                loadedForToken = token
            }
            is ResultState.Error, ResultState.Loading -> Unit
        }
    }

    private suspend fun hydrateCharacterLocked() {
        val savedCharacterId = characterPreference.characterId.first()
        if (savedCharacterId.isNullOrBlank()) {
            characterPreference.save(FallbackCharacterId)
            _appearance.update { it.copy(characterId = FallbackCharacterId) }
        } else {
            _appearance.update { it.copy(characterId = savedCharacterId) }
        }
    }

    /** 구매/착장 저장 응답으로 로컬 기억을 맞춤. 홈이 이 흐름을 보고 바로 따라옴 */
    fun applyAvatar(avatar: MemberAvatar) {
        avatarLoaded = true
        _appearance.update { it.copy(equipped = avatar.equipped) }
    }

    /** 고른 캐릭터를 기기에 남기고 홈/상점이 같은 값을 보게 함. 서버에 보낼 필드가 아직 없음 */
    suspend fun saveCharacter(characterId: String) {
        characterPreference.save(characterId)
        _appearance.update { it.copy(characterId = characterId) }
    }
}
