package com.li_routi.feature.login.screen

import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.offset
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.layout.widthIn
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.BasicTextField
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.focus.onFocusChanged
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.SolidColor
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import com.li_routi.core.designsystem.R
import com.li_routi.core.designsystem.component.HorizontalDoubleButton
import com.li_routi.core.designsystem.component.LiroutiAvatar
import com.li_routi.core.designsystem.theme.LiroutiFrontendTheme
import com.li_routi.core.designsystem.theme.LiroutiTheme

val ProfileHeaderTopOffset = 44.dp
val ProfileHeaderMinWidth = 360.dp
val ProfileHeaderHeight = 48.dp
val ProfileHeaderTitleMinWidth = 328.dp
val ProfileHeaderTitleHeight = 28.dp

val ProfileAvatarTopSpacing = 25.dp
val ProfileAvatarSize = 80.dp

val ProfileAvatarBadgeSize = 24.dp
val ProfileAvatarBadgeStartOffset = 60.dp
val ProfileAvatarBadgeBottomOverhang = 2.dp
val ProfileAvatarBadgeBorderWidth = 1.dp
val ProfileAvatarBadgeBorderColor = Color(0xFFDBDCDF)
val ProfileAvatarBadgeBackgroundColor = Color(0xFFFFFFFF)
val ProfileAvatarBadgeIconSize = 16.dp

val ProfileNicknameSectionTopSpacing = 20.dp
val ProfileNicknameSectionWidth = 328.dp
val ProfileNicknameSectionHeight = 74.dp
val ProfileNicknameLabelHeight = 22.dp
val ProfileNicknameFieldSpacing = 8.dp
val ProfileNicknameInputHeight = 44.dp
val ProfileNicknameInputCornerRadius = 6.dp
val ProfileNicknameInputBorderWidth = 1.dp
val ProfileNicknameInputBorderColor = Color(0xFFDBDCDF)
val ProfileNicknameInputBackgroundColor = Color(0xFFFFFFFF)

val ProfileActionButtonBottomSpacing = 40.dp
val ProfileActionButtonWidth = 328.dp
val ProfileActionButtonHeight = 44.dp

@Composable
fun ProfileSettingHeader(
    title: String,
    modifier: Modifier = Modifier,
    containerMinWidth: Dp = ProfileHeaderMinWidth,
    containerHeight: Dp = ProfileHeaderHeight,
    titleMinWidth: Dp = ProfileHeaderTitleMinWidth,
    titleHeight: Dp = ProfileHeaderTitleHeight,
) {
    Box(
        modifier = modifier
            .fillMaxWidth()
            .widthIn(min = containerMinWidth)
            .height(containerHeight),
        contentAlignment = Alignment.Center,
    ) {
        Box(
            modifier = Modifier
                .widthIn(min = titleMinWidth)
                .height(titleHeight),
            contentAlignment = Alignment.Center,
        ) {
            Text(
                text = title,
                style = LiroutiTheme.typography.heading2Bold,
                color = LiroutiTheme.colors.labelStrong,
            )
        }
    }
}

/**
 * 아바타와, 아바타 좌측 기준 [badgeStartOffset]만큼 떨어진 곳에 겹쳐지는 카메라 배지.
 * 배지 위치는 [avatarSize]/[badgeSize] 기준으로 계산되어, 두 크기가 바뀌어도 항상
 * 아바타 하단보다 [badgeBottomOverhang]만큼 더 아래로 튀어나오도록 유지된다.
 */
@Composable
fun ProfileAvatarWithCameraBadge(
    modifier: Modifier = Modifier,
    onCameraClick: () -> Unit = {},
    avatarSize: Dp = ProfileAvatarSize,
    badgeSize: Dp = ProfileAvatarBadgeSize,
    badgeStartOffset: Dp = ProfileAvatarBadgeStartOffset,
    badgeBottomOverhang: Dp = ProfileAvatarBadgeBottomOverhang,
    badgeBorderWidth: Dp = ProfileAvatarBadgeBorderWidth,
    badgeBorderColor: Color = ProfileAvatarBadgeBorderColor,
    badgeBackgroundColor: Color = ProfileAvatarBadgeBackgroundColor,
    badgeIconSize: Dp = ProfileAvatarBadgeIconSize,
) {
    Box(modifier = modifier.size(avatarSize)) {
        LiroutiAvatar(size = avatarSize)
        Box(
            modifier = Modifier
                .offset(x = badgeStartOffset, y = avatarSize - badgeSize + badgeBottomOverhang)
                .size(badgeSize)
                .clip(CircleShape)
                .background(badgeBackgroundColor, CircleShape)
                .border(badgeBorderWidth, badgeBorderColor, CircleShape)
                .clickable(onClick = onCameraClick),
            contentAlignment = Alignment.Center,
        ) {
            Image(
                painter = painterResource(id = R.drawable.camera),
                contentDescription = "프로필 사진 변경",
                modifier = Modifier.size(badgeIconSize),
            )
        }
    }
}

/**
 * 아바타 아래 닉네임 입력 영역. [initialValue]가 채워진 채로 표시되다가,
 * 사용자가 입력 상자를 처음 클릭(포커스)하면 기존 글자를 지우고 새로 입력할 수 있다.
 */
@Composable
fun ProfileNicknameField(
    modifier: Modifier = Modifier,
    initialValue: String = "잠자는 개구리",
    width: Dp = ProfileNicknameSectionWidth,
    height: Dp = ProfileNicknameSectionHeight,
    labelHeight: Dp = ProfileNicknameLabelHeight,
    fieldSpacing: Dp = ProfileNicknameFieldSpacing,
    inputHeight: Dp = ProfileNicknameInputHeight,
    inputCornerRadius: Dp = ProfileNicknameInputCornerRadius,
    inputBorderWidth: Dp = ProfileNicknameInputBorderWidth,
    inputBorderColor: Color = ProfileNicknameInputBorderColor,
    inputBackgroundColor: Color = ProfileNicknameInputBackgroundColor,
) {
    var nickname by remember { mutableStateOf(initialValue) }
    var hasClearedInitialValue by remember { mutableStateOf(false) }

    Column(
        modifier = modifier
            .width(width)
            .height(height),
    ) {
        Box(
            modifier = Modifier
                .fillMaxWidth()
                .height(labelHeight),
            contentAlignment = Alignment.CenterStart,
        ) {
            Text(
                text = "닉네임",
                style = LiroutiTheme.typography.body2LongSemiBold,
                color = LiroutiTheme.colors.labelDefault,
            )
        }
        Spacer(modifier = Modifier.height(fieldSpacing))
        Box(
            modifier = Modifier
                .fillMaxWidth()
                .height(inputHeight)
                .background(inputBackgroundColor, RoundedCornerShape(inputCornerRadius))
                .border(inputBorderWidth, inputBorderColor, RoundedCornerShape(inputCornerRadius))
                .padding(horizontal = 16.dp),
            contentAlignment = Alignment.CenterStart,
        ) {
            BasicTextField(
                value = nickname,
                onValueChange = { nickname = it },
                singleLine = true,
                textStyle = LiroutiTheme.typography.body2LongRegular.copy(color = LiroutiTheme.colors.labelDefault),
                cursorBrush = SolidColor(LiroutiTheme.colors.labelDefault),
                modifier = Modifier
                    .fillMaxWidth()
                    .onFocusChanged { focusState ->
                        if (focusState.isFocused && !hasClearedInitialValue) {
                            nickname = ""
                            hasClearedInitialValue = true
                        }
                    },
            )
        }
    }
}

@Composable
fun ProfileScreen(
    modifier: Modifier = Modifier,
    onSaveClick: () -> Unit = {},
) {
    var showBottomSheet by remember { mutableStateOf(false) }

    Box(modifier = modifier.fillMaxSize()) {
        Column(modifier = Modifier.fillMaxSize()) {
            ProfileSettingHeader(
                title = "프로필 설정",
                modifier = Modifier.padding(top = ProfileHeaderTopOffset),
            )
            ProfileAvatarWithCameraBadge(
                modifier = Modifier
                    .align(Alignment.CenterHorizontally)
                    .padding(top = ProfileAvatarTopSpacing),
                onCameraClick = { showBottomSheet = true },
            )
            ProfileNicknameField(
                modifier = Modifier
                    .align(Alignment.CenterHorizontally)
                    .padding(top = ProfileNicknameSectionTopSpacing),
            )
            Spacer(modifier = Modifier.weight(1f))
            HorizontalDoubleButton(
                leftLabel = "취소",
                rightLabel = "저장",
                leftWidth = null,
                containerWidth = ProfileActionButtonWidth,
                containerHeight = ProfileActionButtonHeight,
                modifier = Modifier
                    .align(Alignment.CenterHorizontally)
                    .padding(bottom = ProfileActionButtonBottomSpacing),
                onRightClick = onSaveClick,
            )
        }

        if (showBottomSheet) {
            BottomScreen(onDismiss = { showBottomSheet = false })
        }
    }
}

@Preview(showBackground = true)
@Composable
private fun ProfileScreenPreview() {
    LiroutiFrontendTheme {
        ProfileScreen()
    }
}