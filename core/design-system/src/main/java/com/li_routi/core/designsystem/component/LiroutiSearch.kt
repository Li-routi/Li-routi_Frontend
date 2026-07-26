package com.li_routi.core.designsystem.component

import androidx.compose.foundation.Canvas
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.interaction.collectIsFocusedAsState
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.BasicTextField
import androidx.compose.foundation.text.KeyboardActions
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.SolidColor
import androidx.compose.ui.platform.LocalFocusManager
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.input.ImeAction
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.li_routi.core.designsystem.R
import com.li_routi.core.designsystem.theme.LiroutiFrontendTheme
import com.li_routi.core.designsystem.theme.LiroutiTheme

/**
 * 검색창 (Figma "Search", node 2298:12656). Enabled/Pressed/Activated/Completed/Disabled
 * 5가지 상태는 [enabled], 포커스 여부, [value]의 비어있음 여부 조합으로 자동 결정된다.
 */
@Composable
fun LiroutiSearchField(
    value: String,
    onValueChange: (String) -> Unit,
    modifier: Modifier = Modifier,
    placeholder: String = "Search",
    enabled: Boolean = true,
    onSearch: (String) -> Unit = {},
) {
    val focusManager = LocalFocusManager.current
    val interactionSource = remember { MutableInteractionSource() }
    val isFocused by interactionSource.collectIsFocusedAsState()
    val showClearButton = enabled && isFocused && value.isNotEmpty()

    val borderColor = when {
        !enabled -> LiroutiTheme.colors.borderDefault
        isFocused -> LiroutiTheme.colors.labelDefault
        else -> LiroutiTheme.colors.borderDefault
    }
    val backgroundColor = if (enabled) LiroutiTheme.colors.backgroundDefault else LiroutiTheme.colors.backgroundFill
    val textColor = if (enabled) LiroutiTheme.colors.labelDefault else LiroutiTheme.colors.labelDisable
    val shape = RoundedCornerShape(6.dp)

    Row(
        modifier = modifier
            .fillMaxWidth()
            .height(44.dp)
            .background(backgroundColor, shape)
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
                painter = painterResource(id = if (enabled) R.drawable.search else R.drawable.search_gray),
                contentDescription = null,
                modifier = Modifier.size(20.dp),
            )
            Box(modifier = Modifier.weight(1f), contentAlignment = Alignment.CenterStart) {
                BasicTextField(
                    value = value,
                    onValueChange = onValueChange,
                    enabled = enabled,
                    singleLine = true,
                    textStyle = LiroutiTheme.typography.body2LongMedium.copy(color = textColor),
                    cursorBrush = SolidColor(LiroutiTheme.colors.labelDefault),
                    interactionSource = interactionSource,
                    keyboardOptions = KeyboardOptions(imeAction = ImeAction.Search),
                    keyboardActions = KeyboardActions(
                        onSearch = {
                            onSearch(value)
                            focusManager.clearFocus()
                        },
                    ),
                    modifier = Modifier.fillMaxWidth(),
                    decorationBox = { innerTextField ->
                        Box(contentAlignment = Alignment.CenterStart) {
                            if (value.isEmpty()) {
                                Text(
                                    text = placeholder,
                                    style = LiroutiTheme.typography.body2LongMedium,
                                    color = if (enabled) LiroutiTheme.colors.labelInfo else LiroutiTheme.colors.labelDisable,
                                )
                            }
                            innerTextField()
                        }
                    },
                )
            }
        }
        if (showClearButton) {
            LiroutiSearchClearButton(onClick = { onValueChange("") })
        }
    }
}

@Composable
private fun LiroutiSearchClearButton(onClick: () -> Unit, modifier: Modifier = Modifier) {
    val iconColor = LiroutiTheme.colors.backgroundAlternative
    Box(
        modifier = modifier
            .size(16.dp)
            .clip(CircleShape)
            .background(LiroutiTheme.colors.labelSub)
            .clickable(onClick = onClick),
        contentAlignment = Alignment.Center,
    ) {
        Canvas(modifier = Modifier.size(10.dp)) {
            drawCloseIcon(iconColor)
        }
    }
}

@Preview(showBackground = true, name = "전체 상태 프리뷰")
@Composable
private fun LiroutiSearchFieldAllStatesPreview() {
    LiroutiFrontendTheme {
        var typedValue by remember { mutableStateOf("Input Text") }
        var completedValue by remember { mutableStateOf("Completed") }

        Column(
            verticalArrangement = Arrangement.spacedBy(12.dp),
            modifier = Modifier.padding(16.dp),
        ) {
            // Enabled: 비어있고 포커스 없음
            LiroutiSearchField(value = "", onValueChange = {})
            // Activated: 포커스 + 값 있음 (clear 버튼 노출)
            LiroutiSearchField(value = typedValue, onValueChange = { typedValue = it })
            // Completed: 값은 있지만 포커스 없음
            LiroutiSearchField(value = completedValue, onValueChange = { completedValue = it })
            // Disabled
            LiroutiSearchField(value = "", onValueChange = {}, placeholder = "Disabled", enabled = false)
        }
    }
}
