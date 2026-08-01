package com.li_routi.core.data.mapper

import com.li_routi.core.data.network.dto.response.PresignedUrlResponse
import com.li_routi.core.domain.media.MediaPurpose
import com.li_routi.core.domain.media.PresignedUpload

fun PresignedUrlResponse.toDomain(): PresignedUpload = PresignedUpload(
    purpose = purpose.toMediaPurpose(),
    uploadUrl = uploadUrl,
    mediaKey = mediaKey,
    contentType = contentType,
    contentLength = contentLength,
)

private fun String.toMediaPurpose(): MediaPurpose =
    runCatching { MediaPurpose.valueOf(this) }
        .getOrElse { error("Unsupported media purpose: $this") }
