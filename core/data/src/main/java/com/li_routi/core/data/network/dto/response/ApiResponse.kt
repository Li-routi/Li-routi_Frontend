package com.li_routi.core.data.network.dto.response

data class ApiResponse<T>(
    val success: Boolean,
    val code: String,
    val message: String,
    val data: T?
)
