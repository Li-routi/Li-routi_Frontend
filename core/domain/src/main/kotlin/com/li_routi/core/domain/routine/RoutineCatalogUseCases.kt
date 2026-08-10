package com.li_routi.core.domain.routine

import com.li_routi.core.common.kotlin.util.ResultState

class GetMemberRoutinesUseCase(
    private val repository: RoutineCatalogRepository,
) {
    suspend operator fun invoke(): ResultState<MemberRoutineList> = repository.getRoutines()
}

class GetRoutineCategoriesUseCase(
    private val repository: RoutineCatalogRepository,
) {
    suspend operator fun invoke(): ResultState<RoutineCategoryList> = repository.getCategories()
}

class CreateRoutineCategoryUseCase(
    private val repository: RoutineCatalogRepository,
) {
    suspend operator fun invoke(name: String, color: String?): ResultState<RoutineCategory> {
        val trimmed = name.trim()
        if (trimmed.isEmpty() || trimmed.length > 10 || '\n' in trimmed) {
            return ResultState.Error("카테고리 이름은 1~10자로 입력해 주세요.")
        }
        if (trimmed == "전체") {
            return ResultState.Error("「전체」는 사용할 수 없는 이름이에요.")
        }
        return repository.createCategory(name = trimmed, color = color)
    }
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

class UpdateMemberRoutineUseCase(
    private val repository: RoutineCatalogRepository,
) {
    suspend operator fun invoke(
        routineId: Long,
        update: UpdateMemberRoutine,
    ): ResultState<CreatedRoutine> {
        val name = update.name.trim()
        if (name.isEmpty() || name.length > 20 || '\n' in name) {
            return ResultState.Error("루틴 이름은 1~20자로 입력해 주세요.")
        }
        if (update.repeatDays.isEmpty()) {
            return ResultState.Error("반복 요일을 선택해 주세요.")
        }
        return repository.updateRoutine(
            routineId = routineId,
            update = update.copy(name = name),
        )
    }
}

class DeleteMemberRoutineUseCase(
    private val repository: RoutineCatalogRepository,
) {
    suspend operator fun invoke(routineId: Long): ResultState<Unit> =
        repository.deleteRoutine(routineId)
}

class UpdateRoutineCategoryUseCase(
    private val repository: RoutineCatalogRepository,
) {
    suspend operator fun invoke(
        categoryId: Long,
        name: String,
        color: String?,
    ): ResultState<RoutineCategory> {
        val trimmed = name.trim()
        if (trimmed.isEmpty() || trimmed.length > 10 || '\n' in trimmed) {
            return ResultState.Error("카테고리 이름은 1~10자로 입력해 주세요.")
        }
        return repository.updateCategory(categoryId = categoryId, name = trimmed, color = color)
    }
}

class DeleteRoutineCategoryUseCase(
    private val repository: RoutineCatalogRepository,
) {
    suspend operator fun invoke(categoryId: Long): ResultState<Unit> =
        repository.deleteCategory(categoryId)
}
