package video.report.mediaplayer.di

import dagger.Component
import video.report.mediaplayer.MyApplication
import video.report.mediaplayer.billing.BillingManager
import video.report.mediaplayer.ui.activity.SelectPathActivity
import video.report.mediaplayer.ui.activity.NewSettingsActivity
import video.report.mediaplayer.ui.activity.DownloadPathActivity
import video.report.mediaplayer.ui.activity.*
import video.report.mediaplayer.ui.fragment.FragmentDownload
import video.report.mediaplayer.ui.fragment.FragmentHome
import javax.inject.Singleton


@Singleton
@Component(modules = [(AppModule::class), (AppBindsModule::class)])
interface AppComponent {
    fun inject(myApplication: MyApplication)
    fun inject(fragmentDownload: FragmentDownload)
    fun inject(fragmentHome: FragmentHome)
    fun inject(billingManager: BillingManager)
    fun inject(mainactivity: MainNavActivity)
    fun inject(selectPathActivity: SelectPathActivity)
    fun inject(downloadPathActivity: DownloadPathActivity)
    fun inject(vPlayer: VPlayerActivity)
    fun inject(postActivity: PostActivity)
    fun inject(newSettingsActivity: NewSettingsActivity)
}
