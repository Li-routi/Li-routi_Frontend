package com.li_routi.feature.home.component

import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.IntrinsicSize
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.itemsIndexed
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.ColorFilter
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.li_routi.core.common.ui.routine.AddCategoryChip
import com.li_routi.core.common.ui.routine.CategoryColor
import com.li_routi.core.common.ui.routine.detectLabelLongClick
import com.li_routi.core.designsystem.R
import com.li_routi.core.designsystem.component.LiroutiBadge
import com.li_routi.core.designsystem.component.LiroutiBadgeColor
import com.li_routi.core.designsystem.component.LiroutiDivider
import com.li_routi.core.designsystem.component.LiroutiDividerOrientation
import com.li_routi.core.designsystem.component.LiroutiLabel
import com.li_routi.core.designsystem.component.LiroutiLineTab
import com.li_routi.core.designsystem.theme.LiroutiFrontendTheme
import com.li_routi.core.designsystem.theme.LiroutiTheme

/** 체크리스트 항목이 개인 루틴인지 그룹 루틴인지. 인증 API path 분기용. */
enum class RoutineChecklistKind {
    Member,
    Group,
}

/** "오늘의 루틴"/"그룹 루틴" 체크리스트 한 항목 (Figma `List` instance). */
data class RoutineChecklistItemUiModel(
    val id: String,
    val title: String,
    val dueLabel: String,
    val categoryLabel: String,
    val isDone: Boolean = false,
    /**
     * 그룹 루틴 방 이름(표시용 메타). 카테고리 필터는 [categoryLabel]로 한다.
     * 내 루틴 항목은 null.
     */
    val roomLabel: String? = null,
    val kind: RoutineChecklistKind = RoutineChecklistKind.Member,
    val routineId: Long? = null,
    val groupId: Long? = null,
    /** 개인 루틴 카테고리 id. 색 매핑용. */
    val categoryId: Long? = null,
    /** 카메라/업로드 선택 가능 여부. 완료·MISSED 등은 false. */
    val canVerify: Boolean = !isDone,
    /**
     * 카테고리 색. 도메인 매핑용으로 유지한다.
     * 홈 체크리스트 List UI(Figma `3962:11700`)에는 색 닷을 그리지 않는다.
     */
    val categoryColor: CategoryColor? = null,
)

/** Preview/개발 확인용 "오늘의 루틴" 샘플 (그룹방 있을 때, 미완료→완료 정렬용). */
val SampleMyRoutineItems: List<RoutineChecklistItemUiModel> = listOf(
    RoutineChecklistItemUiModel(
        id = "my_0",
        title = "물 마시기",
        dueLabel = "08:00 - 22:00",
        categoryLabel = "건강",
        routineId = 0L,
        categoryColor = CategoryColor.Blue,
    ),
    RoutineChecklistItemUiModel(
        id = "my_1",
        title = "강아지 산책",
        dueLabel = "08:00 - 18:00",
        categoryLabel = "운동",
        routineId = 1L,
        categoryColor = CategoryColor.Orange,
    ),
    RoutineChecklistItemUiModel(
        id = "my_2",
        title = "아침 회의",
        dueLabel = "08:00 - 23:00",
        categoryLabel = "공부",
        isDone = true,
        routineId = 2L,
        categoryColor = CategoryColor.Green,
    ),
    RoutineChecklistItemUiModel(
        id = "my_3",
        title = "스트레칭하기",
        dueLabel = "08:00 - 23:00",
        categoryLabel = "운동",
        isDone = true,
        routineId = 3L,
        categoryColor = CategoryColor.Orange,
    ),
    RoutineChecklistItemUiModel(
        id = "my_4",
        title = "물 마시기",
        dueLabel = "08:00 - 22:00",
        categoryLabel = "코딩",
        isDone = true,
        routineId = 4L,
        categoryColor = CategoryColor.Magenta,
    ),
)

/**
 * Preview/개발 확인용 "오늘의 루틴" 샘플 (그룹방 없을 때).
 *
 * id는 [SampleMyRoutineItems]와 맞춘다. 업로드 미리 선택이 깨지지 않도록
 * `solo_*` 같은 별도 prefix를 쓰지 않는다.
 */
val SampleMyRoutineItemsOnly: List<RoutineChecklistItemUiModel> = listOf(
    RoutineChecklistItemUiModel(
        id = "my_0",
        title = "물 마시기",
        dueLabel = "08:00 - 22:00",
        categoryLabel = "건강",
        routineId = 0L,
        categoryColor = CategoryColor.Blue,
    ),
    RoutineChecklistItemUiModel(
        id = "my_1",
        title = "강아지 산책",
        dueLabel = "08:00 - 18:00",
        categoryLabel = "운동",
        routineId = 1L,
        categoryColor = CategoryColor.Orange,
    ),
    RoutineChecklistItemUiModel(
        id = "my_2",
        title = "물 마시기",
        dueLabel = "08:00 - 22:00",
        categoryLabel = "건강",
        routineId = 2L,
        categoryColor = CategoryColor.Blue,
    ),
)

/**
 * Preview/개발 확인용 그룹 루틴 방 필터.
 * Figma Design Page [1.1] 그룹 탭: 전체 / 방 이름(그룹1…) — 카테고리가 아님.
 */
val SampleGroupRoomFilters: List<String> = listOf("전체", "바디프로필", "사이드 프로젝트")

/**
 * Preview/개발 확인용 "오늘의 루틴" 카테고리 필터.
 * Figma Design Page [1.1]: 전체 / 건강 / 운동 / 공부 / 코딩
 */
val SampleMyRoutineFilters: List<String> = listOf("전체", "건강", "운동", "공부", "코딩")

/** Preview/개발 확인용 "그룹 루틴" 샘플. [categoryLabel]이 필터 chip과 일치해야 한다.
 * id는 업로드 파서와 동일하게 `group_{groupId}_{routineId}` 형식을 쓴다.
 */
val SampleGroupRoomItems: List<RoutineChecklistItemUiModel> = listOf(
    RoutineChecklistItemUiModel(
        id = "group_10_100",
        title = "물 마시기",
        dueLabel = "08:00 - 22:00",
        categoryLabel = "건강",
        roomLabel = "바디프로필",
        kind = RoutineChecklistKind.Group,
        routineId = 100L,
        groupId = 10L,
        categoryColor = CategoryColor.Blue,
    ),
    RoutineChecklistItemUiModel(
        id = "group_11_101",
        title = "스트레칭하기",
        dueLabel = "08:00 - 23:00",
        categoryLabel = "운동",
        roomLabel = "사이드 프로젝트",
        kind = RoutineChecklistKind.Group,
        routineId = 101L,
        groupId = 11L,
        categoryColor = CategoryColor.Orange,
    ),
    RoutineChecklistItemUiModel(
        id = "group_10_102",
        title = "스트레칭하기",
        dueLabel = "08:00 - 23:00",
        categoryLabel = "공부",
        isDone = true,
        roomLabel = "바디프로필",
        kind = RoutineChecklistKind.Group,
        routineId = 102L,
        groupId = 10L,
        categoryColor = CategoryColor.Green,
    ),
)

private const val AllCategoryFilterLabel = "전체"

/** 카테고리 필터 chip 선택에 맞게 개인 루틴 목록을 걸러낸다. "전체"면 원본 그대로. */
internal fun List<RoutineChecklistItemUiModel>.filteredByCategory(
    selectedCategoryName: String,
): List<RoutineChecklistItemUiModel> {
    if (selectedCategoryName == AllCategoryFilterLabel) return this
    return filter { it.categoryLabel == selectedCategoryName }
}

/** 방 이름 필터 chip 선택에 맞게 그룹 루틴 목록을 걸러낸다. "전체"면 원본 그대로. */
internal fun List<RoutineChecklistItemUiModel>.filteredByRoom(
    selectedRoomName: String,
): List<RoutineChecklistItemUiModel> {
    if (selectedRoomName == AllCategoryFilterLabel) return this
    return filter { it.roomLabel == selectedRoomName }
}

/**
 * Figma 카테고리 추가 시트(`4741:43934`) 스와치 fill.
 * [CategoryColor.swatch]와 동일하므로 직접 swatch를 써도 된다.
 */
internal fun CategoryColor.toFigmaDotColor(): Color = swatch

/** Figma List (`3962:11700`) 카드 radius. */
private val ChecklistItemShape = RoundedCornerShape(4.dp)

/** Figma List 미완료 좌측 액센트 바 (`5238:31301`, w=4). */
private val ChecklistAccentWidth = 4.dp

private val HomeMainTabLabels = listOf("오늘의 루틴", "그룹 루틴")

/**
 * 홈 화면의 체크리스트 영역 (Design Page [1.1]).
 *
 * "오늘의 루틴"/"그룹 루틴"은 [LiroutiLineTab](밑줄 탭)으로 전환한다.
 * 그룹방 유무와 관계없이 두 탭을 노출하고, 그룹방 없이 「그룹 루틴」을 고르면 empty를 보여준다.
 * - 오늘의 루틴: 카테고리 필터 chip(전체/카테고리… +)
 * - 그룹 루틴: 방 이름 필터 chip(전체/그룹1…) — `+` 카테고리 추가는 개인 탭만
 *
 * 완료된 항목은 Figma 주석대로 하단에 정렬한다.
 */
@Composable
fun RoutineChecklistSection(
    hasGroupRoom: Boolean,
    myRoutineItems: List<RoutineChecklistItemUiModel>,
    onRoutineCameraClick: (String) -> Unit,
    modifier: Modifier = Modifier,
    myRoutineFilters: List<String> = emptyList(),
    groupRoomFilters: List<String> = SampleGroupRoomFilters,
    groupRoomItems: List<RoutineChecklistItemUiModel> = SampleGroupRoomItems,
    /** 카테고리명 → 색. 필터 칩·목록에 없는 빈 카테고리 색도 표시할 때 사용. */
    categoryColors: Map<String, CategoryColor> = emptyMap(),
    onAddCategoryClick: () -> Unit = {},
    onCategoryLongClick: (String) -> Unit = {},
    addCategoryEnabled: Boolean = true,
) {
    var selectedMainTab by remember { mutableIntStateOf(0) }
    // 인덱스가 아니라 필터명으로 보관 — refresh로 필터 목록이 바뀌어도 선택이 어긋나지 않는다.
    var selectedFilterName by remember { mutableStateOf(AllCategoryFilterLabel) }

    // Design Page [1.1]: 탭은 항상 노출. 그룹방 없어도 「그룹 루틴」 선택 시 empty 문구.
    val isGroupTab = selectedMainTab == 1
    val currentFilters = if (isGroupTab) groupRoomFilters else myRoutineFilters

    LaunchedEffect(currentFilters) {
        if (currentFilters.isNotEmpty() && selectedFilterName !in currentFilters) {
            selectedFilterName = AllCategoryFilterLabel
        }
    }

    val showFilters = currentFilters.isNotEmpty()
    val categoryColorByName = remember(myRoutineItems, categoryColors) {
        buildMap {
            putAll(categoryColors)
            myRoutineItems.forEach { item ->
                val color = item.categoryColor ?: return@forEach
                val key = item.categoryLabel.trim()
                if (key.isNotEmpty()) putIfAbsent(key, color)
            }
        }
    }
    // 그룹 루틴 탭은 각 그룹이 자기 안에서 설정한 카테고리 색이 아니라, "전체" + 그룹 필터
    // 목록에서 몇 번째 자리인지로 색이 정해진다 — 전체=파랑, 첫 번째 그룹=빨강, 두 번째=주황…
    // 카테고리 색 팔레트가 정확히 7개(파랑/빨강/주황/노랑/초록/마젠타/검정)이고, "전체" + 최대
    // 6개 그룹도 정확히 7개라 자리마다 하나씩 겹치지 않게 배정된다.
    val groupFilterColorByName = remember(groupRoomFilters) {
        groupRoomFilters.withIndex().associate { (index, label) ->
            label to CategoryColor.entries[index % CategoryColor.entries.size]
        }
    }
    val displayedItems = remember(
        isGroupTab,
        hasGroupRoom,
        myRoutineItems,
        groupRoomItems,
        selectedFilterName,
    ) {
        when {
            !isGroupTab -> myRoutineItems.filteredByCategory(selectedFilterName)
            hasGroupRoom -> groupRoomItems.filteredByRoom(selectedFilterName)
            else -> emptyList()
        }
    }
    val sortedItems = remember(displayedItems) { displayedItems.sortedBy { it.isDone } }
    val doneCount = displayedItems.count { it.isDone }
    val emptyMessage = when {
        isGroupTab && !hasGroupRoom -> "아직 참여한 그룹방이 없어요!"
        isGroupTab -> "아직 루틴이 없어요!"
        else -> "아직 루틴이 없어요!"
    }

    Column(
        modifier = modifier
            .fillMaxWidth()
            .padding(top = 4.dp),
        verticalArrangement = Arrangement.spacedBy(20.dp),
    ) {
        // Design Page [1.1]: 네모 세그먼트(LiroutiTabButton) → 밑줄 라인 탭(탭 시 전환)
        LiroutiLineTab(
            tabs = HomeMainTabLabels,
            selectedIndex = selectedMainTab,
            onTabSelected = { selectedMainTab = it },
            equalWidth = true,
        )

        if (showFilters) {
            // Figma상 오늘의 루틴 필터 행은 좌우 20dp, 그룹 탭은 16dp로 서로 달랐으나, 탭을 오갈 때
            // 카테고리 칩 시작 위치가 어긋나 보인다는 피드백으로 16dp로 통일함.
            LazyRow(
                modifier = Modifier.padding(horizontal = 16.dp),
                horizontalArrangement = Arrangement.spacedBy(6.dp),
                verticalAlignment = Alignment.CenterVertically,
            ) {
                itemsIndexed(currentFilters) { _, label ->
                    val canEdit = !isGroupTab && label != AllCategoryFilterLabel
                    val selectedColor = if (isGroupTab) {
                        groupFilterColorByName[label]?.swatch
                    } else if (label != AllCategoryFilterLabel) {
                        categoryColorByName[label]?.swatch
                    } else {
                        null
                    }
                    LiroutiLabel(
                        text = label,
                        selected = label == selectedFilterName,
                        onClick = { selectedFilterName = label },
                        selectedContainerColor = selectedColor,
                        modifier = Modifier.detectLabelLongClick(enabled = canEdit) {
                            onCategoryLongClick(label)
                        },
                    )
                }
                // Figma: 카테고리 `+`는 오늘의 루틴 탭만. 그룹 탭은 방 필터만.
                if (!isGroupTab) {
                    item {
                        AddCategoryChip(
                            onClick = onAddCategoryClick,
                            enabled = addCategoryEnabled,
                        )
                    }
                }
            }
        }

        if (displayedItems.isEmpty()) {
            // Figma `처음 진입 시`: 시트 peek 영역 안에서 empty가 가운데 오도록 최소 높이 확보
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .height(220.dp),
                contentAlignment = Alignment.Center,
            ) {
                EmptyRoutineSection(message = emptyMessage)
            }
        } else {
            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 16.dp)
                    .padding(bottom = 16.dp),
                verticalArrangement = Arrangement.spacedBy(8.dp),
            ) {
                // 그룹 탭에선 지금 선택된 필터의 자리색을 모든 표시 항목에 그대로 쓴다 — "전체"를
                // 누르면 서로 다른 그룹의 루틴이 섞여 보여도 전부 "전체"의 색(파랑)으로,
                // 특정 그룹을 누르면 그 그룹의 색으로 통일해서 보여준다.
                val groupAccentColor = if (isGroupTab) {
                    groupFilterColorByName[selectedFilterName]?.swatch
                } else {
                    null
                }
                sortedItems.forEach { item ->
                    RoutineChecklistItemRow(
                        item = item,
                        accentColorOverride = groupAccentColor,
                        onCameraClick = { onRoutineCameraClick(item.id) },
                    )
                }

                Column(
                    modifier = Modifier.fillMaxWidth(),
                    verticalArrangement = Arrangement.spacedBy(14.dp),
                    horizontalAlignment = Alignment.End,
                ) {
                    Text(
                        text = "$doneCount/${displayedItems.size} 완료",
                        // Figma Caption/Regular 12/14
                        style = LiroutiTheme.typography.captionRegular.copy(
                            fontSize = 12.sp,
                            lineHeight = 14.sp,
                        ),
                        color = LiroutiTheme.colors.labelSub,
                        textAlign = TextAlign.End,
                    )
                }
            }
        }
    }
}

/**
 * Figma List (`3962:11700` 미완료 / `3962:11702` 완료).
 * 미완료는 좌측 4dp 액센트 바(카테고리 색) + 메타(카테고리 | 시작 - 마감), 완료는 제목·완료 배지만.
 *
 * 액센트 높이는 [IntrinsicSize.Min] + [fillMaxHeight]로 콘텐츠에 맞춘다.
 * Box 안에서 단독 `fillMaxHeight()`를 쓰면 부모(바텀시트) maxHeight를 통째로 받아
 * 한 줄이 화면을 밀어버리는 버그가 난다.
 */
@Composable
private fun RoutineChecklistItemRow(
    item: RoutineChecklistItemUiModel,
    onCameraClick: () -> Unit,
    modifier: Modifier = Modifier,
    /** 그룹 루틴 탭에서 지금 선택된 필터의 자리색으로 강제할 때 넘긴다. null이면 [item]의 자체 색을 쓴다. */
    accentColorOverride: Color? = null,
) {
    val accentColor = accentColorOverride
        ?: item.categoryColor?.swatch
        ?: LiroutiTheme.colors.primaryNormal

    Row(
        modifier = modifier
            .fillMaxWidth()
            .height(IntrinsicSize.Min)
            .clip(ChecklistItemShape)
            .background(LiroutiTheme.colors.backgroundDefault)
            .border(1.dp, LiroutiTheme.colors.borderAlternative, ChecklistItemShape),
        verticalAlignment = Alignment.CenterVertically,
    ) {
        // 미완료만 좌측 액센트 — 카테고리별 스와치 색(없으면 primary)
        if (!item.isDone) {
            Box(
                modifier = Modifier
                    .width(ChecklistAccentWidth)
                    .fillMaxHeight()
                    .background(
                        color = accentColor,
                        shape = RoundedCornerShape(topStart = 6.dp, bottomStart = 6.dp),
                    ),
            )
        }
        Row(
            modifier = Modifier
                .weight(1f)
                .padding(
                    // 액센트(4dp)가 차지한 만큼 시작 패딩을 줄여 Figma의 좌측 14dp를 유지
                    start = if (item.isDone) 14.dp else (14.dp - ChecklistAccentWidth),
                    end = 14.dp,
                    top = if (item.isDone) 14.dp else 10.dp,
                    bottom = if (item.isDone) 14.dp else 10.dp,
                ),
            horizontalArrangement = Arrangement.spacedBy(12.dp),
            verticalAlignment = Alignment.CenterVertically,
        ) {
            Column(modifier = Modifier.weight(1f)) {
                Text(
                    text = item.title,
                    // Figma List title: Body3/Medium 14/22
                    style = LiroutiTheme.typography.body2LongMedium,
                    color = if (item.isDone) {
                        LiroutiTheme.colors.labelInfo
                    } else {
                        LiroutiTheme.colors.labelDefault
                    },
                    maxLines = 1,
                    overflow = TextOverflow.Ellipsis,
                )
                if (!item.isDone) {
                    Row(
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.spacedBy(6.dp),
                    ) {
                        // Design Page [1.1]: "카테고리 | HH:mm - HH:mm" — Caption/s 11/14
                        val metaStyle = LiroutiTheme.typography.captionRegular.copy(
                            fontSize = 11.sp,
                            lineHeight = 14.sp,
                        )
                        Text(
                            text = item.categoryLabel,
                            style = metaStyle,
                            // 카테고리 색은 좌측 액센트에만 쓰고, 메타 라벨은 가독성 위해 검정(labelDefault)
                            color = LiroutiTheme.colors.labelDefault,
                        )
                        LiroutiDivider(
                            orientation = LiroutiDividerOrientation.Vertical,
                            color = LiroutiTheme.colors.borderStrong,
                            modifier = Modifier.height(10.dp),
                        )
                        Text(
                            text = item.dueLabel,
                            style = metaStyle,
                            color = LiroutiTheme.colors.labelInfo,
                        )
                    }
                }
            }
            if (item.isDone) {
                LiroutiBadge(text = "완료", color = LiroutiBadgeColor.Neutral)
            } else if (!item.canVerify) {
                LiroutiBadge(text = "기간 만료", color = LiroutiBadgeColor.Neutral)
            } else {
                Image(
                    painter = painterResource(id = R.drawable.camera),
                    contentDescription = "루틴 인증 촬영",
                    modifier = Modifier
                        .size(20.dp)
                        .clickable(onClick = onCameraClick),
                    colorFilter = ColorFilter.tint(LiroutiTheme.colors.labelInfo),
                )
            }
        }
    }
}

@Preview(showBackground = true, heightDp = 560, name = "그룹방 O / 오늘의 루틴")
@Composable
private fun RoutineChecklistSectionWithGroupRoomPreview() {
    LiroutiFrontendTheme {
        RoutineChecklistSection(
            hasGroupRoom = true,
            myRoutineItems = SampleMyRoutineItems,
            myRoutineFilters = SampleMyRoutineFilters,
            onRoutineCameraClick = {},
        )
    }
}

@Preview(showBackground = true, heightDp = 360, name = "그룹방 X")
@Composable
private fun RoutineChecklistSectionWithoutGroupRoomPreview() {
    LiroutiFrontendTheme {
        RoutineChecklistSection(
            hasGroupRoom = false,
            myRoutineItems = SampleMyRoutineItemsOnly,
            myRoutineFilters = SampleMyRoutineFilters,
            onRoutineCameraClick = {},
        )
    }
}
