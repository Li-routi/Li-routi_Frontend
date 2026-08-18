package com.li_routi.core.data.network.service

import com.li_routi.core.data.network.dto.request.CreateRoutineCategoryRequest
import com.li_routi.core.data.network.dto.request.CreateRoutinesRequest
import com.li_routi.core.data.network.dto.request.RoutineVerificationRequest
import com.li_routi.core.data.network.dto.request.UpdateMemberRoutineRequest
import com.li_routi.core.data.network.dto.request.UpdateRoutineCategoryRequest
import com.li_routi.core.data.network.dto.response.ApiResponse
import com.li_routi.core.data.network.dto.response.CreateRoutinesResultResponse
import com.li_routi.core.data.network.dto.response.CreatedRoutineResponse
import com.li_routi.core.data.network.dto.response.GroupRoutineVerificationResponse
import com.li_routi.core.data.network.dto.response.MemberRoutineListResponse
import com.li_routi.core.data.network.dto.response.MemberRoutineVerificationResponse
import com.li_routi.core.data.network.dto.response.RoutineCategoryListResponse
import com.li_routi.core.data.network.dto.response.RoutineCategoryResponse
import com.li_routi.core.data.network.dto.response.RoutineTemplateListResponse
import retrofit2.http.Body
import retrofit2.http.DELETE
import retrofit2.http.GET
import retrofit2.http.PATCH
import retrofit2.http.POST
import retrofit2.http.Path
import retrofit2.http.Query

interface RoutineApiService {

    @GET("api/routines")
    suspend fun getRoutines(): ApiResponse<MemberRoutineListResponse>

    @PATCH("api/routines/{routineId}")
    suspend fun updateRoutine(
        @Path("routineId") routineId: Long,
        @Body body: UpdateMemberRoutineRequest,
    ): ApiResponse<CreatedRoutineResponse>

    @DELETE("api/routines/{routineId}")
    suspend fun deleteRoutine(
        @Path("routineId") routineId: Long,
    ): ApiResponse<String?>

    @GET("api/routines/categories")
    suspend fun getCategories(): ApiResponse<RoutineCategoryListResponse>

    @POST("api/routines/categories")
    suspend fun createCategory(
        @Body body: CreateRoutineCategoryRequest,
    ): ApiResponse<RoutineCategoryResponse>

    @PATCH("api/routines/categories/{categoryId}")
    suspend fun updateCategory(
        @Path("categoryId") categoryId: Long,
        @Body body: UpdateRoutineCategoryRequest,
    ): ApiResponse<RoutineCategoryResponse>

    @DELETE("api/routines/categories/{categoryId}")
    suspend fun deleteCategory(
        @Path("categoryId") categoryId: Long,
    ): ApiResponse<String?>

    @GET("api/routines/templates")
    suspend fun getTemplates(
        @Query("categoryId") categoryId: Long?,
    ): ApiResponse<RoutineTemplateListResponse>

    @POST("api/routines")
    suspend fun createRoutines(
        @Body body: CreateRoutinesRequest,
    ): ApiResponse<CreateRoutinesResultResponse>

    @POST("api/routines/{routineId}/verifications")
    suspend fun verifyMemberRoutine(
        @Path("routineId") routineId: Long,
        @Body body: RoutineVerificationRequest,
    ): ApiResponse<MemberRoutineVerificationResponse>

    @POST("api/groups/{groupId}/routines/{routineId}/verifications")
    suspend fun verifyGroupRoutine(
        @Path("groupId") groupId: Long,
        @Path("routineId") routineId: Long,
        @Body body: RoutineVerificationRequest,
    ): ApiResponse<GroupRoutineVerificationResponse>

    @PATCH("api/groups/{groupId}/routines/{routineId}/verifications/{verificationId}")
    suspend fun reverifyGroupRoutine(
        @Path("groupId") groupId: Long,
        @Path("routineId") routineId: Long,
        @Path("verificationId") verificationId: Long,
        @Body body: RoutineVerificationRequest,
    ): ApiResponse<GroupRoutineVerificationResponse>
}
