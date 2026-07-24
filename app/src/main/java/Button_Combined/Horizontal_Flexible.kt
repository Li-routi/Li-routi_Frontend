package com.example.ri_routi.Button_Combined

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp

@Composable
fun Horizontal_FlexibleAsymmetric(
    modifier: Modifier = Modifier,
    leftLabel: String = "Label",
    rightLabel: String = "Label",
    onLeftClick: () -> Unit = {},
    onRightClick: () -> Unit = {}
) {
    val cornerRadius = 8.dp

    Row(
        horizontalArrangement = Arrangement.spacedBy(6.dp),
        verticalAlignment = Alignment.CenterVertically,
        modifier = modifier
            .size(width = 345.dp, height = 44.dp)
    ) {
        // --- [좌측 버튼] 고정 100dp ---
        Box(
            contentAlignment = Alignment.Center,
            modifier = Modifier
                .size(width = 100.dp, height = 44.dp)
                .clip(RoundedCornerShape(cornerRadius))
                .background(Color(0xFFF7F7F8))
                .clickable { onLeftClick() }
        ) {
            Text(
                text = leftLabel,
                fontSize = 14.sp,
                fontWeight = FontWeight(500),
                color = Color(0xFF121416)
            )
        }

        Box(
            contentAlignment = Alignment.Center,
            modifier = Modifier
                .weight(1f)
                .fillMaxHeight()
                .clip(RoundedCornerShape(cornerRadius))
                .background(Color(0xFF338AFF))
                .clickable { onRightClick() }
        ) {
            Text(
                text = rightLabel,
                fontSize = 14.sp,
                fontWeight = FontWeight(500),
                color = Color(0xFFF7F7F8)
            )
        }
    }
}


@Preview(showBackground = false)
@Composable
private fun Horizontal_FlexibleAsymmetricPreview() {
    Horizontal_FlexibleAsymmetric()
}