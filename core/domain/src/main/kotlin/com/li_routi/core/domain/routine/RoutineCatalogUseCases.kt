package com.li_routi.core.domain.routine

import com.li_routi.core.common.kotlin.util.ResultState

class GetRoutineCategoriesUseCase(
    private val repository: RoutineCatalogRepository,
) {
    suspend operator fun invoke(): ResultState<RoutineCategoryList> = repository.getCategories()
}

class CreateRoutineCategoryUseCase(
    private val repository: RoutineCatalogRepository,
) {
    suspend operator fun invoke(name: String, color: String?): ResultState<RoutineCategory> =
        repository.createCategory(name = name, color = color)
}

class GetRoutineTemplatesUseCase(
    private val repository: RoutineCatalogRepository,
) {
    suspend operator fun invoke(categoryId: Long? = null): ResultState<List<RoutineTemplate>> =
        repository.getTemplates(categoryId)
}

class CreateMemberRoutinesUseCase(
    private val repository: RoutineCatalogRepository,
) {
    suspend operator fun invoke(routines: List<CreateRoutineItem>): ResultState<CreateRoutinesResult> {
        if (routines.isEmpty()) {
            return ResultState.Error("추가할 루틴을 선택해 주세요.")
        }
        if (routines.size > 30) {
            return ResultState.Error("루틴은 최대 30개까지 등록할 수 있습니다.")
        }
        return repository.createRoutines(routines)
    }
}
