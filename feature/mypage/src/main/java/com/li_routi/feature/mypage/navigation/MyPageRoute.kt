package com.li_routi.feature.mypage.navigation

import android.widget.Toast
import androidx.activity.compose.BackHandler
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.lifecycle.viewmodel.compose.viewModel
import com.li_routi.core.common.ui.nav.AppBottomTab
import com.li_routi.feature.mypage.screen.AchievementScreen
import com.li_routi.feature.mypage.screen.AppInfoScreen
import com.li_routi.feature.mypage.screen.EditProfileScreen
import com.li_routi.feature.mypage.screen.MyPageScreen
import com.li_routi.feature.mypage.screen.ReportScreen
import com.li_routi.feature.mypage.vm.MyPageUiEvent
import com.li_routi.feature.mypage.vm.MyPageViewModel

/** 마이페이지 내부에서 전환되는 화면들. */
private enum class MyPageDestination {
    MyPage,
    EditProfile,
    MyVerification,
    Achievement,
    Report,
    AppInfo,
    AccountManage,
}

/**
 * 마이페이지 진입점. [MyPageViewModel]과 [MyPageScreen]을 연결한다.
 *
 * "프로필 수정"/"업적"/"리포트"/"앱 정보"/"계정 관리" 탭 시 각각 [EditProfileScreen]/
 * [AchievementScreen]/[ReportScreen]/[AppInfoScreen]/[AccountManageRoute]로 전환한다 —
 * 별도 NavHost 없이 HomeRoute와 동일하게 화면 상태 하나로 전환한다.
 */
@Composable
fun MyPageRoute(
    onTabSelected: (AppBottomTab) -> Unit = {},
    modifier: Modifier = Modifier,
    viewModel: MyPageViewModel = viewModel { MyPageViewModel() },
) {
    val uiState by viewModel.uiState.collectAsStateWithLifecycle()
    var destination by rememberSaveable { mutableStateOf(MyPageDestination.MyPage) }
    val context = LocalContext.current

    // Nav 백스택이 아니라 로컬 전환이므로, 시스템 Back이 마이페이지 하위 화면을 건너뛰고
    // 곧바로 홈 탭으로 넘어가지 않게 가로챈다.
    BackHandler(enabled = destination != MyPageDestination.MyPage) {
        destination = MyPageDestination.MyPage
    }

    LaunchedEffect(viewModel) {
        viewModel.uiEvent.collect { event ->
            when (event) {
                MyPageUiEvent.NavigateToEditProfile -> destination = MyPageDestination.EditProfile
                MyPageUiEvent.NavigateToMyVerification -> destination = MyPageDestination.MyVerification
                MyPageUiEvent.NavigateToAchievement -> destination = MyPageDestination.Achievement
                MyPageUiEvent.NavigateToReport -> destination = MyPageDestination.Report
                MyPageUiEvent.NavigateToAppInfo -> destination = MyPageDestination.AppInfo
                MyPageUiEvent.NavigateToAccountManage -> destination = MyPageDestination.AccountManage
                MyPageUiEvent.ProfileSaved -> destination = MyPageDestination.MyPage
                is MyPageUiEvent.ShowError -> Toast.makeText(context, event.message, Toast.LENGTH_SHORT).show()
            }
        }
    }

    when (destination) {
        MyPageDestination.MyPage -> MyPageScreen(
            actions = viewModel,
            onTabSelected = onTabSelected,
            nickname = uiState.nickname,
            email = uiState.email,
            profileImageUrl = uiState.profileImageUrl,
            modifier = modifier,
        )

        MyPageDestination.EditProfile -> EditProfileScreen(
            initialNickname = uiState.nickname,
            profileImageUrl = uiState.profileImageUrl,
            isSaving = uiState.isSavingProfile,
            onBackClick = { destination = MyPageDestination.MyPage },
            onCancelClick = { destination = MyPageDestination.MyPage },
            onSaveClick = { nickname, imageUri -> viewModel.onSaveProfile(context, nickname, imageUri) },
            modifier = modifier,
        )

        MyPageDestination.MyVerification -> MyVerificationRoute(
            onBackClick = { destination = MyPageDestination.MyPage },
            modifier = modifier,
        )

        MyPageDestination.Achievement -> AchievementScreen(
            onBackClick = { destination = MyPageDestination.MyPage },
            modifier = modifier,
        )

        MyPageDestination.Report -> ReportScreen(
            onBackClick = { destination = MyPageDestination.MyPage },
            modifier = modifier,
        )

        MyPageDestination.AppInfo -> AppInfoScreen(
            onBackClick = { destination = MyPageDestination.MyPage },
            modifier = modifier,
        )

        MyPageDestination.AccountManage -> AccountManageRoute(
            onBackClick = { destination = MyPageDestination.MyPage },
            modifier = modifier,
        )
    }
}
