package com.li_routi.core.data.notification

import kotlinx.coroutines.sync.Mutex
import kotlinx.coroutines.sync.withLock

/**
 * FCM 기기 등록/해제가 동시에  overlapping 되지 않도록 직렬화한다.
 * (로그인 중 등록이 로그아웃 해제 이후에 완료되어 토큰이 다시 살아나는 레이스를 막는다.)
 */
object FcmDeviceSyncGate {
    private val mutex = Mutex()

    suspend fun <T> withExclusive(block: suspend () -> T): T = mutex.withLock { block() }
}
