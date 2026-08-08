package com.li_routi.core.domain.auth

import com.li_routi.core.common.kotlin.util.ResultState

class GetMyInfoUseCase(
    private val repository: AuthRepository,
) {
    suspend operator fun invoke(): ResultState<MyInfo> = repository.getMyInfo()
}
