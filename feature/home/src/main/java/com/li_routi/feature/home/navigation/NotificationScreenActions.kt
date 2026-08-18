package com.li_routi.feature.home.navigation

import com.li_routi.feature.home.vm.NotificationSettingKey

/**
 * 알림 목록 화면 액션.
 */
interface NotificationScreenActions {
    fun onBackClick()
    fun onSettingsClick()
    fun onTabSelected(index: Int)
    fun onNotificationClick(notificationId: String)
}

/**
 * 알림 설정 화면 액션.
 */
interface NotificationSettingsScreenActions {
    fun onBackClick()
    fun onSettingToggle(key: NotificationSettingKey, checked: Boolean)
}
