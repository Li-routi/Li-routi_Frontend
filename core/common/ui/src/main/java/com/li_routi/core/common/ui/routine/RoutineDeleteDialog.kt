package com.li_routi.core.common.ui.routine

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.dp
import androidx.compose.ui.window.Dialog
import com.li_routi.core.designsystem.theme.LiroutiTheme

@Composable
fun RoutineDeleteDialog(
    onDismissRequest: () -> Unit,
    onConfirmDelete: () -> Unit,
    modifier: Modifier = Modifier,
    title: String = "삭제하기",
    message: String = "작성 중이던 루틴이 삭제됩니다.",
    cancelText: String = "취소",
    confirmText: String = "삭제",
) {
    Dialog(onDismissRequest = onDismissRequest) {
        Surface(
            modifier = modifier.fillMaxWidth(),
            shape = RoundedCornerShape(12.dp),
            color = LiroutiTheme.colors.backgroundDefault,
        ) {
            Column(
                modifier = Modifier.padding(20.dp),
                verticalArrangement = Arrangement.spacedBy(20.dp),
            ) {
                Column(verticalArrangement = Arrangement.spacedBy(8.dp)) {
                    Text(
                        text = title,
                        style = LiroutiTheme.typography.heading2SemiBold,
                        color = LiroutiTheme.colors.labelDefault,
                    )
                    Text(
                        text = message,
                        style = LiroutiTheme.typography.body2LongRegular,
                        color = LiroutiTheme.colors.labelSub,
                    )
                }
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.spacedBy(8.dp),
                ) {
                    RoutineDeleteDialogButton(
                        text = cancelText,
                        onClick = onDismissRequest,
                        backgroundColor = LiroutiTheme.colors.backgroundAlternative,
                        textColor = LiroutiTheme.colors.labelDefault,
                        modifier = Modifier.weight(1f),
                    )
                    RoutineDeleteDialogButton(
                        text = confirmText,
                        onClick = onConfirmDelete,
                        backgroundColor = LiroutiTheme.colors.dangerBase,
                        textColor = LiroutiTheme.colors.backgroundAlternative,
                        modifier = Modifier.weight(1f),
                    )
                }
            }
        }
    }
}

@Composable
private fun RoutineDeleteDialogButton(
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
        Text(text = text, style = LiroutiTheme.typography.body2SemiBold, color = textColor)
    }
}
