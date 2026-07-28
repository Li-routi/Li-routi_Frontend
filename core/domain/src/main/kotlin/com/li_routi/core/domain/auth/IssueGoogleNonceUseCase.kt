package com.li_routi.core.domain.auth

import com.li_routi.core.common.kotlin.util.ResultState

class IssueGoogleNonceUseCase(
    private val repository: AuthRepository,
) {
    suspend operator fun invoke(): ResultState<String> = repository.issueGoogleNonce()
}
