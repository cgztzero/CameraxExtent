package cn.com.zt.camera.listener

import android.graphics.Bitmap
import android.net.Uri
import java.io.File

interface ImageSavedCallback {
    fun onError(message: String?, code: Int)

    fun onImageSaved(originUri: Uri, bitmap: Bitmap? = null, watermarkFile: File? = null)
}