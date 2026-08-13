
package com.li_routi.feature.login.screen

import android.content.Context
import android.graphics.Bitmap
import android.graphics.ImageDecoder
import android.net.Uri
import android.os.Build
import android.provider.MediaStore
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.PickVisualMediaRequest
import androidx.activity.result.contract.ActivityResultContracts
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
import androidx.compose.foundation.layout.navigationBarsPadding
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
import androidx.compose.runtime.LaunchedEffect
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
import androidx.compose.ui.graphics.SolidColor
import androidx.compose.ui.graphics.asImageBitmap
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.LocalDensity
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import androidx.core.content.FileProvider
import com.li_routi.core.designsystem.R
import com.li_routi.core.designsystem.component.HorizontalDoubleButton
import com.li_routi.core.designsystem.component.LiroutiAvatar
import com.li_routi.core.designsystem.foundation.color.Neutral100
import com.li_routi.core.designsystem.foundation.color.Neutral96
import com.li_routi.core.designsystem.theme.LiroutiFrontendTheme
import com.li_routi.core.designsystem.theme.LiroutiTheme
import java.io.File
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext

val ProfileHeaderTopOffset = 44.dp
val ProfileHeaderMinWidth = 360.dp
val ProfileHeaderHeight = 48.dp
val ProfileHeaderTitleMinWidth = 328.dp
val ProfileHeaderTitleHeight = 28.dp

val ProfileAvatarTopSpacing = 25.dp
val ProfileAvatarSize = 80.dp

/** 화면 회전 등으로 아바타가 [ProfileAvatarSize]보다 커 보이는 경우를 대비해 여유 있게 디코딩할 배율. */
private const val ProfileAvatarBitmapScaleFactor = 2.5f

val ProfileAvatarBadgeSize = 24.dp
val ProfileAvatarBadgeStartOffset = 60.dp
val ProfileAvatarBadgeBottomOverhang = 2.dp
val ProfileAvatarBadgeBorderWidth = 1.dp
val ProfileAvatarBadgeBorderColor = Neutral96
val ProfileAvatarBadgeBackgroundColor = Neutral100
val ProfileAvatarBadgeIconSize = 16.dp

val ProfileNicknameSectionTopSpacing = 20.dp
val ProfileNicknameSectionWidth = 328.dp
val ProfileNicknameSectionHeight = 74.dp
val ProfileNicknameLabelHeight = 22.dp
val ProfileNicknameFieldSpacing = 8.dp
val ProfileNicknameInputHeight = 44.dp
val ProfileNicknameInputCornerRadius = 6.dp
val ProfileNicknameInputBorderWidth = 1.dp
val ProfileNicknameInputBorderColor = Neutral96
val ProfileNicknameInputBackgroundColor = Neutral100

val ProfileActionButtonBottomSpacing = 40.dp
val ProfileActionButtonWidth = 328.dp
val ProfileActionButtonHeight = 44.dp

/** 저장 요청이 진행 중일 때 저장 버튼이 비활성 상태임을 나타내기 위해 적용하는 투명도. */
const val ProfileActionButtonDisabledAlpha = 0.5f

const val ProfileDefaultNickname = "잠자는 개구리"

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
                style = LiroutiTheme.typography.heading2SemiBold,
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
    profileImageUri: Uri? = null,
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
        if (profileImageUri != null) {
            LiroutiAvatar(size = avatarSize) {
                ProfileAvatarImage(
                    imageUri = profileImageUri,
                    avatarSize = avatarSize,
                    modifier = Modifier.fillMaxSize(),
                )
            }
        } else {
            LiroutiAvatar(size = avatarSize)
        }
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

@Composable
private fun ProfileAvatarImage(imageUri: Uri, avatarSize: Dp, modifier: Modifier = Modifier) {
    val context = LocalContext.current
    val density = LocalDensity.current
    // 실제 표시 크기보다 여유 있게 디코딩해 화질 저하 없이 메모리 사용량만 줄인다.
    val targetSizePx = remember(avatarSize, density) {
        with(density) { (avatarSize * ProfileAvatarBitmapScaleFactor).roundToPx() }
    }
    var bitmap by remember(imageUri, targetSizePx) { mutableStateOf<Bitmap?>(null) }

    // 비트맵 디코딩은 무거운 작업이라 메인(UI) 스레드를 막지 않도록 IO 디스패처에서 실행한다.
    LaunchedEffect(imageUri, targetSizePx) {
        bitmap = withContext(Dispatchers.IO) {
            loadProfileBitmap(context, imageUri, targetSizePx)
        }
    }

    val loadedBitmap = bitmap ?: return
    Image(
        bitmap = loadedBitmap.asImageBitmap(),
        contentDescription = "프로필 사진",
        modifier = modifier.clip(CircleShape),
        contentScale = ContentScale.Crop,
    )
}

private fun loadProfileBitmap(context: Context, uri: Uri, targetSizePx: Int): Bitmap? = runCatching {
    if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.P) {
        val source = ImageDecoder.createSource(context.contentResolver, uri)
        ImageDecoder.decodeBitmap(source) { decoder, _, _ ->
            decoder.setTargetSize(targetSizePx, targetSizePx)
        }
    } else {
        @Suppress("DEPRECATION")

        MediaStore.Images.Media.getBitmap(context.contentResolver, uri)
    }
}.getOrNull()

/** 카메라 앱이 사진을 저장할 임시 파일을 만들고, FileProvider를 통해 접근 가능한 [Uri]로 감싸 반환한다. */
private fun createCameraCaptureUri(context: Context): Uri {
    val imageFile = File.createTempFile("profile_", ".jpg", context.cacheDir)
    return FileProvider.getUriForFile(context, "${context.packageName}.fileprovider", imageFile)
}

/** 아바타 아래 닉네임 입력 영역. 서버가 내려준 초기 닉네임이 채워진 채로 시작하며, 일반 텍스트
 * 필드처럼 자유롭게 고쳐 쓸 수 있다.
 *
 * [showError]가 true면 입력칸 아래에 [errorText]를 빨간 글씨로 보여준다(닉네임이 공백일 때 사용).
 * 원래 [height]가 라벨+간격+입력칸 높이의 합과 정확히 같아 고정 높이를 줘도 잘렸었는데, 에러 텍스트가
 * 추가되는 경우까지 고려해 `Column`은 내용물 높이에 맞춰 자연스럽게 늘어나게 두고 [height]는 더 이상
 * 강제로 적용하지 않는다(호출부 레이아웃이 이미 유연한 Spacer(weight)로 남는 공간을 채우고 있어 안전).
 */
@Composable
fun ProfileNicknameField(
    nickname: String,
    onNicknameChange: (String) -> Unit,
    modifier: Modifier = Modifier,
    width: Dp = ProfileNicknameSectionWidth,
    height: Dp = ProfileNicknameSectionHeight,
    labelHeight: Dp = ProfileNicknameLabelHeight,
    fieldSpacing: Dp = ProfileNicknameFieldSpacing,
    inputHeight: Dp = ProfileNicknameInputHeight,
    inputCornerRadius: Dp = ProfileNicknameInputCornerRadius,
    inputBorderWidth: Dp = ProfileNicknameInputBorderWidth,
    inputBorderColor: Color = ProfileNicknameInputBorderColor,
    inputBackgroundColor: Color = ProfileNicknameInputBackgroundColor,
    showError: Boolean = false,
    errorText: String = "닉네임을 입력해주세요",
) {
    Column(
        modifier = modifier.width(width),
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
                onValueChange = onNicknameChange,
                singleLine = true,
                textStyle = LiroutiTheme.typography.body2LongRegular.copy(color = LiroutiTheme.colors.labelDefault),
                cursorBrush = SolidColor(LiroutiTheme.colors.labelDefault),
                modifier = Modifier.fillMaxWidth(),
            )
        }
        if (showError) {
            Spacer(modifier = Modifier.height(4.dp))
            Text(
                text = errorText,
                style = LiroutiTheme.typography.captionRegular,
                color = LiroutiTheme.colors.dangerText,
            )
        }
    }
}

/** [Uri]는 Bundle에 직접 담기지 않으므로 문자열로 변환해 저장/복원한다. */
private val UriSaver = Saver<Uri?, String>(
    save = { uri -> uri?.toString() },
    restore = { value -> Uri.parse(value) },
)

@Composable
fun ProfileScreen(
    modifier: Modifier = Modifier,
    initialNickname: String = ProfileDefaultNickname,
    isLoading: Boolean = false,
    onSaveClick: (nickname: String, profileImageUri: Uri?) -> Unit = { _, _ -> },
) {
    val context = LocalContext.current
    var showBottomSheet by remember { mutableStateOf(false) }
    // 카메라 앱이 열려 있는 동안 시스템이 메모리 부족 등으로 화면을 재생성해도
    // 값이 유지되도록 remember 대신 rememberSaveable을 사용한다.
    var nickname by rememberSaveable { mutableStateOf(initialNickname) }
    var profileImageUri by rememberSaveable(stateSaver = UriSaver) { mutableStateOf<Uri?>(null) }
    var pendingCameraUri by rememberSaveable(stateSaver = UriSaver) { mutableStateOf<Uri?>(null) }
    val isNicknameBlank = nickname.isBlank()
    val isSaveEnabled = !isLoading && !isNicknameBlank

    val cameraLauncher = rememberLauncherForActivityResult(
        ActivityResultContracts.TakePicture(),
    ) { success ->
        if (success) {
            profileImageUri = pendingCameraUri
        }
    }

    val galleryLauncher = rememberLauncherForActivityResult(
        ActivityResultContracts.PickVisualMedia(),
    ) { uri ->
        if (uri != null) {
            profileImageUri = uri
        }
    }

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
                profileImageUri = profileImageUri,
            )
            ProfileNicknameField(
                nickname = nickname,
                onNicknameChange = { nickname = it },
                showError = isNicknameBlank,
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
                    // 제스처 내비게이션 바 등 시스템 내비게이션 바 높이만큼 버튼을 위로 밀어 올려, 버튼이 내비게이션 바에 가리지 않도록 한다.
                    .navigationBarsPadding()
                    .padding(bottom = ProfileActionButtonBottomSpacing)
                    // HorizontalDoubleButton에 enabled 옵션이 없어, 로딩 중이거나 닉네임이 공백이면
                    // 시각적으로 흐리게 표시해 비활성 상태임을 알린다.
                    .alpha(if (isSaveEnabled) 1f else ProfileActionButtonDisabledAlpha),
                // 클릭 자체는 항상 열려 있으므로, 비활성 상태일 땐 여기서 막아 연타로 인한 중복 저장
                // 요청이나 빈 닉네임 저장을 방지한다.
                onRightClick = { if (isSaveEnabled) onSaveClick(nickname, profileImageUri) },
            )
        }

        if (showBottomSheet) {
            BottomScreen(
                onDismiss = { showBottomSheet = false },
                onTakePhotoClick = {
                    showBottomSheet = false
                    val uri = createCameraCaptureUri(context)
                    pendingCameraUri = uri
                    cameraLauncher.launch(uri)
                },
                onPickAlbumClick = {
                    showBottomSheet = false
                    galleryLauncher.launch(
                        PickVisualMediaRequest(ActivityResultContracts.PickVisualMedia.ImageOnly),
                    )
                },
            )
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