package com.li_routi.core.domain.media

import com.li_routi.core.common.kotlin.util.ResultState

/**
 * 사진 바이트를 purpose에 맞게 업로드하고 [mediaKey]를 반환한다.
 *
 * 1) presigned URL 발급 → 2) S3 PUT → 3) mediaKey 반환
 * 이후 도메인 인증 API에 mediaKey를 넘긴다.
 */
class UploadMediaUseCase(
    private val repository: MediaRepository,
) {
    suspend operator fun invoke(
        purpose: MediaPurpose,
        contentType: String,
        bytes: ByteArray,
    ): ResultState<String> {
        val length = bytes.size.toLong()
        return when (
            val issued = repository.issuePresignedUrl(
                purpose = purpose,
                contentType = contentType,
                contentLength = length,
            )
        ) {
            is ResultState.Success -> {
                when (val uploaded = repository.uploadToPresignedUrl(issued.data, bytes)) {
                    is ResultState.Success -> ResultState.Success(issued.data.mediaKey)
                    is ResultState.Error -> uploaded
                    ResultState.Loading -> ResultState.Loading
                }
            }
            is ResultState.Error -> issued
            ResultState.Loading -> ResultState.Loading
        }
    }
}
