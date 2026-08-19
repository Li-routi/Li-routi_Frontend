package com.li_routi.core.data.network.dto.request

data class CreateSuggestionRequest(
    val categoryId: Long,
    val title: String,
    val content: String,
)
