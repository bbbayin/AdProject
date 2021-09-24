package video.report.mediaplayer.ui.base

import  android.content.Context

interface HomeContact {

    interface View {
        fun getViewContext(): Context

        fun goHomeFragment()

        fun goDownloadFragment()

    }
}
