package com.li_routi.core.data.network.dto.request

data class PresignedUrlRequest(
    val purpose: String,
    val contentType: String,
    val contentLength: Long,
)
