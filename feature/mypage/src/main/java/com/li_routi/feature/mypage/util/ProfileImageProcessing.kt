package com.li_routi.feature.mypage.util

import android.content.Context
import android.graphics.Bitmap
import android.graphics.BitmapFactory
import android.graphics.ImageDecoder
import android.graphics.Matrix
import android.media.ExifInterface
import android.net.Uri
import android.os.Build
import android.provider.MediaStore
import com.li_routi.core.domain.auth.ProfileImageUpload
import java.io.ByteArrayOutputStream
import java.io.IOException
import kotlin.math.max
import kotlin.math.roundToInt

private const val PROFILE_IMAGE_MAX_DIMENSION = 1024
private const val PROFILE_IMAGE_JPEG_QUALITY = 85

/**
 * content:// Uri에서 프로필 이미지를 읽어 긴 변 [PROFILE_IMAGE_MAX_DIMENSION]px 이하로 리사이징하고
 * JPEG로 압축한 뒤 업로드용 바이트로 반환한다. IO/CPU 작업이라 호출부에서 IO 디스패처로 실행해야 한다.
 *
 * feature/login의 `LoginViewModel`에 있는 동일 로직(온보딩 첫 프로필 설정용)을 참고했다 — 다만 원본은
 * `ContentResolver.openInputStream()` + `BitmapFactory.decodeStream()`으로 디코딩했는데, 시스템 포토
 * 피커(Android Photo Picker)가 돌려주는 `content://media/picker/...` Uri에서 이 조합이 스트림을 못 열어
 * (`openInputStream`이 null) 항상 실패했다. [EditProfileScreen][com.li_routi.feature.mypage.screen.EditProfileScreen]의
 * 미리보기(Coil)는 같은 Uri를 문제없이 읽는 걸로 봐서 `openInputStream` 경로 자체의 문제로 보여, 미리보기와
 * 동일하게 [ImageDecoder]/[MediaStore.Images.Media.getBitmap] 기반 디코딩으로 바꿨다.
 */
internal fun readProfileImageUpload(context: Context, uri: Uri): ProfileImageUpload {
    val resolver = context.contentResolver

    var bitmap = decodeBitmap(context, uri, PROFILE_IMAGE_MAX_DIMENSION)
        ?: throw IOException("프로필 이미지를 디코딩할 수 없습니다: $uri")

    bitmap = bitmap.scaleDownTo(PROFILE_IMAGE_MAX_DIMENSION)

    // ImageDecoder(API 28+)는 EXIF 방향을 이미 반영해 디코딩하므로, 그 이하에서만 수동 회전한다.
    if (Build.VERSION.SDK_INT < Build.VERSION_CODES.P) {
        val orientation = runCatching {
            resolver.openInputStream(uri)?.use {
                ExifInterface(it).getAttributeInt(ExifInterface.TAG_ORIENTATION, ExifInterface.ORIENTATION_NORMAL)
            }
        }.getOrNull() ?: ExifInterface.ORIENTATION_NORMAL
        bitmap = bitmap.rotateForExifOrientation(orientation)
    }

    val bytes = ByteArrayOutputStream().use { output ->
        bitmap.compress(Bitmap.CompressFormat.JPEG, PROFILE_IMAGE_JPEG_QUALITY, output)
        output.toByteArray()
    }
    bitmap.recycle()

    return ProfileImageUpload(bytes = bytes, contentType = "image/jpeg")
}

/** [ImageDecoder]로 목표 크기까지 다운샘플링해 디코딩한다. API 28 미만은 [MediaStore.Images.Media.getBitmap]으로 대체한다. */
private fun decodeBitmap(context: Context, uri: Uri, maxDimension: Int): Bitmap? = runCatching {
    if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.P) {
        val source = ImageDecoder.createSource(context.contentResolver, uri)
        ImageDecoder.decodeBitmap(source) { decoder, info, _ ->
            val longerSide = max(info.size.width, info.size.height)
            if (longerSide > maxDimension) {
                val scale = maxDimension.toFloat() / longerSide
                decoder.setTargetSize(
                    (info.size.width * scale).roundToInt().coerceAtLeast(1),
                    (info.size.height * scale).roundToInt().coerceAtLeast(1),
                )
            }
        }
    } else {
        decodeDownsampledBitmap(context, uri, maxDimension)
    }
}.getOrNull()

/** API 28 미만에서 전체 해상도 디코딩으로 인한 메모리 초과를 피하기 위해 경계만 먼저 읽어 inSampleSize를 계산한 뒤 다운샘플링해 디코딩한다. */
private fun decodeDownsampledBitmap(context: Context, uri: Uri, maxDimension: Int): Bitmap? {
    val resolver = context.contentResolver

    val bounds = BitmapFactory.Options().apply { inJustDecodeBounds = true }
    val hasBounds = resolver.openInputStream(uri)?.use {
        BitmapFactory.decodeStream(it, null, bounds)
        true
    } ?: false

    @Suppress("DEPRECATION")
    if (!hasBounds) return MediaStore.Images.Media.getBitmap(resolver, uri)

    val longerSide = max(bounds.outWidth, bounds.outHeight)
    var inSampleSize = 1
    while (longerSide / (inSampleSize * 2) >= maxDimension) {
        inSampleSize *= 2
    }

    val options = BitmapFactory.Options().apply { this.inSampleSize = inSampleSize }
    return resolver.openInputStream(uri)?.use { BitmapFactory.decodeStream(it, null, options) }
}

private fun Bitmap.scaleDownTo(maxDimension: Int): Bitmap {
    val longerSide = max(width, height)
    if (longerSide <= maxDimension) return this
    val scale = maxDimension.toFloat() / longerSide
    val scaledWidth = (width * scale).roundToInt().coerceAtLeast(1)
    val scaledHeight = (height * scale).roundToInt().coerceAtLeast(1)
    val scaled = Bitmap.createScaledBitmap(this, scaledWidth, scaledHeight, true)
    if (scaled !== this) recycle()
    return scaled
}

private fun Bitmap.rotateForExifOrientation(orientation: Int): Bitmap {
    val matrix = when (orientation) {
        ExifInterface.ORIENTATION_ROTATE_90 -> Matrix().apply { postRotate(90f) }
        ExifInterface.ORIENTATION_ROTATE_180 -> Matrix().apply { postRotate(180f) }
        ExifInterface.ORIENTATION_ROTATE_270 -> Matrix().apply { postRotate(270f) }
        ExifInterface.ORIENTATION_FLIP_HORIZONTAL -> Matrix().apply { postScale(-1f, 1f) }
        ExifInterface.ORIENTATION_FLIP_VERTICAL -> Matrix().apply { postScale(1f, -1f) }
        ExifInterface.ORIENTATION_TRANSPOSE -> Matrix().apply { postScale(-1f, 1f); postRotate(270f) }
        ExifInterface.ORIENTATION_TRANSVERSE -> Matrix().apply { postScale(-1f, 1f); postRotate(90f) }
        else -> return this
    }
    val rotated = Bitmap.createBitmap(this, 0, 0, width, height, matrix, true)
    if (rotated !== this) recycle()
    return rotated
}
