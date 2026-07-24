package com.example.ri_routi

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.material3.Icon
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.style.TextDecoration
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp


@Composable
fun AllCustomBoxes() {

    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(Color.White)
            .padding(16.dp),
        verticalArrangement = Arrangement.spacedBy(16.dp)
    ) {

        Text("123x36 Boxes", fontSize = 14.sp, color = Color.Gray)
        Box123x36Left(textColor = Color(0xFF171719), fontSize = 20.sp, underline = false)
        Box123x36Left(textColor = Color(0xFF46474C), fontSize = 20.sp, underline = false)
        Box123x36Left(textColor = Color(0xFF878A93), fontSize = 20.sp, underline = true)

        Spacer(modifier = Modifier.height(8.dp))

        Box123x36Right(textColor = Color(0xFF171719), fontSize = 20.sp, underline = false)
        Box123x36Right(textColor = Color(0xFF46474C), fontSize = 20.sp, underline = false)
        Box123x36Right(textColor = Color(0xFF878A93), fontSize = 20.sp, underline = true)

        Spacer(modifier = Modifier.height(24.dp))


        Text("105x32 Boxes", fontSize = 14.sp, color = Color.Gray)
        Box105x32Left(textColor = Color(0xFF171719), fontSize = 16.sp, underline = false)
        Box105x32Left(textColor = Color(0xFF46474C), fontSize = 16.sp, underline = false)
        Box105x32Left(textColor = Color(0xFF878A93), fontSize = 16.sp, underline = true)

        Spacer(modifier = Modifier.height(8.dp))

        Box105x32Right(textColor = Color(0xFF171719), fontSize = 16.sp, underline = false)
        Box105x32Right(textColor = Color(0xFF46474C), fontSize = 16.sp, underline = false)
        Box105x32Right(textColor = Color(0xFF878A93), fontSize = 16.sp, underline = true)

        Spacer(modifier = Modifier.height(24.dp))


        Text("87x30 Boxes", fontSize = 14.sp, color = Color.Gray)
        Box87x30Left(textColor = Color(0xFF171719), fontSize = 16.sp, underline = false)
        Box87x30Left(textColor = Color(0xFF46474C), fontSize = 16.sp, underline = false)
        Box87x30Left(textColor = Color(0xFF878A93), fontSize = 16.sp, underline = true)

        Spacer(modifier = Modifier.height(8.dp))

        Box87x30Right(textColor = Color(0xFF171719), fontSize = 14.sp, underline = false)
        Box87x30Right(textColor = Color(0xFF46474C), fontSize = 14.sp, underline = false)
        Box87x30Right(textColor = Color(0xFF878A93), fontSize = 14.sp, underline = true)
    }
}

@Composable
fun Box123x36Left(textColor: Color, fontSize: androidx.compose.ui.unit.TextUnit, underline: Boolean) {
    Box(modifier = Modifier.size(width = 123.dp, height = 36.dp)) {
        Icon(

            painter = painterResource(id = R.drawable.chevron__left),
            contentDescription = null,
            tint = textColor,
            modifier = Modifier.size(24.dp).align(Alignment.CenterStart).offset(x = 4.dp)
        )
        Text(
            text = "Text Label",
            color = textColor,
            fontSize = fontSize,
            textDecoration = if (underline) TextDecoration.Underline else null,
            modifier = Modifier.size(width = 87.dp, height = 28.dp).align(Alignment.CenterEnd).offset(x = (-4.dp))
        )
    }
}

@Composable
fun Box123x36Right(textColor: Color, fontSize: androidx.compose.ui.unit.TextUnit, underline: Boolean) {
    Box(modifier = Modifier.size(width = 123.dp, height = 36.dp)) {
        Text(
            text = "Text Label",
            color = textColor,
            fontSize = fontSize,
            textDecoration = if (underline) TextDecoration.Underline else null,
            modifier = Modifier.size(width = 87.dp, height = 28.dp).align(Alignment.CenterStart).offset(x = 4.dp)
        )
        Icon(

            painter = painterResource(id = R.drawable.chevron__right),
            contentDescription = null,
            tint = textColor,
            modifier = Modifier.size(20.dp).align(Alignment.CenterEnd).offset(x = (-4.dp))
        )
    }
}

@Composable
fun Box105x32Left(textColor: Color, fontSize: androidx.compose.ui.unit.TextUnit, underline: Boolean) {
    Box(modifier = Modifier.size(width = 105.dp, height = 32.dp)) {
        Icon(

            painter = painterResource(id = R.drawable.chevron__left),
            contentDescription = null,
            tint = textColor,
            modifier = Modifier.size(16.dp).align(Alignment.CenterStart).offset(x = 4.dp)
        )
        Text(
            text = "Text Label",
            color = textColor,
            fontSize = fontSize,
            textDecoration = if (underline) TextDecoration.Underline else null,
            modifier = Modifier.size(width = 73.dp, height = 24.dp).align(Alignment.CenterEnd).offset(x = (-4.dp))
        )
    }
}

@Composable
fun Box105x32Right(textColor: Color, fontSize: androidx.compose.ui.unit.TextUnit, underline: Boolean) {
    Box(modifier = Modifier.size(width = 105.dp, height = 32.dp)) {
        Text(
            text = "Text Label",
            color = textColor,
            fontSize = fontSize,
            textDecoration = if (underline) TextDecoration.Underline else null,
            modifier = Modifier.size(width = 73.dp, height = 24.dp).align(Alignment.CenterStart).offset(x = 4.dp)
        )
        Icon(

            painter = painterResource(id = R.drawable.chevron__right),
            contentDescription = null,
            tint = textColor,
            modifier = Modifier.size(24.dp).align(Alignment.CenterEnd).offset(x = (-4.dp))
        )
    }
}

@Composable
fun Box87x30Left(textColor: Color, fontSize: androidx.compose.ui.unit.TextUnit, underline: Boolean) {
    Box(modifier = Modifier.size(width = 87.dp, height = 30.dp)) {
        Icon(

            painter = painterResource(id = R.drawable.chevron__left),
            contentDescription = null,
            tint = textColor,
            modifier = Modifier.size(24.dp).align(Alignment.CenterStart).offset(x = 4.dp)
        )
        Text(
            text = "Text Label",
            color = textColor,
            fontSize = fontSize,
            textDecoration = if (underline) TextDecoration.Underline else null,
            modifier = Modifier.size(width = 61.dp, height = 22.dp).align(Alignment.CenterEnd).offset(x = (-4.dp))
        )
    }
}

@Composable
fun Box87x30Right(textColor: Color, fontSize: androidx.compose.ui.unit.TextUnit, underline: Boolean) {
    Box(modifier = Modifier.size(width = 87.dp, height = 30.dp)) {
        Text(
            text = "Text Label",
            color = textColor,
            fontSize = fontSize,
            textDecoration = if (underline) TextDecoration.Underline else null,
            modifier = Modifier.size(width = 61.dp, height = 22.dp).align(Alignment.CenterStart).offset(x = 4.dp)
        )
        Icon(

            painter = painterResource(id = R.drawable.chevron__right),
            contentDescription = null,
            tint = textColor,
            modifier = Modifier.size(24.dp).align(Alignment.CenterEnd).offset(x = (-4.dp))
        )
    }
}



@Preview(showBackground = true, widthDp = 400, heightDp = 1200)
@Composable
fun PreviewAllBoxes() {
    AllCustomBoxes()
}