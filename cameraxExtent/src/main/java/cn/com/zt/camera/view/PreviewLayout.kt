package cn.com.zt.camera.view

import android.annotation.SuppressLint
import android.content.Context
import android.graphics.Bitmap
import android.graphics.BitmapFactory
import android.graphics.Canvas
import android.graphics.Color
import android.net.Uri
import android.os.Environment
import android.os.Handler
import android.os.Looper
import android.util.AttributeSet
import android.util.Log
import android.view.Gravity
import android.view.View
import android.widget.FrameLayout
import android.widget.ImageView
import androidx.camera.core.CameraSelector
import androidx.camera.core.ImageCapture
import androidx.camera.core.ImageCaptureException
import androidx.camera.view.CameraController
import androidx.camera.view.LifecycleCameraController
import androidx.camera.view.PreviewView
import androidx.camera.view.video.OnVideoSavedCallback
import androidx.camera.view.video.OutputFileOptions
import androidx.camera.view.video.OutputFileResults
import androidx.lifecycle.LifecycleOwner
import cn.com.zt.camera.constant.CameraConstant
import cn.com.zt.camera.constant.CameraFlashMode
import cn.com.zt.camera.listener.ImageSavedCallback
import cn.com.zt.camera.listener.VideoSavedCallback
import cn.com.zt.camera.until.BitmapUtil
import cn.com.zt.camera.until.RecordingTimer
import cn.com.zt.camera.until.TimerCallBack
import io.microshow.rxffmpeg.RxFFmpegInvoke
import io.microshow.rxffmpeg.RxFFmpegSubscriber
import java.io.File
import java.io.FileOutputStream
import java.io.IOException
import java.util.concurrent.ExecutorService
import java.util.concurrent.Executors


class PreviewLayout(context: Context, attrs: AttributeSet?) : FrameLayout(context, attrs) {

    private var mPreviewView: PreviewView
    private lateinit var lifecycleCameraController: LifecycleCameraController
    private val cameraExecutor: ExecutorService = Executors.newSingleThreadExecutor()
    private var lensFacing = CameraConstant.LENS_FACING_BACK
    private var fileOutputOptions: FileOutputOptions? = null
    private val defaultImageOutputDirectory =
        context.applicationContext.getExternalFilesDir(Environment.DIRECTORY_PICTURES)?.absolutePath + "/image/"
    private val defaultVideoOutputDirectory =
        context.applicationContext.getExternalFilesDir(Environment.DIRECTORY_PICTURES)?.absolutePath + "/video/"
    private val handler = Handler(Looper.getMainLooper())
    private var watermarkOption: WatermarkOptions? = null
    private var watermark: View? = null
    private var recordingTimer: RecordingTimer? = null

    init {
        mPreviewView = PreviewView(context)
        mPreviewView.layoutParams =
            LayoutParams(LayoutParams.MATCH_PARENT, LayoutParams.MATCH_PARENT)
        addView(mPreviewView)
        initController()
    }

    fun setFileOutputOptions(options: FileOutputOptions) {
        fileOutputOptions = options
    }

    fun setWatermarkOptions(options: WatermarkOptions) {
        watermarkOption = options
        checkWatermark()
    }

    private fun checkWatermark() {
        if (watermarkOption == null) {
            return
        }
        val options = watermarkOption!!
        if (!options.previewShowWaterMark()) {
            return
        }
        post {
            watermark = if (options.getWatermarkView() != null) {
                options.getWatermarkView()!!
            } else {
                val tempView = ImageView(context)
                tempView.setImageBitmap(options.getWatermarkBitmap()!!)
                tempView
            }

            addView(watermark, createWatermarkLayoutParams(options))
        }

    }

    private fun isImageNeedWatermark(): Boolean {
        if (watermarkOption == null || watermarkOption!!.isOnlyVideoAddWatermark()) {
            return false
        }
        return true
    }

    private fun isVideoNeedWatermark(): Boolean {
        if (watermarkOption == null || watermarkOption!!.isOnlyImageAddWatermark()) {
            return false
        }
        return true
    }

    private fun createWatermarkBitmap(): Bitmap {
        val options = watermarkOption!!
        val watermarkBitmap: Bitmap
        if (options.getWatermarkBitmap() != null) {
            watermarkBitmap = options.getWatermarkBitmap()!!
        } else {
            val view = options.getWatermarkView()!!
            watermarkBitmap = Bitmap.createBitmap(
                view.width,
                view.height,
                Bitmap.Config.ARGB_8888
            )
            val canvas = Canvas(watermarkBitmap)
            canvas.drawColor(Color.TRANSPARENT)
            view.draw(canvas)
        }
        return watermarkBitmap
    }

    private fun addWatermarkOnImage(originUri: Uri, callBack: ImageSavedCallback) {
        if (watermarkOption == null) {
            callBack.onError("image uri is null", -1)
            return
        }
        val options = watermarkOption!!
        val position = options.getWatermarkPosition()!!
        val watermarkBitmap = createWatermarkBitmap()

        val rotationInDegrees: Int = BitmapUtil.getBitmapRotation(originUri)
        var originBitmap = BitmapFactory.decodeFile(originUri.path)
        originBitmap = BitmapUtil.rotateBitmap(originBitmap, rotationInDegrees)

        val imageWidth = originBitmap.width
        val imageHeight = originBitmap.height
        val watermarkWidth = watermarkBitmap.width
        val watermarkHeight = watermarkBitmap.height
        var x = 0f
        var y = 0f

        if (position.isCenter()) {
            x = ((imageWidth - watermarkWidth) / 2).toFloat()
            y = ((imageHeight - watermarkHeight) / 2).toFloat()
        } else if (position.isTopLeft()) {
            x = position.x
            y = position.y
        } else if (position.isTopRight()) {
            x = imageWidth - position.x - watermarkWidth
            y = position.y
        } else if (position.isBottomLeft()) {
            x = position.x
            y = imageHeight - position.y - watermarkHeight
        } else if (position.isBottomRight()) {
            x = imageWidth - position.x - watermarkWidth
            y = imageHeight - position.y - watermarkHeight
        }

        val watermarkResultBitmap = BitmapUtil.addWatermarkToBitmap(
            originBitmap, watermarkBitmap,
            x,
            y,
        )

        if (!options.saveWatermarkTofile()) {
            callBack.onImageSaved(originUri, watermarkResultBitmap)
            return
        }

        val directory: String = if (options.getWatermarkFilePath() != null) {
            options.getWatermarkFilePath()!!
        } else if (fileOutputOptions?.getImageOutputDirectory() != null) {
            fileOutputOptions?.getImageOutputDirectory()!!
        } else {
            defaultImageOutputDirectory
        }

        val file = File(directory)
        if (!file.exists()) {
            file.mkdirs()
        }
        val watermarkName = if (options.createWatermarkFileName != null) {
            options.createWatermarkFileName!!.invoke()
        } else {
            System.currentTimeMillis().toString() + "_watermark.jpg"
        }
        val imageFile = File(directory, watermarkName)
        imageFile.createNewFile()
        var fos: FileOutputStream? = null
        try {
            fos = FileOutputStream(imageFile)
            watermarkResultBitmap.compress(Bitmap.CompressFormat.JPEG, 100, fos)
            callBack.onImageSaved(originUri, watermarkResultBitmap, imageFile)
        } catch (e: IOException) {
            callBack.onError("add water IO Exception:${e.message}", -1)
            e.printStackTrace()
        } catch (e: Exception) {
            callBack.onError("add water exception:${e.message}", -1)
            e.printStackTrace()
        } finally {
            try {
                fos?.close()
            } catch (e: IOException) {
                e.printStackTrace()
            }
        }
    }

    private fun createWatermarkFile(watermarkBitmap: Bitmap, callBack: VideoSavedCallback): File? {
        val watermarkParentFile = File(defaultImageOutputDirectory)
        if (!watermarkParentFile.exists()) {
            watermarkParentFile.mkdirs()
        }

        val videoWatermarkPath = watermarkParentFile.path + "watermark_video" + ".png"
        val watermarkImageFile = File(videoWatermarkPath)
        if (watermarkImageFile.exists()) {
            watermarkImageFile.delete()
        }
        watermarkImageFile.createNewFile()
        var fos: FileOutputStream? = null
        try {
            fos = FileOutputStream(watermarkImageFile)
            watermarkBitmap.compress(Bitmap.CompressFormat.PNG, 90, fos)
        } catch (e: IOException) {
            callBack.onError(-1, "create watermark file IO fail:${e.message}", e)
            return null
        } catch (e: java.lang.Exception) {
            callBack.onError(-1, "create watermark fail:${e.message}", e)
            return null
        } finally {
            fos?.close()
        }
        return watermarkImageFile
    }

    private fun addWatermarkOnVideo(originUri: Uri, callBack: VideoSavedCallback) {
        if (watermarkOption == null || watermarkOption?.getWatermarkPosition() == null) {
            callBack.onError(-1, "video watermarkOption or WatermarkPosition is null", null)
            return
        }

        val options = watermarkOption!!
        val position = options.getWatermarkPosition()!!
        val watermarkBitmap = createWatermarkBitmap()

        val videoWatermarkFile = createWatermarkFile(watermarkBitmap, callBack) ?: return

        val sb = StringBuffer()
        sb.append("ffmpeg -y -i ${originUri.path} -i ${videoWatermarkFile.path}")
        sb.append(" -filter_complex ")
        if (position.isCenter()) {
            sb.append("overlay=(main_w-overlay_w)/2:(main_h-overlay_h)/2")
        } else if (position.isTopLeft()) {
            sb.append("overlay=${position.x}:${position.y}")
        } else if (position.isTopRight()) {
            sb.append("overlay=main_w-overlay_w-${position.x}:${position.y}")
        } else if (position.isBottomLeft()) {
            sb.append("overlay=${position.x}:main_h-overlay_h-${position.y}")
        } else if (position.isBottomRight()) {
            sb.append("overlay=main_w-overlay_w-${position.x}:main_h-overlay_h-${position.y}")
        }
        sb.append(" -preset superfast ")

        val directory: String = if (options.getWatermarkFilePath() != null) {
            options.getWatermarkFilePath()!!
        } else if (fileOutputOptions?.getVideoOutputDirectory() != null) {
            fileOutputOptions?.getVideoOutputDirectory()!!
        } else {
            defaultVideoOutputDirectory
        }

        val file = File(directory)
        if (!file.exists()) {
            file.mkdirs()
        }
        val watermarkResultName = if (options.createWatermarkVideoFileName != null) {
            options.createWatermarkVideoFileName!!.invoke()
        } else {
            System.currentTimeMillis().toString() + "_watermark.mp4"
        }
        val resultVideoFile = File(directory, watermarkResultName)
        if (resultVideoFile.exists()) {
            resultVideoFile.delete()
        }
        resultVideoFile.createNewFile()
        sb.append(resultVideoFile.path)
        executeCommand(sb.toString(), originUri, resultVideoFile, callBack)
    }

    private fun executeCommand(command: String, originUri: Uri, resultFile: File, callBack: VideoSavedCallback) {
        val commands: Array<String> = command.split(" ".toRegex()).toTypedArray()
        RxFFmpegInvoke.getInstance()
            .runCommandRxJava(commands)
            .subscribe(
                object : RxFFmpegSubscriber() {
                    override fun onError(message: String?) {
                        callBack.onError(-1, "error occur when add watermark message:$message", null)
                    }

                    override fun onFinish() {
                        callBack.onVideoSaved(originUri, resultFile)
                    }

                    override fun onProgress(progress: Int, progressTime: Long) {
                        callBack.onWatermarkVideoProgressing(progress, progressTime)
                    }

                    override fun onCancel() {
                        callBack.onError(-1, "cancel adding watermark", null)
                    }
                },
            )
    }


    @SuppressLint("RtlHardcoded")
    private fun createWatermarkLayoutParams(options: WatermarkOptions): LayoutParams {
        val layoutParams =
            LayoutParams(LayoutParams.WRAP_CONTENT, LayoutParams.WRAP_CONTENT)
        if (options.getWatermarkPosition() == null) {
            return layoutParams
        }

        val position = options.getWatermarkPosition()!!
        if (position.isCenter()) {
            layoutParams.gravity = Gravity.CENTER
        } else if (position.isTopLeft()) {
            layoutParams.gravity = Gravity.TOP or Gravity.LEFT
            layoutParams.leftMargin = position.x.toInt()
            layoutParams.topMargin = position.y.toInt()
        } else if (position.isTopRight()) {
            layoutParams.gravity = Gravity.TOP or Gravity.RIGHT
            layoutParams.rightMargin = position.x.toInt()
            layoutParams.topMargin = position.y.toInt()
        } else if (position.isBottomLeft()) {
            layoutParams.gravity = Gravity.BOTTOM or Gravity.LEFT
            layoutParams.leftMargin = position.x.toInt()
            layoutParams.bottomMargin = position.y.toInt()
        } else if (position.isBottomRight()) {
            layoutParams.gravity = Gravity.BOTTOM or Gravity.RIGHT
            layoutParams.rightMargin = position.x.toInt()
            layoutParams.bottomMargin = position.y.toInt()
        }

        return layoutParams
    }

    fun startPreview() {
        mPreviewView.controller = lifecycleCameraController
    }

    private fun initController() {
        lifecycleCameraController = LifecycleCameraController(context)
        lifecycleCameraController.imageCaptureFlashMode = ImageCapture.FLASH_MODE_AUTO
    }

    fun bindToLifecycle(lifecycleOwner: LifecycleOwner) {
        lifecycleCameraController.bindToLifecycle(lifecycleOwner)
    }

    internal fun takePicture(callBack: ImageSavedCallback) {
        val photoFile = createPhotoFile()
        val metadata = ImageCapture.Metadata().apply {
            //水平翻转
            isReversedHorizontal = lensFacing == CameraConstant.LENS_FACING_FRONT
        }
        val outputOptions = ImageCapture.OutputFileOptions.Builder(photoFile)
            .setMetadata(metadata)
            .build()
        lifecycleCameraController.setEnabledUseCases(CameraController.IMAGE_CAPTURE or CameraController.IMAGE_ANALYSIS)

        lifecycleCameraController.takePicture(
            outputOptions,
            cameraExecutor,
            object : ImageCapture.OnImageSavedCallback {
                override fun onError(exc: ImageCaptureException) {
                    runOnUiThread {
                        callBack.onError(exc.message, exc.imageCaptureError)
                    }
                }

                override fun onImageSaved(output: ImageCapture.OutputFileResults) {
                    val savedUri = output.savedUri ?: Uri.fromFile(photoFile)
                    if (isImageNeedWatermark()) {
                        addWatermarkOnImage(savedUri, callBack)
                    } else {
                        runOnUiThread {
                            callBack.onImageSaved(savedUri)
                        }
                    }
                }
            })
    }

    @SuppressLint("WrongConstant")
    internal fun switchCamera(): Int {
        lensFacing = if (lensFacing == CameraConstant.LENS_FACING_BACK) {
            CameraConstant.LENS_FACING_FRONT
        } else {
            CameraConstant.LENS_FACING_BACK
        }
        lifecycleCameraController.cameraSelector =
            CameraSelector.Builder().requireLensFacing(lensFacing).build()
        return lensFacing
    }

    private fun createPhotoFile(): File {
        val directory: String = if (fileOutputOptions?.getImageOutputDirectory() != null) {
            fileOutputOptions?.getImageOutputDirectory()!!
        } else {
            defaultImageOutputDirectory
        }

        val file = File(directory)
        if (!file.exists()) {
            file.mkdirs()
        }

        val fileName: String = if (fileOutputOptions?.getCreateImageName() != null) {
            fileOutputOptions?.getCreateImageName()!!.invoke()
        } else {
            System.currentTimeMillis().toString() + ".png"
        }
        return File(directory, fileName)
    }

    private fun createVideoFile(): File {
        val directory: String = if (fileOutputOptions?.getVideoOutputDirectory() != null) {
            fileOutputOptions?.getVideoOutputDirectory()!!
        } else {
            defaultVideoOutputDirectory
        }

        val file = File(directory)
        if (!file.exists()) {
            file.mkdirs()
        }

        val fileName: String = if (fileOutputOptions?.getCreateVideoName() != null) {
            fileOutputOptions?.getCreateVideoName()!!.invoke()
        } else {
            System.currentTimeMillis().toString() + ".mp4"
        }
        return File(directory, fileName)
    }

    @SuppressLint("UnsafeOptInUsageError")
    internal fun startRecord(callBack: VideoSavedCallback) {
        if (isRecording()) {
            return
        }

        val videoFile = createVideoFile()
        val outputOptions = OutputFileOptions.builder(videoFile).build()
        lifecycleCameraController.setEnabledUseCases(CameraController.VIDEO_CAPTURE)
        lifecycleCameraController.startRecording(
            outputOptions,
            cameraExecutor,
            object : OnVideoSavedCallback {
                override fun onVideoSaved(output: OutputFileResults) {
                    val savedUri = output.savedUri ?: Uri.fromFile(videoFile)
                    Log.e("ztzt", "onVideoSaved：${savedUri.path}")
                    if (isVideoNeedWatermark()) {
                        addWatermarkOnVideo(savedUri, callBack)
                    } else {
                        runOnUiThread {
                            callBack.onVideoSaved(savedUri)
                        }
                    }
                }

                override fun onError(videoCaptureError: Int, message: String, cause: Throwable?) {
                    Log.e("ztzt", "onError：${message}")
                    runOnUiThread {
                        callBack.onError(videoCaptureError, message, cause)
                    }
                }
            })
        if (isRecording()) {
            startTiming()
        }
    }

    @SuppressLint("UnsafeOptInUsageError")
    internal fun stopRecord() {
        if (!isRecording()) {
            return
        }
        lifecycleCameraController.stopRecording()
        stopTiming()
    }

    private fun stopTiming() {
        recordingTimer?.stopTimer()
    }

    private fun startTiming() {
        if (recordingTimer == null) {
            initTimer()
        }
        recordingTimer?.startTimer()
    }

    private fun initTimer() {
        recordingTimer = RecordingTimer(
            totalTimeInMillis = fileOutputOptions?.getVideoMaxDuration() ?: (3 * 1000),
            countDownInterval = 1000,
            object : TimerCallBack {
                override fun onTick(millisUntilFinished: Long) {

                }

                override fun onFinish() {
                    Log.e("zt", "计时器到时")
                    stopRecord()
                }
            },
        )
    }

    @SuppressLint("UnsafeOptInUsageError")
    internal fun isRecording(): Boolean {
        return lifecycleCameraController.isRecording
    }

    private fun runOnUiThread(action: Runnable) {
        handler.post(action)
    }

    internal fun setFlashMode(@CameraFlashMode mode: Int) {
        if (lifecycleCameraController.imageCaptureFlashMode != mode) {
            lifecycleCameraController.imageCaptureFlashMode = mode
        }
    }
}