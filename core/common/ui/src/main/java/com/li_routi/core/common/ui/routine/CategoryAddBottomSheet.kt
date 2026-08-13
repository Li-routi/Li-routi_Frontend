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
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.ModalBottomSheet
import androidx.compose.material3.SheetState
import androidx.compose.material3.Text
import androidx.compose.material3.rememberModalBottomSheetState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.li_routi.core.designsystem.component.LiroutiBottomSheetCloseButton
import com.li_routi.core.designsystem.component.LiroutiBottomSheetDeleteButton
import com.li_routi.core.designsystem.component.LiroutiDivider
import com.li_routi.core.designsystem.component.LiroutiTextField
import com.li_routi.core.designsystem.foundation.color.CategoryBlack
import com.li_routi.core.designsystem.foundation.color.CategoryBlackBorder
import com.li_routi.core.designsystem.foundation.color.CategoryBlue
import com.li_routi.core.designsystem.foundation.color.CategoryBlueBorder
import com.li_routi.core.designsystem.foundation.color.CategoryGreen
import com.li_routi.core.designsystem.foundation.color.CategoryGreenBorder
import com.li_routi.core.designsystem.foundation.color.CategoryMagenta
import com.li_routi.core.designsystem.foundation.color.CategoryMagentaBorder
import com.li_routi.core.designsystem.foundation.color.CategoryOrange
import com.li_routi.core.designsystem.foundation.color.CategoryOrangeBorder
import com.li_routi.core.designsystem.foundation.color.CategoryRed
import com.li_routi.core.designsystem.foundation.color.CategoryRedBorder
import com.li_routi.core.designsystem.foundation.color.CategoryYellow
import com.li_routi.core.designsystem.foundation.color.CategoryYellowBorder
import com.li_routi.core.designsystem.theme.LiroutiFrontendTheme
import com.li_routi.core.designsystem.theme.LiroutiTheme

private val ContentPadding = PaddingValues(top = 30.dp, start = 16.dp, end = 16.dp, bottom = 32.dp)
private val SheetShape = RoundedCornerShape(topStart = 20.dp, topEnd = 20.dp)
private val ColorSectionShape = RoundedCornerShape(4.dp)
private const val CategoryNameMaxLength = 20

/**
 * Figma `4741:43934` 카테고리 추가/편집 시트 스와치.
 * fill + 테두리 hex는 Design Page 카테고리 시트와 동일하다.
 */
enum class CategoryColor(
    val label: String,
    val swatch: Color,
    val border: Color,
) {
    Blue("파랑", CategoryBlue, CategoryBlueBorder),
    Red("빨강", CategoryRed, CategoryRedBorder),
    Orange("주황", CategoryOrange, CategoryOrangeBorder),
    Yellow("노랑", CategoryYellow, CategoryYellowBorder),
    Green("초록", CategoryGreen, CategoryGreenBorder),
    Magenta("마젠타", CategoryMagenta, CategoryMagentaBorder),
    Black("검정", CategoryBlack, CategoryBlackBorder),
}

/** API `color` 필드용 (RED, ORANGE, …). */
fun CategoryColor.toApiColor(): String = name.uppercase()

/**
 * 서버 color 문자열 → [CategoryColor]. 알 수 없으면 null.
 *
 * 보내는 값([toApiColor], 영문 대문자)뿐 아니라 한글 라벨(카테고리 색 선택 시트에 쓰는 "파랑" 등)로
 * 와도 매칭한다 — 홈 화면 카테고리 칩을 눌러도 항상 기본(파란) 색으로만 보이는 문제가 있었는데,
 * 서버가 저장/응답 형식을 영문이 아닌 한글 라벨로 내려주는 경우 기존 매칭(영문 대문자만 비교)이
 * 전부 실패해 매번 null이 되고, 그 결과 선택 시 카테고리 고유색 대신 기본색으로만 표시됐다.
 */
fun String?.toCategoryColor(): CategoryColor? {
    val trimmed = this?.trim().orEmpty()
    if (trimmed.isEmpty()) return null
    val upperKey = trimmed.uppercase()
    return CategoryColor.entries.firstOrNull { it.toApiColor() == upperKey }
        ?: CategoryColor.entries.firstOrNull { it.label == trimmed }
}

/**
 * 카테고리 추가/편집 Bottom Sheet.
 *
 * Figma `4741:43934`: 닫기(X) + 삭제, 이름 필드, 회색 배경 색 선택 영역, 확인.
 * design-system [LiroutiBottomSheet] radius(6)와 달리 top 20을 쓴다.
 */
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
    onDeleteClick: () -> Unit = onDismissRequest,
    placeholder: String = "최대 20자",
    errorMessage: String? = null,
) {
    ModalBottomSheet(
        onDismissRequest = onDismissRequest,
        modifier = modifier,
        sheetState = sheetState,
        shape = SheetShape,
        containerColor = LiroutiTheme.colors.backgroundDefault,
        dragHandle = null,
    ) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(ContentPadding),
            verticalArrangement = Arrangement.spacedBy(20.dp),
        ) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically,
            ) {
                LiroutiBottomSheetCloseButton(onClick = onDismissRequest)
                LiroutiBottomSheetDeleteButton(onClick = onDeleteClick)
            }

            LiroutiTextField(
                value = name,
                onValueChange = { onNameChange(it.take(CategoryNameMaxLength)) },
                placeholder = placeholder,
                showLabel = false,
                showHelper = false,
            )

            if (errorMessage != null) {
                Text(
                    text = errorMessage,
                    style = LiroutiTheme.typography.caption,
                    color = LiroutiTheme.colors.dangerText,
                    modifier = Modifier.fillMaxWidth(),
                )
            }

            CategoryColorSection(
                selectedColor = selectedColor,
                onColorSelected = onColorSelected,
            )

            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .height(44.dp)
                    .clip(RoundedCornerShape(6.dp))
                    .background(LiroutiTheme.colors.primaryNormal)
                    .clickable(onClick = onConfirm),
                contentAlignment = Alignment.Center,
            ) {
                Text(
                    text = "확인",
                    style = LiroutiTheme.typography.body3Medium,
                    color = LiroutiTheme.colors.backgroundAlternative,
                )
            }
        }
    }
}

@Composable
private fun CategoryColorSection(
    selectedColor: CategoryColor?,
    onColorSelected: (CategoryColor) -> Unit,
    modifier: Modifier = Modifier,
) {
    Column(
        modifier = modifier
            .fillMaxWidth()
            .clip(ColorSectionShape)
            .background(LiroutiTheme.colors.backgroundFill)
            .padding(horizontal = 12.dp)
            .padding(top = 5.dp, bottom = 14.dp),
        verticalArrangement = Arrangement.spacedBy(5.dp),
        horizontalAlignment = Alignment.CenterHorizontally,
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(vertical = 8.dp),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically,
        ) {
            Text(
                text = "카테고리 색",
                style = LiroutiTheme.typography.body3Medium,
                color = LiroutiTheme.colors.labelSub,
            )
            Text(
                text = selectedColor?.label ?: "없음",
                style = LiroutiTheme.typography.body3Bold,
                color = LiroutiTheme.colors.labelSub,
            )
        }
        LiroutiDivider(color = LiroutiTheme.colors.borderSub)
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 12.dp, vertical = 6.dp),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically,
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

@Composable
private fun CategoryColorSwatch(
    color: CategoryColor,
    selected: Boolean,
    onClick: () -> Unit,
    modifier: Modifier = Modifier,
) {
    Box(
        modifier = modifier
            .size(30.dp)
            .clip(CircleShape)
            .background(color.swatch)
            .border(1.dp, color.border, CircleShape)
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

@OptIn(ExperimentalMaterial3Api::class)
@Preview(showBackground = true, backgroundColor = 0xFF888888)
@Composable
private fun CategoryAddBottomSheetPreview() {
    LiroutiFrontendTheme {
        var name by remember { mutableStateOf("물 마시기") }
        var selected by remember { mutableStateOf<CategoryColor?>(null) }
        CategoryAddBottomSheet(
            name = name,
            onNameChange = { name = it },
            selectedColor = selected,
            onColorSelected = { selected = it },
            onConfirm = {},
            onDismissRequest = {},
        )
    }
}
