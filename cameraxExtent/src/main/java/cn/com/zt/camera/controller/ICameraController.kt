package cn.com.zt.camera.controller

import cn.com.zt.camera.listener.CodeAnalysisCallback
import cn.com.zt.camera.listener.ImageSavedCallback
import cn.com.zt.camera.listener.VideoSavedCallback
import cn.com.zt.camera.view.PreviewLayout
import cn.com.zt.camera.view.WatermarkOptions


interface ICameraController {

    fun takePicture(callBack: ImageSavedCallback, tempOption: WatermarkOptions? = null)

    fun bindCameraPreview(view: PreviewLayout)

    fun switchCamera(): Int

    fun startRecord(callBack: VideoSavedCallback,tempOption: WatermarkOptions? = null)

    fun stopRecord()

    fun isRecording(): Boolean

    fun setFlashMode(mode: Int)

    fun startScan(callBack: CodeAnalysisCallback)

    fun stopScan()

    fun startPreview()

    fun enableTorch(enable: Boolean)
}