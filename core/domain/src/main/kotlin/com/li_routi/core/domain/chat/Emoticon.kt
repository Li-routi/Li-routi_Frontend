package com.li_routi.core.domain.chat

data class Emoticon(
    val id: Long,
    val code: String,
    val assetUrl: String,
    val contentType: String,
    val animated: Boolean,
)
