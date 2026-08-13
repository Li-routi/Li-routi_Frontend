package com.li_routi.core.common.kotlin.util

import java.util.concurrent.CancellationException

suspend fun <T> safeApiCall(
    apiCall: suspend () -> T
): ResultState<T> {
    return try {
        ResultState.Success(apiCall())
    } catch (e: CancellationException) {
        // 취소는 실패가 아님. 삼키면 "StandaloneCoroutine was cancelled"가 토스트로 나감
        throw e
    } catch (e: Exception) {
        ResultState.Error(e.message ?: "알 수 없는 오류")
    }
}
