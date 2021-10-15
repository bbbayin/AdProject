package video.report.mediaplayer

import android.content.Context
import android.content.Intent
import android.os.Build
import androidx.appcompat.app.AppCompatDelegate
import androidx.lifecycle.LifecycleObserver
import androidx.lifecycle.ProcessLifecycleOwner
import androidx.multidex.MultiDexApplication
import com.google.firebase.FirebaseApp
import com.liulishuo.okdownload.OkDownload
import com.liulishuo.okdownload.core.Util
import com.liulishuo.okdownload.core.connection.DownloadUrlConnection
import com.liulishuo.okdownload.core.dispatcher.DownloadDispatcher
import miku.ad.AdConfig
import miku.ad.SDKConfiguration
import miku.ad.adapters.FuseAdLoader
import video.report.mediaplayer.constant.Constants
import video.report.mediaplayer.di.AppComponent
import video.report.mediaplayer.di.AppModule
import video.report.mediaplayer.di.DaggerAppComponent
import video.report.mediaplayer.di.injector
import video.report.mediaplayer.firebase.FireBaseEventUtils
import video.report.mediaplayer.firebase.Events
import video.report.mediaplayer.firebase.RemoteConfig
import video.report.mediaplayer.preference.UserPreferences
import video.report.mediaplayer.util.LanUtils
import java.util.*
import javax.inject.Inject

class MyApplication : MultiDexApplication(), LifecycleObserver {
    val applicationComponent: AppComponent by lazy { appComponent }

    @Inject
    lateinit var userPrefs: UserPreferences

    override fun onCreate() {
        super.onCreate()
        instance = this
        appComponent = DaggerAppComponent.builder().appModule(AppModule(this)).build()
        try {
            injector.inject(this)
            FirebaseApp.initializeApp(instance)
            RemoteConfig.init()
            initAd()
            ProcessLifecycleOwner.get().lifecycle.addObserver(this)
            FireBaseEventUtils.getInstance().report(Events.EVENT_APP_ACTIVE)
        } catch (e: Exception) {
        }

//        loadMainAd()
    }

    private fun initAd() {
        val builder = SDKConfiguration.Builder()
        builder.admobAppId("ca-app-pub-9470036790916620~1407094324")
                .mopubAdUnit("bd1e2e0de6b9485b8e01b5dc40c71e97")
                .vgAdUnit("616155990221f159a6d5cad6")
        FuseAdLoader.setBanInvalidAd(true)
        FuseAdLoader.init(object : FuseAdLoader.ConfigFetcher {
            override fun isAdFree(slot: String): Boolean {
                return /*!BuildConfig.DEBUG &&*/ isAdFree()
            }

            override fun getAdConfigList(slot: String): List<AdConfig> {
                return RemoteConfig.getAdConfigList(slot)
            }
        }, this, builder.build())
    }

    fun isAdFree(): Boolean {
        return false
    }

    fun getPreference(): UserPreferences {
        return userPrefs
    }

    private fun loadMainAd() {
        FuseAdLoader.get(Constants.AD_SLOT_DOWNLOADLIST, this).preLoadAd(this)
        FuseAdLoader.get(Constants.AD_SLOT_HOMEPAGE, this).preLoadAd(this)
        FuseAdLoader.get(Constants.AD_SLOT_DOWNLOAD_INSTERSTITIAL, this).preLoadAd(this)

        FuseAdLoader.get(Constants.AD_SLOT_HOMEPAGE, this).setAutoLoad(true)
        FuseAdLoader.get(Constants.AD_SLOT_DOWNLOADLIST, this).setAutoLoad(true)
    }

    companion object {
        private const val TAG = "MyApplication"

        init {
            AppCompatDelegate.setCompatVectorFromResourcesEnabled(Build.VERSION.SDK_INT == Build.VERSION_CODES.KITKAT)
            AppCompatDelegate.setDefaultNightMode(AppCompatDelegate.MODE_NIGHT_FOLLOW_SYSTEM)
        }

        @JvmStatic
        lateinit var appComponent: AppComponent

        @JvmStatic
        lateinit var instance: MyApplication

        @JvmStatic
        var default_laguage: Locale? = null
    }


    override fun attachBaseContext(base: Context?) {
        try {
            if (!LanUtils.getInstance(base).languageCheckState()) {
                LanUtils.getInstance(base).saveLanguage(0)
                LanUtils.getInstance(base).setLanguageCheckState()
            }
        } catch (e: Exception) {
        }
        default_laguage = LanUtils.getSystemLocale(base)
        val local = if (LanUtils.getInstance(base).selectLanguage == 0) default_laguage else Constants.LANGUAGE[LanUtils.getInstance(base).selectLanguage]
        super.attachBaseContext(LanUtils.setLocal(base, local))
    }

}