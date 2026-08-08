package com.li_routi.core.domain.profile

/**
 * 업로드용으로 리사이징/압축까지 마친 프로필 이미지. presigned URL 발급/업로드에 필요한 최소 정보만 담는다.
 * [bytes]는 원본 그대로가 아니라 긴 변 기준으로 축소되고 JPEG로 재인코딩된 결과물이다 —
 * 원본 해상도 그대로 메모리에 올리면 큰 사진에서 OOM이 날 수 있기 때문이다.
 */
class ProfileImageUpload(
    val bytes: ByteArray,
    val contentType: String,
)
