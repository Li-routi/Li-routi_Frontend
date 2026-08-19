package com.li_routi.feature.mypage.component

/**
 * 건의하기 한 건의 화면 모델.
 *
 * API에 제목이 없어 [categoryName]이 목록·상세의 타이틀 자리다. 상세 GET이 없어 목록에서 받은
 * [content]를 그대로 상세에 쓴다.
 */
data class SuggestionUiModel(
    val id: Long,
    val categoryName: String,
    val date: String,
    val content: String,
)

data class SuggestionCategoryUiModel(
    val id: Long,
    val name: String,
)
