package com.li_routi.feature.login.auth

import android.content.Context
import com.kakao.sdk.auth.model.OAuthToken
import com.kakao.sdk.common.model.ClientError
import com.kakao.sdk.common.model.ClientErrorCause
import com.kakao.sdk.user.UserApiClient
import kotlin.coroutines.resume
import kotlin.coroutines.resumeWithException
import kotlinx.coroutines.suspendCancellableCoroutine

/**
 * [GoogleAuthHelper.getGoogleIdToken]과 API 모양을 맞춰 suspend 함수로 감싼다 —
 * 하나는 콜백, 하나는 suspend로 남기면 ViewModel 호출부가 비대칭이라 실수하기 쉽다.
 */
class KakaoAuthHelper {

    suspend fun login(context: Context): String = suspendCancellableCoroutine { continuation ->
        val accountCallback: (OAuthToken?, Throwable?) -> Unit = { token, error ->
            when {
                error != null -> continuation.resumeWithException(error)
                token != null -> continuation.resume(token.accessToken)
                else -> continuation.resumeWithException(IllegalStateException("카카오 로그인 응답이 비어있습니다."))
            }
        }

        if (UserApiClient.instance.isKakaoTalkLoginAvailable(context)) {
            UserApiClient.instance.loginWithKakaoTalk(context) { token, error ->
                when {
                    // 사용자가 취소한 경우엔 카카오계정 로그인으로 폴백하지 않는다.
                    error is ClientError && error.reason == ClientErrorCause.Cancelled ->
                        continuation.resumeWithException(error)
                    error != null ->
                        UserApiClient.instance.loginWithKakaoAccount(context, callback = accountCallback)
                    token != null -> continuation.resume(token.accessToken)
                    else -> continuation.resumeWithException(IllegalStateException("카카오 로그인 응답이 비어있습니다."))
                }
            }
        } else {
            UserApiClient.instance.loginWithKakaoAccount(context, callback = accountCallback)
        }
    }
}
