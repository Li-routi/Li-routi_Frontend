package com.li_routi.core.data.network.service

import com.li_routi.core.data.network.dto.request.UpdateProfileRequest
import com.li_routi.core.data.network.dto.response.ApiResponse
import com.li_routi.core.data.network.dto.response.MemberInfoResponse
import retrofit2.http.Body
import retrofit2.http.PATCH

interface MemberApiService {

    @PATCH("api/members/me/profile")
    suspend fun updateProfile(
        @Body request: UpdateProfileRequest,
    ): ApiResponse<MemberInfoResponse>
}
