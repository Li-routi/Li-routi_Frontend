package com.cmc.li_routi_frontend.navigation

import android.app.Activity
import android.net.Uri
import android.widget.Toast
import androidx.activity.compose.BackHandler
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.width
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.mutableLongStateOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.saveable.Saver
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.saveable.rememberSaveableStateHolder
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.input.pointer.pointerInput
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.unit.dp
import com.li_routi.core.common.kotlin.util.ResultState
import com.li_routi.core.common.ui.nav.AppBottomTab
import com.li_routi.core.common.ui.routine.CategoryColor
import com.li_routi.core.data.di.ChallengeContainer
import com.li_routi.core.data.di.GroupRoutineContainer
import com.li_routi.core.data.di.HomeContainer
import com.li_routi.core.designsystem.component.LiroutiPrimaryButton
import com.li_routi.core.designsystem.theme.LiroutiTheme
import com.li_routi.core.domain.grouproutine.GroupRoutineStatus
import com.li_routi.core.domain.grouproutine.TodayGroupRoutine
import com.li_routi.core.domain.notification.NotificationNavigationTarget
import com.li_routi.feature.challenge.navigation.ChallengeNavHost
import com.li_routi.feature.grouproutine.navigation.GrouproutineEntryPoint
import com.li_routi.feature.grouproutine.navigation.GrouproutineRootNavHost
import com.li_routi.feature.grouproutine.navigation.GroupRoutineVerificationTarget
import com.li_routi.feature.home.navigation.HomeEntryPoint
import com.li_routi.feature.home.navigation.HomeNavHost
import com.li_routi.feature.home.navigation.RoutineAuthCameraRoute
import com.li_routi.feature.home.navigation.RoutineAuthUploadRoute
import com.li_routi.feature.home.vm.ChallengeIdPrefix
import com.li_routi.feature.home.vm.RoutineAuthCameraUiEvent
import com.li_routi.feature.home.vm.RoutineAuthSelectableUiModel
import com.li_routi.feature.home.vm.RoutineAuthUploadUiEvent
import com.li_routi.feature.home.vm.formatRoutineTimeRange
import com.li_routi.feature.home.vm.toAuthSelectables
import com.li_routi.feature.home.vm.toHomeUiState
import com.li_routi.feature.mypage.navigation.MyPageRoute

private const val DoubleBackPressIntervalMillis = 2000L

private fun TodayGroupRoutine.toAuthSelectable() = RoutineAuthSelectableUiModel(
    id = "group_${groupId}_$routineId",
    title = title,
    dueLabel = formatRoutineTimeRange(scheduledStartTime, scheduledEndTime).takeIf { it.isNotBlank() },
    subtitle = groupName,
    categoryLabel = categoryName,
    categoryColor = CategoryColor.entries[
        (((categoryId % CategoryColor.entries.size) + CategoryColor.entries.size) % CategoryColor.entries.size).toInt()
    ],
    groupId = groupId,
    groupRoutineId = routineId,
)

private fun GroupRoutineVerificationTarget.toAuthSelectable() = RoutineAuthSelectableUiModel(
    id = "group_${groupId}_$routineId",
    title = title,
    dueLabel = formatRoutineTimeRange(startTime = null, endTime = deadline).takeIf { it.isNotBlank() },
    subtitle = roomName,
    categoryLabel = category,
    categoryColor = categoryColor,
    groupId = groupId,
    groupRoutineId = routineId,
    groupRoutineVerificationId = verificationId,
)

/** [Uri]는 Bundle에 바로 못 넣으므로 문자열로 저장/복원한다(구성 변경 후에도 촬영 사진 유지). */
private val NullableUriSaver = Saver<Uri?, String>(
    save = { uri -> uri?.toString().orEmpty() },
    restore = { saved -> saved.takeIf { it.isNotEmpty() }?.let(Uri::parse) },
)

/**
 * 앱 전체 최상위 내비게이션 그래프.
 *
 * 홈/그룹 루틴/챌린지/마이 4탭을 여기서 직접 스위칭한다 — feature는 자기 화면만 노출하고,
 * 탭 전환 자체는 각 feature의 루트 화면이 공용 하단 GNB(`AppBottomNavBar`)를 통해 이리로 위임한다.
 * 홈 화면 "+" 메뉴의 "방 만들기"/"초대코드로 참여"는 그룹 루틴 탭으로 전환하면서
 * 해당 진입점으로 바로 들어가도록 [groupRoutineEntryPoint]로 넘긴다.
 *
 * "인증하기"(카메라 → 메모/루틴 선택)도 같은 방식으로 여기서 소유한다 — 개인 루틴(홈 체크리스트
 * 카메라 아이콘)이든 그룹 루틴(홈 체크리스트에 같이 표시됨)이든 챌린지(챌린지 상세 "인증하기")든
 * 전부 이 오버레이로 모인다. 오버레이가 떠 있는 동안 [selectedTab]은 건드리지 않으므로, 오버레이를
 * 닫으면 자동으로 "시작했던 화면"이 그대로 남아 있다 — 별도의 origin 추적이 필요 없다.
 *
 * 로그인 게이트는 `MainActivity`가 담당하므로(비로그인 시 `LoginActivity`로 리다이렉트) 여기 도달했다는 건
 * 이미 로그인된 상태라는 뜻이다. 탭 간 구분과 무관하게 시작 탭은 홈으로 고정한다.
 */
@Composable
fun AppNavHost(
    modifier: Modifier = Modifier,
    /** FCM 푸시를 탭해서 앱을 열었을 때 이동해야 할 화면. [MainActivity]가 인텐트에서 해석해 넘긴다. */
    pendingNotificationTarget: NotificationNavigationTarget? = null,
    /** 같은 [pendingNotificationTarget] 값이 연속으로 와도(예: 같은 타입 알림 재수신) 매번 새로 처리하기 위한 토큰. */
    pendingNotificationToken: Long = 0L,
    onPendingNotificationConsumed: () -> Unit = {},
) {
    var selectedTab by rememberSaveable { mutableStateOf(AppBottomTab.Home) }
    var homeEntryPoint by rememberSaveable { mutableStateOf<HomeEntryPoint?>(null) }
    // 마이페이지 알림벨/설정처럼 다른 탭에서 홈의 화면으로 진입시킨 경우, 그 화면에서 뒤로가기를
    // 누르면 홈 메인이 아니라 원래 탭으로 돌아가야 한다 — homeEntryPoint는 진입 직후 바로 비워지므로
    // (재진입 시 재실행 방지) 뒤로가기 시점까지 따로 기억해둔다.
    var homeEntryPointOriginTab by rememberSaveable { mutableStateOf<AppBottomTab?>(null) }
    var groupRoutineEntryPoint by rememberSaveable { mutableStateOf<GrouproutineEntryPoint?>(null) }
    var challengeDetailEntryPoint by rememberSaveable { mutableStateOf<Long?>(null) }
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

    // 알림을 눌러 앱에 진입한 경우 해당 탭으로 전환한다. 챌린지 id가 확실한 알림이면
    // 챌린지 탭 전환과 동시에 상세 화면까지 바로 연다(challengeDetailEntryPoint).
    LaunchedEffect(pendingNotificationToken) {
        val target = pendingNotificationTarget ?: return@LaunchedEffect
        when (target) {
            NotificationNavigationTarget.PersonalRoutine -> selectTab(AppBottomTab.Home)
            NotificationNavigationTarget.GroupRoutine -> selectTab(AppBottomTab.GroupRoutine)
            NotificationNavigationTarget.ChallengeHome -> selectTab(AppBottomTab.Challenge)
            is NotificationNavigationTarget.ChallengeDetail -> {
                challengeDetailEntryPoint = target.challengeId
                selectTab(AppBottomTab.Challenge)
            }
        }
        onPendingNotificationConsumed()
    }

    // ---- 공유 인증 플로우(카메라 → 메모/루틴 선택) ----
    // capturedVerificationPhotoUri처럼 rememberSaveable을 써야 구성 변경(회전 등) 후에도
    // 오버레이가 닫히지 않고 유지된다.
    var showVerificationFlow by rememberSaveable { mutableStateOf(false) }
    var verificationPreselectedId by rememberSaveable { mutableStateOf<String?>(null) }
    var verificationPreselectedRoutine by remember {
        mutableStateOf<RoutineAuthSelectableUiModel?>(null)
    }
    var capturedVerificationPhotoUri by rememberSaveable(stateSaver = NullableUriSaver) {
        mutableStateOf(null)
    }
    var verificationRoutines by remember { mutableStateOf<List<RoutineAuthSelectableUiModel>>(emptyList()) }
    var isLoadingVerificationRoutines by remember { mutableStateOf(false) }
    var verificationLoadError by remember { mutableStateOf(false) }
    var verificationLoadRetryTick by remember { mutableIntStateOf(0) }
    var homeRefreshSignal by remember { mutableIntStateOf(0) }
    var challengeRefreshSignal by remember { mutableIntStateOf(0) }
    var groupRoutineRefreshSignal by remember { mutableIntStateOf(0) }
    var verifiedGroupRoutineIdsByGroup by remember {
        mutableStateOf<Map<Long, Set<Long>>>(emptyMap())
    }
    var isGroupRoutineVerification by rememberSaveable { mutableStateOf(false) }

    fun startVerificationFlow(
        preselectedId: String?,
        preselectedRoutine: RoutineAuthSelectableUiModel? = null,
        isGroupRoutine: Boolean = false,
    ) {
        verificationPreselectedId = preselectedId
        verificationPreselectedRoutine = preselectedRoutine
        isGroupRoutineVerification = isGroupRoutine
        capturedVerificationPhotoUri = null
        showVerificationFlow = true
    }

    fun closeVerificationFlow() {
        showVerificationFlow = false
        capturedVerificationPhotoUri = null
        verificationPreselectedId = null
        verificationPreselectedRoutine = null
        isGroupRoutineVerification = false
    }

    // 개인 루틴 + 그룹 루틴(홈 요약) + 참여 중인 챌린지를 한 목록으로 합친다. 플로우가 열릴 때마다
    // 새로 불러와서(선택 화면을 여는 시점 기준) 최신 상태를 반영한다. 둘 중 하나라도 실패하면 빈
    // 목록으로 조용히 넘어가지 않고 에러 상태로 남겨 재시도 UI를 보여준다.
    LaunchedEffect(showVerificationFlow, verificationLoadRetryTick) {
        if (!showVerificationFlow) return@LaunchedEffect
        isLoadingVerificationRoutines = true
        verificationLoadError = false
        val homeResult = HomeContainer.getHomeSummaryUseCase()
        val challengesResult = ChallengeContainer.getMyChallengesUseCase()
        val isGroupVerification = verificationPreselectedId?.startsWith("group_") == true
        val groupRoutinesResult = if (isGroupVerification) {
            GroupRoutineContainer.getTodayGroupRoutinesUseCase()
        } else {
            null
        }
        if (
            homeResult !is ResultState.Success ||
            challengesResult !is ResultState.Success ||
            (isGroupVerification && groupRoutinesResult !is ResultState.Success)
        ) {
            verificationLoadError = true
            isLoadingVerificationRoutines = false
            return@LaunchedEffect
        }
        val homeItems = homeResult.data.toHomeUiState()
            .let { it.myRoutineItems + it.groupRoomItems }
            .toAuthSelectables()
        val groupItems = (groupRoutinesResult as? ResultState.Success)
            ?.data
            .orEmpty()
            .filter { it.status == GroupRoutineStatus.PENDING || it.status == GroupRoutineStatus.IN_PROGRESS }
            .map(TodayGroupRoutine::toAuthSelectable)
        val challengeItems = challengesResult.data.toAuthSelectables()
        verificationRoutines = (
            listOfNotNull(verificationPreselectedRoutine) + groupItems + homeItems + challengeItems
        ).distinctBy { it.id }
        isLoadingVerificationRoutines = false
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
                    onStartVerification = { routineId ->
                        startVerificationFlow(
                            preselectedId = routineId,
                            isGroupRoutine = routineId?.startsWith("group_") == true,
                        )
                    },
                    verificationRefreshSignal = homeRefreshSignal,
                    onTabSelected = ::selectTab,
                    onNavigateToChallengeHome = { selectTab(AppBottomTab.Challenge) },
                    onNavigateToChallengeDetail = { challengeId ->
                        challengeDetailEntryPoint = challengeId
                        selectTab(AppBottomTab.Challenge)
                    },
                    initialEntryPoint = homeEntryPoint,
                    onInitialEntryPointConsumed = { homeEntryPoint = null },
                    entryPointOriginTab = homeEntryPointOriginTab,
                    onExitEntryPoint = {
                        val origin = homeEntryPointOriginTab
                        homeEntryPointOriginTab = null
                        if (origin != null) selectTab(origin)
                    },
                    modifier = Modifier.fillMaxSize(),
                )
            }

            AppBottomTab.GroupRoutine -> saveableStateHolder.SaveableStateProvider(tabKey(AppBottomTab.GroupRoutine)) {
                GrouproutineRootNavHost(
                    initialEntryPoint = groupRoutineEntryPoint,
                    onInitialEntryPointConsumed = { groupRoutineEntryPoint = null },
                    onTabSelected = ::selectTab,
                    onStartVerification = { target ->
                        val selectable = target.toAuthSelectable()
                        startVerificationFlow(
                            preselectedId = selectable.id,
                            preselectedRoutine = selectable,
                            isGroupRoutine = true,
                        )
                    },
                    verificationRefreshSignal = groupRoutineRefreshSignal,
                    verifiedRoutineIdsByGroup = verifiedGroupRoutineIdsByGroup,
                    modifier = Modifier.fillMaxSize(),
                )
            }

            AppBottomTab.Challenge -> saveableStateHolder.SaveableStateProvider(tabKey(AppBottomTab.Challenge)) {
                ChallengeNavHost(
                    onStartVerification = { challengeId ->
                        startVerificationFlow("$ChallengeIdPrefix$challengeId")
                    },
                    verificationRefreshSignal = challengeRefreshSignal,
                    onTabSelected = ::selectTab,
                    initialChallengeDetailId = challengeDetailEntryPoint,
                    onInitialChallengeDetailConsumed = { challengeDetailEntryPoint = null },
                    modifier = Modifier.fillMaxSize(),
                )
            }

            AppBottomTab.My -> saveableStateHolder.SaveableStateProvider(tabKey(AppBottomTab.My)) {
                MyPageRoute(
                    onTabSelected = ::selectTab,
                    refreshTick = myResetGen,
                    onNotificationClick = {
                        homeEntryPoint = HomeEntryPoint.Notification
                        homeEntryPointOriginTab = AppBottomTab.My
                        selectTab(AppBottomTab.Home)
                    },
                    onSettingsClick = {
                        homeEntryPoint = HomeEntryPoint.NotificationSettings
                        homeEntryPointOriginTab = AppBottomTab.My
                        selectTab(AppBottomTab.Home)
                    },
                    modifier = Modifier.fillMaxSize(),
                )
            }
        }

        if (showVerificationFlow) {
            val photoUri = capturedVerificationPhotoUri
            if (photoUri == null) {
                // 오버레이가 떠 있는 동안은 시스템 뒤로가기도 탭 전환이 아니라 이 플로우를 닫아야 한다.
                // BackHandler는 나중에 컴포지션된(더 안쪽) 콜백이 우선하므로, 상단의 탭 전환용
                // BackHandler(line 107)보다 이게 먼저 호출된다.
                BackHandler(onBack = ::closeVerificationFlow)
                RoutineAuthCameraRoute(
                    onEvent = { event ->
                        when (event) {
                            RoutineAuthCameraUiEvent.NavigateBack -> closeVerificationFlow()
                            is RoutineAuthCameraUiEvent.NavigateToRoutineAuthUpload -> {
                                capturedVerificationPhotoUri = event.photoUri
                            }
                        }
                    },
                    modifier = Modifier.fillMaxSize(),
                )
            } else if (verificationLoadError) {
                Box(
                    modifier = Modifier
                        .fillMaxSize()
                        .background(LiroutiTheme.colors.backgroundDefault)
                        .pointerInput(Unit) { awaitPointerEventScope { while (true) awaitPointerEvent() } },
                    contentAlignment = Alignment.Center,
                ) {
                    Column(
                        horizontalAlignment = Alignment.CenterHorizontally,
                        verticalArrangement = Arrangement.spacedBy(16.dp),
                    ) {
                        Text(
                            text = "루틴 목록을 불러오지 못했어요.",
                            style = LiroutiTheme.typography.body1Medium,
                            color = LiroutiTheme.colors.labelStrong,
                        )
                        LiroutiPrimaryButton(
                            text = "다시 시도",
                            onClick = { verificationLoadRetryTick++ },
                            modifier = Modifier.width(160.dp),
                        )
                    }
                }
            } else if (isLoadingVerificationRoutines && verificationRoutines.isEmpty()) {
                Box(
                    modifier = Modifier
                        .fillMaxSize()
                        .background(LiroutiTheme.colors.backgroundDefault)
                        .pointerInput(Unit) { awaitPointerEventScope { while (true) awaitPointerEvent() } },
                    contentAlignment = Alignment.Center,
                ) {
                    CircularProgressIndicator(color = LiroutiTheme.colors.primaryNormal)
                }
            } else {
                val preselected = verificationPreselectedId?.let { setOf(it) }.orEmpty()
                RoutineAuthUploadRoute(
                    photoUri = photoUri,
                    initialSelectedRoutineIds = preselected,
                    routines = verificationRoutines,
                    onEvent = { event ->
                        when (event) {
                            RoutineAuthUploadUiEvent.NavigateBack -> {
                                capturedVerificationPhotoUri = null
                            }
                            RoutineAuthUploadUiEvent.NavigateClose -> closeVerificationFlow()
                            is RoutineAuthUploadUiEvent.NavigateToHome -> {
                                val shouldReturnToGroupRoutine = isGroupRoutineVerification
                                val selectedRoutineIds = event.selectedRoutineIds.ifEmpty {
                                    verificationPreselectedId?.let { setOf(it) }.orEmpty()
                                }
                                val completedGroupRoutineIdsByGroup = if (shouldReturnToGroupRoutine) {
                                    selectedRoutineIds
                                        .mapNotNull { selectedId ->
                                            val routine = verificationRoutines.firstOrNull { it.id == selectedId }
                                            val groupId = routine?.groupId
                                            val routineId = routine?.groupRoutineId
                                            if (groupId != null && routineId != null) {
                                                groupId to routineId
                                            } else {
                                                null
                                            }
                                        }
                                        .groupBy(
                                            keySelector = { it.first },
                                            valueTransform = { it.second },
                                        )
                                        .mapValues { (_, routineIds) -> routineIds.toSet() }
                                } else {
                                    emptyMap()
                                }
                                closeVerificationFlow()
                                homeRefreshSignal++
                                challengeRefreshSignal++
                                verifiedGroupRoutineIdsByGroup = completedGroupRoutineIdsByGroup
                                groupRoutineRefreshSignal++
                                if (shouldReturnToGroupRoutine) {
                                    selectedTab = AppBottomTab.GroupRoutine
                                }
                            }
                        }
                    },
                    modifier = Modifier.fillMaxSize(),
                )
            }
        }
    }
}
