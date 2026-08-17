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
import com.li_routi.core.domain.auth.SocialProvider
import com.li_routi.feature.mypage.component.CoinRefundPolicyLines
import com.li_routi.feature.mypage.component.OpenSourceLicenseLines
import com.li_routi.feature.mypage.component.PrivacyPolicyLines
import com.li_routi.feature.mypage.component.ReportPolicyLines
import com.li_routi.feature.mypage.component.SampleNotices
import com.li_routi.feature.mypage.component.TermsOfServiceLines
import com.li_routi.feature.mypage.screen.AppInfoScreen
import com.li_routi.feature.mypage.screen.EditProfileScreen
import com.li_routi.feature.mypage.screen.MyPageScreen
import com.li_routi.feature.mypage.screen.NoticeDetailScreen
import com.li_routi.feature.mypage.screen.NoticeListScreen
import com.li_routi.feature.mypage.screen.PolicyTextScreen
import com.li_routi.feature.mypage.vm.MyPageUiEvent
import com.li_routi.feature.mypage.vm.MyPageViewModel

/** 계정 관리 화면의 "로그인 정보" 문구로 변환한다. */
private fun SocialProvider.toLoginInfoText(): String = when (this) {
    SocialProvider.KAKAO -> "카카오 계정으로 로그인 중"
    SocialProvider.GOOGLE -> "구글 계정으로 로그인 중"
    SocialProvider.Unknown -> "소셜 계정으로 로그인 중"
}

/** 마이페이지 내부에서 전환되는 화면들. */
private enum class MyPageDestination {
    MyPage,
    EditProfile,
    MyVerification,
    Achievement,
    Report,
    AppInfo,
    AccountManage,
    NoticeList,
    NoticeDetail,
    TermsOfService,
    PrivacyPolicy,
    ReportPolicy,
    OpenSourceLicense,
    CoinRefundPolicy,
}

private const val InquiryComingSoonMessage = "아직 준비중인 서비스에요"

/**
 * 로컬 destination 전환의 상위 화면. 시스템/제스처 Back이 각 화면의 뒤로가기 버튼과 같은 곳으로
 * 가도록 계층을 매핑한다 — 이게 없으면(모두 MyPage로 보내면) 공지사항 상세처럼 4단계 깊이인
 * 화면에서 시스템 Back과 화면 안 뒤로가기 버튼이 서로 다른 곳으로 가버린다.
 */
private val MyPageDestination.parent: MyPageDestination?
    get() = when (this) {
        MyPageDestination.MyPage -> null
        MyPageDestination.NoticeList -> MyPageDestination.AppInfo
        MyPageDestination.NoticeDetail -> MyPageDestination.NoticeList
        MyPageDestination.TermsOfService,
        MyPageDestination.PrivacyPolicy,
        MyPageDestination.ReportPolicy,
        MyPageDestination.OpenSourceLicense,
        MyPageDestination.CoinRefundPolicy,
        -> MyPageDestination.AppInfo
        else -> MyPageDestination.MyPage
    }

/**
 * 마이페이지 진입점. [MyPageViewModel]과 [MyPageScreen]을 연결한다.
 *
 * "프로필 수정"/"업적"/"리포트"/"앱 정보"/"계정 관리" 탭 시 각각 [EditProfileScreen]/
 * [AchievementRoute]/[ReportRoute]/[AppInfoScreen]/[AccountManageRoute]로 전환한다 —
 * 별도 NavHost 없이 HomeRoute와 동일하게 화면 상태 하나로 전환한다.
 */
@Composable
fun MyPageRoute(
    onTabSelected: (AppBottomTab) -> Unit = {},
    /**
     * 마이 탭을 다시 눌러 들어올 때마다 증가하는 값(HomeRoute의 requestRefreshTick과 동일한 패턴).
     * 0보다 커지면(=탭 재진입) 프로필을 다시 불러온다.
     */
    refreshTick: Int = 0,
    /** 상단 바 알림벨 아이콘 탭 — 목적지(홈의 알림 목록)가 다른 feature라 app 모듈에 위임한다. */
    onNotificationClick: () -> Unit = {},
    /** 상단 바 설정 아이콘 탭 — 목적지(홈의 알림 설정)가 다른 feature라 app 모듈에 위임한다. */
    onSettingsClick: () -> Unit = {},
    modifier: Modifier = Modifier,
    viewModel: MyPageViewModel = viewModel { MyPageViewModel() },
) {
    val uiState by viewModel.uiState.collectAsStateWithLifecycle()
    var destination by rememberSaveable { mutableStateOf(MyPageDestination.MyPage) }
    // 공지 상세로 넘어갈 때만 채워진다 — 목록에서 어떤 공지를 눌렀는지 destination 전환과
    // 별도로 들고 있어야 상세 화면이 어떤 내용을 보여줄지 알 수 있다.
    var selectedNoticeId by rememberSaveable { mutableStateOf<String?>(null) }
    val context = LocalContext.current

    LaunchedEffect(refreshTick) {
        if (refreshTick > 0) {
            viewModel.refresh()
        }
    }

    // Nav 백스택이 아니라 로컬 전환이므로, 시스템 Back이 마이페이지 하위 화면을 건너뛰고
    // 곧바로 홈 탭으로 넘어가지 않게 가로챈다.
    BackHandler(enabled = destination != MyPageDestination.MyPage) {
        destination = destination.parent ?: MyPageDestination.MyPage
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
                MyPageUiEvent.NavigateToNotification -> onNotificationClick()
                MyPageUiEvent.NavigateToNotificationSettings -> onSettingsClick()
                MyPageUiEvent.ProfileSaved -> destination = MyPageDestination.MyPage
                is MyPageUiEvent.ShowError -> Toast.makeText(context, event.message, Toast.LENGTH_SHORT).show()
            }
        }
    }

    // 최초 프로필 조회 실패는 uiEvent가 아니라 상태로 온다(MyPageUiState.profileLoadError 문서 참고) —
    // 구독 시작 전에 실패가 끝나도 놓치지 않게 하기 위함이다. 보여준 뒤 바로 지운다.
    LaunchedEffect(uiState.profileLoadError) {
        val message = uiState.profileLoadError ?: return@LaunchedEffect
        Toast.makeText(context, message, Toast.LENGTH_SHORT).show()
        viewModel.onProfileLoadErrorShown()
    }

    when (destination) {
        MyPageDestination.MyPage -> MyPageScreen(
            actions = viewModel,
            onTabSelected = onTabSelected,
            nickname = uiState.nickname,
            email = uiState.email,
            profileImageUrl = uiState.profileImageUrl,
            hasUnreadNotification = uiState.hasUnreadNotification,
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

        MyPageDestination.Achievement -> AchievementRoute(
            onBackClick = { destination = MyPageDestination.MyPage },
            modifier = modifier,
        )

        MyPageDestination.Report -> ReportRoute(
            onBackClick = { destination = MyPageDestination.MyPage },
            modifier = modifier,
        )

        MyPageDestination.AppInfo -> AppInfoScreen(
            onBackClick = { destination = MyPageDestination.MyPage },
            onNoticeClick = { destination = MyPageDestination.NoticeList },
            onInquiryClick = {
                Toast.makeText(context, InquiryComingSoonMessage, Toast.LENGTH_SHORT).show()
            },
            onTermsOfServiceClick = { destination = MyPageDestination.TermsOfService },
            onPrivacyPolicyClick = { destination = MyPageDestination.PrivacyPolicy },
            onCoinRefundPolicyClick = { destination = MyPageDestination.CoinRefundPolicy },
            onReportPolicyClick = { destination = MyPageDestination.ReportPolicy },
            onOpenSourceLicenseClick = { destination = MyPageDestination.OpenSourceLicense },
            modifier = modifier,
        )

        MyPageDestination.NoticeList -> NoticeListScreen(
            onBackClick = { destination = MyPageDestination.AppInfo },
            onNoticeClick = { noticeId ->
                selectedNoticeId = noticeId
                destination = MyPageDestination.NoticeDetail
            },
            modifier = modifier,
        )

        MyPageDestination.NoticeDetail -> {
            val notice = SampleNotices.firstOrNull { it.id == selectedNoticeId }
            if (notice == null) {
                // selectedNoticeId가 목록에 없는 상태(예: 프로세스 복원 후 목록이 바뀐 경우) —
                // 크래시 대신 목록으로 되돌린다.
                LaunchedEffect(selectedNoticeId) { destination = MyPageDestination.NoticeList }
            } else {
                NoticeDetailScreen(
                    notice = notice,
                    onBackClick = { destination = MyPageDestination.NoticeList },
                    modifier = modifier,
                )
            }
        }

        MyPageDestination.TermsOfService -> PolicyTextScreen(
            topBarTitle = "이용 약관",
            lines = TermsOfServiceLines,
            onBackClick = { destination = MyPageDestination.AppInfo },
            modifier = modifier,
        )

        MyPageDestination.PrivacyPolicy -> PolicyTextScreen(
            topBarTitle = "개인정보 처리방침",
            lines = PrivacyPolicyLines,
            onBackClick = { destination = MyPageDestination.AppInfo },
            modifier = modifier,
        )

        MyPageDestination.ReportPolicy -> PolicyTextScreen(
            topBarTitle = "신고 및 운영 정책",
            lines = ReportPolicyLines,
            onBackClick = { destination = MyPageDestination.AppInfo },
            modifier = modifier,
        )

        MyPageDestination.OpenSourceLicense -> PolicyTextScreen(
            topBarTitle = "오픈소스 라이선스",
            lines = OpenSourceLicenseLines,
            onBackClick = { destination = MyPageDestination.AppInfo },
            modifier = modifier,
        )

        MyPageDestination.CoinRefundPolicy -> PolicyTextScreen(
            topBarTitle = "코인 및 환불 정책",
            lines = CoinRefundPolicyLines,
            onBackClick = { destination = MyPageDestination.AppInfo },
            modifier = modifier,
        )

        MyPageDestination.AccountManage -> AccountManageRoute(
            onBackClick = { destination = MyPageDestination.MyPage },
            loginInfo = uiState.socialProvider.toLoginInfoText(),
            modifier = modifier,
        )
    }
}
