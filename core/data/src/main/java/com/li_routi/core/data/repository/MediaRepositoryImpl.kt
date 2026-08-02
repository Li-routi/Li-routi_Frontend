package com.li_routi.core.data.repository

import com.li_routi.core.common.kotlin.util.ApiException
import com.li_routi.core.common.kotlin.util.ResultState
import com.li_routi.core.data.mapper.toDomain
import com.li_routi.core.data.network.dto.request.PresignedUrlRequest
import com.li_routi.core.data.network.dto.response.ApiResponse
import com.li_routi.core.data.network.safeDataApiCall
import com.li_routi.core.data.network.service.MediaApiService
import com.li_routi.core.domain.media.MediaPurpose
import com.li_routi.core.domain.media.MediaRepository
import com.li_routi.core.domain.media.PresignedUpload
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import okhttp3.MediaType.Companion.toMediaType
import okhttp3.OkHttpClient
import okhttp3.Request
import okhttp3.RequestBody.Companion.toRequestBody

class MediaRepositoryImpl(
    private val api: MediaApiService,
    private val s3Client: OkHttpClient,
) : MediaRepository {

    override suspend fun issuePresignedUrl(
        purpose: MediaPurpose,
        contentType: String,
        contentLength: Long,
    ): ResultState<PresignedUpload> = safeDataApiCall {
        api.issuePresignedUrl(
            PresignedUrlRequest(
                purpose = purpose.name,
                contentType = contentType,
                contentLength = contentLength,
            ),
        ).unwrap().toDomain(purpose)
    }

    override suspend fun uploadToPresignedUrl(
        presigned: PresignedUpload,
        bytes: ByteArray,
    ): ResultState<Unit> = withContext(Dispatchers.IO) {
        safeDataApiCall {
            if (bytes.size.toLong() != presigned.contentLength) {
                throw ApiException("업로드 파일 크기가 발급 시 contentLength와 일치하지 않습니다.")
            }
            val body = bytes.toRequestBody(presigned.contentType.toMediaType())
            val request = Request.Builder()
                .url(presigned.uploadUrl)
                .put(body)
                .build()
            s3Client.newCall(request).execute().use { response ->
                if (!response.isSuccessful) {
                    throw ApiException(
                        when (response.code) {
                            403 -> "사진 업로드에 실패했습니다. 다시 시도해 주세요."
                            else -> "사진 업로드에 실패했습니다. (${response.code})"
                        },
                    )
                }
            }
        }
    }
}

private fun <T> ApiResponse<T>.unwrap(): T {
    val result = result
    if (!isSuccess || result == null) throw ApiException(message)
    return result
}
