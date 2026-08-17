package com.li_routi.feature.mypage.component

import androidx.annotation.DrawableRes
import androidx.compose.foundation.Image
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.painterResource
import coil.compose.AsyncImage
import coil.compose.AsyncImagePainter

/**
 * [AchievementListItem]/[AchievementBadgeGrid]가 공유하는 이미지 소스 우선순위:
 * 서버 [imageUrl] > 로컬 [iconRes] > [fallback] composable.
 *
 * `AsyncImage`는 `imageUrl`이 비어 있지 않으면 그 자리를 그대로 차지해, 요청이 실패(404/네트워크
 * 오류 등)해도 `iconRes`/`fallback`으로 넘어가지 않고 빈 자리만 남는다. `onState`로 로드 실패를
 * 감지해 그 다음 우선순위로 직접 넘겨준다.
 */
@Composable
fun AchievementFallbackImage(
    imageUrl: String?,
    @DrawableRes iconRes: Int?,
    contentDescription: String?,
    modifier: Modifier = Modifier,
    fallback: @Composable () -> Unit,
) {
    var imageLoadFailed by remember(imageUrl) { mutableStateOf(false) }
    when {
        !imageUrl.isNullOrBlank() && !imageLoadFailed -> AsyncImage(
            model = imageUrl,
            contentDescription = contentDescription,
            modifier = modifier,
            onState = { state -> if (state is AsyncImagePainter.State.Error) imageLoadFailed = true },
        )
        iconRes != null -> Image(
            painter = painterResource(id = iconRes),
            contentDescription = contentDescription,
            modifier = modifier,
        )
        else -> fallback()
    }
}
