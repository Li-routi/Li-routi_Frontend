package com.li_routi.core.data.repository

import com.li_routi.core.common.kotlin.util.ApiException
import com.li_routi.core.common.kotlin.util.ResultState
import com.li_routi.core.common.kotlin.util.safeApiCall
import com.li_routi.core.data.mapper.toDomain
import com.li_routi.core.data.network.apiCall
import com.li_routi.core.data.network.dto.request.FcmDeviceTokenRequest
import com.li_routi.core.data.network.dto.request.LogoutRequest
import com.li_routi.core.data.network.dto.request.SocialLoginRequest
import com.li_routi.core.data.network.dto.request.UpdateProfileRequest
import com.li_routi.core.data.network.dto.request.WithdrawRequest
import com.li_routi.core.data.network.service.AuthApiService
import com.li_routi.core.data.network.service.NotificationApiService
import com.li_routi.core.data.notification.FcmDeviceSyncGate
import com.li_routi.core.data.preference.AuthTokenPreference
import com.li_routi.core.data.preference.FcmTokenPreference
import com.li_routi.core.domain.auth.AuthRepository
import com.li_routi.core.domain.auth.AuthToken
import com.li_routi.core.domain.auth.MyInfo
import com.li_routi.core.domain.auth.ProfileImageUpload
import com.li_routi.core.domain.auth.SocialProvider
import com.li_routi.core.domain.media.MediaPurpose
import com.li_routi.core.domain.media.UploadMediaUseCase
import kotlinx.coroutines.flow.first

class AuthRepositoryImpl(
    private val api: AuthApiService,
    private val tokenPreference: AuthTokenPreference,
    private val uploadMediaUseCase: UploadMediaUseCase,
    private val fcmTokenPreference: FcmTokenPreference,
    private val notificationApi: NotificationApiService,
) : AuthRepository {

    override suspend fun issueGoogleNonce(): ResultState<String> = safeApiCall {
        apiCall { api.issueGoogleNonce() }.toDomain()
    }

    override suspend fun socialLogin(
        provider: SocialProvider,
        providerToken: String,
        nonce: String?,
    ): ResultState<AuthToken> = safeApiCall {
        apiCall {
            api.socialLogin(
                SocialLoginRequest(
                    provider = provider.name,
                    providerToken = providerToken,
                    nonce = nonce,
                ),
            )
        }.toDomain().also { tokenPreference.saveTokens(it) }
    }

    // 로그아웃 응답은 성공해도 result가 null이라 non-null 결과를 요구하는 apiCall()을 못 쓰고,
    // isSuccess만 직접 확인한다.
    // FCM 해제는 인증 헤더가 유효한 상태에서 먼저 시도한다(실패해도 로그아웃은 진행).
    // 등록과 같은 게이트로 직렬화해, 진행 중 등록이 로그아웃 이후에 토큰을 다시 심지 못하게 한다.
    override suspend fun logout(): ResultState<Unit> = safeApiCall {
        FcmDeviceSyncGate.withExclusive {
            unregisterFcmDeviceQuietly()
            val accessToken = tokenPreference.accessTokenFlow.first().orEmpty()
            val response = api.logout(LogoutRequest(accessToken = accessToken))
            if (!response.isSuccess) throw ApiException(response.message)
            fcmTokenPreference.clear()
            tokenPreference.clear()
        }
    }

    override suspend fun getMyInfo(): ResultState<MyInfo> = safeApiCall {
        apiCall { api.getMyInfo() }.toDomain()
    }

    override suspend fun updateProfile(nickname: String, image: ProfileImageUpload?): ResultState<MyInfo> =
        safeApiCall {
            val profileImageKey = image?.let { uploadProfileImage(it) }
            apiCall {
                api.updateProfile(UpdateProfileRequest(nickname = nickname, profileImageKey = profileImageKey))
            }.toDomain()
        }

    /** presigned URL 발급 → S3 PUT까지 미디어 도메인에 위임하고, 프로필 API에 넘길 mediaKey를 반환한다. */
    private suspend fun uploadProfileImage(image: ProfileImageUpload): String =
        when (val uploaded = uploadMediaUseCase(MediaPurpose.PROFILE, image.contentType, image.bytes)) {
            is ResultState.Success -> uploaded.data
            is ResultState.Error -> throw ApiException(uploaded.message)
            ResultState.Loading -> throw ApiException("업로드가 완료되지 않았습니다.")
        }

    // 탈퇴 응답도 로그아웃과 동일하게 non-null 결과를 요구하는 apiCall()을 못 써서 isSuccess만 직접 확인한다.
    override suspend fun withdraw(): ResultState<Unit> = safeApiCall {
        FcmDeviceSyncGate.withExclusive {
            unregisterFcmDeviceQuietly()
            val response = api.withdraw(WithdrawRequest(confirmation = WithdrawConfirmation))
            if (!response.isSuccess) throw ApiException(response.message)
            fcmTokenPreference.clear()
            tokenPreference.clear()
        }
    }

    private suspend fun unregisterFcmDeviceQuietly() {
        val fcmToken = fcmTokenPreference.getToken()?.takeIf { it.isNotBlank() } ?: return
        runCatching {
            notificationApi.unregisterDevice(FcmDeviceTokenRequest(token = fcmToken))
        }
    }

    private companion object {
        // 확인 모달에 별도 입력 필드가 없어 클라이언트가 고정값을 보낸다. 백엔드(MemberReqDTO.Withdraw)가
        // trim 후 이 문자열과 정확히 일치할 때만 통과시킨다 — 값을 바꾸려면 백엔드와 먼저 맞춰야 한다.
        const val WithdrawConfirmation = "리루티를 탈퇴합니다"
    }
}
