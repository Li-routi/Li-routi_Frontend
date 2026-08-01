package com.li_routi.core.domain.media

/**
 * POST /api/media/presigned-url 의 purpose.
 * 서버 enum 이름과 동일해야 한다.
 */
enum class MediaPurpose {
    CHALLENGE_VERIFICATION,
    MEMBER_ROUTINE_VERIFICATION,
    GROUP_ROUTINE_VERIFICATION,
}

/** 발급된 S3 업로드 정보. PUT 시 [contentType]/[contentLength]는 이 값을 그대로 써야 한다. */
data class PresignedUpload(
    val purpose: MediaPurpose,
    val uploadUrl: String,
    val mediaKey: String,
    val contentType: String,
    val contentLength: Long,
)
