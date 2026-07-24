package com.example.ri_routi

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Icon
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp


@Composable
fun SearchDisabled(
    modifier: Modifier = Modifier,
    placeholderText: String = "Disabled"
) {

    Box(
        modifier = modifier
            .size(width = 360.dp, height = 44.dp)
            .clip(RoundedCornerShape(8.dp))
            .background(Color(0xFFFAFAFA))
            .border(
                width = 1.dp,
                color = Color(0xFFDBDCDF),
                shape = RoundedCornerShape(8.dp)
            )
    ) {
        Row(
            modifier = Modifier
                .fillMaxSize()
                .padding(horizontal = 10.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {

            Icon(
                painter = painterResource(id = R.drawable.search_gray),
                contentDescription = "검색 아이콘",
                modifier = Modifier.size(20.dp),
                tint = Color.Unspecified
            )

            Spacer(modifier = Modifier.width(10.dp))


            Box(
                modifier = Modifier
                    .weight(1f)
                    .height(20.dp),
                contentAlignment = Alignment.CenterStart
            ) {
                Text(
                    text = placeholderText,
                    fontSize = 14.sp,
                    fontWeight = FontWeight.Normal,
                    color = Color(0xFF9E9E9E),
                    maxLines = 1
                )
            }
        }
    }
}

@Preview(showBackground = false)
@Composable
private fun SearchDisabledPreview() {
    SearchDisabled(placeholderText = "Disabled")
}