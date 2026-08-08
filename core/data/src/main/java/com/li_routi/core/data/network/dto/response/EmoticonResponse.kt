package com.li_routi.core.data.network.dto.response

data class EmoticonListResponse(
    val emoticons: List<EmoticonResponse> = emptyList(),
)

data class EmoticonResponse(
    val id: Long,
    val code: String,
    val assetUrl: String,
    val contentType: String,
    val animated: Boolean = false,
)
