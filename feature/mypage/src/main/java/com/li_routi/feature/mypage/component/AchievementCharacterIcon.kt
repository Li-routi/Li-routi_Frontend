package com.li_routi.feature.mypage.component

import androidx.compose.foundation.Image
import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import androidx.compose.ui.Modifier
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.SolidColor
import androidx.compose.ui.graphics.StrokeCap
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.graphics.vector.addPathNodes
import androidx.compose.ui.graphics.vector.rememberVectorPainter
import androidx.compose.ui.unit.dp

private const val StarPath =
    "M68.1411 43.7063C68.8975 31.1757 82.2083 23.4907 93.4384 29.1009L96.7901 30.7754C102.548 33.6517 " +
        "109.419 33.1367 114.684 29.4344L117.748 27.2792C128.017 20.0578 142.324 25.6731 144.94 " +
        "37.9511L145.721 41.6155C147.061 47.9103 151.748 52.9616 157.925 54.7692L161.521 55.8215C173.569 " +
        "59.3472 178.1 74.0344 170.131 83.7345L167.753 86.6296C163.668 91.6027 162.641 98.4165 165.079 " +
        "104.373L166.498 107.84C171.253 119.458 162.595 132.158 150.043 131.975L146.297 131.921C139.861 " +
        "131.828 133.894 135.273 130.757 140.893L128.931 144.164C122.812 155.126 107.485 156.275 99.8017 " +
        "146.347L97.5085 143.385C93.5691 138.295 87.1546 135.777 80.8051 136.829L77.1088 137.441C64.724 " +
        "139.492 54.2697 128.225 57.2404 116.028L58.127 112.388C59.6501 106.135 57.619 99.5499 52.838 " +
        "95.2412L50.0548 92.733C40.7296 84.3289 43.0203 69.1305 54.4085 63.8485L57.8073 62.272C63.6459 " +
        "59.5639 67.5276 53.8705 67.9154 47.4462L68.1411 43.7063Z"

private const val EyeLeftPath = "M84.0273 90.1951a4.66817 4.66817 0 1 0 9.33634 0a4.66817 4.66817 0 1 0 -9.33634 0"
private const val EyeRightPath = "M114.371 87.2811a4.66817 4.66817 0 1 0 9.33634 0a4.66817 4.66817 0 1 0 -9.33634 0"
private const val SmilePath = "M93.3592 102.162C96.2768 104.982 103.104 108.639 107.072 100.703"

private val EyeColor = Color(0xFF205079)
private val SmileColor = Color(0xFF0075DB)

/**
 * 업적 화면 캐릭터 일러스트. Figma node `205:18228`("Frame 2147226283") 기준.
 *
 * 방사형 그라데이션이 있는 vector drawable(`aapt:attr` gradient)이 Compose 프리뷰 렌더러에서
 * `InvocationTargetException`으로 깨져서, `ImageVector.Builder` + `Brush.radialGradient`로
 * 대신 그린다 — 실제 기기/프리뷰 모두 안전한 방식이다.
 */
@Composable
fun AchievementCharacterIcon(modifier: Modifier = Modifier) {
    val image = remember {
        ImageVector.Builder(
            name = "AchievementCharacter",
            defaultWidth = 220.dp,
            defaultHeight = 180.dp,
            viewportWidth = 220f,
            viewportHeight = 180f,
        ).apply {
            addPath(
                pathData = addPathNodes(StarPath),
                fill = Brush.radialGradient(
                    colors = listOf(Color(0xFF28AFFF), Color(0xFF9EDBFF)),
                    center = Offset(110f, 87f),
                    radius = 90f,
                ),
            )
            addPath(pathData = addPathNodes(EyeLeftPath), fill = SolidColor(EyeColor))
            addPath(pathData = addPathNodes(EyeRightPath), fill = SolidColor(EyeColor))
            addPath(
                pathData = addPathNodes(SmilePath),
                stroke = SolidColor(SmileColor),
                strokeLineWidth = 2.91761f,
                strokeLineCap = StrokeCap.Round,
            )
        }.build()
    }
    Image(painter = rememberVectorPainter(image), contentDescription = null, modifier = modifier)
}
