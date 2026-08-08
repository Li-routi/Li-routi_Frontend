package com.li_routi.core.data.mapper

import com.li_routi.core.data.network.dto.response.PresignedUrlResponse
import com.li_routi.core.domain.media.MediaPurpose
import com.li_routi.core.domain.media.PresignedUpload

// purpose는 응답에 없어 요청 시점에 이미 아는 값을 그대로 받는다(서버가 에코하지 않음).
fun PresignedUrlResponse.toDomain(purpose: MediaPurpose): PresignedUpload = PresignedUpload(
    purpose = purpose,
    uploadUrl = uploadUrl,
    mediaKey = mediaKey,
    contentType = contentType,
    contentLength = contentLength,
)
