package com.li_routi.core.domain.home

import com.li_routi.core.common.kotlin.util.ResultState

interface HomeRepository {

    /** 로그인 회원의 홈 화면 요약(닉네임, 오늘 개인/그룹 루틴)을 조회한다. */
    suspend fun getHomeSummary(): ResultState<HomeSummary>
}
