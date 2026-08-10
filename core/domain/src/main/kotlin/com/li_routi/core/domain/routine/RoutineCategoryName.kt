package com.li_routi.core.domain.routine

/** 루틴 카테고리 이름 생성/수정 시 공통 검증 (카테고리 생성·수정 UseCase, 관련 ViewModel에서 공용으로 쓴다). */
object RoutineCategoryName {

    private const val MaxLength = 20

    sealed class Result {
        data class Valid(val trimmedName: String) : Result()
        data class Invalid(val message: String) : Result()
    }

    fun validate(name: String): Result {
        val trimmed = name.trim()
        return if (trimmed.isEmpty() || trimmed.length > MaxLength || '\n' in trimmed) {
            Result.Invalid("카테고리 이름은 1~${MaxLength}자로 입력해 주세요.")
        } else {
            Result.Valid(trimmed)
        }
    }
}
