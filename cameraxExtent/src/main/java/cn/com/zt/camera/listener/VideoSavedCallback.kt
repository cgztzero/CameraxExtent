package cn.com.zt.camera.listener

import android.net.Uri
import java.io.File

interface VideoSavedCallback {

    fun onVideoSaved(uri: Uri, watermarkFile: File? = null)

    fun onError(videoCaptureError: Int, message: String, cause: Throwable?)

    fun onWatermarkVideoProgressing(progress: Int, progressTime: Long)
}