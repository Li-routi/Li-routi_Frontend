package com.li_routi.core.common.ui.routine

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.SheetState
import androidx.compose.material3.rememberModalBottomSheetState
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.dp
import com.li_routi.core.designsystem.component.LiroutiBottomSheet
import com.li_routi.core.designsystem.component.LiroutiBottomSheetField
import com.li_routi.core.designsystem.component.LiroutiDivider
import com.li_routi.core.designsystem.component.LiroutiTextField
import com.li_routi.core.designsystem.theme.LiroutiTheme

private val ContentPadding = PaddingValues(top = 30.dp, start = 16.dp, end = 16.dp, bottom = 32.dp)
private const val CategoryNameMaxLength = 20

enum class CategoryColor(val label: String, val swatch: Color) {
    Red("빨강", Color(0xFFFF5A5F)),
    Orange("주황", Color(0xFFFF9F43)),
    Yellow("노랑", Color(0xFFFFD93D)),
    Green("초록", Color(0xFF3DD98C)),
    Blue("파랑", Color(0xFF3B9AFF)),
    Magenta("마젠타", Color(0xFFE066FF)),
    Black("검정", Color(0xFF1A1A1A)),
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun CategoryAddBottomSheet(
    name: String,
    onNameChange: (String) -> Unit,
    selectedColor: CategoryColor?,
    onColorSelected: (CategoryColor) -> Unit,
    onConfirm: () -> Unit,
    onDismissRequest: () -> Unit,
    modifier: Modifier = Modifier,
    sheetState: SheetState = rememberModalBottomSheetState(skipPartiallyExpanded = true),
    title: String = "카테고리",
    placeholder: String = "최대 20자",
) {
    LiroutiBottomSheet(
        onDismissRequest = onDismissRequest,
        modifier = modifier,
        sheetState = sheetState,
        title = title,
        contentPadding = ContentPadding,
        primaryButtonText = "확인",
        onPrimaryButtonClick = onConfirm,
    ) {
        Column(verticalArrangement = Arrangement.spacedBy(20.dp)) {
            LiroutiTextField(
                value = name,
                onValueChange = { onNameChange(it.take(CategoryNameMaxLength)) },
                placeholder = placeholder,
                showLabel = false,
                showHelper = false,
            )
            Column(verticalArrangement = Arrangement.spacedBy(12.dp)) {
                Column(verticalArrangement = Arrangement.spacedBy(2.dp)) {
                    LiroutiBottomSheetField(label = "카테고리 색", value = selectedColor?.label ?: "없음")
                    LiroutiDivider(color = LiroutiTheme.colors.borderSub)
                }
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                ) {
                    CategoryColor.entries.forEach { color ->
                        CategoryColorSwatch(
                            color = color,
                            selected = color == selectedColor,
                            onClick = { onColorSelected(color) },
                        )
                    }
                }
            }
        }
    }
}

@Composable
private fun CategoryColorSwatch(
    color: CategoryColor,
    selected: Boolean,
    onClick: () -> Unit,
    modifier: Modifier = Modifier,
) {
    Box(
        modifier = modifier
            .size(32.dp)
            .clip(CircleShape)
            .background(color.swatch)
            .then(
                if (selected) {
                    Modifier.border(2.dp, LiroutiTheme.colors.labelDefault, CircleShape)
                } else {
                    Modifier
                },
            )
            .clickable(onClick = onClick),
    )
}
