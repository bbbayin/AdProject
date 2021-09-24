package video.report.mediaplayer.ui.activity

import android.os.Bundle
import android.view.Window
import kotlinx.android.synthetic.main.mp_video_player_activity.*
import miku.ad.AdConstants
import miku.ad.adapters.FuseAdLoader
import video.report.mediaplayer.MyApplication
import video.report.mediaplayer.R
import video.report.mediaplayer.constant.Constants
import video.report.mediaplayer.firebase.FireBaseEventUtils
import video.report.mediaplayer.firebase.Events
import video.report.mediaplayer.util.NetworkUtils
import java.util.*


class VPlayerActivity : BaseActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        requestWindowFeature(Window.FEATURE_NO_TITLE)
        super.onCreate(savedInstanceState)
        loadVideoExitAd()
        setContentView(R.layout.mp_video_player_activity)
        show_btn.setOnClickListener {
            finish()
        }
    }

    override fun finish() {
        FireBaseEventUtils.getInstance().report(Events.AD_VIDEOEXIT_COME)
        if (MyApplication.instance.isAdFree()) {
            FireBaseEventUtils.getInstance().report(Events.AD_VIDEOEXIT_AD_CLOSE)
            super.finish()
            return
        }
        FireBaseEventUtils.getInstance().report(Events.AD_VIDEOEXIT_AD_OPEN)
        if (!NetworkUtils.isNetworkConnected()) {
            FireBaseEventUtils.getInstance().report(Events.AD_VIDEOEXIT_WITH_NO_NETWORK)
            super.finish()
            return
        }
        FireBaseEventUtils.getInstance().report(Events.AD_VIDEOEXIT_WITH_NETWORK)
        try {

            val typeList = ArrayList<String>()
            typeList.add(AdConstants.AdType.AD_SOURCE_MOPUB_INTERSTITIAL)
            typeList.add(AdConstants.AdType.AD_SOURCE_ADMOB_INTERSTITIAL_H)
            typeList.add(AdConstants.AdType.AD_SOURCE_ADMOB_INTERSTITIAL_M)
            typeList.add(AdConstants.AdType.AD_SOURCE_ADMOB_INTERSTITIAL)
//            typeList.add(AdConstants.AdType.AD_SOURCE_VG_INTERSTITIAL)
            var ad = FuseAdLoader.getAllTopAdByScenes(this, typeList, Constants.AD_SLOT_VIDEOEXIT, Constants.AD_SLOT_DOWNLOAD_INSTERSTITIAL)

            if (ad != null) {
                ad?.show()
                FireBaseEventUtils.getInstance().report(Events.AD_VIDEOEXIT_ADSHOW)
                if (ad.adType == AdConstants.AdType.AD_SOURCE_ADMOB_INTERSTITIAL||ad.adType == AdConstants.AdType.AD_SOURCE_ADMOB_INTERSTITIAL_H||ad.adType == AdConstants.AdType.AD_SOURCE_ADMOB_INTERSTITIAL_M) {
                    FireBaseEventUtils.getInstance().report(Events.AD_VIDEOEXIT_ADSHOW_ADMOB)
                }else if (ad.adType == AdConstants.AdType.AD_SOURCE_MOPUB_INTERSTITIAL) {
                    FireBaseEventUtils.getInstance().report(Events.AD_VIDEOEXIT_ADSHOW_MOPUB)
                }
                super.finish()
            } else {
                super.finish()
            }
        } catch (e: Exception) {
            super.finish()
            //try again
        }
    }

    private fun loadVideoExitAd() {
        FuseAdLoader.get(Constants.AD_SLOT_VIDEOEXIT, this).preLoadAd(this)
    }
}
