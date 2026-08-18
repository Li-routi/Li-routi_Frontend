package com.li_routi.core.domain.achievement

import com.li_routi.core.common.kotlin.util.ResultState

class GetWaveRoutineStatusUseCase(
    private val repository: AchievementRepository,
) {
    suspend operator fun invoke(): ResultState<WaveRoutineStatus> = repository.getWaveRoutineStatus()
}

class SelectWaveRoutineUseCase(
    private val repository: AchievementRepository,
) {
    suspend operator fun invoke(memberRoutineId: Long): ResultState<Unit> =
        repository.selectWaveRoutine(memberRoutineId)
}
