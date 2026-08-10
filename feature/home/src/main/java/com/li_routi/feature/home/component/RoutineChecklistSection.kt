package com.li_routi.feature.home.component

import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.itemsIndexed
import androidx.compose.foundation.shape.CircleShape
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
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.li_routi.core.common.ui.routine.AddCategoryChip
import com.li_routi.core.common.ui.routine.CategoryColor
import com.li_routi.core.common.ui.routine.detectLabelLongClick
import com.li_routi.core.designsystem.R
import com.li_routi.core.designsystem.component.CheckBoxState
import com.li_routi.core.designsystem.component.CustomCheckBox
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
     * 카테고리 색. Figma 카테고리 추가 시트 스와치와 동일 hex로 그린다
     * ([CategoryColor.toFigmaDotColor]).
     * null이면 제목 앞 category-dot를 그리지 않는다.
     */
    val categoryColor: CategoryColor? = null,
)

/** Preview/개발 확인용 "오늘의 루틴" 샘플 (그룹방 있을 때, 미완료→완료 정렬용). */
val SampleMyRoutineItems: List<RoutineChecklistItemUiModel> = listOf(
    RoutineChecklistItemUiModel(
        id = "my_0",
        title = "물 마시기",
        dueLabel = "마감 22:00",
        categoryLabel = "건강",
        routineId = 0L,
        categoryColor = CategoryColor.Blue,
    ),
    RoutineChecklistItemUiModel(
        id = "my_1",
        title = "강아지 산책",
        dueLabel = "마감 18:00",
        categoryLabel = "운동",
        routineId = 1L,
        categoryColor = CategoryColor.Orange,
    ),
    RoutineChecklistItemUiModel(
        id = "my_2",
        title = "아침 회의",
        dueLabel = "마감 23:00",
        categoryLabel = "공부",
        isDone = true,
        routineId = 2L,
        categoryColor = CategoryColor.Green,
    ),
    RoutineChecklistItemUiModel(
        id = "my_3",
        title = "스트레칭하기",
        dueLabel = "마감 23:00",
        categoryLabel = "운동",
        isDone = true,
        routineId = 3L,
        categoryColor = CategoryColor.Orange,
    ),
    RoutineChecklistItemUiModel(
        id = "my_4",
        title = "물 마시기",
        dueLabel = "마감 22:00",
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
        dueLabel = "마감 22:00",
        categoryLabel = "건강",
        routineId = 0L,
        categoryColor = CategoryColor.Blue,
    ),
    RoutineChecklistItemUiModel(
        id = "my_1",
        title = "강아지 산책",
        dueLabel = "마감 18:00",
        categoryLabel = "운동",
        routineId = 1L,
        categoryColor = CategoryColor.Orange,
    ),
    RoutineChecklistItemUiModel(
        id = "my_2",
        title = "물 마시기",
        dueLabel = "마감 22:00",
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
        dueLabel = "마감 22:00",
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
        dueLabel = "마감 23:00",
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
        dueLabel = "마감 23:00",
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
            LazyRow(
                modifier = Modifier.padding(horizontal = 16.dp),
                horizontalArrangement = Arrangement.spacedBy(6.dp),
                verticalAlignment = Alignment.CenterVertically,
            ) {
                itemsIndexed(currentFilters) { _, label ->
                    val canEdit = !isGroupTab && label != AllCategoryFilterLabel
                    LiroutiLabel(
                        text = label,
                        selected = label == selectedFilterName,
                        onClick = { selectedFilterName = label },
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
                sortedItems.forEach { item ->
                    RoutineChecklistItemRow(
                        item = item,
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

@Composable
private fun RoutineChecklistItemRow(
    item: RoutineChecklistItemUiModel,
    onCameraClick: () -> Unit,
    modifier: Modifier = Modifier,
) {
    Row(
        modifier = modifier
            .fillMaxWidth()
            .clip(RoundedCornerShape(6.dp))
            .background(LiroutiTheme.colors.backgroundDefault)
            .border(1.dp, LiroutiTheme.colors.borderAlternative, RoundedCornerShape(6.dp))
            .padding(horizontal = 14.dp, vertical = 10.dp),
        horizontalArrangement = Arrangement.spacedBy(12.dp),
        verticalAlignment = Alignment.CenterVertically,
    ) {
        CustomCheckBox(
            state = if (item.isDone) CheckBoxState.B else CheckBoxState.A,
        )
        Column(modifier = Modifier.weight(1f)) {
            Row(
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.spacedBy(6.dp),
            ) {
                // Figma List `category-dot` — 카테고리 추가 시트 스와치 hex
                item.categoryColor?.let { color ->
                    Box(
                        modifier = Modifier
                            .size(6.dp)
                            .clip(CircleShape)
                            .background(color.toFigmaDotColor()),
                    )
                }
                Text(
                    text = item.title,
                    // Figma List title: Body3/Medium 14/22
                    style = LiroutiTheme.typography.body2LongMedium,
                    color = if (item.isDone) {
                        LiroutiTheme.colors.labelInfo
                    } else {
                        LiroutiTheme.colors.labelStrong
                    },
                )
            }
            if (!item.isDone) {
                Row(
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.spacedBy(6.dp),
                ) {
                    // Design Page [1.1]: "카테고리 | 마감 HH:mm" — Caption/s 11/14
                    val metaStyle = LiroutiTheme.typography.captionRegular.copy(
                        fontSize = 11.sp,
                        lineHeight = 14.sp,
                    )
                    Text(
                        text = item.categoryLabel,
                        style = metaStyle,
                        color = LiroutiTheme.colors.labelInfo,
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
