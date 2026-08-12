package com.li_routi.core.data.network.service

import com.li_routi.core.data.network.dto.request.CreateGroupRequest
import com.li_routi.core.data.network.dto.request.CreateGroupRoutineCategoryRequest
import com.li_routi.core.data.network.dto.request.GroupRoutineVerificationReadRequest
import com.li_routi.core.data.network.dto.request.JoinGroupRequest
import com.li_routi.core.data.network.dto.request.TransferGroupOwnerRequest
import com.li_routi.core.data.network.dto.request.UpdateGroupNameRequest
import com.li_routi.core.data.network.dto.request.UpdateStatusMessageRequest
import com.li_routi.core.data.network.dto.request.UpdateGroupRoutineRequest
import com.li_routi.core.data.network.dto.response.ApiResponse
import com.li_routi.core.data.network.dto.response.GroupCreateResultResponse
import com.li_routi.core.data.network.dto.response.GroupDetailResponse
import com.li_routi.core.data.network.dto.response.GroupRoutineDisappointmentResponse
import com.li_routi.core.data.network.dto.response.GroupInviteCodeResponse
import com.li_routi.core.data.network.dto.response.GroupJoinPreviewResponse
import com.li_routi.core.data.network.dto.response.GroupJoinResultResponse
import com.li_routi.core.data.network.dto.response.GroupLockStateResponse
import com.li_routi.core.data.network.dto.response.GroupRoutineLikeResponse
import com.li_routi.core.data.network.dto.response.GroupRoutineCategoryListResponse
import com.li_routi.core.data.network.dto.response.GroupStatusMessageResponse
import com.li_routi.core.data.network.dto.response.GroupRoutineCategoryResponse
import com.li_routi.core.data.network.dto.response.GroupRoutineFeedResponse
import com.li_routi.core.data.network.dto.response.GroupRoutineUpdateResultResponse
import com.li_routi.core.data.network.dto.response.GroupRoutineVerificationReadResponse
import com.li_routi.core.data.network.dto.response.ParticipatingGroupListResponse
import com.li_routi.core.data.network.dto.response.TodayGroupRoutineListResponse
import com.li_routi.core.data.network.dto.response.UnreadGroupRoutineVerificationListResponse
import retrofit2.http.Body
import retrofit2.http.DELETE
import retrofit2.http.GET
import retrofit2.http.PATCH
import retrofit2.http.POST
import retrofit2.http.PUT
import retrofit2.http.Path
import retrofit2.http.Query

interface GroupRoutineApiService {

    @POST("api/groups")
    suspend fun createGroup(
        @Body request: CreateGroupRequest,
    ): ApiResponse<GroupCreateResultResponse>

    @GET("api/groups")
    suspend fun getParticipatingGroups(): ApiResponse<ParticipatingGroupListResponse>

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

    @POST("api/groups/join")
    suspend fun joinGroup(
        @Body request: JoinGroupRequest,
    ): ApiResponse<GroupJoinResultResponse>

    @GET("api/groups/join/preview")
    suspend fun getJoinPreview(
        @Query("inviteCode") inviteCode: String,
    ): ApiResponse<GroupJoinPreviewResponse>

    @PATCH("api/groups/{groupId}/lock")
    suspend fun lockGroup(
        @Path("groupId") groupId: Long,
    ): ApiResponse<GroupLockStateResponse>

    @PATCH("api/groups/{groupId}/unlock")
    suspend fun unlockGroup(
        @Path("groupId") groupId: Long,
    ): ApiResponse<GroupLockStateResponse>

    @PATCH("api/groups/{groupId}/members/me/status-message")
    suspend fun updateStatusMessage(
        @Path("groupId") groupId: Long,
        @Body request: UpdateStatusMessageRequest,
    ): ApiResponse<GroupStatusMessageResponse>

    @PATCH("api/groups/{groupId}/name")
    suspend fun updateGroupName(
        @Path("groupId") groupId: Long,
        @Body request: UpdateGroupNameRequest,
    ): ApiResponse<Unit?>

    @PATCH("api/groups/{groupId}/owner")
    suspend fun transferOwner(
        @Path("groupId") groupId: Long,
        @Body request: TransferGroupOwnerRequest,
    ): ApiResponse<Unit?>

    @DELETE("api/groups/{groupId}/members/{targetMemberId}")
    suspend fun kickMember(
        @Path("groupId") groupId: Long,
        @Path("targetMemberId") targetMemberId: Long,
    ): ApiResponse<Unit?>

    @POST("api/groups/{groupId}/members/{targetMemberId}/pokes")
    suspend fun pokeMember(
        @Path("groupId") groupId: Long,
        @Path("targetMemberId") targetMemberId: Long,
    ): ApiResponse<Unit?>

    @DELETE("api/groups/{groupId}/routines/{routineId}")
    suspend fun deleteRoutine(
        @Path("groupId") groupId: Long,
        @Path("routineId") routineId: Long,
    ): ApiResponse<Unit?>

    @GET("api/groups/routines/today")
    suspend fun getTodayRoutines(): ApiResponse<TodayGroupRoutineListResponse>

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

    @GET("api/groups/{groupId}/routine-verifications/unread")
    suspend fun getUnreadRoutineVerifications(
        @Path("groupId") groupId: Long,
        @Query("cursor") cursor: Long?,
        @Query("size") size: Int?,
    ): ApiResponse<UnreadGroupRoutineVerificationListResponse>

    @POST("api/groups/{groupId}/routine-verifications/read")
    suspend fun markRoutineVerificationsRead(
        @Path("groupId") groupId: Long,
        @Body request: GroupRoutineVerificationReadRequest,
    ): ApiResponse<GroupRoutineVerificationReadResponse>

    @POST("api/groups/{groupId}/verifications/{verificationId}/likes")
    suspend fun likeRoutineVerification(
        @Path("groupId") groupId: Long,
        @Path("verificationId") verificationId: Long,
    ): ApiResponse<GroupRoutineLikeResponse>

    @DELETE("api/groups/{groupId}/verifications/{verificationId}/likes")
    suspend fun unlikeRoutineVerification(
        @Path("groupId") groupId: Long,
        @Path("verificationId") verificationId: Long,
    ): ApiResponse<GroupRoutineLikeResponse>

    @POST("api/groups/{groupId}/verifications/{verificationId}/disappointments")
    suspend fun disappointRoutineVerification(
        @Path("groupId") groupId: Long,
        @Path("verificationId") verificationId: Long,
    ): ApiResponse<GroupRoutineDisappointmentResponse>

    @DELETE("api/groups/{groupId}/verifications/{verificationId}/disappointments")
    suspend fun undisappointRoutineVerification(
        @Path("groupId") groupId: Long,
        @Path("verificationId") verificationId: Long,
    ): ApiResponse<GroupRoutineDisappointmentResponse>
}
