package com.li_routi.feature.challenge.navigation

import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.tooling.preview.Preview
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.compose.LocalLifecycleOwner
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.lifecycle.repeatOnLifecycle
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.NavHostController
import androidx.navigation.NavType
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import androidx.navigation.navArgument
import com.li_routi.core.common.ui.nav.AppBottomTab
import com.li_routi.core.data.di.ChallengeContainer
import com.li_routi.core.designsystem.theme.LiroutiFrontendTheme
import com.li_routi.feature.challenge.screen.ChallengeDetailScreen
import com.li_routi.feature.challenge.screen.ChallengeScreen
import com.li_routi.feature.challenge.screen.FindChallengeScreen
import com.li_routi.feature.challenge.vm.ChallengeDetailViewModel
import com.li_routi.feature.challenge.vm.ChallengeViewModel
import com.li_routi.feature.challenge.vm.FindChallengeViewModel

private const val RouteChallengeHome = "challenge_home"
private const val RouteFindChallenge = "challenge_find"
private const val ArgChallengeId = "challengeId"
private const val RouteChallengeDetail = "challenge_detail/{$ArgChallengeId}"

/**
 * 챌린지 피처 내비게이션 그래프.
 *
 * "챌린지 없음(디폴트)" 화면 → "챌린지 찾아보기" 화면 → 카드 탭 → 챌린지 상세 화면,
 * 3단계 전환까지 연결한다. 상세 화면 자체는 아직 빈 화면(id만 표시)이다.
 */
@Composable
fun ChallengeNavHost(
    /** "인증하기" 탭 — `app`이 소유한 공유 인증 플로우를 이 챌린지로 시작해 달라는 요청. */
    onStartVerification: (challengeId: Long) -> Unit = {},
    /** 공유 인증 플로우에서 챌린지 인증이 성공할 때마다 증가한다 — 지금 열려 있는 상세 화면이 있으면 새로고침한다. */
    verificationRefreshSignal: Int = 0,
    onTabSelected: (AppBottomTab) -> Unit = {},
    /** 알림 탭 등 외부에서 특정 챌린지 상세로 바로 진입해 달라는 요청. 진입 후 [onInitialChallengeDetailConsumed]로 소비 처리해야 한다. */
    initialChallengeDetailId: Long? = null,
    onInitialChallengeDetailConsumed: () -> Unit = {},
    modifier: Modifier = Modifier,
    navController: NavHostController = rememberNavController(),
) {
    LaunchedEffect(initialChallengeDetailId) {
        if (initialChallengeDetailId != null) {
            navController.navigate("challenge_detail/$initialChallengeDetailId")
            onInitialChallengeDetailConsumed()
        }
    }

    NavHost(
        navController = navController,
        startDestination = RouteChallengeHome,
        modifier = modifier,
    ) {
        composable(RouteChallengeHome) {
            val challengeViewModel: ChallengeViewModel = viewModel {
                ChallengeViewModel(ChallengeContainer.getMyChallengesUseCase)
            }
            val challengeUiState by challengeViewModel.uiState.collectAsStateWithLifecycle()

            // 챌린지 찾기 → 참여 → 뒤로가기로 돌아와도 이 화면의 ViewModel은 백스택에 남아 있던 게
            // 그대로라 init{}이 다시 안 불린다 — 화면이 다시 보일 때(RESUMED)마다 새로 불러서, 방금
            // 참여한 챌린지가 탭을 나갔다 와야만 반영되던 문제를 없앤다.
            val lifecycleOwner = LocalLifecycleOwner.current
            LaunchedEffect(lifecycleOwner, challengeViewModel) {
                lifecycleOwner.lifecycle.repeatOnLifecycle(Lifecycle.State.RESUMED) {
                    challengeViewModel.refresh()
                }
            }

            ChallengeScreen(
                uiState = challengeUiState,
                onFindNewChallengeClick = {
                    navController.navigate(RouteFindChallenge)
                },
                onChallengeClick = { challengeId ->
                    navController.navigate("challenge_detail/$challengeId")
                },
                onTabSelected = onTabSelected,
            )
        }
        composable(RouteFindChallenge) {
            val findChallengeViewModel: FindChallengeViewModel = viewModel {
                FindChallengeViewModel(ChallengeContainer.getChallengesUseCase)
            }
            val findChallengeUiState by findChallengeViewModel.uiState.collectAsStateWithLifecycle()

            FindChallengeScreen(
                uiState = findChallengeUiState,
                actions = findChallengeViewModel,
                onBackClick = { navController.popBackStack() },
                onChallengeClick = { challengeId ->
                    navController.navigate("challenge_detail/$challengeId")
                },
                onTabSelected = onTabSelected,
            )
        }
        composable(
            route = RouteChallengeDetail,
            arguments = listOf(navArgument(ArgChallengeId) { type = NavType.LongType }),
        ) { backStackEntry ->
            val challengeId = backStackEntry.arguments?.getLong(ArgChallengeId) ?: 0L
            val detailViewModel: ChallengeDetailViewModel = viewModel(key = "challenge_detail_$challengeId") {
                ChallengeDetailViewModel(
                    challengeId = challengeId,
                    getChallengeDetailUseCase = ChallengeContainer.getChallengeDetailUseCase,
                    getVerificationsUseCase = ChallengeContainer.getVerificationsUseCase,
                    getMyVerificationsUseCase = ChallengeContainer.getMyVerificationsUseCase,
                    participateChallengeUseCase = ChallengeContainer.participateChallengeUseCase,
                    leaveChallengeUseCase = ChallengeContainer.leaveChallengeUseCase,
                    reportVerificationUseCase = ChallengeContainer.reportVerificationUseCase,
                    deleteVerificationUseCase = ChallengeContainer.deleteVerificationUseCase,
                    likeVerificationUseCase = ChallengeContainer.likeVerificationUseCase,
                    unlikeVerificationUseCase = ChallengeContainer.unlikeVerificationUseCase,
                    editVerificationUseCase = ChallengeContainer.editVerificationUseCase,
                )
            }
            val detailUiState by detailViewModel.uiState.collectAsStateWithLifecycle()

            LaunchedEffect(verificationRefreshSignal) {
                if (verificationRefreshSignal > 0) {
                    detailViewModel.onVerificationSubmitted()
                }
            }

            ChallengeDetailScreen(
                uiState = detailUiState,
                actions = detailViewModel,
                onBackClick = { navController.popBackStack() },
                onStartVerification = { onStartVerification(challengeId) },
            )
        }
    }
}

@Preview(showBackground = true, heightDp = 800)
@Composable
private fun ChallengeNavHostPreview() {
    LiroutiFrontendTheme {
        ChallengeNavHost()
    }
}
