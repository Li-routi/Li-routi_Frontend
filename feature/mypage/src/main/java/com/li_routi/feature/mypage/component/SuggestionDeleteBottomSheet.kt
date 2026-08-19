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
import com.li_routi.core.designsystem.theme.LiroutiFrontendTheme
import com.li_routi.core.designsystem.theme.LiroutiTheme

private val SheetCornerRadius = 20.dp
private val SheetShadowColor = Color.Black.copy(alpha = 0.25f)
private val HandleColor = Color(0xFFDEDEDE)
private val ItemTextStyle = TextStyle(fontSize = 14.sp, lineHeight = 22.sp, letterSpacing = (-0.35).sp)
private val ItemShape = RoundedCornerShape(6.dp)

/**
 * 건의 삭제 확인 바텀시트. [PhotoSourceBottomSheet]와 같은 카드/닫기 분리 구조를 따른다.
 */
@Composable
fun SuggestionDeleteBottomSheet(
    onDismiss: () -> Unit,
    onDeleteClick: () -> Unit,
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
                SuggestionDeleteBottomSheetItem(
                    text = "건의 삭제",
                    textColor = LiroutiTheme.colors.dangerText,
                    onClick = onDeleteClick,
                )
                SuggestionDeleteBottomSheetItem(
                    text = "닫기",
                    textColor = LiroutiTheme.colors.labelDefault,
                    onClick = onDismiss,
                )
            }
        }
    }
}

@Composable
private fun SuggestionDeleteBottomSheetItem(
    text: String,
    textColor: Color,
    onClick: () -> Unit,
    modifier: Modifier = Modifier,
) {
    Box(
        modifier = modifier
            .fillMaxWidth()
            .background(LiroutiTheme.colors.backgroundAlternative, ItemShape)
            .clickable(onClick = onClick)
            .padding(horizontal = 12.dp, vertical = 11.dp),
        contentAlignment = Alignment.Center,
    ) {
        Text(text = text, style = ItemTextStyle, color = textColor)
    }
}

@Preview(showBackground = true, heightDp = 400)
@Composable
private fun SuggestionDeleteBottomSheetPreview() {
    LiroutiFrontendTheme {
        SuggestionDeleteBottomSheet(onDismiss = {}, onDeleteClick = {})
    }
}
