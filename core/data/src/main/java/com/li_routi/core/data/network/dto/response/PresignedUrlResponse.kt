package com.li_routi.core.data.network.dto.response

/**
 * POST /api/media/presigned-url 응답 result.
 *
 * 클라이언트는 [uploadUrl]로 PUT하고 [mediaKey]를 도메인 API에 전달한다.
 * Content-Type/Length는 응답 값을 그대로 써야 한다. purpose는 응답에 없다 —
 * 요청 시점에 이미 알고 있으므로 매핑 시 별도로 전달받는다.
 */
data class PresignedUrlResponse(
    val uploadUrl: String,
    val mediaKey: String,
    val contentType: String,
    val contentLength: Long,
)
