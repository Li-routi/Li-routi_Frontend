package com.li_routi.core.data.di

import com.li_routi.core.data.network.NetworkModule
import com.li_routi.core.data.repository.ChallengeRepositoryImpl
import com.li_routi.core.domain.challenge.ChallengeRepository
import com.li_routi.core.domain.challenge.GetChallengesUseCase

/**
 * Hilt 등 DI 프레임워크가 붙기 전까지 사용하는 수동 구성 root.
 * feature 모듈은 여기서 필요한 UseCase만 가져다 쓴다.
 */
object ChallengeContainer {

    private val repository: ChallengeRepository by lazy {
        ChallengeRepositoryImpl(NetworkModule.challengeApiService)
    }

    val getChallengesUseCase: GetChallengesUseCase by lazy {
        GetChallengesUseCase(repository)
    }
}
