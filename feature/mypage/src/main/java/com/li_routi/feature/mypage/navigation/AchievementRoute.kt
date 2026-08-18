package com.li_routi.feature.mypage.navigation

import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.lifecycle.viewmodel.compose.viewModel
import com.li_routi.feature.mypage.screen.AchievementScreen
import com.li_routi.feature.mypage.vm.AchievementViewModel

/** "업적" 화면 진입점. [AchievementViewModel]과 [AchievementScreen]을 연결한다. */
@Composable
fun AchievementRoute(
    onBackClick: () -> Unit,
    modifier: Modifier = Modifier,
    viewModel: AchievementViewModel = viewModel { AchievementViewModel() },
) {
    val uiState by viewModel.uiState.collectAsStateWithLifecycle()

    AchievementScreen(
        onBackClick = onBackClick,
        achievements = uiState.achievements,
        achievedBadges = uiState.achievedBadges,
        isLoading = uiState.isLoading,
        isError = uiState.isError,
        onClaimClick = viewModel::onClaimClick,
        claimMessage = uiState.claimMessage,
        onClaimMessageDismissed = viewModel::onClaimMessageDismissed,
        waveRoutineStatus = uiState.waveRoutineStatus,
        isWaveRoutinePickerVisible = uiState.isWaveRoutinePickerVisible,
        myRoutines = uiState.myRoutines,
        onWaveRoutinePickerOpen = viewModel::onWaveRoutinePickerOpen,
        onWaveRoutinePickerDismiss = viewModel::onWaveRoutinePickerDismiss,
        onWaveRoutineChosen = viewModel::onWaveRoutineChosen,
        modifier = modifier,
    )
}
