package com.example.ri_routi

import com.li_routi.core.designsystem.R
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.interaction.collectIsPressedAsState
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
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.li_routi.core.designsystem.foundation.color.Neutral96
import com.li_routi.core.designsystem.theme.LiroutiFrontendTheme
import com.li_routi.core.designsystem.theme.LiroutiTheme

data class LabelBoxStyle(
    val backgroundColor: Color,
    val labelColor: Color,
    val borderColor: Color
)

/** Figma "Button_Filled" 컴포넌트의 4가지 style 변형 (node 2405:57078). */
enum class LiroutiButtonStyle { Primary, Secondary, Tertiary, Quaternary }

// Figma 변수 "outlined/outlined". 아직 시맨틱 컬러 스킴에 승격되지 않아 로컬 상수로 둔다.
private val OutlinedStrong = Color(0xFF121416)

/**
 * "Fixed"(고정 162x44) 모양의 채워진 버튼 (Figma node 2156:28091~28114, "새 챌린지 찾아보기" 등에 쓰임).
 * Pressed 상태는 [style]로 지정하는 게 아니라 실제 터치 인터랙션에서 자동으로 파생된다.
 */
@Composable
fun LabelButton(
    text: String,
    onClick: () -> Unit,
    modifier: Modifier = Modifier,
    style: LiroutiButtonStyle = LiroutiButtonStyle.Primary,
    enabled: Boolean = true,
) {
    val interactionSource = remember { MutableInteractionSource() }
    val isPressed by interactionSource.collectIsPressedAsState()
    val pressed = enabled && isPressed

    val backgroundColor: Color
    val borderColor: Color?
    val textColor: Color
    when (style) {
        LiroutiButtonStyle.Primary -> {
            backgroundColor = when {
                !enabled -> LiroutiTheme.colors.backgroundAlternative
                pressed -> LiroutiTheme.colors.primaryActive
                else -> LiroutiTheme.colors.primaryNormal
            }
            borderColor = null
            textColor = when {
                pressed -> LiroutiTheme.colors.labelReverse
                enabled -> LiroutiTheme.colors.backgroundAlternative
                else -> LiroutiTheme.colors.labelInfo
            }
        }
        LiroutiButtonStyle.Secondary -> {
            backgroundColor = if (pressed) LiroutiTheme.colors.backgroundAlternative else LiroutiTheme.colors.backgroundDefault
            borderColor = OutlinedStrong
            textColor = if (enabled) LiroutiTheme.colors.labelDefault else LiroutiTheme.colors.labelInfo
        }
        LiroutiButtonStyle.Tertiary -> {
            backgroundColor = if (pressed) Neutral96 else LiroutiTheme.colors.backgroundAlternative
            borderColor = null
            textColor = if (enabled) LiroutiTheme.colors.labelDefault else LiroutiTheme.colors.labelInfo
        }
        LiroutiButtonStyle.Quaternary -> {
            backgroundColor = if (pressed) LiroutiTheme.colors.backgroundAlternative else LiroutiTheme.colors.backgroundDefault
            borderColor = LiroutiTheme.colors.borderDefault
            textColor = if (enabled) LiroutiTheme.colors.labelDefault else LiroutiTheme.colors.labelInfo
        }
    }

    val shape = RoundedCornerShape(6.dp)
    Box(
        modifier = modifier
            .size(width = 162.dp, height = 44.dp)
            .background(backgroundColor, shape)
            .then(if (borderColor != null) Modifier.border(1.dp, borderColor, shape) else Modifier)
            .clickable(
                interactionSource = interactionSource,
                indication = null,
                enabled = enabled,
                onClick = onClick,
            ),
        contentAlignment = Alignment.Center,
    ) {
        Text(text = text, style = LiroutiTheme.typography.body2LongMedium, color = textColor)
    }
}

@Preview(showBackground = true, name = "LabelButton (Fixed)")
@Composable
private fun LabelButtonStylesPreview() {
    LiroutiFrontendTheme {
        Column(
            verticalArrangement = Arrangement.spacedBy(10.dp),
            modifier = Modifier.padding(16.dp),
        ) {
            LiroutiButtonStyle.entries.forEach { style ->
                Row(horizontalArrangement = Arrangement.spacedBy(12.dp)) {
                    LabelButton(text = style.name, onClick = {}, style = style)
                    LabelButton(text = style.name, onClick = {}, style = style, enabled = false)
                }
            }
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

                LabelWithChevronButton(style = style)

                CompactLabelButton(style = style)

                IconCButton(style = style)

            }

        }

    }

}





@Preview(

    showBackground = true,

    widthDp = 600

)

@Composable

fun LabelButtonComparisonPreview() {

    LabelButtonComparisonColumn()

}