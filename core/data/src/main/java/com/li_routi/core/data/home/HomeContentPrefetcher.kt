package com.li_routi.core.data.home

import coil.Coil
import coil.request.ImageRequest
import com.li_routi.core.common.kotlin.util.ResultState
import com.li_routi.core.data.di.HomeContainer
import com.li_routi.core.data.di.NotificationContainer
import com.li_routi.core.data.di.RoutineContainer
import com.li_routi.core.data.di.ShopContainer
import com.li_routi.core.data.network.NetworkModule
import com.li_routi.core.data.preference.AuthTokenPreference
import com.li_routi.core.data.profile.MemberProfileCache
import kotlinx.coroutines.async
import kotlinx.coroutines.awaitAll
import kotlinx.coroutines.coroutineScope
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.withTimeoutOrNull

/**
 * 로그인 로딩 화면(신규가입/재로그인) 또는 토큰이 남은 채 앱을 재실행했을 때, 로딩 화면이 떠 있는
 * 동안 홈 화면이 필요로 하는 데이터·이미지를 뒤에서 미리 받아둔다.
 *
 * 홈 ViewModel이 만들어질 때 [HomeReadinessCache]를 먼저 확인해 이미 받아둔 값이 있으면 로딩
 * 스피너 없이 바로 완성된 화면을 그린다. 여기서 실패하거나 시간 안에 못 끝나도 조용히 넘어가고,
 * 부족한 부분은 홈 화면 자체의 재조회(refresh)가 다시 채우므로 실패를 별도로 알리지 않는다.
 */
object HomeContentPrefetcher {

    /** 프리페치가 너무 빨리 끝나도 로딩화면이 반짝하고 사라지지 않게 최소 이만큼은 띄워 둔다. */
    const val MinLoadingDurationMillis = 1200L

    /** 네트워크가 느려도 로딩화면이 무한정 떠 있지 않도록 이 시간이 지나면 포기하고 넘어간다. */
    const val MaxPrefetchWaitMillis = 8000L

    /** 최소 노출 시간과 프리페치 완료(또는 타임아웃) 중 더 늦게 끝나는 쪽까지 기다린다. */
    suspend fun prefetchWithMinDuration() {
        coroutineScope {
            val minDelay = async { delay(MinLoadingDurationMillis) }
            val prefetchJob = async { withTimeoutOrNull(MaxPrefetchWaitMillis) { prefetch() } }
            minDelay.await()
            prefetchJob.await()
        }
    }

    private suspend fun prefetch() {
        val token = AuthTokenPreference(NetworkModule.appContext).accessTokenFlow.first()
        if (token.isNullOrBlank()) return

        coroutineScope {
            val summaryDeferred = async { HomeContainer.getHomeSummaryUseCase() }
            val categoriesDeferred = async { RoutineContainer.getRoutineCategoriesUseCase() }
            val unreadDeferred = async { NotificationContainer.hasUnreadNotificationUseCase() }
            // MemberAppearanceStore는 이미 받아둔 게 있으면 네트워크를 다시 안 치므로 그냥 기다린다.
            ShopContainer.memberAppearanceStore.ensureLoaded()

            val summaryResult = summaryDeferred.await()
            val categoriesResult = categoriesDeferred.await()
            val unreadResult = unreadDeferred.await()

            if (summaryResult is ResultState.Success && categoriesResult is ResultState.Success) {
                HomeReadinessCache.store(summaryResult.data, categoriesResult.data)
                MemberProfileCache.nickname.value = summaryResult.data.userInfo.nickname
            }
            if (unreadResult is ResultState.Success) {
                MemberProfileCache.hasUnreadNotification.value = unreadResult.data
            }

            val appearance = ShopContainer.memberAppearanceStore.appearance.value
            MemberProfileCache.characterId.value = appearance.characterId
            MemberProfileCache.characterImageUrl.value = appearance.characterImageUrl
            MemberProfileCache.layers.value = appearance.layers

            val imageUrls = buildSet {
                appearance.characterImageUrl?.takeIf { it.isNotBlank() }?.let(::add)
                for (layer in appearance.layers) {
                    if (layer.imageUrl.isNotBlank()) add(layer.imageUrl)
                }
            }
            val imageLoader = Coil.imageLoader(NetworkModule.appContext)
            imageUrls.map { url ->
                async {
                    // 프리페치 실패(깨진 URL 등)로 전체 대기가 멈추면 안 되므로 개별 실패는 삼킨다.
                    runCatching {
                        imageLoader.execute(ImageRequest.Builder(NetworkModule.appContext).data(url).build())
                    }
                }
            }.awaitAll()
        }
    }
}
