package com.li_routi.core.data.network.service

import com.li_routi.core.data.network.dto.request.LogoutRequest
import com.li_routi.core.data.network.dto.request.SocialLoginRequest
import com.li_routi.core.data.network.dto.response.ApiResponse
import com.li_routi.core.data.network.dto.response.GoogleNonceResponse
import com.li_routi.core.data.network.dto.response.TokenResponse
import retrofit2.http.Body
import retrofit2.http.POST

interface AuthApiService {

    @POST("api/auth/social-login")
    suspend fun socialLogin(
        @Body request: SocialLoginRequest,
    ): ApiResponse<TokenResponse>

    @POST("api/auth/google/nonce")
    suspend fun issueGoogleNonce(): ApiResponse<GoogleNonceResponse>

    /** 응답 result가 항상 null이라(성공해도 페이로드 없음) Unit?으로 받는다. */
    @POST("api/v1/members/logout")
    suspend fun logout(
        @Body request: LogoutRequest,
    ): ApiResponse<Unit?>
}
