package com.li_routi.feature.home.screen

import android.Manifest
import android.content.Context
import android.content.pm.PackageManager
import android.graphics.Bitmap
import android.net.Uri
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.camera.core.CameraSelector
import androidx.camera.core.ImageCapture
import androidx.camera.core.ImageCaptureException
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.BoxWithConstraints
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.navigationBarsPadding
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.statusBarsPadding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.DisposableEffect
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.blur
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.clipToBounds
import androidx.compose.ui.draw.drawWithContent
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.ColorFilter
import androidx.compose.ui.graphics.asImageBitmap
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.LocalInspectionMode
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.IntOffset
import androidx.compose.ui.unit.IntSize
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.core.content.ContextCompat
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.LifecycleEventObserver
import androidx.lifecycle.compose.LocalLifecycleOwner
import com.li_routi.core.common.ui.camera.LiroutiCameraPreview
import com.li_routi.core.common.ui.camera.captureLiroutiCameraPhoto
import com.li_routi.core.designsystem.R
import com.li_routi.core.designsystem.foundation.color.ScrimStrong
import com.li_routi.core.designsystem.theme.LiroutiFrontendTheme
import com.li_routi.core.designsystem.theme.LiroutiTheme
import com.li_routi.feature.home.navigation.RoutineAuthCameraScreenActions
import com.li_routi.feature.home.vm.VerificationPhotoAspectRatio
import com.li_routi.feature.home.vm.VerificationPhotoTopCropBias
import kotlin.math.roundToInt

/**
 * "루틴 인증하기" 카메라 화면 (Figma node `2156:32485`).
 *
 * CameraX 프리뷰/촬영을 연동한다. [isCameraActive]가 false이면 카메라를 언바인딩한다.
 */
@Composable
fun RoutineAuthCameraScreen(
    actions: RoutineAuthCameraScreenActions,
    modifier: Modifier = Modifier,
    isCameraActive: Boolean = true,
) {
    // Preview에는 ActivityResultRegistry가 없어 런처 등록 시 크래시 난다.
    if (LocalInspectionMode.current) {
        RoutineAuthCameraLayout(
            actions = actions,
            hasCameraPermission = true,
            isTorchOn = false,
            isCapturing = false,
            previewSnapshot = null,
            onToggleLens = {},
            onToggleFlash = {},
            onShutterClick = {},
            cameraContent = {
                Box(
                    modifier = Modifier
                        .fillMaxSize()
                        .background(Color.Black),
                )
            },
            modifier = modifier,
        )
        return
    }

    val context = LocalContext.current
    val lifecycleOwner = LocalLifecycleOwner.current
    var hasCameraPermission by remember {
        mutableStateOf(context.isCameraPermissionGranted())
    }
    val permissionLauncher = rememberLauncherForActivityResult(
        ActivityResultContracts.RequestPermission(),
    ) { granted ->
        hasCameraPermission = granted
    }

    // 설정에서 권한을 바꾼 뒤 복귀해도 상태가 갱신되도록 ON_RESUME마다 다시 확인한다.
    DisposableEffect(lifecycleOwner, context) {
        val observer = LifecycleEventObserver { _, event ->
            if (event == Lifecycle.Event.ON_RESUME) {
                hasCameraPermission = context.isCameraPermissionGranted()
            }
        }
        lifecycleOwner.lifecycle.addObserver(observer)
        onDispose { lifecycleOwner.lifecycle.removeObserver(observer) }
    }

    LaunchedEffect(isCameraActive) {
        if (isCameraActive && !hasCameraPermission) {
            permissionLauncher.launch(Manifest.permission.CAMERA)
        }
    }

    var lensFacing by remember { mutableIntStateOf(CameraSelector.LENS_FACING_BACK) }
    var isTorchOn by remember { mutableStateOf(false) }
    var imageCapture by remember { mutableStateOf<ImageCapture?>(null) }
    var isCapturing by remember { mutableStateOf(false) }
    var previewSnapshot by remember { mutableStateOf<Bitmap?>(null) }

    RoutineAuthCameraLayout(
        actions = actions,
        hasCameraPermission = hasCameraPermission,
        isTorchOn = isTorchOn,
        isCapturing = isCapturing,
        previewSnapshot = previewSnapshot,
        onToggleLens = {
            lensFacing = if (lensFacing == CameraSelector.LENS_FACING_BACK) {
                CameraSelector.LENS_FACING_FRONT
            } else {
                CameraSelector.LENS_FACING_BACK
            }
            // 전면 등 플래시 유닛 없는 렌즈로 바꿀 때 손전등 상태 초기화.
            isTorchOn = false
        },
        onToggleFlash = {
            isTorchOn = !isTorchOn
        },
        onShutterClick = {
            val capture = imageCapture
            if (capture == null) return@RoutineAuthCameraLayout
            isCapturing = true
            captureLiroutiCameraPhoto(
                context = context,
                imageCapture = capture,
                executor = ContextCompat.getMainExecutor(context),
                onSuccess = { uri ->
                    isCapturing = false
                    actions.onCaptureSuccess(uri)
                },
                onError = { _: ImageCaptureException ->
                    isCapturing = false
                },
                filePrefix = "routine_auth",
            )
        },
        onRequestPermission = {
            permissionLauncher.launch(Manifest.permission.CAMERA)
        },
        cameraContent = {
            LiroutiCameraPreview(
                lensFacing = lensFacing,
                flashMode = ImageCapture.FLASH_MODE_OFF,
                isActive = isCameraActive,
                torchEnabled = isTorchOn,
                onImageCaptureReady = { capture: ImageCapture? ->
                    imageCapture = capture
                },
                onPreviewSnapshot = { previewSnapshot = it },
                modifier = Modifier.fillMaxSize(),
            )
        },
        modifier = modifier,
    )
}

// Figma `backdrop-blur(5px)` + rgba(93,93,93,0.6) — minSdk 24라 실제 backdrop blur(API 31+)를
// 못 써서 ChatBox.ChatDateDividerBackground와 동일한 값의 반투명 단색으로 대체한다.
private val HintPillScrimColor = ScrimStrong
private val HintPillShape = RoundedCornerShape(percent = 50)

/**
 * 상/하단 크롭 밴드에 적용하는 블러 반경. Figma `backdrop-blur(5px)`을 그대로 dp로 옮기면(5dp)
 * 라이브 카메라 화면처럼 디테일이 많은 배경 위에서는 60% 불투명 스크림에 묻혀 거의 안 보여서,
 * 실기기에서 눈에 띄는 수준(약 3배)으로 키웠다.
 */
private val CropBandBlurRadius = 16.dp

/**
 * Figma node `4734:42024`/`5562:14547`: 프리뷰가 화면 전체(상태바/내비게이션 바 영역까지)를 꽉
 * 채우고, 닫기·안내 문구·하단 컨트롤은 그 위에 얹힌 오버레이다 — 흰 배경의 별도 상단/하단 바가
 * 아니다. 상/하단에는 실제 인증 사진 크롭 시 잘려나갈 영역을 미리 보여주는 블러+스크림을 깔아
 * 오버레이 가독성과 크롭 안내를 함께 담당한다(Figma `backdrop-blur(5px)`).
 *
 * 블러는 라이브 프리뷰를 직접 캡처해 씌우지 않고 [previewSnapshot](정적 [Bitmap])에 적용한다.
 * [com.li_routi.core.common.ui.camera.LiroutiCameraPreview]가 내부 `PreviewView`를
 * `ImplementationMode.COMPATIBLE`(TextureView)로 강제해도, TextureView처럼 하드웨어 합성되는
 * 라이브 콘텐츠는 GraphicsLayer로 캡처한 뒤 그 레이어에 `Modifier.blur()`(RenderEffect)를 씌워도
 * 실제로 블러가 적용되지 않는 걸 실기기(Android 16)에서 확인했다 — 정적 비트맵에 직접 블러를
 * 거는 건 PendingVerificationCard에서 이미 검증된 방식이라 그쪽으로 우회한다. 아이콘·텍스트는
 * 흰색(labelReverse)을 쓴다.
 */
@Composable
private fun RoutineAuthCameraLayout(
    actions: RoutineAuthCameraScreenActions,
    hasCameraPermission: Boolean,
    isTorchOn: Boolean,
    isCapturing: Boolean,
    previewSnapshot: Bitmap?,
    onToggleLens: () -> Unit,
    onToggleFlash: () -> Unit,
    onShutterClick: () -> Unit,
    cameraContent: @Composable () -> Unit,
    modifier: Modifier = Modifier,
    onRequestPermission: () -> Unit = {},
) {
    // dimmerDefault(순검정 60%)는 라이브 카메라 배경 위에서 너무 어둡다는 피드백으로, 더 밝은
    // 회색 톤인 dimmerSecondary로 바꿨다. 완전 흰색은 위에 얹히는 흰색(labelReverse) 아이콘/
    // 텍스트 대비를 해쳐서 회색 선에서 조정했다.
    val dimmerColor = LiroutiTheme.colors.dimmerSecondary

    BoxWithConstraints(
        modifier = modifier
            .fillMaxSize()
            .background(Color.Black),
    ) {
        // 실제 크롭(centerCropToRatio)은 항상 폭을 그대로 두고 세로만 VerificationPhotoAspectRatio에
        // 맞춰 잘라내므로, 선명하게 남는 높이 = 화면 폭 × 비율 — 화면 "높이"와는 무관하다. 즉 화면이
        // 길어질수록 그 늘어난 만큼이 전부 상/하단 블러 밴드로 간다(선명한 영역 길이는 기기와 무관하게
        // 고정). 이 공식은 PhotoProcessing.centerCropToRatio가 하는 계산을 그대로 미러링한 것이라
        // 안내와 실제 크롭 결과가 항상 정확히 일치한다.
        val sharpHeight = (maxWidth * VerificationPhotoAspectRatio).coerceAtMost(maxHeight)
        val totalCropHeight = maxHeight - sharpHeight
        val topBandHeight = totalCropHeight * VerificationPhotoTopCropBias
        val bottomBandHeight = totalCropHeight * (1f - VerificationPhotoTopCropBias)
        // drawWithContent 람다 안(DrawScope)에서는 BoxWithConstraintsScope의 maxHeight를 암시적
        // 리시버로 바로 못 써서(컴파일 에러), 비율을 미리 평범한 Float로 계산해둔다.
        val topBandFraction = topBandHeight / maxHeight
        val bottomBandFraction = bottomBandHeight / maxHeight

        Box(modifier = Modifier.fillMaxSize()) {
            if (hasCameraPermission) {
                cameraContent()
            } else {
                CameraPermissionDenied(
                    onRequestPermission = onRequestPermission,
                    modifier = Modifier.fillMaxSize(),
                )
            }
        }

        // 상단 크롭 아웃 블러 — 닫기 버튼 가독성 확보 + 잘려나갈 영역 안내를 겸한다.
        // previewSnapshot의 상단 topBandFraction만큼을 밴드 크기에 맞춰 그린다.
        Box(
            modifier = Modifier
                .align(Alignment.TopCenter)
                .fillMaxWidth()
                .height(topBandHeight)
                .clipToBounds(),
        ) {
            Box(
                modifier = Modifier
                    .matchParentSize()
                    .blur(CropBandBlurRadius)
                    .drawWithContent {
                        val snapshot = previewSnapshot ?: return@drawWithContent
                        val srcHeight = (snapshot.height * topBandFraction)
                            .roundToInt()
                            .coerceIn(1, snapshot.height)
                        drawImage(
                            image = snapshot.asImageBitmap(),
                            srcOffset = IntOffset.Zero,
                            srcSize = IntSize(snapshot.width, srcHeight),
                            dstSize = IntSize(size.width.toInt(), size.height.toInt()),
                        )
                    },
            )
            Box(
                modifier = Modifier
                    .matchParentSize()
                    .background(dimmerColor),
            )
        }
        Image(
            painter = painterResource(id = R.drawable.close),
            contentDescription = "닫기",
            // 터치 영역은 접근성 최소 권장 크기(48dp)로 확보하고, 안쪽 padding으로 시각적 아이콘
            // 크기(28dp)는 그대로 유지한다.
            modifier = Modifier
                .align(Alignment.TopEnd)
                .statusBarsPadding()
                .padding(horizontal = 6.dp)
                .size(48.dp)
                .clickable(onClick = actions::onBackClick)
                .padding(10.dp),
            colorFilter = ColorFilter.tint(LiroutiTheme.colors.labelReverse),
        )

        // 하단 크롭 아웃 블러+스크림 — 기기와 무관하게 항상 bottomBandHeight 비율 그대로 고정
        // 크기로 그린다. 컨트롤 실제 높이(내비게이션 바 인셋 등)에 맞춰 늘어나지 않게, 안내
        // 문구/컨트롤(아래 Column)과 분리된 독립적인 크기를 갖는다.
        Box(
            modifier = Modifier
                .align(Alignment.BottomCenter)
                .fillMaxWidth()
                .height(bottomBandHeight)
                .clipToBounds(),
        ) {
            Box(
                modifier = Modifier
                    .matchParentSize()
                    .blur(CropBandBlurRadius)
                    .drawWithContent {
                        // previewSnapshot의 하단 bottomBandFraction만큼(끝에서부터)을 밴드
                        // 크기에 맞춰 그린다.
                        val snapshot = previewSnapshot ?: return@drawWithContent
                        val srcHeight = (snapshot.height * bottomBandFraction)
                            .roundToInt()
                            .coerceIn(1, snapshot.height)
                        drawImage(
                            image = snapshot.asImageBitmap(),
                            srcOffset = IntOffset(0, snapshot.height - srcHeight),
                            srcSize = IntSize(snapshot.width, srcHeight),
                            dstSize = IntSize(size.width.toInt(), size.height.toInt()),
                        )
                    },
            )
            Box(
                modifier = Modifier
                    .matchParentSize()
                    .background(dimmerColor),
            )
        }

        // 안내 문구 + 컨트롤(전환/셔터/플래시). 위 블러+스크림 밴드와 크기가 독립적이라, 내비게이션
        // 바 인셋 등으로 컨트롤 실제 높이가 밴드보다 커지면 자연히 밴드 밖(선명한 프리뷰 위)으로
        // 올라간다 — 밴드를 컨트롤 높이에 맞춰 늘리면 "여기까지 잘린다"는 안내가 부정확해지므로,
        // 크롭 안내의 정확도를 우선한다.
        Column(
            modifier = Modifier
                .align(Alignment.BottomCenter)
                .fillMaxWidth()
                .navigationBarsPadding()
                .padding(top = 24.dp, bottom = 24.dp),
            horizontalAlignment = Alignment.CenterHorizontally,
        ) {
            if (hasCameraPermission) {
                Text(
                    text = "가로로 촬영해 주세요",
                    style = LiroutiTheme.typography.body2LongMedium,
                    // 어두운 프리뷰 위에서도 읽히도록 reverse + scrim
                    color = LiroutiTheme.colors.labelReverse,
                    modifier = Modifier
                        .background(
                            color = HintPillScrimColor,
                            shape = HintPillShape,
                        )
                        .padding(horizontal = 12.dp, vertical = 8.dp),
                )
                Spacer(modifier = Modifier.height(20.dp))
            }
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 40.dp),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically,
            ) {
                CameraControlAction(
                    iconResId = R.drawable.cameraswitch,
                    label = "전환",
                    onClick = onToggleLens,
                )
                Box(
                    modifier = Modifier
                        .size(64.dp)
                        .clickable(
                            enabled = !isCapturing && hasCameraPermission,
                            onClick = onShutterClick,
                        ),
                    contentAlignment = Alignment.Center,
                ) {
                    Image(
                        painter = painterResource(id = R.drawable.shutter__outer),
                        contentDescription = null,
                        modifier = Modifier.size(64.dp),
                        colorFilter = ColorFilter.tint(LiroutiTheme.colors.labelReverse),
                    )
                    Image(
                        painter = painterResource(id = R.drawable.shutter),
                        contentDescription = "촬영",
                        modifier = Modifier.size(52.dp),
                        colorFilter = ColorFilter.tint(LiroutiTheme.colors.labelReverse),
                    )
                }
                CameraControlAction(
                    iconResId = R.drawable.flash,
                    // 폭 고정 + 짧은 라벨로 토글 시 셔터가 밀리지 않게 한다.
                    label = if (isTorchOn) "켜짐" else "플래시",
                    onClick = onToggleFlash,
                )
            }
        }
    }
}

@Composable
private fun CameraPermissionDenied(
    onRequestPermission: () -> Unit,
    modifier: Modifier = Modifier,
) {
    Box(
        modifier = modifier.background(Color.Black),
        contentAlignment = Alignment.Center,
    ) {
        Column(
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.spacedBy(12.dp),
            modifier = Modifier.padding(24.dp),
        ) {
            Text(
                text = "카메라 권한이 필요해요",
                style = LiroutiTheme.typography.body2,
                color = LiroutiTheme.colors.labelReverse,
                textAlign = TextAlign.Center,
            )
            Box(
                modifier = Modifier
                    .clip(RoundedCornerShape(6.dp))
                    .background(LiroutiTheme.colors.primaryNormal)
                    .clickable(onClick = onRequestPermission)
                    .padding(horizontal = 16.dp, vertical = 10.dp),
            ) {
                Text(
                    text = "권한 허용",
                    style = LiroutiTheme.typography.body2,
                    color = LiroutiTheme.colors.labelReverse,
                )
            }
        }
    }
}

@Composable
private fun CameraControlAction(
    @androidx.annotation.DrawableRes iconResId: Int,
    label: String,
    onClick: () -> Unit,
    modifier: Modifier = Modifier,
) {
    Column(
        // 좌우 컨트롤 폭을 맞춰 SpaceBetween에서 셔터가 한쪽으로 밀리지 않게 한다.
        modifier = modifier
            .width(64.dp)
            .clickable(onClick = onClick),
        horizontalAlignment = Alignment.CenterHorizontally,
    ) {
        Image(
            painter = painterResource(id = iconResId),
            contentDescription = label,
            modifier = Modifier.size(24.dp),
            // 카메라 프리뷰 위에 얹히는 오버레이라 흰색(labelReverse)을 쓴다.
            colorFilter = ColorFilter.tint(LiroutiTheme.colors.labelReverse),
        )
        Spacer(modifier = Modifier.height(4.dp))
        Text(
            text = label,
            // Figma Body4 13/16
            style = LiroutiTheme.typography.body3Regular.copy(lineHeight = 16.sp),
            color = LiroutiTheme.colors.labelReverse,
            textAlign = TextAlign.Center,
            maxLines = 1,
        )
    }
}

private fun Context.isCameraPermissionGranted(): Boolean =
    ContextCompat.checkSelfPermission(this, Manifest.permission.CAMERA) ==
        PackageManager.PERMISSION_GRANTED

private object PreviewRoutineAuthCameraScreenActions : RoutineAuthCameraScreenActions {
    override fun onBackClick() = Unit
    override fun onCaptureSuccess(photoUri: Uri) = Unit
}

@Preview(showBackground = true, widthDp = 360, heightDp = 800)
@Composable
private fun RoutineAuthCameraScreenPreview() {
    LiroutiFrontendTheme {
        RoutineAuthCameraScreen(
            actions = PreviewRoutineAuthCameraScreenActions,
            isCameraActive = false,
        )
    }
}
