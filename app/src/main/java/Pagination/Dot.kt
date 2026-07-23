package com.example.ri_routi


import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import kotlin.math.min


@Composable
fun LiroutiInstagramIndicator(
    totalCount: Int,
    currentPage: Int,
    onPageSelected: (targetIndex: Int) -> Unit,
    modifier: Modifier = Modifier,
    maxVisibleDots: Int = 5,
    activeColor: Color = Color(0xFF121416),
    inactiveColor: Color = Color(0xFFD9D9D9)
) {
    if (totalCount <= 0) return

    val visibleDotCount = min(totalCount, maxVisibleDots)


    val firstVisibleIndex = when {
        totalCount <= maxVisibleDots -> 0
        currentPage <= maxVisibleDots / 2 -> 0
        currentPage >= totalCount - (maxVisibleDots / 2) -> totalCount - maxVisibleDots
        else -> currentPage - (maxVisibleDots / 2)
    }

    Row(
        horizontalArrangement = Arrangement.spacedBy(8.dp),
        verticalAlignment = Alignment.CenterVertically,
        modifier = modifier
    ) {
        repeat(visibleDotCount) { offset ->
            val actualIndex = firstVisibleIndex + offset
            val isSelected = (actualIndex == currentPage)

            Box(
                modifier = Modifier
                    .size(10.dp)
                    .clip(CircleShape)
                    .background(if (isSelected) activeColor else inactiveColor)
                    .clickable(
                        interactionSource = remember { MutableInteractionSource() },
                        indication = null
                    ) {
                        onPageSelected(actualIndex)
                    }
            )
        }
    }
}


@Preview(showBackground = false)
@Composable
private fun PureIndicatorPreview() {

    var currentPage by remember { mutableIntStateOf(0) }

    LiroutiInstagramIndicator(
        totalCount = 7,
        currentPage = currentPage,
        onPageSelected = { clickedIndex ->
            currentPage = clickedIndex
        }
    )
}