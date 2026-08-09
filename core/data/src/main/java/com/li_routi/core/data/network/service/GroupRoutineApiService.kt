package com.li_routi.core.data.network.service

import com.li_routi.core.data.network.dto.request.CreateGroupRequest
import com.li_routi.core.data.network.dto.request.CreateGroupRoutineCategoryRequest
import com.li_routi.core.data.network.dto.request.UpdateGroupRoutineRequest
import com.li_routi.core.data.network.dto.response.ApiResponse
import com.li_routi.core.data.network.dto.response.GroupCreateResultResponse
import com.li_routi.core.data.network.dto.response.GroupDetailResponse
import com.li_routi.core.data.network.dto.response.GroupInviteCodeResponse
import com.li_routi.core.data.network.dto.response.GroupRoutineCategoryListResponse
import com.li_routi.core.data.network.dto.response.GroupRoutineCategoryResponse
import com.li_routi.core.data.network.dto.response.GroupRoutineFeedResponse
import com.li_routi.core.data.network.dto.response.GroupRoutineUpdateResultResponse
import com.li_routi.core.data.network.dto.response.TodayGroupRoutineListResponse
import retrofit2.http.Body
import retrofit2.http.DELETE
import retrofit2.http.GET
import retrofit2.http.POST
import retrofit2.http.PUT
import retrofit2.http.Path
import retrofit2.http.Query

interface GroupRoutineApiService {

    @POST("api/groups")
    suspend fun createGroup(
        @Body request: CreateGroupRequest,
    ): ApiResponse<GroupCreateResultResponse>

    // 스웨거 문서(CreateRoutine)랑 실제 요청/응답 스키마가 다름 — 실서버 호출로 확인해봄. PUT이랑 동일하게
    // title/description/schedules 받고 단일 루틴 객체 반환함
    @POST("api/groups/{groupId}/routines")
    suspend fun createRoutine(
        @Path("groupId") groupId: Long,
        @Body request: UpdateGroupRoutineRequest,
    ): ApiResponse<GroupRoutineUpdateResultResponse>

    @PUT("api/groups/{groupId}/routines/{routineId}")
    suspend fun updateRoutine(
        @Path("groupId") groupId: Long,
        @Path("routineId") routineId: Long,
        @Body request: UpdateGroupRoutineRequest,
    ): ApiResponse<GroupRoutineUpdateResultResponse>

    @GET("api/groups/{groupId}")
    suspend fun getGroupDetail(
        @Path("groupId") groupId: Long,
    ): ApiResponse<GroupDetailResponse>

    @DELETE("api/groups/{groupId}")
    suspend fun deleteGroup(
        @Path("groupId") groupId: Long,
    ): ApiResponse<Unit?>

    @DELETE("api/groups/{groupId}/leave")
    suspend fun leaveGroup(
        @Path("groupId") groupId: Long,
    ): ApiResponse<Unit?>

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

    @GET("api/groups/{groupId}/categories")
    suspend fun getCategories(
        @Path("groupId") groupId: Long,
    ): ApiResponse<GroupRoutineCategoryListResponse>

    @POST("api/groups/{groupId}/categories")
    suspend fun createCategory(
        @Path("groupId") groupId: Long,
        @Body request: CreateGroupRoutineCategoryRequest,
    ): ApiResponse<GroupRoutineCategoryResponse>

    @GET("api/groups/{groupId}/routines/{routineId}/verifications")
    suspend fun getRoutineVerifications(
        @Path("groupId") groupId: Long,
        @Path("routineId") routineId: Long,
        @Query("cursor") cursor: Long?,
        @Query("size") size: Int?,
    ): ApiResponse<GroupRoutineFeedResponse>
}
