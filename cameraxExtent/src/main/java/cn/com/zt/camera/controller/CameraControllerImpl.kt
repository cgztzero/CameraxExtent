package cn.com.zt.camera.controller

import cn.com.zt.camera.constant.CameraFlashMode
import cn.com.zt.camera.listener.CodeAnalysisCallback
import cn.com.zt.camera.listener.ImageSavedCallback
import cn.com.zt.camera.listener.VideoSavedCallback
import cn.com.zt.camera.view.PreviewLayout

class CameraControllerImpl : ICameraController {
    private var cameraView: PreviewLayout? = null

    override fun takePicture(callBack: ImageSavedCallback) {
        checkCameraView()
        cameraView!!.takePicture(callBack)
    }

    override fun bindCameraPreview(view: PreviewLayout) {
        if (cameraView != null) {
            throw Exception("ICameraController only bind a unique CameraView")
        }
        cameraView = view
    }

    override fun switchCamera(): Int {
        checkCameraView()
        return cameraView!!.switchCamera()
    }

    override fun startRecord(callBack: VideoSavedCallback) {
        checkCameraView()
        cameraView!!.startRecord(callBack)
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

    private fun checkCameraView() {
        if (cameraView == null) {
            throw Exception("CameraView in ICameraController is null")
        }
    }
}