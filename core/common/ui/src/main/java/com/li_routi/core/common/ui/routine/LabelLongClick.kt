package com.li_routi.core.common.ui.routine

import androidx.compose.foundation.gestures.awaitEachGesture
import androidx.compose.foundation.gestures.awaitFirstDown
import androidx.compose.foundation.gestures.waitForUpOrCancellation
import androidx.compose.ui.Modifier
import androidx.compose.ui.input.pointer.PointerEventPass
import androidx.compose.ui.input.pointer.pointerInput
import kotlinx.coroutines.withTimeoutOrNull

/**
 * [com.li_routi.core.designsystem.component.LiroutiLabel] 등 내부 clickable이 있는
 * 컴포넌트에 롱프레스를 추가한다. Initial pass에서 먼저 감지해 DS 수정 없이 동작한다.
 *
 * 타임아웃(롱프레스 성공)과 제스처 취소(스크롤 등)를 구분한다.
 * [withTimeoutOrNull]과 [waitForUpOrCancellation]이 모두 null을 반환할 수 있어
 * 내부 결과를 감싸서 구분한다.
 */
fun Modifier.detectLabelLongClick(
    enabled: Boolean = true,
    onLongClick: () -> Unit,
): Modifier {
    if (!enabled) return this
    return pointerInput(onLongClick) {
        awaitEachGesture {
            awaitFirstDown(requireUnconsumed = false, pass = PointerEventPass.Initial)
            val longPressTimeout = viewConfiguration.longPressTimeoutMillis
            // null = 타임아웃(롱프레스), Cancelled = 스크롤 등으로 취소, Completed = 조기 손을 뗌
            val outcome = withTimeoutOrNull(longPressTimeout) {
                val up = waitForUpOrCancellation(pass = PointerEventPass.Initial)
                if (up == null) LongPressProbe.Cancelled else LongPressProbe.Completed
            }
            if (outcome == null) {
                onLongClick()
                // 롱프레스 후 클릭이 나가지 않도록 손을 뗄 때까지 소비한다.
                while (true) {
                    val event = awaitPointerEvent(PointerEventPass.Initial)
                    event.changes.forEach { it.consume() }
                    if (event.changes.none { it.pressed }) break
                }
            }
        }
    }
}

private enum class LongPressProbe {
    Completed,
    Cancelled,
}
