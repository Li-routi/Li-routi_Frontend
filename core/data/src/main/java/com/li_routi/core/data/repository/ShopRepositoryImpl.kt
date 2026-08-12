package com.li_routi.core.data.repository

import com.li_routi.core.common.kotlin.util.ApiException
import com.li_routi.core.common.kotlin.util.ResultState
import com.li_routi.core.common.kotlin.util.safeApiCall
import com.li_routi.core.data.mapper.toDomain
import com.li_routi.core.data.network.dto.request.ExchangeRequest
import com.li_routi.core.data.network.dto.response.ApiResponse
import com.li_routi.core.data.network.service.ShopApiService
import com.li_routi.core.domain.shop.ChargeProduct
import com.li_routi.core.domain.shop.ExchangeProduct
import com.li_routi.core.domain.shop.ExchangeResult
import com.li_routi.core.domain.shop.MemberAvatar
import com.li_routi.core.domain.shop.ShopAvatarItem
import com.li_routi.core.domain.shop.ShopRepository

class ShopRepositoryImpl(
    private val api: ShopApiService,
) : ShopRepository {

    override suspend fun getAvatarItems(
        slot: String?,
        ownedOnly: Boolean?,
    ): ResultState<List<ShopAvatarItem>> = safeApiCall {
        api.getAvatarItems(slot = slot, ownedOnly = ownedOnly).unwrap().toDomain()
    }

    override suspend fun purchaseAvatarItem(itemId: Long): ResultState<MemberAvatar> = safeApiCall {
        api.purchaseAvatarItem(itemId).unwrap().toDomain()
    }

    override suspend fun getChargeProducts(): ResultState<List<ChargeProduct>> = safeApiCall {
        api.getChargeProducts().unwrap().toDomain()
    }

    override suspend fun getExchangeProducts(): ResultState<List<ExchangeProduct>> = safeApiCall {
        api.getExchangeProducts().unwrap().toDomain()
    }

    override suspend fun exchangeCurrency(
        productId: Long,
        idempotencyKey: String,
    ): ResultState<ExchangeResult> = safeApiCall {
        api.exchangeCurrency(
            ExchangeRequest(productId = productId, idempotencyKey = idempotencyKey),
        ).unwrap().toDomain()
    }
}

private fun <T> ApiResponse<T>.unwrap(): T {
    val result = result
    if (!isSuccess || result == null) throw ApiException(message)
    return result
}
