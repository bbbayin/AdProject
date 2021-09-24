package video.report.mediaplayer.preference

import android.content.SharedPreferences
import video.report.mediaplayer.constant.Constants
import video.report.mediaplayer.di.UserPrefs
import video.report.mediaplayer.preference.delegates.booleanPreference
import video.report.mediaplayer.preference.delegates.intPreference
import video.report.mediaplayer.preference.delegates.longPreference
import video.report.mediaplayer.preference.delegates.stringPreference
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class UserPreferences @Inject constructor(
        @UserPrefs preferences: SharedPreferences
) {
    var alreadyPurchase by preferences.booleanPreference(AD_BUYED, false)
    var purchasePrice by preferences.stringPreference(AD_PRICE, "")
    var downloadDirectory by preferences.stringPreference(DOWNLOAD_DIRECTORY, Constants.DEFAULT_DOWNLOAD_PATH)

    var newUser by preferences.booleanPreference(NEW_USER, true)
}

private const val AD_BUYED = "alreadybuyed"
private const val AD_PRICE = "ad_price"
private const val DOWNLOAD_DIRECTORY = "downloadLocation"
private const val NEW_USER = "newUser"

