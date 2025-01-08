package cn.com.zt.camera.until

import android.graphics.Bitmap
import android.graphics.Canvas
import android.graphics.Matrix
import android.media.ExifInterface
import android.net.Uri
import java.io.IOException

object BitmapUtil {

    fun addWatermarkToBitmap(bitmap: Bitmap, watermark: Bitmap, x: Float, y: Float): Bitmap {
        val output = Bitmap.createBitmap(bitmap.width, bitmap.height, Bitmap.Config.ARGB_8888)
        val canvas = Canvas(output)
        canvas.drawBitmap(bitmap, Matrix(), null)
        canvas.drawBitmap(watermark, x, y, null)
        return output
    }

    fun getBitmapRotation(imageUri: Uri): Int {
        if (imageUri.path == null) {
            return 0
        }
        var rotation = 0
        try {
            val exif = ExifInterface(imageUri.path!!)
            when (exif.getAttributeInt(ExifInterface.TAG_ORIENTATION, ExifInterface.ORIENTATION_NORMAL)) {
                ExifInterface.ORIENTATION_ROTATE_270 -> rotation = 270
                ExifInterface.ORIENTATION_ROTATE_180 -> rotation = 180
                ExifInterface.ORIENTATION_ROTATE_90 -> rotation = 90
            }
        } catch (e: IOException) {
            e.printStackTrace()
        }
        return rotation
    }

    fun rotateBitmap(bitmap: Bitmap, rotationInDegrees: Int): Bitmap {
        var originBitmap = bitmap
        if (rotationInDegrees != 0) {
            val matrix = Matrix()
            matrix.preRotate(rotationInDegrees * 1F)
            originBitmap =
                Bitmap.createBitmap(originBitmap, 0, 0, originBitmap.width, originBitmap.height, matrix, true)
        }
        return originBitmap
    }
}