package com.li_routi.core.domain.routine

/**
 * 루틴 카테고리 이름 공통 검증.
 *
 * 규칙: trim 후 1~10자, 줄바꿈 불가, 「전체」 예약어 금지.
 */
object RoutineCategoryName {
    const val MaxLength: Int = 10
    const val ReservedAllLabel: String = "전체"

    const val InvalidLengthMessage: String = "카테고리 이름은 1~10자로 입력해 주세요."
    const val ReservedNameMessage: String = "「전체」는 사용할 수 없는 이름이에요."

    /**
     * @return 성공 시 trim된 이름, 실패 시 사용자용 에러 메시지.
     */
    fun validate(name: String): Result {
        val trimmed = name.trim()
        if (trimmed.isEmpty() || trimmed.length > MaxLength || '\n' in trimmed) {
            return Result.Invalid(InvalidLengthMessage)
        }
        if (trimmed == ReservedAllLabel) {
            return Result.Invalid(ReservedNameMessage)
        }
        return Result.Valid(trimmed)
    }

    sealed interface Result {
        data class Valid(val trimmedName: String) : Result
        data class Invalid(val message: String) : Result
    }
}
