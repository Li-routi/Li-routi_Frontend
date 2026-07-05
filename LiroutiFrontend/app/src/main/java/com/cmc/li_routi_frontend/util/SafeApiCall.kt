package com.cmc.li_routi_frontend.util

suspend fun <T> safeApiCall(
    apiCall: suspend () -> T
): ResultState<T> {
    return try {
        ResultState.Success(apiCall())
    } catch (e: Exception) {
        ResultState.Error(e.message ?: "알 수 없는 오류")
    }
}