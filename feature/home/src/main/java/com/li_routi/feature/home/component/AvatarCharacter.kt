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
import com.li_routi.core.domain.shop.AvatarLayer

/**
 * 아바타를 [layers] 순서 그대로 겹쳐 그림.
 *
 * 서버가 캐릭터·둥지·착장을 이미 겹칠 순서로 계산해서 내려준다 — 레이어 이름으로 깊이를
 * 다시 판단하지 않는다. 캐릭터를 하나도 못 열었으면 `CHARACTER`·둥지 레이어가 통째로 빠져서
 * 오는데, 그때만 로컬 기본 실루엣으로 대체한다.
 */
@Composable
fun AvatarCharacter(
    modifier: Modifier = Modifier,
    layers: List<AvatarLayer> = emptyList(),
) {
    Box(modifier = modifier, contentAlignment = Alignment.Center) {
        if (layers.none { it.layer == "CHARACTER" }) {
            Image(
                painter = painterResource(id = R.drawable.default_character),
                contentDescription = null,
                contentScale = ContentScale.Fit,
                modifier = Modifier.fillMaxSize(),
            )
        }
        layers.forEach { layer ->
            key(layer.layer) {
                AsyncImage(
                    model = layer.imageUrl,
                    contentDescription = null,
                    contentScale = ContentScale.Fit,
                    modifier = Modifier.fillMaxSize(),
                )
            }
        }
    }
}
