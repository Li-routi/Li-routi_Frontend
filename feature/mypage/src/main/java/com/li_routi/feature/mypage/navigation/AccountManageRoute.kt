package com.li_routi.feature.mypage.navigation

import android.content.Intent
import android.widget.Toast
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.lifecycle.viewmodel.compose.viewModel
import com.li_routi.feature.mypage.screen.AccountManageScreen
import com.li_routi.feature.mypage.vm.AccountManageUiEvent
import com.li_routi.feature.mypage.vm.AccountManageViewModel
import kotlinx.coroutines.delay

/** `:app`을 컴파일 타임에 참조할 수 없어(:app -> feature:mypage 방향으로만 의존) LoginActivity로 문자열 + Intent로 넘긴다. */
private const val LoginActivityClassName = "com.li_routi.feature.login.LoginActivity"

/** 로그아웃 성공 토스트를 보여주는 최소 시간(사용자가 확인할 수 있게) 후 로그인 화면으로 이동한다. */
private const val LogoutToastDurationMillis = 1200L

/** 계정 관리 화면 진입점. [AccountManageViewModel]과 [AccountManageScreen]을 연결한다. */
@Composable
fun AccountManageRoute(
    onBackClick: () -> Unit,
    modifier: Modifier = Modifier,
    viewModel: AccountManageViewModel = viewModel { AccountManageViewModel() },
) {
    val context = LocalContext.current
    var showLogoutToast by remember { mutableStateOf(false) }

    LaunchedEffect(viewModel) {
        viewModel.uiEvent.collect { event ->
            when (event) {
                AccountManageUiEvent.LogoutSucceeded -> {
                    showLogoutToast = true
                    delay(LogoutToastDurationMillis)
                    val intent = Intent().apply {
                        setClassName(context.packageName, LoginActivityClassName)
                        flags = Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TASK
                    }
                    context.startActivity(intent)
                }
                is AccountManageUiEvent.ShowError -> {
                    Toast.makeText(context, event.message, Toast.LENGTH_SHORT).show()
                }
            }
        }
    }

    AccountManageScreen(
        onBackClick = onBackClick,
        onLogoutConfirmed = viewModel::onLogoutConfirmed,
        showLogoutToast = showLogoutToast,
        onDismissLogoutToast = { showLogoutToast = false },
        modifier = modifier,
    )
}
