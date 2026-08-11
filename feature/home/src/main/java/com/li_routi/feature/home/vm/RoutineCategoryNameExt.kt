package com.li_routi.feature.home.vm

import com.li_routi.core.domain.routine.RoutineCategoryName

/**
 * 카테고리 이름 검증 결과를 소비하는 공통 헬퍼.
 *
 * 유효하면 trim된 이름으로 [action]을 실행하고 `null`을 반환한다.
 * 유효하지 않으면 [action]을 실행하지 않고 에러 메시지를 반환한다 — 호출부는 이 메시지를
 * 각자의 방식(uiState 갱신, 이벤트 emit 등)으로 노출하면 된다.
 *
 * inline이라 [action] 내부의 `return`은 호출부 함수를 그대로 빠져나간다(non-local return),
 * 기존 `when` 분기 안에서 바로 `return`하던 동작과 동일하다.
 */
internal inline fun RoutineCategoryName.Result.onValid(
    action: (trimmedName: String) -> Unit,
): String? = when (this) {
    is RoutineCategoryName.Result.Valid -> {
        action(trimmedName)
        null
    }
    is RoutineCategoryName.Result.Invalid -> message
}
