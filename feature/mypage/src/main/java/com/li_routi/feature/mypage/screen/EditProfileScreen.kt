package com.li_routi.feature.mypage.screen

import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.gestures.detectTapGestures
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.navigationBarsPadding
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.ColorFilter
import androidx.compose.ui.input.pointer.pointerInput
import androidx.compose.ui.platform.LocalFocusManager
import androidx.compose.ui.platform.LocalSoftwareKeyboardController
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.li_routi.core.designsystem.R
import com.li_routi.core.designsystem.component.LiroutiAvatar
import com.li_routi.core.designsystem.component.LiroutiTextField
import com.li_routi.core.designsystem.theme.LiroutiFrontendTheme
import com.li_routi.core.designsystem.theme.LiroutiTheme
import com.li_routi.feature.mypage.component.EditProfileTopBar

/**
 * 프로필 수정(닉네임 변경) 화면. Figma node `205:18107`("마이") 기준.
 *
 * 마이페이지의 "프로필 수정" 버튼으로 진입한다. 지금은 닉네임 한 항목만 편집한다.
 */
@Composable
fun EditProfileScreen(
    initialNickname: String,
    onBackClick: () -> Unit,
    onCancelClick: () -> Unit,
    onSaveClick: (String) -> Unit,
    onEditPhotoClick: () -> Unit = {},
    isSaving: Boolean = false,
    modifier: Modifier = Modifier,
) {
    var nickname by remember(initialNickname) { mutableStateOf(initialNickname) }
    val focusManager = LocalFocusManager.current
    val keyboardController = LocalSoftwareKeyboardController.current

    Column(
        modifier = modifier
            .fillMaxSize()
            .background(LiroutiTheme.colors.backgroundDefault)
            .pointerInput(Unit) {
                detectTapGestures(onTap = {
                    keyboardController?.hide()
                    focusManager.clearFocus()
                })
            },
    ) {
        EditProfileTopBar(title = "프로필 수정", onBackClick = onBackClick)
        Column(
            modifier = Modifier
                .weight(1f)
                .fillMaxWidth()
                .padding(horizontal = 16.dp, vertical = 24.dp),
        ) {
            EditProfileAvatar(
                onEditPhotoClick = onEditPhotoClick,
                modifier = Modifier.align(Alignment.CenterHorizontally),
            )
            Spacer(modifier = Modifier.height(24.dp))
            LiroutiTextField(
                value = nickname,
                onValueChange = { nickname = it },
                labelText = "닉네임",
                showHelper = false,
            )
        }
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .navigationBarsPadding()
                .padding(horizontal = 16.dp, vertical = 16.dp),
            horizontalArrangement = Arrangement.spacedBy(6.dp),
        ) {
            EditProfileActionButton(
                text = "취소",
                onClick = onCancelClick,
                backgroundColor = LiroutiTheme.colors.backgroundAlternative,
                textColor = LiroutiTheme.colors.labelDefault,
                enabled = !isSaving,
                modifier = Modifier.weight(1f),
            )
            EditProfileActionButton(
                text = "저장",
                onClick = { onSaveClick(nickname) },
                backgroundColor = LiroutiTheme.colors.primaryNormal,
                textColor = LiroutiTheme.colors.backgroundAlternative,
                enabled = !isSaving,
                modifier = Modifier.weight(1f),
            )
        }
    }
}

private val EditProfileAvatarSize = 80.dp
private val EditProfilePhotoBadgeSize = 24.dp

@Composable
private fun EditProfileAvatar(
    onEditPhotoClick: () -> Unit,
    modifier: Modifier = Modifier,
) {
    Box(modifier = modifier.size(EditProfileAvatarSize)) {
        LiroutiAvatar(size = EditProfileAvatarSize)
        Box(
            modifier = Modifier
                .align(Alignment.BottomEnd)
                .size(EditProfilePhotoBadgeSize)
                .clip(CircleShape)
                .background(LiroutiTheme.colors.backgroundDefault)
                .border(1.dp, LiroutiTheme.colors.borderDefault, CircleShape)
                .clickable(onClick = onEditPhotoClick),
            contentAlignment = Alignment.Center,
        ) {
            Image(
                painter = painterResource(id = R.drawable.camera),
                contentDescription = "프로필 사진 변경",
                modifier = Modifier.size(16.dp),
                colorFilter = ColorFilter.tint(LiroutiTheme.colors.labelDefault),
            )
        }
    }
}

@Composable
private fun EditProfileActionButton(
    text: String,
    onClick: () -> Unit,
    backgroundColor: Color,
    textColor: Color,
    modifier: Modifier = Modifier,
    enabled: Boolean = true,
) {
    Box(
        modifier = modifier
            .height(44.dp)
            .background(backgroundColor, RoundedCornerShape(6.dp))
            .clickable(enabled = enabled, onClick = onClick),
        contentAlignment = Alignment.Center,
    ) {
        Text(text = text, style = LiroutiTheme.typography.body2LongMedium, color = textColor)
    }
}

@Preview(showBackground = true, heightDp = 800)
@Composable
private fun EditProfileScreenPreview() {
    LiroutiFrontendTheme {
        EditProfileScreen(
            initialNickname = "잠자는 개구리",
            onBackClick = {},
            onCancelClick = {},
            onSaveClick = {},
        )
    }
}
