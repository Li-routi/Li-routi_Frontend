package com.li_routi.feature.mypage.navigation

import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.lifecycle.viewmodel.compose.viewModel
import com.li_routi.feature.mypage.screen.MyVerificationScreen
import com.li_routi.feature.mypage.vm.MyVerificationViewModel

/** "내 인증" 화면 진입점. [MyVerificationViewModel]과 [MyVerificationScreen]을 연결한다. */
@Composable
fun MyVerificationRoute(
    onBackClick: () -> Unit,
    modifier: Modifier = Modifier,
    viewModel: MyVerificationViewModel = viewModel { MyVerificationViewModel() },
) {
    val uiState by viewModel.uiState.collectAsStateWithLifecycle()

    MyVerificationScreen(
        onBackClick = onBackClick,
        selectedDate = uiState.selectedDate,
        onDateChange = viewModel::onDateSelected,
        verifications = uiState.verifications,
        pendingVerifications = uiState.pendingVerifications,
        modifier = modifier,
    )
}
