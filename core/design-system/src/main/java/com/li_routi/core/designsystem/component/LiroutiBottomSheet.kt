package com.li_routi.core.designsystem.component

import androidx.compose.foundation.Canvas
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.ColumnScope
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.ModalBottomSheet
import androidx.compose.material3.SheetState
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.material3.rememberModalBottomSheetState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateListOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
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

private val SheetShape = RoundedCornerShape(topStart = 6.dp, topEnd = 6.dp)
private val DefaultContentPadding = PaddingValues(24.dp)

private val TitleTextStyle = TextStyle(
    fontFamily = Pretendard,
    fontWeight = FontWeight.Bold,
    fontSize = 18.sp,
    lineHeight = 28.sp,
    letterSpacing = (-0.025f).em,
)

private val ButtonTextStyle = TextStyle(
    fontFamily = Pretendard,
    fontWeight = FontWeight.Medium,
    fontSize = 14.sp,
    lineHeight = 20.sp,
)

internal val FieldLabelTextStyle = TextStyle(
    fontFamily = Pretendard,
    fontWeight = FontWeight.Medium,
    fontSize = 14.sp,
    lineHeight = 22.sp,
    letterSpacing = (-0.025f).em,
)

internal val FieldValueTextStyle = FieldLabelTextStyle.copy(fontWeight = FontWeight.Bold)

internal val InfoTextStyle = TextStyle(
    fontFamily = Pretendard,
    fontSize = 11.sp,
    lineHeight = 14.sp,
)

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun LiroutiBottomSheet(
    onDismissRequest: () -> Unit,
    modifier: Modifier = Modifier,
    sheetState: SheetState = rememberModalBottomSheetState(skipPartiallyExpanded = true),
    title: String? = null,
    contentPadding: PaddingValues = DefaultContentPadding,
    primaryButtonText: String? = null,
    onPrimaryButtonClick: () -> Unit = {},
    secondaryButtonText: String? = null,
    onSecondaryButtonClick: () -> Unit = {},
    content: @Composable ColumnScope.() -> Unit,
) {
    ModalBottomSheet(
        onDismissRequest = onDismissRequest,
        modifier = modifier,
        sheetState = sheetState,
        shape = SheetShape,
        containerColor = LiroutiTheme.colors.backgroundDefault,
        dragHandle = null,
    ) {
        LiroutiBottomSheetContent(
            title = title,
            onClose = onDismissRequest,
            contentPadding = contentPadding,
            primaryButtonText = primaryButtonText,
            onPrimaryButtonClick = onPrimaryButtonClick,
            secondaryButtonText = secondaryButtonText,
            onSecondaryButtonClick = onSecondaryButtonClick,
            content = content,
        )
    }
}

@Composable
private fun LiroutiBottomSheetContent(
    onClose: () -> Unit,
    modifier: Modifier = Modifier,
    title: String? = null,
    contentPadding: PaddingValues = DefaultContentPadding,
    primaryButtonText: String? = null,
    onPrimaryButtonClick: () -> Unit = {},
    secondaryButtonText: String? = null,
    onSecondaryButtonClick: () -> Unit = {},
    content: @Composable ColumnScope.() -> Unit,
) {
    Column(
        modifier = modifier
            .fillMaxWidth()
            .padding(contentPadding),
        verticalArrangement = Arrangement.spacedBy(20.dp),
    ) {
        if (title != null) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(20.dp),
                verticalAlignment = Alignment.CenterVertically,
            ) {
                Text(
                    text = title,
                    modifier = Modifier.weight(1f),
                    style = TitleTextStyle,
                    color = LiroutiTheme.colors.labelDefault,
                )
                LiroutiBottomSheetCloseButton(onClick = onClose)
            }
        }

        Column(content = content)

        if (primaryButtonText != null) {
            if (secondaryButtonText != null) {
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.spacedBy(6.dp),
                ) {
                    LiroutiBottomSheetButton(
                        text = secondaryButtonText,
                        onClick = onSecondaryButtonClick,
                        backgroundColor = LiroutiTheme.colors.backgroundAlternative,
                        textColor = LiroutiTheme.colors.labelDefault,
                        modifier = Modifier.weight(1f),
                    )
                    LiroutiBottomSheetButton(
                        text = primaryButtonText,
                        onClick = onPrimaryButtonClick,
                        backgroundColor = LiroutiTheme.colors.primaryNormal,
                        textColor = LiroutiTheme.colors.backgroundAlternative,
                        modifier = Modifier.weight(1f),
                    )
                }
            } else {
                LiroutiBottomSheetButton(
                    text = primaryButtonText,
                    onClick = onPrimaryButtonClick,
                    backgroundColor = LiroutiTheme.colors.primaryNormal,
                    textColor = LiroutiTheme.colors.backgroundAlternative,
                    modifier = Modifier.fillMaxWidth(),
                )
            }
        }
    }
}

@Composable
private fun LiroutiBottomSheetButton(
    text: String,
    onClick: () -> Unit,
    backgroundColor: Color,
    textColor: Color,
    modifier: Modifier = Modifier,
) {
    Row(
        modifier = modifier
            .height(44.dp)
            .clip(RoundedCornerShape(6.dp))
            .background(backgroundColor)
            .clickable(onClick = onClick),
        horizontalArrangement = Arrangement.Center,
        verticalAlignment = Alignment.CenterVertically,
    ) {
        Text(text = text, style = ButtonTextStyle, color = textColor)
    }
}

@Composable
private fun LiroutiBottomSheetCloseButton(
    onClick: () -> Unit,
    modifier: Modifier = Modifier,
    color: Color = LiroutiTheme.colors.labelDefault,
) {
    Canvas(
        modifier = modifier
            .size(24.dp)
            .clickable(onClick = onClick),
    ) {
        drawCloseIcon(color)
    }
}

private val RoutineCategoryContentPadding = PaddingValues(top = 30.dp, start = 16.dp, end = 16.dp, bottom = 32.dp)

/** Figma "Bottom Sheet / list" variant: title header, four list rows, cancel + confirm footer. */
@Preview(showBackground = true)
@Composable
private fun LiroutiBottomSheetListVariantPreview() {
    LiroutiFrontendTheme {
        val switchStates = remember { mutableStateListOf(true, true, true, true) }

        Surface(shape = SheetShape, color = LiroutiTheme.colors.backgroundDefault) {
            LiroutiBottomSheetContent(
                onClose = {},
                title = "Title",
                primaryButtonText = "Label",
                secondaryButtonText = "Label",
            ) {
                Column {
                    switchStates.forEachIndexed { index, checked ->
                        LiroutiBottomSheetListItem(
                            label = "6시간",
                            switchChecked = checked,
                            onSwitchCheckedChange = { switchStates[index] = it },
                        )
                    }
                }
            }
        }
    }
}

/** Figma "Bottom Sheet / Routine" variant: delete header, form fields, info block, confirm footer. */
@Preview(showBackground = true)
@Composable
private fun LiroutiBottomSheetRoutineVariantPreview() {
    LiroutiFrontendTheme {
        var text by remember { mutableStateOf("") }
        var selectedDays by remember { mutableStateOf(emptySet<Int>()) }

        Surface(shape = SheetShape, color = LiroutiTheme.colors.backgroundDefault) {
            LiroutiBottomSheetContent(
                onClose = {},
                contentPadding = RoutineCategoryContentPadding,
                primaryButtonText = "확인",
            ) {
                Column(verticalArrangement = Arrangement.spacedBy(20.dp)) {
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically,
                    ) {
                        LiroutiBottomSheetCloseButton(onClick = {})
                        LiroutiBottomSheetDeleteButton(onClick = {})
                    }

                    Column(verticalArrangement = Arrangement.spacedBy(20.dp)) {
                        LiroutiTextField(
                            value = text,
                            onValueChange = { text = it },
                            showLabel = false,
                            showHelper = false,
                        )
                        Column(verticalArrangement = Arrangement.spacedBy(2.dp)) {
                            LiroutiBottomSheetField(label = "마감시간", value = "오후 11:00")
                            LiroutiBottomSheetRepeatField(
                                value = "없음",
                                selectedDays = selectedDays,
                                onDayClick = { index ->
                                    selectedDays = if (index in selectedDays) {
                                        selectedDays - index
                                    } else {
                                        selectedDays + index
                                    }
                                },
                            )
                            LiroutiBottomSheetField(
                                label = "알람 시간",
                                value = "없음",
                                trailing = {
                                    LiroutiChevronRightIcon(
                                        modifier = Modifier.size(20.dp),
                                        color = LiroutiTheme.colors.labelSub,
                                    )
                                },
                            )
                        }
                    }

                    Column(verticalArrangement = Arrangement.spacedBy(4.dp)) {
                        LiroutiBottomSheetInfoRow(
                            text = "다시 알림을 켜면 설정한 시작 시각부터 선택한 간격마다 완료하거나 마감될 때까지 알림이 와요.",
                        )
                        LiroutiBottomSheetInfoRow(text = "이 설정은 방 멤버 모두에게 동일하게 적용돼요.")
                    }
                }
            }
        }
    }
}

/** Figma "Bottom Sheet / category" variant: title header, a single field, confirm footer. */
@Preview(showBackground = true)
@Composable
private fun LiroutiBottomSheetCategoryVariantPreview() {
    LiroutiFrontendTheme {
        var text by remember { mutableStateOf("") }

        Surface(shape = SheetShape, color = LiroutiTheme.colors.backgroundDefault) {
            LiroutiBottomSheetContent(
                onClose = {},
                title = "Title",
                contentPadding = RoutineCategoryContentPadding,
                primaryButtonText = "확인",
            ) {
                LiroutiTextField(
                    value = text,
                    onValueChange = { text = it },
                    showLabel = false,
                    showHelper = false,
                )
            }
        }
    }
}
