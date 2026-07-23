package com.example.ri_routi

import androidx.compose.foundation.Image
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp

val AstaSans = FontFamily.Default

@Composable
fun HeaderStoreSetting(
    modifier: Modifier = Modifier
) {
    // 1. 바깥쪽 투명 박스: Width 360dp, Height 48dp
    Row(
        modifier = modifier
            .width(360.dp)
            .height(48.dp),
        horizontalArrangement = Arrangement.Center,
        verticalAlignment = Alignment.CenterVertically
    ) {

        Row(
            modifier = Modifier
                .width(328.dp)
                .height(28.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {

            Image(
                painter = painterResource(id = R.drawable.chevron__left),
                contentDescription = "Back",
                modifier = Modifier.size(20.dp)
            )


            Spacer(modifier = Modifier.width(10.dp))


            Text(
                text = "상점",
                fontFamily = AstaSans,
                fontSize = 18.sp,
                fontWeight = FontWeight.Bold,
                color = Color.Black
            )


            Spacer(modifier = Modifier.width(137.dp))


            Box(
                modifier = Modifier
                    .size(width = 65.dp, height = 35.dp)
                    .border(
                        width = 1.dp,
                        color = Color(0xFFA8A8A8),
                        shape = RoundedCornerShape(50)
                    ),
                contentAlignment = Alignment.CenterStart
            ) {
                Row(
                    modifier = Modifier.padding(start = 10.dp),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Image(
                        painter = painterResource(id = R.drawable.diamond_orange),
                        contentDescription = "Orange Diamond",
                        modifier = Modifier.size(16.dp)
                    )

                    Spacer(modifier = Modifier.width(4.dp))

                    Text(
                        text = "450",
                        fontFamily = AstaSans,
                        fontSize = 14.sp,
                        fontWeight = FontWeight.Medium,
                        color = Color.Black
                    )
                }
            }


            Spacer(modifier = Modifier.width(8.dp))

            Box(
                modifier = Modifier
                    .size(width = 57.dp, height = 30.dp)
                    .border(
                        width = 1.dp,
                        color = Color(0xFFA8A8A8),
                        shape = RoundedCornerShape(50)
                    ),
                contentAlignment = Alignment.CenterStart
            ) {
                Row(
                    modifier = Modifier.padding(start = 10.dp),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Image(
                        painter = painterResource(id = R.drawable.diamond_blue),
                        contentDescription = "Blue Diamond",
                        modifier = Modifier.size(16.dp)
                    )

                    Spacer(modifier = Modifier.width(4.dp))

                    Text(
                        text = "30",
                        fontFamily = AstaSans,
                        fontSize = 14.sp,
                        fontWeight = FontWeight.Medium,
                        color = Color.Black
                    )
                }
            }
        }
    }
}

@Preview(showBackground = true)
@Composable
fun HeaderStoreSettingPreview() {
    HeaderStoreSetting()
}