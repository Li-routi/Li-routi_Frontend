package com.li_routi.core.domain.grouproutine

// categoryId(기본 카테고리)랑 categoryKey(같은 요청의 NewGroupCategory.clientKey) 중 하나만 채워야 함
data class NewGroupRoutine(
    val categoryId: Long?,
    val categoryKey: String?,
    val title: String,
    val description: String,
    val schedules: List<GroupRoutineSchedule>,
) {
    init {
        require((categoryId == null) != (categoryKey == null)) {
            "categoryId와 categoryKey 중 정확히 하나만 값이 있어야 합니다."
        }
    }
}