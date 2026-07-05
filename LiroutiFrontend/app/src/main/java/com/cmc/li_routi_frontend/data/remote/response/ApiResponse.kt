package com.cmc.li_routi_frontend.data.remote.response

data class ApiResponse<T>(
    val success: Boolean,
    val code: String,
    val message: String,
    val data: T?
)