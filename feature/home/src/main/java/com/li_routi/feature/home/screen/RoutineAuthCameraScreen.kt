package com.li_routi.feature.home.screen

import android.Manifest
import android.content.Context
import android.content.pm.PackageManager
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
import androidx.compose.foundation.layout.heightIn
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
import androidx.compose.ui.graphics.GraphicsContext
import androidx.compose.ui.graphics.drawscope.translate
import androidx.compose.ui.graphics.layer.GraphicsLayer
import androidx.compose.ui.graphics.layer.drawLayer
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.LocalGraphicsContext
import androidx.compose.ui.platform.LocalInspectionMode
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.tooling.preview.Preview
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

    RoutineAuthCameraLayout(
        actions = actions,
        hasCameraPermission = hasCameraPermission,
        isTorchOn = isTorchOn,
        isCapturing = isCapturing,
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
                modifier = Modifier.fillMaxSize(),
            )
        },
        modifier = modifier,
    )
}

// Figma node 5562:14552(Subtract): 360x800 기준 프레임에서 상단 0–88dp(88/800), 하단
// 620–800dp(180/800) 구간이 최종 인증 사진 크롭 시 잘려나간다. 실제 화면 높이에 이 비율을
// 곱해 "나중에 잘리는 영역"을 촬영 전에 블러+스크림으로 미리 보여준다. (가운데 선명하게 찍히는
// 영역을 살짝 더 넓혀달라는 요청으로 원래 Figma 값인 94/800, 186/800에서 각각 6dp씩 줄임)
private const val CropIndicatorTopFraction = 88f / 800f
private const val CropIndicatorBottomFraction = 180f / 800f

// Figma `backdrop-blur(5px)` + rgba(93,93,93,0.6) — minSdk 24라 실제 backdrop blur(API 31+)를
// 못 써서 ChatBox.ChatDateDividerBackground와 동일한 값의 반투명 단색으로 대체한다.
private val HintPillScrimColor = ScrimStrong
private val HintPillShape = RoundedCornerShape(percent = 50)

/** 상/하단 크롭 밴드에 적용하는 블러 반경. Figma `backdrop-blur(5px)`과 동일. */
private val CropBandBlurRadius = 5.dp

/**
 * [GraphicsContext.createGraphicsLayer]로 만든 [GraphicsLayer]를 컴포지션 생명주기에 묶어
 * 관리한다. 카메라 프리뷰(또는 권한 거부 화면)를 한 번 그린 결과를 캡처해뒀다가, 상/하단 크롭
 * 밴드에서 그 프레임에 블러를 씌워 다시 그리는 데 쓴다 — CameraX 바인딩은
 * [com.li_routi.core.common.ui.camera.LiroutiCameraPreview] 하나만 써야 해서(같은 화면에
 * 두 번째 프리뷰를 더 얹으면 새 바인딩이 `unbindAll()`을 부르며 첫 번째 바인딩을 끊어버린다)
 * 실제 카메라를 두 번 그리는 대신 이미 그려진 프레임을 재사용한다.
 */
@Composable
private fun rememberCameraGraphicsLayer(): GraphicsLayer {
    val graphicsContext = LocalGraphicsContext.current
    val layer = remember(graphicsContext) { graphicsContext.createGraphicsLayer() }
    DisposableEffect(graphicsContext, layer) {
        onDispose { graphicsContext.releaseGraphicsLayer(layer) }
    }
    return layer
}

/**
 * Figma node `4734:42024`/`5562:14547`: 프리뷰가 화면 전체(상태바/내비게이션 바 영역까지)를 꽉
 * 채우고, 닫기·안내 문구·하단 컨트롤은 그 위에 얹힌 오버레이다 — 흰 배경의 별도 상단/하단 바가
 * 아니다. 상/하단에는 실제 인증 사진 크롭 시 잘려나갈 영역을 미리 보여주는 블러+스크림을 깔아
 * 오버레이 가독성과 크롭 안내를 함께 담당한다(Figma `backdrop-blur(5px)`).
 * [com.li_routi.core.common.ui.camera.LiroutiCameraPreview]가 내부 `PreviewView`를
 * `ImplementationMode.COMPATIBLE`(TextureView)로 강제해둔 덕에 API 31+ 기기에서는 실제 블러가
 * 걸리고, 그 아래(minSdk 24까지)에서는 [androidx.compose.ui.draw.blur]가 조용히 no-op이라
 * 자동으로 스크림만 남는다(PendingVerificationCard와 동일한 전례). 아이콘·텍스트는 흰색
 * (labelReverse)을 쓴다.
 */
@Composable
private fun RoutineAuthCameraLayout(
    actions: RoutineAuthCameraScreenActions,
    hasCameraPermission: Boolean,
    isTorchOn: Boolean,
    isCapturing: Boolean,
    onToggleLens: () -> Unit,
    onToggleFlash: () -> Unit,
    onShutterClick: () -> Unit,
    cameraContent: @Composable () -> Unit,
    modifier: Modifier = Modifier,
    onRequestPermission: () -> Unit = {},
) {
    val cameraLayer = rememberCameraGraphicsLayer()
    val dimmerColor = LiroutiTheme.colors.dimmerDefault

    BoxWithConstraints(
        modifier = modifier
            .fillMaxSize()
            .background(Color.Black),
    ) {
        val screenHeight = maxHeight
        val topBandHeight = screenHeight * CropIndicatorTopFraction
        val bottomBandHeight = screenHeight * CropIndicatorBottomFraction

        Box(
            modifier = Modifier
                .fillMaxSize()
                // 실제 화면에 그려질 프레임(카메라 또는 권한 거부 화면)을 그대로 캡처해둔다 —
                // 이 레이어는 아래 밴드들에서 블러를 씌워 재사용한다.
                .drawWithContent {
                    cameraLayer.record(this, layoutDirection, IntSize(size.width.toInt(), size.height.toInt())) {
                        this@drawWithContent.drawContent()
                    }
                    drawLayer(cameraLayer)
                },
        ) {
            if (hasCameraPermission) {
                cameraContent()
            } else {
                CameraPermissionDenied(
                    onRequestPermission = onRequestPermission,
                    modifier = Modifier.fillMaxSize(),
                )
            }
        }

        // 상단 크롭 아웃 블러 — 닫기 버튼 가독성 확보 + 잘려나갈 영역 안내를 겸한다. 위에서
        // 캡처해둔 같은 프레임을 밴드 높이만큼 잘라 블러를 씌우고 그 위에 스크림 틴트를 얹는다.
        // 캡처 원점과 이 밴드의 원점이 같은(화면 맨 위) 좌표라 별도 오프셋이 필요 없다.
        Box(
            modifier = Modifier
                .align(Alignment.TopCenter)
                .fillMaxWidth()
                .height(topBandHeight)
                .clipToBounds()
                .blur(CropBandBlurRadius)
                .drawWithContent {
                    drawLayer(cameraLayer)
                    // 스크림도 같은 blur 레이어 안에서 그려지지만 단색이라 블러 유무가 눈에
                    // 띄지 않는다 — 별도로 안 그려도 되지만 명시적으로 남겨 의도를 분명히 한다.
                    drawRect(color = dimmerColor)
                },
        )
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

        // 안내 문구 + 컨트롤(전환/셔터/플래시). heightIn min으로 하단 밴드 높이 하한만 보장하고
        // (Figma 크롭 비율), 실제 컨텐츠가 그보다 커지면(내비게이션 바 인셋 등) 자연히 늘어난다.
        // 블러 배경(캡처된 프레임 재사용) + 스크림 틴트 + 실제 컨트롤을 이 순서로 겹쳐서, 컨트롤
        // 자체(텍스트/버튼)는 블러 레이어 밖에 있어 흐려지지 않고 선명하게 남는다.
        Box(
            modifier = Modifier
                .align(Alignment.BottomCenter)
                .fillMaxWidth()
                .heightIn(min = bottomBandHeight)
                .clipToBounds(),
        ) {
            // matchParentSize(): fillMaxSize()를 쓰면 이 Box가 부모 Box의 크기를 "정하는"
            // 자식이 돼버려서, heightIn(min=...)로 하한만 두려던 부모가 화면 전체 높이로
            // 부풀어 오르고(다른 fillMaxSize 자식이 화면을 가득 채우라고 요구하므로) 그 안의
            // Column(안내문구+버튼)이 기본 정렬(TopStart)로 밀려 화면 맨 위에 나타나는 버그가
            // 있었다. matchParentSize는 부모 크기 결정에 관여하지 않고 "부모가 정해진 뒤" 그
            // 크기에 맞춰지므로, 부모 크기는 여전히 Column 실제 높이(또는 하한값 중 큰 쪽)를
            // 따르고 배경만 그 크기에 맞게 채워진다.
            Box(
                modifier = Modifier
                    .matchParentSize()
                    .blur(CropBandBlurRadius)
                    .drawWithContent {
                        // 캡처된 프레임은 화면 맨 위를 원점으로 하는데, 이 밴드는 화면 아래쪽에
                        // 있으므로 (전체 화면 높이 - 이 밴드 높이)만큼 위로 당겨 그려야 실제
                        // "화면 하단" 부분이 이 밴드 안에 나타난다(안 당기면 화면 맨 위 쪽이
                        // 잘못 나타남). DrawScope 안이라 Dp.toPx()를 바로 쓸 수 있다.
                        translate(top = -(screenHeight.toPx() - size.height)) {
                            drawLayer(cameraLayer)
                        }
                    },
            )
            Box(
                modifier = Modifier
                    .matchParentSize()
                    .background(dimmerColor),
            )
            Column(
                modifier = Modifier
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
                        )
                        Image(
                            painter = painterResource(id = R.drawable.shutter),
                            contentDescription = "촬영",
                            modifier = Modifier.size(52.dp),
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
