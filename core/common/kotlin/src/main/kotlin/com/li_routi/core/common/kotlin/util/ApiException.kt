package com.li_routi.core.common.kotlin.util

class ApiException(
    override val message: String,
    val statusCode: Int? = null,
    cause: Throwable? = null,
) : Exception(message, cause)
