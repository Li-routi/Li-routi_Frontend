package com.li_routi.feature.mypage.component

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.navigationBarsPadding
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
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.li_routi.core.designsystem.component.LiroutiDim
import com.li_routi.core.designsystem.component.LiroutiDivider
import com.li_routi.core.designsystem.theme.LiroutiFrontendTheme
import com.li_routi.core.designsystem.theme.LiroutiTheme

private val SheetCornerRadius = 20.dp
private val SheetShadowColor = Color.Black.copy(alpha = 0.25f)
private val HandleColor = Color(0xFFDEDEDE)
private val ItemTextStyle = TextStyle(fontSize = 14.sp, lineHeight = 22.sp, letterSpacing = (-0.35).sp)
private val TopItemShape = RoundedCornerShape(topStart = 6.dp, topEnd = 6.dp)
private val MiddleItemShape = RoundedCornerShape(0.dp)
private val BottomItemShape = RoundedCornerShape(bottomStart = 6.dp, bottomEnd = 6.dp)
private val StandaloneItemShape = RoundedCornerShape(6.dp)

/**
 * 프로필 사진 변경 방법을 고르는 바텀시트. Figma node `6075:25632`("Bottom Sheet") 기준 —
 * "사진 촬영하기"/"앨범에서 가져오기"/"기본 이미지로 변경"은 구분선으로 나뉜 한 카드로 묶고,
 * "닫기"는 아래에 별도 버튼으로 둔다.
 */
@Composable
fun PhotoSourceBottomSheet(
    onDismiss: () -> Unit,
    onTakePhotoClick: () -> Unit,
    onPickAlbumClick: () -> Unit,
    onResetToDefaultClick: () -> Unit,
    modifier: Modifier = Modifier,
) {
    val sheetShape: Shape = RoundedCornerShape(topStart = SheetCornerRadius, topEnd = SheetCornerRadius)

    Box(modifier = modifier.fillMaxSize()) {
        LiroutiDim(modifier = Modifier.fillMaxSize(), onClick = onDismiss)
        Column(
            modifier = Modifier
                .align(Alignment.BottomCenter)
                .fillMaxWidth()
                .shadow(elevation = 8.dp, shape = sheetShape, ambientColor = SheetShadowColor, spotColor = SheetShadowColor)
                .background(LiroutiTheme.colors.backgroundDefault, sheetShape)
                .padding(horizontal = 24.dp)
                .navigationBarsPadding()
                .padding(bottom = 32.dp),
            verticalArrangement = Arrangement.spacedBy(14.dp),
        ) {
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .height(24.dp)
                    .padding(top = 8.dp),
                contentAlignment = Alignment.Center,
            ) {
                Box(
                    modifier = Modifier
                        .size(width = 44.dp, height = 4.dp)
                        .background(HandleColor, RoundedCornerShape(4.dp)),
                )
            }
            Column(verticalArrangement = Arrangement.spacedBy(8.dp)) {
                Column {
                    PhotoSourceBottomSheetItem(text = "사진 촬영하기", shape = TopItemShape, onClick = onTakePhotoClick)
                    LiroutiDivider(color = LiroutiTheme.colors.borderSub)
                    PhotoSourceBottomSheetItem(text = "앨범에서 가져오기", shape = MiddleItemShape, onClick = onPickAlbumClick)
                    LiroutiDivider(color = LiroutiTheme.colors.borderSub)
                    PhotoSourceBottomSheetItem(text = "기본 이미지로 변경", shape = BottomItemShape, onClick = onResetToDefaultClick)
                }
                PhotoSourceBottomSheetItem(text = "닫기", shape = StandaloneItemShape, onClick = onDismiss)
            }
        }
    }
}

@Composable
private fun PhotoSourceBottomSheetItem(text: String, shape: Shape, onClick: () -> Unit, modifier: Modifier = Modifier) {
    Box(
        modifier = modifier
            .fillMaxWidth()
            .background(LiroutiTheme.colors.backgroundAlternative, shape)
            .clickable(onClick = onClick)
            .padding(horizontal = 12.dp, vertical = 11.dp),
        contentAlignment = Alignment.Center,
    ) {
        Text(text = text, style = ItemTextStyle, color = LiroutiTheme.colors.labelDefault)
    }
}

@Preview(showBackground = true, heightDp = 400)
@Composable
private fun PhotoSourceBottomSheetPreview() {
    LiroutiFrontendTheme {
        PhotoSourceBottomSheet(
            onDismiss = {},
            onTakePhotoClick = {},
            onPickAlbumClick = {},
            onResetToDefaultClick = {},
        )
    }
}
