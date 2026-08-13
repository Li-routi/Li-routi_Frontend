package com.li_routi.core.data.network.service

import com.li_routi.core.data.network.dto.request.ExchangeRequest
import com.li_routi.core.data.network.dto.request.StartChargeRequest
import com.li_routi.core.data.network.dto.response.ApiResponse
import com.li_routi.core.data.network.dto.response.ChargeProductsResponse
import com.li_routi.core.data.network.dto.response.ChargeSettledResponse
import com.li_routi.core.data.network.dto.response.ChargeStartedResponse
import com.li_routi.core.data.network.dto.response.ExchangeProductsResponse
import com.li_routi.core.data.network.dto.response.ExchangeResultResponse
import com.li_routi.core.data.network.dto.response.MemberAvatarResponse
import com.li_routi.core.data.network.dto.response.ShopAvatarItemsResponse
import com.li_routi.core.data.network.dto.response.WalletBalancesResponse
import retrofit2.http.Body
import retrofit2.http.GET
import retrofit2.http.POST
import retrofit2.http.Path
import retrofit2.http.Query

interface ShopApiService {

    @POST("api/shop/charges")
    suspend fun startCharge(
        @Body request: StartChargeRequest,
    ): ApiResponse<ChargeStartedResponse>

    // 결제창을 마친 뒤 호출함. 서버가 포트원에 다시 물어봐서 판단하므로 body가 없음
    @POST("api/shop/charges/{paymentId}/complete")
    suspend fun completeCharge(
        @Path("paymentId") paymentId: String,
    ): ApiResponse<ChargeSettledResponse>

    @GET("api/members/me/wallet")
    suspend fun getWalletBalances(): ApiResponse<WalletBalancesResponse>

    @GET("api/shop/items")
    suspend fun getAvatarItems(
        @Query("slot") slot: String?,
        @Query("ownedOnly") ownedOnly: Boolean?,
    ): ApiResponse<ShopAvatarItemsResponse>

    // 요청 body 없음 — 가격이랑 결제 재화는 서버가 갖고 있음
    @POST("api/shop/items/{itemId}/purchase")
    suspend fun purchaseAvatarItem(
        @Path("itemId") itemId: Long,
    ): ApiResponse<MemberAvatarResponse>

    @GET("api/shop/charge-products")
    suspend fun getChargeProducts(): ApiResponse<ChargeProductsResponse>

    @GET("api/shop/exchange-products")
    suspend fun getExchangeProducts(): ApiResponse<ExchangeProductsResponse>

    @POST("api/shop/exchanges")
    suspend fun exchangeCurrency(
        @Body request: ExchangeRequest,
    ): ApiResponse<ExchangeResultResponse>
}
