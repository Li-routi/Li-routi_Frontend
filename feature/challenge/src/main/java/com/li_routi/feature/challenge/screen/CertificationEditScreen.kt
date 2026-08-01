package com.li_routi.feature.challenge.screen

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.statusBarsPadding
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import coil.compose.AsyncImage
import androidx.compose.material3.Text
import com.li_routi.core.designsystem.component.LiroutiBottomSheetCloseButton
import com.li_routi.core.designsystem.component.LiroutiChevronLeftIcon
import com.li_routi.core.designsystem.component.LiroutiPrimaryButton
import com.li_routi.core.designsystem.component.LiroutiTextField
import com.li_routi.core.designsystem.theme.LiroutiFrontendTheme
import com.li_routi.core.designsystem.theme.LiroutiTheme
import com.li_routi.feature.challenge.vm.CertificationUiModel

// Figma node 3610:30282 ("인증 수정"). 인증 카드의 더보기 > "수정하기"를 누르면 뜨는 전체 화면.
// 수정 API가 아직 없어 "완료"는 화면/로컬 상태만 갱신한다(ChallengeDetailViewModel.onEditCertificationSubmit).
@Composable
fun CertificationEditScreen(
    certification: CertificationUiModel,
    onClose: () -> Unit,
    onSubmit: (content: String) -> Unit,
    modifier: Modifier = Modifier,
) {
    var content by remember(certification.id) { mutableStateOf(certification.content) }

    Column(
        modifier = modifier
            .fillMaxSize()
            .background(LiroutiTheme.colors.backgroundDefault),
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
                text = "인증수정",
                style = LiroutiTheme.typography.heading2SemiBold,
                color = LiroutiTheme.colors.labelDefault,
            )
            LiroutiBottomSheetCloseButton(
                modifier = Modifier
                    .align(Alignment.CenterEnd)
                    .padding(end = 16.dp),
                onClick = onClose,
                color = LiroutiTheme.colors.labelDefault,
            )
        }

        Column(
            modifier = Modifier
                .weight(1f)
                .fillMaxWidth()
                .padding(horizontal = 16.dp)
                .padding(top = 25.dp),
        ) {
            if (certification.imageUrl.isNotBlank()) {
                AsyncImage(
                    model = certification.imageUrl,
                    contentDescription = null,
                    contentScale = ContentScale.Crop,
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(120.dp)
                        .clip(RoundedCornerShape(6.dp)),
                )
            } else {
                Box(
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(120.dp)
                        .background(LiroutiTheme.colors.backgroundSecondary, RoundedCornerShape(6.dp)),
                )
            }

            LiroutiTextField(
                value = content,
                onValueChange = { content = it },
                labelText = "메모",
                showHelper = false,
                modifier = Modifier.padding(top = 24.dp),
            )
        }

        LiroutiPrimaryButton(
            text = "완료",
            onClick = { onSubmit(content) },
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 16.dp)
                .padding(bottom = 32.dp),
        )
    }
}

@Preview(showBackground = true, heightDp = 900)
@Composable
private fun CertificationEditScreenPreview() {
    LiroutiFrontendTheme {
        CertificationEditScreen(
            certification = CertificationUiModel(
                id = 1L,
                authorName = "민지",
                content = "오늘의 루틴 끝",
                imageUrl = "",
                timeLabel = "9시간 전",
                likeCount = 1,
                liked = false,
                isMine = true,
            ),
            onClose = {},
            onSubmit = {},
        )
    }
}
