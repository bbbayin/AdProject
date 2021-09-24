package video.report.mediaplayer.util;

import android.content.Context;
import android.os.Build;
import android.os.Build.VERSION;
import android.os.Environment;
import android.os.storage.StorageManager;
import android.os.storage.StorageVolume;

import java.io.File;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.util.ArrayList;
import java.util.List;

import video.report.mediaplayer.R;

public class StorageUtils {

    // -------------------------------------------------------------------------
    /**
     * @brief Get android sdk version of current mobile.
     * @par Sync (or) Async: This is a Synchronous function.
     * @return sdk version in integer. \n
     * @par Prospective Clients: External Classes
     */
    public static int getSdkVersion() {
        return VERSION.SDK_INT;
    }

    /**
     * 由于线程竞争或进程竞争， 某些版本的android系统上 context.getFilesDir() 创建目录会失败，并返回null，
     * 为解决该问题， 当发现返回值为null时，try again。
     * 
     * @param context
     * @return files directory
     */
    public static File getFilesDir(Context context) {
        File filesDir = context.getFilesDir();
        return filesDir != null ? filesDir : context.getFilesDir();
    }

    /**
     * 一张SD卡的相关信息
     * 
     */
    public static class SDCard {
        private int index;
        private String path;
        private boolean mount;
        private boolean removeable;

        public SDCard(int index, String path, boolean mount, boolean removeable) {
            this.index = index;
            this.path = path;
            this.mount = mount;
            this.removeable = removeable;
        }

        /**
         * 获取SD卡的名字
         */
        public String getName(Context context) {
            String ret = "";

            if (context != null) {
                if (!removeable) {
                    // 手机存储
                    if (index > 1) {
                        ret = String.format("%s%s",
                                context.getString(R.string.s_download_innerSDCard), index);
                    } else {
                        ret = context.getString(R.string.s_download_innerSDCard);
                    }
                } else {
                    // SD卡
                    if (index > 1) {
                        ret = String.format("%s%s",
                                context.getString(R.string.s_download_outerSDCard), index);
                    } else {
                        ret = context.getString(R.string.s_download_outerSDCard);
                    }
                }
            }

            return ret;
        }

        /**
         * @return the path
         */
        public String getPath() {
            return path;
        }

        /**
         * @return the mount
         */
        public boolean isMount() {
            return mount;
        }

        /**
         * @return the removeable
         */
        public boolean isRemoveable() {
            return removeable;
        }
    }

    private static String getVolumePath(StorageVolume volume) {
        try {
            Method method = volume.getClass().getMethod("getPath");
            if (method != null)
                return (String) method.invoke(volume);
        } catch (NoSuchMethodException e) {
            e.printStackTrace();
        } catch (IllegalAccessException e) {
            e.printStackTrace();
        } catch (InvocationTargetException e) {
            e.printStackTrace();
        }
        return null;
    }

    /**
     * 读取所有SD卡的状态
     * 
     * @param context
     * @return
     */
    public static List<SDCard> getAllSdcardState(Context context) {
        List<SDCard> ret = new ArrayList<SDCard>();
        if (context != null) {
            // TODO support sdk version < 24
            if (getSdkVersion() >= Build.VERSION_CODES.N) {
                StorageManager mStorageManager = null;
                mStorageManager = (StorageManager) context
                        .getSystemService(Context.STORAGE_SERVICE);
                List<StorageVolume> storagePathList = mStorageManager.getStorageVolumes();

                int innerSDCardIndex = 1;
                int outerSDCardIndex = 1;
                if (storagePathList != null) {
                    for (StorageVolume volume : storagePathList) {
                        String path = getVolumePath(volume);
                        boolean mount = checkSDCardMount(context, path);
                        boolean isRemovable = volume.isRemovable();
                        int index = 0;
                        if (!isRemovable) {
                            // 手机存储
                            index = innerSDCardIndex;
                            innerSDCardIndex++;
                        } else {
                            // SD卡
                            index = outerSDCardIndex;
                            outerSDCardIndex++;
                        }
                        SDCard state = new SDCard(index, path, mount, isRemovable);
                        ret.add(state);
                    }
                }
            } else {
                // for lower than android 4.0 , still using /mnt/sdcard
                SDCard state = new SDCard(0, "/mnt/sdcard/", true, false);
                ret.add(state);
            }
        }
        return ret;
    }

    private static String getVolumeState(StorageManager storageManager, String mountPoint) {
        try {
            Method method = storageManager.getClass().getMethod("getVolumeState", String.class);
            if (method != null)
                return (String) method.invoke(storageManager, mountPoint);
        } catch (NoSuchMethodException e) {
            e.printStackTrace();
        } catch (IllegalAccessException e) {
            e.printStackTrace();
        } catch (InvocationTargetException e) {
            e.printStackTrace();
        }
        return "";
    }

    // -------------------------------------------------------------------------
    /**
     * @brief Check sdcard whether mounted.
     * @par Sync (or) Async: This is a Synchronous function.
     * @return true if sdcard been mounted \n
     * @par Prospective Clients: External Classes
     */
    public static boolean checkSDCardMount(Context context, String mountPoint) {
        if (mountPoint == null) {
            return false;
        }

        if (getSdkVersion() >= 14) {
            String state = null;
            StorageManager mStorageManager = null;
            mStorageManager = (StorageManager) context.getSystemService(Context.STORAGE_SERVICE);
            try {
                state = getVolumeState(mStorageManager, mountPoint); // mStorageManager.getVolumeState(mountPoint);
            } catch (IllegalArgumentException e) {
                return false;
            }
            return Environment.MEDIA_MOUNTED.equals(state);
        } else {
            return Environment.getExternalStorageState().equals(Environment.MEDIA_MOUNTED);
        }
    }

    public static String getDisplayName(String path) {
        if (path != null) {
            File f = new File(path);
            return f.getName();
        }
        return "";
    }
}
