package com.li_routi.core.data.network.service

import com.li_routi.core.data.network.dto.request.EquipAvatarRequest
import com.li_routi.core.data.network.dto.request.ExchangeRequest
import com.li_routi.core.data.network.dto.request.PurchaseItemsRequest
import com.li_routi.core.data.network.dto.request.StartChargeRequest
import com.li_routi.core.data.network.dto.response.ApiResponse
import com.li_routi.core.data.network.dto.response.ChargeProductsResponse
import com.li_routi.core.data.network.dto.response.ChargeSettledResponse
import com.li_routi.core.data.network.dto.response.ChargeStartedResponse
import com.li_routi.core.data.network.dto.response.ExchangeProductsResponse
import com.li_routi.core.data.network.dto.response.ExchangeResultResponse
import com.li_routi.core.data.network.dto.response.MemberAvatarResponse
import com.li_routi.core.data.network.dto.response.ShopAvatarItemsResponse
import com.li_routi.core.data.network.dto.response.ShopCategoriesResponse
import com.li_routi.core.data.network.dto.response.ShopPurchaseResultResponse
import com.li_routi.core.data.network.dto.response.WalletBalancesResponse
import retrofit2.http.Body
import retrofit2.http.GET
import retrofit2.http.POST
import retrofit2.http.PUT
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

    @GET("api/members/me/avatar")
    suspend fun getMyAvatar(): ApiResponse<MemberAvatarResponse>

    // 보낸 것이 곧 전체 착장임 — 바뀐 것만 보내면 나머지가 벗겨짐
    @PUT("api/members/me/avatar")
    suspend fun equipAvatar(
        @Body request: EquipAvatarRequest,
    ): ApiResponse<MemberAvatarResponse>

    @GET("api/shop/categories")
    suspend fun getShopCategories(): ApiResponse<ShopCategoriesResponse>

    @GET("api/shop/items")
    suspend fun getAvatarItems(
        @Query("slot") slot: String?,
        @Query("ownedOnly") ownedOnly: Boolean?,
    ): ApiResponse<ShopAvatarItemsResponse>

    // 여러 아이템을 한 번에 삼(최대 30개). 가격/결제 재화는 서버가 갖고 있고, 재화가 섞여도
    // 재화별로 합산해 각각 차감한다. 이 응답 자체는 착용 상태를 바꾸지 않으므로, 산 뒤 원하는
    // 착장은 equipAvatar로 별도 저장해야 한다.
    @POST("api/shop/items/purchase")
    suspend fun purchaseItems(
        @Body request: PurchaseItemsRequest,
    ): ApiResponse<ShopPurchaseResultResponse>

    @GET("api/shop/charge-products")
    suspend fun getChargeProducts(): ApiResponse<ChargeProductsResponse>

    @GET("api/shop/exchange-products")
    suspend fun getExchangeProducts(): ApiResponse<ExchangeProductsResponse>

    @POST("api/shop/exchanges")
    suspend fun exchangeCurrency(
        @Body request: ExchangeRequest,
    ): ApiResponse<ExchangeResultResponse>
}
