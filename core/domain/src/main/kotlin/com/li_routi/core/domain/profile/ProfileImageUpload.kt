package com.li_routi.core.domain.profile

/** 로컬에서 읽어들인 프로필 이미지 원본. presigned URL 발급/업로드에 필요한 최소 정보만 담는다. */
class ProfileImageUpload(
    val bytes: ByteArray,
    val contentType: String,
)
