package cn.com.zt.camera.view

import android.graphics.Bitmap
import android.view.View
import cn.com.zt.camera.constant.CameraConstant
import cn.com.zt.camera.constant.WatermarkMode
import cn.com.zt.camera.constant.WatermarkPositionMode


class WatermarkOptions private constructor(
    private var watermarkView: View? = null,
    private var watermarkBitmap: Bitmap? = null,
    private var position: WatermarkPosition?,
    @WatermarkMode private var mode: Int,
    private var previewShowWaterMark: Boolean,
    private var saveWatermarkTofile: Boolean = false,
    private var watermarkFilePath: String? = null,
    var createWatermarkFileName: (() -> String)? = null,
    var createWatermarkVideoFileName: (() -> String)? = null
) {

    fun getWatermarkFilePath(): String? {
        return watermarkFilePath
    }

    fun saveWatermarkTofile(): Boolean {
        return saveWatermarkTofile
    }

    fun previewShowWaterMark(): Boolean {
        return previewShowWaterMark
    }

    fun getWatermarkPosition(): WatermarkPosition? {
        return position
    }

    fun getWatermarkMode(): Int {
        return mode
    }

    fun isOnlyVideoAddWatermark(): Boolean {
        return mode == CameraConstant.WATER_MARK_ONLY_VIDEO
    }

    fun isOnlyImageAddWatermark(): Boolean {
        return mode == CameraConstant.WATER_MARK_ONLY_IMAGE
    }

    fun getWatermarkBitmap(): Bitmap? {
        return watermarkBitmap
    }

    fun getWatermarkView(): View? {
        return watermarkView
    }

    companion object {

        class WatermarkOptionsBuilder {
            private var watermarkView: View? = null
            private var watermarkBitmap: Bitmap? = null
            private var position: WatermarkPosition? = null
            private var mode: Int = CameraConstant.WATER_MARK_BOTH
            private var previewShowWaterMark: Boolean = true
            private var saveWatermarkTofile: Boolean = false
            private var watermarkFilePath: String? = null
            private var createWatermarkFileName: (() -> String)? = null
            private var createWatermarkVideoFileName: (() -> String)? = null

            fun setCreateWatermarkVideoFileName(f: () -> String): WatermarkOptionsBuilder {
                createWatermarkVideoFileName = f
                return this
            }

            fun setWatermarkFilePath(path: String): WatermarkOptionsBuilder {
                watermarkFilePath = path
                return this
            }

            fun setCreateWatermarkFileName(f: () -> String): WatermarkOptionsBuilder {
                createWatermarkFileName = f
                return this
            }


            fun setSaveWatermarkTofile(b: Boolean): WatermarkOptionsBuilder {
                saveWatermarkTofile = b
                return this
            }

            fun setWatermarkPosition(p: WatermarkPosition?): WatermarkOptionsBuilder {
                position = p
                return this
            }

            fun setWatermarkView(view: View): WatermarkOptionsBuilder {
                watermarkView = view
                return this
            }

            fun setWatermarkBitmap(bitmap: Bitmap): WatermarkOptionsBuilder {
                watermarkBitmap = bitmap
                return this
            }

            fun setMode(@WatermarkMode m: Int): WatermarkOptionsBuilder {
                mode = m
                return this
            }

            fun setPreviewShowWaterMark(show: Boolean): WatermarkOptionsBuilder {
                previewShowWaterMark = show
                return this
            }

            fun build(): WatermarkOptions {
                return WatermarkOptions(
                    watermarkView,
                    watermarkBitmap,
                    position,
                    mode,
                    previewShowWaterMark,
                    saveWatermarkTofile,
                    watermarkFilePath,
                    createWatermarkFileName
                )
            }
        }
    }

}

class WatermarkPosition constructor(
    @WatermarkPositionMode var position: Int,
    var x: Float,
    var y: Float,
) {

    fun isCenter(): Boolean {
        return position == CameraConstant.WATER_POSITION_CENTER
    }

    fun isTopLeft(): Boolean {
        return position == CameraConstant.WATER_POSITION_TOP_LEFT
    }

    fun isTopRight(): Boolean {
        return position == CameraConstant.WATER_POSITION_TOP_RIGHT
    }

    fun isBottomLeft(): Boolean {
        return position == CameraConstant.WATER_POSITION_BOTTOM_LEFT
    }

    fun isBottomRight(): Boolean {
        return position == CameraConstant.WATER_POSITION_BOTTOM_RIGHT
    }

}