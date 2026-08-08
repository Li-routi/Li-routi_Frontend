package com.li_routi.core.data.network.dto.request

/** POST /api/media/presigned-url 요청 body. */
data class PresignedUrlRequest(
    val purpose: String,
    val contentType: String,
    val contentLength: Long,
)
