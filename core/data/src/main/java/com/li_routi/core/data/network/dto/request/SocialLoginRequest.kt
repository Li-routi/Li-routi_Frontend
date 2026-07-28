package com.li_routi.core.data.network.dto.request

data class SocialLoginRequest(
    val provider: String,
    val providerToken: String,
    val nonce: String?,
)
