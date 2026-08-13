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
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.li_routi.core.designsystem.component.CheckBoxState
import com.li_routi.core.designsystem.component.CustomCheckBox
import com.li_routi.core.designsystem.component.LiroutiBadge
import com.li_routi.core.designsystem.component.LiroutiBadgeColor
import com.li_routi.core.designsystem.component.LiroutiBottomSheetCloseButton
import com.li_routi.core.designsystem.component.LiroutiChevronLeftIcon
import com.li_routi.core.designsystem.component.LiroutiDashedAddButton
import com.li_routi.core.designsystem.component.LiroutiDivider
import com.li_routi.core.designsystem.component.LiroutiDividerOrientation
import com.li_routi.core.designsystem.component.LiroutiLabel
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
    /** false면 이미 등록된 기본 루틴 등으로 선택 변경 불가. */
    val selectable: Boolean = true,
    /** false면 체크박스를 그리지 않는다(선택 대상이 아니라 이미 등록된 항목을 그냥 보여줄 때). */
    val showCheckbox: Boolean = true,
    /** true면 행을 탭했을 때 [RoutineChecklistScreen.onItemClick]이 불린다(예: 커스텀 루틴 수정). */
    val editable: Boolean = false,
)

/**
 * 루틴 추가/선택 체크리스트 화면.
 *
 * Figma Design Page [1.1] `루틴 추가` (예: node `3704:61832`).
 */
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
    addCategoryEnabled: Boolean = true,
    primaryButtonEnabled: Boolean = true,
    onCategoryLongClick: (String) -> Unit = {},
    /** [RoutineChecklistItem.editable]이 true인 항목을 탭했을 때 호출(예: 커스텀 루틴 수정 시트 열기). */
    onItemClick: ((String) -> Unit)? = null,
    /** 30개 한도 초과 등 제출을 막는 이유. null이 아니면 하단 "총 N개 선택됨" 대신 이 문구를 보여준다. */
    warningText: String? = null,
) {
    Column(
        modifier = modifier
            .fillMaxSize()
            .background(LiroutiTheme.colors.backgroundDefault)
            .statusBarsPadding()
            .navigationBarsPadding(),
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 16.dp, vertical = 10.dp),
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
                // Figma Body1/Bold 18/28
                style = LiroutiTheme.typography.heading2Bold,
                color = LiroutiTheme.colors.labelDefault,
            )
            LiroutiBottomSheetCloseButton(onClick = onCloseClick)
        }

        Column(
            modifier = Modifier
                .weight(1f)
                .fillMaxWidth()
                .padding(horizontal = 16.dp)
                .padding(top = 25.dp),
            verticalArrangement = Arrangement.spacedBy(32.dp),
        ) {
            if (heading != null) {
                Column(verticalArrangement = Arrangement.spacedBy(2.dp)) {
                    Text(
                        text = heading,
                        style = LiroutiTheme.typography.heading2Bold,
                        color = LiroutiTheme.colors.labelDefault,
                    )
                    if (description != null) {
                        Text(
                            text = description,
                            // Figma Body4/Regular 13/16 · label/info
                            style = LiroutiTheme.typography.body3Regular.copy(lineHeight = 16.sp),
                            color = LiroutiTheme.colors.labelInfo,
                        )
                    }
                }
            }

            Column(
                modifier = Modifier.weight(1f, fill = false),
                verticalArrangement = Arrangement.spacedBy(12.dp),
            ) {
                RoutineCategoryChipRow(
                    categories = categories,
                    selectedCategory = selectedCategory,
                    onCategorySelected = onCategorySelected,
                    onAddCategoryClick = onAddCategoryClick,
                    addCategoryEnabled = addCategoryEnabled,
                    onCategoryLongClick = onCategoryLongClick,
                )

                RoutineSelectAllRow(
                    checked = allSelected,
                    onCheckedChange = onSelectAllChange,
                )

                LiroutiDivider(color = LiroutiTheme.colors.borderDefault)

                Column(
                    modifier = Modifier
                        .weight(1f, fill = false)
                        .heightIn(max = 312.dp)
                        .verticalScroll(rememberScrollState()),
                    verticalArrangement = Arrangement.spacedBy(8.dp),
                ) {
                    items.forEach { item ->
                        RoutineItemRow(
                            name = item.name,
                            deadlineText = item.deadlineText,
                            category = item.category,
                            repeatLabel = item.repeatLabel,
                            checked = if (item.showCheckbox) item.checked else null,
                            onCheckedChange = if (item.selectable && item.showCheckbox) {
                                { onItemCheckedChange(item.id, it) }
                            } else {
                                null
                            },
                            onRowClick = if (item.editable && onItemClick != null) {
                                { onItemClick(item.id) }
                            } else {
                                null
                            },
                        )
                    }
                }

                LiroutiDashedAddButton(
                    text = "루틴 추가",
                    onClick = onAddRoutineClick,
                )
            }
        }

        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(bottom = 32.dp),
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.spacedBy(20.dp),
        ) {
            Text(
                text = warningText
                    ?: "총 ${items.count { it.showCheckbox && it.checked }}개 선택됨",
                // Figma Caption/s 11/14
                style = LiroutiTheme.typography.captionRegular.copy(
                    fontSize = 11.sp,
                    lineHeight = 14.sp,
                ),
                color = if (warningText != null) {
                    LiroutiTheme.colors.primaryNormal
                } else {
                    LiroutiTheme.colors.labelInfo
                },
                textAlign = TextAlign.Center,
                modifier = Modifier.fillMaxWidth(),
            )
            LiroutiPrimaryButton(
                text = primaryButtonText,
                onClick = onPrimaryButtonClick,
                modifier = Modifier.padding(horizontal = 16.dp),
                enabled = primaryButtonEnabled,
            )
        }
    }
}

@Composable
fun RoutineCategoryChipRow(
    categories: List<String>,
    selectedCategory: String,
    onCategorySelected: (String) -> Unit,
    onAddCategoryClick: () -> Unit,
    modifier: Modifier = Modifier,
    addCategoryEnabled: Boolean = true,
    onCategoryLongClick: (String) -> Unit = {},
) {
    Row(
        modifier = modifier
            .fillMaxWidth()
            .horizontalScroll(rememberScrollState()),
        horizontalArrangement = Arrangement.spacedBy(6.dp),
        verticalAlignment = Alignment.CenterVertically,
    ) {
        categories.forEach { category ->
            val canEdit = category != "전체"
            LiroutiLabel(
                text = category,
                selected = category == selectedCategory,
                onClick = { onCategorySelected(category) },
                modifier = Modifier.detectLabelLongClick(enabled = canEdit) {
                    onCategoryLongClick(category)
                },
            )
        }
        AddCategoryChip(
            onClick = onAddCategoryClick,
            enabled = addCategoryEnabled,
        )
    }
}

@Composable
fun AddCategoryChip(
    onClick: () -> Unit,
    modifier: Modifier = Modifier,
    enabled: Boolean = true,
) {
    // Figma select +: 36×36
    Box(
        modifier = modifier
            .size(36.dp)
            .clip(CircleShape)
            .border(
                1.dp,
                if (enabled) LiroutiTheme.colors.borderDefault else LiroutiTheme.colors.borderSub,
                CircleShape,
            )
            .clickable(enabled = enabled, onClick = onClick),
        contentAlignment = Alignment.Center,
    ) {
        LiroutiPlusIcon(
            modifier = Modifier.size(20.dp),
            color = if (enabled) {
                LiroutiTheme.colors.labelDefault
            } else {
                LiroutiTheme.colors.labelInfo
            },
        )
    }
}

@Composable
private fun RoutineSelectAllRow(
    checked: Boolean,
    onCheckedChange: (Boolean) -> Unit,
    modifier: Modifier = Modifier,
) {
    Row(
        modifier = modifier
            .fillMaxWidth()
            .height(48.dp)
            .clip(RoundedCornerShape(6.dp))
            .border(1.dp, LiroutiTheme.colors.borderAlternative, RoundedCornerShape(6.dp))
            .clickable { onCheckedChange(!checked) }
            .padding(horizontal = 14.dp),
        horizontalArrangement = Arrangement.spacedBy(12.dp),
        verticalAlignment = Alignment.CenterVertically,
    ) {
        CustomCheckBox(
            state = if (checked) CheckBoxState.B else CheckBoxState.A,
            onClick = { onCheckedChange(!checked) },
        )
        Text(
            text = "전체 선택",
            style = LiroutiTheme.typography.body2LongSemiBold.copy(fontWeight = FontWeight.Bold),
            color = LiroutiTheme.colors.labelDefault,
            maxLines = 1,
            overflow = TextOverflow.Ellipsis,
            modifier = Modifier.weight(1f),
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
    /** null이 아니면 체크 토글 대신 이 콜백으로 행 전체 탭을 처리한다(예: 수정 시트 열기). */
    onRowClick: (() -> Unit)? = null,
    bold: Boolean = false,
) {
    val showMeta = deadlineText.isNotEmpty() || category.isNotEmpty()
    val cardHeight = if (showMeta || checked != null) 56.dp else 48.dp

    Row(
        modifier = modifier
            .fillMaxWidth()
            .heightIn(min = cardHeight)
            .clip(RoundedCornerShape(6.dp))
            .border(1.dp, LiroutiTheme.colors.borderAlternative, RoundedCornerShape(6.dp))
            .then(
                when {
                    onRowClick != null -> Modifier.clickable(onClick = onRowClick)
                    onCheckedChange != null -> Modifier.clickable {
                        onCheckedChange(!(checked ?: false))
                    }
                    else -> Modifier
                },
            )
            .padding(horizontal = 14.dp, vertical = 10.dp),
        horizontalArrangement = Arrangement.spacedBy(12.dp),
        verticalAlignment = Alignment.CenterVertically,
    ) {
        if (checked != null) {
            CustomCheckBox(
                state = if (checked) CheckBoxState.B else CheckBoxState.A,
                // onCheckedChange가 없는 행(예: 이미 등록된 커스텀 루틴 — 체크박스는 보여주되 탭으로
                // 해제는 안 됨)에서는 체크박스 자체를 비활성화해 터치가 그냥 사라지지 않고 아래 Row의
                // onRowClick(수정 시트 열기)으로 넘어가게 한다. enabled=true인 채로 두면
                // toggleable이 터치를 먼저 먹어버려서 행 전체 클릭도, 체크 해제도 둘 다 안 되는
                // "죽은 영역"이 된다.
                enabled = onCheckedChange != null,
                onClick = {
                    onCheckedChange?.invoke(!checked)
                },
            )
        }
        Column(modifier = Modifier.weight(1f)) {
            Text(
                text = name,
                // Figma List title: Body3/Medium 14/22 (전체 선택은 Bold)
                style = if (bold) {
                    LiroutiTheme.typography.body2LongSemiBold.copy(fontWeight = FontWeight.Bold)
                } else {
                    LiroutiTheme.typography.body2LongMedium
                },
                color = LiroutiTheme.colors.labelDefault,
                maxLines = 1,
                overflow = TextOverflow.Ellipsis,
            )
            if (showMeta) {
                Row(
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.spacedBy(6.dp),
                ) {
                    val metaStyle = LiroutiTheme.typography.captionRegular.copy(
                        fontSize = 11.sp,
                        lineHeight = 14.sp,
                    )
                    // Figma: 마감 | 카테고리
                    if (deadlineText.isNotEmpty()) {
                        Text(
                            text = deadlineText,
                            style = metaStyle,
                            color = LiroutiTheme.colors.labelInfo,
                        )
                    }
                    if (deadlineText.isNotEmpty() && category.isNotEmpty()) {
                        LiroutiDivider(
                            orientation = LiroutiDividerOrientation.Vertical,
                            color = LiroutiTheme.colors.borderStrong,
                            modifier = Modifier.height(10.dp),
                        )
                    }
                    if (category.isNotEmpty()) {
                        Text(
                            text = category,
                            style = metaStyle,
                            color = LiroutiTheme.colors.labelInfo,
                        )
                    }
                }
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
            primaryButtonText = "완료",
            onPrimaryButtonClick = {},
            onBackClick = {},
            onCloseClick = {},
        )
    }
}
