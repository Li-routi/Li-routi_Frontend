package com.li_routi.feature.grouproutine.navigation

import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.ui.Modifier
import androidx.compose.ui.tooling.preview.Preview
import androidx.navigation.NavHostController
import androidx.navigation.NavType
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import androidx.navigation.navArgument
import com.li_routi.core.common.ui.nav.AppBottomTab
import com.li_routi.core.designsystem.theme.LiroutiFrontendTheme
import com.li_routi.feature.grouproutine.screen.RoomListScreen

private const val RouteRoomList = "room_list"
private const val ArgRoomId = "roomId"
private const val RouteRoomDetail = "room_detail/{$ArgRoomId}"
private const val RouteCreateEntry = "group_create_entry"
private const val RouteJoinEntry = "group_join_entry"

private fun roomDetailRoute(roomId: String) = "room_detail/$roomId"

/**
 * "그룹 루틴" 탭의 진짜 루트 (Figma `05_모임_생성_참여_목록` / `06_모임방_상세...`).
 *
 * 모임 목록(ROOM_LIST) → 모임방 상세(ROOM_DETAIL)를 연결하고, 방 만들기/초대코드 참여는
 * 기존 [GrouproutineNavHost]를 그대로 내장해서 재사용한다.
 *
 * 방 목록 조회 API가 아직 없어서 항상 빈 리스트로 시작한다(ROOM_LIST가 빈 상태를 보여준다).
 * 방 만들기/참여를 마쳐도 실제로 목록에 반영할 방법이 없으므로, 완료 시엔 그냥 목록으로
 * 돌아간다 — 나머지는 TODO: 실제 API 연동.
 *
 * @param initialEntryPoint 홈 화면 `+` 메뉴("방 만들기"/"초대코드로 참여")에서 곧바로 넘어온 경우,
 * ROOM_LIST를 건너뛰고 해당 진입점 화면으로 바로 이동한다. 소비 후 [onInitialEntryPointConsumed]로
 * 알려서 재구성 시 같은 화면으로 다시 튀지 않게 한다.
 */
@Composable
fun GrouproutineRootNavHost(
    initialEntryPoint: GrouproutineEntryPoint?,
    onInitialEntryPointConsumed: () -> Unit,
    onTabSelected: (AppBottomTab) -> Unit,
    modifier: Modifier = Modifier,
    navController: NavHostController = rememberNavController(),
) {
    LaunchedEffect(initialEntryPoint) {
        when (initialEntryPoint) {
            GrouproutineEntryPoint.CreateRoom -> navController.navigate(RouteCreateEntry)
            GrouproutineEntryPoint.JoinWithInviteCode -> navController.navigate(RouteJoinEntry)
            null -> Unit
        }
        if (initialEntryPoint != null) onInitialEntryPointConsumed()
    }

    NavHost(
        navController = navController,
        startDestination = RouteRoomList,
        modifier = modifier,
    ) {
        composable(RouteRoomList) {
            RoomListScreen(
                rooms = emptyList(),
                onRoomClick = { roomId -> navController.navigate(roomDetailRoute(roomId)) },
                onCreateRoomClick = { navController.navigate(RouteCreateEntry) },
                onJoinRoomWithInviteCodeClick = { navController.navigate(RouteJoinEntry) },
                onTabSelected = onTabSelected,
            )
        }
        composable(
            route = RouteRoomDetail,
            arguments = listOf(navArgument(ArgRoomId) { type = NavType.StringType }),
        ) {
            // TODO: 방 목록 API 연동 후 실제 roomId로 조회. 지금은 목록이 항상 비어 있어
            // 이 화면에 도달할 방법이 없다(향후를 위한 스캐폴딩).
            navController.popBackStack()
        }
        composable(RouteCreateEntry) {
            GrouproutineNavHost(
                entryPoint = GrouproutineEntryPoint.CreateRoom,
                onRoomCreated = { navController.popBackStack() },
            )
        }
        composable(RouteJoinEntry) {
            GrouproutineNavHost(
                entryPoint = GrouproutineEntryPoint.JoinWithInviteCode,
                onJoinedRoom = { navController.popBackStack() },
            )
        }
    }
}

@Preview(showBackground = true, heightDp = 800)
@Composable
private fun GrouproutineRootNavHostPreview() {
    LiroutiFrontendTheme {
        GrouproutineRootNavHost(
            initialEntryPoint = null,
            onInitialEntryPointConsumed = {},
            onTabSelected = {},
        )
    }
}
