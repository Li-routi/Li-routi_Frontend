package com.li_routi.core.data.preference

import android.content.Context
import androidx.datastore.preferences.core.edit
import androidx.datastore.preferences.core.stringPreferencesKey
import androidx.datastore.preferences.preferencesDataStore
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.flow.map

private val Context.fcmTokenDataStore by preferencesDataStore(name = "fcm_token_prefs")

/** 서버에 등록한 FCM 기기 토큰을 로컬에 보관한다. 로그아웃/탈퇴 시 해제에 쓴다. */
class FcmTokenPreference(private val context: Context) {

    private val tokenKey = stringPreferencesKey("fcm_device_token")

    suspend fun saveToken(token: String) {
        context.fcmTokenDataStore.edit { prefs ->
            prefs[tokenKey] = token
        }
    }

    suspend fun getToken(): String? =
        context.fcmTokenDataStore.data.map { it[tokenKey] }.first()

    suspend fun clear() {
        context.fcmTokenDataStore.edit { it.clear() }
    }
}
