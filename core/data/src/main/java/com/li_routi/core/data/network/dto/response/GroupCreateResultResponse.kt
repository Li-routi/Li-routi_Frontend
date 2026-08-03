package com.li_routi.core.data.network.dto.response

/** POST /api/groups 응답 result. customCategories/routines 세부 항목은 지금 화면에서 쓰지 않아 생략. */
data class GroupCreateResultResponse(
    val groupId: Long,
    val name: String,
    val assignmentCount: Int,
)
