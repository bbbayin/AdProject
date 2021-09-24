package miku.utils;

import android.annotation.SuppressLint;
import android.content.Context;
import android.content.Intent;
import android.net.Uri;

import androidx.annotation.NonNull;

import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;

/**
 * Created by goldze on 2017/5/14.
 * 常用工具类
 */
public final class Utils {

    public static final String FORMAT_YEAR = "yyyy-MM-dd";
    public static final String FORMAT_YEAR_CHINESE = "yyyy年MM月dd日";
    public static final String FORMAT_MONTH = "MM-dd";
    public static final String FORMAT_MONTH_CHINESE = "MM月dd日";
    public static final String FORMAT_DAY_TIME = "MM-dd HH:mm";
    public static final String FORMAT_TIME = "HH:mm";
    public static final String FORMAT_TIME_CHINESE = "hh:mm";
    public static final String FORMAT_FULL = "yyyy-MM-dd HH:mm:ss";
    public static int SECONDS_PER_MINUTE = 60;
    public static int SECONDS_PER_HOUR = SECONDS_PER_MINUTE * 60;
    public static int SECONDS_PER_DAY = SECONDS_PER_HOUR * 24;
    public static int TIME_5_MINS = SECONDS_PER_MINUTE * 1000 * 5;

    @SuppressLint("StaticFieldLeak")
    private static Context context;

    private Utils() {
        throw new UnsupportedOperationException("u can't instantiate me...");
    }

    /**
     * 初始化工具类
     *
     * @param context 上下文
     */
    public static void init(@NonNull final Context context) {
        Utils.context = context.getApplicationContext();
    }

    /**
     * 获取ApplicationContext
     *
     * @return ApplicationContext
     */
    public static Context getContext() {
        if (context != null) {
            return context;
        }
        throw new NullPointerException("should be initialized in application");
    }

    public static String getString(int id) {
        return context.getString(id);
    }

    public static void goWeb(String url) {
        try {
            Intent intent = new Intent();
            intent.setAction(Intent.ACTION_VIEW);
            Uri content_url = Uri.parse(url);//此处填链接
            intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
            intent.setData(content_url);
            context.startActivity(intent);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public static void callPhone(String phoneNum) {
        try {
            Intent intent = new Intent(Intent.ACTION_DIAL);
            Uri data = Uri.parse("tel:" + phoneNum);
            intent.setData(data);
            intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
            context.startActivity(intent);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public static String getYearChinese(long time) {
        SimpleDateFormat format = new SimpleDateFormat();
        format.applyPattern(FORMAT_YEAR_CHINESE);
        return format.format(time);
    }

    public static String getDateTime(long time) {
        Calendar pre = Calendar.getInstance();
        Date predate = new Date(time);
        pre.setTime(predate);

        String timeOfDay;
        int hour = pre.get(Calendar.HOUR_OF_DAY);
        if (hour >= 0 && hour < 6) {
            timeOfDay = "凌晨" + hour + "时";
        } else if (hour >= 6 && hour < 12) {
            timeOfDay = "上午" + hour + "时";
        } else if (hour >= 12 && hour < 18) {
            timeOfDay = "下午" + (hour - 12) + "时";
        } else {
            timeOfDay = "晚上" + (hour - 12) + "时";
        }
        return timeOfDay;
    }
}