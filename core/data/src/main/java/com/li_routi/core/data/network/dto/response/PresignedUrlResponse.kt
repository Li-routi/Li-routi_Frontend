package com.li_routi.core.data.network.dto.response

data class PresignedUrlResponse(
    val uploadUrl: String,
    val mediaKey: String,
    val mediaUrl: String,
    val contentType: String,
    val contentLength: Long,
    val expiresAt: String,
)
