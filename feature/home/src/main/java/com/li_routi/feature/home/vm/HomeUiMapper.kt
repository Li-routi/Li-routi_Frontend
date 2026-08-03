package com.li_routi.feature.home.vm

import com.li_routi.core.common.ui.routine.CategoryColor
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
    // Design Page [1.1] 그룹 탭 필터: 전체 + 카테고리명 (방 이름 아님)
    val categoryNames = groupRoutines
        .map { it.categoryName.trim() }
        .filter { it.isNotEmpty() }
        .distinct()
    return HomeUiState(
        nickname = userInfo.nickname.ifBlank { "닉네임" },
        hasActiveRoutine = myRoutines.isNotEmpty(),
        hasGroupRoom = groupRoutines.isNotEmpty(),
        myRoutineItems = myItems,
        groupRoomFilters = if (groupRoutines.isEmpty()) {
            emptyList()
        } else {
            listOf("전체") + categoryNames
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
    categoryLabel = categoryName.trim(),
    isDone = completedToday,
    roomLabel = null,
    kind = RoutineChecklistKind.Member,
    routineId = routineId,
    groupId = null,
    canVerify = !completedToday,
    // TODO: 홈 API에 categoryColor가 생기면 서버 색으로 교체. 현재는 id 기반 안정 매핑.
    categoryColor = categoryColorFromId(categoryId),
)

private fun GroupRoutine.toChecklistItem(): RoutineChecklistItemUiModel = RoutineChecklistItemUiModel(
    id = "group_${groupId}_$routineId",
    title = title,
    dueLabel = scheduledEndTime.toDueLabel(),
    categoryLabel = categoryName.trim(),
    isDone = status.isDone,
    roomLabel = groupName,
    kind = RoutineChecklistKind.Group,
    routineId = routineId,
    groupId = groupId,
    canVerify = status.canVerify,
    // TODO: 홈 API에 categoryColor가 생기면 서버 색으로 교체. 현재는 id 기반 안정 매핑.
    categoryColor = categoryColorFromId(categoryId),
)

/**
 * 홈 API에 카테고리 색 필드가 없어 categoryId로 [CategoryColor] 팔레트를 안정 매핑한다.
 * 서버가 색을 내려주면 그 값으로 교체한다.
 */
private fun categoryColorFromId(categoryId: Long): CategoryColor {
    val colors = CategoryColor.entries
    val index = ((categoryId % colors.size) + colors.size) % colors.size
    return colors[index.toInt()]
}

/** API `HH:mm` → UI `마감 HH:mm`. 값이 없으면 빈 문자열. */
private fun String?.toDueLabel(): String {
    val time = this?.trim().orEmpty()
    if (time.isEmpty()) return ""
    return if (time.startsWith("마감")) time else "마감 $time"
}
