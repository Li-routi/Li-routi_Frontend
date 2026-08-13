package com.li_routi.core.data.network

import com.li_routi.core.common.kotlin.util.ApiException
import com.li_routi.core.common.kotlin.util.ResultState
import kotlinx.coroutines.CancellationException
import retrofit2.HttpException
import java.io.IOException

private const val RetryAfterHeader = "Retry-After"

/**
 * 홈/미디어/루틴 인증용 API 호출 래퍼.
 * HTTP 상태 코드를 사용자용 메시지로 변환한다.
 */
internal suspend fun <T> safeDataApiCall(apiCall: suspend () -> T): ResultState<T> {
    return try {
        ResultState.Success(apiCall())
    } catch (e: CancellationException) {
        throw e
    } catch (e: Exception) {
        ResultState.Error(e.toUserFacingMessage())
    }
}

internal fun Throwable.toUserFacingMessage(): String = when (this) {
    is HttpException -> retryAfterMessage() ?: httpCodeToMessage(code())
    is IOException -> "네트워크 연결을 확인해 주세요."
    is ApiException -> message?.takeIf { it.isNotBlank() } ?: "요청에 실패했습니다."
    else -> message?.takeIf { it.isNotBlank() } ?: "알 수 없는 오류가 발생했습니다."
}

/**
 * 인증을 너무 자주 시도하면 서버가 [RetryAfterHeader] 헤더(초 단위)를 내려준다.
 * 있으면 상태 코드별 고정 문구보다 우선해 "n초 후 다시 시도해 주세요." 안내로 바꾼다.
 */
internal fun HttpException.retryAfterMessage(): String? =
    response()?.headers()?.get(RetryAfterHeader)
        ?.toIntOrNull()
        ?.takeIf { it > 0 }
        ?.let { seconds -> "${seconds}초 후 다시 시도해 주세요." }

private fun httpCodeToMessage(code: Int): String = when (code) {
    400 -> "요청 형식이 올바르지 않습니다."
    401, 403 -> "로그인이 필요합니다."
    404 -> "요청한 정보를 찾을 수 없습니다."
    409 -> "한도를 초과했거나 이미 등록된 항목입니다."
    413 -> "파일 용량이 너무 큽니다."
    in 500..599 -> "서버 오류가 발생했습니다. 잠시 후 다시 시도해 주세요."
    else -> "요청에 실패했습니다. ($code)"
}
