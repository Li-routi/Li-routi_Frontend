package com.li_routi.core.data.di

import com.li_routi.core.data.network.NetworkModule
import com.li_routi.core.data.repository.ChallengeRepositoryImpl
import com.li_routi.core.domain.challenge.ChallengeRepository
import com.li_routi.core.domain.challenge.GetChallengeDetailUseCase
import com.li_routi.core.domain.challenge.GetChallengesUseCase
import com.li_routi.core.domain.challenge.GetMyChallengesUseCase
import com.li_routi.core.domain.challenge.GetMyVerificationsUseCase
import com.li_routi.core.domain.challenge.GetVerificationsUseCase
import com.li_routi.core.domain.challenge.LeaveChallengeUseCase
import com.li_routi.core.domain.challenge.LikeVerificationUseCase
import com.li_routi.core.domain.challenge.ParticipateChallengeUseCase
import com.li_routi.core.domain.challenge.ReportVerificationUseCase
import com.li_routi.core.domain.challenge.UnlikeVerificationUseCase

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

    val getChallengeDetailUseCase: GetChallengeDetailUseCase by lazy {
        GetChallengeDetailUseCase(repository)
    }

    val getVerificationsUseCase: GetVerificationsUseCase by lazy {
        GetVerificationsUseCase(repository)
    }

    val participateChallengeUseCase: ParticipateChallengeUseCase by lazy {
        ParticipateChallengeUseCase(repository)
    }

    val leaveChallengeUseCase: LeaveChallengeUseCase by lazy {
        LeaveChallengeUseCase(repository)
    }

    val getMyChallengesUseCase: GetMyChallengesUseCase by lazy {
        GetMyChallengesUseCase(repository)
    }

    val getMyVerificationsUseCase: GetMyVerificationsUseCase by lazy {
        GetMyVerificationsUseCase(repository)
    }

    val reportVerificationUseCase: ReportVerificationUseCase by lazy {
        ReportVerificationUseCase(repository)
    }

    val likeVerificationUseCase: LikeVerificationUseCase by lazy {
        LikeVerificationUseCase(repository)
    }

    val unlikeVerificationUseCase: UnlikeVerificationUseCase by lazy {
        UnlikeVerificationUseCase(repository)
    }
}
