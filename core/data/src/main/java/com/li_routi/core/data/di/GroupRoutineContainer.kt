package com.li_routi.core.data.di

import com.li_routi.core.data.network.NetworkModule
import com.li_routi.core.data.repository.GroupRoutineRepositoryImpl
import com.li_routi.core.domain.grouproutine.CreateGroupRoutineCategoryUseCase
import com.li_routi.core.domain.grouproutine.CreateGroupRoutineUseCase
import com.li_routi.core.domain.grouproutine.CreateGroupUseCase
import com.li_routi.core.domain.grouproutine.DeleteGroupRoutineUseCase
import com.li_routi.core.domain.grouproutine.DeleteGroupUseCase
import com.li_routi.core.domain.grouproutine.GetGroupJoinPreviewUseCase
import com.li_routi.core.domain.grouproutine.GetGroupDetailUseCase
import com.li_routi.core.domain.grouproutine.GetGroupInviteCodeUseCase
import com.li_routi.core.domain.grouproutine.GetGroupRoutineCategoriesUseCase
import com.li_routi.core.domain.grouproutine.GetGroupRoutineVerificationsUseCase
import com.li_routi.core.domain.grouproutine.GetTodayGroupRoutinesUseCase
import com.li_routi.core.domain.grouproutine.GroupRoutineRepository
import com.li_routi.core.domain.grouproutine.JoinGroupUseCase
import com.li_routi.core.domain.grouproutine.KickGroupMemberUseCase
import com.li_routi.core.domain.grouproutine.LeaveGroupUseCase
import com.li_routi.core.domain.grouproutine.SetGroupLockUseCase
import com.li_routi.core.domain.grouproutine.UpdateGroupRoutineUseCase

/**
 * Hilt 등 DI 프레임워크가 붙기 전까지 사용하는 수동 구성 root.
 * feature 모듈은 여기서 필요한 UseCase만 가져다 씀.
 */
object GroupRoutineContainer {

    private val repository: GroupRoutineRepository by lazy {
        GroupRoutineRepositoryImpl(NetworkModule.groupRoutineApiService)
    }

    val createGroupUseCase: CreateGroupUseCase by lazy {
        CreateGroupUseCase(repository)
    }

    val createGroupRoutineUseCase: CreateGroupRoutineUseCase by lazy {
        CreateGroupRoutineUseCase(repository)
    }

    val updateGroupRoutineUseCase: UpdateGroupRoutineUseCase by lazy {
        UpdateGroupRoutineUseCase(repository)
    }

    val getGroupDetailUseCase: GetGroupDetailUseCase by lazy {
        GetGroupDetailUseCase(repository)
    }

    val deleteGroupUseCase: DeleteGroupUseCase by lazy {
        DeleteGroupUseCase(repository)
    }

    val leaveGroupUseCase: LeaveGroupUseCase by lazy {
        LeaveGroupUseCase(repository)
    }

    val joinGroupUseCase: JoinGroupUseCase by lazy {
        JoinGroupUseCase(repository)
    }

    val getGroupJoinPreviewUseCase: GetGroupJoinPreviewUseCase by lazy {
        GetGroupJoinPreviewUseCase(repository)
    }

    val setGroupLockUseCase: SetGroupLockUseCase by lazy {
        SetGroupLockUseCase(repository)
    }

    val kickGroupMemberUseCase: KickGroupMemberUseCase by lazy {
        KickGroupMemberUseCase(repository)
    }

    val deleteGroupRoutineUseCase: DeleteGroupRoutineUseCase by lazy {
        DeleteGroupRoutineUseCase(repository)
    }

    val getTodayGroupRoutinesUseCase: GetTodayGroupRoutinesUseCase by lazy {
        GetTodayGroupRoutinesUseCase(repository)
    }

    val getGroupInviteCodeUseCase: GetGroupInviteCodeUseCase by lazy {
        GetGroupInviteCodeUseCase(repository)
    }

    val getGroupRoutineCategoriesUseCase: GetGroupRoutineCategoriesUseCase by lazy {
        GetGroupRoutineCategoriesUseCase(repository)
    }

    val createGroupRoutineCategoryUseCase: CreateGroupRoutineCategoryUseCase by lazy {
        CreateGroupRoutineCategoryUseCase(repository)
    }

    val getGroupRoutineVerificationsUseCase: GetGroupRoutineVerificationsUseCase by lazy {
        GetGroupRoutineVerificationsUseCase(repository)
    }
}
