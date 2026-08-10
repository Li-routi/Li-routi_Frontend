package com.li_routi.core.data.repository

import com.li_routi.core.common.kotlin.util.ApiException
import com.li_routi.core.common.kotlin.util.ResultState
import com.li_routi.core.data.mapper.toDomain
import com.li_routi.core.data.mapper.toRequest
import com.li_routi.core.data.network.dto.request.CreateRoutineCategoryRequest
import com.li_routi.core.data.network.dto.request.CreateRoutinesRequest
import com.li_routi.core.data.network.dto.request.UpdateMemberRoutineRequest
import com.li_routi.core.data.network.dto.request.UpdateRoutineCategoryRequest
import com.li_routi.core.data.network.apiCall
import com.li_routi.core.data.network.safeDataApiCall
import com.li_routi.core.data.network.service.RoutineApiService
import com.li_routi.core.domain.routine.CreateRoutineItem
import com.li_routi.core.domain.routine.CreateRoutinesResult
import com.li_routi.core.domain.routine.CreatedRoutine
import com.li_routi.core.domain.routine.MemberRoutineList
import com.li_routi.core.domain.routine.RoutineCatalogRepository
import com.li_routi.core.domain.routine.RoutineCategory
import com.li_routi.core.domain.routine.RoutineCategoryList
import com.li_routi.core.domain.routine.RoutineTemplate
import com.li_routi.core.domain.routine.UpdateMemberRoutine

class RoutineCatalogRepositoryImpl(
    private val api: RoutineApiService,
) : RoutineCatalogRepository {

    override suspend fun getRoutines(): ResultState<MemberRoutineList> = safeDataApiCall {
        apiCall { api.getRoutines() }.toDomain()
    }

    override suspend fun updateRoutine(
        routineId: Long,
        update: UpdateMemberRoutine,
    ): ResultState<CreatedRoutine> = safeDataApiCall {
        apiCall {
            api.updateRoutine(
                routineId = routineId,
                body = UpdateMemberRoutineRequest(
                    name = update.name,
                    endTime = update.endTime,
                    repeatDays = update.repeatDays,
                    alarmTime = update.alarmTime,
                ),
            )
        }.toDomain()
    }

    override suspend fun deleteRoutine(routineId: Long): ResultState<Unit> = safeDataApiCall {
        val response = api.deleteRoutine(routineId)
        if (!response.isSuccess) throw ApiException(response.message)
    }

    override suspend fun getCategories(): ResultState<RoutineCategoryList> = safeDataApiCall {
        apiCall { api.getCategories() }.toDomain()
    }

    override suspend fun createCategory(
        name: String,
        color: String?,
    ): ResultState<RoutineCategory> = safeDataApiCall {
        apiCall {
            api.createCategory(
                CreateRoutineCategoryRequest(
                    name = name.trim(),
                    color = color,
                ),
            )
        }.toDomain()
    }

    override suspend fun updateCategory(
        categoryId: Long,
        name: String,
        color: String?,
    ): ResultState<RoutineCategory> = safeDataApiCall {
        apiCall {
            api.updateCategory(
                categoryId = categoryId,
                body = UpdateRoutineCategoryRequest(name = name.trim(), color = color),
            )
        }.toDomain()
    }

    override suspend fun deleteCategory(categoryId: Long): ResultState<Unit> = safeDataApiCall {
        val response = api.deleteCategory(categoryId)
        if (!response.isSuccess) throw ApiException(response.message)
    }

    override suspend fun getTemplates(categoryId: Long?): ResultState<List<RoutineTemplate>> =
        safeDataApiCall {
            apiCall { api.getTemplates(categoryId = categoryId) }.toDomain()
        }

    override suspend fun createRoutines(
        routines: List<CreateRoutineItem>,
    ): ResultState<CreateRoutinesResult> = safeDataApiCall {
        apiCall {
            api.createRoutines(
                CreateRoutinesRequest(routines = routines.map { it.toRequest() }),
            )
        }.toDomain()
    }
}
