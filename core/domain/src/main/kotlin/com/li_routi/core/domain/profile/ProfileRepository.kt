package com.li_routi.core.domain.profile

import com.li_routi.core.common.kotlin.util.ResultState

interface ProfileRepository {

    /** 닉네임/프로필 이미지를 수정한다. [image]가 null이면 기존 프로필 이미지를 그대로 유지한다. */
    suspend fun updateProfile(nickname: String, image: ProfileImageUpload?): ResultState<MemberProfile>
}
