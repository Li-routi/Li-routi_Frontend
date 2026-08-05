package com.cmc.li_routi_frontend.navigation

import android.app.Activity
import android.widget.Toast
import androidx.activity.compose.BackHandler
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.mutableLongStateOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.saveable.rememberSaveableStateHolder
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import com.li_routi.core.common.ui.nav.AppBottomTab
import com.li_routi.feature.challenge.navigation.ChallengeNavHost
import com.li_routi.feature.grouproutine.navigation.GrouproutineEntryPoint
import com.li_routi.feature.grouproutine.navigation.GrouproutineRootNavHost
import com.li_routi.feature.home.navigation.HomeNavHost
import com.li_routi.feature.mypage.navigation.MyPageRoute

private const val DoubleBackPressIntervalMillis = 2000L

/**
 * 앱 전체 최상위 내비게이션 그래프.
 *
 * 홈/그룹 루틴/챌린지/마이 4탭을 여기서 직접 스위칭한다 — feature는 자기 화면만 노출하고,
 * 탭 전환 자체는 각 feature의 루트 화면이 공용 하단 GNB(`AppBottomNavBar`)를 통해 이리로 위임한다.
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

    // 탭별로 몇 번 리셋됐는지 세는 값. SaveableStateProvider의 key에 섞어 넣어서,
    // 이미 선택돼 있는 탭을 다시 눌러도(= selectedTab 값 자체는 안 바뀌어도) key가 바뀌어
    // 그 탭의 내용이 강제로 새로 생성되도록 한다.
    var homeResetGen by rememberSaveable { mutableIntStateOf(0) }
    var groupRoutineResetGen by rememberSaveable { mutableIntStateOf(0) }
    var challengeResetGen by rememberSaveable { mutableIntStateOf(0) }
    var myResetGen by rememberSaveable { mutableIntStateOf(0) }

    fun resetGenOf(tab: AppBottomTab) = when (tab) {
        AppBottomTab.Home -> homeResetGen
        AppBottomTab.GroupRoutine -> groupRoutineResetGen
        AppBottomTab.Challenge -> challengeResetGen
        AppBottomTab.My -> myResetGen
    }

    fun bumpResetGenOf(tab: AppBottomTab) {
        when (tab) {
            AppBottomTab.Home -> homeResetGen++
            AppBottomTab.GroupRoutine -> groupRoutineResetGen++
            AppBottomTab.Challenge -> challengeResetGen++
            AppBottomTab.My -> myResetGen++
        }
    }

    fun tabKey(tab: AppBottomTab) = "${tab.name}_${resetGenOf(tab)}"

    val context = LocalContext.current
    var lastBackPressedAt by remember { mutableLongStateOf(0L) }

    // 탭 전환은 NavController가 아닌 이 상태로만 이뤄지므로, 홈이 아닌 탭의 루트 화면에서는
    // 시스템 백버튼을 여기서 가로채 홈 탭으로 되돌린다.
    // 홈 탭에서는 2초 이내에 다시 누른 경우에만 종료하고, 그 외엔 토스트만 띄운다.
    BackHandler {
        if (selectedTab != AppBottomTab.Home) {
            selectedTab = AppBottomTab.Home
            return@BackHandler
        }

        val now = System.currentTimeMillis()
        if (now - lastBackPressedAt < DoubleBackPressIntervalMillis) {
            (context as? Activity)?.finish()
        } else {
            lastBackPressedAt = now
            Toast.makeText(context, "한 번 더 누르면 종료됩니다", Toast.LENGTH_SHORT).show()
        }
    }

    // 하단바 탭을 누르면(이미 선택돼 있던 탭을 다시 누른 경우 포함) 그 탭은 무조건
    // 시작 화면부터 다시 보여줘야 한다. 저장된 이전 상태(내부 NavController 백스택 등)를
    // 지우고, 리셋 카운터를 올려 key를 바꿔서 그 탭의 내용을 강제로 새로 만든다.
    fun selectTab(tab: AppBottomTab) {
        saveableStateHolder.removeState(tabKey(tab))
        bumpResetGenOf(tab)
        selectedTab = tab
    }

    Box(modifier = modifier) {
        when (selectedTab) {
            AppBottomTab.Home -> saveableStateHolder.SaveableStateProvider(tabKey(AppBottomTab.Home)) {
                HomeNavHost(
                    onCreateRoomClick = {
                        groupRoutineEntryPoint = GrouproutineEntryPoint.CreateRoom
                        selectTab(AppBottomTab.GroupRoutine)
                    },
                    onJoinRoomWithInviteCodeClick = {
                        groupRoutineEntryPoint = GrouproutineEntryPoint.JoinWithInviteCode
                        selectTab(AppBottomTab.GroupRoutine)
                    },
                    onTabSelected = ::selectTab,
                    modifier = Modifier.fillMaxSize(),
                )
            }

            AppBottomTab.GroupRoutine -> saveableStateHolder.SaveableStateProvider(tabKey(AppBottomTab.GroupRoutine)) {
                GrouproutineRootNavHost(
                    initialEntryPoint = groupRoutineEntryPoint,
                    onInitialEntryPointConsumed = { groupRoutineEntryPoint = null },
                    onTabSelected = ::selectTab,
                    modifier = Modifier.fillMaxSize(),
                )
            }

            AppBottomTab.Challenge -> saveableStateHolder.SaveableStateProvider(tabKey(AppBottomTab.Challenge)) {
                ChallengeNavHost(
                    onTabSelected = ::selectTab,
                    modifier = Modifier.fillMaxSize(),
                )
            }

            AppBottomTab.My -> saveableStateHolder.SaveableStateProvider(tabKey(AppBottomTab.My)) {
                MyPageRoute(
                    onTabSelected = ::selectTab,
                    modifier = Modifier.fillMaxSize(),
                )
            }
        }
    }
}
