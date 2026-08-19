package com.li_routi.core.domain.shop

/** 서버 `AvatarLayer.layer` enum 이름. 깊이 판단용이 아니라 슬롯 교체용 */
object AvatarLayerName {
    const val CHARACTER = "CHARACTER"
    const val BODY = "BODY"
    const val HEAD = "HEAD"
    const val HAND = "HAND"
    val CLOTHING = setOf(BODY, HEAD, HAND)
}

fun List<AvatarLayer>.hasCharacterLayer(): Boolean =
    any { it.layer == AvatarLayerName.CHARACTER }

/** CHARACTER URL만 바꿈. 없으면 순서를 추측해 끼우지 않음 */
fun List<AvatarLayer>.withCharacterImageUrl(imageUrl: String?): List<AvatarLayer> {
    val url = imageUrl?.takeIf { it.isNotBlank() } ?: return this
    return map { layer ->
        if (layer.layer == AvatarLayerName.CHARACTER) layer.copy(imageUrl = url) else layer
    }
}

/**
 * 서버가 준 [savedLayers] 순서는 유지하고, 캐릭터/의상만 미리보기 값으로 바꿔 끼움.
 * 벗은 의상 자리는 빼고, 저장된 목록에 없던 미리보기만 맨 뒤에 붙임.
 */
fun previewAvatarLayers(
    savedLayers: List<AvatarLayer>,
    previewCharacterImageUrl: String?,
    equippedImageUrls: Map<String, String?>,
): List<AvatarLayer> {
    val previewByLayer = buildMap {
        previewCharacterImageUrl?.takeIf { it.isNotBlank() }?.let { put(AvatarLayerName.CHARACTER, it) }
        equippedImageUrls.forEach { (slot, url) ->
            url?.takeIf { it.isNotBlank() }?.let { put(slot.uppercase(), it) }
        }
    }
    val used = mutableSetOf<String>()
    return buildList {
        for (layer in savedLayers) {
            when (layer.layer) {
                AvatarLayerName.CHARACTER -> {
                    add(layer.copy(imageUrl = previewByLayer[AvatarLayerName.CHARACTER] ?: layer.imageUrl))
                    used += AvatarLayerName.CHARACTER
                }
                in AvatarLayerName.CLOTHING -> {
                    val url = previewByLayer[layer.layer] ?: continue
                    add(layer.copy(imageUrl = url))
                    used += layer.layer
                }
                else -> add(layer)
            }
        }
        previewByLayer.forEach { (slot, url) ->
            if (slot !in used) add(AvatarLayer(layer = slot, imageUrl = url))
        }
    }
}
