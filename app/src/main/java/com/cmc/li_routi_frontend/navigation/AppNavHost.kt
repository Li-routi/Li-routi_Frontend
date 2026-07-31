package com.cmc.li_routi_frontend.navigation

import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.saveable.rememberSaveableStateHolder
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import com.li_routi.core.common.ui.nav.AppBottomTab
import com.li_routi.feature.challenge.navigation.ChallengeNavHost
import com.li_routi.feature.grouproutine.navigation.GrouproutineEntryPoint
import com.li_routi.feature.grouproutine.navigation.GrouproutineRootNavHost
import com.li_routi.feature.home.navigation.HomeNavHost
import com.li_routi.feature.mypage.navigation.MyPageRoute

/**
 * 앱 전체 최상위 내비게이션 그래프.
 *
 * 홈/그룹 루틴/챌린지/마이 4탭을 여기서 직접 스위칭한다 — feature는 자기 화면만 노출하고,
 * 탭 전환 자체는 각 feature의 루트 화면이 [AppBottomNavBar]를 통해 이리로 위임한다.
 * 홈 화면 "+" 메뉴의 "방 만들기"/"초대코드로 참여"는 그룹 루틴 탭으로 전환하면서
 * 해당 진입점으로 바로 들어가도록 [groupRoutineEntryPoint]로 넘긴다.
 *
 * 로그인 게이트는 `MainActivity`가 담당하므로(비로그인 시 `LoginActivity`로 리다이렉트) 여기 도달했다는 건
 * 이미 로그인된 상태라는 뜻이다. 탭 간 구분과 무관하게 시작 탭은 홈으로 고정한다.
 */
@Composable
fun AppNavHost(
    modifier: Modifier = Modifier,
) {
    var selectedTab by rememberSaveable { mutableStateOf(AppBottomTab.Home) }
    var groupRoutineEntryPoint by rememberSaveable { mutableStateOf<GrouproutineEntryPoint?>(null) }
    val saveableStateHolder = rememberSaveableStateHolder()

    Box(modifier = modifier) {
        when (selectedTab) {
            AppBottomTab.Home -> saveableStateHolder.SaveableStateProvider(AppBottomTab.Home.name) {
                HomeNavHost(
                    onCreateRoomClick = {
                        groupRoutineEntryPoint = GrouproutineEntryPoint.CreateRoom
                        selectedTab = AppBottomTab.GroupRoutine
                    },
                    onJoinRoomWithInviteCodeClick = {
                        groupRoutineEntryPoint = GrouproutineEntryPoint.JoinWithInviteCode
                        selectedTab = AppBottomTab.GroupRoutine
                    },
                    onTabSelected = { selectedTab = it },
                    modifier = Modifier.fillMaxSize(),
                )
            }

            AppBottomTab.GroupRoutine -> saveableStateHolder.SaveableStateProvider(AppBottomTab.GroupRoutine.name) {
                GrouproutineRootNavHost(
                    initialEntryPoint = groupRoutineEntryPoint,
                    onInitialEntryPointConsumed = { groupRoutineEntryPoint = null },
                    onTabSelected = { selectedTab = it },
                    modifier = Modifier.fillMaxSize(),
                )
            }

            AppBottomTab.Challenge -> saveableStateHolder.SaveableStateProvider(AppBottomTab.Challenge.name) {
                ChallengeNavHost(
                    onTabSelected = { selectedTab = it },
                    modifier = Modifier.fillMaxSize(),
                )
            }

            AppBottomTab.My -> saveableStateHolder.SaveableStateProvider(AppBottomTab.My.name) {
                MyPageRoute(
                    onTabSelected = { selectedTab = it },
                    modifier = Modifier.fillMaxSize(),
                )
            }
        }
    }
}
