package com.li_routi.core.domain.profile

import com.li_routi.core.common.kotlin.util.ResultState

class UpdateProfileUseCase(
    private val repository: ProfileRepository,
) {
    suspend operator fun invoke(nickname: String, image: ProfileImageUpload?): ResultState<MemberProfile> =
        repository.updateProfile(nickname, image)
}
