package com.li_routi.core.data.home

import com.li_routi.core.domain.home.HomeSummary
import com.li_routi.core.domain.routine.RoutineCategoryList

/**
 * 로딩 화면(로그인 직후 또는 앱 재실행)에서 [HomeContentPrefetcher]가 미리 받아둔 홈 요약/카테고리를
 * 1회성으로 보관한다. 홈 ViewModel이 이 값을 소비하면 로딩 스피너 없이 바로 완성된 화면을 그릴 수
 * 있다. 한 번 읽으면 비워서, 다음에 홈 ViewModel이 다시 만들어질 때(탭 전환 등)는 옛 값을 재사용하지
 * 않고 정상적으로 새로 조회하게 한다.
 */
object HomeReadinessCache {
    private var summary: HomeSummary? = null
    private var categories: RoutineCategoryList? = null

    fun store(summary: HomeSummary, categories: RoutineCategoryList) {
        this.summary = summary
        this.categories = categories
    }

    fun consume(): Pair<HomeSummary, RoutineCategoryList>? {
        val cachedSummary = summary ?: return null
        val cachedCategories = categories ?: return null
        clear()
        return cachedSummary to cachedCategories
    }

    /** 로그아웃/탈퇴/세션 만료 시 [com.li_routi.core.data.profile.MemberProfileCache.clear]와 같이 호출한다. */
    fun clear() {
        summary = null
        categories = null
    }
}
