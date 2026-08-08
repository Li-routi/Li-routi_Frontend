package com.li_routi.core.designsystem.component

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.window.Dialog
import com.li_routi.core.designsystem.theme.LiroutiFrontendTheme
import com.li_routi.core.designsystem.theme.LiroutiTheme

/**
 * 제목 + 설명 + 취소/확인 2버튼으로 된 범용 확인 다이얼로그. 확인 버튼은 기본적으로 위험 동작(삭제 등)을
 * 나타내는 붉은색이다 — 위험하지 않은 동작이면 [isConfirmDestructive]를 false로 넘긴다.
 */
@Composable
fun LiroutiConfirmDialog(
    title: String,
    message: String,
    confirmText: String,
    onConfirm: () -> Unit,
    onDismissRequest: () -> Unit,
    modifier: Modifier = Modifier,
    cancelText: String = "취소",
    isConfirmDestructive: Boolean = true,
) {
    Dialog(onDismissRequest = onDismissRequest) {
        Box(
            modifier = modifier
                .width(320.dp)
                .clip(RoundedCornerShape(16.dp))
                .background(LiroutiTheme.colors.backgroundDefault)
                .padding(24.dp),
        ) {
            Column(modifier = Modifier.fillMaxWidth()) {
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.Top,
                ) {
                    Text(
                        text = title,
                        style = LiroutiTheme.typography.heading2SemiBold,
                        color = LiroutiTheme.colors.labelDefault,
                    )
                    LiroutiBottomSheetCloseButton(onClick = onDismissRequest)
                }
                Spacer(modifier = Modifier.height(8.dp))
                Text(
                    text = message,
                    style = LiroutiTheme.typography.body2LongRegular,
                    color = LiroutiTheme.colors.labelSub,
                )
                Spacer(modifier = Modifier.height(24.dp))
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.spacedBy(8.dp),
                ) {
                    ConfirmDialogButton(
                        text = cancelText,
                        backgroundColor = LiroutiTheme.colors.backgroundAlternative,
                        textColor = LiroutiTheme.colors.labelDefault,
                        onClick = onDismissRequest,
                        modifier = Modifier.weight(1f),
                    )
                    ConfirmDialogButton(
                        text = confirmText,
                        backgroundColor = if (isConfirmDestructive) {
                            LiroutiTheme.colors.dangerBase
                        } else {
                            LiroutiTheme.colors.primaryNormal
                        },
                        textColor = LiroutiTheme.colors.backgroundAlternative,
                        onClick = onConfirm,
                        modifier = Modifier.weight(1f),
                    )
                }
            }
        }
    }
}

@Composable
private fun ConfirmDialogButton(
    text: String,
    backgroundColor: androidx.compose.ui.graphics.Color,
    textColor: androidx.compose.ui.graphics.Color,
    onClick: () -> Unit,
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
        Text(text = text, style = LiroutiTheme.typography.body1SemiBold, color = textColor)
    }
}

@Preview(showBackground = true)
@Composable
private fun LiroutiConfirmDialogPreview() {
    LiroutiFrontendTheme {
        LiroutiConfirmDialog(
            title = "삭제하기",
            message = "내 인증이 삭제됩니다.",
            confirmText = "삭제",
            onConfirm = {},
            onDismissRequest = {},
        )
    }
}
