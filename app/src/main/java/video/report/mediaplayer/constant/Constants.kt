package video.report.mediaplayer.constant

import android.os.Environment
import java.io.File
import java.util.*

object Constants {
    val DEFAULT_DOWNLOAD_PATH = Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_DCIM).path +
            File.separator + "Player" + File.separator

    const val BAN_DOWNLOAD_AD = "ban_downlaod_ad"//禁止显示下载完插页广告
    const val AD_APPEXIT = "appexit"

    const val AD_SLOT_HOMEPAGE = "slot_homepage_native"
    const val AD_SLOT_DOWNLOADLIST = "slot_downloadlist_native"
    const val AD_SLOT_VIDEOEXIT = "slot_videoexit_insterstitial"
    const val AD_SLOT_DOWNLOAD_INSTERSTITIAL = "slot_download_insterstitial"

    const val REMOVE_AD = "inshand_billing_77"

    const val ONE_DAYS_TIME = 24 * 60 * 60 * 1000
    const val FIVE_DAYS_TIME = 5 * 24 * 60 * 60 * 1000
    const val THREE_DAYS_TIME = 3 * 24 * 60 * 60 * 1000
    const val TEN_DAYS_TIME = 10 * 24 * 60 * 60 * 1000

    const val ONE_HOURS = 60 * 60 * 1000 // 1h
    const val FOURTY_FIVE_MIN = 45 * 60 * 1000 // 45m
    const val CHECKING_WAITING_TIME = 3000L // 1h


    const val NOTIFICATION_JUMP_KEY = "jump_location"

    const val RECENT_USER_ROW = 2
    const val RECENT_USER_COLUMN = 4

    const val CHECK_STORY_POST = 9

    const val LOGIN_FROM_STORY = "is_story"

    const val NEW_USER_PRE = "newuser_"

    val LANGUAGE = listOf(
            null,
            Locale("en"),
            Locale("de"),
            Locale("es"),
            Locale("fil"),
            Locale("fr"),
            Locale("in"),
            Locale("it"),
            Locale("ja"),
            Locale("ko"),
            Locale("ms"),
            Locale("pt"),
            Locale("ru"),
            Locale("tr"),
            Locale("vi"),
            Locale("hi"),
            Locale("ar"),
            Locale("fa"),
            Locale("th"),
            Locale.TRADITIONAL_CHINESE,
            Locale.SIMPLIFIED_CHINESE
    )

    @JvmField
    val REFOUND_USERS = listOf(
            "GPA.3341-8990-0079-77319"
    )
}
