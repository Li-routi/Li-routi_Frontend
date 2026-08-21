package com.li_routi.feature.home.vm

import com.li_routi.core.common.ui.routine.CategoryColor
import com.li_routi.core.common.ui.routine.toCategoryColor
import com.li_routi.core.domain.home.GroupRoutine
import com.li_routi.core.domain.home.HomeSummary
import com.li_routi.core.domain.home.MyRoutine
import com.li_routi.core.domain.routine.RoutineCategory
import com.li_routi.feature.home.component.RoutineChecklistItemUiModel
import com.li_routi.feature.home.component.RoutineChecklistKind

/**
 * [HomeSummary] → 홈 체크리스트 UI 모델.
 * id 규칙: 개인 `my_{routineId}`, 그룹 `group_{groupId}_{routineId}` (인증 업로드 선택 키와 공유).
 *
 * `app` 모듈이 공유 인증 플로우(개인+그룹+챌린지 통합 선택 목록)를 만들 때도 이 매핑을 그대로
 * 재사용하도록 공개돼 있다 — id 규칙이 [RoutineAuthUploadHelper]의 파싱 로직과 반드시 일치해야 한다.
 */
fun HomeSummary.toHomeUiState(): HomeUiState {
    val myItems = myRoutines.map { it.toChecklistItem() }
    val groupItems = groupRoutines.map { it.toChecklistItem() }
    // Design Page [1.1] 탭 필터:
    // - 오늘의 루틴: 전체 + 카테고리명
    // - 그룹 루틴: 전체 + 방 이름(Figma `그룹1` …)
    val myCategoryNames = myRoutines
        .map { it.categoryName.trim() }
        .filter { it.isNotEmpty() }
        .distinct()
    val groupRoomNames = groupRoutines
        .map { it.groupName.trim() }
        .filter { it.isNotEmpty() }
        .distinct()
    return HomeUiState(
        nickname = userInfo.nickname.ifBlank { "닉네임" },
        representativeBadgeName = userInfo.representativeBadgeName,
        representativeBadgeImageUrl = userInfo.representativeBadgeImageUrl,
        hasActiveRoutine = myRoutines.isNotEmpty(),
        hasGroupRoom = groupRoutines.isNotEmpty(),
        myRoutineItems = myItems,
        myRoutineFilters = if (myRoutines.isEmpty()) emptyList() else listOf("전체") + myCategoryNames,
        groupRoomFilters = if (groupRoutines.isEmpty()) {
            emptyList()
        } else {
            listOf("전체") + groupRoomNames
        },
        groupRoomItems = groupItems,
        isLoading = false,
        loadError = false,
    )
}

/**
 * 카테고리 API 목록과 루틴에서 뽑은 필터를 합친다.
 * 루틴이 아직 없는 사용자 카테고리 chip도 유지한다.
 */
fun mergeCategoryFilters(
    routineFilters: List<String>,
    categories: List<RoutineCategory>,
    hasActiveRoutine: Boolean,
): List<String> {
    val fromCategories = categories.map { it.name.trim() }.filter { it.isNotEmpty() }
    val fromRoutines = routineFilters.filter { it != "전체" }
    val names = (fromCategories + fromRoutines).distinct()
    if (names.isEmpty() && !hasActiveRoutine) return emptyList()
    return listOf("전체") + names
}

fun RoutineChecklistItemUiModel.withCategoryColor(
    colorByCategoryId: Map<Long, String?>,
): RoutineChecklistItemUiModel {
    val id = categoryId ?: return this
    val mapped = colorByCategoryId[id].toCategoryColor() ?: return this
    return copy(categoryColor = mapped)
}

private fun MyRoutine.toChecklistItem(): RoutineChecklistItemUiModel = RoutineChecklistItemUiModel(
    id = "my_$routineId",
    title = name,
    dueLabel = formatPersonalRoutineTimeRange(startTime = startTime, endTime = endTime),
    categoryLabel = categoryName.trim(),
    isDone = completedToday,
    roomLabel = null,
    kind = RoutineChecklistKind.Member,
    routineId = routineId,
    groupId = null,
    categoryId = categoryId,
    canVerify = !completedToday,
    categoryColor = categoryColorFromId(categoryId),
)

private fun GroupRoutine.toChecklistItem(): RoutineChecklistItemUiModel = RoutineChecklistItemUiModel(
    id = "group_${groupId}_$routineId",
    title = title,
    dueLabel = formatGroupRoutineTimeRange(scheduledStartTime, scheduledEndTime),
    categoryLabel = categoryName.trim(),
    isDone = status.isDone,
    roomLabel = groupName.trim(),
    kind = RoutineChecklistKind.Group,
    routineId = routineId,
    groupId = groupId,
    categoryId = categoryId,
    canVerify = status.canVerify,
    categoryColor = categoryColorFromId(categoryId),
)

/**
 * 카테고리 API color가 없을 때의 fallback. 서버 color가 오면 [withCategoryColor]로 덮어쓴다.
 */
private fun categoryColorFromId(categoryId: Long): CategoryColor {
    val colors = CategoryColor.entries
    val index = ((categoryId % colors.size) + colors.size) % colors.size
    return colors[index.toInt()]
}

/** 개인 루틴 시트 기본 시작(오전 8시)과 동일. 값이 없으면 이 시각으로 둔다. */
internal const val DefaultPersonalStartTimeHHmm = "08:00"

internal fun String?.orDefaultPersonalStartTime(): String =
    this?.trim()?.takeIf { it.isNotEmpty() } ?: DefaultPersonalStartTimeHHmm

/**
 * 개인 루틴의 API 시간 값을 목록용 `HH:mm ~ HH:mm` 문구로 변환한다.
 *
 * 기존 루틴처럼 [startTime]이 없으면 화면에만 오전 8시를 표시한다. 마감 시각이 비어 있는
 * 이전 응답에는 서버 생성 기본값과 같은 23:59를 표시한다. 원본 nullable 값은 변경하지 않으므로
 * 서버의 시작 제한 없음 정책과 클라이언트 인증 가능 시간 계산에는 영향을 주지 않는다.
 *
 * @param startTime API가 전달한 nullable 시작 시각.
 * @param endTime API가 전달한 nullable 종료 시각.
 * @return 개인 루틴 목록에 표시할 시간 범위 문구.
 */
internal fun formatPersonalRoutineTimeRange(
    startTime: String?,
    endTime: String?,
): String {
    val resolvedEndTime = endTime?.trim()?.takeIf { it.isNotEmpty() } ?: "23:59"
    return "${startTime.orDefaultPersonalStartTime()} ~ $resolvedEndTime"
}

/** API `HH:mm` → UI `시작 - 마감`. 시작이 없으면 `마감 HH:mm`. */
fun formatRoutineTimeRange(startTime: String?, endTime: String?): String {
    val start = startTime?.trim().orEmpty()
    val end = endTime?.trim().orEmpty().removePrefix("마감").trim()
    return when {
        start.isNotEmpty() && end.isNotEmpty() -> "$start - $end"
        end.isNotEmpty() -> "마감 $end"
        start.isNotEmpty() -> start
        else -> ""
    }
}

fun formatGroupRoutineTimeRange(startTime: String?, endTime: String?): String =
    formatRoutineTimeRange(startTime, endTime).replace(" - ", " ~ ")
