package com.li_routi.feature.home.component

import androidx.annotation.DrawableRes
import androidx.compose.foundation.Image
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.runtime.Composable
import androidx.compose.runtime.key
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.res.painterResource
import coil.compose.AsyncImage
import com.li_routi.core.designsystem.R

/** 옷 위에 모자, 그 위에 손에 든 것 순으로 겹침 */
private val EquipSlotOrder = listOf("BODY", "HEAD", "HAND")

/** 자리별 착용 아이템을 겹칠 순서대로 폄. 안 입은 자리는 빠짐 */
fun equippedImageUrlsOf(equipped: Map<String, String?>): List<String> =
    EquipSlotOrder.mapNotNull { slot ->
        equipped.entries.firstOrNull { it.key.equals(slot, ignoreCase = true) }
            ?.value
            ?.takeIf { it.isNotBlank() }
    }

/**
 * 캐릭터 위에 착용 아이템을 겹쳐 그림.
 *
 * 아이템 이미지가 캐릭터와 같은 캔버스로 그려져 있어서 크기만 맞추면 위치가 따로 필요 없음
 */
@Composable
fun AvatarCharacter(
    modifier: Modifier = Modifier,
    equippedImageUrls: List<String> = emptyList(),
    /** 겹쳐 입기의 바탕이 되는 캐릭터. 캐릭터 탭에서 고른 것이 여기로 들어옴 */
    @DrawableRes characterRes: Int = R.drawable.default_character,
) {
    Box(modifier = modifier, contentAlignment = Alignment.Center) {
        Image(
            painter = painterResource(id = characterRes),
            contentDescription = null,
            contentScale = ContentScale.Fit,
            modifier = Modifier.fillMaxSize(),
        )
        equippedImageUrls.forEach { url ->
            key(url) {
                AsyncImage(
                    model = url,
                    contentDescription = null,
                    contentScale = ContentScale.Fit,
                    modifier = Modifier.fillMaxSize(),
                )
            }
        }
    }
}
