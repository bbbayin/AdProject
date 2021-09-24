package video.report.mediaplayer.util

import android.app.ActivityManager
import android.content.Context
import android.content.pm.PackageInfo
import android.content.pm.PackageManager
import android.graphics.Point
import android.util.DisplayMetrics
import android.view.WindowManager
import androidx.annotation.NonNull
import java.util.*

/**
 * LanUtils used to access information about the device.
 */
object DeviceUtils {

    /**
     * Gets the width of the device's screen.
     *
     * @param context the context used to access the [WindowManager].
     */
    @JvmStatic
    fun getScreenWidth(context: Context): Int {
        val windowManager = context.getSystemService(Context.WINDOW_SERVICE) as WindowManager

        return Point().apply {
            windowManager.defaultDisplay.getSize(this)
        }.x
    }

    /**
     * Gets the width of the screen space currently available to the app.
     *
     * @param context the context used to access the [WindowManager].
     */
    @JvmStatic
    fun getAvailableScreenWidth(context: Context): Int {
        val windowManager = context.getSystemService(Context.WINDOW_SERVICE) as WindowManager

        return DisplayMetrics().apply {
            windowManager.defaultDisplay.getRealMetrics(this)
        }.widthPixels
    }

    @JvmStatic
    fun getScreenHeight(@NonNull context: Context): Int {
        val dm = context.resources.displayMetrics
        return dm.heightPixels
    }

    @JvmStatic
    fun getSystemLanguage(): String {
        val language = Locale.getDefault().language;
        return language
    }

    @JvmStatic
    fun getMcc(context: Context): Int {
        val cfg = context.resources.configuration
        return cfg.mcc
    }

    @JvmStatic
    fun judgeServiceAlive(serviceName: String, context: Context): Boolean {
        var am = context.getSystemService(Context.ACTIVITY_SERVICE) as ActivityManager
        var infos = am.getRunningServices(100)
        for (i in 0 until infos.size) {
            var name = infos[i].service.className
            if (name != null && serviceName.contains(serviceName)) {
                return true
            }
        }
        return false
    }

    @JvmStatic
    fun checkAppInstalled(context: Context, pkgName: String): Boolean {
        if (pkgName == null || pkgName.isEmpty()) {
            return false
        }
        var packageInfo: PackageInfo? = null
        packageInfo = try {
            context.packageManager.getPackageInfo(pkgName, 0)
        } catch (e: PackageManager.NameNotFoundException) {
            null
        }
        return packageInfo != null
    }

}
