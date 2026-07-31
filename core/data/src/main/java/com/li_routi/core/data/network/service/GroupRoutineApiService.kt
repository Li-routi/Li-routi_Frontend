package com.li_routi.core.data.network.service

import com.li_routi.core.data.network.dto.request.CreateGroupRoutineRequest
import com.li_routi.core.data.network.dto.request.UpdateGroupRoutineRequest
import com.li_routi.core.data.network.dto.response.ApiResponse
import com.li_routi.core.data.network.dto.response.GroupInviteCodeResponse
import com.li_routi.core.data.network.dto.response.GroupRoutineCreateResultResponse
import com.li_routi.core.data.network.dto.response.GroupRoutineUpdateResultResponse
import com.li_routi.core.data.network.dto.response.TodayGroupRoutineListResponse
import retrofit2.http.Body
import retrofit2.http.GET
import retrofit2.http.POST
import retrofit2.http.PUT
import retrofit2.http.Path

interface GroupRoutineApiService {

    @POST("api/groups/{groupId}/routines")
    suspend fun createRoutine(
        @Path("groupId") groupId: Long,
        @Body request: CreateGroupRoutineRequest,
    ): ApiResponse<GroupRoutineCreateResultResponse>

    @PUT("api/groups/{groupId}/routines/{routineId}")
    suspend fun updateRoutine(
        @Path("groupId") groupId: Long,
        @Path("routineId") routineId: Long,
        @Body request: UpdateGroupRoutineRequest,
    ): ApiResponse<GroupRoutineUpdateResultResponse>

    @GET("api/groups/routines/today")
    suspend fun getTodayRoutines(): ApiResponse<TodayGroupRoutineListResponse>

    @POST("api/groups/{groupId}/invite-code")
    suspend fun issueInviteCode(
        @Path("groupId") groupId: Long,
    ): ApiResponse<GroupInviteCodeResponse>

    @GET("api/groups/{groupId}/invite-code")
    suspend fun getInviteCode(
        @Path("groupId") groupId: Long,
    ): ApiResponse<GroupInviteCodeResponse>
}
