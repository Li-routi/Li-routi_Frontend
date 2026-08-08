package com.li_routi.core.designsystem.component

import android.widget.NumberPicker
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.toArgb
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.viewinterop.AndroidView
import com.li_routi.core.designsystem.foundation.color.Neutral98
import com.li_routi.core.designsystem.theme.LiroutiFrontendTheme
import com.li_routi.core.designsystem.theme.LiroutiTheme
import java.util.Locale

data class LiroutiClockTime(
    /** 0 = 오전, 1 = 오후 */
    val periodIndex: Int,
    /** 1..12 */
    val hour12: Int,
    /** 0, 10, 20, … 50 (Figma 휠은 10분 단위) */
    val minute: Int,
) {
    fun toDisplayText(): String {
        val period = if (periodIndex == 0) "오전" else "오후"
        return String.format(Locale.KOREA, "%s %d:%02d", period, hour12, minute)
    }

    /** API `HH:mm` (24h). */
    fun toApiHHmm(): String {
        val hour24 = when {
            periodIndex == 0 && hour12 == 12 -> 0
            periodIndex == 0 -> hour12
            hour12 == 12 -> 12
            else -> hour12 + 12
        }
        return String.format(Locale.US, "%02d:%02d", hour24, minute)
    }

    companion object {
        fun fromApiHHmm(
            value: String?,
            fallback: LiroutiClockTime = DefaultMorning,
        ): LiroutiClockTime {
            val parts = value?.trim()?.split(':') ?: return fallback
            if (parts.size < 2) return fallback
            val hour24 = parts[0].toIntOrNull() ?: return fallback
            val rawMinute = parts[1].toIntOrNull() ?: return fallback
            val minute = ((rawMinute / 10) * 10).coerceIn(0, 50)
            return when (hour24) {
                0 -> LiroutiClockTime(0, 12, minute)
                in 1..11 -> LiroutiClockTime(0, hour24, minute)
                12 -> LiroutiClockTime(1, 12, minute)
                in 13..23 -> LiroutiClockTime(1, hour24 - 12, minute)
                else -> fallback
            }
        }

        val DefaultMorning = LiroutiClockTime(periodIndex = 0, hour12 = 8, minute = 0)
        val DefaultEvening = LiroutiClockTime(periodIndex = 1, hour12 = 8, minute = 0)
    }
}

/**
 * Figma DS `시작시간`/`마감시간` 펼침 컴포넌트용 3열 텀블러 (오전/오후 · 시 · 분).
 * 분은 10분 단위. 선택 행 하이라이트는 Neutral98 + radius 6.
 */
@Composable
fun LiroutiTimeWheelPicker(
    value: LiroutiClockTime,
    onValueChange: (LiroutiClockTime) -> Unit,
    modifier: Modifier = Modifier,
) {
    val periods = arrayOf("오전", "오후")
    val hours = (1..12).map { it.toString() }.toTypedArray()
    val minutes = arrayOf("00", "10", "20", "30", "40", "50")
    val selectedTextColorArgb = LiroutiTheme.colors.labelDefault.toArgb()

    Box(
        modifier = modifier
            .fillMaxWidth()
            .height(90.dp),
        contentAlignment = Alignment.Center,
    ) {
        Box(
            modifier = Modifier
                .fillMaxWidth()
                .height(34.dp)
                .clip(RoundedCornerShape(6.dp))
                .background(Neutral98),
        )
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically,
        ) {
            WheelNumberPicker(
                values = periods,
                selectedIndex = value.periodIndex.coerceIn(0, 1),
                onSelectedIndexChange = { index ->
                    onValueChange(value.copy(periodIndex = index))
                },
                selectedTextColorArgb = selectedTextColorArgb,
                modifier = Modifier.weight(1f),
            )
            WheelNumberPicker(
                values = hours,
                selectedIndex = (value.hour12 - 1).coerceIn(0, 11),
                onSelectedIndexChange = { index ->
                    onValueChange(value.copy(hour12 = index + 1))
                },
                selectedTextColorArgb = selectedTextColorArgb,
                modifier = Modifier.weight(1f),
            )
            WheelNumberPicker(
                values = minutes,
                selectedIndex = (value.minute / 10).coerceIn(0, 5),
                onSelectedIndexChange = { index ->
                    onValueChange(value.copy(minute = index * 10))
                },
                selectedTextColorArgb = selectedTextColorArgb,
                modifier = Modifier.weight(1f),
            )
        }
    }
}

@Composable
private fun WheelNumberPicker(
    values: Array<String>,
    selectedIndex: Int,
    onSelectedIndexChange: (Int) -> Unit,
    selectedTextColorArgb: Int,
    modifier: Modifier = Modifier,
) {
    AndroidView(
        modifier = modifier.height(90.dp),
        factory = { context ->
            NumberPicker(context).apply {
                minValue = 0
                maxValue = values.lastIndex
                displayedValues = values
                wrapSelectorWheel = true
                descendantFocusability = NumberPicker.FOCUS_BLOCK_DESCENDANTS
                try {
                    NumberPicker::class.java.getDeclaredField("mSelectionDividerHeight").apply {
                        isAccessible = true
                        setInt(this@apply, 0)
                    }
                } catch (_: Exception) {
                    // API 차이 무시 — Compose 하이라이트 Box가 대체
                }
                setBackgroundColor(android.graphics.Color.TRANSPARENT)
                setOnValueChangedListener { _, _, newVal ->
                    onSelectedIndexChange(newVal)
                }
            }
        },
        update = { picker ->
            if (picker.displayedValues?.contentEquals(values) != true) {
                picker.displayedValues = null
                picker.minValue = 0
                picker.maxValue = values.lastIndex
                picker.displayedValues = values
            }
            if (picker.value != selectedIndex) {
                picker.value = selectedIndex
            }
            try {
                val selectorWheelPaintField =
                    NumberPicker::class.java.getDeclaredField("mSelectorWheelPaint")
                selectorWheelPaintField.isAccessible = true
                val paint = selectorWheelPaintField.get(picker) as android.graphics.Paint
                paint.color = selectedTextColorArgb
                paint.isFakeBoldText = true
                for (i in 0 until picker.childCount) {
                    val child = picker.getChildAt(i)
                    if (child is android.widget.EditText) {
                        child.setTextColor(selectedTextColorArgb)
                        child.textSize = 14f
                    }
                }
                picker.invalidate()
            } catch (_: Exception) {
                // no-op
            }
        },
    )
}

@Preview(showBackground = true)
@Composable
private fun LiroutiTimeWheelPickerPreview() {
    LiroutiFrontendTheme {
        LiroutiTimeWheelPicker(
            value = LiroutiClockTime.DefaultMorning,
            onValueChange = {},
            modifier = Modifier.padding(16.dp),
        )
    }
}
