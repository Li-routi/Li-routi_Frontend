package com.li_routi.core.domain.grouproutine

/** 그룹 생성과 함께 추가할 사용자 카테고리. [clientKey]로 [NewGroupRoutine.categoryKey]와 연결함 */
data class NewGroupCategory(
    val clientKey: String,
    val name: String,
    val color: String?,
)
