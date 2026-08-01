package com.li_routi.core.data.network.dto.response

/**
 * POST /api/media/presigned-url 응답 result.
 *
 * 스웨거 예시에는 일부만 보이지만, 문서상 클라이언트는 [uploadUrl]로 PUT하고
 * [mediaKey]를 도메인 API에 전달한다. Content-Type/Length는 응답 값을 그대로 써야 한다.
 */
data class PresignedUrlResponse(
    val purpose: String,
    val uploadUrl: String,
    val mediaKey: String,
    val contentType: String,
    val contentLength: Long,
)
