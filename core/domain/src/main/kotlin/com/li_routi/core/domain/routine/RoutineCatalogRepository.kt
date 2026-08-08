package com.li_routi.core.domain.routine

import com.li_routi.core.common.kotlin.util.ResultState

interface RoutineCatalogRepository {

    suspend fun getRoutines(): ResultState<MemberRoutineList>

    suspend fun updateRoutine(
        routineId: Long,
        update: UpdateMemberRoutine,
    ): ResultState<CreatedRoutine>

    suspend fun deleteRoutine(routineId: Long): ResultState<Unit>

    suspend fun getCategories(): ResultState<RoutineCategoryList>

    suspend fun createCategory(name: String, color: String?): ResultState<RoutineCategory>

    suspend fun updateCategory(
        categoryId: Long,
        name: String,
        color: String?,
    ): ResultState<RoutineCategory>

    suspend fun deleteCategory(categoryId: Long): ResultState<Unit>

    suspend fun getTemplates(categoryId: Long?): ResultState<List<RoutineTemplate>>

    suspend fun createRoutines(routines: List<CreateRoutineItem>): ResultState<CreateRoutinesResult>
}
