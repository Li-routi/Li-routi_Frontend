package com.li_routi.core.common.ui.camera

import android.content.Context
import android.net.Uri
import androidx.camera.core.Camera
import androidx.camera.core.CameraSelector
import androidx.camera.core.FocusMeteringAction
import androidx.camera.core.ImageCapture
import androidx.camera.core.ImageCaptureException
import androidx.camera.core.Preview
import androidx.camera.lifecycle.ProcessCameraProvider
import androidx.camera.view.PreviewView
import androidx.compose.animation.AnimatedVisibility
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
import kotlinx.coroutines.coroutineScope
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch

/**
 * CameraX Preview + ImageCapture 바인딩. 루틴/그룹 루틴/챌린지 인증 촬영 화면에서 공용으로 쓴다.
 *
 * [isActive]가 false이면 카메라 언바인딩(페이저 등에서 화면을 벗어났을 때 자원 해제).
 * [torchEnabled]는 재바인딩 없이 [CameraControl.enableTorch]로 상시 손전등을 켠다.
 * [flashMode]는 바인딩된 [ImageCapture]에 setter로만 반영해 토글 시 화면이 밀리지 않게 한다.
 * 셔터는 호출부에서 보관하는 [ImageCapture]로 [captureLiroutiCameraPhoto]를 호출한다.
 */
@Composable
fun LiroutiCameraPreview(
    lensFacing: Int,
    flashMode: Int,
    isActive: Boolean,
    onImageCaptureReady: (ImageCapture?) -> Unit,
    modifier: Modifier = Modifier,
    torchEnabled: Boolean = false,
) {
    val context = LocalContext.current
    val lifecycleOwner = LocalLifecycleOwner.current
    val previewView = remember { PreviewView(context) }
    var boundCamera by remember { mutableStateOf<Camera?>(null) }
    var boundImageCapture by remember { mutableStateOf<ImageCapture?>(null) }
    // 탭한 지점에 초점 링을 잠깐 보여주기 위한 상태. requestId는 같은 좌표를 연달아 탭해도
    // (offset이 같아 key가 안 바뀌어 애니메이션이 재시작 안 되는 문제를 막기 위해) 매번 값을 바꿔준다.
    var focusPoint by remember { mutableStateOf<Offset?>(null) }
    var focusRequestId by remember { mutableIntStateOf(0) }

    Box(modifier = modifier.fillMaxSize()) {
        AndroidView(
            factory = { previewView },
            modifier = Modifier
                .fillMaxSize()
                .pointerInput(boundCamera) {
                    coroutineScope {
                        // 탭-투-포커스: 탭 지점을 PreviewView 좌표계의 MeteringPoint로 변환해
                        // startFocusAndMetering으로 넘긴다. 3초 뒤 자동으로 연속 포커스로 되돌아간다.
                        launch {
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
                        launch {
                            detectTransformGestures { _, _, gestureZoom, _ ->
                                val camera = boundCamera ?: return@detectTransformGestures
                                val zoomState = camera.cameraInfo.zoomState.value
                                    ?: return@detectTransformGestures
                                val newRatio = (zoomState.zoomRatio * gestureZoom)
                                    .coerceIn(zoomState.minZoomRatio, zoomState.maxZoomRatio)
                                camera.cameraControl.setZoomRatio(newRatio)
                            }
                        }
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

    // lens/active만 재바인딩. flash·torch 토글은 아래에서 setter로 처리한다.
    LaunchedEffect(lensFacing, isActive, lifecycleOwner) {
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

        try {
            cameraProvider.unbindAll()
            val camera = cameraProvider.bindToLifecycle(
                lifecycleOwner,
                selector,
                preview,
                imageCapture,
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
        exit = fadeOut(),
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
