package com.li_routi.core.domain.grouproutine

/**
 * 방 나가기 결과.
 *
 * 서버가 그룹방 상세에 OWNER 여부를 안 내려줘서 프론트가 미리 알 수 없음 —
 * 일단 나가기를 시도해보고 [OwnerMustDelete]가 오면 삭제로 유도함
 */
enum class LeaveGroupResult {
    /** 정상적으로 나감 */
    Left,

    /** OWNER라서 못 나감(GROUP409_1). 방장 위임이나 그룹 삭제가 필요함 */
    OwnerMustDelete,
}
