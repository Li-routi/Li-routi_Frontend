package com.li_routi.core.domain.auth

import com.li_routi.core.common.kotlin.util.ResultState

class UpdateProfileUseCase(
    private val repository: AuthRepository,
) {
    suspend operator fun invoke(
        nickname: String,
        image: ProfileImageUpload? = null,
        removeImage: Boolean = false,
    ): ResultState<MyInfo> = repository.updateProfile(nickname, image, removeImage)
}
