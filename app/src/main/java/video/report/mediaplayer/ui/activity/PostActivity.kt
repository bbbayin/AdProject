package video.report.mediaplayer.ui.activity

import android.os.Build
import android.os.Bundle
import android.view.View
import android.view.Window
import butterknife.ButterKnife
import io.reactivex.Scheduler
import kotlinx.android.synthetic.main.activity_post.*
import kotlinx.android.synthetic.main.activity_post.show_btn
import kotlinx.android.synthetic.main.activity_post.tool_back_img
import kotlinx.android.synthetic.main.mp_video_player_activity.*
import miku.ad.AdConstants
import miku.ad.adapters.FuseAdLoader
import video.report.mediaplayer.MyApplication
import video.report.mediaplayer.R
import video.report.mediaplayer.constant.Constants
import video.report.mediaplayer.di.DatabaseScheduler
import video.report.mediaplayer.di.injector
import video.report.mediaplayer.firebase.FireBaseEventUtils
import video.report.mediaplayer.firebase.Events
import video.report.mediaplayer.firebase.Events.AD_POST_ADSHOW
import video.report.mediaplayer.util.NetworkUtils
import miku.firebase.BaseDataReportUtils
import java.util.*
import javax.inject.Inject


class PostActivity : BaseActivity() {

    @Inject
    @field:DatabaseScheduler
    lateinit var databaseScheduler: Scheduler

    override fun onCreate(savedInstanceState: Bundle?) {
        injector.inject(this)
            loadVideoExitAd()
        super.onCreate(savedInstanceState)
        try {
            requestWindowFeature(Window.FEATURE_NO_TITLE)
            val decorView = window.decorView
            decorView.setOnSystemUiVisibilityChangeListener { visibility ->
                if (visibility and View.SYSTEM_UI_FLAG_FULLSCREEN == 0)
                    hideSystemUi()
            }
            hideSystemUi()
        } catch (e: Exception) {
            // c.a nameless Exception
        }
        setContentView(R.layout.activity_post)
        ButterKnife.bind(this)

        initView()
        FireBaseEventUtils.getInstance().report(Events.AD_POSTVIEW_COME)
        FireBaseEventUtils.getInstance().report(Events.EVENT_DOWNLOADED_REVIEW_SHOW)
    }

    private fun hideSystemUi() {
        val decorView = window.decorView

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.KITKAT) {
            val uiOptions = View.SYSTEM_UI_FLAG_HIDE_NAVIGATION or
                    View.SYSTEM_UI_FLAG_LAYOUT_STABLE or
                    View.SYSTEM_UI_FLAG_LAYOUT_HIDE_NAVIGATION or
                    View.SYSTEM_UI_FLAG_LAYOUT_FULLSCREEN or
                    View.SYSTEM_UI_FLAG_FULLSCREEN or
                    View.SYSTEM_UI_FLAG_IMMERSIVE_STICKY
            decorView.systemUiVisibility = uiOptions
        }
    }

    private fun initView() {
        tool_back_img.setOnClickListener {
            finish()
        }
        show_btn.setOnClickListener {
            finish()
        }
    }

    private fun loadVideoExitAd() {
        FuseAdLoader.get(Constants.AD_SLOT_VIDEOEXIT, this).preLoadAd(this)
    }

    override fun finish() {
        if (MyApplication.instance.isAdFree()) {
            FireBaseEventUtils.getInstance().report(Events.AD_POST_AD_CLOSE)
            super.finish()
            return
        }
        FireBaseEventUtils.getInstance().report(Events.AD_POST_AD_OPEN)
        if (!NetworkUtils.isNetworkConnected()) {
            FireBaseEventUtils.getInstance().report(Events.AD_POST_WITH_NO_NETWORK)
            super.finish()
            return
        }
        FireBaseEventUtils.getInstance().report(Events.AD_POST_WITH_NETWORK)
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
                FireBaseEventUtils.getInstance().report(AD_POST_ADSHOW)
                BaseDataReportUtils.getInstance().reportAdTypeShowAndClick(ad, AD_POST_ADSHOW)
                super.finish()
            } else {
                super.finish()
            }
        } catch (e: java.lang.Exception) {
            super.finish()
            //try again
        }
    }
}
