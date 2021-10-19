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


    private static boolean isSameDate(long a, long b){
        return a/(24*60*60*1000L) == b/ (24*60*60*1000L);
    }

    public static boolean checkTimes(Context context,String type){
        long last = getCurrentDate(context);
        if(isSameDate(last,System.currentTimeMillis())){
            switch (type){
                case "applovin":
                    int num = getApplovinClickNum(context);
                    if(num>=5){
                        return false;
                    }else{
                        return true;
                    }
                case "adcolony":
                    int num2 = getAdcolonyClickNum(context);
                    if(num2>=5){
                        return false;
                    }else{
                        return true;
                    }
                case "vungle":
                    int num3 = getVungleClickNum(context);
                    if(num3>=5){
                        return false;
                    }else{
                        return true;
                    }
                default:
                    return true;

            }

        }else{
            setCurrentDate(context);

        }

        return false;
    }

    private static long getCurrentDate(Context context){
        SharedPreferences var = context.getSharedPreferences("sdk_preference", 0);
        return var.getLong("currentDate", 0L);
    }

    private static void setCurrentDate(Context context ){
        SharedPreferences var = context.getSharedPreferences("sdk_preference", 0);
        var.edit().putLong("currentDate", System.currentTimeMillis()).commit();
    }

    public static String getAndroidID(Context context) {
        return Settings.Secure.getString(context.getContentResolver(), Settings.Secure.ANDROID_ID);
    }

    private static int getApplovinClickNum(Context var0){
        SharedPreferences var = var0.getSharedPreferences("sdk_preference", 0);
        return var.getInt("ApplovinClickNum", 0);
    }
    private static int getAdcolonyClickNum(Context var0){
        SharedPreferences var = var0.getSharedPreferences("sdk_preference", 0);
        return var.getInt("AdcolonyClickNum", 0);
    }
    private static int getVungleClickNum(Context var0){
        SharedPreferences var = var0.getSharedPreferences("sdk_preference", 0);
        return var.getInt("VungleClickNum", 0);
    }

    public static void setApplovinClickNum(Context context ){
        SharedPreferences var = context.getSharedPreferences("sdk_preference", 0);
        int  num = var.getInt("ApplovinClickNum", 0);
        var.edit().putInt("ApplovinClickNum", num+1).commit();
    }
    public static void setAdcolonyClickNum(Context context ){
        SharedPreferences var = context.getSharedPreferences("sdk_preference", 0);
        int  num = var.getInt("AdcolonyClickNum", 0);
        var.edit().putInt("AdcolonyClickNum", num+1).commit();
    }
    public static void setVungleClickNum(Context context ){
        SharedPreferences var = context.getSharedPreferences("sdk_preference", 0);
        int  num = var.getInt("VungleClickNum",0);
        var.edit().putInt("VungleClickNum", num+1).commit();
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
