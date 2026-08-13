package com.li_routi.feature.home.navigation

/** [HomeNavHost]가 어느 화면부터 시작할지 고르는 진입점. */
enum class HomeEntryPoint {
    /** 마이페이지 상단 바 알림벨 아이콘 — 알림 목록 화면. */
    Notification,

    /** 마이페이지 상단 바 설정 아이콘 — 알림 설정 화면. */
    NotificationSettings,
}
