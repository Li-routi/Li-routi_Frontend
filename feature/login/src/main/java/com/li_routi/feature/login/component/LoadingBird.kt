package com.li_routi.feature.login.component

import androidx.compose.foundation.Image
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.tooling.preview.Preview
import com.li_routi.core.designsystem.R
import com.li_routi.core.designsystem.theme.LiroutiFrontendTheme
import kotlinx.coroutines.delay

// bird1(눈 뜸) -> bird2(반쯤 감음) -> bird3(완전히 감음) -> bird2 -> bird1 순서로,
// 프레임마다 유지 시간을 다르게 둬서 눈이 스냅하듯 감겼다 뜨는 깜빡임처럼 보이게 한다.
private val BlinkFrames = listOf(
    R.drawable.bird1 to 700L,
    R.drawable.bird2 to 100L,
    R.drawable.bird3 to 100L,
    R.drawable.bird2 to 100L,
    R.drawable.bird1 to 700L,
)

@Composable
fun LoadingBird(
    modifier: Modifier = Modifier,
) {
    var frameIndex by remember { mutableIntStateOf(0) }

    LaunchedEffect(Unit) {
        while (true) {
            delay(BlinkFrames[frameIndex].second)
            frameIndex = (frameIndex + 1) % BlinkFrames.size
        }
    }

    Image(
        painter = painterResource(id = BlinkFrames[frameIndex].first),
        contentDescription = null,
        modifier = modifier,
    )
}

@Preview(showBackground = true)
@Composable
private fun LoadingBirdPreview() {
    LiroutiFrontendTheme {
        LoadingBird()
    }
}