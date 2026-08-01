package com.li_routi.feature.home.screen

import androidx.compose.foundation.Canvas
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.navigationBarsPadding
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.statusBarsPadding
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.BasicTextField
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.StrokeCap
import androidx.compose.ui.graphics.drawscope.Stroke
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.li_routi.core.common.ui.routine.RoutineCategoryChipRow
import com.li_routi.core.common.ui.routine.RoutineChecklistItem
import com.li_routi.core.common.ui.routine.RoutineItemRow
import com.li_routi.core.designsystem.component.LiroutiBottomSheetCloseButton
import com.li_routi.core.designsystem.component.LiroutiChevronLeftIcon
import com.li_routi.core.designsystem.component.LiroutiDashedAddButton
import com.li_routi.core.designsystem.theme.LiroutiFrontendTheme
import com.li_routi.core.designsystem.theme.LiroutiTheme
import kotlin.math.cos
import kotlin.math.sin

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
                    .size(20.dp)
                    .clickable(onClick = onBackClick),
                color = LiroutiTheme.colors.labelDefault,
            )
            Text(
                text = "내 루틴",
                style = LiroutiTheme.typography.heading2SemiBold,
                color = LiroutiTheme.colors.labelDefault,
            )
            LiroutiBottomSheetCloseButton(onClick = onCloseClick)
        }

        Spacer(modifier = Modifier.height(32.dp))

        Text(
            text = "등록된 루틴",
            style = LiroutiTheme.typography.heading2SemiBold,
            color = LiroutiTheme.colors.labelDefault,
        )
        Spacer(modifier = Modifier.height(2.dp))
        Text(
            text = "루틴을 누르면 세부 설정을 변경할 수 있어요",
            // Figma Body4 13/16
            style = LiroutiTheme.typography.body3Regular.copy(lineHeight = 16.sp),
            color = LiroutiTheme.colors.labelSub,
        )

        Spacer(modifier = Modifier.height(32.dp))

        MyRoutineSearchField(value = query, onValueChange = onQueryChange)

        Spacer(modifier = Modifier.height(16.dp))

        RoutineCategoryChipRow(
            categories = categories,
            selectedCategory = selectedCategory,
            onCategorySelected = onCategorySelected,
            onAddCategoryClick = onAddCategoryClick,
        )

        Spacer(modifier = Modifier.height(16.dp))

        Column {
            routines.forEach { routine ->
                RoutineItemRow(
                    name = routine.name,
                    deadlineText = routine.deadlineText,
                    category = routine.category,
                    repeatLabel = routine.repeatLabel,
                )
            }
        }

        Spacer(modifier = Modifier.height(8.dp))

        LiroutiDashedAddButton(text = "루틴 추가", onClick = onAddRoutineClick)
    }
}

@Composable
private fun MyRoutineSearchField(
    value: String,
    onValueChange: (String) -> Unit,
    modifier: Modifier = Modifier,
) {
    Row(
        modifier = modifier
            .fillMaxWidth()
            .height(44.dp)
            .background(LiroutiTheme.colors.backgroundDefault, RoundedCornerShape(6.dp))
            .border(1.dp, LiroutiTheme.colors.borderDefault, RoundedCornerShape(6.dp))
            .padding(horizontal = 16.dp),
        horizontalArrangement = Arrangement.spacedBy(8.dp),
        verticalAlignment = Alignment.CenterVertically,
    ) {
        MyRoutineSearchIcon(
            modifier = Modifier.size(20.dp),
            color = LiroutiTheme.colors.labelInfo,
        )
        Box(
            modifier = Modifier.weight(1f),
            contentAlignment = Alignment.CenterStart,
        ) {
            if (value.isEmpty()) {
                Text(
                    text = "루틴 검색",
                    style = LiroutiTheme.typography.body2LongRegular,
                    color = LiroutiTheme.colors.labelInfo,
                )
            }
            BasicTextField(
                value = value,
                onValueChange = onValueChange,
                singleLine = true,
                maxLines = 1,
                textStyle = LiroutiTheme.typography.body2LongRegular.copy(color = LiroutiTheme.colors.labelDefault),
                modifier = Modifier.fillMaxWidth(),
            )
        }
    }
}

@Composable
private fun MyRoutineSearchIcon(
    modifier: Modifier = Modifier,
    color: Color = Color.Unspecified,
) {
    Canvas(modifier = modifier) {
        val strokeColor = if (color == Color.Unspecified) Color.Black else color
        val radius = size.minDimension * 0.32f
        val center = Offset(size.width * 0.42f, size.height * 0.42f)
        drawCircle(
            color = strokeColor,
            radius = radius,
            center = center,
            style = Stroke(width = 1.4.dp.toPx()),
        )
        val angle = Math.toRadians(45.0)
        val start = Offset(
            (center.x + radius * cos(angle)).toFloat(),
            (center.y + radius * sin(angle)).toFloat(),
        )
        drawLine(
            color = strokeColor,
            start = start,
            end = Offset(size.width * 0.9f, size.height * 0.9f),
            strokeWidth = 1.6.dp.toPx(),
            cap = StrokeCap.Round,
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
