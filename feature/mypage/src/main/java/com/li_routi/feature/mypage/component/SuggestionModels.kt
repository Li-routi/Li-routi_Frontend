package com.li_routi.feature.mypage.component

/**
 * 건의하기 한 건의 화면 모델.
 *
 * 상세 GET이 없어 목록에서 받은 [title]/[content]를 상세에 그대로 쓴다.
 */
data class SuggestionUiModel(
    val id: Long,
    val title: String,
    val categoryName: String,
    val date: String,
    val content: String,
)

data class SuggestionCategoryUiModel(
    val id: Long,
    val name: String,
)
