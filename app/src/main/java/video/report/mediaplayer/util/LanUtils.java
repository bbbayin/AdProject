package video.report.mediaplayer.util;

import android.app.Activity;
import android.content.Context;
import android.content.SharedPreferences;
import android.content.res.Configuration;
import android.content.res.Resources;
import android.media.AudioManager;
import android.os.Build;
import android.os.LocaleList;
import android.provider.Settings;
import android.util.DisplayMetrics;
import android.view.WindowManager;


import java.math.BigDecimal;
import java.util.Locale;


public class LanUtils {
    public static int getSystemMusicMaxVolume(Context context) {
        AudioManager am = (AudioManager) context.getSystemService(Context.AUDIO_SERVICE);
        return am.getStreamMaxVolume(AudioManager.STREAM_MUSIC);
    }

    public static int getSystemMusicVolume(Context context) {
        AudioManager am = (AudioManager) context.getSystemService(Context.AUDIO_SERVICE);
        return am.getStreamVolume(AudioManager.STREAM_MUSIC);
    }

    public static void setSystemMusicVolume(Context context, int volume) {
        AudioManager am = (AudioManager) context.getSystemService(Context.AUDIO_SERVICE);
        try {
            am.setStreamVolume(AudioManager.STREAM_MUSIC, volume, 0);
        } catch (Exception e) {
        }
    }

    public static void setScreenBrightness(Context context, float brightness) {
        WindowManager.LayoutParams layout = ((Activity) context).getWindow().getAttributes();
        layout.screenBrightness = brightness;
        ((Activity) context).getWindow().setAttributes(layout);
    }


    public static float getScreenBrightness(Context context) {
        WindowManager.LayoutParams layout = ((Activity) context).getWindow().getAttributes();
        if (layout.screenBrightness == WindowManager.LayoutParams.BRIGHTNESS_OVERRIDE_NONE) {
            try {
                int curBrightnessValue = Settings.System.getInt(
                        context.getContentResolver(),
                        Settings.System.SCREEN_BRIGHTNESS);
                return (float) curBrightnessValue / 255;
            } catch (Settings.SettingNotFoundException e) {
                e.printStackTrace();
                return 0;
            }
        }
        return layout.screenBrightness;
    }

    public static BigDecimal string2BigDecimal(String str) throws Exception{
        return new BigDecimal(str);
    }

    public static Locale getSystemLocale(Context context) {
        Locale locale;
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.N) {
            locale = LocaleList.getDefault().get(0);
        } else {
            locale = Locale.getDefault();
        }
        return locale;
    }

    public static Context setLocal(Context context, Locale locale) {
        return updateResources(context, locale);
    }

    private static Context updateResources(Context context, Locale locale) {
        Locale.setDefault(locale);
        Resources res = context.getResources();
        Configuration config = new Configuration(res.getConfiguration());
        config.setLocale(locale);
        context = context.createConfigurationContext(config);
        return context;
    }

    private final String SP_NAME = "language_setting";
    private final String TAG_LANGUAGE = "language_select";
    private final String TAG_NIGHTMODE = "nightmode_select";
    private static volatile LanUtils instance;

    public static int sDefaultUiMode = 0;
    public static int sCurrentUiMode = -1;

    private final String FIRST_SET = "first_set";

    private final SharedPreferences mSharedPreferences;

    public LanUtils(Context context) {
        mSharedPreferences = context.getSharedPreferences(SP_NAME, Context.MODE_PRIVATE);
    }

    public void saveLanguage(int select) {
        SharedPreferences.Editor edit = mSharedPreferences.edit();
        edit.putInt(TAG_LANGUAGE, select);
        edit.apply();
    }

    public boolean languageCheckState() {
        return mSharedPreferences.getBoolean(FIRST_SET, false);
    }

    public void setLanguageCheckState() {
        SharedPreferences.Editor edit = mSharedPreferences.edit();
        edit.putBoolean(FIRST_SET, true);
        edit.apply();
    }

    public int getSelectLanguage() {
        return mSharedPreferences.getInt(TAG_LANGUAGE, 0);
    }

    public static LanUtils getInstance(Context context) {
        if (instance == null) {
            synchronized (LanUtils.class) {
                if (instance == null) {
                    instance = new LanUtils(context);
                }
            }
        }
        return instance;
    }

    /**
     * 设置语言类型
     */
    public static void setApplicationLanguage(Context context, Locale locale) {
        Resources resources = context.getApplicationContext().getResources();
        DisplayMetrics dm = resources.getDisplayMetrics();
        Configuration config = resources.getConfiguration();
        //Locale locale = getSetLanguageLocale(context);
        config.locale = locale;
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.N) {
            LocaleList localeList = new LocaleList(locale);
            LocaleList.setDefault(localeList);
            config.setLocales(localeList);
            context.createConfigurationContext(config);
            Locale.setDefault(locale);
        }
        resources.updateConfiguration(config, dm);
    }
}
