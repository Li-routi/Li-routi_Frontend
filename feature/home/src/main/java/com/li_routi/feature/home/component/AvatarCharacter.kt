package com.li_routi.feature.home.component

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
    /**
     * 겹쳐 입기의 바탕이 되는 캐릭터. `GET /api/characters`가 이미 알/성체 중 보여줄 그림을 골라
     * 내려준다 — 아직 안 받아왔거나(로딩) 실패했으면 null로 두면 로컬 기본 실루엣으로 대체된다.
     */
    characterImageUrl: String? = null,
) {
    Box(modifier = modifier, contentAlignment = Alignment.Center) {
        if (characterImageUrl.isNullOrBlank()) {
            Image(
                painter = painterResource(id = R.drawable.default_character),
                contentDescription = null,
                contentScale = ContentScale.Fit,
                modifier = Modifier.fillMaxSize(),
            )
        } else {
            AsyncImage(
                model = characterImageUrl,
                contentDescription = null,
                contentScale = ContentScale.Fit,
                modifier = Modifier.fillMaxSize(),
            )
        }
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
