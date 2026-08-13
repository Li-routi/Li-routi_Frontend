package com.li_routi.core.data.preference

import android.content.Context
import androidx.datastore.preferences.core.edit
import androidx.datastore.preferences.core.stringPreferencesKey
import androidx.datastore.preferences.preferencesDataStore
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map

private val Context.characterDataStore by preferencesDataStore(name = "character_prefs")

/**
 * 고른 캐릭터 본체를 기기에 보관한다.
 *
 * 착장(모자/옷/소품)은 `GET`/`PUT /api/members/me/avatar`로 서버에 남지만, 캐릭터 본체 id 필드는
 * 스웨거에 아직 없다. 그래서 계정이 아니라 기기에 남는다 — 기기를 바꾸면 기본 캐릭터로 돌아간다.
 * 캐릭터 API가 생기면 착장처럼 서버로 옮겨야 함
 */
class SelectedCharacterPreference(private val context: Context) {

    private val characterIdKey = stringPreferencesKey("selected_character_id")

    /** 상점에서 바꾸면 홈도 따라와야 해서 흐름으로 냄. 고른 적이 없으면 null */
    val characterId: Flow<String?> =
        context.characterDataStore.data.map { it[characterIdKey] }

    suspend fun save(characterId: String) {
        context.characterDataStore.edit { prefs ->
            prefs[characterIdKey] = characterId
        }
    }
}
