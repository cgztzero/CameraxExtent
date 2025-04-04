package cn.com.zt.camera.controller

import cn.com.zt.camera.constant.CameraFlashMode
import cn.com.zt.camera.listener.CodeAnalysisCallback
import cn.com.zt.camera.listener.ImageSavedCallback
import cn.com.zt.camera.listener.VideoSavedCallback
import cn.com.zt.camera.view.PreviewLayout
import cn.com.zt.camera.view.WatermarkOptions

class CameraControllerImpl : ICameraController {
    private var cameraView: PreviewLayout? = null

    override fun takePicture(callBack: ImageSavedCallback, tempOption: WatermarkOptions?) {
        checkCameraView()
        cameraView!!.takePicture(callBack, tempOption)
    }

    override fun bindCameraPreview(view: PreviewLayout) {
        if (cameraView != null) {
            throw Exception("ICameraController only bind an unique CameraView")
        }
        cameraView = view
    }

    override fun switchCamera(): Int {
        checkCameraView()
        return cameraView!!.switchCamera()
    }

    override fun startRecord(callBack: VideoSavedCallback,tempOption: WatermarkOptions?) {
        checkCameraView()
        cameraView!!.startRecord(callBack,tempOption)
    }

    override fun stopRecord() {
        checkCameraView()
        cameraView!!.stopRecord()
    }

    override fun isRecording(): Boolean {
        checkCameraView()
        return cameraView!!.isRecording()
    }

    override fun setFlashMode(@CameraFlashMode mode: Int) {
        checkCameraView()
        cameraView!!.setFlashMode(mode)
    }

    override fun startScan(callBack: CodeAnalysisCallback) {
        checkCameraView()
        cameraView!!.startScan(callBack)
    }

    override fun stopScan() {
        checkCameraView()
        cameraView!!.stopScan()
    }

    override fun startPreview() {
        checkCameraView()
        cameraView!!.startPreview()
    }

    override fun enableTorch(enable: Boolean) {
        checkCameraView()
        cameraView!!.enableTorch(enable)
    }

    private fun checkCameraView() {
        if (cameraView == null) {
            throw Exception("CameraView in ICameraController is null")
        }
    }
}