package com.li_routi.core.data.network.dto.response

data class ReissueResponse(
    val accessToken: String,
    val refreshToken: String,
    val accessTokenExpiresIn: Long,
)