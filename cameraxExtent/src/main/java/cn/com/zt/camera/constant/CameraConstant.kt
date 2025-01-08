package cn.com.zt.camera.constant

import androidx.annotation.IntDef
import androidx.camera.core.CameraSelector
import androidx.camera.core.ImageCapture

object CameraConstant {
    const val LENS_FACING_FRONT = CameraSelector.LENS_FACING_FRONT

    const val LENS_FACING_BACK = CameraSelector.LENS_FACING_BACK


    const val FLASH_MODE_AUTO = ImageCapture.FLASH_MODE_AUTO
    const val FLASH_MODE_ON = ImageCapture.FLASH_MODE_ON
    const val FLASH_MODE_OFF = ImageCapture.FLASH_MODE_OFF

    const val WATER_MARK_BOTH = 0
    const val WATER_MARK_ONLY_IMAGE = 1
    const val WATER_MARK_ONLY_VIDEO = 2

    const val WATER_POSITION_TOP_LEFT = 0
    const val WATER_POSITION_TOP_RIGHT = 1
    const val WATER_POSITION_BOTTOM_LEFT = 2
    const val WATER_POSITION_BOTTOM_RIGHT = 3
    const val WATER_POSITION_CENTER = 4

}

@IntDef(CameraConstant.FLASH_MODE_AUTO, CameraConstant.FLASH_MODE_ON, CameraConstant.FLASH_MODE_OFF)
@Retention(AnnotationRetention.SOURCE)
annotation class CameraFlashMode

@IntDef(CameraConstant.WATER_MARK_BOTH, CameraConstant.WATER_MARK_ONLY_IMAGE, CameraConstant.WATER_MARK_ONLY_VIDEO)
@Retention(AnnotationRetention.SOURCE)
annotation class WatermarkMode

@IntDef(
    CameraConstant.WATER_POSITION_TOP_LEFT,
    CameraConstant.WATER_POSITION_TOP_RIGHT,
    CameraConstant.WATER_POSITION_BOTTOM_LEFT,
    CameraConstant.WATER_POSITION_BOTTOM_RIGHT,
    CameraConstant.WATER_POSITION_CENTER,
)
@Retention(AnnotationRetention.SOURCE)
annotation class WatermarkPositionMode