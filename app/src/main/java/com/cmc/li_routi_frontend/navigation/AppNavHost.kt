package com.cmc.li_routi_frontend.navigation

import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.navigation.NavHostController
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import com.li_routi.feature.challenge.navigation.ChallengeNavHost

private const val RouteChallenge = "challenge"

/**
 * 앱 전체 최상위 내비게이션 그래프.
 *
 * 각 feature는 자기 화면(컴포저블 진입점)만 노출하고, feature 간 이동은 여기서 중계한다
 * (feature가 다른 feature를 직접 의존하면 안 되기 때문). 지금은 [feature:challenge]만
 * 연결된 상태이고, 나머지 feature는 아래 패턴을 따라 추가하면 된다:
 *
 * ```
 * composable("home") {
 *     HomeRoute(
 *         onEvent = { event ->
 *             when (event) {
 *                 HomeUiEvent.NavigateToShop -> navController.navigate("shop")
 *                 // ...
 *             }
 *         },
 *     )
 * }
 * ```
 */
@Composable
fun AppNavHost(
    modifier: Modifier = Modifier,
    navController: NavHostController = rememberNavController(),
) {
    NavHost(
        navController = navController,
        startDestination = RouteChallenge,
        modifier = modifier,
    ) {
        composable(RouteChallenge) {
            ChallengeNavHost()
        }
    }
}
