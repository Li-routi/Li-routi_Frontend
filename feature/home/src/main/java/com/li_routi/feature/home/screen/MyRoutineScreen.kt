package com.li_routi.feature.home.screen

import androidx.compose.foundation.Canvas
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.interaction.collectIsFocusedAsState
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.navigationBarsPadding
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.statusBarsPadding
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.BasicTextField
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
import androidx.compose.ui.draw.drawBehind
import androidx.compose.ui.geometry.CornerRadius
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.PathEffect
import androidx.compose.ui.graphics.SolidColor
import androidx.compose.ui.graphics.StrokeCap
import androidx.compose.ui.graphics.drawscope.Stroke
import androidx.compose.ui.res.painterResource
import androidx.compose.foundation.Image
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.li_routi.core.common.ui.routine.RoutineCategoryChipRow
import com.li_routi.core.common.ui.routine.RoutineChecklistItem
import com.li_routi.core.common.ui.routine.RoutineItemRow
import com.li_routi.core.designsystem.R
import com.li_routi.core.designsystem.component.LiroutiBottomSheetCloseButton
import com.li_routi.core.designsystem.component.LiroutiChevronLeftIcon
import com.li_routi.core.designsystem.component.LiroutiPlusIcon
import com.li_routi.core.designsystem.theme.LiroutiFrontendTheme
import com.li_routi.core.designsystem.theme.LiroutiTheme

/**
 * 내 루틴 목록 화면 (Figma Design Page [1.1] `루틴 추가` / `3731:63477`).
 *
 * 등록된 루틴 검색·카테고리 필터·항목 탭(세부 설정)·하단 「루틴 추가」.
 */
@Composable
fun MyRoutineScreen(
    query: String,
    onQueryChange: (String) -> Unit,
    categories: List<String>,
    selectedCategory: String,
    onCategorySelected: (String) -> Unit,
    onAddCategoryClick: () -> Unit,
    routines: List<RoutineChecklistItem>,
    onAddRoutineClick: () -> Unit,
    onBackClick: () -> Unit,
    onCloseClick: () -> Unit,
    modifier: Modifier = Modifier,
    addCategoryEnabled: Boolean = true,
    onRoutineClick: (String) -> Unit = {},
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
                .height(48.dp)
                .padding(horizontal = 16.dp),
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
                text = "내 루틴",
                style = LiroutiTheme.typography.heading2Bold,
                color = LiroutiTheme.colors.labelDefault,
            )
            LiroutiBottomSheetCloseButton(onClick = onCloseClick)
        }

        Column(
            modifier = Modifier
                .weight(1f)
                .fillMaxWidth()
                .verticalScroll(rememberScrollState())
                .padding(horizontal = 16.dp)
                .padding(top = 25.dp, bottom = 16.dp),
            verticalArrangement = Arrangement.spacedBy(32.dp),
        ) {
            Column(verticalArrangement = Arrangement.spacedBy(2.dp)) {
                Text(
                    text = "등록된 루틴",
                    style = LiroutiTheme.typography.heading2Bold,
                    color = LiroutiTheme.colors.labelDefault,
                )
                Text(
                    text = "루틴을 누르면 세부 설정을 변경할 수 있어요",
                    // Figma Body4/Regular 13/16 · label/info
                    style = LiroutiTheme.typography.body3Regular.copy(lineHeight = 16.sp),
                    color = LiroutiTheme.colors.labelInfo,
                )
            }

            MyRoutineSearchField(value = query, onValueChange = onQueryChange)

            Column(verticalArrangement = Arrangement.spacedBy(12.dp)) {
                RoutineCategoryChipRow(
                    categories = categories,
                    selectedCategory = selectedCategory,
                    onCategorySelected = onCategorySelected,
                    onAddCategoryClick = onAddCategoryClick,
                    addCategoryEnabled = addCategoryEnabled,
                )

                Column(verticalArrangement = Arrangement.spacedBy(8.dp)) {
                    routines.forEach { routine ->
                        RoutineItemRow(
                            name = routine.name,
                            deadlineText = routine.deadlineText,
                            category = routine.category,
                            repeatLabel = routine.repeatLabel,
                            modifier = Modifier.clickable { onRoutineClick(routine.id) },
                        )
                    }
                }

                MyRoutineDashedAddButton(onClick = onAddRoutineClick)
            }
        }
    }
}

/**
 * Figma Search (`3731:63612` / Activated `4741:58278`).
 * design-system [LiroutiSearchField]를 바꾸지 않고 화면 전용으로 구성한다.
 */
@Composable
private fun MyRoutineSearchField(
    value: String,
    onValueChange: (String) -> Unit,
    modifier: Modifier = Modifier,
) {
    val interactionSource = remember { MutableInteractionSource() }
    val isFocused by interactionSource.collectIsFocusedAsState()
    val shape = RoundedCornerShape(6.dp)
    val borderColor = if (isFocused) {
        LiroutiTheme.colors.labelDefault
    } else {
        LiroutiTheme.colors.borderDefault
    }
    // Figma Activated: 포커스 시 값이 비어 있어도 clear 노출
    val showClear = isFocused

    Row(
        modifier = modifier
            .fillMaxWidth()
            .height(44.dp)
            .background(LiroutiTheme.colors.backgroundDefault, shape)
            .border(1.dp, borderColor, shape)
            .padding(horizontal = 16.dp),
        horizontalArrangement = Arrangement.spacedBy(16.dp),
        verticalAlignment = Alignment.CenterVertically,
    ) {
        Row(
            modifier = Modifier.weight(1f),
            horizontalArrangement = Arrangement.spacedBy(10.dp),
            verticalAlignment = Alignment.CenterVertically,
        ) {
            Image(
                painter = painterResource(id = R.drawable.search),
                contentDescription = null,
                modifier = Modifier.size(20.dp),
            )
            Box(modifier = Modifier.weight(1f), contentAlignment = Alignment.CenterStart) {
                BasicTextField(
                    value = value,
                    onValueChange = onValueChange,
                    singleLine = true,
                    textStyle = LiroutiTheme.typography.body2LongMedium.copy(
                        color = LiroutiTheme.colors.labelDefault,
                    ),
                    cursorBrush = SolidColor(LiroutiTheme.colors.labelDefault),
                    interactionSource = interactionSource,
                    modifier = Modifier.fillMaxWidth(),
                    decorationBox = { innerTextField ->
                        Box(contentAlignment = Alignment.CenterStart) {
                            if (value.isEmpty()) {
                                Text(
                                    text = "루틴 검색",
                                    style = LiroutiTheme.typography.body2LongMedium,
                                    color = LiroutiTheme.colors.labelInfo,
                                )
                            }
                            innerTextField()
                        }
                    },
                )
            }
        }
        if (showClear) {
            val clearIconColor = LiroutiTheme.colors.backgroundAlternative
            Box(
                modifier = Modifier
                    .size(16.dp)
                    .clip(CircleShape)
                    .background(LiroutiTheme.colors.labelSub)
                    .clickable { onValueChange("") },
                contentAlignment = Alignment.Center,
            ) {
                Canvas(modifier = Modifier.size(10.dp)) {
                    val stroke = 1.2.dp.toPx()
                    drawLine(
                        color = clearIconColor,
                        start = Offset(0f, 0f),
                        end = Offset(size.width, size.height),
                        strokeWidth = stroke,
                        cap = StrokeCap.Round,
                    )
                    drawLine(
                        color = clearIconColor,
                        start = Offset(size.width, 0f),
                        end = Offset(0f, size.height),
                        strokeWidth = stroke,
                        cap = StrokeCap.Round,
                    )
                }
            }
        }
    }
}

/** Figma Label+Trailing (`3731:63644`): 56h, Medium, label/default. */
@Composable
private fun MyRoutineDashedAddButton(
    onClick: () -> Unit,
    modifier: Modifier = Modifier,
) {
    val strokeColor = LiroutiTheme.colors.borderDefault
    Row(
        modifier = modifier
            .fillMaxWidth()
            .height(56.dp)
            .clickable(onClick = onClick)
            .drawBehind {
                drawRoundRect(
                    color = strokeColor,
                    style = Stroke(
                        width = 1.dp.toPx(),
                        pathEffect = PathEffect.dashPathEffect(floatArrayOf(8f, 6f)),
                    ),
                    cornerRadius = CornerRadius(6.dp.toPx()),
                )
            },
        horizontalArrangement = Arrangement.Center,
        verticalAlignment = Alignment.CenterVertically,
    ) {
        Text(
            text = "루틴 추가",
            style = LiroutiTheme.typography.body2Medium,
            color = LiroutiTheme.colors.labelDefault,
        )
        LiroutiPlusIcon(
            modifier = Modifier.size(16.dp),
            color = LiroutiTheme.colors.labelDefault,
        )
    }
}

@Preview(showBackground = true, heightDp = 700)
@Composable
private fun MyRoutineScreenPreview() {
    var query by remember { mutableStateOf("") }
    var selectedCategory by remember { mutableStateOf("전체") }
    LiroutiFrontendTheme {
        MyRoutineScreen(
            query = query,
            onQueryChange = { query = it },
            categories = listOf("전체", "건강", "운동", "공부"),
            selectedCategory = selectedCategory,
            onCategorySelected = { selectedCategory = it },
            onAddCategoryClick = {},
            routines = listOf(
                RoutineChecklistItem("1", "물 마시기", false, "마감 22:00", "건강", "주중"),
                RoutineChecklistItem("2", "물 마시기", false, "마감 22:00", "건강", "월,수,금"),
                RoutineChecklistItem("3", "물 마시기", false, "마감 22:00", "건강", "금요일마다"),
            ),
            onAddRoutineClick = {},
            onBackClick = {},
            onCloseClick = {},
        )
    }
}
