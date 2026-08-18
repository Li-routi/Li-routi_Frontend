package com.li_routi.core.domain.character

/**
 * 캐릭터 한 마리 (`GET /api/characters` 도감 응답).
 *
 * [imageUrl]은 서버가 이미 알/성체 중 보여줄 그림을 골라서 내려준다 — 앱이 [unlocked] 여부로
 * 다시 고르면 안 된다. [unlocked]는 잠금 표시(자물쇠·흐리게)에만 쓴다.
 */
data class Character(
    val id: Long,
    /** 로컬 자산(폴백 이미지 등)과 매칭할 때 쓰는 논리 키. 예: "ROUTI" */
    val code: String,
    val name: String,
    val unlocked: Boolean,
    /** 지금 착용 중인 캐릭터인가 */
    val selected: Boolean,
    val imageUrl: String?,
    /** 해금한 날. 아직이면 null */
    val unlockedDate: String?,
)
