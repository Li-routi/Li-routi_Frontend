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
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
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
    routines: List<String>,
    onAddRoutineClick: () -> Unit,
    onBackClick: () -> Unit,
    onCloseClick: () -> Unit,
    modifier: Modifier = Modifier,
) {
    Column(
        modifier = modifier
            .fillMaxSize()
            .background(LiroutiTheme.colors.backgroundDefault)
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
                text = "내 루틴",
                style = LiroutiTheme.typography.heading2SemiBold,
                color = LiroutiTheme.colors.labelDefault,
            )
            LiroutiBottomSheetCloseButton(onClick = onCloseClick)
        }

        Spacer(modifier = Modifier.height(16.dp))

        MyRoutineSearchField(value = query, onValueChange = onQueryChange)

        Spacer(modifier = Modifier.height(24.dp))

        Text(
            text = "등록된 루틴",
            style = LiroutiTheme.typography.body1SemiBold,
            color = LiroutiTheme.colors.labelDefault,
        )

        Spacer(modifier = Modifier.height(8.dp))

        Column(
            modifier = Modifier
                .fillMaxWidth()
                .background(LiroutiTheme.colors.backgroundDefault, RoundedCornerShape(6.dp))
                .border(1.dp, LiroutiTheme.colors.borderDefault, RoundedCornerShape(6.dp))
                .padding(horizontal = 16.dp),
        ) {
            routines.forEach { name ->
                Text(
                    text = name,
                    style = LiroutiTheme.typography.body1Regular,
                    color = LiroutiTheme.colors.labelDefault,
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(vertical = 12.dp),
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
            .padding(horizontal = 12.dp),
        horizontalArrangement = Arrangement.spacedBy(8.dp),
        verticalAlignment = Alignment.CenterVertically,
    ) {
        MyRoutineSearchIcon(
            modifier = Modifier.size(18.dp),
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

@Preview(showBackground = true, heightDp = 500)
@Composable
private fun MyRoutineScreenPreview() {
    var query by remember { mutableStateOf("") }
    LiroutiFrontendTheme {
        MyRoutineScreen(
            query = query,
            onQueryChange = { query = it },
            routines = listOf("비타민 먹기", "스트레칭 하기", "명상 하기"),
            onAddRoutineClick = {},
            onBackClick = {},
            onCloseClick = {},
        )
    }
}
