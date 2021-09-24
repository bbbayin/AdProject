package video.report.mediaplayer.ui.activity

import android.Manifest
import android.annotation.SuppressLint
import android.app.Dialog
import android.content.Context
import android.content.Intent
import android.content.SharedPreferences
import android.content.pm.PackageManager
import android.graphics.Color
import android.os.*
import android.text.TextUtils
import android.view.*
import android.webkit.CookieManager
import android.webkit.CookieSyncManager
import android.widget.LinearLayout
import android.widget.TextView
import androidx.appcompat.app.ActionBarDrawerToggle
import androidx.appcompat.view.menu.MenuBuilder
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat
import androidx.core.view.GravityCompat
import androidx.core.view.isVisible
import androidx.fragment.app.Fragment
import butterknife.ButterKnife
import com.anthonycr.grant.PermissionsManager
import com.anthonycr.grant.PermissionsResultAction
import com.google.android.material.bottomnavigation.BottomNavigationItemView
import com.google.android.material.bottomnavigation.BottomNavigationMenuView
import io.reactivex.Completable
import kotlinx.android.synthetic.main.activity_main.*
import miku.ad.AdConstants
import miku.ad.AdConstants.AdType.*
import miku.ad.AdViewBinder
import miku.ad.adapters.FuseAdLoader
import miku.ad.adapters.IAdAdapter
import video.report.mediaplayer.MyApplication
import video.report.mediaplayer.BuildConfig
import video.report.mediaplayer.R
import video.report.mediaplayer.billing.BillingManager
import video.report.mediaplayer.constant.Constants
import video.report.mediaplayer.constant.Constants.AD_SLOT_DOWNLOADLIST
import video.report.mediaplayer.constant.Constants.AD_SLOT_DOWNLOAD_INSTERSTITIAL
import video.report.mediaplayer.constant.Constants.AD_SLOT_HOMEPAGE
import video.report.mediaplayer.constant.Constants.FIVE_DAYS_TIME
import video.report.mediaplayer.constant.Constants.NOTIFICATION_JUMP_KEY
import video.report.mediaplayer.constant.Constants.TEN_DAYS_TIME
import video.report.mediaplayer.di.injector
import video.report.mediaplayer.exten.inflater
import video.report.mediaplayer.exten.toast
import video.report.mediaplayer.firebase.FireBaseEventUtils
import video.report.mediaplayer.firebase.Events
import video.report.mediaplayer.firebase.RemoteConfig
import video.report.mediaplayer.preference.UserPreferences
import video.report.mediaplayer.rxbus.RxBus
import video.report.mediaplayer.ui.fragment.FragmentDownload
import video.report.mediaplayer.ui.fragment.FragmentHome
import video.report.mediaplayer.util.DeviceUtils.getSystemLanguage
import video.report.mediaplayer.util.LanUtils.string2BigDecimal
import video.report.mediaplayer.util.NetworkUtils.isNetworkConnected
import java.math.BigDecimal
import java.util.*
import javax.inject.Inject


class MainNavActivity : BaseActivity(), FragmentDownload.InfoCallback, FragmentHome.InfoCallback, View.OnClickListener {

    internal var fragmentHome: FragmentHome? = null
    internal var fragmentDownload: FragmentDownload? = null

    private var isConfirmDialogShow: Boolean = false
    private var billingManager: BillingManager? = null

    // ad related
    @Inject
    lateinit var userPrefs: UserPreferences

    private var mIsAutoGoDownload: Boolean = false

    private var sp: SharedPreferences? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        try {
            injector.inject(this@MainNavActivity)
        } catch (e: Exception) {
            // android 8.1 inject error for castException
        }
        initBilling()
        setContentView(R.layout.activity_main)
        setSupportActionBar(toolbar)
        supportActionBar?.title = resources.getString(R.string.app_name)
        toolbar?.setTitleTextColor(Color.WHITE)

        ButterKnife.bind(this)
        sp = getSharedPreferences(this.packageName, Context.MODE_PRIVATE)
        initView()

        loadHomeAd()
        loadDownloadAd()

        initListener()
        if (BuildConfig.VERSION_NAME.contains('P', true))
            getFlavorChannel(this)

        billingManager?.checkUserBuyedState()

    }

    private fun initBilling() {
        billingManager = BillingManager(this@MainNavActivity)
    }

    override fun onDestroy() {
        super.onDestroy()
        billingManager?.onDestroy()
        RxBus.instance?.unSubscribe(this)
    }

    override fun onNewIntent(intent: Intent?) {
        super.onNewIntent(intent)
        if (intent?.getIntExtra(NOTIFICATION_JUMP_KEY, 0) == 1) {
            FireBaseEventUtils.getInstance().report(Events.NOTIFICATION_CLICK)
        }

        mIsAutoGoHome = true
    }

    @SuppressLint("RestrictedApi")
    override fun onCreateOptionsMenu(menu: Menu): Boolean {
        val inflater: MenuInflater = menuInflater
        inflater.inflate(R.menu.main, menu)
        val lang = getSystemLanguage()
        if ("ur" == lang || "ar" == lang || "fa" == lang) {
        } else {
            if (menu is MenuBuilder) {
                menu.setOptionalIconsVisible(true)
            }
        }
        return true

    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        return when (item.itemId) {
            else -> super.onOptionsItemSelected(item)
        }
    }

    private fun updateSelectToolbar() {
    }

    override fun onClick(p0: View?) {
        when (p0?.id) {
        }
    }

    override fun onBackPressed() {
        FireBaseEventUtils.getInstance().report(Events.AD_APPEXIT_COME)
        var ad: IAdAdapter? = null
        FireBaseEventUtils.getInstance().report(Events.AD_APPEXIT_AD_OPEN)
        if (isNetworkConnected()) {
            FireBaseEventUtils.getInstance().report(Events.AD_APPEXIT_WITH_NETWORK)
            val typeList = ArrayList<String>()
            typeList.add(AD_SOURCE_ADMOB_H)
            typeList.add(AD_SOURCE_ADMOB_M)
            typeList.add(AD_SOURCE_ADMOB)
            typeList.add(AD_SOURCE_MOPUB)
            ad = FuseAdLoader.getAllTopAdByScenes(this, typeList, AD_SLOT_DOWNLOADLIST, AD_SLOT_HOMEPAGE)
        } else {
            FireBaseEventUtils.getInstance().report(Events.AD_APPEXIT_WITH_NO_NETWORK)
        }
        when {
            ad == null -> super.onBackPressed()
            !isConfirmDialogShow -> {
                showExitConfirmDialog(ad)
                FireBaseEventUtils.getInstance().report(Events.AD_APPEXIT_ADSHOW)
                if (ad.adType == AD_SOURCE_ADMOB || ad.adType == AD_SOURCE_ADMOB_H || ad.adType == AD_SOURCE_ADMOB_M) {
                    FireBaseEventUtils.getInstance().report(Events.AD_APPEXIT_ADSHOW_ADMOB)
                } else if (ad.adType == AD_SOURCE_MOPUB) {
                    FireBaseEventUtils.getInstance().report(Events.AD_APPEXIT_ADSHOW_MOPUB)
                }
            }
        }
    }


    /**
     * Drawer关联Toolbar
     */
    private fun initActionBarDrawer() {


        val toggle = object : ActionBarDrawerToggle(
                this,
                drawer_layout,
                toolbar,
                R.string.ok,
                R.string.cancel
        ) {
            override fun onDrawerOpened(drawerView: View) {
                super.onDrawerOpened(drawerView)
                nav_view.menu.getItem(0).isVisible = !MyApplication.instance.isAdFree()
//                nav_view.menu.getItem(0).setVisible(true)
            }
        }

        drawer_layout.addDrawerListener(toggle)
        toggle.syncState()

        toolbar.setNavigationIcon(R.drawable.icon_meanu);
        nav_view.itemIconTintList = null;

    }

    private fun initListener() {

        /**
         * 侧边栏点击事件
         */
        nav_view.setNavigationItemSelectedListener {
            // Handle navigation view item clicks here.
            when (it.itemId) {
                R.id.nav_setting -> {
                    FireBaseEventUtils.getInstance().report(Events.HOME_MORE_SETTING_CLICK)
                    setting()
                }
            }

            //关闭侧边栏
            drawer_layout.closeDrawer(GravityCompat.START)

            true
        }
    }

    val DELAY = 1000
    var lastClickTime: Long = 0
    fun isNotFastClick(): Boolean {
        val currentTime = System.currentTimeMillis()
        if (currentTime - lastClickTime > DELAY) {
            lastClickTime = currentTime
            return true
        } else {
            return false
        }
    }

    interface BadgeInterface {
        fun showBadge()
    }

    private fun initView() {
        val fragmentManager = supportFragmentManager

        fragmentHome = fragmentManager.findFragmentByTag(TAG_FRAGMENT_HOME) as FragmentHome?
        fragmentDownload = fragmentManager.findFragmentByTag(TAG_FRAGMENT_DOWNLOAD) as FragmentDownload?

        if (fragmentHome == null) {
            fragmentHome = FragmentHome()
            fragmentManager.beginTransaction().add(R.id.content_frame, fragmentHome!!, TAG_FRAGMENT_HOME).commitAllowingStateLoss()
        }

        fragmentHome?.badgeInterface = object : BadgeInterface {
            override fun showBadge() {
                showBadgeView(1)
            }
        }

        if (fragmentDownload == null) {
            fragmentDownload = FragmentDownload()
            fragmentManager.beginTransaction().add(R.id.content_frame, fragmentDownload!!, TAG_FRAGMENT_DOWNLOAD).commitAllowingStateLoss()
        }

        fragmentManager.executePendingTransactions()
        mIsAutoGoHome = true


        bottom_navigation.setOnNavigationItemSelectedListener { menuItem ->
            when (menuItem.itemId) {
                R.id.menu_home -> {
                    if (!mIsAutoGoHome) {
                        if (fragmentHome?.isHidden == false) {
                        } else {
                            showInterstitail(COME_HOME)
                            FireBaseEventUtils.getInstance().report(Events.EVENT_HOME_TAB_CLICK)
                            FireBaseEventUtils.getInstance().reportNew(Events.EVENT_HOME_TAB_CLICK)
                        }
                    }
                    fragmentHome?.let { selectFragment(it, TAG_FRAGMENT_HOME) }
                    mIsAutoGoHome = false
                    //退出select 模式
                    fragmentDownload?.setDefaultCheckFragment()
                }

                R.id.menu_download -> {
                    if (!mIsAutoGoDownload) {
                        FireBaseEventUtils.getInstance().report(Events.EVENT_DOWNLOADS_TAB_CLICK)
                        FireBaseEventUtils.getInstance().reportNew(Events.EVENT_DOWNLOADS_TAB_CLICK)
                        FireBaseEventUtils.getInstance().report(Events.AD_CLICKDOWNLOAD_COME)
                        FireBaseEventUtils.getInstance().report(Events.AD_TAB_COME_DOWNLOADS)

                        showInterstitail(COME_DOWNLOAD)
                    } else {
                        fragmentDownload?.mIsAutoDownload = true
                    }
                    fragmentDownload?.let {
                        selectFragment(it, TAG_FRAGMENT_DOWNLOAD)
                    }
                }
            }
            true
        }
        bottom_navigation?.selectedItemId = R.id.menu_home
        initActionBarDrawer()
    }

    private fun hideAllFragments() {
        val fragmentManager = supportFragmentManager
        fragmentHome?.let {
            fragmentManager.beginTransaction().hide(it).commitAllowingStateLoss()
        }
        fragmentDownload?.let {
            fragmentManager.beginTransaction().hide(it).commitAllowingStateLoss()
        }
    }

    private fun getFlavorChannel(context: Context) {
        try {
            if (sp != null && !sp!!.getBoolean("first_open", false)) {
                val pm = context.packageManager
                val appInfo = pm.getApplicationInfo(context.packageName, PackageManager.GET_META_DATA)
                val values = Bundle()
                values.putString("channel", appInfo.metaData.getString("channel"))
                FireBaseEventUtils.getInstance().report("source_channel", values)
                sp!!.edit().putBoolean("first_open", true).apply()
            }
        } catch (ignored: PackageManager.NameNotFoundException) {
        }
    }

    private fun selectFragment(fragment: Fragment, tag: String) {
        hideAllFragments()
        val fragmentManager = supportFragmentManager
        fragmentManager
                .beginTransaction()
                .show(fragment)
                .commitAllowingStateLoss()
    }


    override fun showBillDialog() {
        showBillingDialog()
    }

    private fun showBillingDialog() {
        var originPrice = userPrefs.purchasePrice
        if (isNetworkConnected() && originPrice.isNotEmpty() && !userPrefs.alreadyPurchase) {
            var index = 0
            var isNumReverse = true
            if (Character.isDigit(originPrice[0])) {
                isNumReverse = false
            }
            for (i in originPrice.indices) {
                if (Character.isDigit(originPrice[i])) {
                    index = i
                    if (isNumReverse)
                        break
                }
            }
            try {
                var subs: String
                var monkey: String
                if (isNumReverse) {
                    subs = originPrice.substring(0, index)
                    monkey = originPrice.substring(index)
                    monkey = monkey.replace(",", ".")
                    originPrice = originPrice.replace(",", ".")
                } else {
                    subs = originPrice.substring(index + 1)
                    monkey = originPrice.substring(0, index + 1)
                    monkey = monkey.replace(",", ".")
                    originPrice = originPrice.replace(",", ".")
                }
                var finalStringency = (string2BigDecimal(monkey).multiply(BigDecimal(2))).toString()
//                var dialog = BillingGuideDialog(this)
//                dialog.showBillingDialog(originPrice, if (isNumReverse) subs + finalStringency else finalStringency + subs, true)
                FireBaseEventUtils.getInstance().report(Events.BILLING_PROMOTE_SHOW)
            } catch (e: Exception) {
            }
        }
    }

    private fun setting() {
        try {
            val intent = Intent(this, NewSettingsActivity::class.java)
            startActivity(intent)
        } catch (e: Exception) {
            e.printStackTrace()
        }
    }

    override fun attachBaseContext(newBase: Context?) {
        super.attachBaseContext(newBase)
        mContext = this
    }

    private fun showBadgeView(index: Int) {
        val bottomNavigationMenuView = bottom_navigation.getChildAt(0) as BottomNavigationMenuView
        val itemView = bottomNavigationMenuView.getChildAt(index) as BottomNavigationItemView
        if (itemView.findViewById<View>(R.id.badge_notification) == null)
            inflater.inflate(R.layout.badge_view, itemView, true)
    }

    override fun backHomePage() {
        try {
            selectFragment(fragmentHome!!, TAG_FRAGMENT_HOME)
            runOnUiThread {
                bottom_navigation.selectedItemId = R.id.menu_home
            }
        } catch (e: Exception) {
        }

    }

    private fun showExitConfirmDialog(ad: IAdAdapter) {
        if (ad == null) {
            finish()
            return
        }
        val customView = this.inflater.inflate(R.layout.dialog_exit, null, false)
        val adContainer = customView.findViewById<LinearLayout>(R.id.exitad)

        ad.let {
            if (!it.isExpired && isNetworkConnected()) {
                val viewBinder: AdViewBinder = AdViewBinder.Builder(R.layout.layout_exit_native_ad)
                        .titleId(R.id.ad_title)
                        .textId(R.id.ad_subtitle_text)
                        .mainMediaId(R.id.ad_cover_image)
                        .iconImageId(R.id.ad_icon_image)
                        .admMediaId(R.id.ad_adm_mediaview)
                        .callToActionId(R.id.ad_cta_text)
                        .privacyInformationId(R.id.ad_choices_container)
                        .adFlagId(R.id.ad_flag)
                        .build()
                val adView = it.getAdView(this, viewBinder)
                if (adView != null) {
                    adContainer?.apply {
                        removeAllViews()
                        addView(adView)
                        visibility = View.VISIBLE
                        FireBaseEventUtils.getInstance().reportAdShow(Constants.AD_APPEXIT)

                    }
                }
            } else {
                finish()
                return
            }
        }
        val clickView = customView.findViewById<TextView>(R.id.exit)
        clickView.setOnClickListener {
            finish()
        }
        try {
            val dialog = Dialog(this, R.style.DialogTheme)
            dialog.setContentView(customView)
            val window = dialog.window
            window!!.setGravity(Gravity.BOTTOM)
            window.setWindowAnimations(R.style.DialogAnimStyle)
            window.setLayout(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.WRAP_CONTENT)
            dialog.show()
            isConfirmDialogShow = true
            dialog.setOnDismissListener {
                isConfirmDialogShow = false
            }
            dialog.setOnKeyListener { _, keyCode, _ ->
                if (keyCode == KeyEvent.KEYCODE_BACK) {
                    finish()
                }
                true
            }
        } catch (e: Exception) {
        }
    }

    private fun loadDownloadAd() {
        FuseAdLoader.get(Constants.AD_SLOT_DOWNLOADLIST, this).preLoadAd(this)
    }

    private fun loadHomeAd() {
        FuseAdLoader.get(Constants.AD_SLOT_HOMEPAGE, this).preLoadAd(this)
    }

    private fun loadDownloadInsterAd() {
        FuseAdLoader.get(AD_SLOT_DOWNLOAD_INSTERSTITIAL, this).preLoadAd(this)
    }

    private fun loadVideoExitAd() {
        FuseAdLoader.get(Constants.AD_SLOT_VIDEOEXIT, this).preLoadAd(this)
    }

    private val mHandler = object : Handler(Looper.getMainLooper()) {
        override fun handleMessage(msg: Message) {
        }
    }

    override fun backDownloadPage() {
        try {
            selectFragment(fragmentDownload!!, TAG_FRAGMENT_DOWNLOAD)
            runOnUiThread {
                mIsAutoGoDownload = true
                bottom_navigation.selectedItemId = R.id.menu_download
            }
        } catch (e: Exception) {
        }
    }

    fun getHandler(): Handler {
        return mHandler
    }

    companion object {
        const val COME_HOME = 0
        const val COME_DOWNLOAD = 1

        private const val TAG_FRAGMENT_HOME = "HOME-FRAGMENT"
        private const val TAG_FRAGMENT_DOWNLOAD = "DOWNLOAD-FRAGMENT"

        @JvmStatic
        lateinit var mContext: MainNavActivity

        @JvmStatic
        var mIsAutoGoHome: Boolean = false
    }

    override fun onRequestPermissionsResult(requestCode: Int, permissions: Array<String>, grantResults: IntArray) {
        PermissionsManager.getInstance().notifyPermissionsChange(permissions, grantResults)
        super.onRequestPermissionsResult(requestCode, permissions, grantResults)
        if (requestCode == 10 && grantResults.isNotEmpty()) {
            if (grantResults[0] == PackageManager.PERMISSION_GRANTED) {
            } else {
                finish()
            }
        }
    }

    override fun onResume() {
        try {
            super.onResume()
            //检查读写权限
            if (ContextCompat.checkSelfPermission(this, Manifest.permission.WRITE_EXTERNAL_STORAGE) != PackageManager.PERMISSION_GRANTED) {
                ActivityCompat.requestPermissions(this, arrayOf(Manifest.permission.READ_EXTERNAL_STORAGE, Manifest.permission.WRITE_EXTERNAL_STORAGE), 10)
            }
        } catch (e: Exception) {
            //
        }

        FireBaseEventUtils.getInstance().report(Events.AD_DONWLOADINTERS_ONRESUME_COME_MEETRULE)
        if (isNetworkConnected()) {
            loadDownloadInsterAd()
            FireBaseEventUtils.getInstance().report(Events.AD_DONWLOADINTERS_ONRESUME_NOCACHE)
        }

    }

    fun showInterstitail(come: Int) {
        FireBaseEventUtils.getInstance().report(Events.AD_TAB_AD_OPEN)
        if (isNetworkConnected()) {
            FireBaseEventUtils.getInstance().report(Events.AD_TAB_WITH_NETWORK)
            FireBaseEventUtils.getInstance().report(Events.AD_TAB_MEETRULE)
            val typeList = ArrayList<String>()
            typeList.add(AdConstants.AdType.AD_SOURCE_MOPUB_INTERSTITIAL)
            typeList.add(AdConstants.AdType.AD_SOURCE_ADMOB_INTERSTITIAL_H)
            typeList.add(AdConstants.AdType.AD_SOURCE_ADMOB_INTERSTITIAL_M)
            typeList.add(AdConstants.AdType.AD_SOURCE_ADMOB_INTERSTITIAL)
            var ad = FuseAdLoader.getAllTopAdByScenes(this, typeList, Constants.AD_SLOT_DOWNLOAD_INSTERSTITIAL, Constants.AD_SLOT_VIDEOEXIT)
            if (ad != null) {
                ad.show()
                FireBaseEventUtils.getInstance().report(Events.AD_TAB_ADSHOW)
                if (ad.adType == AdConstants.AdType.AD_SOURCE_ADMOB_INTERSTITIAL
                        || ad.adType == AdConstants.AdType.AD_SOURCE_ADMOB_INTERSTITIAL_H
                        || ad.adType == AdConstants.AdType.AD_SOURCE_ADMOB_INTERSTITIAL_M) {
                    FireBaseEventUtils.getInstance().report(Events.AD_TAB_ADSHOW_ADMOB)
                } else if (ad.adType == AdConstants.AdType.AD_SOURCE_MOPUB_INTERSTITIAL) {
                    FireBaseEventUtils.getInstance().report(Events.AD_TAB_ADSHOW_MOPUB)
                }
            } else {
                mHandler?.postDelayed({
                    loadDownloadInsterAd()
                }, 1000)
                FireBaseEventUtils.getInstance().report(Events.AD_TAB_NOADS_REQUIREADS)
            }
        } else {
            FireBaseEventUtils.getInstance().report(Events.AD_TABAD_CLOSE)
        }
    }

    fun reStart(context: Context) {
        val intent = Intent(context, MainNavActivity::class.java)
        intent.flags = Intent.FLAG_ACTIVITY_CLEAR_TASK or Intent.FLAG_ACTIVITY_NEW_TASK
        context.startActivity(intent)
    }


    override fun updateToolBar() {
        updateSelectToolbar()
    }

    override fun updateCheckTitle() {
    }

    override fun updateCheckTitleInit() {
    }
}
