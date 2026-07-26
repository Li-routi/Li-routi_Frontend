package com.li_routi.core.common.ui.routine

import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.SheetState
import androidx.compose.material3.rememberModalBottomSheetState
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import com.li_routi.core.designsystem.component.LiroutiBottomSheet
import com.li_routi.core.designsystem.component.LiroutiTextField

private val ContentPadding = PaddingValues(top = 30.dp, start = 16.dp, end = 16.dp, bottom = 32.dp)
private const val CategoryNameMaxLength = 20

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun CategoryAddBottomSheet(
    name: String,
    onNameChange: (String) -> Unit,
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
        LiroutiTextField(
            value = name,
            onValueChange = { onNameChange(it.take(CategoryNameMaxLength)) },
            placeholder = placeholder,
            showLabel = false,
            showHelper = false,
        )
    }
}
