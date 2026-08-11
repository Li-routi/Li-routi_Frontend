package com.li_routi.core.data.network.service

import com.li_routi.core.data.network.dto.request.LogoutRequest
import com.li_routi.core.data.network.dto.request.ReissueRequest
import com.li_routi.core.data.network.dto.request.SocialLoginRequest
import com.li_routi.core.data.network.dto.request.UpdateProfileRequest
import com.li_routi.core.data.network.dto.request.WithdrawRequest
import com.li_routi.core.data.network.dto.response.ApiResponse
import com.li_routi.core.data.network.dto.response.GoogleNonceResponse
import com.li_routi.core.data.network.dto.response.MemberVerificationsResponse
import com.li_routi.core.data.network.dto.response.MyInfoResponse
import com.li_routi.core.data.network.dto.response.ReissueResponse
import com.li_routi.core.data.network.dto.response.TokenResponse
import retrofit2.http.Body
import retrofit2.http.GET
import retrofit2.http.HTTP
import retrofit2.http.PATCH
import retrofit2.http.POST
import retrofit2.http.Query

interface AuthApiService {

    @POST("api/auth/social-login")
    suspend fun socialLogin(
        @Body request: SocialLoginRequest,
    ): ApiResponse<TokenResponse>

    @POST("api/auth/google/nonce")
    suspend fun issueGoogleNonce(): ApiResponse<GoogleNonceResponse>

    /** 로그인한 회원의 프로필 정보를 조회한다. */
    @GET("api/members/me")
    suspend fun getMyInfo(): ApiResponse<MyInfoResponse>

    /** 로그인한 회원의 프로필 정보를 수정한다. */
    @PATCH("api/members/me/profile")
    suspend fun updateProfile(
        @Body request: UpdateProfileRequest,
    ): ApiResponse<MyInfoResponse>

    /**
     * 특정 날짜에 남긴 인증(챌린지·개인 루틴·그룹 루틴 통합)을 최신순으로 조회한다.
     * [date]를 생략하면 오늘(KST) 기준으로 조회한다. [status]="PENDING"으로 좁히면 심사 중인 챌린지
     * 인증만 내려간다 — 루틴 인증은 심사가 없어 빠진다.
     */
    @GET("api/members/me/verifications")
    suspend fun getMyVerifications(
        @Query("date") date: String?,
        @Query("status") status: String?,
    ): ApiResponse<MemberVerificationsResponse>

    /** accessToken 만료 시 refreshToken으로 서비스 토큰을 재발급받는다. */
    @POST("api/auth/reissue")
    suspend fun reissue(
        @Body request: ReissueRequest,
    ): ApiResponse<ReissueResponse>

    /** 응답 result가 항상 null이라(성공해도 페이로드 없음) Unit?으로 받는다. */
    @POST("api/members/logout")
    suspend fun logout(
        @Body request: LogoutRequest,
    ): ApiResponse<Unit?>

    /**
     * 회원 탈퇴. Retrofit의 `@DELETE`는 요청 바디를 지원하지 않아 `@HTTP(hasBody = true)`로 대체한다.
     * `confirmation`은 [com.li_routi.core.data.repository.AuthRepositoryImpl]가 고정 문구를 채워 보낸다.
     */
    @HTTP(method = "DELETE", path = "api/members/me", hasBody = true)
    suspend fun withdraw(
        @Body request: WithdrawRequest,
    ): ApiResponse<String?>
}
