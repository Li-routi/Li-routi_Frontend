package com.li_routi.feature.mypage.component

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import coil.compose.AsyncImage
import com.li_routi.core.designsystem.component.LiroutiAvatar
import com.li_routi.core.designsystem.theme.LiroutiFrontendTheme
import com.li_routi.core.designsystem.theme.LiroutiTheme

private val ProfileHeaderAvatarSize = 52.dp

/** 마이페이지 프로필 영역. Figma node `205:18081` 기준. */
@Composable
fun MyPageProfileHeader(
    nickname: String,
    email: String,
    profileImageUrl: String?,
    onEditProfileClick: () -> Unit,
    modifier: Modifier = Modifier,
) {
    Column(
        modifier = modifier
            .fillMaxWidth()
            .padding(horizontal = 16.dp, vertical = 14.dp),
        verticalArrangement = Arrangement.spacedBy(14.dp),
    ) {
        Row(horizontalArrangement = Arrangement.spacedBy(8.dp), verticalAlignment = Alignment.CenterVertically) {
            LiroutiAvatar(size = ProfileHeaderAvatarSize) {
                if (profileImageUrl != null) {
                    AsyncImage(
                        model = profileImageUrl,
                        contentDescription = "프로필 사진",
                        modifier = Modifier.size(ProfileHeaderAvatarSize),
                        contentScale = ContentScale.Crop,
                    )
                }
            }
            Column(verticalArrangement = Arrangement.spacedBy(4.dp)) {
                Text(
                    text = nickname,
                    style = LiroutiTheme.typography.body1Bold,
                    color = LiroutiTheme.colors.labelDefault,
                )
                Text(
                    text = email,
                    style = LiroutiTheme.typography.captionSemiBold,
                    color = LiroutiTheme.colors.labelSub,
                )
            }
        }
        EditProfileButton(onClick = onEditProfileClick, modifier = Modifier.fillMaxWidth())
    }
}

@Composable
private fun EditProfileButton(
    onClick: () -> Unit,
    modifier: Modifier = Modifier,
) {
    val interactionSource = remember { MutableInteractionSource() }
    val shape = RoundedCornerShape(6.dp)

    Box(
        modifier = modifier
            .height(44.dp)
            .background(color = LiroutiTheme.colors.backgroundAlternative, shape = shape)
            .clickable(
                interactionSource = interactionSource,
                indication = null,
                onClick = onClick,
            ),
        contentAlignment = Alignment.Center,
    ) {
        Text(
            text = "프로필 수정",
            style = LiroutiTheme.typography.body2LongMedium,
            color = LiroutiTheme.colors.labelDefault,
        )
    }
}

@Preview(showBackground = true)
@Composable
private fun MyPageProfileHeaderPreview() {
    LiroutiFrontendTheme {
        MyPageProfileHeader(
            nickname = "잠자는개구리",
            email = "example@gamil.com",
            profileImageUrl = null,
            onEditProfileClick = {},
        )
    }
}
