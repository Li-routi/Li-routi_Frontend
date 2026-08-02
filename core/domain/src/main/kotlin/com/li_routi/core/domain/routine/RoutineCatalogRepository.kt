package com.li_routi.core.domain.routine

import com.li_routi.core.common.kotlin.util.ResultState

interface RoutineCatalogRepository {

    suspend fun getCategories(): ResultState<RoutineCategoryList>

    suspend fun createCategory(name: String, color: String?): ResultState<RoutineCategory>

    suspend fun getTemplates(categoryId: Long?): ResultState<List<RoutineTemplate>>

    suspend fun createRoutines(routines: List<CreateRoutineItem>): ResultState<CreateRoutinesResult>
}
