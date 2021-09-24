package video.report.mediaplayer.ui.fragment

import android.os.Bundle
import android.text.TextUtils
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.LinearLayoutManager
import io.reactivex.Completable
import io.reactivex.Scheduler
import io.reactivex.disposables.CompositeDisposable
import io.reactivex.functions.Consumer
import kotlinx.android.synthetic.main.fragment_download.*
import kotlinx.android.synthetic.main.fragment_download.ad_container
import miku.ad.AdConstants
import miku.ad.AdViewBinder
import miku.ad.adapters.AdBaseListener
import miku.ad.adapters.FuseAdLoader
import miku.ad.adapters.IAdAdapter
import video.report.mediaplayer.ui.dialog.DialogItem
import video.report.mediaplayer.MyApplication
import video.report.mediaplayer.R
import video.report.mediaplayer.constant.Constants
import video.report.mediaplayer.di.DatabaseScheduler
import video.report.mediaplayer.di.MainScheduler
import video.report.mediaplayer.di.injector
import video.report.mediaplayer.firebase.FireBaseEventUtils
import video.report.mediaplayer.firebase.Events
import video.report.mediaplayer.preference.UserPreferences
import video.report.mediaplayer.rxbus.RxBus
import video.report.mediaplayer.rxbus.event.ADStateEvent
import video.report.mediaplayer.ui.activity.MainNavActivity
import video.report.mediaplayer.ui.dialog.DownloaderDialog
import video.report.mediaplayer.util.FileUtils
import video.report.mediaplayer.util.MediaScannerUtils
import video.report.mediaplayer.util.NetworkUtils
import miku.firebase.BaseDataReportUtils
import video.report.mediaplayer.firebase.RemoteConfig
import java.util.*
import javax.inject.Inject

class FragmentDownload : BaseFragment() {

    var mIsAutoDownload: Boolean = false


    private val disposer: CompositeDisposable = CompositeDisposable()

    @Inject
    @field:MainScheduler
    lateinit var mainScheduler: Scheduler
    @Inject
    @field:DatabaseScheduler
    lateinit var databaseScheduler: Scheduler

    var ad: IAdAdapter? = null

    @Inject
    lateinit var userPrefs: UserPreferences

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        try {
            injector.inject(this)
        } catch (e: Exception) {
        }
    }

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        return inflater.inflate(R.layout.fragment_download, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        initView()
    }


    override fun onDestroyView() {
        super.onDestroyView()
        disposer.clear()
        RxBus.instance?.unSubscribe(this)
    }

    override fun onHiddenChanged(hidden: Boolean) {
        super.onHiddenChanged(hidden)

        if (!hidden) {
            if (!mIsAutoDownload) {
                showDownloadAd()
            }
            //showDownloadAd()
            //showAd()
        }
        mIsAutoDownload = false

    }

    override fun onResume() {
        super.onResume()
        if (!isHidden ) {
            showDownloadAd()
        }
            if (ad_container != null) {
                ad_container?.apply {
                    visibility = View.VISIBLE
                }
            }
    }


    private fun initView() {
    }


    fun setDefaultCheckFragment() {
    }

    interface InfoCallback {
        fun updateToolBar()
        fun updateCheckTitle()
        fun updateCheckTitleInit()
    }
    internal fun loadAd() {
        if (!MyApplication.instance.isAdFree()&&activity != null && !activity!!.isFinishing) {
            FuseAdLoader.get(Constants.AD_SLOT_DOWNLOADLIST, activity).loadAd(activity, object : AdBaseListener() {
                override fun onAdLoaded() {
                    if (isFragmentForeground) {
                        val typeList = ArrayList<String>()
                        typeList.add(AdConstants.AdType.AD_SOURCE_MOPUB)
                        typeList.add(AdConstants.AdType.AD_SOURCE_ADMOB_H)
                        typeList.add(AdConstants.AdType.AD_SOURCE_ADMOB_M)
                        typeList.add(AdConstants.AdType.AD_SOURCE_ADMOB)
                        var ad = FuseAdLoader.getAllTopAdByScenes(activity, typeList,true, Constants.AD_SLOT_DOWNLOADLIST, Constants.AD_SLOT_HOMEPAGE)
                        inflateNativeAd(ad)
                    }
                }
            })
        }
    }

    private fun showDownloadAd() {
        try {
            FireBaseEventUtils.getInstance().report(Events.AD_DOWNLOADS_COME)
            if (!MyApplication.instance.isAdFree()) {
                FireBaseEventUtils.getInstance().report(Events.AD_DOWNLOADS_AD_OPEN)
                if (NetworkUtils.isNetworkConnected()) {
                    FireBaseEventUtils.getInstance().report(Events.AD_DOWNLOADS_WITH_NETWORK)
                    val typeList = ArrayList<String>()
                    typeList.add(AdConstants.AdType.AD_SOURCE_MOPUB)
                    typeList.add(AdConstants.AdType.AD_SOURCE_ADMOB_H)
                    typeList.add(AdConstants.AdType.AD_SOURCE_ADMOB_M)
                    typeList.add(AdConstants.AdType.AD_SOURCE_ADMOB)
                    var ad = FuseAdLoader.getAllTopAdByScenes(activity, typeList,true, Constants.AD_SLOT_DOWNLOADLIST, Constants.AD_SLOT_HOMEPAGE)
                    if (ad != null) {
                        inflateNativeAd(ad)
                    }
                }
            }
        } catch (e: Exception) {
            loadAd()
        }
    }

    private fun inflateNativeAd(ad: IAdAdapter?) {
        ad?.let {
            if (activity != null && isVisible) {
                val viewBinder = AdViewBinder.Builder(R.layout.layout_download_native_ad)
                        .titleId(R.id.ad_title)
                        .textId(R.id.ad_subtitle_text)
                        .iconImageId(R.id.ad_icon_image)
                        .callToActionId(R.id.ad_cta_text)
                        .privacyInformationId(R.id.ad_choices_container)
                        .adFlagId(R.id.ad_flag)
                        .build()
                try {
                    val adView = it.getAdView(activity, viewBinder)
                    if (adView != null) {
                        ad_container.removeAllViews()
                        ad_container.addView(adView)
                        ad_container.visibility = View.VISIBLE
                        FireBaseEventUtils.getInstance().report(Events.AD_NAME_DOWNLOADLIST)
                        if (ad.adType == AdConstants.AdType.AD_SOURCE_ADMOB||ad.adType == AdConstants.AdType.AD_SOURCE_ADMOB_H||ad.adType == AdConstants.AdType.AD_SOURCE_ADMOB_M) {
                            FireBaseEventUtils.getInstance().report(Events.AD_DOWNLOADSTAB_ADSHOW_ADMOB)
                        }else if (ad.adType == AdConstants.AdType.AD_SOURCE_MOPUB) {
                            FireBaseEventUtils.getInstance().report(Events.AD_DOWNLOADSTAB_ADSHOW_MOPUB)
                        }
                        BaseDataReportUtils.getInstance().reportAdTypeShowAndClick(ad, Events.AD_DOWNLOADS_ADSHOW)
                    }
                } catch (e: Exception) {
                }
            }
        }
    }

}