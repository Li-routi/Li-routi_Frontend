package com.li_routi.core.data.preference

import android.content.Context
import androidx.datastore.preferences.core.edit
import androidx.datastore.preferences.core.longPreferencesKey
import androidx.datastore.preferences.core.stringPreferencesKey
import androidx.datastore.preferences.preferencesDataStore
import com.li_routi.core.domain.auth.AuthToken
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.runBlocking

private val Context.authTokenDataStore by preferencesDataStore(name = "auth_token_prefs")

/** 서비스 토큰(accessToken/refreshToken)을 DataStore에 저장/조회한다. */
class AuthTokenPreference(private val context: Context) {

    private val accessTokenKey = stringPreferencesKey("access_token")
    private val refreshTokenKey = stringPreferencesKey("refresh_token")
    private val accessTokenExpiresInKey = longPreferencesKey("access_token_expires_in")

    suspend fun saveTokens(token: AuthToken) {
        saveTokens(token.accessToken, token.refreshToken, token.accessTokenExpiresIn)
    }

    suspend fun saveTokens(accessToken: String, refreshToken: String, accessTokenExpiresIn: Long) {
        context.authTokenDataStore.edit { prefs ->
            prefs[accessTokenKey] = accessToken
            prefs[refreshTokenKey] = refreshToken
            prefs[accessTokenExpiresInKey] = accessTokenExpiresIn
        }
    }

    val accessTokenFlow: Flow<String?> = context.authTokenDataStore.data.map { it[accessTokenKey] }

    suspend fun getRefreshToken(): String? = context.authTokenDataStore.data.map { it[refreshTokenKey] }.first()

    /** [getAccessTokenBlocking]과 같은 이유로 동기 조회가 필요한 [okhttp3.Authenticator] 등을 위한 블로킹 조회. */
    fun getRefreshTokenBlocking(): String? = runBlocking { getRefreshToken() }

    /**
     * OkHttp [okhttp3.Interceptor]나 `Activity.onCreate`처럼 suspend를 쓸 수 없는 동기 호출부를 위한
     * 블로킹 조회. DataStore는 의도적으로 동기 API를 제공하지 않으므로 여기서만 `runBlocking`으로 감싼다.
     */
    fun getAccessTokenBlocking(): String? = runBlocking { accessTokenFlow.first() }

    suspend fun clear() {
        context.authTokenDataStore.edit { it.clear() }
    }
}
