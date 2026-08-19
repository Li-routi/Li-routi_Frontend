package com.li_routi.feature.grouproutine.navigation

import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.tooling.preview.Preview
import com.li_routi.core.common.ui.nav.AppBottomTab
import com.li_routi.core.common.ui.routine.CategoryColor
import com.li_routi.core.designsystem.theme.LiroutiFrontendTheme
import com.li_routi.feature.grouproutine.screen.GroupRoutineRoute

data class GroupRoutineVerificationTarget(
    val groupId: Long,
    val routineId: Long,
    val verificationId: Long? = null,
    val roomName: String,
    val title: String,
    val category: String,
    val deadline: String,
    val categoryColor: CategoryColor?,
)

@Composable
fun GrouproutineRootNavHost(
    initialEntryPoint: GrouproutineEntryPoint?,
    onInitialEntryPointConsumed: () -> Unit,
    onTabSelected: (AppBottomTab) -> Unit,
    onStartVerification: (GroupRoutineVerificationTarget) -> Unit = {},
    verificationRefreshSignal: Int = 0,
    verifiedRoutineIds: Set<Long> = emptySet(),
    modifier: Modifier = Modifier,
) {
    GroupRoutineRoute(
        initialEntryPoint = initialEntryPoint,
        onInitialEntryPointConsumed = onInitialEntryPointConsumed,
        onTabSelected = onTabSelected,
        onStartVerification = onStartVerification,
        verificationRefreshSignal = verificationRefreshSignal,
        verifiedRoutineIds = verifiedRoutineIds,
        modifier = modifier,
    )
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
