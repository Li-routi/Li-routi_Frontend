package com.li_routi.core.data.network.dto.request

/**
 * PUT /api/members/me/avatar 요청 body.
 *
 * 보낸 것이 곧 전체 착장임 — 목록에 없는 자리는 벗겨지므로 항상 전부 실어 보내야 함
 */
data class EquipAvatarRequest(
    val itemIds: List<Long>,
)
