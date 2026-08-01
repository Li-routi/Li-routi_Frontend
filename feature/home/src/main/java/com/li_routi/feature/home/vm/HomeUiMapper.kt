package com.li_routi.feature.home.vm

import com.li_routi.core.domain.home.GroupRoutine
import com.li_routi.core.domain.home.HomeSummary
import com.li_routi.core.domain.home.MyRoutine
import com.li_routi.feature.home.component.RoutineChecklistItemUiModel
import com.li_routi.feature.home.component.RoutineChecklistKind

/**
 * [HomeSummary] → 홈 체크리스트 UI 모델.
 * id 규칙: 개인 `my_{routineId}`, 그룹 `group_{groupId}_{routineId}` (인증 업로드 선택 키와 공유).
 */
internal fun HomeSummary.toHomeUiState(): HomeUiState {
    val myItems = myRoutines.map { it.toChecklistItem() }
    val groupItems = groupRoutines.map { it.toChecklistItem() }
    val roomNames = groupRoutines.map { it.groupName }.distinct()
    return HomeUiState(
        nickname = userInfo.nickname.ifBlank { "닉네임" },
        hasActiveRoutine = myRoutines.isNotEmpty(),
        hasGroupRoom = groupRoutines.isNotEmpty(),
        myRoutineItems = myItems,
        groupRoomFilters = if (roomNames.isEmpty()) {
            emptyList()
        } else {
            listOf("전체") + roomNames
        },
        groupRoomItems = groupItems,
        isLoading = false,
        loadError = false,
    )
}

private fun MyRoutine.toChecklistItem(): RoutineChecklistItemUiModel = RoutineChecklistItemUiModel(
    id = "my_$routineId",
    title = name,
    dueLabel = endTime.toDueLabel(),
    categoryLabel = categoryName,
    isDone = completedToday,
    roomLabel = null,
    kind = RoutineChecklistKind.Member,
    routineId = routineId,
    groupId = null,
    canVerify = !completedToday,
)

private fun GroupRoutine.toChecklistItem(): RoutineChecklistItemUiModel = RoutineChecklistItemUiModel(
    id = "group_${groupId}_$routineId",
    title = title,
    dueLabel = scheduledEndTime.toDueLabel(),
    categoryLabel = categoryName,
    isDone = status.isDone,
    roomLabel = groupName,
    kind = RoutineChecklistKind.Group,
    routineId = routineId,
    groupId = groupId,
    canVerify = status.canVerify,
)

/** API `HH:mm` → UI `마감 HH:mm`. 값이 없으면 빈 문자열. */
private fun String?.toDueLabel(): String {
    val time = this?.trim().orEmpty()
    if (time.isEmpty()) return ""
    return if (time.startsWith("마감")) time else "마감 $time"
}
