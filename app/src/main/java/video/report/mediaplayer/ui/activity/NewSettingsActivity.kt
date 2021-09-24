package video.report.mediaplayer.ui.activity

import android.Manifest
import android.content.Context
import android.content.Intent
import android.os.Bundle
import android.view.MenuItem
import android.view.View
import android.view.ViewGroup
import butterknife.ButterKnife
import com.afollestad.materialdialogs.MaterialDialog
import com.afollestad.materialdialogs.ModalDialog
import com.afollestad.materialdialogs.list.listItemsSingleChoice
import com.anthonycr.grant.PermissionsManager
import com.anthonycr.grant.PermissionsResultAction
import io.reactivex.Scheduler
import kotlinx.android.synthetic.main.activity_new_setting.*
import video.report.mediaplayer.MyApplication
import video.report.mediaplayer.BuildConfig
import video.report.mediaplayer.R
import video.report.mediaplayer.constant.Constants
import video.report.mediaplayer.di.DatabaseScheduler
import video.report.mediaplayer.di.MainScheduler
import video.report.mediaplayer.di.injector
import video.report.mediaplayer.exten.dimen
import video.report.mediaplayer.firebase.FireBaseEventUtils
import video.report.mediaplayer.firebase.Events
import video.report.mediaplayer.preference.UserPreferences
import video.report.mediaplayer.util.DeviceUtils
import video.report.mediaplayer.util.LanUtils
import javax.inject.Inject

class NewSettingsActivity : BaseActivity(), View.OnClickListener {
    @Inject
    lateinit var userPrefs: UserPreferences
    @Inject
    internal lateinit var userPreferences: UserPreferences

    @Inject
    @field:DatabaseScheduler
    internal lateinit var databaseScheduler: Scheduler
    @Inject
    @field:MainScheduler
    internal lateinit var mainScheduler: Scheduler

    var newSettingsActivity:NewSettingsActivity?=null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        injector.inject(this)
        setContentView(R.layout.activity_new_setting)
        ButterKnife.bind(this)
        newSettingsActivity=this
        toolbar.setNavigationOnClickListener(this)
        initView()
        FireBaseEventUtils.getInstance().report(Events.SETTING_SHOW)
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
//        startActivity(Intent(this, MainNavActivity::class.java))
        finish()
        return true
    }

    override fun onRequestPermissionsResult(requestCode: Int, permissions: Array<String>, grantResults: IntArray) {
        PermissionsManager.getInstance().notifyPermissionsChange(permissions, grantResults)
        super.onRequestPermissionsResult(requestCode, permissions, grantResults)
    }

    private fun initView() {
        language_layout.setOnClickListener(this)
        storage_layout.setOnClickListener(this)

        storage_tv2.text=userPreferences.downloadDirectory
        version_tv2.text=BuildConfig.VERSION_NAME

    }

    override fun onClick(v: View?) {
        when (v?.id) {
            -1 -> {
                finish()
            }
            R.id.language_layout -> {
                setLanguage()
            }
            R.id.storage_layout -> {
                showDownloadLocationDialog(this)
            }
        }
    }

    private fun setLanguage() {
        FireBaseEventUtils.getInstance().report(Events.SETTING_LANGUAGE_CLICK)
        val defaultIndex = LanUtils.getInstance(MyApplication.instance).selectLanguage
        var isChoose = false
        if (this.isFinishing||this==null){
            return
        }
        val dialog = MaterialDialog(this!!, ModalDialog).show {
            title(R.string.setting_language_title)
            listItemsSingleChoice(
                    res = R.array.language_options,
                    initialSelection = defaultIndex
            ) { _, which, _ ->
                LanUtils.getInstance(MyApplication.instance).saveLanguage(which)
                if (defaultIndex != which) {
                    try {
                        if (which == 0) {
                            // update index
                            LanUtils.setLocal(MyApplication.instance, MyApplication.default_laguage)
                            LanUtils.setApplicationLanguage(MyApplication.instance, MyApplication.default_laguage)
                            MainNavActivity.mContext.reStart(MyApplication.instance)
                        } else {
                            var locale = Constants.LANGUAGE[which]
                            if (locale != null) {
                                LanUtils.setLocal(MyApplication.instance, locale)
                                LanUtils.setApplicationLanguage(MyApplication.instance, locale)
                                MainNavActivity.mContext.reStart(MyApplication.instance)
                            }
                        }
                    } catch (e: java.lang.Exception) {
                    }
                    isChoose = true
                    FireBaseEventUtils.getInstance().report(Events.SETTING_LANGUAGE_SELECT)
                }
            }
            positiveButton(R.string.setting_language_choose)
            setOnDismissListener {
                if (!isChoose) {
                    FireBaseEventUtils.getInstance().report(Events.SETTING_LANGUAGE_CANCEL)
                }
            }
        }
        val window = dialog?.window
//        window?.setBackgroundDrawableResource(R.drawable.shape_dialog_radiu_8dp)
        val padding = MainNavActivity.mContext.dimen(R.dimen.dialog_padding)
        val screenSize = DeviceUtils.getScreenWidth(MainNavActivity.mContext)
        var  maxWidth = screenSize - 2 * padding
        window?.setLayout(maxWidth, ViewGroup.LayoutParams.WRAP_CONTENT)

    }

    private fun showDownloadLocationDialog(context: Context) {
        FireBaseEventUtils.getInstance().report(Events.SETTING_DOWNLOAD_LOCATION_CLICK)
        PermissionsManager.getInstance().requestPermissionsIfNecessaryForResult(this,
                arrayOf(Manifest.permission.READ_EXTERNAL_STORAGE, Manifest.permission.WRITE_EXTERNAL_STORAGE),
                object : PermissionsResultAction() {
                    override fun onGranted() {
                        val intent = Intent(context, DownloadPathActivity::class.java)
                       startActivity(intent)
                    }

                    override fun onDenied(permission: String?) {
                    }

                })
    }

    override fun onResume() {
        super.onResume()
        storage_tv2.text=userPreferences.downloadDirectory
        language_tv2.text=resources.getStringArray(R.array.language_options)[LanUtils.getInstance(MyApplication.instance).selectLanguage]

    }
}
