package com.li_routi.core.data.di

import com.li_routi.core.data.network.NetworkModule
import com.li_routi.core.data.repository.RoutineCatalogRepositoryImpl
import com.li_routi.core.data.repository.RoutineVerificationRepositoryImpl
import com.li_routi.core.domain.media.UploadMediaUseCase
import com.li_routi.core.domain.routine.CreateMemberRoutinesUseCase
import com.li_routi.core.domain.routine.CreateRoutineCategoryUseCase
import com.li_routi.core.domain.routine.DeleteMemberRoutineUseCase
import com.li_routi.core.domain.routine.DeleteRoutineCategoryUseCase
import com.li_routi.core.domain.routine.GetMemberRoutinesUseCase
import com.li_routi.core.domain.routine.GetRoutineCategoriesUseCase
import com.li_routi.core.domain.routine.GetRoutineTemplatesUseCase
import com.li_routi.core.domain.routine.RoutineCatalogRepository
import com.li_routi.core.domain.routine.RoutineVerificationRepository
import com.li_routi.core.domain.routine.ReverifyGroupRoutineUseCase
import com.li_routi.core.domain.routine.SubmitGroupRoutineAuthUseCase
import com.li_routi.core.domain.routine.SubmitMemberRoutineAuthUseCase
import com.li_routi.core.domain.routine.SubmitRoutineAuthUseCase
import com.li_routi.core.domain.routine.UpdateMemberRoutineUseCase
import com.li_routi.core.domain.routine.UpdateRoutineCategoryUseCase
import com.li_routi.core.domain.routine.VerifyGroupRoutineUseCase
import com.li_routi.core.domain.routine.VerifyMemberRoutineUseCase

/**
 * 루틴 인증·카탈로그(목록/카테고리/템플릿/생성·수정·삭제) UseCase 수동 구성 root.
 */
object RoutineContainer {

    private val verificationRepository: RoutineVerificationRepository by lazy {
        RoutineVerificationRepositoryImpl(NetworkModule.routineApiService)
    }

    private val catalogRepository: RoutineCatalogRepository by lazy {
        RoutineCatalogRepositoryImpl(NetworkModule.routineApiService)
    }

    private val verifyMemberRoutineUseCase: VerifyMemberRoutineUseCase by lazy {
        VerifyMemberRoutineUseCase(verificationRepository)
    }

    private val verifyGroupRoutineUseCase: VerifyGroupRoutineUseCase by lazy {
        VerifyGroupRoutineUseCase(verificationRepository)
    }

    private val reverifyGroupRoutineUseCase: ReverifyGroupRoutineUseCase by lazy {
        ReverifyGroupRoutineUseCase(verificationRepository)
    }

    private val uploadMediaUseCase: UploadMediaUseCase
        get() = MediaContainer.uploadMediaUseCase

    private val submitMemberRoutineAuthUseCase: SubmitMemberRoutineAuthUseCase by lazy {
        SubmitMemberRoutineAuthUseCase(
            uploadMediaUseCase = uploadMediaUseCase,
            verifyMemberRoutineUseCase = verifyMemberRoutineUseCase,
        )
    }

    private val submitGroupRoutineAuthUseCase: SubmitGroupRoutineAuthUseCase by lazy {
        SubmitGroupRoutineAuthUseCase(
            uploadMediaUseCase = uploadMediaUseCase,
            verifyGroupRoutineUseCase = verifyGroupRoutineUseCase,
            reverifyGroupRoutineUseCase = reverifyGroupRoutineUseCase,
        )
    }

    val submitRoutineAuthUseCase: SubmitRoutineAuthUseCase by lazy {
        SubmitRoutineAuthUseCase(
            submitMemberRoutineAuthUseCase = submitMemberRoutineAuthUseCase,
            submitGroupRoutineAuthUseCase = submitGroupRoutineAuthUseCase,
        )
    }

    val getMemberRoutinesUseCase: GetMemberRoutinesUseCase by lazy {
        GetMemberRoutinesUseCase(catalogRepository)
    }

    val getRoutineCategoriesUseCase: GetRoutineCategoriesUseCase by lazy {
        GetRoutineCategoriesUseCase(catalogRepository)
    }

    val createRoutineCategoryUseCase: CreateRoutineCategoryUseCase by lazy {
        CreateRoutineCategoryUseCase(catalogRepository)
    }

    val getRoutineTemplatesUseCase: GetRoutineTemplatesUseCase by lazy {
        GetRoutineTemplatesUseCase(catalogRepository)
    }

    val createMemberRoutinesUseCase: CreateMemberRoutinesUseCase by lazy {
        CreateMemberRoutinesUseCase(catalogRepository)
    }

    val updateMemberRoutineUseCase: UpdateMemberRoutineUseCase by lazy {
        UpdateMemberRoutineUseCase(catalogRepository)
    }

    val deleteMemberRoutineUseCase: DeleteMemberRoutineUseCase by lazy {
        DeleteMemberRoutineUseCase(catalogRepository)
    }

    val updateRoutineCategoryUseCase: UpdateRoutineCategoryUseCase by lazy {
        UpdateRoutineCategoryUseCase(catalogRepository)
    }

    val deleteRoutineCategoryUseCase: DeleteRoutineCategoryUseCase by lazy {
        DeleteRoutineCategoryUseCase(catalogRepository)
    }
}
