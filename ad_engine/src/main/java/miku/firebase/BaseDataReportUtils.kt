package miku.firebase

import android.os.Bundle
import android.text.TextUtils
import android.util.Log
import com.google.firebase.analytics.FirebaseAnalytics
import miku.ad.AdConstants.AdType.*
import miku.ad.AdUtils
import miku.ad.BuildConfig
import miku.ad.adapters.FuseAdLoader
import miku.ad.adapters.IAdAdapter
import miku.storage.LocalDataSourceImpl
import java.text.SimpleDateFormat
import java.util.*

class BaseDataReportUtils private constructor() {
    val BASE_ACTION = "base_action"

    fun reportReward(key: String, name: String, param: String) {
        val bundle = Bundle()
        bundle.putString(name, param)
        reportDirect(key, bundle)
    }

    fun reportAdClickType(ad: IAdAdapter, key: String) {
        if (ad.adType === AD_SOURCE_ADMOB ||ad.adType === AD_SOURCE_ADMOB_H ||ad.adType === AD_SOURCE_ADMOB_M || ad.adType === AD_SOURCE_ADMOB_INTERSTITIAL|| ad.adType === AD_SOURCE_ADMOB_INTERSTITIAL_H|| ad.adType === AD_SOURCE_ADMOB_INTERSTITIAL_M
                || ad.adType === AD_SOURCE_ADMOB_BANNER || ad.adType === AD_SOURCE_ADMOB_REWARD) {
            reportDirect(key + "_admob")
        } else if (ad.adType === AD_SOURCE_MOPUB || ad.adType === AD_SOURCE_MOPUB_INTERSTITIAL || ad.adType === AD_SOURCE_MOPUB_REWARD) {
            reportDirect(key + "_mopub")
        } else if (ad.adType === AD_SOURCE_VG_INTERSTITIAL || ad.adType === AD_SOURCE_VG || ad.adType === AD_SOURCE_VG_REWARD || ad.adType === AD_SOURCE_VG_BANNER) {
            reportDirect(key + "_vungle")
        }  else {
            reportDirect(key + "_other")
        }
    }

    fun reportAdType(ad: IAdAdapter, key: String) {
        if (ad.adType === AD_SOURCE_ADMOB ||ad.adType === AD_SOURCE_ADMOB_H ||ad.adType === AD_SOURCE_ADMOB_M || ad.adType === AD_SOURCE_ADMOB_INTERSTITIAL|| ad.adType === AD_SOURCE_ADMOB_INTERSTITIAL_H|| ad.adType === AD_SOURCE_ADMOB_INTERSTITIAL_M
                || ad.adType === AD_SOURCE_ADMOB_BANNER || ad.adType === AD_SOURCE_ADMOB_REWARD) {
            reportDirect(key + "_admob")
        } else if (ad.adType === AD_SOURCE_MOPUB || ad.adType === AD_SOURCE_MOPUB_INTERSTITIAL || ad.adType === AD_SOURCE_MOPUB_REWARD) {
            reportDirect(key + "_mopub")
        }else if (ad.adType === AD_SOURCE_VG_INTERSTITIAL || ad.adType === AD_SOURCE_VG || ad.adType === AD_SOURCE_VG_REWARD || ad.adType === AD_SOURCE_VG_BANNER) {
            reportDirect(key + "_vungle")
        } else if (ad.adType === AD_SOURCE_ADMOB_H || ad.adType === AD_SOURCE_ADMOB_INTERSTITIAL_H) {
            reportDirect(key + "_admob_h")
        } else if (ad.adType === AD_SOURCE_ADMOB_M || ad.adType === AD_SOURCE_ADMOB_INTERSTITIAL_M) {
            reportDirect(key + "_admob_m")
        } else if (ad.adType === AD_SOURCE_PROPHET) {
            reportDirect(key + "_prophet")
        }else {
            reportDirect(key + "_other")
        }
        LocalDataSourceImpl.getInstance().addAdShowNum(ad);
    }

    fun reportAdTypeShowAndClick(ad: IAdAdapter, key: String) {
        reportAdType(ad, key);
        FuseAdLoader.setAdClickListener(ad, key.replace("adshow", "adclick"))
    }

    @JvmOverloads
    fun reportDirect(key: String, value: Bundle? = null) {
        if (BuildConfig.DEBUG) {
            if (key.equals("ad_category")) {
                return;
            }
            Log.d("BaseDataReportUtils", "$key")
        }
        mFirebaseAnalytics.logEvent(key, value ?: Bundle())
    }

    @JvmOverloads
    fun reportDirect(key: String, name: String, param: String) {
        if (BuildConfig.DEBUG) {
            if (key.equals("ad_category")) {
                return;
            }
            Log.d("BaseDataReportUtils", "$key" + param)
        }
        val bundle = Bundle()
        bundle.putString(name, param)
        mFirebaseAnalytics.logEvent(key, bundle)
    }

    @JvmOverloads
    fun report(key: String, value: Bundle? = null) {
        if (BuildConfig.DEBUG) {
            Log.d("BaseDataReportUtils", "$key" + value)
            mFirebaseAnalytics.logEvent(key, value ?: Bundle())
        }
    }

    fun report(key: String, name: String, param: String) {
        val bundle = Bundle()
        bundle.putString(name, param)
        report(key, bundle)
    }

    fun reportAction(key: String, direct: Boolean, vararg args: String) {
        if (direct) {
            reportActionDirect(key, *args);
        } else {
            reportActionCategory(key, *args)
        }
    }

    fun reportActionDirect(key: String, vararg args: String) {
        if (args.size > 0) {
            val bundle = Bundle()
            for (i in args.indices) {
                if (args.size > i + 1) {
                    bundle.putString(args[i], args[i + 1])
                }
            }
            report(key, bundle)
            return
        }
        report(key, null)
    }

    fun reportActionCategory(key: String, vararg args: String) {
        var action = ""
        for (i in args.indices) {
            if (TextUtils.isEmpty(action)) {
                action += args[i]
            } else {
                action = action + "_" + args[i]
            }
        }
        if (TextUtils.isEmpty(action)) {
            report(key, null)
        } else {
            val bundle = Bundle()
            bundle.putString(BASE_ACTION, action)
            report(key, bundle)
        }
    }

    fun reportAdClickAndShowIfNeed() {
        var curDate = getDate()
        if (!TextUtils.isEmpty(LocalDataSourceImpl.getInstance().adReportDate) && !LocalDataSourceImpl.getInstance().adReportDate.equals(curDate)) {
            reportAdClick()
            reportAdShow()
            FuseAdLoader.setAdmobFree(false)
            FuseAdLoader.setFanFree(false)
        }
        LocalDataSourceImpl.getInstance().adReportDate = getInstance().getDate()
    }

    fun reportAdClick() {
        var num = LocalDataSourceImpl.getInstance().getAdNumByKey("admob_click_num")
        var value = "ad_admob_" + "click_" + num;
        reportDirect("ad_platform", "ad_platform_action_number", value)
        LocalDataSourceImpl.getInstance().setAdNumByKey("admob_click_num", 0);

        num = LocalDataSourceImpl.getInstance().getAdNumByKey("fan_click_num")
        value = "ad_fan_" + "click_" + num;
        reportDirect("ad_platform", "ad_platform_action_number", value)
        LocalDataSourceImpl.getInstance().setAdNumByKey("fan_click_num", 0);

        num = LocalDataSourceImpl.getInstance().getAdNumByKey("mopub_click_num")
        value = "ad_mopub_" + "click_" + num;
        reportDirect("ad_platform", "ad_platform_action_number", value)
        LocalDataSourceImpl.getInstance().setAdNumByKey("mopub_click_num", 0);
    }

    fun reportAdShow() {
        var num = LocalDataSourceImpl.getInstance().getAdNumByKey("admob_show_num")
        var value = "ad_admob_" + "show_" + num;
        reportDirect("ad_platform", "ad_platform_action_number", value)
        LocalDataSourceImpl.getInstance().setAdNumByKey("admob_show_num", 0);

        num = LocalDataSourceImpl.getInstance().getAdNumByKey("fan_show_num")
        value = "ad_fan_" + "show_" + num;
        reportDirect("ad_platform", "ad_platform_action_number", value)
        LocalDataSourceImpl.getInstance().setAdNumByKey("fan_show_num", 0);

        num = LocalDataSourceImpl.getInstance().getAdNumByKey("mopub_show_num")
        value = "ad_mopub_" + "show_" + num;
        reportDirect("ad_platform", "ad_platform_action_number", value)
        LocalDataSourceImpl.getInstance().setAdNumByKey("mopub_show_num", 0);
    }

    fun getExceedKey(ad: IAdAdapter): String {
        var key = ""
        if (ad.getAdType() === AD_SOURCE_ADMOB ||ad.getAdType() === AD_SOURCE_ADMOB_H ||ad.getAdType() === AD_SOURCE_ADMOB_M || ad.getAdType() === AD_SOURCE_ADMOB_INTERSTITIAL|| ad.getAdType() === AD_SOURCE_ADMOB_INTERSTITIAL_H|| ad.getAdType() === AD_SOURCE_ADMOB_INTERSTITIAL_M
                || ad.getAdType() === AD_SOURCE_ADMOB_BANNER || ad.getAdType() === AD_SOURCE_ADMOB_REWARD ) {
            key = "admob_exceed_"
        }  else if (ad.adType === AD_SOURCE_MOPUB || ad.adType === AD_SOURCE_MOPUB_INTERSTITIAL || ad.adType === AD_SOURCE_MOPUB_REWARD) {
            key = "mopub_exceed_"
        }
        return key;
    }

    fun reportExceedAdClick(ad: IAdAdapter) {
        var key = getExceedKey(ad);
        if (TextUtils.isEmpty(key)) {
            return
        }
        reportDirect("ad_platform", "ad_platform_action_number", key + AdUtils.getLocalIpAddress())
    }

    fun getDate(): String {
        var simpleDateFormat = SimpleDateFormat("MM:dd");// HH:mm:ss
        var date = Date(System.currentTimeMillis());
        return simpleDateFormat.format(date);
    }

    companion object {
        @Volatile
        private var instance: BaseDataReportUtils? = null
        private var mFirebaseAnalytics: FirebaseAnalytics = FirebaseAnalytics.getInstance(FuseAdLoader.getContext())

        @JvmStatic
        fun getInstance(): BaseDataReportUtils {
            if (instance == null) {
                instance = BaseDataReportUtils()
            }
            return instance!!
        }
    }

}