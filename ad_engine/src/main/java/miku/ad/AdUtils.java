package miku.ad;

import android.Manifest;
import android.content.Context;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.provider.Settings;
import android.telephony.TelephonyManager;
import android.text.TextUtils;
import android.text.format.Formatter;

import com.google.android.gms.ads.identifier.AdvertisingIdClient;

import java.net.InetAddress;
import java.net.NetworkInterface;
import java.net.SocketException;
import java.util.Enumeration;


public class AdUtils {

    private static IAdEventLogger sLogger;

    public static void setEventLogger(IAdEventLogger logger) {
        sLogger = logger;
    }

    public static void trackAdEvent(String slot, String event) {
        if (sLogger != null) {
            sLogger.trackEvent(slot, event);
        }
    }

    public static String MD5(String md5) {
        try {
            java.security.MessageDigest md = java.security.MessageDigest.getInstance("MD5");
            byte[] array = md.digest(md5.getBytes());
            StringBuffer sb = new StringBuffer();
            for (int i = 0; i < array.length; ++i) {
                sb.append(Integer.toHexString((array[i] & 0xFF) | 0x100).substring(1, 3));
            }
            return sb.toString();
        } catch (java.security.NoSuchAlgorithmException e) {
        }
        return null;
    }

    public static String getAndroidID(Context context) {
        return Settings.Secure.getString(context.getContentResolver(), Settings.Secure.ANDROID_ID);
    }

    private static void setGaid(Context var0, String var1) {
        SharedPreferences var2 = var0.getSharedPreferences("sdk_preference", 0);
        var2.edit().putString("gaid", var1).apply();
    }

    private static String getGaid(Context var0) {
        SharedPreferences var1 = var0.getSharedPreferences("sdk_preference", 0);
        return var1.getString("gaid", "");
    }

    public static final String getGoogleAdvertisingId(Context var0) {
        if (var0 == null) {
            return "";
        } else {
            String var1 = getGaid(var0);
            if (!TextUtils.isEmpty(var1)) {
                return var1;
            } else {
                try {
                    String var2 = AdvertisingIdClient.getAdvertisingIdInfo(var0).getId();
                    if (!TextUtils.isEmpty(var2)) {
                        setGaid(var0, var2);
                        return var2;
                    }
                } catch (Throwable var3) {
                    var3.printStackTrace();
                }

                return "";
            }
        }
    }

    public static boolean hasConnectedNetwork(Context context) {
        NetworkInfo info = getActiveNetworkInfo(context);
        return info != null && info.isConnected();
    }

    private static NetworkInfo getActiveNetworkInfo(Context context) {
        if (context == null) {
            return null;
        }
        ConnectivityManager cm = (ConnectivityManager) context
                .getSystemService(Context.CONNECTIVITY_SERVICE);
        if (cm != null)
            return cm.getActiveNetworkInfo();
        return null;
    }

    public static String getLocalIpAddress() {
        try {
            for (Enumeration<NetworkInterface> en = NetworkInterface.getNetworkInterfaces(); en.hasMoreElements(); ) {
                NetworkInterface intf = en.nextElement();
                for (Enumeration<InetAddress> enumIpAddr = intf.getInetAddresses(); enumIpAddr.hasMoreElements(); ) {
                    InetAddress inetAddress = enumIpAddr.nextElement();
                    if (!inetAddress.isLoopbackAddress()) {
                        String ip = Formatter.formatIpAddress(inetAddress.hashCode());
                        return ip;
                    }
                }
            }
        } catch (SocketException ex) {
        }
        return null;
    }
}
