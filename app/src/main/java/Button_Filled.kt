package com.example.ri_routi

import androidx.compose.foundation.Image

import androidx.compose.foundation.background

import androidx.compose.foundation.border

import androidx.compose.foundation.clickable

import androidx.compose.foundation.layout.Arrangement

import androidx.compose.foundation.layout.Box

import androidx.compose.foundation.layout.Column

import androidx.compose.foundation.layout.Row

import androidx.compose.foundation.layout.padding

import androidx.compose.foundation.layout.size

import androidx.compose.foundation.rememberScrollState

import androidx.compose.foundation.shape.RoundedCornerShape

import androidx.compose.foundation.verticalScroll

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



data class LabelBoxStyle(

    val backgroundColor: Color,

    val labelColor: Color,

    val borderColor: Color

)




@Composable

fun LabelButton(

    style: LabelBoxStyle,

    onClick: () -> Unit = {},

    modifier: Modifier = Modifier

) {

    val cornerRadius = 8.dp



    Box(

        modifier = modifier

            .size(width = 162.dp, height = 44.dp)

            .clip(RoundedCornerShape(cornerRadius))

            .background(style.backgroundColor)

            .border(

                width = 1.dp,

                color = style.borderColor,

                shape = RoundedCornerShape(cornerRadius)

            )

            .clickable { onClick() }

    ) {

        Box(

            contentAlignment = Alignment.CenterStart,

            modifier = Modifier

                .align(Alignment.TopStart)

                .padding(start = 20.dp, top = 11.dp)

                .size(width = 122.dp, height = 22.dp)

        ) {

            Text(

                text = "Label",

                fontSize = 14.sp,

                fontWeight = FontWeight.Medium,

                color = style.labelColor

            )

        }

    }

}




@Composable

fun LabelWithChevronButton(

    style: LabelBoxStyle,

    onClick: () -> Unit = {},

    modifier: Modifier = Modifier

) {

    val cornerRadius = 8.dp



    Box(

        modifier = modifier

            .size(width = 103.dp, height = 44.dp)

            .clip(RoundedCornerShape(cornerRadius))

            .background(style.backgroundColor)

            .border(

                width = 1.dp,

                color = style.borderColor,

                shape = RoundedCornerShape(cornerRadius)

            )

            .clickable { onClick() }

    ) {

        Row(

            modifier = Modifier

                .align(Alignment.TopStart)

                .padding(start = 22.dp, top = 12.dp)

                .size(width = 63.dp, height = 20.dp),

            horizontalArrangement = Arrangement.SpaceBetween,

            verticalAlignment = Alignment.CenterVertically

        ) {

            Box(

                modifier = Modifier.size(width = 41.dp, height = 20.dp),

                contentAlignment = Alignment.CenterStart

            ) {

                Text(

                    text = "Label",

                    fontSize = 14.sp,

                    fontWeight = FontWeight.Medium,

                    color = style.labelColor

                )

            }



            Image(

                painter = painterResource(id = R.drawable.chevron__right),

                contentDescription = "Chevron Right",

                modifier = Modifier.size(16.dp)

            )

        }

    }

}



@Composable

fun CompactLabelButton(

    style: LabelBoxStyle,

    onClick: () -> Unit = {},

    modifier: Modifier = Modifier

) {

    val cornerRadius = 8.dp



    Box(

        modifier = modifier

            .size(width = 73.dp, height = 44.dp)

            .clip(RoundedCornerShape(cornerRadius))

            .background(style.backgroundColor)

            .border(

                width = 1.dp,

                color = style.borderColor,

                shape = RoundedCornerShape(cornerRadius)

            )

            .clickable { onClick() }

    ) {

        Box(

            contentAlignment = Alignment.Center,

            modifier = Modifier

                .align(Alignment.TopStart)

                .padding(start = 20.dp, top = 11.dp)

                .size(width = 33.dp, height = 22.dp)

        ) {

            Text(

                text = "Label",

                fontSize = 14.sp,

                fontWeight = FontWeight.Medium,

                color = style.labelColor

            )

        }

    }

}





@Composable

fun IconCButton(

    style: LabelBoxStyle,

    onClick: () -> Unit = {},

    modifier: Modifier = Modifier

) {

    val cornerRadius = 8.dp



    Box(

        modifier = modifier

            .size(width = 160.dp, height = 44.dp)

            .clip(RoundedCornerShape(cornerRadius))

            .background(style.backgroundColor)

            .border(

                width = 1.dp,

                color = style.borderColor,

                shape = RoundedCornerShape(cornerRadius)

            )

            .clickable { onClick() }

    ) {

        Box(

            contentAlignment = Alignment.Center,

            modifier = Modifier

                .align(Alignment.TopStart)

                .padding(start = 70.dp, top = 12.dp)

                .size(20.dp)

        ) {

            Text(

                text = "C",

                fontSize = 14.sp,

                fontWeight = FontWeight.Bold,

                color = style.labelColor

            )

        }

    }

}




@Composable

fun LabelButtonComparisonColumn() {

    val styles = listOf(

        // [1그룹] 기존 9개 상자 (1~3행)

        LabelBoxStyle(Color(0xFF338AFF), Color(0xFFF7F7F8), Color(0xFF338AFF)),

        LabelBoxStyle(Color(0xFF296ECC), Color(0xFFF7F7F8), Color(0xFF296ECC)),

        LabelBoxStyle(Color(0xFFF7F7F8), Color(0xFF878A93), Color(0xFFF7F7F8)),



        // [2그룹] 기존 9개 상자 (4~6행)

        LabelBoxStyle(Color.Transparent, Color(0xFF121416), Color(0xFF000000)),

        LabelBoxStyle(Color(0xFFF7F7F8), Color(0xFF121416), Color(0xFF000000)),

        LabelBoxStyle(Color(0xFFDBDCDF), Color(0xFF878A93), Color(0xFF878A93)),



        // [3그룹] 기존 9개 상자 (7~9행)

        LabelBoxStyle(Color(0xFFF7F7F8), Color(0xFF171719), Color(0xFFF7F7F8)),

        LabelBoxStyle(Color(0xFFDBDCDF), Color(0xFF171719), Color(0xFFDBDCDF)),

        LabelBoxStyle(Color(0xFFF7F7F8), Color(0xFF878A93), Color(0xFFF7F7F8)),



        // [4그룹] ★ 새로 추가된 12개 상자 (10~12행)

        // 10행: 투명 바탕 + #DBDCDF 테두리 + 검은색 텍스트 (#121416 또는 Color.Black)

        LabelBoxStyle(Color.Transparent, Color(0xFF121416), Color(0xFFDBDCDF)),

        // 11행: #F7F7F8 바탕 + #DBDCDF 테두리 + 검은색 텍스트

        LabelBoxStyle(Color(0xFFF7F7F8), Color(0xFF121416), Color(0xFFDBDCDF)),

        // 12행: 투명 바탕 + #DBDCDF 테두리 + #878A93 텍스트

        LabelBoxStyle(Color.Transparent, Color(0xFF878A93), Color(0xFFDBDCDF))

    )



    Column(

        verticalArrangement = Arrangement.spacedBy(10.dp),

        horizontalAlignment = Alignment.Start,

        modifier = Modifier

            .verticalScroll(rememberScrollState())

            .padding(16.dp)

    ) {

        styles.forEach { style ->

            Row(

                horizontalArrangement = Arrangement.spacedBy(12.dp),

                verticalAlignment = Alignment.CenterVertically

            ) {

                LabelButton(style = style)

                LabelWithChevronButton(style = style)

                CompactLabelButton(style = style)

                IconCButton(style = style)

            }

        }

    }

}





@Preview(

    showBackground = false,

    widthDp = 600

)

@Composable

fun LabelButtonComparisonPreview() {

    LabelButtonComparisonColumn()

}