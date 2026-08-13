
package com.li_routi.feature.login.screen

import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.Shape
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import com.li_routi.core.designsystem.R
import com.li_routi.core.designsystem.component.LiroutiDim
import com.li_routi.core.designsystem.foundation.color.Neutral99
import com.li_routi.core.designsystem.theme.LiroutiFrontendTheme
import com.li_routi.core.designsystem.theme.LiroutiTheme

val BottomSheetHeight = 211.dp
val BottomSheetCornerRadius = 20.dp
val BottomSheetShadowElevation = 8.dp
val BottomSheetShadowColor = Color.Black.copy(alpha = 0.25f)

val BottomSheetHandleAreaHeight = 24.dp
val BottomSheetHandleAreaPaddingTop = 8.dp
val BottomSheetHandleAreaPaddingBottom = 12.dp
val BottomSheetHandleIconWidth = 50.dp
val BottomSheetHandleIconHeight = 4.dp

val BottomSheetBoxTopSpacing = 26.dp
val BottomSheetBoxGapAfterFirst = 1.dp
val BottomSheetBoxGapAfterSecond = 8.dp
val BottomSheetBoxBottomSpacing = 32.dp
val BottomSheetBoxHorizontalMargin = 34.dp
val BottomSheetBoxBackgroundColor = Neutral99

@Composable
fun BottomScreen(
    modifier: Modifier = Modifier,
    onDismiss: () -> Unit = {},
    onTakePhotoClick: () -> Unit = {},
    onPickAlbumClick: () -> Unit = {},
    sheetHeight: Dp = BottomSheetHeight,
    sheetCornerRadius: Dp = BottomSheetCornerRadius,
    sheetBackgroundColor: Color = LiroutiTheme.colors.backgroundDefault,
    sheetShadowElevation: Dp = BottomSheetShadowElevation,
    sheetShadowColor: Color = BottomSheetShadowColor,
    handleAreaHeight: Dp = BottomSheetHandleAreaHeight,
    handleAreaPaddingTop: Dp = BottomSheetHandleAreaPaddingTop,
    handleAreaPaddingBottom: Dp = BottomSheetHandleAreaPaddingBottom,
    handleIconWidth: Dp = BottomSheetHandleIconWidth,
    handleIconHeight: Dp = BottomSheetHandleIconHeight,
    boxTopSpacing: Dp = BottomSheetBoxTopSpacing,
    boxGapAfterFirst: Dp = BottomSheetBoxGapAfterFirst,
    boxGapAfterSecond: Dp = BottomSheetBoxGapAfterSecond,
    boxBottomSpacing: Dp = BottomSheetBoxBottomSpacing,
    boxHorizontalMargin: Dp = BottomSheetBoxHorizontalMargin,
    boxBackgroundColor: Color = BottomSheetBoxBackgroundColor,
) {
    val sheetShape: Shape = RoundedCornerShape(topStart = sheetCornerRadius, topEnd = sheetCornerRadius)

    // 헤더 아이콘 아래 세 상자 + 상/하 간격을 뺀 나머지를 3등분해, 세 상자의 세로 길이를 동일하게 맞춘다.
    val boxHeight = (
        sheetHeight - handleAreaHeight - boxTopSpacing -
            boxGapAfterFirst - boxGapAfterSecond - boxBottomSpacing
        ) / 3

    Box(modifier = modifier.fillMaxSize()) {
        LiroutiDim(modifier = Modifier.fillMaxSize(), onClick = onDismiss)
        Column(
            modifier = Modifier
                .align(Alignment.BottomCenter)
                .fillMaxWidth()
                .height(sheetHeight)
                .shadow(
                    elevation = sheetShadowElevation,
                    shape = sheetShape,
                    ambientColor = sheetShadowColor,
                    spotColor = sheetShadowColor,
                )
                .background(color = sheetBackgroundColor, shape = sheetShape),
        ) {
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .height(handleAreaHeight)
                    .padding(top = handleAreaPaddingTop, bottom = handleAreaPaddingBottom),
                contentAlignment = Alignment.Center,
            ) {
                Image(
                    painter = painterResource(id = R.drawable.header),
                    contentDescription = null,
                    modifier = Modifier.size(width = handleIconWidth, height = handleIconHeight),
                )
            }
            Spacer(modifier = Modifier.height(boxTopSpacing))
            BottomSheetContentBox(
                text = "사진 촬영하기",
                height = boxHeight,
                horizontalMargin = boxHorizontalMargin,
                backgroundColor = boxBackgroundColor,
                onClick = onTakePhotoClick,
            )
            Spacer(modifier = Modifier.height(boxGapAfterFirst))
            BottomSheetContentBox(
                text = "앨범에서 가져오기",
                height = boxHeight,
                horizontalMargin = boxHorizontalMargin,
                backgroundColor = boxBackgroundColor,
                onClick = onPickAlbumClick,
            )
            Spacer(modifier = Modifier.height(boxGapAfterSecond))
            BottomSheetContentBox(
                text = "닫기",
                height = boxHeight,
                horizontalMargin = boxHorizontalMargin,
                backgroundColor = boxBackgroundColor,
                onClick = onDismiss,
            )
        }
    }
}

@Composable
private fun BottomSheetContentBox(
    text: String,
    height: Dp,
    horizontalMargin: Dp,
    backgroundColor: Color,
    onClick: () -> Unit = {},
) {
    Box(
        modifier = Modifier
            .fillMaxWidth()
            .height(height)
            .padding(horizontal = horizontalMargin)
            .background(color = backgroundColor, shape = RoundedCornerShape(6.dp))
            .clickable(onClick = onClick),
        contentAlignment = Alignment.Center,
    ) {
        Text(text = text)
    }
}

@Preview(showBackground = true)
@Composable
private fun BottomScreenPreview() {
    LiroutiFrontendTheme {
        BottomScreen()
    }
}