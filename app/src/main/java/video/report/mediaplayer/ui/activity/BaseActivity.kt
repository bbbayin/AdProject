package video.report.mediaplayer.ui.activity

import android.content.Context
import android.content.res.Configuration
import androidx.appcompat.app.AppCompatActivity
import com.anthonycr.grant.PermissionsManager
import video.report.mediaplayer.constant.Constants
import video.report.mediaplayer.util.LanUtils

abstract class BaseActivity : AppCompatActivity() {

    override fun onConfigurationChanged(newConfig: Configuration) {
        super.onConfigurationChanged(newConfig)
    }

    override fun onRequestPermissionsResult(requestCode: Int, permissions: Array<String>, grantResults: IntArray) {
        PermissionsManager.getInstance().notifyPermissionsChange(permissions, grantResults)
        super.onRequestPermissionsResult(requestCode, permissions, grantResults)
    }

    override fun attachBaseContext(newBase: Context?) {
        val local = if (LanUtils.getInstance(newBase).selectLanguage == 0) LanUtils.getSystemLocale(newBase) else Constants.LANGUAGE[LanUtils.getInstance(newBase).selectLanguage]
        super.attachBaseContext(LanUtils.setLocal(newBase, local))
    }
}
