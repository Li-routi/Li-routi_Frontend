package com.li_routi.core.data.di

import com.li_routi.core.data.network.NetworkModule
import com.li_routi.core.data.repository.MediaRepositoryImpl
import com.li_routi.core.domain.media.MediaRepository
import com.li_routi.core.domain.media.UploadMediaUseCase

/**
 * Hilt 등 DI 프레임워크가 붙기 전까지 사용하는 미디어 수동 구성 root.
 */
object MediaContainer {

    private val repository: MediaRepository by lazy {
        MediaRepositoryImpl(
            api = NetworkModule.mediaApiService,
            s3Client = NetworkModule.s3OkHttpClient,
        )
    }

    val uploadMediaUseCase: UploadMediaUseCase by lazy {
        UploadMediaUseCase(repository)
    }
}
