package com.li_routi.feature.home.shop.vm

import com.li_routi.core.data.appearance.FallbackCharacterId
import com.li_routi.core.domain.shop.AvatarLayer
import com.li_routi.feature.home.shop.component.ShopItemUiModel

/**
 * 상점 상단 탭 하나.
 *
 * 이름으로 분기하지 않고 [slot]을 그대로 조회에 실어 보냄 — 탭이 늘어도 앱을 안 고치려는 것임
 */
/**
 * 캐릭터 위에 겹쳐 그릴 착용 아이템 한 건.
 *
 * 안 산 아이템도 미리보기로 올라오므로 [owned]로 갈라둠 — 저장은 보유한 것만 보낼 수 있음
 */
data class EquippedUiModel(
    val itemId: Long,
    val imageUrl: String?,
    val owned: Boolean = true,
)

data class ShopCategoryUiModel(
    val key: String,
    val name: String,
    /** `ITEM`이면 서버 아이템 목록, `CHARACTER`면 앱에 넣은 캐릭터 목록을 뿌림 */
    val source: String,
    val slot: String?,
)

/**
 * 상점 최상위 탭. Figma node `6057:20320`: "캐릭터"/"의상"이 한 줄 필터가 아니라 상위 탭으로
 * 분리되어 있고, [CLOTHING]을 골랐을 때만 그 아래에 [ShopCategoryUiModel] 하위 필터(전체/머리장식/
 * 옷/소품/세트)가 보인다.
 */
enum class ShopMainTab {
    CHARACTER,
    CLOTHING,
}

/**
 * 아이템 상점 화면 UI 상태.
 *
 * [items]는 `GET /api/shop/items` 결과로 채운다. 실패하면 빈 목록이라 샘플이 실제 상품처럼
 * 보이는 일이 없다 — 샘플 id로는 구매도 되지 않는다.
 *
 * 탭 선택과 보유 토글은 곧바로 서버 재조회로 이어져서 여기서 함께 관리한다.
 */
data class ShopUiState(
    val nickname: String = "닉네임",
    val coinBalance: Int = 450,
    val gemBalance: Int = 30,
    /** 상위 탭. "의상"일 때만 [categories] 하위 필터가 보인다. 기본은 "캐릭터" */
    val selectedMainTab: ShopMainTab = ShopMainTab.CHARACTER,
    /** 서버가 내려준 의상 하위 필터. 받은 순서대로 그린다 */
    val categories: List<ShopCategoryUiModel> = emptyList(),
    val selectedCategoryIndex: Int = 0,
    val showOwnedOnly: Boolean = false,
    val items: List<ShopItemUiModel> = emptyList(),
    /**
     * 지금 캐릭터에 올려둔 착장. 자리 → 아이템.
     *
     * 저장 전에도 화면에 바로 비치게 여기서 들고 있다가, 저장할 때 통째로 보냄.
     * 한 자리엔 하나만 올라가므로 안 산 것을 미리 입어보면 그 자리 미리보기가 바뀜
     */
    val equipped: Map<String, EquippedUiModel> = emptyMap(),
    /**
     * 서버에 저장된 착장. 목록의 `착용중` 표시는 이걸 따름.
     *
     * [equipped]를 쓰면 고르자마자 착용중이 돼서 저장한 것과 구분이 안 됨
     */
    val savedEquippedItemIds: Set<Long> = emptySet(),
    /**
     * 서버에 저장된 아바타 레이어(캐릭터·둥지 포함). 미리보기 캐릭터 카드는 이 중 둥지 레이어를
     * 그대로 가져다 쓴다 — 둥지는 상점에서 직접 고르는 게 아니라 서버가 계산해서 내려줌
     */
    val savedLayers: List<AvatarLayer> = emptyList(),
    /**
     * 서버에 저장된(선택된) 캐릭터. 캐릭터 탭의 `착용중` 표시는 이걸 따름.
     *
     * 캐릭터는 `GET /api/shop/items` 아이템이 아니라 겹쳐 입기의 바탕 그림이라 [equipped]와 따로 둠.
     */
    val savedCharacterId: Long = FallbackCharacterId,
    /** 지금 캐릭터 카드에 비치는 캐릭터. 고르면 저장 전에도 바로 바뀜 */
    val previewCharacterId: Long = FallbackCharacterId,
    /** [previewCharacterId]의 그림. 캐릭터 카드 렌더링에 씀 */
    val previewCharacterImageUrl: String? = null,
    /** 착장 저장 중 */
    val isEquipping: Boolean = false,
    /**
     * 그리드에서 고른 아이템. 아이템 id → 아이템.
     *
     * 탭을 옮겨도 유지돼야 여러 탭에서 고른 걸 한 번에 살 수 있음.
     * [items]는 지금 탭 것만 들고 있어서 id만 갖고는 다른 탭 선택을 되짚을 수 없어 아이템째로 담음
     */
    val selectedItems: Map<String, ShopItemUiModel> = emptyMap(),
    val isLoading: Boolean = false,
    /** 구매 요청 중. 따닥으로 두 번 사는 것 방지 */
    val isPurchasing: Boolean = false,
    /** 하단 "N개 구매" 버튼을 눌러 확인 다이얼로그가 떠 있는지 */
    val isPurchaseConfirmVisible: Boolean = false,
    val message: String? = null,
) {
    /** 고른 것 중 아직 안 산 아이템. 구매 대상 여부만 가름 — 버튼 활성화는 [hasUnsavedChanges]가 함 */
    val purchaseTargets: List<ShopItemUiModel>
        get() = selectedItems.values.filterNot { it.owned }

    /**
     * 지금 미리보기 중인 모습(착장+캐릭터)이 저장된 모습과 다른지.
     *
     * 이미 보유한 다른 아이템/캐릭터로 바꾼 경우엔 [purchaseTargets]가 비어 있어도 저장은 해야
     * 하므로, 하단 버튼 활성화는 구매 대상 유무가 아니라 이 값으로 가른다.
     */
    val hasUnsavedChanges: Boolean
        get() = equipped.values.mapTo(mutableSetOf()) { it.itemId } != savedEquippedItemIds ||
            previewCharacterId != savedCharacterId

    /** 구매 대상 중 GEM(블루젬)으로 결제할 것들의 합계 */
    val purchaseGemTotal: Int
        get() = purchaseTargets.filter { it.currency == "GEM" }.sumOf { it.price }

    /** 구매 대상 중 TOPAZ(오렌지젬)로 결제할 것들의 합계 */
    val purchaseTopazTotal: Int
        get() = purchaseTargets.filter { it.currency == "TOPAZ" }.sumOf { it.price }

    /** 블루젬(GEM)이 모자란지 — 부족분 계산에도 씀 */
    val isGemShort: Boolean
        get() = purchaseGemTotal > gemBalance

    /** 오렌지젬(TOPAZ)이 모자란지 — 부족분 계산에도 씀 */
    val isTopazShort: Boolean
        get() = purchaseTopazTotal > coinBalance

    /**
     * 캐릭터 카드에 지금 그릴 레이어. 미리보기(안 산 아이템, 안 고른 캐릭터)를 얹어야 해서
     * 저장된 [savedLayers]를 그대로 쓸 수 없다.
     *
     * 둥지는 상점에서 고르는 게 아니라 [savedLayers]에서 그대로 가져오고, 캐릭터/BODY/HEAD/HAND만
     * 지금 미리보기 값으로 바꿔 끼움. 자리가 비었으면(안 입음) 그 레이어를 통째로 뺌
     */
    val previewLayers: List<AvatarLayer>
        get() = buildList {
            savedLayers.firstOrNull { it.layer == "NEST_BACK" }?.let(::add)
            previewCharacterImageUrl?.takeIf { it.isNotBlank() }
                ?.let { add(AvatarLayer(layer = "CHARACTER", imageUrl = it)) }
            equipped["BODY"]?.imageUrl?.takeIf { it.isNotBlank() }
                ?.let { add(AvatarLayer(layer = "BODY", imageUrl = it)) }
            savedLayers.firstOrNull { it.layer == "NEST_FRONT" }?.let(::add)
            equipped["HEAD"]?.imageUrl?.takeIf { it.isNotBlank() }
                ?.let { add(AvatarLayer(layer = "HEAD", imageUrl = it)) }
            equipped["HAND"]?.imageUrl?.takeIf { it.isNotBlank() }
                ?.let { add(AvatarLayer(layer = "HAND", imageUrl = it)) }
        }
}

/**
 * 아이템 상점 화면의 일회성 UI 이벤트.
 *
 * Navigation은 [com.li_routi.feature.home.shop.navigation.ShoppingRoute] / AppNavHost에서 처리한다.
 */
sealed interface ShopUiEvent {
    data object NavigateBack : ShopUiEvent
    /**
     * 잔액 chip 탭 → 재화 구매 화면.
     * @param tabIndex 0 = 주황보석, 1 = 파란보석
     */
    data class NavigateToCurrencyShop(val tabIndex: Int = 0) : ShopUiEvent
    /** 하단 저장 버튼 탭. API 연동 전: ShoppingRoute에서 no-op. */
    data object SaveSelectedItems : ShopUiEvent
}
