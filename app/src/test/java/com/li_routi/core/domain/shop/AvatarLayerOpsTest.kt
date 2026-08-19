package com.li_routi.core.domain.shop

import org.junit.Assert.assertEquals
import org.junit.Assert.assertFalse
import org.junit.Assert.assertTrue
import org.junit.Test

class AvatarLayerOpsTest {

    private fun layer(name: String, url: String = "$name.png") = AvatarLayer(name, url)

    @Test
    fun characterMissingDoesNotInsert() {
        val saved = listOf(layer("NEST_BACK"), layer("NEST_FRONT"))
        assertEquals(saved, saved.withCharacterImageUrl("new.png"))
        assertFalse(saved.hasCharacterLayer())
    }

    @Test
    fun characterUrlReplaceKeepsOrder() {
        val saved = listOf(
            layer("NEST_BACK"),
            layer("CHARACTER", "old.png"),
            layer("NEST_FRONT"),
        )
        val next = saved.withCharacterImageUrl("new.png")
        assertEquals(listOf("NEST_BACK", "CHARACTER", "NEST_FRONT"), next.map { it.layer })
        assertEquals("new.png", next[1].imageUrl)
        assertTrue(next.hasCharacterLayer())
    }

    @Test
    fun blankCharacterUrlIsNoOp() {
        val saved = listOf(layer("CHARACTER", "old.png"))
        assertEquals(saved, saved.withCharacterImageUrl(" "))
        assertEquals(saved, saved.withCharacterImageUrl(null))
    }

    @Test
    fun previewKeepsServerOrderAndReplacesClothing() {
        val saved = listOf(
            layer("NEST_BACK"),
            layer("CHARACTER", "old-char.png"),
            layer("BODY", "old-body.png"),
            layer("NEST_FRONT"),
            layer("HEAD", "old-head.png"),
        )
        val preview = previewAvatarLayers(
            savedLayers = saved,
            previewCharacterImageUrl = "new-char.png",
            equippedImageUrls = mapOf("BODY" to "new-body.png", "HEAD" to "new-head.png"),
        )
        assertEquals(
            listOf("NEST_BACK", "CHARACTER", "BODY", "NEST_FRONT", "HEAD"),
            preview.map { it.layer },
        )
        assertEquals("new-char.png", preview[1].imageUrl)
        assertEquals("new-body.png", preview[2].imageUrl)
        assertEquals("new-head.png", preview[4].imageUrl)
    }

    @Test
    fun previewDropsUnequippedClothingAndAppendsMissing() {
        val saved = listOf(
            layer("NEST_BACK"),
            layer("CHARACTER", "char.png"),
            layer("BODY", "body.png"),
            layer("NEST_FRONT"),
        )
        val preview = previewAvatarLayers(
            savedLayers = saved,
            previewCharacterImageUrl = "char.png",
            equippedImageUrls = mapOf("HEAD" to "hat.png"),
        )
        assertEquals(
            listOf("NEST_BACK", "CHARACTER", "NEST_FRONT", "HEAD"),
            preview.map { it.layer },
        )
    }

    @Test
    fun previewKeepsUnknownLayersInPlace() {
        val saved = listOf(
            layer("NEST_BACK"),
            layer("WINGS", "wings.png"),
            layer("CHARACTER", "c.png"),
        )
        val preview = previewAvatarLayers(saved, "c2.png", emptyMap())
        assertEquals(listOf("NEST_BACK", "WINGS", "CHARACTER"), preview.map { it.layer })
        assertEquals("wings.png", preview[1].imageUrl)
        assertEquals("c2.png", preview[2].imageUrl)
    }
}
