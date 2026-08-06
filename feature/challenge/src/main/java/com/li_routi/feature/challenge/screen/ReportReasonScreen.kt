package com.li_routi.feature.challenge.screen

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.gestures.detectTapGestures
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.navigationBarsPadding
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.statusBarsPadding
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
import androidx.compose.ui.input.pointer.pointerInput
import androidx.compose.ui.platform.LocalFocusManager
import androidx.compose.ui.platform.LocalSoftwareKeyboardController
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.li_routi.core.designsystem.component.CheckBoxState
import com.li_routi.core.designsystem.component.CustomCheckBox
import com.li_routi.core.designsystem.component.LiroutiChevronLeftIcon
import com.li_routi.core.designsystem.component.LiroutiPrimaryButton
import com.li_routi.core.designsystem.theme.LiroutiFrontendTheme
import com.li_routi.core.designsystem.theme.LiroutiTheme

private val PresetReportReasons = listOf(
    "실제 루틴 수행과 무관한 사진이에요",
    "예전에 인증했던 사진을 재사용했어요",
    "타인의 사진을 도용한 것 같아요",
    "스팸 또는 광고성 콘텐츠예요",
)
private const val OtherReasonLabel = "기타"
private const val OtherReasonMaxLength = 100

// 인증 게시글 더보기 > "신고하기"를 누르면 뜨는 신고 사유 선택 화면 (Figma node 4741:57548).
// 기존에는 "신고하기"를 누르는 즉시 사유 없이 신고됐지만, 이제 이 화면에서 사유를 골라야 "완료"로 신고된다.
// 사유는 미리 정의된 4개 중 하나를 고르거나 "기타"를 골라 직접 입력한다(둘 다 없으면 사유 없이 신고).
@Composable
fun ReportReasonScreen(
    onClose: () -> Unit,
    onSubmit: (reason: String?) -> Unit,
    modifier: Modifier = Modifier,
) {
    var selectedIndex by remember { mutableStateOf(PresetReportReasons.size) }
    var otherReason by remember { mutableStateOf("") }
    val focusManager = LocalFocusManager.current
    val keyboardController = LocalSoftwareKeyboardController.current

    val reason = if (selectedIndex == PresetReportReasons.size) {
        otherReason.trim().ifBlank { null }
    } else {
        PresetReportReasons.getOrNull(selectedIndex)
    }

    Column(
        modifier = modifier
            .fillMaxSize()
            .background(LiroutiTheme.colors.backgroundDefault)
            .pointerInput(Unit) {
                detectTapGestures(onTap = {
                    keyboardController?.hide()
                    focusManager.clearFocus()
                })
            },
    ) {
        Box(
            modifier = Modifier
                .fillMaxWidth()
                .background(LiroutiTheme.colors.backgroundDefault)
                .statusBarsPadding()
                .padding(vertical = 10.dp),
            contentAlignment = Alignment.Center,
        ) {
            LiroutiChevronLeftIcon(
                modifier = Modifier
                    .align(Alignment.CenterStart)
                    .padding(start = 16.dp)
                    .size(24.dp)
                    .clickable(onClick = onClose),
                color = LiroutiTheme.colors.labelDefault,
            )
            Text(
                text = "신고하기",
                style = LiroutiTheme.typography.heading2SemiBold,
                color = LiroutiTheme.colors.labelDefault,
            )
        }

        Column(
            modifier = Modifier
                .weight(1f)
                .fillMaxWidth()
                .padding(horizontal = 16.dp),
        ) {
            Text(
                text = "신고하는 이유를 선택해 주세요",
                style = LiroutiTheme.typography.heading2SemiBold,
                color = LiroutiTheme.colors.labelDefault,
                modifier = Modifier.padding(top = 25.dp, bottom = 20.dp),
            )
            PresetReportReasons.forEachIndexed { index, label ->
                ReportReasonRow(
                    label = label,
                    selected = selectedIndex == index,
                    onClick = { selectedIndex = index },
                )
                Spacer(modifier = Modifier.height(8.dp))
            }
            ReportReasonRow(
                label = OtherReasonLabel,
                selected = selectedIndex == PresetReportReasons.size,
                onClick = { selectedIndex = PresetReportReasons.size },
            )
            if (selectedIndex == PresetReportReasons.size) {
                Spacer(modifier = Modifier.height(8.dp))
                OtherReasonField(value = otherReason, onValueChange = { otherReason = it })
            }
        }

        LiroutiPrimaryButton(
            text = "완료",
            onClick = { onSubmit(reason) },
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 16.dp)
                .navigationBarsPadding()
                .padding(bottom = 32.dp),
        )
    }
}

@Composable
private fun ReportReasonRow(
    label: String,
    selected: Boolean,
    onClick: () -> Unit,
    modifier: Modifier = Modifier,
) {
    Row(
        modifier = modifier
            .fillMaxWidth()
            .height(52.dp)
            .clickable(onClick = onClick),
        verticalAlignment = Alignment.CenterVertically,
        horizontalArrangement = Arrangement.spacedBy(8.dp),
    ) {
        CustomCheckBox(state = if (selected) CheckBoxState.B else CheckBoxState.A, isCircle = true, onClick = onClick)
        Text(text = label, style = LiroutiTheme.typography.body1Medium, color = LiroutiTheme.colors.labelDefault)
    }
}

@Composable
private fun OtherReasonField(
    value: String,
    onValueChange: (String) -> Unit,
    modifier: Modifier = Modifier,
) {
    Column(modifier = modifier.fillMaxWidth()) {
        Box(
            modifier = Modifier
                .fillMaxWidth()
                .height(86.dp)
                .background(LiroutiTheme.colors.backgroundDefault, RoundedCornerShape(6.dp))
                .border(1.dp, LiroutiTheme.colors.borderDefault, RoundedCornerShape(6.dp))
                .padding(12.dp),
        ) {
            if (value.isEmpty()) {
                Text(
                    text = "신고사유를 직접 입력해 주세요",
                    style = LiroutiTheme.typography.body2LongRegular,
                    color = LiroutiTheme.colors.labelInfo,
                )
            }
            BasicTextField(
                value = value,
                onValueChange = { if (it.length <= OtherReasonMaxLength) onValueChange(it) },
                textStyle = LiroutiTheme.typography.body2LongRegular.copy(color = LiroutiTheme.colors.labelDefault),
                modifier = Modifier.fillMaxSize(),
            )
        }
        Text(
            text = "${value.length}/$OtherReasonMaxLength",
            style = LiroutiTheme.typography.captionRegular,
            color = LiroutiTheme.colors.labelInfo,
            textAlign = TextAlign.End,
            modifier = Modifier
                .fillMaxWidth()
                .padding(top = 4.dp),
        )
    }
}

@Preview(showBackground = true, heightDp = 900)
@Composable
private fun ReportReasonScreenPreview() {
    LiroutiFrontendTheme {
        ReportReasonScreen(onClose = {}, onSubmit = {})
    }
}
