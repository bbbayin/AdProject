package video.report.mediaplayer.firebase;


import android.text.TextUtils;
import android.util.Log;

import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.remoteconfig.FirebaseRemoteConfig;
import com.google.firebase.remoteconfig.FirebaseRemoteConfigSettings;

import org.jetbrains.annotations.NotNull;

import java.util.ArrayList;
import java.util.List;

import miku.ad.AdConfig;
import video.report.mediaplayer.BuildConfig;
import video.report.mediaplayer.R;

public class RemoteConfig {

    private static FirebaseRemoteConfig mFirebaseRemoteConfig = FirebaseRemoteConfig.getInstance();
    private static String TAG = "RemoteConfig";

    public static void init() {
        FirebaseRemoteConfigSettings configSettings = new FirebaseRemoteConfigSettings.Builder()
                .setDeveloperModeEnabled(BuildConfig.DEBUG)
                .build();
        mFirebaseRemoteConfig.setConfigSettings(configSettings);
        int cacheTime = BuildConfig.DEBUG ? 0 : 2 * 60 * 60;
        mFirebaseRemoteConfig.setDefaults(R.xml.default_remote_config);
        mFirebaseRemoteConfig.fetch(cacheTime).addOnSuccessListener(new OnSuccessListener<Void>() {
            @Override
            public void onSuccess(Void aVoid) {
                Log.d(TAG, "Fetch Succeeded");
                mFirebaseRemoteConfig.activateFetched();
            }
        }).addOnFailureListener(new OnFailureListener() {
            @Override
            public void onFailure(@NotNull Exception exception) {
                Log.d(TAG, "Fetch failed" + exception);
            }
        });
        mFirebaseRemoteConfig.activateFetched();
    }

    public static boolean getBoolean(String key) {
        return mFirebaseRemoteConfig.getBoolean(key);
    }

    public static String getString(String key) {
        return mFirebaseRemoteConfig.getString(key);
    }

    public static List<AdConfig> getAdConfigList(String placement) {
        String config = getString(placement);
        if (TextUtils.isEmpty(config)) {
            return new ArrayList<>();
        }
        Log.d(TAG, "placement: " + placement);
        Log.d(TAG, "config: " + config);
        List<AdConfig> configList = new ArrayList<>();
        String[] sources = config.split(";");
        for (String s : sources) {
            String[] configs = s.split(":");
            if (configs == null || configs.length < 2) {
                Log.e(TAG, "Wrong config: " + s);
                continue;
            }
            int cachTime = 0;
            if (configs.length == 3) {
                try {
                    cachTime = Integer.valueOf(configs[2]);
                } catch (Exception e) {
                    Log.e(TAG, "Wrong config: " + config);
                }
            }
            if (cachTime <= 0) {
                cachTime = 60 * 60; // 1h
            }
            int bannerType = -1;
            if (configs.length == 4) {
                try {
                    bannerType = Integer.valueOf(configs[3]);
                } catch (Exception e) {
                    Log.e(TAG, "Wrong config: " + config);
                }
            }
            if (bannerType == -1) {
                configList.add(new AdConfig(configs[0], configs[1], cachTime));
            } else {
                configList.add(new AdConfig(configs[0], configs[1], cachTime, bannerType));
            }
        }
        return configList;
    }
}
