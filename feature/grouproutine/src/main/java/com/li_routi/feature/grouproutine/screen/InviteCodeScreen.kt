package com.li_routi.feature.grouproutine.screen

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
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.li_routi.core.designsystem.component.LiroutiBottomSheetCloseButton
import com.li_routi.core.designsystem.component.LiroutiChevronLeftIcon
import com.li_routi.core.designsystem.component.LiroutiPrimaryButton
import com.li_routi.core.designsystem.theme.LiroutiFrontendTheme
import com.li_routi.core.designsystem.theme.LiroutiTheme

@Composable
fun InviteCodeScreen(
    code: String,
    onCodeChange: (String) -> Unit,
    errorMessage: String?,
    isConfirmEnabled: Boolean,
    onConfirmClick: () -> Unit,
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
                text = "초대코드 입력",
                style = LiroutiTheme.typography.heading2SemiBold,
                color = LiroutiTheme.colors.labelDefault,
            )
            LiroutiBottomSheetCloseButton(onClick = onCloseClick)
        }

        Spacer(modifier = Modifier.height(20.dp))

        Text(
            text = "친구에게 받은 초대코드를 입력하세요",
            style = LiroutiTheme.typography.heading2SemiBold,
            color = LiroutiTheme.colors.labelDefault,
        )
        Spacer(modifier = Modifier.height(4.dp))
        Text(
            text = "대소문자와 하이픈은 그대로 입력해주세요",
            style = LiroutiTheme.typography.body2LongRegular,
            color = LiroutiTheme.colors.labelSub,
        )

        Spacer(modifier = Modifier.height(24.dp))

        Text(
            text = "초대코드",
            style = LiroutiTheme.typography.body2LongRegular,
            color = LiroutiTheme.colors.labelDefault,
            modifier = Modifier.padding(bottom = 8.dp),
        )

        val isError = errorMessage != null
        val borderColor = if (isError) LiroutiTheme.colors.dangerBase else LiroutiTheme.colors.borderDefault

        Row(
            modifier = Modifier
                .fillMaxWidth()
                .height(44.dp)
                .background(LiroutiTheme.colors.backgroundDefault, RoundedCornerShape(6.dp))
                .border(1.dp, borderColor, RoundedCornerShape(6.dp))
                .padding(horizontal = 16.dp, vertical = 10.dp),
            horizontalArrangement = Arrangement.spacedBy(8.dp),
            verticalAlignment = Alignment.CenterVertically,
        ) {
            Box(modifier = Modifier.weight(1f), contentAlignment = Alignment.CenterStart) {
                if (code.isEmpty()) {
                    Text(
                        text = "####-####",
                        style = LiroutiTheme.typography.body2LongRegular,
                        color = LiroutiTheme.colors.labelInfo,
                    )
                }
                BasicTextField(
                    value = code,
                    onValueChange = onCodeChange,
                    singleLine = true,
                    maxLines = 1,
                    textStyle = LiroutiTheme.typography.body2LongRegular.copy(color = LiroutiTheme.colors.labelDefault),
                    modifier = Modifier.fillMaxWidth(),
                )
            }
            if (code.isNotEmpty()) {
                LiroutiBottomSheetCloseButton(
                    onClick = { onCodeChange("") },
                    modifier = Modifier.size(18.dp),
                    color = LiroutiTheme.colors.labelInfo,
                )
            }
        }

        if (errorMessage != null) {
            Spacer(modifier = Modifier.height(4.dp))
            Text(
                text = errorMessage,
                style = LiroutiTheme.typography.captionRegular,
                color = LiroutiTheme.colors.dangerText,
            )
        }

        Spacer(modifier = Modifier.weight(1f))

        LiroutiPrimaryButton(
            text = "확인",
            enabled = isConfirmEnabled,
            onClick = onConfirmClick,
            modifier = Modifier.padding(bottom = 16.dp),
        )
    }
}

@Preview(showBackground = true, heightDp = 500)
@Composable
private fun InviteCodeScreenPreview() {
    var code by remember { mutableStateOf("") }
    LiroutiFrontendTheme {
        InviteCodeScreen(
            code = code,
            onCodeChange = { code = it },
            errorMessage = null,
            isConfirmEnabled = code.isNotBlank(),
            onConfirmClick = {},
            onBackClick = {},
            onCloseClick = {},
        )
    }
}

@Preview(showBackground = true, heightDp = 500)
@Composable
private fun InviteCodeScreenErrorPreview() {
    LiroutiFrontendTheme {
        InviteCodeScreen(
            code = "alskjdlaksjd",
            onCodeChange = {},
            errorMessage = "초대코드를 정확히 입력해주세요",
            isConfirmEnabled = false,
            onConfirmClick = {},
            onBackClick = {},
            onCloseClick = {},
        )
    }
}
