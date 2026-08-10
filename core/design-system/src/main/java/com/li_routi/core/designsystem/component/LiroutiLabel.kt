package com.li_routi.core.designsystem.component

import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.interaction.collectIsPressedAsState
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.em
import androidx.compose.ui.unit.sp
import com.li_routi.core.designsystem.foundation.typography.Pretendard
import com.li_routi.core.designsystem.theme.LiroutiFrontendTheme
import com.li_routi.core.designsystem.theme.LiroutiTheme
import androidx.compose.foundation.horizontalScroll
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.defaultMinSize
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.text.BasicTextField
import androidx.compose.foundation.text.KeyboardActions
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.compose.ui.focus.FocusRequester
import androidx.compose.ui.focus.focusRequester
import androidx.compose.ui.graphics.SolidColor
import androidx.compose.ui.text.input.ImeAction

private val LabelShape = RoundedCornerShape(percent = 50)

private val LabelTextStyle = TextStyle(
    fontFamily = Pretendard,
    fontWeight = FontWeight.Medium,
    fontSize = 14.sp,
    lineHeight = 22.sp,
    letterSpacing = (-0.025f).em,
)

private val LabelOutlinedBackgroundColor = Color(0xFFF7F7F8)
private val LabelOutlinedBorderColor = Color(0xFFCDD0D5)
private val LabelInputMinWidth = 60.dp

@Composable
fun LiroutiLabel(
    text: String,
    selected: Boolean,
    onClick: () -> Unit,
    modifier: Modifier = Modifier,
    enabled: Boolean = true,
    editable: Boolean = false,
    focusRequester: FocusRequester? = null,
    onValueChange: (String) -> Unit = {},
    onImeDone: () -> Unit = {},
    /** 선택 상태 배경색. null이면 primaryNormal. 카테고리별 색 칩에 사용. */
    selectedContainerColor: Color? = null,
) {
    val interactionSource = remember { MutableInteractionSource() }
    val pressed by interactionSource.collectIsPressedAsState()

    val backgroundColor: Color
    val border: BorderStroke?
    val textColor: Color
    when {
        selected -> {
            backgroundColor = selectedContainerColor ?: LiroutiTheme.colors.primaryNormal
            border = null
            textColor = LiroutiTheme.colors.labelReverse
        }
        !enabled -> {
            backgroundColor = LiroutiTheme.colors.backgroundDefault
            border = BorderStroke(1.dp, LiroutiTheme.colors.outlinedMid)
            textColor = LiroutiTheme.colors.labelInfo
        }
        pressed -> {
            backgroundColor = LiroutiTheme.colors.backgroundAlternative
            border = BorderStroke(1.dp, LiroutiTheme.colors.outlinedMid)
            textColor = LiroutiTheme.colors.labelDefault
        }
        else -> {
            backgroundColor = LiroutiTheme.colors.backgroundDefault
            border = BorderStroke(1.dp, LiroutiTheme.colors.outlinedMid)
            textColor = LiroutiTheme.colors.labelDefault
        }
    }

    Row(
        modifier = modifier
            .height(38.dp)
            .background(backgroundColor, LabelShape)
            .then(if (border != null) Modifier.border(border, LabelShape) else Modifier)
            .then(
                if (editable) {
                    Modifier
                } else {
                    Modifier.clickable(
                        interactionSource = interactionSource,
                        indication = null,
                        enabled = enabled,
                        onClick = onClick,
                    )
                },
            )
            .padding(
                start = if (selected) 12.dp else 16.dp,
                end = 16.dp,
            ),
        horizontalArrangement = Arrangement.spacedBy(4.dp),
        verticalAlignment = Alignment.CenterVertically,
    ) {
        if (selected) {
            LiroutiCheckmarkIcon(
                modifier = Modifier.size(16.dp),
                color = LiroutiTheme.colors.labelReverse,
            )
        }
        if (editable) {
            BasicTextField(
                value = text,
                onValueChange = onValueChange,
                modifier = Modifier
                    .defaultMinSize(minWidth = LabelInputMinWidth)
                    .then(if (focusRequester != null) Modifier.focusRequester(focusRequester) else Modifier),
                singleLine = true,
                textStyle = LabelTextStyle.copy(color = textColor),
                cursorBrush = SolidColor(textColor),
                keyboardOptions = KeyboardOptions(imeAction = ImeAction.Done),
                keyboardActions = KeyboardActions(onDone = { onImeDone() }),
                decorationBox = { innerTextField ->
                    Box {
                        if (text.isEmpty()) {
                            Text(text = "제목 입력", style = LabelTextStyle, color = LiroutiTheme.colors.labelInfo)
                        }
                        innerTextField()
                    }
                },
            )
        } else {
            Text(text = text, style = LabelTextStyle, color = textColor)
        }
    }
}

/** 색상이 정해진 정적 라벨(예: 챌린지 카드의 비활성 태그)이 필요할 때 쓰는 [LiroutiLabel]의 단순 버전. */
@Composable
fun LiroutiLabelOutlined(
    text: String,
    modifier: Modifier = Modifier,
) {
    Row(
        modifier = modifier
            .height(38.dp)
            .background(LabelOutlinedBackgroundColor, LabelShape)
            .border(BorderStroke(1.dp, LabelOutlinedBorderColor), LabelShape)
            .padding(horizontal = 16.dp),
        horizontalArrangement = Arrangement.spacedBy(4.dp),
        verticalAlignment = Alignment.CenterVertically,
    ) {
        Text(text = text, style = LabelTextStyle, color = LiroutiTheme.colors.labelDefault)
    }
}

@Preview(showBackground = true)
@Composable
private fun LiroutiLabelPreview() {
    LiroutiFrontendTheme {
        Row(
            modifier = Modifier.padding(16.dp),
            horizontalArrangement = Arrangement.spacedBy(8.dp),
        ) {
            LiroutiLabel(text = "Label", selected = true, onClick = {})
            LiroutiLabel(text = "Label", selected = false, onClick = {})
            LiroutiLabel(text = "Label", selected = false, onClick = {}, enabled = false)
            LiroutiLabelOutlined(text = "Label")
        }
    }
}

/**
 * "전체" + 카테고리 목록 + "+" 추가 버튼으로 구성된 카테고리 필터 행.
 * 카테고리를 누르면 해당 카테고리가 선택 상태로 표시되고 [onCategorySelected]가 호출된다.
 * "전체"를 누르면 선택이 해제되고 [onCategorySelected]에 `null`이 전달된다.
 * "+"를 누르면 그 자리의 [LiroutiLabel]이 `editable = true`로 바뀌어 새 카테고리 제목을
 * 직접 입력할 수 있고, 입력 완료(Done)하면 목록에 일반 [LiroutiLabel]로 추가된다.
 */
@Composable
fun LiroutiCategoryLabelRow(
    modifier: Modifier = Modifier,
    onCategorySelected: (String?) -> Unit = {},
) {
    var categories by remember { mutableStateOf(listOf("건강", "운동", "공부")) }
    var selectedCategory by remember { mutableStateOf<String?>(null) }
    var isAdding by remember { mutableStateOf(false) }
    var newCategoryText by remember { mutableStateOf("") }
    val focusRequester = remember { FocusRequester() }

    Row(
        modifier = modifier.horizontalScroll(rememberScrollState()),
        horizontalArrangement = Arrangement.spacedBy(8.dp),
        verticalAlignment = Alignment.CenterVertically,
    ) {
        LiroutiLabel(
            text = "전체",
            selected = selectedCategory == null,
            onClick = {
                selectedCategory = null
                onCategorySelected(null)
            },
        )
        categories.forEach { category ->
            LiroutiLabel(
                text = category,
                selected = selectedCategory == category,
                onClick = {
                    selectedCategory = category
                    onCategorySelected(category)
                },
            )
        }
        if (isAdding) {
            LiroutiLabel(
                text = newCategoryText,
                selected = false,
                onClick = {},
                editable = true,
                focusRequester = focusRequester,
                onValueChange = { newCategoryText = it },
                onImeDone = {
                    val title = newCategoryText.trim()
                    if (title.isNotEmpty()) {
                        categories = categories + title
                    }
                    newCategoryText = ""
                    isAdding = false
                },
            )
            LaunchedEffect(Unit) { focusRequester.requestFocus() }
        } else {
            LiroutiLabel(text = "+", selected = false, onClick = { isAdding = true })
        }
    }
}

@Preview(showBackground = true)
@Composable
private fun LiroutiCategoryLabelRowPreview() {
    LiroutiFrontendTheme {
        LiroutiCategoryLabelRow(modifier = Modifier.padding(16.dp))
    }
}
