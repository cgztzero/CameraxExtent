package cn.com.zt.camera.listener

import android.graphics.RectF

interface CodeAnalysisCallback {


    /**
     * scan code success
     * @param rectList all codes
     * @param resultList all codes position on the screen
     */
    fun onCodeAnalysisSuccess(
        resultList: ArrayList<String>,
        rectList: ArrayList<RectF>,
        imageWidth: Int,
        imageHeight: Int
    )

    /**
     * scan code fail
     */
    fun onCodeAnalysisFail(fail:Exception)


    /**
     * scan operation complete
     */
    fun onCodeAnalysisComplete()
}