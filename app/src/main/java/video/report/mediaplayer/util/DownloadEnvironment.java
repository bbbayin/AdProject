package video.report.mediaplayer.util;

import android.content.Context;
import android.os.Environment;

import java.io.File;

import video.report.mediaplayer.MyApplication;


public class DownloadEnvironment {
    public static final int ANDROID_4_4_2 = 19; // first 4.4 API

    /**
     * 系统的SDK版本号
     *
     * @return
     */
    public static int getSdkVersion() {
        return android.os.Build.VERSION.SDK_INT;
    }

    /**
     * 获得app的context,其他模块不应使用这里的context。从KBrowserEngine获取，未必生命周期
     * 一致。
     *
     * @hide
     */
    public static Context getContext() {
        return MyApplication.instance;
    }

    /**
     * 用于展现ui时的context
     *
     * @hide
     */
    public static Context getUiContext() {
        return MyApplication.instance;
    }

    /**
     * 获取Interal Storage上的默认存储目录，app私有目录，卸载时存在这里的文件会被删除
     *
     * @return
     */
    public static String getInternalStorageSaveDir() {
        File filesDirFile = StorageUtils.getFilesDir(getContext());
        File downloadDirFile = new File(filesDirFile,
                Environment.DIRECTORY_DOWNLOADS);
        return downloadDirFile.getAbsolutePath();
    }

    /**
     * 获取External Storage上的公开存储目录，卸载时不会删除
     *
     * @return
     */
    public static String getExternalStoragePublicSaveDir() {
        //File filesDirFile = Environment.getExternalStoragePublicDirectory(
        //        Environment.DIRECTORY_DOWNLOADS);
        //return filesDirFile.getAbsolutePath();

        String path = DownloadUtil.getPrivateDownloadFilePath();
        File filesDirFile = new File(path);
//        if (!filesDirFile.exists()) {
//            new File(path + "/.nomedia").mkdirs();
//        }

        return filesDirFile.getAbsolutePath();
    }
}
