package cn.com.zt.camera.until

import android.media.MediaMetadataRetriever

object VideoUtil {

    fun getVideoSize(videoPath: String?): Pair<Int, Int>? {
        if (videoPath == null) {
            return null
        }
        val retriever = MediaMetadataRetriever()
        retriever.setDataSource(videoPath)
        try {
            val width = retriever.extractMetadata(MediaMetadataRetriever.METADATA_KEY_VIDEO_WIDTH)?.toInt() ?: 1
            val height = retriever.extractMetadata(MediaMetadataRetriever.METADATA_KEY_VIDEO_HEIGHT)?.toInt() ?: 1
            return Pair(width, height)
        } catch (e: Exception) {

        } finally {
            retriever.release()
        }
        return null
    }
}