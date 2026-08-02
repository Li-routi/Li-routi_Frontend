package com.li_routi.core.domain.auth

import com.li_routi.core.common.kotlin.util.ResultState

interface AuthRepository {

    /** 구글 ID 토큰 재사용 공격 방지를 위한 1회용 nonce를 서버로부터 발급받는다. */
    suspend fun issueGoogleNonce(): ResultState<String>

    /**
     * 카카오/구글 SDK로 획득한 토큰을 서버에 검증 요청해 서비스 토큰을 발급받는다.
     *
     * @param nonce 구글 로그인 시 [issueGoogleNonce]로 발급받은 값을 그대로 전달한다. 카카오는 null.
     */
    suspend fun socialLogin(
        provider: SocialProvider,
        providerToken: String,
        nonce: String?,
    ): ResultState<AuthToken>

    /** 서버 세션을 무효화하고 로컬에 저장된 토큰을 지운다. */
    suspend fun logout(): ResultState<Unit>
}
