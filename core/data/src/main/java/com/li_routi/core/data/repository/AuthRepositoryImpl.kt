package com.li_routi.core.data.repository

import com.li_routi.core.common.kotlin.util.ApiException
import com.li_routi.core.common.kotlin.util.ResultState
import com.li_routi.core.common.kotlin.util.safeApiCall
import com.li_routi.core.data.mapper.toDomain
import com.li_routi.core.data.network.NetworkModule
import com.li_routi.core.data.network.apiCall
import com.li_routi.core.data.network.dto.request.FcmDeviceTokenRequest
import com.li_routi.core.data.network.dto.request.LogoutRequest
import com.li_routi.core.data.network.dto.request.SocialLoginRequest
import com.li_routi.core.data.network.dto.request.UpdateProfileRequest
import com.li_routi.core.data.network.dto.request.WithdrawRequest
import com.li_routi.core.data.network.service.AuthApiService
import com.li_routi.core.data.network.service.NotificationApiService
import com.li_routi.core.data.home.HomeReadinessCache
import com.li_routi.core.data.notification.FcmDeviceSyncGate
import com.li_routi.core.data.preference.AuthTokenPreference
import com.li_routi.core.data.preference.FcmTokenPreference
import com.li_routi.core.data.profile.MemberProfileCache
import com.li_routi.core.domain.auth.AuthRepository
import com.li_routi.core.domain.auth.AuthToken
import com.li_routi.core.domain.auth.MyInfo
import com.li_routi.core.domain.auth.MyVerificationDay
import com.li_routi.core.domain.auth.ProfileImageUpload
import com.li_routi.core.domain.auth.SocialProvider
import com.li_routi.core.domain.auth.VerificationReviewStatus
import com.li_routi.core.domain.media.MediaPurpose
import com.li_routi.core.domain.media.UploadMediaUseCase
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.withContext
import okhttp3.Request

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
            // 안 지우면 같은 기기에서 다른 계정으로 다시 로그인했을 때, 새 세션 조회가 끝나기 전까지
            // 이전 계정의 닉네임/캐릭터/알림 상태가 잠깐 그대로 보인다.
            MemberProfileCache.clear()
            HomeReadinessCache.clear()
        }
    }

    override suspend fun getMyInfo(): ResultState<MyInfo> = safeApiCall {
        apiCall { api.getMyInfo() }.toDomain()
    }

    override suspend fun updateProfile(nickname: String, image: ProfileImageUpload?): ResultState<MyInfo> =
        safeApiCall {
            val profileImageKey = resolveProfileImageKey(image)
            apiCall {
                api.updateProfile(UpdateProfileRequest(nickname = nickname, profileImageKey = profileImageKey))
            }.toDomain()
        }

    /**
     * [image]가 null(새 사진을 안 골랐음)이어도 서버가 `profileImageKey: null`을 "사진 삭제"로
     * 잘못 처리하는 문제가 있다(백엔드 이슈 — 필드를 아예 빼도 동일하게 삭제 처리됨을 확인함).
     * 기존 사진이 있으면 그 바이트를 다시 받아 재업로드해서 유효한 키를 채워 보낸다 — 닉네임만
     * 바꿔도 기존 프로필 사진이 지워지지 않게 하기 위한 우회다.
     *
     * 조회/다운로드/재업로드 중 어느 하나라도 실패하면 null로 폴백하지 않고 예외를 던져 저장
     * 자체를 실패시킨다 — null로 폴백하면 일시적인 네트워크 실패만으로도 (백엔드가 null을 "삭제"로
     * 해석해) 기존 프로필 사진이 지워질 수 있다.
     */
    private suspend fun resolveProfileImageKey(image: ProfileImageUpload?): String? {
        if (image != null) return uploadProfileImage(image)
        val currentImageUrl = when (val myInfo = getMyInfo()) {
            is ResultState.Success -> myInfo.data.profileImageUrl?.takeIf { it.isNotBlank() }
            is ResultState.Error -> throw ApiException(myInfo.message)
            ResultState.Loading -> throw ApiException("내 정보를 확인하지 못했습니다.")
        } ?: return null
        val existingImage = runCatching {
            ProfileImageUpload(bytes = downloadImageBytes(currentImageUrl), contentType = "image/jpeg")
        }.getOrElse { throw ApiException("기존 프로필 사진을 불러오지 못했습니다.") }
        return uploadProfileImage(existingImage)
    }

    /** OkHttp의 동기 [okhttp3.Call.execute]를 그대로 부르면 호출 스레드를 막으므로 IO로 옮긴다. */
    private suspend fun downloadImageBytes(url: String): ByteArray = withContext(Dispatchers.IO) {
        // 서버가 내려준 URL이라도 그대로 요청을 만들지 않는다 — presigned 업로드 쪽
        // (MediaRepositoryImpl.uploadToPresignedUrl)과 동일하게 HTTPS만 허용해, 응답이 조작되거나
        // 다른 스킴으로 바뀌어도 내부망 등 임의 호스트로 요청이 나가지 않게 한다.
        if (!url.startsWith("https://")) throw ApiException("잘못된 이미지 주소입니다.")
        val request = Request.Builder().url(url).build()
        NetworkModule.s3OkHttpClient.newCall(request).execute().use { response ->
            if (!response.isSuccessful) throw ApiException("이미지를 불러오지 못했습니다.")
            response.body?.bytes() ?: throw ApiException("이미지 응답이 비어 있습니다.")
        }
    }

    override suspend fun getMyVerifications(
        date: String?,
        status: VerificationReviewStatus?,
    ): ResultState<MyVerificationDay> = safeApiCall {
        apiCall { api.getMyVerifications(date = date, status = status?.name) }.toDomain()
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
            MemberProfileCache.clear()
            HomeReadinessCache.clear()
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
