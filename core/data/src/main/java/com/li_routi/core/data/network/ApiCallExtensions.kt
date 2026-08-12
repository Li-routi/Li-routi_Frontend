package com.li_routi.core.data.network

import com.google.gson.Gson
import com.li_routi.core.common.kotlin.util.ApiException
import com.li_routi.core.data.network.dto.response.ApiResponse
import retrofit2.HttpException

private val errorBodyGson = Gson()

/**
 * Retrofit은 suspend 함수의 응답 바디 타입이 [ApiResponse]처럼 non-Response 타입이면
 * HTTP 상태 코드가 2xx가 아닐 때 바디를 파싱하지 않고 [HttpException]을 던진다.
 * 이 경우 서버가 내려주는 실제 에러 메시지(message 필드)를 못 쓰게 되므로,
 * errorBody를 직접 [ApiResponse]로 파싱해 서버 메시지를 꺼내온다.
 */
suspend fun <T> apiCall(call: suspend () -> ApiResponse<T>): T {
    val response = try {
        call()
    } catch (e: HttpException) {
        val message = e.retryAfterMessage()
            ?: e.response()?.errorBody()?.string()
                ?.let { body -> runCatching { errorBodyGson.fromJson(body, ApiResponse::class.java).message }.getOrNull() }
        throw ApiException(
            message = message ?: e.message(),
            statusCode = e.code(),
            cause = e,
        )
    }
    val result = response.result
    if (!response.isSuccess || result == null) throw ApiException(response.message)
    return result
}
