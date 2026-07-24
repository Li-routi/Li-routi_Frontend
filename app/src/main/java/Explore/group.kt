package com.example.ri_routi

import androidx.compose.foundation.Image
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp

@Composable
fun HeaderNavigationWithActions(
    modifier: Modifier = Modifier
) {

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


            Spacer(modifier = Modifier.width(100.5.dp))


            Text(
                text = "Navigation",
                fontSize = 18.sp,
                fontWeight = FontWeight.SemiBold,
                color = Color.Black
            )


            Spacer(modifier = Modifier.width(66.5.dp))

            // ③ chat 아이콘 (20x20)
            Image(
                painter = painterResource(id = R.drawable.chat),
                contentDescription = "Chat",
                modifier = Modifier.size(20.dp)
            )


            Spacer(modifier = Modifier.width(14.dp))


            Image(
                painter = painterResource(id = R.drawable.settings),
                contentDescription = "Setting",
                modifier = Modifier.size(20.dp)
            )
        }
    }
}


@Preview(showBackground = true)
@Composable
fun HeaderNavigationWithActionsPreview() {
    HeaderNavigationWithActions()
}


