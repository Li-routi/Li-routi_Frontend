package com.li_routi.core.common.kotlin.util

sealed class ResultState<out T> {
    data class Success<T>(val data: T) : ResultState<T>()
    /** 실패 메시지와, 서버가 제공한 경우 업무 오류 코드를 함께 보존한다. */
    data class Error(
        val message: String,
        val errorCode: String? = null,
    ) : ResultState<Nothing>()
    object Loading : ResultState<Nothing>()
}
