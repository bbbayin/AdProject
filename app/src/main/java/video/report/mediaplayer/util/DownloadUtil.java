package video.report.mediaplayer.util;

import android.content.Context;
import android.os.StatFs;
import android.provider.MediaStore;
import android.text.TextUtils;
import android.webkit.MimeTypeMap;

import org.apache.http.conn.ssl.SSLSocketFactory;

import java.io.File;
import java.io.IOException;
import java.net.Socket;
import java.net.UnknownHostException;
import java.security.KeyManagementException;
import java.security.KeyStore;
import java.security.KeyStoreException;
import java.security.NoSuchAlgorithmException;
import java.security.UnrecoverableKeyException;
import java.security.cert.CertificateException;
import java.security.cert.X509Certificate;
import java.util.List;

import javax.net.ssl.SSLContext;
import javax.net.ssl.TrustManager;
import javax.net.ssl.X509TrustManager;

import video.report.mediaplayer.MyApplication;


//=============================================================================
//                                 CLASS DEFINITIONS
//=============================================================================

/**
 * @version 1.0.0.0
 * @class DownloadUtil.java
 * @brief download utils. \n
 * @date 2012-12-20
 * @par Applied: External
 * @since 1.0.0.0
 */
public class DownloadUtil {
    private static final String TAG = "DownloadUtil";

    /* 常用单位*/
    public final static long KB = 1024;
    public final static long MB = KB * 1024;
    public final static long GB = MB * 1024;
    /** 剩余空间低于此值时，进行警告 */
//    public static final long DEFAULT_MIN_SPACE_WARNING = 1024 * MB;
    /**
     * 剩余空间低于此值时，强制终止下载
     */
    public static final long DEFAULT_MIN_SPACE_ERROR = 40 * MB;

    /**
     * 小于此大小时,就算支持分段下载也只启动一个线程
     */
    public static final long DEFAULT_SINGLE_THREAD_THRESHOLD = 500 * KB;
    /**
     * 默认UA
     */
    private static String mDefaultUA = String
            .format("Mozilla/5.0 (Linux; Android %s; %s Build/%s) AppleWebKit/535.19 (KHTML, like Gecko) Chrome/18.0.1025.166 Mobile Safari/535.19",
                    android.os.Build.VERSION.RELEASE, android.os.Build.MODEL, android.os.Build.ID);
    ;
    static SSLSocketFactory sSSLSocketFactoryInstance = null;


    public static enum SortDateType // 日期排序类型
    {
        CREATE_TIME, // 根据创建时间排序
        FINISH_TIME // 根据完成时间排序
    }

    ;

    public static boolean nullOrEmptyString(String string) {
        if (string == null || string.trim().length() == 0)
            return true;

        return false;
    }

//    public static void requestMediaScan(Context context, String filePath) {
//        if (filePath == null)
//            return;
//        File file = new File(filePath);
//        // TODO: implement
////        LocalImageManager.addFile(context, file, LocalImageManager.REQUEST_SCAN_FROM_OTHER);
//
//        Uri uri = Uri.parse("file://" + filePath);
//
//        if (context != null) {
//            context.sendBroadcast(new Intent(
//                    Intent.ACTION_MEDIA_SCANNER_SCAN_FILE, uri));
//        }
//
//        // TODO: implement
////        LocalImageManager.getInstance().scanPrivateImages(LocalImageManager.REQUEST_SCAN_FROM_OTHER);
//
//        RxBus.getInstance().postEntity(new Event(Event.DOWNLOAD_COMPLETE, FileUtil.getFileType(filePath)));
//    }

    public static int requestMediaRemoval(String filePath, String mimeType) {
        try {
            int deletecount = 0;

            if (nullOrEmptyString(mimeType))
                return deletecount;

            Context context = MyApplication.getInstance();

            String[] selectionArgs = new String[]{
                    filePath
            };

            if (mimeType.toLowerCase(java.util.Locale.US).startsWith("audio")) {
                String selection = MediaStore.Audio.Media.DATA + " = ?";
                deletecount = context.getContentResolver().delete(
                        MediaStore.Audio.Media.EXTERNAL_CONTENT_URI, selection,
                        selectionArgs);
            } else if (mimeType.toLowerCase(java.util.Locale.US).startsWith("video")) {
                String selection = MediaStore.Video.Media.DATA + " = ?";
                deletecount = context.getContentResolver().delete(
                        MediaStore.Video.Media.EXTERNAL_CONTENT_URI, selection,
                        selectionArgs);
            } else if (mimeType.toLowerCase(java.util.Locale.US).startsWith("image")) {
                String selection = MediaStore.Images.Media.DATA + " = ?";
                deletecount = context.getContentResolver().delete(
                        MediaStore.Images.Media.EXTERNAL_CONTENT_URI, selection,
                        selectionArgs);
            }

            return deletecount;
        } catch (Exception e) {
            e.printStackTrace();
            return -1;
        }
    }

    public static int requestMediaRemoval(String filePath) {
        if (nullOrEmptyString(filePath))
            return 0;

        int start = filePath.lastIndexOf("");
        if (start != -1 && filePath.length() > start + 1) {
            String extension = filePath.substring(start + 1);
            String guessMimeType = MimeTypeMap.getSingleton()
                    .getMimeTypeFromExtension(extension);
            return requestMediaRemoval(filePath, guessMimeType);
        } else {
            return 0;
        }
    }

    /**
     * 获取内外置SD卡路径
     */
    public static String getSDCardPath(Context context, boolean isInternal) {
        String ret = "";

        List<StorageUtils.SDCard> list = StorageUtils.getAllSdcardState(context);
        for (StorageUtils.SDCard sd : list) {
            if (sd != null && sd.isMount()) {
                if (isInternal) {
                    // 查找内置卡
                    if (!sd.isRemoveable()) {
                        ret = sd.getPath();
                    }
                } else {
                    // 查找外置卡
                    if (sd.isRemoveable()) {
                        ret = sd.getPath();
                    }
                }
            }
        }

        return ret;
    }

    /**
     * 获取隐私文件存储路径
     */
    public static String getPrivateDownloadFilePath() {
        String path = MyApplication.getInstance().getPreference().getDownloadDirectory();

//        // for gallery multi images download, need mkdir one new folder
//        if (true) { //SettingsModel.getInstance().getCanResetDownloadToastShow() > 1) {
//            String title = null;
//            String folder;
//            title = BrowserActivity.getMContext().getTabModel().getCurrentTab().getTitle();
//
//            if (TextUtils.isEmpty(title)) {
//                SimpleDateFormat simpleDateFormat = new SimpleDateFormat("yyyyMMdd_HH_mm_ss");
//                folder = simpleDateFormat.format(new Date());
//            } else {
//                folder = title;
//            }
//            path = path + "/" + folder;
//        }

        return path;
    }

    /**
     * 获取隐私视频缩略图存储路径，隐私图片不会扫描该路径
     */
    public static String getPrivateVideoThumbPath() {
        String thumbPath = null;
//        thumbPath = PathManager.getParentFolder(getPrivateDownloadFilePath()) +
//                File.separator + Constants.PRIVATE_VIDEO_THUMB_FOLDER_NEW;
        return thumbPath;
    }


    /**
     * 读取SD卡可用空间大小
     *
     * @param path_s 会读取此路径所在的SD卡空间大小
     * @return
     */
    public static long getAvailableSize(String path_s) {
        long ret = 0;

        if (!TextUtils.isEmpty(path_s)) {
            boolean mount = PathResolver.checkSDCardMountByFile(
                    MyApplication.getInstance(), path_s);
            if (mount) {
                try {
                    File path = new File(path_s);
                    StatFs stat = new StatFs(path.getPath());
                    long blockSize = stat.getBlockSize();
                    long availableBlocks = stat.getAvailableBlocks();
                    ret = availableBlocks * blockSize;
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
        }

        return ret;
    }


    /**
     * 读取SD卡总空间大小
     *
     * @param path_s 会读取此路径所在的SD卡空间大小
     * @return
     */
    public static long getTotalSize(String path_s) {
        long ret = 0;

        if (!TextUtils.isEmpty(path_s)) {
            boolean mount = PathResolver.checkSDCardMountByFile(
                    MyApplication.getInstance(), path_s);
            if (mount) {
                try {
                    File path = new File(path_s);
                    StatFs stat = new StatFs(path.getPath());
                    long blockSize = stat.getBlockSize();
                    long totalBlocks = stat.getBlockCount();
                    ret = totalBlocks * blockSize;
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
        }

        return ret;
    }


    /**
     * 读取SD卡已用空间百分比
     *
     * @param path_s 会读取此路径所在的SD卡空间百分比
     */
    public static int getUsedSizePer(String path_s) {
        int per = 0;

        if (!TextUtils.isEmpty(path_s)) {
            boolean mount = PathResolver.checkSDCardMountByFile(
                    MyApplication.getInstance(), path_s);
            if (mount) {
                try {
                    File path = new File(path_s);
                    StatFs stat = new StatFs(path.getPath());
                    long availableBlocks = stat.getAvailableBlocks();
                    long totalBlocks = stat.getBlockCount();
                    long usedBloacks = totalBlocks - availableBlocks;
                    if (usedBloacks > 0 && totalBlocks > 0) {
                        per = (int) ((usedBloacks * 100) / totalBlocks);
                    }
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
        }

        return per;
    }

    /**
     * url与对应的UserAgent列表
     */
    private static final String[][] UserAgentList = {
            // url, User-Agent
            {
                    ".baidu.com", "AndroidDownloadManager"
            }
    };

    /**
     * 根据url读取特殊的UserAgent
     *
     * @param url
     * @return 若找到对应的url, 返回UserAgent, 否则返回null
     */
    public static String getUserAgentByUrl(String url) {
        String userAgent = mDefaultUA;

        if (!TextUtils.isEmpty(url)) {
            for (int i = 0; i < UserAgentList.length; i++) {
                if (url.indexOf(UserAgentList[i][0]) > 0) {
                    userAgent = UserAgentList[i][1];
                    break; // 适配第一个符合的url
                }
            }
        }

        // KLog.debug(TAG, "getUserAgentByUrl, url : " + url + ", agent :" +
        // userAgent);

        return userAgent;
    }


    /**
     * 根据文件大小显示相应的字符串
     */
    public static String showSize(long oriBytes) {
        if (oriBytes < KB) // Byte
        {
            return String.format(java.util.Locale.getDefault(), "%dB", oriBytes);
        } else if (oriBytes < MB) // Kb
        {
            long kb = oriBytes / KB;
            return String.format(java.util.Locale.getDefault(), "%dKB", kb);
        } else if (oriBytes < GB) // Mb
        {
            double mb = oriBytes / (double) MB;
            return String.format(java.util.Locale.getDefault(), "%.1fMB", mb);
        } else // Gb
        {
            double gb = oriBytes / (double) GB;
            return String.format(java.util.Locale.getDefault(), "%.1fGB", gb);
        }
    }

//
//    /**
//     * 打印返回的http header,原来在DownloadTask里
//     */
//    public static void dumpHeaders(HttpMessage message) {
//        if (message == null) {
//            return;
//        }
//
//        HeaderIterator i = message.headerIterator();
//        while (i.hasNext()) {
//            Header header = i.nextHeader();
//        }
//    }


    /**
     * Default connection and socket timeout of 60 seconds. Tweak to taste.
     */
    private static final int SOCKET_OPERATION_TIMEOUT = 60 * 1000;


    private synchronized static SSLSocketFactory getSSLSocketFactory() {
        if (null != sSSLSocketFactoryInstance)
            return sSSLSocketFactoryInstance;

        KeyStore trustStore = null;
        try {
            trustStore = KeyStore.getInstance(KeyStore.getDefaultType());
            trustStore.load(null, null);

            sSSLSocketFactoryInstance = new MySSLSocketFactory(trustStore);
            sSSLSocketFactoryInstance.setHostnameVerifier(
                    SSLSocketFactory.STRICT_HOSTNAME_VERIFIER);
        } catch (KeyStoreException e) {
            e.printStackTrace();
        } catch (NoSuchAlgorithmException e) {
            e.printStackTrace();
        } catch (CertificateException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        } catch (KeyManagementException e) {
            e.printStackTrace();
        } catch (UnrecoverableKeyException e) {
            e.printStackTrace();
        }

        return sSSLSocketFactoryInstance;
    }

    private static class MySSLSocketFactory extends SSLSocketFactory {
        SSLContext sslContext = SSLContext.getInstance("TLS");

        public MySSLSocketFactory(KeyStore truststore) throws NoSuchAlgorithmException,
                KeyManagementException, KeyStoreException, UnrecoverableKeyException {
            super(truststore);

            TrustManager tm = new X509TrustManager() {
                @Override
                public void checkClientTrusted(X509Certificate[] chain, String authType)
                        throws CertificateException {
                }

                @Override
                public void checkServerTrusted(X509Certificate[] chain, String authType)
                        throws CertificateException {
                }

                @Override
                public X509Certificate[] getAcceptedIssuers() {
                    return null;
                }
            };

            sslContext.init(null, new TrustManager[]{
                    tm
            }, null);
        }

        @Override
        public Socket createSocket(Socket socket, String host, int port, boolean autoClose)
                throws IOException, UnknownHostException {
            return sslContext.getSocketFactory().createSocket(socket, host, port, autoClose);
        }

        @Override
        public Socket createSocket() throws IOException {
            return sslContext.getSocketFactory().createSocket();
        }
    }


    public static enum NetworkType {
        NETWORK_UNDEFINE,       //未定义
        NETWORK_NONE,           //无网络
        NETWORK_WIFI,           //Wifi,以及其他不收流量费的网络
        NETWORK_MOBILE,         //2G/3G,以及其他收流量费的网络
        NETWORK_ETHERNET,       //有线网络
    }

    ;

    public static enum MoibleNetWorkType {
        /**
         * 未知
         */
        MOIBLE_UNKNOW,
        MOIBLE_2G,
        MOIBLE_3G,
        MOIBLE_4G;
    }

    public static class NetworkState {
        private NetworkType networkType = NetworkType.NETWORK_UNDEFINE;
        private MoibleNetWorkType moibleNetWorkType = MoibleNetWorkType.MOIBLE_UNKNOW;
        private boolean isNetworkAvailable = false;

        public NetworkType getNetworkType() {
            return networkType;
        }

        public void setNetworkType(NetworkType networkType) {
            this.networkType = networkType;
        }

        public boolean isNetworkAvailable() {
            return isNetworkAvailable;
        }

        public void setNetworkAvailable(boolean isNetworkAvailable) {
            this.isNetworkAvailable = isNetworkAvailable;
        }

        public MoibleNetWorkType getMoibleNetWorkType() {
            return moibleNetWorkType;
        }

        public void setMoibleNetWorkType(MoibleNetWorkType moibleNetWorkType) {
            this.moibleNetWorkType = moibleNetWorkType;
        }

    }


}
