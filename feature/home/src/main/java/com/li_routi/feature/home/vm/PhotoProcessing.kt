package com.li_routi.feature.home.vm

import android.content.Context
import android.graphics.Bitmap
import android.graphics.BitmapFactory
import android.graphics.Matrix
import android.media.ExifInterface
import android.net.Uri
import java.io.ByteArrayOutputStream

/** Figma `촬영 후 메모/선택`(node 3610:26875) 사진 박스 비율(328:184 ≈ 16:9). 미리보기/업로드가 항상 이 비율로 크롭한다. */
internal const val VerificationPhotoAspectRatio = 328f / 184f

/** 업로드용으로 디코딩할 비트맵의 (긴 변 기준) 최대 픽셀 크기. */
internal const val VerificationPhotoUploadTargetSizePx = 1920

/**
 * EXIF Orientation을 반영해 세로/가로가 올바른 방향으로 보이게, [maxLongEdgePx] 이하로 샘플링해 디코딩한다.
 *
 * [uri]가 `file://`(카메라 캡처는 항상 이 스킴)면 `ContentResolver` 스트림을 거치지 않고 파일 경로로
 * 직접 디코딩한다 — 일부 기기/에뮬레이터에서 `file://`를 `ContentResolver.openInputStream()`으로 열어
 * `BitmapFactory.decodeStream()`에 넘기면 디코딩이 끝없이 멈춰버리는 경우가 있어(예외 없이 그냥
 * 반환되지 않음), 더 안정적인 경로로 우회한다.
 */
internal fun decodeBitmapWithExif(context: Context, uri: Uri, maxLongEdgePx: Int): Bitmap? {
    val filePath = uri.takeIf { it.scheme == "file" }?.path

    val orientation = runCatching {
        if (filePath != null) {
            ExifInterface(filePath).getAttributeInt(ExifInterface.TAG_ORIENTATION, ExifInterface.ORIENTATION_NORMAL)
        } else {
            context.contentResolver.openInputStream(uri)?.use { stream ->
                ExifInterface(stream).getAttributeInt(ExifInterface.TAG_ORIENTATION, ExifInterface.ORIENTATION_NORMAL)
            }
        }
    }.getOrNull() ?: ExifInterface.ORIENTATION_NORMAL

    val bounds = BitmapFactory.Options().apply { inJustDecodeBounds = true }
    val bitmap = if (filePath != null) {
        BitmapFactory.decodeFile(filePath, bounds)
        val options = BitmapFactory.Options().apply {
            inSampleSize = calculateInSampleSize(bounds.outWidth, bounds.outHeight, maxLongEdgePx)
        }
        BitmapFactory.decodeFile(filePath, options)
    } else {
        // inJustDecodeBounds = true인 동안은 decodeStream이 항상 null을 반환하는 게 정상 동작이라
        // (디코딩 자체를 안 하고 크기만 읽음), 그 반환값으로 스트림 열기 성공 여부를 판단하면 안 된다.
        val resolver = context.contentResolver
        val boundsRead = resolver.openInputStream(uri)?.use { stream ->
            BitmapFactory.decodeStream(stream, null, bounds)
            true
        } ?: false
        if (!boundsRead) return null
        val options = BitmapFactory.Options().apply {
            inSampleSize = calculateInSampleSize(bounds.outWidth, bounds.outHeight, maxLongEdgePx)
        }
        resolver.openInputStream(uri)?.use { stream -> BitmapFactory.decodeStream(stream, null, options) }
    } ?: return null

    return bitmap.applyExifOrientation(orientation)
}

/** [width]/[height] 중 긴 변이 [reqSize] 이하가 되는 가장 작은 2의 거듭제곱 다운샘플링 비율을 계산한다. */
private fun calculateInSampleSize(width: Int, height: Int, reqSize: Int): Int {
    var inSampleSize = 1
    if (width <= 0 || height <= 0) return inSampleSize
    while (maxOf(width, height) / inSampleSize > reqSize) {
        inSampleSize *= 2
    }
    return inSampleSize
}

private fun Bitmap.applyExifOrientation(orientation: Int): Bitmap {
    val matrix = Matrix()
    when (orientation) {
        ExifInterface.ORIENTATION_ROTATE_90 -> matrix.postRotate(90f)
        ExifInterface.ORIENTATION_ROTATE_180 -> matrix.postRotate(180f)
        ExifInterface.ORIENTATION_ROTATE_270 -> matrix.postRotate(270f)
        ExifInterface.ORIENTATION_FLIP_HORIZONTAL -> matrix.postScale(-1f, 1f)
        ExifInterface.ORIENTATION_FLIP_VERTICAL -> matrix.postScale(1f, -1f)
        ExifInterface.ORIENTATION_TRANSPOSE -> {
            matrix.postRotate(90f)
            matrix.postScale(-1f, 1f)
        }
        ExifInterface.ORIENTATION_TRANSVERSE -> {
            matrix.postRotate(270f)
            matrix.postScale(-1f, 1f)
        }
        else -> return this
    }
    val rotated = Bitmap.createBitmap(this, 0, 0, width, height, matrix, true)
    if (rotated !== this) recycle()
    return rotated
}

/**
 * 세로 사진(height > width)을 90도 회전해 가로로 만든다. [centerCropToRatio]는 항상 가로
 * 비율로 크롭하므로, 이 회전 없이 세로 사진이 들어오면 위아래 대부분이 잘려나간다.
 */
internal fun Bitmap.rotateToLandscapeIfPortrait(): Bitmap {
    if (height <= width) return this
    val matrix = Matrix().apply { postRotate(-90f) }
    val rotated = Bitmap.createBitmap(this, 0, 0, width, height, matrix, true)
    if (rotated !== this) recycle()
    return rotated
}

/**
 * 사진을 [targetRatio](가로/세로)에 맞춰 잘라낸다. [startBias]는 잘려나가는 전체 양 중 시작 쪽
 * (가로 크롭이면 왼쪽, 세로 크롭이면 위쪽)에서 가져갈 비율로, 0.5면 정중앙 대칭 크롭이다.
 * 미리보기·업로드가 같은 크롭 결과를 쓰도록 공유한다.
 *
 * 주의: 크롭이 필요해 새 비트맵을 만드는 경우 수신 객체(this)를 [Bitmap.recycle]한다 — 소유권이 반환값으로
 * 넘어가므로 호출 후 원본 비트맵을 다시 쓰면 안 된다. 크롭이 필요 없을 때(비율이 이미 같을 때)는 원본을
 * 그대로 반환하고 recycle하지 않는다.
 */
internal fun Bitmap.centerCropToRatio(targetRatio: Float, startBias: Float = 0.5f): Bitmap {
    val bias = startBias.coerceIn(0f, 1f)
    val currentRatio = width.toFloat() / height.toFloat()
    val cropped = when {
        currentRatio > targetRatio -> {
            val newWidth = (height * targetRatio).toInt().coerceIn(1, width)
            val x = ((width - newWidth) * bias).toInt()
            Bitmap.createBitmap(this, x, 0, newWidth, height)
        }
        currentRatio < targetRatio -> {
            val newHeight = (width / targetRatio).toInt().coerceIn(1, height)
            val y = ((height - newHeight) * bias).toInt()
            Bitmap.createBitmap(this, 0, y, width, newHeight)
        }
        else -> this
    }
    if (cropped !== this) recycle()
    return cropped
}

/**
 * 인증 사진 크롭 시 상/하단 중 어느 쪽을 더 잘라낼지 정하는 비율(위쪽에서 가져가는 비율). Figma
 * node `5562:14552`(Subtract)의 원래 상/하단 크롭 비율(88:180)에서 그대로 가져왔다 —
 * 가운데 피사체가 살짝 위쪽에 오도록 아래를 더 많이 잘라낸다.
 *
 * 촬영 화면의 크롭 안내([com.li_routi.feature.home.screen.RoutineAuthCameraScreen])와 실제
 * 크롭(이 값 자체)이 같은 소스를 쓰므로 항상 일치한다. [rotateToLandscapeIfPortrait]로 -90도
 * 회전한 뒤에는 원본 상단이 회전된 이미지의 왼쪽, 원본 하단이 오른쪽이 되므로
 * ([centerCropToRatio]가 회전 후 이미지 기준 currentRatio > targetRatio라 가로(왼쪽/오른쪽)를
 * 잘라내는 경우), 이 비율을 [centerCropToRatio]의 [startBias]로 그대로 쓰면 "원본 기준 위쪽에서
 * 이 비율만큼" 잘라내는 것과 같아진다.
 */
internal const val VerificationPhotoTopCropBias: Float = 88f / (88f + 180f)

/**
 * [RoutineAuthUploadScreen]의 [CapturedPhotoPreview]에서 `ContentScale.Crop` 정렬에 쓰는 값.
 * `BiasAlignment`는 bias=-1일 때 시작 쪽이 0% 잘리고(전부 반대쪽에서 잘림), bias=+1일 때 100%
 * 잘리는 선형 관계라 `bias = 2 * startBias - 1`로 변환해야 [VerificationPhotoTopCropBias]와
 * 같은 크롭 결과가 된다.
 */
internal val VerificationPhotoCropAlignmentBias: Float = 2f * VerificationPhotoTopCropBias - 1f

/** 크롭된 비트맵을 JPEG 바이트로 재인코딩한다. 업로드 직전에 호출한다. */
internal fun Bitmap.toJpegBytes(quality: Int = 90): ByteArray =
    ByteArrayOutputStream().use { stream ->
        compress(Bitmap.CompressFormat.JPEG, quality, stream)
        stream.toByteArray()
    }
