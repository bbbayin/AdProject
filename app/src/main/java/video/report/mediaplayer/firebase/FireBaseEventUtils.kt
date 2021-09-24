package video.report.mediaplayer.firebase

import android.os.Bundle
import android.util.Log
import com.google.firebase.analytics.FirebaseAnalytics
import video.report.mediaplayer.MyApplication
import video.report.mediaplayer.BuildConfig
import video.report.mediaplayer.constant.Constants
import video.report.mediaplayer.firebase.Events.USER_PROPERTY_COUNTRY
import video.report.mediaplayer.preference.UserPreferences
import video.report.mediaplayer.util.DeviceUtils
import video.report.mediaplayer.util.NetworkUtils

class FireBaseEventUtils private constructor() {
    var userPreferences: UserPreferences? = MyApplication.instance.userPrefs

    @JvmOverloads
    fun report(key: String, value: Bundle? = null) {
        val key=key.replace(" ","")
        if (BuildConfig.DEBUG)
            Log.d("FireBaseEventUtils", "$key")
        mFirebaseAnalytics.logEvent(key, value ?: Bundle())
    }

    fun report(key: String, name: String, param: String) {
        val key=key.replace(" ","")
        val bundle: Bundle = Bundle()
        bundle.putString(name, param)
        report(key, bundle)
    }

    fun reportAd(slot: String) {
        reportAdScenarioCome(slot)
        reportAdNetworkStatus(slot)
        reportAdStatus(slot)
    }

    fun reportAdScenarioCome(slot: String) {
        report("ad_${slot}_come")
    }

    fun reportAdStatus(slot: String) {
        val key = "ad_${slot}_ad_" + if (MyApplication.instance.isAdFree()) "close" else "open"
        report(key)
    }

    fun reportAdNetworkStatus(slot: String) {
        val key = "ad_${slot}_with_" + if (NetworkUtils.isNetworkConnected()) "network" else "no_network"
        report(key)
    }

    fun reportAdShow(slot: String) {
        report("ad_${slot}_adshow")
    }

    companion object {

        @Volatile
        private var instance: FireBaseEventUtils? = null
        private var mFirebaseAnalytics: FirebaseAnalytics = FirebaseAnalytics.getInstance(MyApplication.instance)

        @JvmStatic
        fun getInstance(): FireBaseEventUtils {
            if (instance == null) {
                instance = FireBaseEventUtils()
                mFirebaseAnalytics.setUserProperty(USER_PROPERTY_COUNTRY, DeviceUtils.getMcc(MyApplication.instance).toString())
                if (BuildConfig.VERSION_NAME.contains('P', true)) {
                    mFirebaseAnalytics.setUserProperty("channel", "palmstore")
                }
            }
            return instance!!
        }
    }

    fun reportNew(key: String) {
        if (userPreferences?.newUser!!) {
            report(Constants.NEW_USER_PRE + key)
        }
    }

}
/**
 * 埋点
 *
 * @param key
 */
