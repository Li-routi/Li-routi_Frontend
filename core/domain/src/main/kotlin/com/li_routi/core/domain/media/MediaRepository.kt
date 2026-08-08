package com.li_routi.core.domain.media

import com.li_routi.core.common.kotlin.util.ResultState

interface MediaRepository {

    /**
     * S3 업로드용 presigned URL을 발급받는다.
     *
     * @param purpose 업로드 용도
     * @param contentType 원본 Content-Type (예: image/jpeg)
     * @param contentLength 바이트 길이
     */
    suspend fun issuePresignedUrl(
        purpose: MediaPurpose,
        contentType: String,
        contentLength: Long,
    ): ResultState<PresignedUpload>

    /**
     * 발급받은 [PresignedUpload.uploadUrl]로 파일을 PUT 한다.
     * Content-Type·Content-Length는 반드시 [presigned]의 정규화된 값을 사용한다.
     */
    suspend fun uploadToPresignedUrl(
        presigned: PresignedUpload,
        bytes: ByteArray,
    ): ResultState<Unit>
}
