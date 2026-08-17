package com.li_routi.core.common.ui.camera

import android.content.Context
import android.graphics.Bitmap
import android.graphics.Rect
import android.net.Uri
import android.view.View
import androidx.camera.core.Camera
import androidx.camera.core.CameraSelector
import androidx.camera.core.FocusMeteringAction
import androidx.camera.core.ImageCapture
import androidx.camera.core.ImageCaptureException
import androidx.camera.core.Preview
import androidx.camera.core.UseCaseGroup
import androidx.camera.core.ViewPort
import androidx.camera.lifecycle.ProcessCameraProvider
import androidx.camera.view.PreviewView
import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.core.tween
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.foundation.border
import androidx.compose.foundation.gestures.detectTapGestures
import androidx.compose.foundation.gestures.detectTransformGestures
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.offset
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.runtime.Composable
import androidx.compose.runtime.DisposableEffect
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.input.pointer.pointerInput
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.LocalDensity
import androidx.compose.ui.unit.IntOffset
import androidx.compose.ui.unit.dp
import androidx.compose.ui.viewinterop.AndroidView
import androidx.core.content.ContextCompat
import androidx.lifecycle.compose.LocalLifecycleOwner
import java.io.File
import java.util.concurrent.Executor
import java.util.concurrent.TimeUnit
import kotlin.coroutines.resume
import kotlin.coroutines.suspendCoroutine
import kotlin.math.roundToInt
import kotlinx.coroutines.awaitCancellation
import kotlinx.coroutines.delay
import kotlinx.coroutines.suspendCancellableCoroutine

/**
 * CameraX Preview + ImageCapture 바인딩. 루틴/그룹 루틴/챌린지 인증 촬영 화면에서 공용으로 쓴다.
 *
 * [isActive]가 false이면 카메라 언바인딩(페이저 등에서 화면을 벗어났을 때 자원 해제).
 * [torchEnabled]는 재바인딩 없이 [CameraControl.enableTorch]로 상시 손전등을 켠다.
 * [flashMode]는 바인딩된 [ImageCapture]에 setter로만 반영해 토글 시 화면이 밀리지 않게 한다.
 * 셔터는 호출부에서 보관하는 [ImageCapture]로 [captureLiroutiCameraPhoto]를 호출한다.
 *
 * [onPreviewSnapshot]을 넘기면 [PreviewSnapshotIntervalMs] 간격으로 [PreviewView.getBitmap]
 * 정적 스냅샷을 콜백으로 흘려준다. 라이브 프리뷰(TextureView 등 하드웨어 합성 콘텐츠)는
 * `Modifier.blur()`/`RenderEffect`를 중첩 레이어로 캡처해 블러를 씌워도 실제로 적용되지 않는
 * 경우가 있어(호출부의 배경 블러 오버레이 참고), 이럴 땐 라이브 텍스처 대신 일반 [Bitmap]으로 뜬
 * 스냅샷에 블러를 적용해야 한다.
 */
@Composable
fun LiroutiCameraPreview(
    lensFacing: Int,
    flashMode: Int,
    isActive: Boolean,
    onImageCaptureReady: (ImageCapture?) -> Unit,
    modifier: Modifier = Modifier,
    torchEnabled: Boolean = false,
    onPreviewSnapshot: ((Bitmap) -> Unit)? = null,
) {
    val context = LocalContext.current
    val lifecycleOwner = LocalLifecycleOwner.current
    val previewView = remember {
        PreviewView(context).apply {
            // 기본(PERFORMANCE)은 가능하면 SurfaceView를 쓰는데, SurfaceView는 별도 하드웨어
            // 레이어로 직접 합성돼 Compose의 Modifier.blur() 등 일반 View 캡처/렌더 이펙트가
            // 전혀 먹지 않는다. COMPATIBLE로 강제해 TextureView를 쓰면 일반 View처럼 캡처/블러가
            // 가능해진다(루틴 인증하기 카메라의 크롭 밴드 블러 참고). 단순 정지사진 촬영 화면이라
            // TextureView의 약간의 성능 비용은 무시할 만하다.
            implementationMode = PreviewView.ImplementationMode.COMPATIBLE
        }
    }
    var boundCamera by remember { mutableStateOf<Camera?>(null) }
    var boundImageCapture by remember { mutableStateOf<ImageCapture?>(null) }
    // 탭한 지점에 초점 링을 잠깐 보여주기 위한 상태. requestId는 같은 좌표를 연달아 탭해도
    // (offset이 같아 key가 안 바뀌어 애니메이션이 재시작 안 되는 문제를 막기 위해) 매번 값을 바꿔준다.
    var focusPoint by remember { mutableStateOf<Offset?>(null) }
    var focusRequestId by remember { mutableIntStateOf(0) }
    // PreviewView의 실제 배치(회전/리사이즈 등)가 바뀔 때마다 증가하는 토큰. 아래 바인딩
    // LaunchedEffect의 키에 넣어서 ViewPort가 바뀔 때마다 새 UseCaseGroup으로 재바인딩한다 —
    // 이게 없으면 최초 1회 바인딩 때의 ViewPort로 고정돼서, 이후 회전 등으로 프리뷰 실제 크기가
    // 바뀌어도 촬영 결과 크롭은 예전 프리뷰 크기 기준으로 남는다.
    var viewportEpoch by remember { mutableIntStateOf(0) }

    DisposableEffect(previewView) {
        var lastBounds: Rect? = null
        val listener = View.OnLayoutChangeListener { _, left, top, right, bottom, _, _, _, _ ->
            val bounds = Rect(left, top, right, bottom)
            val previous = lastBounds
            lastBounds = bounds
            // 최초 레이아웃 확정은 이미 아래 바인딩 이펙트가 awaitViewPort()로 기다리고 있으므로
            // 여기서 또 세면 시작하자마자 불필요한 재바인딩이 한 번 더 일어난다. 실제로 크기/위치가
            // "바뀐" 경우에만 센다.
            if (previous != null && previous != bounds) {
                viewportEpoch++
            }
        }
        previewView.addOnLayoutChangeListener(listener)
        onDispose { previewView.removeOnLayoutChangeListener(listener) }
    }

    Box(modifier = modifier.fillMaxSize()) {
        AndroidView(
            factory = { previewView },
            modifier = Modifier
                .fillMaxSize()
                // 탭-투-포커스: 탭 지점을 PreviewView 좌표계의 MeteringPoint로 변환해
                // startFocusAndMetering으로 넘긴다. 3초 뒤 자동으로 연속 포커스로 되돌아간다.
                // 핀치 확대/축소와 별개의 pointerInput으로 분리해 각자 독립된 코루틴에서 이벤트를
                // 받게 한다(같은 스코프에서 두 제스처를 같이 launch하면 한쪽의 이벤트 소비가
                // 다른 쪽 인식에 영향을 줄 수 있다).
                .pointerInput(boundCamera) {
                    detectTapGestures { tapOffset ->
                        val camera = boundCamera ?: return@detectTapGestures
                        val point = previewView.meteringPointFactory
                            .createPoint(tapOffset.x, tapOffset.y)
                        val action = FocusMeteringAction.Builder(point)
                            .setAutoCancelDuration(3, TimeUnit.SECONDS)
                            .build()
                        runCatching { camera.cameraControl.startFocusAndMetering(action) }
                        focusPoint = tapOffset
                        focusRequestId++
                    }
                }
                // 핀치 제스처로 확대/축소. 비율은 CameraX의 실시간 zoomState를 그대로 곱해서
                // 반영하므로 별도 상태 없이도 카메라 재바인딩(렌즈 전환 등) 시 자동으로 1배로
                // 초기화된다. 축소 한계(minZoomRatio)는 기기가 초광각 등 다중 카메라를 지원하면
                // 1 미만으로 내려가 줌아웃도 된다.
                .pointerInput(boundCamera) {
                    detectTransformGestures { _, _, gestureZoom, _ ->
                        val camera = boundCamera ?: return@detectTransformGestures
                        val zoomState = camera.cameraInfo.zoomState.value
                            ?: return@detectTransformGestures
                        val newRatio = (zoomState.zoomRatio * gestureZoom)
                            .coerceIn(zoomState.minZoomRatio, zoomState.maxZoomRatio)
                        camera.cameraControl.setZoomRatio(newRatio)
                    }
                },
        )

        focusPoint?.let { point ->
            FocusIndicator(
                offset = point,
                requestId = focusRequestId,
                onFinished = { focusPoint = null },
            )
        }
    }

    // lens/active/viewportEpoch가 바뀔 때 재바인딩. flash·torch 토글은 아래에서 setter로 처리한다.
    LaunchedEffect(lensFacing, isActive, lifecycleOwner, viewportEpoch) {
        if (!isActive) {
            boundCamera = null
            boundImageCapture = null
            onImageCaptureReady(null)
            runCatching { context.awaitCameraProvider().unbindAll() }
            return@LaunchedEffect
        }

        val cameraProvider = context.awaitCameraProvider()
        val preview = Preview.Builder().build().also {
            it.surfaceProvider = previewView.surfaceProvider
        }
        val imageCapture = ImageCapture.Builder()
            .setFlashMode(flashMode)
            .build()
        val selector = CameraSelector.Builder()
            .requireLensFacing(lensFacing)
            .build()

        // Preview/ImageCapture를 따로 바인딩하면 CameraX가 유스케이스별로 서로 다른
        // 해상도·화각을 고를 수 있다 — PreviewView는 자기 비율에 맞게 크롭해서 보여주지만
        // 실제 촬영 결과는 그 크롭이 적용되지 않아, 화면(프리뷰)에 안 보이던 좌우 영역까지
        // 더 넓게 찍히는 문제가 있었다. PreviewView의 실제 화면 크기를 기준으로 한 ViewPort로
        // 두 유스케이스를 묶어 바인딩하면 촬영 결과가 항상 프리뷰와 같은 화각으로 크롭된다.
        val viewPort = previewView.awaitViewPort()
        val useCaseGroup = UseCaseGroup.Builder()
            .setViewPort(viewPort)
            .addUseCase(preview)
            .addUseCase(imageCapture)
            .build()

        try {
            cameraProvider.unbindAll()
            val camera = cameraProvider.bindToLifecycle(
                lifecycleOwner,
                selector,
                useCaseGroup,
            )
            boundCamera = camera
            boundImageCapture = imageCapture
            onImageCaptureReady(imageCapture)
            try {
                awaitCancellation()
            } finally {
                boundCamera = null
                boundImageCapture = null
                onImageCaptureReady(null)
                cameraProvider.unbindAll()
            }
        } catch (_: Exception) {
            boundCamera = null
            boundImageCapture = null
            onImageCaptureReady(null)
        }
    }

    LaunchedEffect(flashMode, boundImageCapture) {
        boundImageCapture?.flashMode = flashMode
    }

    LaunchedEffect(torchEnabled, boundCamera) {
        val camera = boundCamera ?: return@LaunchedEffect
        if (!camera.cameraInfo.hasFlashUnit()) return@LaunchedEffect
        runCatching { camera.cameraControl.enableTorch(torchEnabled) }
    }

    if (onPreviewSnapshot != null) {
        LaunchedEffect(isActive, onPreviewSnapshot) {
            if (!isActive) return@LaunchedEffect
            while (true) {
                runCatching { previewView.bitmap }.getOrNull()
                    ?.downscaledForBlur()
                    ?.let(onPreviewSnapshot)
                delay(PreviewSnapshotIntervalMs)
            }
        }
    }
}

/** [LiroutiCameraPreview]의 [onPreviewSnapshot] 폴링 간격. 배경 블러용이라 실시간일 필요는 없다. */
private const val PreviewSnapshotIntervalMs = 200L

/** [downscaledForBlur]가 줄여내는 목표 (긴 변 기준) 픽셀 크기. */
private const val PreviewSnapshotMaxDimensionPx = 240

/**
 * [PreviewView.getBitmap]은 프리뷰 화면 전체 해상도(기기에 따라 수 MB)로 매번 새 [Bitmap]을
 * 만든다. 블러로 뭉개질 배경용 스냅샷이라 그 해상도가 전혀 필요 없으므로, 즉시 작은 크기로
 * 축소하고 원본은 곧바로 recycle해 폴링마다 큰 비트맵이 잠깐이라도 메모리에 남지 않게 한다.
 */
private fun Bitmap.downscaledForBlur(): Bitmap {
    val scale = PreviewSnapshotMaxDimensionPx.toFloat() / maxOf(width, height)
    if (scale >= 1f) return this
    val targetWidth = (width * scale).roundToInt().coerceAtLeast(1)
    val targetHeight = (height * scale).roundToInt().coerceAtLeast(1)
    val scaled = Bitmap.createScaledBitmap(this, targetWidth, targetHeight, true)
    if (scaled !== this) recycle()
    return scaled
}

private val FocusIndicatorSize = 64.dp
private const val FocusIndicatorVisibleMs = 600L
private const val FocusIndicatorFadeOutMs = 150L

/** 탭한 지점에 잠깐 나타났다 사라지는 정사각형 포커스 링. [requestId]가 바뀔 때마다 다시 나타난다. */
@Composable
private fun FocusIndicator(
    offset: Offset,
    requestId: Int,
    onFinished: () -> Unit,
    modifier: Modifier = Modifier,
) {
    var visible by remember(requestId) { mutableStateOf(true) }
    LaunchedEffect(requestId) {
        visible = true
        delay(FocusIndicatorVisibleMs)
        visible = false
        delay(FocusIndicatorFadeOutMs)
        onFinished()
    }

    val density = LocalDensity.current
    val halfSizePx = with(density) { (FocusIndicatorSize / 2).roundToPx() }

    AnimatedVisibility(
        visible = visible,
        enter = fadeIn(),
        exit = fadeOut(animationSpec = tween(durationMillis = FocusIndicatorFadeOutMs.toInt())),
        modifier = modifier.offset {
            IntOffset(offset.x.roundToInt() - halfSizePx, offset.y.roundToInt() - halfSizePx)
        },
    ) {
        Box(
            modifier = Modifier
                .size(FocusIndicatorSize)
                .border(1.5.dp, Color.White, RoundedCornerShape(4.dp)),
        )
    }
}

/** 캐시 디렉터리에 JPEG로 저장하고 [Uri]를 반환한다. [filePrefix]로 호출부별 임시 파일명을 구분한다. */
fun captureLiroutiCameraPhoto(
    context: Context,
    imageCapture: ImageCapture,
    executor: Executor,
    onSuccess: (Uri) -> Unit,
    onError: (ImageCaptureException) -> Unit,
    filePrefix: String = "lirouti_auth",
) {
    val photoFile = File(
        context.cacheDir,
        "${filePrefix}_${System.currentTimeMillis()}.jpg",
    )
    val outputOptions = ImageCapture.OutputFileOptions.Builder(photoFile).build()
    imageCapture.takePicture(
        outputOptions,
        executor,
        object : ImageCapture.OnImageSavedCallback {
            override fun onImageSaved(outputFileResults: ImageCapture.OutputFileResults) {
                val uri = outputFileResults.savedUri ?: Uri.fromFile(photoFile)
                onSuccess(uri)
            }

            override fun onError(exception: ImageCaptureException) {
                onError(exception)
            }
        },
    )
}

private suspend fun Context.awaitCameraProvider(): ProcessCameraProvider =
    suspendCoroutine { continuation ->
        val future = ProcessCameraProvider.getInstance(this)
        future.addListener(
            { continuation.resume(future.get()) },
            ContextCompat.getMainExecutor(this),
        )
    }

/**
 * [PreviewView.getViewPort]는 뷰가 실제로 측정/배치(너비·높이 확정)된 뒤에만 null이 아니다.
 * 이 함수가 보통 컴포지션 직후(레이아웃 전)에 호출되므로, 이미 값이 있으면 바로 쓰고 없으면
 * 레이아웃이 끝날 때까지 기다렸다가 돌려준다.
 */
private suspend fun PreviewView.awaitViewPort(): ViewPort {
    viewPort?.let { return it }
    return suspendCancellableCoroutine { continuation ->
        val listener = object : View.OnLayoutChangeListener {
            override fun onLayoutChange(
                v: View?,
                left: Int,
                top: Int,
                right: Int,
                bottom: Int,
                oldLeft: Int,
                oldTop: Int,
                oldRight: Int,
                oldBottom: Int,
            ) {
                val currentViewPort = viewPort ?: return
                removeOnLayoutChangeListener(this)
                continuation.resume(currentViewPort)
            }
        }
        addOnLayoutChangeListener(listener)
        continuation.invokeOnCancellation { removeOnLayoutChangeListener(listener) }
    }
}
