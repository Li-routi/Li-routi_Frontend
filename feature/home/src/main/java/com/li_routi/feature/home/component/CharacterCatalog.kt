package com.li_routi.feature.home.component

import androidx.annotation.DrawableRes
import com.li_routi.core.data.appearance.FallbackCharacterId
import com.li_routi.core.designsystem.R

/** 고를 수 있는 캐릭터 한 종류. */
data class CharacterUiModel(
    val id: String,
    val name: String,
    @DrawableRes val imageRes: Int,
)

/** 고른 적이 없으면 쓰는 캐릭터 */
const val DefaultCharacterId: String = FallbackCharacterId

/**
 * 상점 캐릭터 탭에 뿌릴 목록.
 *
 * 서버에 캐릭터 도메인이 없어서 앱에 넣은 이미지로 목록을 만든다 — 캐릭터 목록 API가 생기면
 * 이 목록을 서버 응답으로 갈아끼우면 됨.
 *
 * 모두 같은 캔버스에 같은 자세로 그려져 있어서 의상 이미지가 그대로 겹쳐진다 —
 * 캐릭터를 바꿔도 착장 위치를 따로 맞출 필요가 없음
 */
val CharacterCatalog: List<CharacterUiModel> = listOf(
    CharacterUiModel(DefaultCharacterId, "파랑새", R.drawable.default_character),
    CharacterUiModel("sky_bird", "하늘새", R.drawable.blue_character),
    CharacterUiModel("mint_bird", "민트새", R.drawable.mint_character),
    CharacterUiModel("green_bird", "초록새", R.drawable.green_character),
    CharacterUiModel("lime_bird", "연두새", R.drawable.green_character2),
    CharacterUiModel("yellow_bird", "노랑새", R.drawable.yellow_character),
    CharacterUiModel("orange_bird", "주황새", R.drawable.yellow_character2),
    CharacterUiModel("coral_bird", "코랄새", R.drawable.red_character),
    CharacterUiModel("pink_bird", "분홍새", R.drawable.violet_character),
    CharacterUiModel("purple_bird", "보라새", R.drawable.purple_character),
    CharacterUiModel("white_bird", "흰새", R.drawable.white_character),
    CharacterUiModel("penguin", "펭귄", R.drawable.penguin_character),
    CharacterUiModel("baby_penguin", "아기펭귄", R.drawable.penguin_character2),
)

/**
 * 저장된 id로 이미지를 되짚음.
 *
 * 목록에서 빠진 id가 기기에 남아 있을 수 있어서(앱 업데이트로 캐릭터가 바뀌는 경우)
 * 못 찾으면 기본 캐릭터로 둠
 */
@DrawableRes
fun characterImageResOf(characterId: String?): Int =
    CharacterCatalog.firstOrNull { it.id == characterId }?.imageRes
        ?: R.drawable.default_character
