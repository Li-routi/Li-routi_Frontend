package com.li_routi.feature.challenge.navigation

import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.tooling.preview.Preview
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.NavHostController
import androidx.navigation.NavType
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import androidx.navigation.navArgument
import com.li_routi.core.designsystem.theme.LiroutiFrontendTheme
import com.li_routi.feature.challenge.screen.ChallengeDetailScreen
import com.li_routi.feature.challenge.screen.ChallengeScreen
import com.li_routi.feature.challenge.screen.FindChallengeScreen
import com.li_routi.feature.challenge.vm.ChallengeDetailViewModel

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
    modifier: Modifier = Modifier,
    navController: NavHostController = rememberNavController(),
) {
    NavHost(
        navController = navController,
        startDestination = RouteChallengeHome,
        modifier = modifier,
    ) {
        composable(RouteChallengeHome) {
            ChallengeScreen(
                routines = emptyList(),
                onFindNewChallengeClick = {
                    navController.navigate(RouteFindChallenge)
                },
            )
        }
        composable(RouteFindChallenge) {
            FindChallengeScreen(
                onBackClick = { navController.popBackStack() },
                onChallengeClick = { challengeId ->
                    navController.navigate("challenge_detail/$challengeId")
                },
            )
        }
        composable(
            route = RouteChallengeDetail,
            arguments = listOf(navArgument(ArgChallengeId) { type = NavType.LongType }),
        ) { backStackEntry ->
            val challengeId = backStackEntry.arguments?.getLong(ArgChallengeId) ?: 0L
            val detailViewModel: ChallengeDetailViewModel = viewModel(key = "challenge_detail_$challengeId") {
                ChallengeDetailViewModel(challengeId = challengeId)
            }
            val detailUiState by detailViewModel.uiState.collectAsStateWithLifecycle()

            ChallengeDetailScreen(
                uiState = detailUiState,
                actions = detailViewModel,
                onBackClick = { navController.popBackStack() },
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
