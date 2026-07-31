package com.li_routi.core.data.di

import com.li_routi.core.data.network.NetworkModule
import com.li_routi.core.data.repository.GroupRoutineRepositoryImpl
import com.li_routi.core.domain.grouproutine.CreateGroupRoutineUseCase
import com.li_routi.core.domain.grouproutine.GetGroupInviteCodeUseCase
import com.li_routi.core.domain.grouproutine.GetTodayGroupRoutinesUseCase
import com.li_routi.core.domain.grouproutine.GroupRoutineRepository
import com.li_routi.core.domain.grouproutine.IssueGroupInviteCodeUseCase
import com.li_routi.core.domain.grouproutine.UpdateGroupRoutineUseCase

/**
 * Hilt 등 DI 프레임워크가 붙기 전까지 사용하는 수동 구성 root.
 * feature 모듈은 여기서 필요한 UseCase만 가져다 쓴다.
 */
object GroupRoutineContainer {

    private val repository: GroupRoutineRepository by lazy {
        GroupRoutineRepositoryImpl(NetworkModule.groupRoutineApiService)
    }

    val createGroupRoutineUseCase: CreateGroupRoutineUseCase by lazy {
        CreateGroupRoutineUseCase(repository)
    }

    val updateGroupRoutineUseCase: UpdateGroupRoutineUseCase by lazy {
        UpdateGroupRoutineUseCase(repository)
    }

    val getTodayGroupRoutinesUseCase: GetTodayGroupRoutinesUseCase by lazy {
        GetTodayGroupRoutinesUseCase(repository)
    }

    val issueGroupInviteCodeUseCase: IssueGroupInviteCodeUseCase by lazy {
        IssueGroupInviteCodeUseCase(repository)
    }

    val getGroupInviteCodeUseCase: GetGroupInviteCodeUseCase by lazy {
        GetGroupInviteCodeUseCase(repository)
    }
}
