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
            val upOrCancel = withTimeoutOrNull(longPressTimeout) {
                waitForUpOrCancellation(pass = PointerEventPass.Initial)
            }
            if (upOrCancel == null) {
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
