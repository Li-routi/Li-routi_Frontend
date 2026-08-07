package com.li_routi.core.data.repository

import com.li_routi.core.common.kotlin.util.ApiException
import com.li_routi.core.common.kotlin.util.ResultState
import com.li_routi.core.common.kotlin.util.safeApiCall
import com.li_routi.core.data.mapper.toDomain
import com.li_routi.core.data.network.dto.request.PresignedUrlRequest
import com.li_routi.core.data.network.dto.request.UpdateProfileRequest
import com.li_routi.core.data.network.dto.response.ApiResponse
import com.li_routi.core.data.network.service.MediaApiService
import com.li_routi.core.data.network.service.MemberApiService
import com.li_routi.core.domain.profile.MemberProfile
import com.li_routi.core.domain.profile.ProfileImageUpload
import com.li_routi.core.domain.profile.ProfileRepository
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import okhttp3.MediaType.Companion.toMediaTypeOrNull
import okhttp3.OkHttpClient
import okhttp3.Request
import okhttp3.RequestBody.Companion.toRequestBody

private const val PresignedUrlPurposeProfile = "PROFILE"

class ProfileRepositoryImpl(
    private val memberApi: MemberApiService,
    private val mediaApi: MediaApiService,
    /** presigned URL로의 S3 직접 업로드는 우리 서버 인증 토큰을 실어 보내면 안 되므로, AuthInterceptor가 붙지 않은 클라이언트를 받는다. */
    private val uploadHttpClient: OkHttpClient,
) : ProfileRepository {

    override suspend fun updateProfile(
        nickname: String,
        image: ProfileImageUpload?,
    ): ResultState<MemberProfile> = safeApiCall {
        val profileImageKey = image?.let { uploadProfileImage(it) }
        memberApi.updateProfile(
            UpdateProfileRequest(nickname = nickname, profileImageKey = profileImageKey),
        ).unwrap().toDomain()
    }

    /** presigned URL을 발급받아 S3에 원본 바이트를 직접 PUT하고, 프로필 API에 넘길 mediaKey를 반환한다. */
    private suspend fun uploadProfileImage(image: ProfileImageUpload): String {
        val presigned = mediaApi.issuePresignedUrl(
            PresignedUrlRequest(
                purpose = PresignedUrlPurposeProfile,
                contentType = image.contentType,
                contentLength = image.bytes.size.toLong(),
            ),
        ).unwrap()

        val requestBody = image.bytes.toRequestBody(presigned.contentType.toMediaTypeOrNull())
        val request = Request.Builder()
            .url(presigned.uploadUrl)
            .put(requestBody)
            .build()

        // OkHttpClient.newCall(...).execute()는 블로킹 호출이라 IO 디스패처에서 실행한다.
        val response = withContext(Dispatchers.IO) { uploadHttpClient.newCall(request).execute() }
        response.use {
            if (!it.isSuccessful) throw ApiException("프로필 이미지 업로드에 실패했습니다.")
        }

        return presigned.mediaKey
    }
}

private fun <T> ApiResponse<T>.unwrap(): T {
    val result = result
    if (!isSuccess || result == null) throw ApiException(message)
    return result
}
