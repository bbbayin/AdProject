package video.report.mediaplayer.ui.fragment

import android.app.Activity
import android.content.Context
import android.content.Intent
import android.os.Bundle
import android.view.*
import io.reactivex.Scheduler
import io.reactivex.disposables.CompositeDisposable
import kotlinx.android.synthetic.main.fragment_home.*
import miku.ad.AdConstants
import miku.ad.AdViewBinder
import miku.ad.adapters.AdBaseListener
import miku.ad.adapters.FuseAdLoader
import miku.ad.adapters.IAdAdapter
import video.report.mediaplayer.MyApplication
import video.report.mediaplayer.R
import video.report.mediaplayer.constant.Constants
import video.report.mediaplayer.di.DatabaseScheduler
import video.report.mediaplayer.di.MainScheduler
import video.report.mediaplayer.di.injector
import video.report.mediaplayer.firebase.FireBaseEventUtils
import video.report.mediaplayer.firebase.Events
import video.report.mediaplayer.firebase.Events.AD_HOMEPAGE_ADSHOW
import video.report.mediaplayer.preference.UserPreferences
import video.report.mediaplayer.ui.activity.MainNavActivity
import video.report.mediaplayer.ui.base.HomeContact
import video.report.mediaplayer.util.NetworkUtils
import miku.firebase.BaseDataReportUtils
import video.report.mediaplayer.ui.activity.NewSettingsActivity
import video.report.mediaplayer.ui.activity.PostActivity
import video.report.mediaplayer.ui.activity.VPlayerActivity
import java.util.*
import javax.inject.Inject


class FragmentHome : BaseFragment(), HomeContact.View {

    var infoCallback: InfoCallback? = null
    var badgeInterface: MainNavActivity.BadgeInterface? = null

    @Inject
    lateinit var userPrefs: UserPreferences

    var mActivity: Activity? = null

    @Inject
    @field:MainScheduler
    lateinit var mainScheduler: Scheduler
    @Inject
    @field:DatabaseScheduler
    lateinit var databaseScheduler: Scheduler

    private val disposer: CompositeDisposable = CompositeDisposable()

    var privateUrlDialog: Boolean = false

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        return inflater.inflate(R.layout.fragment_home, container, false)
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        mActivity = activity
        try {
            injector.inject(this)
        } catch (e: Exception) {
            // kotlin inject error for castException
        }
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        initView()
    }

    override fun onDestroyView() {
        super.onDestroyView()
        disposer.clear()
    }

    private fun initView() {
        img_btn.setOnClickListener {
            try {
                val intent = Intent(activity, PostActivity::class.java)
                startActivity(intent)
            } catch (e: Exception) {
                e.printStackTrace()
            }
        }
        video_btn.setOnClickListener {
            try {
                val intent = Intent(activity, VPlayerActivity::class.java)
                startActivity(intent)
            } catch (e: Exception) {
                e.printStackTrace()
            }
        }
    }

    override fun onResume() {
        super.onResume()

            try {
                    showHomeAd()
            } catch (e: Exception) {
                showHomeAd()
            }

    }

    override fun onHiddenChanged(hidden: Boolean) {
        super.onHiddenChanged(hidden)
                showHomeAd()
    }


    override fun getViewContext(): Context {
        return activity ?: MyApplication.instance
    }

    override fun onAttach(context: Context) {
        super.onAttach(context)
        if (context is InfoCallback) {
            infoCallback = context as InfoCallback
        }
    }

    override fun goHomeFragment() {
        infoCallback?.backHomePage()
    }

    private fun loadAd() {
        if (activity != null && !requireActivity().isFinishing ) {
            FuseAdLoader.get(Constants.AD_SLOT_HOMEPAGE, activity).loadAd(activity, object : AdBaseListener() {
                override fun onAdLoaded() {
                    if (isFragmentForeground) {
                        val typeList = ArrayList<String>()
                        typeList.add(AdConstants.AdType.AD_SOURCE_MOPUB)
                        typeList.add(AdConstants.AdType.AD_SOURCE_ADMOB_H)
                        typeList.add(AdConstants.AdType.AD_SOURCE_ADMOB_M)
                        typeList.add(AdConstants.AdType.AD_SOURCE_ADMOB)
                        var ad = FuseAdLoader.getAllTopAdByScenes(activity, typeList,true, Constants.AD_SLOT_HOMEPAGE, Constants.AD_SLOT_DOWNLOADLIST)
                        inflateNativeAd(ad)
                    }
                }
            })
        }
    }

    private fun showHomeAd() {
        try {
            FireBaseEventUtils.getInstance().report(Events.AD_HOMEPAGE_COME)
                FireBaseEventUtils.getInstance().report(Events.AD_HOMEPAGE_AD_OPEN)
                if (NetworkUtils.isNetworkConnected()) {
                    FireBaseEventUtils.getInstance().report(Events.AD_HOMEPAGE_WITH_NETWORK)
                    val typeList = ArrayList<String>()
                    typeList.add(AdConstants.AdType.AD_SOURCE_MOPUB)
                    typeList.add(AdConstants.AdType.AD_SOURCE_ADMOB_H)
                    typeList.add(AdConstants.AdType.AD_SOURCE_ADMOB_M)
                    typeList.add(AdConstants.AdType.AD_SOURCE_ADMOB)
                    var ad = FuseAdLoader.getAllTopAdByScenes(activity, typeList, Constants.AD_SLOT_HOMEPAGE, Constants.AD_SLOT_DOWNLOADLIST)
                    if (ad != null) {
                        inflateNativeAd(ad)
                    } else {
                        loadAd()
                    }
                } else {
                    FireBaseEventUtils.getInstance().report(Events.AD_HOMEPAGE_WITH_NO_NETWORK)
                }
        } catch (e: Exception) {
        }
    }

    private fun inflateNativeAd(ad: IAdAdapter?) {
        if (ad != null && activity != null) {
            val viewBinder = AdViewBinder.Builder(R.layout.layout_home_native_ad)
                    .titleId(R.id.ad_title)
                    .textId(R.id.ad_subtitle_text)
                    .mainMediaId(R.id.ad_cover_image)
                    .iconImageId(R.id.ad_icon_image)
                    .admMediaId(R.id.ad_adm_mediaview)
                    .callToActionId(R.id.ad_cta_text)
                    .privacyInformationId(R.id.ad_choices_container)
                    .adFlagId(R.id.ad_flag)
                    .build()
            try {
                val adView = ad.getAdView(activity, viewBinder)
                if (adView != null) {
                    ad_container?.removeAllViews()
                    ad_container?.addView(adView)
                    ad_container?.visibility = View.VISIBLE
                    FireBaseEventUtils.getInstance().report(Events.AD_HOMEPAGE_ADSHOW)
                    BaseDataReportUtils.getInstance().reportAdTypeShowAndClick(ad, AD_HOMEPAGE_ADSHOW)
                }
            } catch (e: Exception) {
            }
        }
    }


    companion object {
        const val SOURCE_COPY = 0
        const val SOURCE_SHARE = 1

    }

    override fun goDownloadFragment() {
        infoCallback?.backDownloadPage()
    }

    interface InfoCallback {
        fun backHomePage()
        fun backDownloadPage()
        fun showBillDialog()
    }

    override fun onStart() {
        super.onStart()
    }

    override fun onStop() {
        super.onStop()
        privateUrlDialog = false
    }

    override fun onViewStateRestored(savedInstanceState: Bundle?) {
        super.onViewStateRestored(savedInstanceState)
    }


}
