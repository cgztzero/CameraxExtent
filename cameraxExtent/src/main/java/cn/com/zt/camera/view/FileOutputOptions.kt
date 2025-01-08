package cn.com.zt.camera.view

class FileOutputOptions private constructor(
    private var imageOutputDirectory: String? = null,
    private var videoOutputDirectory: String? = null,
    private var createImageName: (() -> String)? = null,
    private var createVideoName: (() -> String)? = null,
    private var videoMaxDuration: Long = 3 * 1000,
) {

    fun getVideoMaxDuration(): Long {
        return videoMaxDuration
    }

    fun getImageOutputDirectory(): String? {
        return imageOutputDirectory
    }

    fun getVideoOutputDirectory(): String? {
        return videoOutputDirectory
    }

    fun getCreateImageName(): (() -> String)? {
        return createImageName
    }

    fun getCreateVideoName(): (() -> String)? {
        return createVideoName
    }

    companion object {

        class FileOutputOptionsBuilder {
            private var imageOutputDirectory: String? = null
            private var videoOutputDirectory: String? = null
            private var createImageName: (() -> String)? = null
            private var createVideoName: (() -> String)? = null
            private var videoMaxDuration: Long = 30 * 1000

            fun setVideoMaxDuration(millis: Long): FileOutputOptionsBuilder {
                videoMaxDuration = if (millis < 1000) {
                    1
                } else {
                    millis
                }
                return this
            }

            fun setVideoOutputDirectory(path: String): FileOutputOptionsBuilder {
                videoOutputDirectory = path
                return this
            }

            fun createVideoName(f: () -> String): FileOutputOptionsBuilder {
                createVideoName = f
                return this
            }

            fun setImageOutputDirectory(path: String): FileOutputOptionsBuilder {
                imageOutputDirectory = path
                return this
            }

            fun createImageName(f: () -> String): FileOutputOptionsBuilder {
                createImageName = f
                return this
            }

            fun build(): FileOutputOptions {
                return FileOutputOptions(
                    imageOutputDirectory, videoOutputDirectory, createImageName, createVideoName, videoMaxDuration
                )
            }
        }
    }
}

