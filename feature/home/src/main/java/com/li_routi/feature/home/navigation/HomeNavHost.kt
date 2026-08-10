package com.li_routi.feature.home.navigation

import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.ui.Modifier
import androidx.compose.ui.tooling.preview.Preview
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.navigation.NavHostController
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import com.li_routi.core.common.ui.nav.AppBottomTab
import com.li_routi.core.designsystem.theme.LiroutiFrontendTheme
import com.li_routi.feature.home.vm.HomeUiEvent
import com.li_routi.feature.home.vm.NotificationUiEvent
import com.li_routi.feature.shopping.navigation.ShoppingRoute

private const val RouteHomeMain = "home_main"
private const val RouteMyRoutine = "myRoutine"
private const val RouteRoutineManage = "routineManage"
private const val RouteNotification = "notification"
private const val RouteNotificationSettings = "notification_settings"
private const val RouteShop = "shop"

/** 루틴 관리 완료 후 홈 요약 재조회 요청 플래그 (SavedStateHandle). */
private const val KeyRefreshHome = "refresh_home"

/**
 * 홈 피처 내비게이션 그래프.
 *
 * 홈 화면 → "내 루틴" / 알림 / 상점가기 / `+` 메뉴의 "내 루틴 관리"까지는
 * 이 NavHost 안에서 자체 처리하고, 방 만들기·초대코드 참여(다른 feature)는
 * [onCreateRoomClick]/[onJoinRoomWithInviteCodeClick]로 호출부(앱 전체 내비게이션)에 위임한다.
 */
@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun HomeNavHost(
    onCreateRoomClick: () -> Unit = {},
    onJoinRoomWithInviteCodeClick: () -> Unit = {},
    /** 체크리스트 카메라 아이콘 탭 — `app`이 소유한 공유 인증 플로우를 시작해 달라는 요청. null이면 특정 루틴 미리 선택 없음. */
    onStartVerification: (routineId: String?) -> Unit = {},
    /** 공유 인증 플로우에서 개인/그룹 루틴 인증이 성공할 때마다 증가한다 — 홈 요약을 다시 불러온다. */
    verificationRefreshSignal: Int = 0,
    onTabSelected: (AppBottomTab) -> Unit = {},
    modifier: Modifier = Modifier,
    navController: NavHostController = rememberNavController(),
) {
    NavHost(
        navController = navController,
        startDestination = RouteHomeMain,
        modifier = modifier,
    ) {
        composable(RouteHomeMain) { entry ->
            val refreshHomeFromNav by entry.savedStateHandle
                .getStateFlow(KeyRefreshHome, false)
                .collectAsStateWithLifecycle()
            var refreshHomeFromVerification by remember { mutableStateOf(false) }
            LaunchedEffect(verificationRefreshSignal) {
                if (verificationRefreshSignal > 0) refreshHomeFromVerification = true
            }
            val refreshHome = refreshHomeFromNav || refreshHomeFromVerification

            HomeRoute(
                onEvent = { event ->
                    when (event) {
                        HomeUiEvent.NavigateToMyRoutine -> navController.navigate(RouteMyRoutine)
                        HomeUiEvent.NavigateToManageMyRoutine -> navController.navigate(RouteRoutineManage)
                        HomeUiEvent.NavigateToCreateRoom -> onCreateRoomClick()
                        HomeUiEvent.NavigateToJoinRoomWithInviteCode -> onJoinRoomWithInviteCodeClick()
                        HomeUiEvent.NavigateToNotification -> navController.navigate(RouteNotification)
                        HomeUiEvent.NavigateToShop -> navController.navigate(RouteShop)
                        HomeUiEvent.NavigateToRoutineAuthCamera -> onStartVerification(null)
                        is HomeUiEvent.NavigateToRoutineAuthCameraWithId ->
                            onStartVerification(event.routineId)
                        HomeUiEvent.CategoryCreated,
                        is HomeUiEvent.CategoryCreateFailed,
                        HomeUiEvent.CategoryUpdated,
                        is HomeUiEvent.CategoryUpdateFailed,
                        HomeUiEvent.CategoryDeleted,
                        is HomeUiEvent.CategoryDeleteFailed,
                        -> Unit
                    }
                },
                onTabSelected = onTabSelected,
                requestRefresh = refreshHome,
                onRefreshHandled = {
                    entry.savedStateHandle[KeyRefreshHome] = false
                    refreshHomeFromVerification = false
                },
            )
        }

        composable(RouteNotification) {
            NotificationRoute(
                onEvent = { event ->
                    when (event) {
                        NotificationUiEvent.NavigateBack -> navController.popBackStack()
                        NotificationUiEvent.NavigateToSettings -> {
                            navController.navigate(RouteNotificationSettings)
                        }
                    }
                },
            )
        }

        composable(RouteNotificationSettings) {
            NotificationSettingsRoute(
                onNavigateBack = { navController.popBackStack() },
            )
        }

        composable(RouteMyRoutine) {
            MyRoutineRoute(
                onNavigateBack = { navController.popBackStack() },
                onNavigateToAddRoutine = { navController.navigate(RouteRoutineManage) },
                onRoutinesChanged = {
                    navController.getBackStackEntry(RouteHomeMain)
                        .savedStateHandle[KeyRefreshHome] = true
                },
            )
        }

        composable(RouteRoutineManage) {
            RoutineManageRoute(
                onNavigateBack = { navController.popBackStack() },
                onSubmitSuccess = {
                    navController.getBackStackEntry(RouteHomeMain)
                        .savedStateHandle[KeyRefreshHome] = true
                    navController.popBackStack()
                },
            )
        }

        composable(RouteShop) {
            ShoppingRoute(
                onNavigateBack = { navController.popBackStack() },
            )
        }
    }
}

@Preview(showBackground = true, heightDp = 800)
@Composable
private fun HomeNavHostPreview() {
    LiroutiFrontendTheme {
        HomeNavHost()
    }
}
