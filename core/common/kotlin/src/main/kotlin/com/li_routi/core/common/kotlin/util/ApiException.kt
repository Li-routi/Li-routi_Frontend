package com.li_routi.core.common.kotlin.util

class ApiException(
    override val message: String,
    val statusCode: Int? = null,
    /** 서버 공통 응답의 업무 오류 코드. HTTP 응답을 파싱할 수 없으면 null이다. */
    val errorCode: String? = null,
    cause: Throwable? = null,
) : Exception(message, cause)
