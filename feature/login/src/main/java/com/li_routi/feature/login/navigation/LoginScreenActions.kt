package com.li_routi.feature.login.navigation

import android.app.Activity
import android.content.Context

/**
 * `LoginScreen`에서 발생하는 사용자 이벤트에 대한 콜백 계약(contract).
 */
interface LoginScreenActions {
    /** "카카오로 시작하기" 버튼 탭 */
    fun onKakaoLoginClick(context: Context)

    /** "Google로 시작하기" 버튼 탭 (Credential Manager 호출에 Activity가 필요) */
    fun onGoogleLoginClick(activity: Activity)
}
