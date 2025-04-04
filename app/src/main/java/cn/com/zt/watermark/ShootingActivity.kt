package cn.com.zt.watermark

import android.graphics.Bitmap
import android.net.Uri
import android.os.Bundle
import android.os.Environment
import android.view.LayoutInflater
import android.widget.TextView
import androidx.fragment.app.FragmentActivity
import cn.com.zt.camera.constant.CameraConstant
import cn.com.zt.camera.controller.CameraControllerImpl
import cn.com.zt.camera.listener.ImageSavedCallback
import cn.com.zt.camera.listener.VideoSavedCallback
import cn.com.zt.camera.view.FileOutputOptions
import cn.com.zt.camera.view.WatermarkOptions
import cn.com.zt.camera.view.WatermarkPosition
import cn.com.zt.watermark.databinding.ActivityShootingBinding
import java.io.File

class ShootingActivity : FragmentActivity() {
    lateinit var binding: ActivityShootingBinding
    private val controller = CameraControllerImpl()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityShootingBinding.inflate(LayoutInflater.from(this))
        setContentView(binding.root)
        binding.previewLayout.bindToLifecycle(this)
        controller.bindCameraPreview(binding.previewLayout)

        controller.startPreview()
        val option = FileOutputOptions.Companion.FileOutputOptionsBuilder()
            .setImageOutputDirectory(applicationContext.getExternalFilesDir(Environment.DIRECTORY_PICTURES)?.absolutePath + "/image/")
            .createImageName {
                System.currentTimeMillis().toString() + ".jpg"
            }
            .setVideoOutputDirectory(applicationContext.getExternalFilesDir(Environment.DIRECTORY_PICTURES)?.absolutePath + "/video/")
            .createVideoName {
                System.currentTimeMillis().toString() + ".mp4"
            }
            .setVideoMaxDuration(10 * 1000)
            .build()
        binding.previewLayout.setFileOutputOptions(option)

        val watermark = TextView(this)
        watermark.text = "测试水印水印"
        watermark.textSize = 14f
        watermark.setTextColor(resources.getColor(android.R.color.white))

        val watermarkOptions = WatermarkOptions.Companion.WatermarkOptionsBuilder()
            .setWatermarkView(watermark)
            .setPreviewShowWaterMark(true)
            .setWatermarkPosition(WatermarkPosition(CameraConstant.WATER_POSITION_CENTER, 150f, 200f))
            .setSaveWatermarkTofile(true)
            .build()
        //add watermark
        binding.previewLayout.setWatermarkOptions(watermarkOptions)

        binding.takePicture.setOnClickListener {
            controller.takePicture(
                object : ImageSavedCallback {
                    override fun onError(message: String?, code: Int) {

                    }

                    override fun onImageSaved(originUri: Uri, bitmap: Bitmap?, watermarkFile: File?) {

                    }
                },
//                tempOption = watermarkOptions //add watermark just in time
            )
        }

        binding.startRecord.setOnClickListener {
            if (controller.isRecording()) {
                controller.stopRecord()
                binding.startRecord.text = "start recording"
            } else {
                controller.startRecord(
                    object : VideoSavedCallback {
                        override fun onVideoSaved(uri: Uri, watermarkFile: File?) {
                            binding.startRecord.text = "start recording"
                        }

                        override fun onError(videoCaptureError: Int, message: String, cause: Throwable?) {

                        }

                        override fun onWatermarkVideoProgressing(progress: Int, progressTime: Long) {
                            //do some loading
                        }

                    },
                    tempOption = watermarkOptions
                )
                binding.startRecord.text = "stop recording"
            }
        }

        binding.switchCamera.setOnClickListener {
            controller.switchCamera()
        }

        binding.flashAuto.setOnClickListener {
            controller.setFlashMode(CameraConstant.FLASH_MODE_AUTO)
        }

        binding.flashOn.setOnClickListener {
            controller.setFlashMode(CameraConstant.FLASH_MODE_ON)
        }

        binding.flashOff.setOnClickListener {
            controller.setFlashMode(CameraConstant.FLASH_MODE_OFF)
        }

    }
}