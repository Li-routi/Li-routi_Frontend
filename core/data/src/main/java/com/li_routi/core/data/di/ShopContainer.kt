package com.li_routi.core.data.di

import com.li_routi.core.data.appearance.MemberAppearanceStore
import com.li_routi.core.data.network.NetworkModule
import com.li_routi.core.data.preference.AuthTokenPreference
import com.li_routi.core.data.repository.ShopRepositoryImpl
import com.li_routi.core.domain.shop.ExchangeCurrencyUseCase
import com.li_routi.core.domain.shop.GetChargeProductsUseCase
import com.li_routi.core.domain.shop.GetExchangeProductsUseCase
import com.li_routi.core.domain.shop.CompleteChargeUseCase
import com.li_routi.core.domain.shop.EquipAvatarUseCase
import com.li_routi.core.domain.shop.GetMyAvatarUseCase
import com.li_routi.core.domain.shop.GetShopAvatarItemsUseCase
import com.li_routi.core.domain.shop.GetShopCategoriesUseCase
import com.li_routi.core.domain.shop.StartChargeUseCase
import com.li_routi.core.domain.shop.GetWalletBalancesUseCase
import com.li_routi.core.domain.shop.PurchaseShopItemsUseCase
import com.li_routi.core.domain.shop.ShopRepository

/**
 * Hilt 등 DI 프레임워크가 붙기 전까지 사용하는 수동 구성 root.
 * feature 모듈은 여기서 필요한 UseCase만 가져다 씀.
 */
object ShopContainer {

    private val repository: ShopRepository by lazy {
        ShopRepositoryImpl(NetworkModule.shopApiService)
    }

    /** 홈/상점이 같이 보는 착장·캐릭터. 정보가 없을 때만 GET 하고, 저장하면 여기도 갱신함 */
    val memberAppearanceStore: MemberAppearanceStore by lazy {
        MemberAppearanceStore(
            repository = repository,
            characterRepository = CharacterContainer.repository,
            tokenPreference = AuthTokenPreference(NetworkModule.appContext),
        )
    }

    val startChargeUseCase: StartChargeUseCase by lazy {
        StartChargeUseCase(repository)
    }

    val completeChargeUseCase: CompleteChargeUseCase by lazy {
        CompleteChargeUseCase(repository)
    }

    val getWalletBalancesUseCase: GetWalletBalancesUseCase by lazy {
        GetWalletBalancesUseCase(repository)
    }

    val getMyAvatarUseCase: GetMyAvatarUseCase by lazy {
        GetMyAvatarUseCase(repository)
    }

    val equipAvatarUseCase: EquipAvatarUseCase by lazy {
        EquipAvatarUseCase(repository)
    }

    val getShopCategoriesUseCase: GetShopCategoriesUseCase by lazy {
        GetShopCategoriesUseCase(repository)
    }

    val getShopAvatarItemsUseCase: GetShopAvatarItemsUseCase by lazy {
        GetShopAvatarItemsUseCase(repository)
    }

    val purchaseShopItemsUseCase: PurchaseShopItemsUseCase by lazy {
        PurchaseShopItemsUseCase(repository)
    }

    val getChargeProductsUseCase: GetChargeProductsUseCase by lazy {
        GetChargeProductsUseCase(repository)
    }

    val getExchangeProductsUseCase: GetExchangeProductsUseCase by lazy {
        GetExchangeProductsUseCase(repository)
    }

    val exchangeCurrencyUseCase: ExchangeCurrencyUseCase by lazy {
        ExchangeCurrencyUseCase(repository)
    }
}
