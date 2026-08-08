package com.li_routi.core.data.di

import com.li_routi.core.data.network.NetworkModule
import com.li_routi.core.data.preference.AuthTokenPreference
import com.li_routi.core.data.repository.AuthRepositoryImpl
import com.li_routi.core.domain.auth.AuthRepository
import com.li_routi.core.domain.auth.GetMyInfoUseCase
import com.li_routi.core.domain.auth.IssueGoogleNonceUseCase
import com.li_routi.core.domain.auth.LogoutUseCase
import com.li_routi.core.domain.auth.SocialLoginUseCase
import com.li_routi.core.domain.auth.UpdateProfileUseCase
import com.li_routi.core.domain.auth.WithdrawUseCase

/**
 * Hilt 등 DI 프레임워크가 붙기 전까지 사용하는 수동 구성 root.
 * feature 모듈은 여기서 필요한 UseCase만 가져다 쓴다.
 */
object AuthContainer {

    private val tokenPreference: AuthTokenPreference by lazy {
        AuthTokenPreference(NetworkModule.appContext)
    }

    private val repository: AuthRepository by lazy {
        AuthRepositoryImpl(NetworkModule.authApiService, tokenPreference)
    }

    val socialLoginUseCase: SocialLoginUseCase by lazy {
        SocialLoginUseCase(repository)
    }

    val issueGoogleNonceUseCase: IssueGoogleNonceUseCase by lazy {
        IssueGoogleNonceUseCase(repository)
    }

    val logoutUseCase: LogoutUseCase by lazy {
        LogoutUseCase(repository)
    }

    val getMyInfoUseCase: GetMyInfoUseCase by lazy {
        GetMyInfoUseCase(repository)
    }

    val updateProfileUseCase: UpdateProfileUseCase by lazy {
        UpdateProfileUseCase(repository)
    }

    val withdrawUseCase: WithdrawUseCase by lazy {
        WithdrawUseCase(repository)
    }
}
