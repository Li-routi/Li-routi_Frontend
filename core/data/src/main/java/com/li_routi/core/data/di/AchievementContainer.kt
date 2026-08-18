package com.li_routi.core.data.di

import com.li_routi.core.data.network.NetworkModule
import com.li_routi.core.data.repository.AchievementRepositoryImpl
import com.li_routi.core.domain.achievement.AchievementRepository
import com.li_routi.core.domain.achievement.ClaimAchievementUseCase
import com.li_routi.core.domain.achievement.GetAchievementsUseCase
import com.li_routi.core.domain.achievement.GetWaveRoutineStatusUseCase
import com.li_routi.core.domain.achievement.SelectWaveRoutineUseCase

/**
 * Hilt 등 DI 프레임워크가 붙기 전까지 사용하는 수동 구성 root.
 * feature 모듈은 여기서 필요한 UseCase만 가져다 쓴다.
 */
object AchievementContainer {

    private val repository: AchievementRepository by lazy {
        AchievementRepositoryImpl(NetworkModule.achievementApiService)
    }

    val getAchievementsUseCase: GetAchievementsUseCase by lazy {
        GetAchievementsUseCase(repository)
    }

    val claimAchievementUseCase: ClaimAchievementUseCase by lazy {
        ClaimAchievementUseCase(repository)
    }

    val getWaveRoutineStatusUseCase: GetWaveRoutineStatusUseCase by lazy {
        GetWaveRoutineStatusUseCase(repository)
    }

    val selectWaveRoutineUseCase: SelectWaveRoutineUseCase by lazy {
        SelectWaveRoutineUseCase(repository)
    }
}
