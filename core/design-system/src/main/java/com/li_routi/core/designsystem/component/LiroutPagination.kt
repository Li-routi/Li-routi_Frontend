package com.li_routi.core.designsystem.component

import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
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
import androidx.compose.ui.graphics.ColorFilter
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.li_routi.core.designsystem.R
import kotlin.math.min

// =============================================================================
// 1. Standard Indicators
// =============================================================================

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

@Composable
fun LiroutiOvalIndicator10x10(
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

            if (isSelected) {
                Image(
                    painter = painterResource(id = R.drawable._16x10),
                    contentDescription = null,
                    colorFilter = ColorFilter.tint(activeColor),
                    contentScale = ContentScale.Fit,
                    modifier = Modifier
                        .clickable(
                            interactionSource = remember { MutableInteractionSource() },
                            indication = null
                        ) { onPageSelected(actualIndex) }
                )
            } else {
                Box(
                    modifier = Modifier
                        .size(10.dp)
                        .clip(CircleShape)
                        .background(inactiveColor)
                        .clickable(
                            interactionSource = remember { MutableInteractionSource() },
                            indication = null
                        ) { onPageSelected(actualIndex) }
                )
            }
        }
    }
}

@Composable
fun LiroutiIndicator8x8(
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
        horizontalArrangement = Arrangement.spacedBy(6.dp),
        verticalAlignment = Alignment.CenterVertically,
        modifier = modifier
    ) {
        repeat(visibleDotCount) { offset ->
            val actualIndex = firstVisibleIndex + offset
            val isSelected = (actualIndex == currentPage)

            Box(
                modifier = Modifier
                    .size(8.dp)
                    .clip(CircleShape)
                    .background(if (isSelected) activeColor else inactiveColor)
                    .clickable(
                        interactionSource = remember { MutableInteractionSource() },
                        indication = null
                    ) { onPageSelected(actualIndex) }
            )
        }
    }
}

@Composable
fun LiroutiOvalIndicator8x8(
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
        horizontalArrangement = Arrangement.spacedBy(6.dp),
        verticalAlignment = Alignment.CenterVertically,
        modifier = modifier
    ) {
        repeat(visibleDotCount) { offset ->
            val actualIndex = firstVisibleIndex + offset
            val isSelected = (actualIndex == currentPage)

            if (isSelected) {
                Image(
                    painter = painterResource(id = R.drawable._16x8),
                    contentDescription = null,
                    colorFilter = ColorFilter.tint(activeColor),
                    contentScale = ContentScale.Fit,
                    modifier = Modifier
                        .clickable(
                            interactionSource = remember { MutableInteractionSource() },
                            indication = null
                        ) { onPageSelected(actualIndex) }
                )
            } else {
                Box(
                    modifier = Modifier
                        .size(8.dp)
                        .clip(CircleShape)
                        .background(inactiveColor)
                        .clickable(
                            interactionSource = remember { MutableInteractionSource() },
                            indication = null
                        ) { onPageSelected(actualIndex) }
                )
            }
        }
    }
}

// =============================================================================
// 2. Custom Containers
// =============================================================================

@Composable
fun LiroutiCustomContainer1(
    currentPage: Int,
    onPageSelected: (targetIndex: Int) -> Unit,
    modifier: Modifier = Modifier,
    activeColor: Color = Color(0xFF121416)
) {
    Row(
        modifier = modifier
            .width(134.dp)
            .height(50.dp)
            .padding(horizontal = 13.dp),
        verticalAlignment = Alignment.CenterVertically,
        horizontalArrangement = Arrangement.SpaceBetween
    ) {
        // 첫 번째 영역 (_16x10.xml 리소스 사용)
        Box(
            modifier = Modifier.size(width = 30.dp, height = 24.dp),
            contentAlignment = Alignment.Center
        ) {
            Image(
                painter = painterResource(id = R.drawable._16x10),
                contentDescription = null,
                colorFilter = ColorFilter.tint(activeColor),
                contentScale = ContentScale.Fit,
                modifier = Modifier
                    .clickable(
                        interactionSource = remember { MutableInteractionSource() },
                        indication = null
                    ) { onPageSelected(0) }
            )
        }

        // 두 번째 영역 (_16x8.xml 리소스 사용)
        Box(
            modifier = Modifier.size(width = 30.dp, height = 24.dp),
            contentAlignment = Alignment.Center
        ) {
            Image(
                painter = painterResource(id = R.drawable._16x8),
                contentDescription = null,
                colorFilter = ColorFilter.tint(activeColor),
                contentScale = ContentScale.Fit,
                modifier = Modifier
                    .clickable(
                        interactionSource = remember { MutableInteractionSource() },
                        indication = null
                    ) { onPageSelected(1) }
            )
        }
    }
}

@Composable
fun LiroutiCustomContainer2(
    currentPage: Int,
    onPageSelected: (targetIndex: Int) -> Unit,
    modifier: Modifier = Modifier,
    blackColor: Color = Color(0xFF121416),
    grayColor: Color = Color(0xFFC2C4C8)
) {
    Row(
        modifier = modifier
            .width(171.dp)
            .height(50.dp)
            .padding(start = 13.dp),
        verticalAlignment = Alignment.CenterVertically
    ) {
        // 1. 10dp 검은색 점
        Box(
            modifier = Modifier.size(24.dp),
            contentAlignment = Alignment.Center
        ) {
            Box(
                modifier = Modifier
                    .size(10.dp)
                    .clip(CircleShape)
                    .background(blackColor)
                    .clickable(
                        interactionSource = remember { MutableInteractionSource() },
                        indication = null
                    ) { onPageSelected(0) }
            )
        }

        Spacer(modifier = Modifier.width(17.dp))

        // 2. 10dp 회색(#C2C4C8) 점
        Box(
            modifier = Modifier.size(24.dp),
            contentAlignment = Alignment.Center
        ) {
            Box(
                modifier = Modifier
                    .size(10.dp)
                    .clip(CircleShape)
                    .background(grayColor)
                    .clickable(
                        interactionSource = remember { MutableInteractionSource() },
                        indication = null
                    ) { onPageSelected(1) }
            )
        }

        Spacer(modifier = Modifier.width(17.dp))

        // 3. 8dp 검은색 점
        Box(
            modifier = Modifier.size(22.dp),
            contentAlignment = Alignment.Center
        ) {
            Box(
                modifier = Modifier
                    .size(8.dp)
                    .clip(CircleShape)
                    .background(blackColor)
                    .clickable(
                        interactionSource = remember { MutableInteractionSource() },
                        indication = null
                    ) { onPageSelected(2) }
            )
        }

        Spacer(modifier = Modifier.width(19.dp))

        // 4. 8dp 회색(#C2C4C8) 점
        Box(
            modifier = Modifier.size(22.dp),
            contentAlignment = Alignment.Center
        ) {
            Box(
                modifier = Modifier
                    .size(8.dp)
                    .clip(CircleShape)
                    .background(grayColor)
                    .clickable(
                        interactionSource = remember { MutableInteractionSource() },
                        indication = null
                    ) { onPageSelected(3) }
            )
        }
    }
}

// =============================================================================
// Preview
// =============================================================================

@Preview(showBackground = true)
@Composable
private fun LiroutiAllIndicatorsPreview() {
    var page by remember { mutableIntStateOf(0) }

    Column(
        verticalArrangement = Arrangement.spacedBy(16.dp),
        horizontalAlignment = Alignment.CenterHorizontally,
        modifier = Modifier.padding(16.dp)
    ) {
        LiroutiInstagramIndicator(
            totalCount = 7,
            currentPage = page,
            onPageSelected = { index: Int -> page = index }
        )
        LiroutiOvalIndicator10x10(
            totalCount = 7,
            currentPage = page,
            onPageSelected = { index: Int -> page = index }
        )
        LiroutiIndicator8x8(
            totalCount = 7,
            currentPage = page,
            onPageSelected = { index: Int -> page = index }
        )
        LiroutiOvalIndicator8x8(
            totalCount = 7,
            currentPage = page,
            onPageSelected = { index: Int -> page = index }
        )

        Row(
            horizontalArrangement = Arrangement.spacedBy(16.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            LiroutiCustomContainer1(
                currentPage = page,
                onPageSelected = { index: Int -> page = index }
            )
            LiroutiCustomContainer2(
                currentPage = page,
                onPageSelected = { index: Int -> page = index }
            )
        }
    }
}