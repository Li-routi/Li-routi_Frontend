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
import com.li_routi.core.designsystem.theme.LiroutiFrontendTheme
import com.li_routi.core.designsystem.theme.LiroutiTheme
import com.li_routi.feature.home.navigation.RoutineAuthUploadScreenActions
import com.li_routi.feature.home.navigation.RoutineAuthUploadScreenActions.Companion.MEMO_MAX_LENGTH
import com.li_routi.feature.home.vm.RoutineAuthBadgeTone
import com.li_routi.feature.home.vm.RoutineAuthSelectableUiModel
import com.li_routi.feature.home.vm.SampleRoutineAuthSelectables
import com.li_routi.feature.home.vm.VerificationPhotoAspectRatio
import com.li_routi.feature.home.vm.decodeBitmapWithExif
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext

/** 미리보기용으로 디코딩할 비트맵의 (긴 변 기준) 최대 픽셀 크기. 전체 해상도 디코딩으로 인한 OOM/버벅임을 막는다. */
private const val PhotoPreviewTargetSizePx = 1024

/**
 * 촬영 후 메모/루틴 선택(업로드) 화면 (Figma `촬영 후 메모/선택`, node `2176:20314` 등).
 *
 * - 메모는 선택 입력 (최대 [MEMO_MAX_LENGTH]자)
 * - 루틴 1개 이상 선택 시 하단 업로드 버튼 활성
 * - 뒤로가기/X → 저장 없이 즉시 이탈
 * - 업로드 실패 시에만 토스트 표시
 */
@Composable
fun RoutineAuthUploadScreen(
    actions: RoutineAuthUploadScreenActions,
    memo: String,
    routines: List<RoutineAuthSelectableUiModel>,
    selectedRoutineIds: Set<String>,
    isUploadEnabled: Boolean,
    isUploading: Boolean,
    showUploadFailedToast: Boolean,
    uploadErrorMessage: String? = null,
    photoUri: Uri? = null,
    modifier: Modifier = Modifier,
) {
    BackHandler(onBack = actions::onBackClick)

    val focusManager = LocalFocusManager.current
    val keyboardController = LocalSoftwareKeyboardController.current

    Scaffold(
        modifier = modifier
            .fillMaxSize()
            // 콘텐츠 Column에만 있으면 topBar/bottomBar 영역은 아예 범위 밖이라 탭해도
            // 키보드가 안 닫혔다. Scaffold 전체로 옮겨 그 영역의 빈 공간도 커버한다.
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
                verticalArrangement = Arrangement.spacedBy(12.dp),
            ) {
                UploadActionButton(
                    enabled = isUploadEnabled,
                    isUploading = isUploading,
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

    // Figma node 4424:52039 "서버 오류" 다이얼로그. "취소"는 에러만 지우고 화면에 남아 재시도할 수
    // 있게 하고, "임시 저장"은 저장 없이 플로우를 완전히 닫는다(onCloseClick과 동일하게 처리).
    if (showUploadFailedToast) {
        LiroutiConfirmDialog(
            title = "서버 오류",
            message = uploadErrorMessage ?: "서버 통신 상태가 원활하지 않습니다.",
            confirmText = "임시 저장",
            isConfirmDestructive = false,
            onConfirm = actions::onCloseClick,
            onDismissRequest = actions::onDismissUploadFailedToast,
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
                runCatching { decodeBitmapWithExif(context, uri, PhotoPreviewTargetSizePx) }
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
            Text(
                text = item.title,
                style = LiroutiTheme.typography.body2,
                color = LiroutiTheme.colors.labelStrong,
                maxLines = 1,
                overflow = TextOverflow.Ellipsis,
            )
            if (item.dueLabel != null) {
                Row(
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.spacedBy(6.dp),
                ) {
                    Text(
                        text = item.dueLabel,
                        style = LiroutiTheme.typography.caption,
                        color = LiroutiTheme.colors.labelInfo,
                    )
                    if (item.subtitle != null) {
                        LiroutiDivider(
                            orientation = LiroutiDividerOrientation.Vertical,
                            color = LiroutiTheme.colors.borderStrong,
                            modifier = Modifier.height(10.dp),
                        )
                        Text(
                            text = item.subtitle,
                            style = LiroutiTheme.typography.caption,
                            color = LiroutiTheme.colors.labelInfo,
                        )
                    }
                }
            }
        }
        LiroutiBadge(
            text = item.categoryLabel,
            color = when (item.badgeTone) {
                RoutineAuthBadgeTone.Secondary -> LiroutiBadgeColor.Blue
                RoutineAuthBadgeTone.Challenge -> LiroutiBadgeColor.Green
            },
        )
    }
}

@Composable
private fun UploadActionButton(
    enabled: Boolean,
    isUploading: Boolean,
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
        if (isUploading) {
            CircularProgressIndicator(
                modifier = Modifier.size(20.dp),
                color = contentColor,
                strokeWidth = 2.dp,
            )
        } else {
            Text(
                text = "업로드",
                style = LiroutiTheme.typography.body2,
                color = contentColor,
            )
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
            showUploadFailedToast = false,
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
            showUploadFailedToast = false,
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
            showUploadFailedToast = true,
            uploadErrorMessage = "지금은 인증할 수 없습니다. 이미 완료했거나 가능한 시간이 아닙니다.",
        )
    }
}
