package com.li_routi.feature.home.navigation

import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
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
import com.li_routi.feature.home.shop.navigation.ShoppingRoute
import com.li_routi.feature.home.vm.HomeUiEvent
import com.li_routi.feature.home.vm.NotificationUiEvent

private const val RouteHomeMain = "home_main"
private const val RouteMyRoutine = "myRoutine"
private const val RouteRoutineManage = "routineManage"
private const val RouteNotification = "notification"
private const val RouteNotificationSettings = "notification_settings"
private const val RouteShop = "shop"

/** 루틴 관리 완료 후 홈 요약 재조회 요청 카운터 (SavedStateHandle). */
private const val KeyRefreshHome = "refresh_home_tick"

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
    /** 알림 탭에서 챌린지 관련 알림을 눌렀을 때 챌린지 탭(목록)으로 이동해 달라는 요청. */
    onNavigateToChallengeHome: () -> Unit = {},
    /** 알림 탭에서 챌린지 id를 특정할 수 있는 알림을 눌렀을 때 해당 상세로 이동해 달라는 요청. */
    onNavigateToChallengeDetail: (challengeId: Long) -> Unit = {},
    /** 다른 탭(마이페이지 알림벨/설정)에서 진입했을 때 바로 열어야 할 화면. null이면 홈 메인부터 시작. */
    initialEntryPoint: HomeEntryPoint? = null,
    /** [initialEntryPoint] 처리가 끝났음을 알리는 콜백 — 처리 후 값을 null로 되돌려 재진입 시 재실행을 막는다. */
    onInitialEntryPointConsumed: () -> Unit = {},
    /**
     * [initialEntryPoint]로 진입시킨 탭이 있으면 그 탭. null이면 홈 자신의 벨/설정 아이콘으로
     * 들어온 것이라 평소처럼 홈 메인으로 되돌아가면 된다.
     */
    entryPointOriginTab: AppBottomTab? = null,
    /** [entryPointOriginTab]가 있는 화면에서 뒤로가기 — 홈 메인이 아니라 원래 탭으로 돌려보내 달라는 요청. */
    onExitEntryPoint: () -> Unit = {},
    modifier: Modifier = Modifier,
    navController: NavHostController = rememberNavController(),
) {
    LaunchedEffect(initialEntryPoint) {
        when (initialEntryPoint) {
            HomeEntryPoint.Notification -> navController.navigate(RouteNotification)
            HomeEntryPoint.NotificationSettings -> navController.navigate(RouteNotificationSettings)
            null -> Unit
        }
        if (initialEntryPoint != null) onInitialEntryPointConsumed()
    }

    NavHost(
        navController = navController,
        startDestination = RouteHomeMain,
        modifier = modifier,
    ) {
        composable(RouteHomeMain) { entry ->
            val refreshHomeTick by entry.savedStateHandle
                .getStateFlow(KeyRefreshHome, 0)
                .collectAsStateWithLifecycle()
            // 두 카운터를 더해야 한쪽만 증가해도 LaunchedEffect 키가 바뀐다(maxOf는 유실 가능).
            val requestRefreshTick = refreshHomeTick + verificationRefreshSignal

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
                requestRefreshTick = requestRefreshTick,
            )
        }

        composable(RouteNotification) {
            NotificationRoute(
                onEvent = { event ->
                    when (event) {
                        // 이 화면은 항상 홈 메인 바로 위에서 시작하므로(entryPointOriginTab이 있든
                        // 없든), origin 탭이 있으면 거기로, 없으면(홈 자신의 벨 아이콘) 평소처럼
                        // 홈 메인으로 popBackStack.
                        NotificationUiEvent.NavigateBack -> {
                            if (entryPointOriginTab != null) onExitEntryPoint() else navController.popBackStack()
                        }
                        NotificationUiEvent.NavigateToSettings -> {
                            navController.navigate(RouteNotificationSettings)
                        }
                        NotificationUiEvent.NavigateToPersonalRoutine -> {
                            navController.popBackStack(RouteHomeMain, inclusive = false)
                        }
                        NotificationUiEvent.NavigateToGroupRoutine -> onTabSelected(AppBottomTab.GroupRoutine)
                        NotificationUiEvent.NavigateToChallengeHome -> onNavigateToChallengeHome()
                        is NotificationUiEvent.NavigateToChallengeDetail ->
                            onNavigateToChallengeDetail(event.challengeId)
                    }
                },
            )
        }

        composable(RouteNotificationSettings) {
            NotificationSettingsRoute(
                onNavigateBack = {
                    // 알림 목록을 거쳐 설정으로 들어온 경우("설정" 링크)엔 origin 탭과 무관하게 항상
                    // 목록으로 돌아간다. 목록 없이 설정으로 곧장 진입한 경우(마이페이지 설정 아이콘)만
                    // origin 탭으로 돌아간다.
                    val enteredDirectlyFromHomeMain =
                        navController.previousBackStackEntry?.destination?.route == RouteHomeMain
                    if (entryPointOriginTab != null && enteredDirectlyFromHomeMain) {
                        onExitEntryPoint()
                    } else {
                        navController.popBackStack()
                    }
                },
            )
        }

        composable(RouteMyRoutine) {
            MyRoutineRoute(
                onNavigateBack = { navController.popBackStack() },
                onNavigateToAddRoutine = { navController.navigate(RouteRoutineManage) },
                onRoutinesChanged = {
                    val handle = navController.getBackStackEntry(RouteHomeMain).savedStateHandle
                    handle[KeyRefreshHome] = (handle.get<Int>(KeyRefreshHome) ?: 0) + 1
                },
            )
        }

        composable(RouteRoutineManage) {
            RoutineManageRoute(
                onNavigateBack = { navController.popBackStack() },
                onSubmitSuccess = {
                    val handle = navController.getBackStackEntry(RouteHomeMain).savedStateHandle
                    handle[KeyRefreshHome] = (handle.get<Int>(KeyRefreshHome) ?: 0) + 1
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
