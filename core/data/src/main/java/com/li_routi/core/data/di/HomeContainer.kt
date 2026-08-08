package com.li_routi.core.data.di

import com.li_routi.core.data.network.NetworkModule
import com.li_routi.core.data.repository.HomeRepositoryImpl
import com.li_routi.core.domain.home.GetHomeSummaryUseCase
import com.li_routi.core.domain.home.HomeRepository

/**
 * Hilt 등 DI 프레임워크가 붙기 전까지 사용하는 홈 수동 구성 root.
 * feature 모듈은 여기서 필요한 UseCase만 가져다 쓴다.
 */
object HomeContainer {

    private val repository: HomeRepository by lazy {
        HomeRepositoryImpl(NetworkModule.homeApiService)
    }

    val getHomeSummaryUseCase: GetHomeSummaryUseCase by lazy {
        GetHomeSummaryUseCase(repository)
    }
}
