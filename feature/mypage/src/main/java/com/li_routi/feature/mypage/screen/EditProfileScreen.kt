package com.li_routi.feature.mypage.screen

import android.content.Context
import android.net.Uri
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.PickVisualMediaRequest
import androidx.activity.result.contract.ActivityResultContracts
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
import androidx.compose.runtime.saveable.Saver
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.alpha
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.ColorFilter
import androidx.compose.ui.input.pointer.pointerInput
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.LocalFocusManager
import androidx.compose.ui.platform.LocalSoftwareKeyboardController
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.core.content.FileProvider
import coil.compose.AsyncImage
import com.li_routi.core.designsystem.R
import com.li_routi.core.designsystem.component.LiroutiAvatar
import com.li_routi.core.designsystem.component.LiroutiTextField
import com.li_routi.core.designsystem.theme.LiroutiFrontendTheme
import com.li_routi.core.designsystem.theme.LiroutiTheme
import com.li_routi.feature.mypage.component.EditProfileTopBar
import com.li_routi.feature.mypage.component.PhotoSourceBottomSheet
import java.io.File

/**
 * 프로필 수정(닉네임/프로필 사진) 화면. Figma node `205:18107`("마이") 기준.
 *
 * 마이페이지의 "프로필 수정" 버튼으로 진입한다. 사진 편집 배지를 탭하면 "사진 촬영하기"/"앨범에서
 * 가져오기" 바텀시트가 뜨고([PhotoSourceBottomSheet], Figma node `6075:25632` 기준), 실제 업로드는
 * "저장" 탭 시 [onSaveClick]으로 골라둔 [Uri]를 넘겨 처리한다.
 */
@Composable
fun EditProfileScreen(
    initialNickname: String,
    profileImageUrl: String?,
    onBackClick: () -> Unit,
    onCancelClick: () -> Unit,
    onSaveClick: (nickname: String, imageUri: Uri?) -> Unit,
    isSaving: Boolean = false,
    modifier: Modifier = Modifier,
) {
    val context = LocalContext.current
    var nickname by remember(initialNickname) { mutableStateOf(initialNickname) }
    var selectedImageUri by rememberSaveable(stateSaver = UriSaver) { mutableStateOf<Uri?>(null) }
    var pendingCameraUri by rememberSaveable(stateSaver = UriSaver) { mutableStateOf<Uri?>(null) }
    var showPhotoSourceSheet by remember { mutableStateOf(false) }
    val isNicknameBlank = nickname.isBlank()
    // 닉네임도 안 바꾸고 사진도 새로 안 골랐으면 서버에 보낼 변경 사항이 없어 저장을 막는다 — 그냥
    // 불필요한 요청을 줄이기 위함이며, 사진을 안 바꿔도 저장 자체는 안전하다(사진 유지 로직은
    // AuthRepositoryImpl.resolveProfileImageKey 참고 — 새 이미지가 없으면 기존 사진을 재업로드해
    // 서버의 "profileImageKey=null → 삭제" 처리 문제를 우회한다).
    val hasChanges = nickname != initialNickname || selectedImageUri != null
    val focusManager = LocalFocusManager.current
    val keyboardController = LocalSoftwareKeyboardController.current
    val galleryLauncher = rememberLauncherForActivityResult(
        ActivityResultContracts.PickVisualMedia(),
    ) { uri -> if (uri != null) selectedImageUri = uri }
    val cameraLauncher = rememberLauncherForActivityResult(
        ActivityResultContracts.TakePicture(),
    ) { success -> if (success) selectedImageUri = pendingCameraUri }

    Box(modifier = modifier.fillMaxSize()) {
        Column(
            modifier = Modifier
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
                    profileImageUrl = profileImageUrl,
                    selectedImageUri = selectedImageUri,
                    onEditPhotoClick = { showPhotoSourceSheet = true },
                    modifier = Modifier.align(Alignment.CenterHorizontally),
                )
                Spacer(modifier = Modifier.height(24.dp))
                LiroutiTextField(
                    value = nickname,
                    onValueChange = { nickname = it },
                    labelText = "닉네임",
                    helperText = "닉네임을 입력해주세요",
                    showHelper = isNicknameBlank,
                    isError = isNicknameBlank,
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
                    onClick = { onSaveClick(nickname, selectedImageUri) },
                    backgroundColor = LiroutiTheme.colors.primaryNormal,
                    textColor = LiroutiTheme.colors.backgroundAlternative,
                    enabled = !isSaving && !isNicknameBlank && hasChanges,
                    modifier = Modifier.weight(1f),
                )
            }
        }

        if (showPhotoSourceSheet) {
            PhotoSourceBottomSheet(
                onDismiss = { showPhotoSourceSheet = false },
                onTakePhotoClick = {
                    showPhotoSourceSheet = false
                    val uri = createProfileCameraCaptureUri(context)
                    pendingCameraUri = uri
                    cameraLauncher.launch(uri)
                },
                onPickAlbumClick = {
                    showPhotoSourceSheet = false
                    galleryLauncher.launch(
                        PickVisualMediaRequest(ActivityResultContracts.PickVisualMedia.ImageOnly),
                    )
                },
            )
        }
    }
}

/** [Uri]는 Bundle에 직접 담기지 않으므로 문자열로 변환해 저장/복원한다. */
private val UriSaver = Saver<Uri?, String>(
    save = { uri -> uri?.toString() },
    restore = { value -> Uri.parse(value) },
)

/** 카메라 앱이 사진을 저장할 임시 파일을 만들고, FileProvider를 통해 접근 가능한 [Uri]로 감싸 반환한다. */
private fun createProfileCameraCaptureUri(context: Context): Uri {
    val imageFile = File.createTempFile("profile_", ".jpg", context.cacheDir)
    return FileProvider.getUriForFile(context, "${context.packageName}.fileprovider", imageFile)
}

private val EditProfileAvatarSize = 80.dp
private val EditProfilePhotoBadgeSize = 24.dp

@Composable
private fun EditProfileAvatar(
    profileImageUrl: String?,
    selectedImageUri: Uri?,
    onEditPhotoClick: () -> Unit,
    modifier: Modifier = Modifier,
) {
    Box(modifier = modifier.size(EditProfileAvatarSize)) {
        LiroutiAvatar(size = EditProfileAvatarSize) {
            val model = selectedImageUri ?: profileImageUrl
            if (model != null) {
                AsyncImage(
                    model = model,
                    contentDescription = "프로필 사진",
                    modifier = Modifier.size(EditProfileAvatarSize),
                    contentScale = ContentScale.Crop,
                )
            }
        }
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
            .alpha(if (enabled) 1f else EditProfileActionButtonDisabledAlpha)
            .background(backgroundColor, RoundedCornerShape(6.dp))
            .clickable(enabled = enabled, onClick = onClick),
        contentAlignment = Alignment.Center,
    ) {
        Text(text = text, style = LiroutiTheme.typography.body2LongMedium, color = textColor)
    }
}

private const val EditProfileActionButtonDisabledAlpha = 0.5f

@Preview(showBackground = true, heightDp = 800)
@Composable
private fun EditProfileScreenPreview() {
    LiroutiFrontendTheme {
        EditProfileScreen(
            initialNickname = "잠자는 개구리",
            profileImageUrl = null,
            onBackClick = {},
            onCancelClick = {},
            onSaveClick = { _, _ -> },
        )
    }
}
