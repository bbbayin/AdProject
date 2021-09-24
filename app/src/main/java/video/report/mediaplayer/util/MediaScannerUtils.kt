package video.report.mediaplayer.util

import android.media.MediaScannerConnection
import video.report.mediaplayer.MyApplication
import java.io.File

object MediaScannerUtils {
    fun scanner(file: File) {
        MediaScannerConnection.scanFile(MyApplication.instance, arrayOf(file.absolutePath), null) { path, uri -> }
    }

    fun scanner(path: String?) {
        MediaScannerConnection.scanFile(MyApplication.instance, arrayOf(path), null) { path, uri -> }
    }
}
