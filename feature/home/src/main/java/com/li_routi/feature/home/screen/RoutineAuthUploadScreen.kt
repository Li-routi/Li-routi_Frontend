package com.li_routi.feature.home.screen

import android.graphics.Bitmap
import android.net.Uri
import android.util.Log
import androidx.activity.compose.BackHandler
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.gestures.detectTapGestures
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.aspectRatio
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.navigationBarsPadding
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.statusBarsPadding
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.BiasAlignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.ColorFilter
import androidx.compose.ui.graphics.asImageBitmap
import androidx.compose.ui.input.pointer.pointerInput
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.LocalFocusManager
import androidx.compose.ui.platform.LocalSoftwareKeyboardController
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.li_routi.core.designsystem.R
import com.li_routi.core.designsystem.component.CheckBoxState
import com.li_routi.core.designsystem.component.CustomCheckBox
import com.li_routi.core.designsystem.component.LiroutiBadge
import com.li_routi.core.designsystem.component.LiroutiBadgeColor
import com.li_routi.core.designsystem.component.LiroutiConfirmDialog
import com.li_routi.core.designsystem.component.LiroutiDivider
import com.li_routi.core.designsystem.component.LiroutiDividerOrientation
import com.li_routi.core.designsystem.component.LiroutiTextField
import com.li_routi.core.designsystem.component.LiroutiToast
import com.li_routi.core.designsystem.component.LiroutiToastStyle
import com.li_routi.core.designsystem.theme.LiroutiFrontendTheme
import com.li_routi.core.designsystem.theme.LiroutiTheme
import com.li_routi.feature.home.component.toFigmaDotColor
import com.li_routi.feature.home.navigation.RoutineAuthUploadScreenActions
import com.li_routi.feature.home.navigation.RoutineAuthUploadScreenActions.Companion.MEMO_MAX_LENGTH
import com.li_routi.feature.home.vm.RoutineAuthBadgeTone
import com.li_routi.feature.home.vm.RoutineAuthSelectableUiModel
import com.li_routi.feature.home.vm.SampleRoutineAuthSelectables
import com.li_routi.feature.home.vm.VerificationPhotoAspectRatio
import com.li_routi.feature.home.vm.VerificationPhotoCropAlignmentBias
import com.li_routi.feature.home.vm.decodeBitmapWithExif
import com.li_routi.feature.home.vm.rotateToLandscapeIfPortrait
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext

/** 미리보기용으로 디코딩할 비트맵의 (긴 변 기준) 최대 픽셀 크기. 전체 해상도 디코딩으로 인한 OOM/버벅임을 막는다. */
private const val PhotoPreviewTargetSizePx = 1024

/**
 * 촬영 후 메모/루틴 선택(업로드) 화면 (Figma `촬영 후 메모/선택`).
 *
 * - 메모는 선택 입력 (최대 [MEMO_MAX_LENGTH]자)
 * - 루틴 1개 이상 선택 시 하단 업로드 버튼 활성
 * - 뒤로가기/X → 이탈 확인 다이얼로그
 * - 업로드 실패/성공 → 버튼 위 16dp 토스트
 * - 업로드 성공 후 버튼「완료」
 */
@Composable
fun RoutineAuthUploadScreen(
    actions: RoutineAuthUploadScreenActions,
    memo: String,
    routines: List<RoutineAuthSelectableUiModel>,
    selectedRoutineIds: Set<String>,
    isUploadEnabled: Boolean,
    isUploading: Boolean,
    isUploadCompleted: Boolean = false,
    toastMessage: String? = null,
    showExitConfirmDialog: Boolean = false,
    onDismissExitConfirmDialog: () -> Unit = {},
    onConfirmExit: () -> Unit = {},
    photoUri: Uri? = null,
    modifier: Modifier = Modifier,
) {
    BackHandler(onBack = actions::onBackClick)

    val focusManager = LocalFocusManager.current
    val keyboardController = LocalSoftwareKeyboardController.current

    Scaffold(
        modifier = modifier
            .fillMaxSize()
            .pointerInput(Unit) {
                detectTapGestures(onTap = {
                    keyboardController?.hide()
                    focusManager.clearFocus()
                })
            },
        containerColor = LiroutiTheme.colors.backgroundDefault,
        topBar = {
            RoutineAuthUploadTopBar(
                onBackClick = actions::onBackClick,
                onCloseClick = actions::onCloseClick,
            )
        },
        bottomBar = {
            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .navigationBarsPadding()
                    .padding(horizontal = 16.dp)
                    .padding(bottom = 32.dp),
                // Figma: 토스트와 업로드 버튼 사이 16
                verticalArrangement = Arrangement.spacedBy(16.dp),
            ) {
                toastMessage?.let { message ->
                    LiroutiToast(
                        message = message,
                        style = LiroutiToastStyle.Black,
                        onCloseClick = actions::onDismissUploadFailedToast,
                    )
                }
                UploadActionButton(
                    enabled = isUploadEnabled,
                    isUploading = isUploading,
                    isUploadCompleted = isUploadCompleted,
                    onClick = actions::onUploadClick,
                )
            }
        },
    ) { innerPadding ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(innerPadding)
                .verticalScroll(rememberScrollState())
                .padding(top = 25.dp, bottom = 16.dp),
            verticalArrangement = Arrangement.spacedBy(24.dp),
        ) {
            CapturedPhotoPreview(
                photoUri = photoUri,
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 16.dp),
            )

            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 16.dp),
                verticalArrangement = Arrangement.spacedBy(20.dp),
            ) {
                MemoSection(
                    memo = memo,
                    onMemoChange = actions::onMemoChange,
                )
                RoutineSelectSection(
                    routines = routines,
                    selectedRoutineIds = selectedRoutineIds,
                    onRoutineToggle = actions::onRoutineToggle,
                )
            }
        }
    }

    if (showExitConfirmDialog) {
        LiroutiConfirmDialog(
            title = "화면을 나가시겠어요?",
            message = "작성 중인 메모가 사라져요.",
            confirmText = "나가기",
            cancelText = "취소",
            isConfirmDestructive = true,
            onConfirm = onConfirmExit,
            onDismissRequest = onDismissExitConfirmDialog,
        )
    }
}

@Composable
private fun CapturedPhotoPreview(
    photoUri: Uri?,
    modifier: Modifier = Modifier,
) {
    val context = LocalContext.current
    var bitmap by remember { mutableStateOf<Bitmap?>(null) }

    LaunchedEffect(photoUri) {
        bitmap = null
        bitmap = photoUri?.let { uri ->
            // CodeRabbit 반영: 컴포지션 스레드에서 원본 해상도를 디코딩하면 카메라 캡처 크기에 따라
            // 화면이 멈추거나 OOM이 날 수 있어, IO 디스패처에서 샘플링된 비트맵만 디코딩한다.
            withContext(Dispatchers.IO) {
                runCatching { decodeBitmapWithExif(context, uri, PhotoPreviewTargetSizePx)?.rotateToLandscapeIfPortrait() }
                    .onFailure { Log.w("CapturedPhotoPreview", "사진 디코딩 실패: uri=$uri", it) }
                    .getOrNull()
            }
        }
    }

    // Figma `촬영 후 메모/선택`(3610:26875) 기준 328:184 고정 비율 박스. 원본 비율과 달라도
    // ContentScale.Crop으로 채워 실제 업로드되는 크롭 결과와 미리보기를 일치시킨다.
    Box(
        modifier = modifier
            .fillMaxWidth()
            .aspectRatio(VerificationPhotoAspectRatio)
            .clip(RoundedCornerShape(6.dp))
            .background(LiroutiTheme.colors.backgroundStrong),
        contentAlignment = Alignment.Center,
    ) {
        val currentBitmap = bitmap
        if (currentBitmap == null) {
            Text(
                text = "촬영 사진",
                style = LiroutiTheme.typography.caption,
                color = LiroutiTheme.colors.labelInfo,
            )
            return@Box
        }

        Image(
            bitmap = currentBitmap.asImageBitmap(),
            contentDescription = "촬영 사진",
            modifier = Modifier.fillMaxWidth().aspectRatio(VerificationPhotoAspectRatio),
            // 실제 업로드 크롭(centerCropToRatio + VerificationPhotoTopCropBias)과 같은 결과를
            // 보여주도록, 회전된 이미지 기준 가로 크롭 위치를 같은 비율로 맞춘다.
            alignment = BiasAlignment(horizontalBias = VerificationPhotoCropAlignmentBias, verticalBias = 0f),
            contentScale = ContentScale.Crop,
        )
    }
}

@Composable
private fun RoutineAuthUploadTopBar(
    onBackClick: () -> Unit,
    onCloseClick: () -> Unit,
    modifier: Modifier = Modifier,
) {
    Box(
        modifier = modifier
            .fillMaxWidth()
            .statusBarsPadding()
            .padding(horizontal = 16.dp, vertical = 10.dp),
    ) {
        Image(
            painter = painterResource(id = R.drawable.chevron__left),
            contentDescription = "뒤로가기",
            modifier = Modifier
                .align(Alignment.CenterStart)
                .size(20.dp)
                .clickable(onClick = onBackClick),
            colorFilter = ColorFilter.tint(LiroutiTheme.colors.labelStrong),
        )
        Text(
            text = "루틴 인증하기",
            style = LiroutiTheme.typography.heading2,
            color = LiroutiTheme.colors.labelStrong,
            modifier = Modifier.align(Alignment.Center),
            textAlign = TextAlign.Center,
        )
        Image(
            painter = painterResource(id = R.drawable.close),
            contentDescription = "닫기",
            modifier = Modifier
                .align(Alignment.CenterEnd)
                .size(20.dp)
                .clickable(onClick = onCloseClick),
            colorFilter = ColorFilter.tint(LiroutiTheme.colors.labelStrong),
        )
    }
}

@Composable
private fun MemoSection(
    memo: String,
    onMemoChange: (String) -> Unit,
    modifier: Modifier = Modifier,
) {
    LiroutiTextField(
        value = memo,
        onValueChange = onMemoChange,
        modifier = modifier,
        placeholder = "한줄 메모 (선택, 최대 ${MEMO_MAX_LENGTH}자)",
        labelText = "메모",
        helperText = "",
        showLabel = true,
        showHelper = false,
    )
}

@Composable
private fun RoutineSelectSection(
    routines: List<RoutineAuthSelectableUiModel>,
    selectedRoutineIds: Set<String>,
    onRoutineToggle: (String) -> Unit,
    modifier: Modifier = Modifier,
) {
    Column(
        modifier = modifier.fillMaxWidth(),
        verticalArrangement = Arrangement.spacedBy(8.dp),
    ) {
        Text(
            text = "인증할 루틴 선택",
            style = LiroutiTheme.typography.body2,
            color = LiroutiTheme.colors.labelStrong,
        )
        Column(verticalArrangement = Arrangement.spacedBy(8.dp)) {
            routines.forEach { item ->
                RoutineSelectRow(
                    item = item,
                    selected = item.id in selectedRoutineIds,
                    onClick = { onRoutineToggle(item.id) },
                )
            }
        }
    }
}

@Composable
private fun RoutineSelectRow(
    item: RoutineAuthSelectableUiModel,
    selected: Boolean,
    onClick: () -> Unit,
    modifier: Modifier = Modifier,
) {
    val badgeText = when (item.badgeTone) {
        RoutineAuthBadgeTone.Challenge -> item.categoryLabel
        // 그룹: 방 이름. 개인(방 없음): Secondary 배지 숨김.
        RoutineAuthBadgeTone.Secondary -> item.subtitle
    }
    Row(
        modifier = modifier
            .fillMaxWidth()
            .clip(RoundedCornerShape(6.dp))
            .border(
                width = 1.dp,
                color = LiroutiTheme.colors.borderAlternative,
                shape = RoundedCornerShape(6.dp),
            )
            .clickable(onClick = onClick)
            .padding(horizontal = 14.dp, vertical = 10.dp),
        horizontalArrangement = Arrangement.spacedBy(12.dp),
        verticalAlignment = Alignment.CenterVertically,
    ) {
        CustomCheckBox(
            state = if (selected) CheckBoxState.B else CheckBoxState.A,
            onClick = onClick,
        )
        Column(modifier = Modifier.weight(1f)) {
            Row(
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.spacedBy(6.dp),
            ) {
                item.categoryColor?.let { color ->
                    Box(
                        modifier = Modifier
                            .size(6.dp)
                            .clip(CircleShape)
                            .background(color.toFigmaDotColor()),
                    )
                }
                Text(
                    text = item.title,
                    style = LiroutiTheme.typography.body2,
                    color = LiroutiTheme.colors.labelStrong,
                    maxLines = 1,
                    overflow = TextOverflow.Ellipsis,
                )
            }
            // Figma List: "카테고리 | 마감 HH:mm"
            if (item.dueLabel != null) {
                Row(
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.spacedBy(6.dp),
                ) {
                    Text(
                        text = item.categoryLabel,
                        style = LiroutiTheme.typography.caption,
                        color = LiroutiTheme.colors.labelInfo,
                    )
                    LiroutiDivider(
                        orientation = LiroutiDividerOrientation.Vertical,
                        color = LiroutiTheme.colors.borderStrong,
                        modifier = Modifier.height(10.dp),
                    )
                    Text(
                        text = item.dueLabel,
                        style = LiroutiTheme.typography.caption,
                        color = LiroutiTheme.colors.labelInfo,
                    )
                }
            }
        }
        if (badgeText != null) {
            LiroutiBadge(
                text = badgeText,
                color = when (item.badgeTone) {
                    RoutineAuthBadgeTone.Secondary -> LiroutiBadgeColor.Blue
                    RoutineAuthBadgeTone.Challenge -> LiroutiBadgeColor.Orange
                },
            )
        }
    }
}

@Composable
private fun UploadActionButton(
    enabled: Boolean,
    isUploading: Boolean,
    isUploadCompleted: Boolean,
    onClick: () -> Unit,
    modifier: Modifier = Modifier,
) {
    // TODO(design-system): Button_Filled 완성 시 교체. enabled/disabled 상태는 UX상 지금 표시한다.
    val background = if (enabled) {
        LiroutiTheme.colors.primaryNormal
    } else {
        LiroutiTheme.colors.backgroundAlternative
    }
    val contentColor = if (enabled) {
        LiroutiTheme.colors.labelReverse
    } else {
        LiroutiTheme.colors.labelInfo
    }
    Box(
        modifier = modifier
            .fillMaxWidth()
            .height(44.dp)
            .clip(RoundedCornerShape(6.dp))
            .background(background)
            .clickable(enabled = enabled, onClick = onClick),
        contentAlignment = Alignment.Center,
    ) {
        when {
            isUploading -> {
                CircularProgressIndicator(
                    modifier = Modifier.size(20.dp),
                    color = contentColor,
                    strokeWidth = 2.dp,
                )
            }
            isUploadCompleted -> {
                Text(
                    text = "완료",
                    style = LiroutiTheme.typography.body2,
                    color = contentColor,
                )
            }
            else -> {
                Text(
                    text = "업로드",
                    style = LiroutiTheme.typography.body2,
                    color = contentColor,
                )
            }
        }
    }
}

private object PreviewRoutineAuthUploadScreenActions : RoutineAuthUploadScreenActions {
    override fun onBackClick() = Unit
    override fun onCloseClick() = Unit
    override fun onMemoChange(memo: String) = Unit
    override fun onRoutineToggle(routineId: String) = Unit
    override fun onUploadClick() = Unit
    override fun onDismissUploadFailedToast() = Unit
}

@Preview(showBackground = true, heightDp = 800, name = "미선택 / 업로드 비활성")
@Composable
private fun RoutineAuthUploadScreenDisabledPreview() {
    LiroutiFrontendTheme {
        RoutineAuthUploadScreen(
            actions = PreviewRoutineAuthUploadScreenActions,
            memo = "",
            routines = SampleRoutineAuthSelectables,
            selectedRoutineIds = emptySet(),
            isUploadEnabled = false,
            isUploading = false,
        )
    }
}

@Preview(showBackground = true, heightDp = 800, name = "선택 / 업로드 활성")
@Composable
private fun RoutineAuthUploadScreenEnabledPreview() {
    LiroutiFrontendTheme {
        RoutineAuthUploadScreen(
            actions = PreviewRoutineAuthUploadScreenActions,
            memo = "오늘의 루틴 끝",
            routines = SampleRoutineAuthSelectables,
            selectedRoutineIds = setOf("my_0"),
            isUploadEnabled = true,
            isUploading = false,
        )
    }
}

@Preview(showBackground = true, heightDp = 800, name = "업로드 실패 토스트")
@Composable
private fun RoutineAuthUploadScreenFailedPreview() {
    LiroutiFrontendTheme {
        RoutineAuthUploadScreen(
            actions = PreviewRoutineAuthUploadScreenActions,
            memo = "오늘의 루틴 끝",
            routines = SampleRoutineAuthSelectables,
            selectedRoutineIds = setOf("my_0"),
            isUploadEnabled = true,
            isUploading = false,
            toastMessage = "업로드 실패",
        )
    }
}

@Preview(showBackground = true, heightDp = 800, name = "업로드 완료")
@Composable
private fun RoutineAuthUploadScreenCompletedPreview() {
    LiroutiFrontendTheme {
        RoutineAuthUploadScreen(
            actions = PreviewRoutineAuthUploadScreenActions,
            memo = "오늘의 루틴 끝",
            routines = SampleRoutineAuthSelectables,
            selectedRoutineIds = setOf("my_0"),
            isUploadEnabled = true,
            isUploading = false,
            isUploadCompleted = true,
            toastMessage = "업로드가 완료되었습니다!",
        )
    }
}
