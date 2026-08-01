package com.li_routi.feature.home.component

import androidx.compose.ui.graphics.ColorFilter
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
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
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.li_routi.core.designsystem.R
import com.li_routi.core.designsystem.component.CheckBoxState
import com.li_routi.core.designsystem.component.CustomCheckBox
import com.li_routi.core.designsystem.component.LiroutiBadge
import com.li_routi.core.designsystem.component.LiroutiBadgeColor
import com.li_routi.core.designsystem.component.LiroutiDivider
import com.li_routi.core.designsystem.component.LiroutiDividerOrientation
import com.li_routi.core.designsystem.component.LiroutiLabel
import com.li_routi.core.designsystem.component.LiroutiTabButton
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
     * 그룹 루틴 방 이름. 방 필터 chip과 매칭한다.
     * 내 루틴 항목은 null.
     */
    val roomLabel: String? = null,
    val kind: RoutineChecklistKind = RoutineChecklistKind.Member,
    val routineId: Long? = null,
    val groupId: Long? = null,
    /** 카메라/업로드 선택 가능 여부. 완료·MISSED 등은 false. */
    val canVerify: Boolean = !isDone,
)

/** Preview/개발 확인용 "오늘의 루틴" 샘플 (그룹방 있을 때, 미완료→완료 정렬용). */
val SampleMyRoutineItems: List<RoutineChecklistItemUiModel> = listOf(
    RoutineChecklistItemUiModel(id = "my_0", title = "물 마시기", dueLabel = "마감 22:00", categoryLabel = "카테고리"),
    RoutineChecklistItemUiModel(id = "my_1", title = "스트레칭하기", dueLabel = "마감 23:00", categoryLabel = "카테고리"),
    RoutineChecklistItemUiModel(id = "my_2", title = "스트레칭하기", dueLabel = "마감 23:00", categoryLabel = "카테고리", isDone = true),
    RoutineChecklistItemUiModel(id = "my_3", title = "스트레칭하기", dueLabel = "마감 23:00", categoryLabel = "카테고리", isDone = true),
    RoutineChecklistItemUiModel(id = "my_4", title = "물 마시기", dueLabel = "마감 22:00", categoryLabel = "카테고리", isDone = true),
)

/**
 * Preview/개발 확인용 "오늘의 루틴" 샘플 (그룹방 없을 때).
 *
 * id는 [SampleMyRoutineItems]와 맞춘다. 업로드 미리 선택이 깨지지 않도록
 * `solo_*` 같은 별도 prefix를 쓰지 않는다.
 */
val SampleMyRoutineItemsOnly: List<RoutineChecklistItemUiModel> = listOf(
    RoutineChecklistItemUiModel(id = "my_0", title = "물 마시기", dueLabel = "마감 22:00", categoryLabel = "Sub tit"),
    RoutineChecklistItemUiModel(id = "my_1", title = "스트레칭하기", dueLabel = "마감 22:00", categoryLabel = "Sub tit"),
    RoutineChecklistItemUiModel(id = "my_2", title = "물 마시기", dueLabel = "마감 22:00", categoryLabel = "Sub tit"),
)

/** Preview/개발 확인용 "그룹 루틴" 방 필터 목록. */
val SampleGroupRoomFilters: List<String> = listOf("전체", "바디프로필", "사이드 프로젝트")

/** Preview/개발 확인용 "그룹 루틴" 샘플. [roomLabel]이 필터 chip과 일치해야 한다. */
val SampleGroupRoomItems: List<RoutineChecklistItemUiModel> = listOf(
    RoutineChecklistItemUiModel(
        id = "group_0",
        title = "물 마시기",
        dueLabel = "마감 22:00",
        categoryLabel = "카테고리",
        roomLabel = "바디프로필",
    ),
    RoutineChecklistItemUiModel(
        id = "group_1",
        title = "스트레칭하기",
        dueLabel = "마감 23:00",
        categoryLabel = "카테고리",
        roomLabel = "사이드 프로젝트",
    ),
    RoutineChecklistItemUiModel(
        id = "group_2",
        title = "스트레칭하기",
        dueLabel = "마감 23:00",
        categoryLabel = "카테고리",
        isDone = true,
        roomLabel = "바디프로필",
    ),
)

/** 방 필터 chip 선택에 맞게 그룹 루틴 목록을 걸러낸다. "전체"면 원본 그대로. */
internal fun List<RoutineChecklistItemUiModel>.filteredByRoom(
    filters: List<String>,
    selectedFilterIndex: Int,
): List<RoutineChecklistItemUiModel> {
    val selected = filters.getOrNull(selectedFilterIndex) ?: return this
    if (selected == "전체") return this
    return filter { it.roomLabel == selected }
}

private val HomeMainTabLabels = listOf("오늘의 루틴", "그룹 루틴")

/**
 * 홈 화면의 "내 루틴이 있을 때" 체크리스트 영역.
 *
 * - [hasGroupRoom]=false (내 루틴 O, 그룹방 X): 탭 없이 흰 카드 안에 "오늘의 루틴" 타이틀 + 리스트.
 * - [hasGroupRoom]=true (내 루틴 O, 그룹방 O): "오늘의 루틴"/"그룹 루틴" [HomeRoutineTabRow]로 전환.
 *   "그룹 루틴" 탭에서는 방 필터 chip이 나타나고, 선택에 따라 리스트를 교체할 수 있다.
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
    val displayedItems = remember(
        showGroupRoomTab,
        myRoutineItems,
        groupRoomItems,
        groupRoomFilters,
        selectedFilterIndex,
    ) {
        if (showGroupRoomTab) {
            groupRoomItems.filteredByRoom(groupRoomFilters, selectedFilterIndex)
        } else {
            myRoutineItems
        }
    }
    val sortedItems = remember(displayedItems) { displayedItems.sortedBy { it.isDone } }
    val doneCount = displayedItems.count { it.isDone }

    Column(
        modifier = modifier
            .fillMaxWidth()
            .padding(top = 4.dp),
        verticalArrangement = Arrangement.spacedBy(20.dp),
    ) {
        LiroutiTabButton(
            tabs = HomeMainTabLabels,
            selectedIndex = selectedMainTab,
            onTabSelected = { selectedMainTab = it },
            modifier = Modifier.padding(horizontal = 16.dp),
        )

        if (showGroupRoomTab) {
            LazyRow(
                modifier = Modifier.padding(horizontal = 16.dp),
                horizontalArrangement = Arrangement.spacedBy(6.dp),
            ) {
                itemsIndexed(groupRoomFilters) { index, label ->
                    LiroutiLabel(
                        text = label,
                        selected = index == selectedFilterIndex,
                        onClick = { selectedFilterIndex = index },
                    )
                }
            }
        }

        if (displayedItems.isEmpty()) {
            EmptyRoutineSection(
                message = if (showGroupRoomTab) "아직 참여한 그룹방이 없어요!" else "아직 루틴이 없어요!",
                modifier = Modifier.padding(bottom = 20.dp),
            )
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
                        style = LiroutiTheme.typography.caption,
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
                    LiroutiDivider(
                        orientation = LiroutiDividerOrientation.Vertical,
                        color = LiroutiTheme.colors.borderStrong,
                        modifier = Modifier.height(10.dp),
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
