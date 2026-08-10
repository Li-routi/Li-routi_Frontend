package com.li_routi.core.data.network.service

import com.li_routi.core.data.network.dto.request.ChallengeVerificationRequest
import com.li_routi.core.data.network.dto.request.ReportRequest
import com.li_routi.core.data.network.dto.request.UpdateVerificationMemoRequest
import com.li_routi.core.data.network.dto.response.ApiResponse
import com.li_routi.core.data.network.dto.response.ChallengeDetailResponse
import com.li_routi.core.data.network.dto.response.ChallengeListingResponse
import com.li_routi.core.data.network.dto.response.CreateVerificationResponse
import com.li_routi.core.data.network.dto.response.DeleteVerificationResponse
import com.li_routi.core.data.network.dto.response.LikeResponse
import com.li_routi.core.data.network.dto.response.MyChallengeListingResponse
import com.li_routi.core.data.network.dto.response.MyVerificationFeedResponse
import com.li_routi.core.data.network.dto.response.ParticipationResponse
import com.li_routi.core.data.network.dto.response.ReportResponse
import com.li_routi.core.data.network.dto.response.UpdateVerificationMemoResponse
import com.li_routi.core.data.network.dto.response.VerificationFeedResponse
import retrofit2.http.Body
import retrofit2.http.DELETE
import retrofit2.http.GET
import retrofit2.http.PATCH
import retrofit2.http.POST
import retrofit2.http.Path
import retrofit2.http.Query

interface ChallengeApiService {

    @GET("api/challenges")
    suspend fun getChallenges(
        @Query("category") category: String?,
        @Query("keyword") keyword: String?,
        @Query("cursor") cursor: Long?,
        @Query("size") size: Int?,
    ): ApiResponse<ChallengeListingResponse>

    @GET("api/challenges/{challengeId}")
    suspend fun getChallenge(
        @Path("challengeId") challengeId: Long,
    ): ApiResponse<ChallengeDetailResponse>

    @GET("api/challenges/{challengeId}/verifications")
    suspend fun getVerifications(
        @Path("challengeId") challengeId: Long,
        @Query("cursor") cursor: Long?,
        @Query("cursorLikeCount") cursorLikeCount: Long?,
        @Query("size") size: Int?,
        @Query("sort") sort: String,
    ): ApiResponse<VerificationFeedResponse>

    @GET("api/challenges/{challengeId}/verifications/me")
    suspend fun getMyVerifications(
        @Path("challengeId") challengeId: Long,
        @Query("cursor") cursor: Long?,
        @Query("cursorLikeCount") cursorLikeCount: Long?,
        @Query("size") size: Int?,
        @Query("sort") sort: String,
    ): ApiResponse<MyVerificationFeedResponse>

    /** 사진(presigned 업로드로 받은 mediaKey)과 코멘트로 인증 게시글을 작성한다. */
    @POST("api/challenges/{challengeId}/verifications")
    suspend fun createVerification(
        @Path("challengeId") challengeId: Long,
        @Body request: ChallengeVerificationRequest,
    ): ApiResponse<CreateVerificationResponse>

    /** 인증 게시글의 메모(코멘트)만 수정한다 (사진은 그대로 유지). */
    @PATCH("api/challenges/{challengeId}/verifications/{verificationId}")
    suspend fun updateVerificationMemo(
        @Path("challengeId") challengeId: Long,
        @Path("verificationId") verificationId: Long,
        @Body request: UpdateVerificationMemoRequest,
    ): ApiResponse<UpdateVerificationMemoResponse>

    @DELETE("api/challenges/{challengeId}/verifications/{verificationId}")
    suspend fun deleteVerification(
        @Path("challengeId") challengeId: Long,
        @Path("verificationId") verificationId: Long,
    ): ApiResponse<DeleteVerificationResponse>

    @POST("api/challenges/{challengeId}/verifications/{verificationId}/reports")
    suspend fun reportVerification(
        @Path("challengeId") challengeId: Long,
        @Path("verificationId") verificationId: Long,
        @Body request: ReportRequest,
    ): ApiResponse<ReportResponse>

    @POST("api/challenges/{challengeId}/verifications/{verificationId}/likes")
    suspend fun likeVerification(
        @Path("challengeId") challengeId: Long,
        @Path("verificationId") verificationId: Long,
    ): ApiResponse<LikeResponse>

    @DELETE("api/challenges/{challengeId}/verifications/{verificationId}/likes")
    suspend fun unlikeVerification(
        @Path("challengeId") challengeId: Long,
        @Path("verificationId") verificationId: Long,
    ): ApiResponse<LikeResponse>

    @POST("api/challenges/{challengeId}/participation")
    suspend fun participate(
        @Path("challengeId") challengeId: Long,
    ): ApiResponse<ParticipationResponse>

    @DELETE("api/challenges/{challengeId}/participation")
    suspend fun leaveChallenge(
        @Path("challengeId") challengeId: Long,
    ): ApiResponse<ParticipationResponse>

    @GET("api/members/me/challenges")
    suspend fun getMyChallenges(
        @Query("category") category: String?,
        @Query("keyword") keyword: String?,
    ): ApiResponse<MyChallengeListingResponse>
}
