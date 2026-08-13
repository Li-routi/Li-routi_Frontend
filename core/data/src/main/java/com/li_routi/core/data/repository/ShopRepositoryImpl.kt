package com.li_routi.core.data.repository

import com.li_routi.core.common.kotlin.util.ApiException
import com.li_routi.core.common.kotlin.util.ResultState
import com.li_routi.core.common.kotlin.util.safeApiCall
import com.li_routi.core.data.mapper.toDomain
import com.li_routi.core.data.network.dto.request.ExchangeRequest
import com.li_routi.core.data.network.dto.request.StartChargeRequest
import com.li_routi.core.data.network.dto.response.ApiResponse
import com.li_routi.core.data.network.service.ShopApiService
import com.li_routi.core.domain.shop.ChargeProduct
import com.li_routi.core.domain.shop.ChargeSettled
import com.li_routi.core.domain.shop.ChargeStarted
import com.li_routi.core.domain.shop.CurrencyBalance
import com.li_routi.core.domain.shop.ExchangeProduct
import com.li_routi.core.domain.shop.ExchangeResult
import com.li_routi.core.domain.shop.MemberAvatar
import com.li_routi.core.domain.shop.ShopAvatarItem
import com.li_routi.core.domain.shop.ShopRepository
import com.google.gson.Gson
import com.google.gson.JsonObject
import retrofit2.HttpException

class ShopRepositoryImpl(
    private val api: ShopApiService,
) : ShopRepository {

    override suspend fun startCharge(productId: Long): ResultState<ChargeStarted> = shopCall {
        api.startCharge(StartChargeRequest(productId = productId)).unwrap().toDomain()
    }

    override suspend fun completeCharge(paymentId: String): ResultState<ChargeSettled> = shopCall {
        api.completeCharge(paymentId).unwrap().toDomain()
    }

    override suspend fun getWalletBalances(): ResultState<List<CurrencyBalance>> = shopCall {
        api.getWalletBalances().unwrap().toDomain()
    }

    override suspend fun getAvatarItems(
        slot: String?,
        ownedOnly: Boolean?,
    ): ResultState<List<ShopAvatarItem>> = shopCall {
        api.getAvatarItems(slot = slot, ownedOnly = ownedOnly).unwrap().toDomain()
    }

    override suspend fun purchaseAvatarItem(itemId: Long): ResultState<MemberAvatar> = shopCall {
        api.purchaseAvatarItem(itemId).unwrap().toDomain()
    }

    override suspend fun getChargeProducts(): ResultState<List<ChargeProduct>> = shopCall {
        api.getChargeProducts().unwrap().toDomain()
    }

    override suspend fun getExchangeProducts(): ResultState<List<ExchangeProduct>> = shopCall {
        api.getExchangeProducts().unwrap().toDomain()
    }

    override suspend fun exchangeCurrency(
        productId: Long,
        idempotencyKey: String,
    ): ResultState<ExchangeResult> = shopCall {
        api.exchangeCurrency(
            ExchangeRequest(productId = productId, idempotencyKey = idempotencyKey),
        ).unwrap().toDomain()
    }
}

/**
 * 4xx/5xx면 Retrofit이 [HttpException]을 먼저 던져서 서버가 준 message가 버려짐.
 * 상점은 잔액 부족처럼 사용자에게 사유를 그대로 보여줘야 하는 경우가 많아 바디에서 꺼내 씀
 */
private suspend fun <T> shopCall(block: suspend () -> T): ResultState<T> = safeApiCall {
    try {
        block()
    } catch (e: HttpException) {
        throw ApiException(e.serverMessage() ?: "요청을 처리하지 못했어요.")
    }
}

private fun HttpException.serverMessage(): String? = runCatching {
    response()?.errorBody()?.string()?.let { body ->
        Gson().fromJson(body, JsonObject::class.java)?.get("message")?.asString
    }
}.getOrNull()?.takeIf { it.isNotBlank() }

private fun <T> ApiResponse<T>.unwrap(): T {
    val result = result
    if (!isSuccess || result == null) throw ApiException(message)
    return result
}
