package com.li_routi.core.designsystem.component

import android.graphics.Paint
import android.os.Build
import android.view.ContextThemeWrapper
import android.widget.EditText
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
import androidx.compose.runtime.getValue
import androidx.compose.runtime.rememberUpdatedState
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.clipToBounds
import androidx.compose.ui.graphics.toArgb
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import androidx.compose.ui.viewinterop.AndroidView
import com.li_routi.core.designsystem.R
import com.li_routi.core.designsystem.foundation.color.Neutral10
import com.li_routi.core.designsystem.foundation.color.Neutral98
import com.li_routi.core.designsystem.theme.LiroutiFrontendTheme
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
 *
 * 글자색은 Compose 다크 모드와 무관하게 [Neutral10](#171719)으로 고정한다.
 * 밝은 시트 위에서 흰 글씨가 되는 문제를 막기 위함이다.
 */
@Composable
fun LiroutiTimeWheelPicker(
    value: LiroutiClockTime,
    onValueChange: (LiroutiClockTime) -> Unit,
    modifier: Modifier = Modifier,
) {
    val periods = arrayOf("오전", "오후")
    val currentValue by rememberUpdatedState(value)
    val currentOnValueChange by rememberUpdatedState(onValueChange)
    val hours = (1..12).map { it.toString() }.toTypedArray()
    val minutes = arrayOf("00", "10", "20", "30", "40", "50")
    // 다크 모드 labelDefault(밝은색)를 쓰지 않고 라이트 라벨색으로 고정
    val wheelTextColorArgb = Neutral10.toArgb()

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
                    currentOnValueChange(currentValue.copy(periodIndex = index))
                },
                selectedTextColorArgb = wheelTextColorArgb,
                modifier = Modifier.weight(1f),
            )
            WheelNumberPicker(
                values = hours,
                selectedIndex = (value.hour12 - 1).coerceIn(0, 11),
                onSelectedIndexChange = { index ->
                    currentOnValueChange(currentValue.copy(hour12 = index + 1))
                },
                selectedTextColorArgb = wheelTextColorArgb,
                modifier = Modifier.weight(1f),
            )
            WheelNumberPicker(
                values = minutes,
                selectedIndex = (value.minute / 10).coerceIn(0, 5),
                onSelectedIndexChange = { index ->
                    currentOnValueChange(currentValue.copy(minute = index * 10))
                },
                selectedTextColorArgb = wheelTextColorArgb,
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
 * 복사된다 — 그래서 [R.style.LiroutiNumberPicker]로 감싼 [ContextThemeWrapper]를 쓰고,
 * 추가로 EditText/`mSelectorWheelPaint`에도 동일 색을 강제한다.
 *
 * 선택 값 위/아래의 기본 구분선(selection divider)도 API 29(Q)부터는 공개 메서드
 * `setSelectionDividerHeight()`를 우선 쓰고, 그 미만 버전에서만 리플렉션으로 폴백한다.
 */
@Composable
fun WheelNumberPicker(
    values: Array<String>,
    selectedIndex: Int,
    onSelectedIndexChange: (Int) -> Unit,
    selectedTextColorArgb: Int,
    modifier: Modifier = Modifier,
    height: Dp = 90.dp,
    wrapSelectorWheel: Boolean = true,
) {
    val currentOnSelectedIndexChange by rememberUpdatedState(onSelectedIndexChange)

    AndroidView(
        // 네이티브 NumberPicker가 셀렉터 휠의 위/아래 값을 그릴 때 지정한 height보다 살짝 더 크게
        // 그려서(스크롤 애니메이션용), 강제로 높이를 줄여도 위아래 옆 값이 몇 px씩 삐져나와 보인다 —
        // clipToBounds로 이 높이 밖 렌더링을 확실히 잘라낸다.
        modifier = modifier.height(height).clipToBounds(),
        factory = { context ->
            val themedContext = ContextThemeWrapper(context, R.style.LiroutiNumberPicker)
            NumberPicker(themedContext).apply picker@{
                minValue = 0
                maxValue = values.lastIndex
                displayedValues = values
                this.wrapSelectorWheel = wrapSelectorWheel
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
                applyWheelTextColor(selectedTextColorArgb)
                setOnValueChangedListener { _, _, newVal ->
                    currentOnSelectedIndexChange(newVal)
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
            if (picker.wrapSelectorWheel != wrapSelectorWheel) {
                picker.wrapSelectorWheel = wrapSelectorWheel
            }
            picker.applyWheelTextColor(selectedTextColorArgb)
        },
    )
}

/** 선택 EditText + 휠 프리뷰 Paint 색을 동일하게 맞춘다. */
private fun NumberPicker.applyWheelTextColor(colorArgb: Int) {
    for (i in 0 until childCount) {
        val child = getChildAt(i)
        if (child is EditText) {
            child.setTextColor(colorArgb)
            child.textSize = 14f
        }
    }
    try {
        val field = NumberPicker::class.java.getDeclaredField("mSelectorWheelPaint")
        field.isAccessible = true
        (field.get(this) as? Paint)?.color = colorArgb
        invalidate()
    } catch (_: Exception) {
        // OEM/API 차이 — 테마 textColorPrimary로 이미 맞춰 둔 상태
    }
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
