package com.li_routi.feature.mypage.component

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.window.Dialog
import com.li_routi.core.designsystem.theme.LiroutiFrontendTheme
import com.li_routi.core.designsystem.theme.LiroutiTheme

/**
 * 제목 + 취소/확인 2버튼 모달. Figma node `205:18035`(로그아웃)/`205:18033`(회원 탈퇴) 기준 —
 * 두 모달이 메시지 본문 없이 제목 하나 + 버튼 행만 있는 동일한 구조라 하나로 공용화했다.
 */
@Composable
fun ConfirmActionDialog(
    title: String,
    confirmText: String,
    onCancel: () -> Unit,
    onConfirm: () -> Unit,
    modifier: Modifier = Modifier,
    cancelText: String = "취소",
    confirmButtonColor: Color = LiroutiTheme.colors.primaryNormal,
    confirmTextColor: Color = LiroutiTheme.colors.backgroundAlternative,
) {
    Dialog(onDismissRequest = onCancel) {
        ConfirmActionDialogContent(
            title = title,
            confirmText = confirmText,
            onCancel = onCancel,
            onConfirm = onConfirm,
            modifier = modifier,
            cancelText = cancelText,
            confirmButtonColor = confirmButtonColor,
            confirmTextColor = confirmTextColor,
        )
    }
}

/**
 * [ConfirmActionDialog]의 실제 내용물. `Dialog`는 별도 시스템 윈도우에 그려져서 Compose 프리뷰(정적/
 * Interactive Mode 둘 다)가 캡처하지 못한다 — 그래서 내용만 따로 빼서 프리뷰 가능하게 둔다.
 */
@Composable
private fun ConfirmActionDialogContent(
    title: String,
    confirmText: String,
    onCancel: () -> Unit,
    onConfirm: () -> Unit,
    modifier: Modifier = Modifier,
    cancelText: String = "취소",
    confirmButtonColor: Color = LiroutiTheme.colors.primaryNormal,
    confirmTextColor: Color = LiroutiTheme.colors.backgroundAlternative,
) {
    Surface(
        modifier = modifier.width(320.dp),
        shape = RoundedCornerShape(6.dp),
        color = LiroutiTheme.colors.backgroundDefault,
    ) {
        Column(
            modifier = Modifier.padding(24.dp),
            verticalArrangement = Arrangement.spacedBy(24.dp),
        ) {
            Text(
                text = title,
                style = LiroutiTheme.typography.heading2SemiBold,
                color = LiroutiTheme.colors.labelDefault,
            )
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(6.dp),
            ) {
                ConfirmActionDialogButton(
                    text = cancelText,
                    onClick = onCancel,
                    backgroundColor = LiroutiTheme.colors.backgroundAlternative,
                    textColor = LiroutiTheme.colors.labelDefault,
                    modifier = Modifier.weight(1f),
                )
                ConfirmActionDialogButton(
                    text = confirmText,
                    onClick = onConfirm,
                    backgroundColor = confirmButtonColor,
                    textColor = confirmTextColor,
                    modifier = Modifier.weight(1f),
                )
            }
        }
    }
}

@Composable
private fun ConfirmActionDialogButton(
    text: String,
    onClick: () -> Unit,
    backgroundColor: Color,
    textColor: Color,
    modifier: Modifier = Modifier,
) {
    Box(
        modifier = modifier
            .height(44.dp)
            .background(backgroundColor, RoundedCornerShape(6.dp))
            .clickable(onClick = onClick),
        contentAlignment = Alignment.Center,
    ) {
        Text(text = text, style = LiroutiTheme.typography.body2LongMedium, color = textColor)
    }
}

@Preview(showBackground = true, name = "1. 로그아웃")
@Composable
private fun ConfirmActionDialogLogoutPreview() {
    LiroutiFrontendTheme {
        ConfirmActionDialogContent(
            title = "로그아웃 하시겠습니까?",
            confirmText = "로그아웃",
            onCancel = {},
            onConfirm = {},
            modifier = Modifier.padding(16.dp),
        )
    }
}

@Preview(showBackground = true, name = "2. 회원 탈퇴")
@Composable
private fun ConfirmActionDialogWithdrawPreview() {
    LiroutiFrontendTheme {
        ConfirmActionDialogContent(
            title = "탈퇴 하시겠습니까?",
            confirmText = "탈퇴하기",
            onCancel = {},
            onConfirm = {},
            modifier = Modifier.padding(16.dp),
        )
    }
}
