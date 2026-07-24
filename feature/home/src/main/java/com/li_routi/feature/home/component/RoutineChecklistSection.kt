package com.li_routi.feature.home.component

import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
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
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.li_routi.core.designsystem.component.DsPlaceholder
import com.li_routi.core.designsystem.theme.LiroutiFrontendTheme
import com.li_routi.core.designsystem.theme.LiroutiTheme

/** "오늘의 루틴"/"그룹 루틴" 체크리스트 한 항목 (Figma `List` instance). */
data class RoutineChecklistItemUiModel(
    val id: String,
    val title: String,
    val dueLabel: String,
    val categoryLabel: String,
    val isDone: Boolean = false,
)

/** Preview/개발 확인용 "오늘의 루틴" 샘플 (그룹방 있을 때, 미완료→완료 정렬용). */
val SampleMyRoutineItems: List<RoutineChecklistItemUiModel> = listOf(
    RoutineChecklistItemUiModel(id = "my_0", title = "물 마시기", dueLabel = "마감 22:00", categoryLabel = "카테고리"),
    RoutineChecklistItemUiModel(id = "my_1", title = "스트레칭하기", dueLabel = "마감 23:00", categoryLabel = "카테고리"),
    RoutineChecklistItemUiModel(id = "my_2", title = "스트레칭하기", dueLabel = "마감 23:00", categoryLabel = "카테고리", isDone = true),
    RoutineChecklistItemUiModel(id = "my_3", title = "스트레칭하기", dueLabel = "마감 23:00", categoryLabel = "카테고리", isDone = true),
    RoutineChecklistItemUiModel(id = "my_4", title = "물 마시기", dueLabel = "마감 22:00", categoryLabel = "카테고리", isDone = true),
)

/** Preview/개발 확인용 "오늘의 루틴" 샘플 (그룹방 없을 때). */
val SampleMyRoutineItemsOnly: List<RoutineChecklistItemUiModel> = listOf(
    RoutineChecklistItemUiModel(id = "solo_0", title = "물 마시기", dueLabel = "마감 22:00", categoryLabel = "Sub tit"),
    RoutineChecklistItemUiModel(id = "solo_1", title = "스트레칭하기", dueLabel = "마감 22:00", categoryLabel = "Sub tit"),
    RoutineChecklistItemUiModel(id = "solo_2", title = "물 마시기", dueLabel = "마감 22:00", categoryLabel = "Sub tit"),
)

/** Preview/개발 확인용 "그룹 루틴" 방 필터 목록. */
val SampleGroupRoomFilters: List<String> = listOf("전체", "바디프로필", "사이드 프로젝트")

/** Preview/개발 확인용 "그룹 루틴" 샘플. */
val SampleGroupRoomItems: List<RoutineChecklistItemUiModel> = listOf(
    RoutineChecklistItemUiModel(id = "group_0", title = "물 마시기", dueLabel = "마감 22:00", categoryLabel = "카테고리"),
    RoutineChecklistItemUiModel(id = "group_1", title = "스트레칭하기", dueLabel = "마감 23:00", categoryLabel = "카테고리"),
    RoutineChecklistItemUiModel(id = "group_2", title = "스트레칭하기", dueLabel = "마감 23:00", categoryLabel = "카테고리", isDone = true),
)

private val HomeMainTabLabels = listOf("오늘의 루틴", "그룹 루틴")

/**
 * 홈 화면의 "내 루틴이 있을 때" 체크리스트 영역.
 *
 * - [hasGroupRoom]=false (내 루틴 O, 그룹방 X): 탭 없이 흰 카드 안에 "오늘의 루틴" 타이틀 + 리스트.
 * - [hasGroupRoom]=true (내 루틴 O, 그룹방 O): "오늘의 루틴"/"그룹 루틴" [HomeRoutineTabRow]로 전환.
 *   "그룹 루틴" 탭에서는 방 필터 chip이 나타나고, 선택에 따라 리스트를 교체할 수 있다.
 *
 * Tab/Filter는 Design System instance라 완성되면 [DsPlaceholder] 자리에 끼운다.
 * 지금은 화면 상태 전환(탭/필터 선택)이 보이도록 레이아웃만 구현한다.
 *
 * 완료된 항목은 Figma 주석대로 하단에 정렬한다.
 */
@Composable
fun RoutineChecklistSection(
    hasGroupRoom: Boolean,
    myRoutineItems: List<RoutineChecklistItemUiModel>,
    onRoutineCameraClick: (String) -> Unit,
    modifier: Modifier = Modifier,
    groupRoomFilters: List<String> = SampleGroupRoomFilters,
    groupRoomItems: List<RoutineChecklistItemUiModel> = SampleGroupRoomItems,
) {
    var selectedMainTab by remember { mutableIntStateOf(0) }
    var selectedFilterIndex by remember { mutableIntStateOf(0) }

    val showGroupRoomTab = hasGroupRoom && selectedMainTab == 1
    val displayedItems = if (showGroupRoomTab) groupRoomItems else myRoutineItems
    val sortedItems = remember(displayedItems) { displayedItems.sortedBy { it.isDone } }
    val doneCount = displayedItems.count { it.isDone }

    Column(
        modifier = modifier
            .fillMaxWidth()
            .padding(horizontal = 16.dp)
            .padding(top = 28.dp, bottom = 16.dp),
        verticalArrangement = Arrangement.spacedBy(14.dp),
    ) {
        if (hasGroupRoom) {
            HomeRoutineTabRow(
                labels = HomeMainTabLabels,
                selectedIndex = selectedMainTab,
                onTabSelected = { selectedMainTab = it },
            )
        }

        Column(
            modifier = Modifier
                .fillMaxWidth()
                .clip(RoundedCornerShape(6.dp))
                .background(LiroutiTheme.colors.backgroundDefault)
                .padding(20.dp),
            verticalArrangement = Arrangement.spacedBy(16.dp),
        ) {
            if (!hasGroupRoom) {
                Text(
                    text = "오늘의 루틴",
                    style = LiroutiTheme.typography.body1,
                    color = LiroutiTheme.colors.labelStrong,
                )
            }

            if (showGroupRoomTab) {
                // TODO(design-system): Figma Filter 완성 시 실제 Filter 컴포넌트로 교체
                LazyRow(horizontalArrangement = Arrangement.spacedBy(6.dp)) {
                    itemsIndexed(groupRoomFilters) { index, label ->
                        GroupRoomFilterChip(
                            label = label,
                            selected = index == selectedFilterIndex,
                            onClick = { selectedFilterIndex = index },
                        )
                    }
                }
            }

            Column(verticalArrangement = Arrangement.spacedBy(2.dp)) {
                sortedItems.forEach { item ->
                    RoutineChecklistItemRow(
                        item = item,
                        onCameraClick = { onRoutineCameraClick(item.id) },
                    )
                }
            }

            Column(
                modifier = Modifier.fillMaxWidth(),
                verticalArrangement = Arrangement.spacedBy(14.dp),
                horizontalAlignment = Alignment.End,
            ) {
                Box(
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(1.dp)
                        .background(LiroutiTheme.colors.borderSub),
                )
                Text(
                    text = "$doneCount/${displayedItems.size} 완료",
                    style = LiroutiTheme.typography.caption,
                    color = LiroutiTheme.colors.labelSub,
                    textAlign = TextAlign.End,
                )
            }
        }
    }
}

/**
 * "오늘의 루틴" / "그룹 루틴" 세그먼트 탭.
 *
 * Design System `Tab` instance 자리. 선택 상태 전환이 보이도록 레이아웃만 구현한다.
 * TODO(design-system): Figma Tab 완성 시 실제 Tab 컴포넌트로 교체
 */
@Composable
private fun HomeRoutineTabRow(
    labels: List<String>,
    selectedIndex: Int,
    onTabSelected: (Int) -> Unit,
    modifier: Modifier = Modifier,
) {
    Row(
        modifier = modifier
            .fillMaxWidth()
            .height(40.dp)
            .clip(RoundedCornerShape(6.dp))
            .background(LiroutiTheme.colors.backgroundStrong)
            .padding(3.dp),
    ) {
        labels.forEachIndexed { index, label ->
            val selected = index == selectedIndex
            Box(
                modifier = Modifier
                    .weight(1f)
                    .fillMaxHeight()
                    .clip(RoundedCornerShape(4.dp))
                    .background(
                        if (selected) {
                            LiroutiTheme.colors.backgroundDefault
                        } else {
                            LiroutiTheme.colors.backgroundStrong
                        },
                    )
                    .clickable { onTabSelected(index) },
                contentAlignment = Alignment.Center,
            ) {
                Text(
                    text = label,
                    style = LiroutiTheme.typography.body2.copy(
                        fontWeight = if (selected) FontWeight.Bold else FontWeight.Medium,
                        fontSize = 13.sp,
                        lineHeight = 16.sp,
                    ),
                    color = if (selected) {
                        LiroutiTheme.colors.labelStrong
                    } else {
                        LiroutiTheme.colors.labelInfo
                    },
                    textAlign = TextAlign.Center,
                )
            }
        }
    }
}

/**
 * 그룹 루틴 방 필터 chip.
 *
 * Design System `Filter` instance 자리. 선택 시 primary 배경 + 체크 아이콘 placeholder.
 * TODO(design-system): Figma Filter 완성 시 실제 Filter 컴포넌트로 교체
 */
@Composable
private fun GroupRoomFilterChip(
    label: String,
    selected: Boolean,
    onClick: () -> Unit,
    modifier: Modifier = Modifier,
) {
    Row(
        modifier = modifier
            .height(38.dp)
            .clip(RoundedCornerShape(40.dp))
            .background(
                if (selected) LiroutiTheme.colors.primaryNormal else LiroutiTheme.colors.backgroundDefault,
            )
            .then(
                if (selected) {
                    Modifier
                } else {
                    Modifier.border(
                        BorderStroke(1.dp, LiroutiTheme.colors.borderDefault),
                        RoundedCornerShape(40.dp),
                    )
                },
            )
            .clickable(onClick = onClick)
            .padding(
                start = if (selected) 12.dp else 16.dp,
                end = 16.dp,
            ),
        verticalAlignment = Alignment.CenterVertically,
        horizontalArrangement = Arrangement.spacedBy(4.dp),
    ) {
        if (selected) {
            DsPlaceholder(
                componentName = "Icon/checkmark",
                modifier = Modifier.size(16.dp),
            )
        }
        Text(
            text = label,
            style = LiroutiTheme.typography.body2,
            color = if (selected) {
                LiroutiTheme.colors.labelReverse
            } else {
                LiroutiTheme.colors.labelStrong
            },
        )
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
            .padding(vertical = if (item.isDone) 14.dp else 10.dp),
        horizontalArrangement = Arrangement.spacedBy(12.dp),
        verticalAlignment = Alignment.CenterVertically,
    ) {
        DsPlaceholder(
            componentName = "Checkbox",
            shape = RoundedCornerShape(2.dp),
            modifier = Modifier.size(16.dp),
        )
        Column(modifier = Modifier.weight(1f)) {
            Text(
                text = item.title,
                style = LiroutiTheme.typography.body2,
                color = if (item.isDone) {
                    LiroutiTheme.colors.labelInfo
                } else {
                    LiroutiTheme.colors.labelStrong
                },
            )
            if (!item.isDone) {
                Row(
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.spacedBy(6.dp),
                ) {
                    Text(
                        text = item.dueLabel,
                        style = LiroutiTheme.typography.caption,
                        color = LiroutiTheme.colors.labelInfo,
                    )
                    Box(
                        modifier = Modifier
                            .width(1.dp)
                            .height(10.dp)
                            .background(LiroutiTheme.colors.borderStrong),
                    )
                    Text(
                        text = item.categoryLabel,
                        style = LiroutiTheme.typography.caption,
                        color = LiroutiTheme.colors.labelInfo,
                    )
                }
            }
        }
        if (item.isDone) {
            DsPlaceholder(
                componentName = "Badge/완료",
                shape = RoundedCornerShape(6.dp),
                modifier = Modifier.height(20.dp),
            )
        } else {
            DsPlaceholder(
                componentName = "Icon/camera",
                modifier = Modifier
                    .size(20.dp)
                    .clickable(onClick = onCameraClick),
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
            onRoutineCameraClick = {},
        )
    }
}
