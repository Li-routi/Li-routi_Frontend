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
private suspend fun <T> callOrThrowApiException(call: suspend () -> T): T = try {
    call()
} catch (e: HttpException) {
    val errorResponse = e.response()?.errorBody()?.string()
        ?.let { body -> runCatching { errorBodyGson.fromJson(body, ApiResponse::class.java) }.getOrNull() }
    val message = e.retryAfterMessage()
        ?: errorResponse?.message
    throw ApiException(
        message = message ?: e.message(),
        statusCode = e.code(),
        errorCode = errorResponse?.code,
        cause = e,
    )
}

suspend fun <T> apiCall(call: suspend () -> ApiResponse<T>): T {
    val response = callOrThrowApiException(call)
    val result = response.result
    if (!response.isSuccess || result == null) {
        throw ApiException(message = response.message, errorCode = response.code)
    }
    return result
}

/**
 * [apiCall]과 동일하게 HTTP 에러 상태코드에서도 서버의 실제 메시지를 꺼내오지만, 성공 시 결과
 * 페이로드가 없거나(Unit) 의미가 없는 API(예: 대표 업적 설정/해제, 파도타기 루틴 선택)에 쓴다.
 * `if (!response.isSuccess) throw ApiException(response.message)`처럼 직접 확인하는 기존 방식은
 * HTTP 상태 코드 자체가 에러(4xx/5xx)일 때 이 코드에 도달하기도 전에 HttpException이 던져져
 * 서버 메시지 대신 "HTTP 409" 같은 일반 문구만 보이는 문제가 있었다.
 */
suspend fun apiCallUnit(call: suspend () -> ApiResponse<Unit?>) {
    val response = callOrThrowApiException(call)
    if (!response.isSuccess) {
        throw ApiException(message = response.message, errorCode = response.code)
    }
}
