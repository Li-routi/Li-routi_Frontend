package com.li_routi.core.common.ui.routine

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.horizontalScroll
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.heightIn
import androidx.compose.foundation.layout.navigationBarsPadding
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.statusBarsPadding
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.li_routi.core.designsystem.component.LiroutiBadge
import com.li_routi.core.designsystem.component.LiroutiBadgeColor
import com.li_routi.core.designsystem.component.LiroutiBottomSheetCloseButton
import com.li_routi.core.designsystem.component.LiroutiCheckmarkIcon
import com.li_routi.core.designsystem.component.LiroutiChevronLeftIcon
import com.li_routi.core.designsystem.component.LiroutiDashedAddButton
import com.li_routi.core.designsystem.component.LiroutiDivider
import com.li_routi.core.designsystem.component.LiroutiPlusIcon
import com.li_routi.core.designsystem.component.LiroutiPrimaryButton
import com.li_routi.core.designsystem.theme.LiroutiFrontendTheme
import com.li_routi.core.designsystem.theme.LiroutiTheme

data class RoutineChecklistItem(
    val id: String,
    val name: String,
    val checked: Boolean,
    val deadlineText: String = "",
    val category: String = "",
    val repeatLabel: String = "",
)

@Composable
fun RoutineChecklistScreen(
    topBarTitle: String,
    categories: List<String>,
    selectedCategory: String,
    onCategorySelected: (String) -> Unit,
    onAddCategoryClick: () -> Unit,
    items: List<RoutineChecklistItem>,
    onItemCheckedChange: (String, Boolean) -> Unit,
    allSelected: Boolean,
    onSelectAllChange: (Boolean) -> Unit,
    onAddRoutineClick: () -> Unit,
    primaryButtonText: String,
    onPrimaryButtonClick: () -> Unit,
    onBackClick: () -> Unit,
    onCloseClick: () -> Unit,
    modifier: Modifier = Modifier,
    heading: String? = null,
    description: String? = null,
) {
    Column(
        modifier = modifier
            .fillMaxSize()
            .background(LiroutiTheme.colors.backgroundDefault)
            .statusBarsPadding()
            .navigationBarsPadding()
            .padding(horizontal = 16.dp),
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .height(56.dp),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically,
        ) {
            LiroutiChevronLeftIcon(
                modifier = Modifier
                    .size(24.dp)
                    .clickable(onClick = onBackClick),
                color = LiroutiTheme.colors.labelDefault,
            )
            Text(
                text = topBarTitle,
                style = LiroutiTheme.typography.heading2SemiBold,
                color = LiroutiTheme.colors.labelDefault,
            )
            LiroutiBottomSheetCloseButton(onClick = onCloseClick)
        }

        if (heading != null) {
            Spacer(modifier = Modifier.height(20.dp))
            Text(
                text = heading,
                style = LiroutiTheme.typography.heading2SemiBold,
                color = LiroutiTheme.colors.labelDefault,
            )
            if (description != null) {
                Spacer(modifier = Modifier.height(4.dp))
                Text(
                    text = description,
                    style = LiroutiTheme.typography.body2LongRegular,
                    color = LiroutiTheme.colors.labelSub,
                )
            }
        }

        Spacer(modifier = Modifier.height(20.dp))

        RoutineCategoryChipRow(
            categories = categories,
            selectedCategory = selectedCategory,
            onCategorySelected = onCategorySelected,
            onAddCategoryClick = onAddCategoryClick,
        )

        Spacer(modifier = Modifier.height(16.dp))

        Column(modifier = Modifier.fillMaxWidth()) {
            RoutineItemRow(
                name = "전체 선택",
                checked = allSelected,
                onCheckedChange = onSelectAllChange,
                bold = true,
            )
            LiroutiDivider(color = LiroutiTheme.colors.borderSub)
            Column(
                modifier = Modifier
                    .heightIn(max = 260.dp)
                    .verticalScroll(rememberScrollState()),
            ) {
                items.forEach { item ->
                    RoutineItemRow(
                        name = item.name,
                        deadlineText = item.deadlineText,
                        category = item.category,
                        repeatLabel = item.repeatLabel,
                        checked = item.checked,
                        onCheckedChange = { onItemCheckedChange(item.id, it) },
                    )
                }
            }
        }

        Spacer(modifier = Modifier.weight(1f))

        LiroutiDashedAddButton(
            text = "루틴 추가",
            onClick = onAddRoutineClick,
        )

        Text(
            text = "총 ${items.count { it.checked }}개 선택됨",
            style = LiroutiTheme.typography.body3Regular,
            color = LiroutiTheme.colors.labelInfo,
            modifier = Modifier
                .fillMaxWidth()
                .padding(vertical = 8.dp),
            textAlign = TextAlign.Center,
        )

        LiroutiPrimaryButton(
            text = primaryButtonText,
            onClick = onPrimaryButtonClick,
            modifier = Modifier.padding(bottom = 16.dp),
        )
    }
}

@Composable
fun RoutineCategoryChipRow(
    categories: List<String>,
    selectedCategory: String,
    onCategorySelected: (String) -> Unit,
    onAddCategoryClick: () -> Unit,
    modifier: Modifier = Modifier,
) {
    Row(
        modifier = modifier
            .fillMaxWidth()
            .horizontalScroll(rememberScrollState()),
        horizontalArrangement = Arrangement.spacedBy(8.dp),
        verticalAlignment = Alignment.CenterVertically,
    ) {
        categories.forEach { category ->
            RoutineCategoryChip(
                label = category,
                selected = category == selectedCategory,
                onClick = { onCategorySelected(category) },
            )
        }
        AddCategoryChip(onClick = onAddCategoryClick)
    }
}

@Composable
fun RoutineCategoryChip(
    label: String,
    selected: Boolean,
    onClick: () -> Unit,
    modifier: Modifier = Modifier,
) {
    Row(
        modifier = modifier
            .height(32.dp)
            .clip(RoundedCornerShape(50))
            .then(
                if (selected) {
                    Modifier.background(LiroutiTheme.colors.primaryNormal)
                } else {
                    Modifier.border(1.dp, LiroutiTheme.colors.borderStrong, RoundedCornerShape(50))
                },
            )
            .clickable(onClick = onClick)
            .padding(horizontal = 14.dp),
        horizontalArrangement = Arrangement.spacedBy(4.dp),
        verticalAlignment = Alignment.CenterVertically,
    ) {
        if (selected) {
            LiroutiCheckmarkIcon(
                modifier = Modifier.size(14.dp),
                color = LiroutiTheme.colors.labelReverse,
            )
        }
        Text(
            text = label,
            style = LiroutiTheme.typography.body2SemiBold,
            color = if (selected) LiroutiTheme.colors.labelReverse else LiroutiTheme.colors.labelDefault,
        )
    }
}

@Composable
fun AddCategoryChip(
    onClick: () -> Unit,
    modifier: Modifier = Modifier,
) {
    Box(
        modifier = modifier
            .size(32.dp)
            .clip(CircleShape)
            .border(1.dp, LiroutiTheme.colors.borderStrong, CircleShape)
            .clickable(onClick = onClick),
        contentAlignment = Alignment.Center,
    ) {
        LiroutiPlusIcon(
            modifier = Modifier.size(14.dp),
            color = LiroutiTheme.colors.labelDefault,
        )
    }
}

/**
 * 루틴 한 건을 나타내는 행. [checked]가 null이면 체크박스 없이 표시한다(내 루틴 화면처럼 다건 선택이 필요
 * 없는 목록용). [deadlineText]/[category]가 비어있지 않으면 이름 아래에 "마감시간 | 카테고리" 서브텍스트를,
 * [repeatLabel]이 비어있지 않으면 오른쪽에 반복 배지를 표시한다.
 */
@Composable
fun RoutineItemRow(
    name: String,
    modifier: Modifier = Modifier,
    deadlineText: String = "",
    category: String = "",
    repeatLabel: String = "",
    checked: Boolean? = null,
    onCheckedChange: ((Boolean) -> Unit)? = null,
    bold: Boolean = false,
) {
    Row(
        modifier = modifier
            .fillMaxWidth()
            .then(
                if (onCheckedChange != null) {
                    Modifier.clickable { onCheckedChange(!(checked ?: false)) }
                } else {
                    Modifier
                },
            )
            .padding(vertical = 10.dp),
        horizontalArrangement = Arrangement.spacedBy(12.dp),
        verticalAlignment = Alignment.CenterVertically,
    ) {
        if (checked != null) {
            Box(
                modifier = Modifier
                    .size(20.dp)
                    .clip(RoundedCornerShape(4.dp))
                    .background(if (checked) LiroutiTheme.colors.primaryNormal else LiroutiTheme.colors.backgroundDefault)
                    .border(
                        width = 1.dp,
                        color = if (checked) LiroutiTheme.colors.primaryNormal else LiroutiTheme.colors.borderStrong,
                        shape = RoundedCornerShape(4.dp),
                    ),
                contentAlignment = Alignment.Center,
            ) {
                if (checked) {
                    LiroutiCheckmarkIcon(
                        modifier = Modifier.size(12.dp),
                        color = LiroutiTheme.colors.labelReverse,
                    )
                }
            }
        }
        Column(modifier = Modifier.weight(1f)) {
            Text(
                text = name,
                style = if (bold) LiroutiTheme.typography.body1SemiBold else LiroutiTheme.typography.body1Regular,
                color = LiroutiTheme.colors.labelDefault,
            )
            if (deadlineText.isNotEmpty() || category.isNotEmpty()) {
                Text(
                    text = listOf(deadlineText, category).filter { it.isNotEmpty() }.joinToString(" | "),
                    style = LiroutiTheme.typography.captionRegular,
                    color = LiroutiTheme.colors.labelInfo,
                )
            }
        }
        if (repeatLabel.isNotEmpty()) {
            LiroutiBadge(text = repeatLabel, color = LiroutiBadgeColor.Blue)
        }
    }
}

@Preview(showBackground = true, heightDp = 800)
@Composable
private fun RoutineChecklistScreenPreview() {
    var selectedCategory by remember { mutableStateOf("전체") }
    var items by remember {
        mutableStateOf(
            listOf(
                RoutineChecklistItem("1", "물 마시기", true, "마감 22:00", "건강", "주중"),
                RoutineChecklistItem("2", "물 마시기", false, "마감 22:00", "건강", "월,수,금"),
                RoutineChecklistItem("3", "물 마시기", false, "마감 22:00", "건강", "금요일마다"),
            ),
        )
    }
    LiroutiFrontendTheme {
        RoutineChecklistScreen(
            topBarTitle = "루틴 추가",
            heading = "내 루틴을 추가해 보세요",
            description = "루틴을 누르면 세부 설정을 변경할 수 있어요",
            categories = listOf("전체", "건강", "운동", "공부"),
            selectedCategory = selectedCategory,
            onCategorySelected = { selectedCategory = it },
            onAddCategoryClick = {},
            items = items,
            onItemCheckedChange = { id, checked ->
                items = items.map { if (it.id == id) it.copy(checked = checked) else it }
            },
            allSelected = items.all { it.checked },
            onSelectAllChange = { checked -> items = items.map { it.copy(checked = checked) } },
            onAddRoutineClick = {},
            primaryButtonText = "방 만들기",
            onPrimaryButtonClick = {},
            onBackClick = {},
            onCloseClick = {},
        )
    }
}
