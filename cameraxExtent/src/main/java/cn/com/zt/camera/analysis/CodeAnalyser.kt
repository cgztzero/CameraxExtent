package cn.com.zt.camera.analysis

import android.annotation.SuppressLint
import android.graphics.Rect
import android.graphics.RectF
import android.util.Log
import androidx.camera.core.ImageAnalysis
import androidx.camera.core.ImageProxy
import cn.com.zt.camera.listener.CodeAnalysisCallback
import com.google.mlkit.vision.barcode.BarcodeScannerOptions
import com.google.mlkit.vision.barcode.BarcodeScanning
import com.google.mlkit.vision.barcode.common.Barcode
import com.google.mlkit.vision.common.InputImage

/**
 * QRCode or Barcode image Analyser
 */
class CodeAnalyser(
    private val listener: CodeAnalysisCallback,
    private val scanViewWidth: Int,
    private val scanViewHeight: Int,
) :
    ImageAnalysis.Analyzer {

    private val options = BarcodeScannerOptions.Builder()
        .setBarcodeFormats(Barcode.FORMAT_ALL_FORMATS).build()
    private val detector = BarcodeScanning.getClient(options)

    @SuppressLint("UnsafeExperimentalUsageError", "UnsafeOptInUsageError")
    override fun analyze(imageProxy: ImageProxy) {
        val mediaImage = imageProxy.image ?: kotlin.run {
            imageProxy.close()
            return
        }
        val image = InputImage.fromMediaImage(mediaImage, imageProxy.imageInfo.rotationDegrees)
        detector.process(image)
            .addOnSuccessListener { barCodes ->
                if (barCodes.size > 0) {
                    parseCode(barCodes, imageProxy.width, imageProxy.height)

                }
            }
            .addOnFailureListener {
                listener.onCodeAnalysisFail(it)
            }
            .addOnCompleteListener {
                imageProxy.close()
                listener.onCodeAnalysisComplete()
            }
    }

    private fun parseCode(barcodes: List<Barcode>, width: Int, height: Int) {
        if (barcodes.isEmpty()) {
            return
        }
        initScale(width, height)
        val rectList = ArrayList<RectF>()
        val resultList = ArrayList<String>()

        barcodes.forEach { barcode ->
            barcode.boundingBox?.let { rect ->
                val translateRect = translateRect(rect)
                rectList.add(translateRect)
                resultList.add(barcode.rawValue ?: "")
            }
        }
        listener.onCodeAnalysisSuccess(resultList, rectList, width, height)
    }

    private var scaleX = 1f
    private var scaleY = 1f

    private fun translateX(x: Float): Float = x * scaleX
    private fun translateY(y: Float): Float = y * scaleY

    private fun translateRect(rect: Rect) = RectF(
        translateX(rect.left.toFloat()),
        translateY(rect.top.toFloat()),
        translateX(rect.right.toFloat()),
        translateY(rect.bottom.toFloat())
    )

    private fun initScale(imageWidth: Int, imageHeight: Int) {
        scaleY = scanViewHeight.toFloat() / imageWidth.toFloat()
        scaleX = scanViewWidth.toFloat() / imageHeight.toFloat()
    }
}