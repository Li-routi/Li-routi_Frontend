package com.li_routi.core.designsystem.component

import android.os.Build
import android.view.ContextThemeWrapper
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
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import androidx.compose.ui.viewinterop.AndroidView
import com.li_routi.core.designsystem.R
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

/**
 * `android.widget.NumberPicker`를 감싼 단일 휠 컬럼. [LiroutiTimeWheelPicker]가 오전/오후·시·분 3열에
 * 쓰고, 마이페이지 "내 인증" 화면의 연/월 선택 휠(2열)도 이 컴포저블을 그대로 재사용한다.
 *
 * NumberPicker는 가운데 선택 값(EditText) 바깥의 휠 프리뷰 값들을 내부 Paint로 그리는데, 이 Paint의
 * 색은 NumberPicker 생성 시점에 EditText의 기본 텍스트 색(테마의 `android:textColorPrimary`)에서
 * 복사된다 — 그래서 이전엔 그 Paint 필드를 리플렉션으로 직접 바꿨는데, Android 버전에 따라 필드명이
 * 달라지거나 아예 없어져(`NoSuchFieldException`) 조용히 실패하고 프리뷰 값이 시스템 기본 옅은 회색으로
 * 남는 문제가 있었다. 리플렉션 대신 `[R.style.LiroutiNumberPicker]`로 감싼 [ContextThemeWrapper]를
 * NumberPicker 생성에 써서, 생성 시점부터 올바른 색이 적용되게 한다.
 *
 * 선택 값 위/아래의 기본 구분선(selection divider)도 같은 이유로 리플렉션(`mSelectionDividerHeight`)이
 * 조용히 실패해 안 지워지는 문제가 있었다. API 29(Q)부터는 공개 메서드 `setSelectionDividerHeight()`가
 * 있어 그걸 우선 쓰고, 그 미만 버전에서만 기존 리플렉션으로 폴백한다.
 */
@Composable
fun WheelNumberPicker(
    values: Array<String>,
    selectedIndex: Int,
    onSelectedIndexChange: (Int) -> Unit,
    selectedTextColorArgb: Int,
    modifier: Modifier = Modifier,
    height: Dp = 90.dp,
) {
    AndroidView(
        modifier = modifier.height(height),
        factory = { context ->
            val themedContext = ContextThemeWrapper(context, R.style.LiroutiNumberPicker)
            NumberPicker(themedContext).apply picker@{
                minValue = 0
                maxValue = values.lastIndex
                displayedValues = values
                wrapSelectorWheel = true
                descendantFocusability = NumberPicker.FOCUS_BLOCK_DESCENDANTS
                if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.Q) {
                    setSelectionDividerHeight(0)
                } else {
                    try {
                        val field = NumberPicker::class.java.getDeclaredField("mSelectionDividerHeight")
                        field.isAccessible = true
                        field.setInt(this@picker, 0)
                    } catch (_: Exception) {
                        // API 차이 무시 — Compose 하이라이트 Box가 대체
                    }
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
            for (i in 0 until picker.childCount) {
                val child = picker.getChildAt(i)
                if (child is android.widget.EditText) {
                    child.setTextColor(selectedTextColorArgb)
                    child.textSize = 14f
                }
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
