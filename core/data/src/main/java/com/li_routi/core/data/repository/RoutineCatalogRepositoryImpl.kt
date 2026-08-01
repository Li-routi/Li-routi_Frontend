package com.li_routi.core.data.repository

import com.li_routi.core.common.kotlin.util.ApiException
import com.li_routi.core.common.kotlin.util.ResultState
import com.li_routi.core.data.mapper.toDomain
import com.li_routi.core.data.mapper.toRequest
import com.li_routi.core.data.network.dto.request.CreateRoutineCategoryRequest
import com.li_routi.core.data.network.dto.request.CreateRoutinesRequest
import com.li_routi.core.data.network.dto.response.ApiResponse
import com.li_routi.core.data.network.safeDataApiCall
import com.li_routi.core.data.network.service.RoutineApiService
import com.li_routi.core.domain.routine.CreateRoutineItem
import com.li_routi.core.domain.routine.CreateRoutinesResult
import com.li_routi.core.domain.routine.RoutineCatalogRepository
import com.li_routi.core.domain.routine.RoutineCategory
import com.li_routi.core.domain.routine.RoutineCategoryList
import com.li_routi.core.domain.routine.RoutineTemplate

class RoutineCatalogRepositoryImpl(
    private val api: RoutineApiService,
) : RoutineCatalogRepository {

    override suspend fun getCategories(): ResultState<RoutineCategoryList> = safeDataApiCall {
        api.getCategories().unwrap().toDomain()
    }

    override suspend fun createCategory(
        name: String,
        color: String?,
    ): ResultState<RoutineCategory> = safeDataApiCall {
        api.createCategory(
            CreateRoutineCategoryRequest(
                name = name.trim(),
                color = color,
            ),
        ).unwrap().toDomain()
    }

    override suspend fun getTemplates(categoryId: Long?): ResultState<List<RoutineTemplate>> =
        safeDataApiCall {
            api.getTemplates(categoryId = categoryId).unwrap().toDomain()
        }

    override suspend fun createRoutines(
        routines: List<CreateRoutineItem>,
    ): ResultState<CreateRoutinesResult> = safeDataApiCall {
        api.createRoutines(
            CreateRoutinesRequest(routines = routines.map { it.toRequest() }),
        ).unwrap().toDomain()
    }
}

private fun <T> ApiResponse<T>.unwrap(): T {
    val result = result
    if (!isSuccess || result == null) throw ApiException(message)
    return result
}
