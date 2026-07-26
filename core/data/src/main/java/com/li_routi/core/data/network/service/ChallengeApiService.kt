package com.li_routi.core.data.network.service

import com.li_routi.core.data.network.dto.response.ApiResponse
import com.li_routi.core.data.network.dto.response.ChallengeDetailResponse
import com.li_routi.core.data.network.dto.response.ChallengeListingResponse
import com.li_routi.core.data.network.dto.response.MyChallengeListingResponse
import com.li_routi.core.data.network.dto.response.ParticipationResponse
import com.li_routi.core.data.network.dto.response.VerificationFeedResponse
import retrofit2.http.DELETE
import retrofit2.http.GET
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
        @Query("size") size: Int?,
    ): ApiResponse<VerificationFeedResponse>

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
