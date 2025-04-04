package cn.com.zt.watermark

import android.graphics.RectF
import android.os.Bundle
import android.view.LayoutInflater
import android.widget.Toast
import androidx.fragment.app.FragmentActivity
import cn.com.zt.camera.controller.CameraControllerImpl
import cn.com.zt.camera.listener.CodeAnalysisCallback
import cn.com.zt.watermark.databinding.ScanActivityBinding

class ScanActivity : FragmentActivity() {

    private lateinit var binding: ScanActivityBinding
    private val controller = CameraControllerImpl()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ScanActivityBinding.inflate(LayoutInflater.from(this))
        setContentView(binding.root)
        binding.previewLayout.bindToLifecycle(this)
        controller.bindCameraPreview(binding.previewLayout)
        controller.startPreview()
        controller.startScan(
            object : CodeAnalysisCallback {
                override fun onCodeAnalysisSuccess(
                    resultList: ArrayList<String>,
                    rectList: ArrayList<RectF>,
                    imageWidth: Int,
                    imageHeight: Int,
                ) {
                    resultList.forEach {
                        Toast.makeText(this@ScanActivity, it, Toast.LENGTH_LONG).show()
                    }
                }

                override fun onCodeAnalysisFail(fail: Exception) {

                }

                override fun onCodeAnalysisComplete() {

                }

            },
        )

    }
}