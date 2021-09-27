package miku.firebase;


import android.text.TextUtils;
import android.util.Log;

import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.remoteconfig.FirebaseRemoteConfig;
import com.google.firebase.remoteconfig.FirebaseRemoteConfigSettings;
import com.google.gson.Gson;

import miku.ad.AdConstants;
import miku.ad.prophet.ProphetFirebaseBean;
import miku.ad.prophet.ProphetManager;
import miku.storage.LocalDataSourceImpl;

public class RemoteConfig {
    private static FirebaseRemoteConfig mFirebaseRemoteConfig;
    private static String TAG = "RemoteConfig";

    public static void init() {
        try {
            mFirebaseRemoteConfig = FirebaseRemoteConfig.getInstance();
            FirebaseRemoteConfigSettings configSettings = new FirebaseRemoteConfigSettings.Builder()
//                    .setDeveloperModeEnabled(AdConstants.DEBUG)
                    .build();
            mFirebaseRemoteConfig.setConfigSettingsAsync(configSettings);
            int cacheTime = AdConstants.DEBUG ? 0 : 2 * 60 * 60;
            mFirebaseRemoteConfig.fetch(cacheTime).addOnSuccessListener(new OnSuccessListener<Void>() {
                @Override
                public void onSuccess(Void aVoid) {
                    Log.i(TAG, "engine Fetch Succeeded");
                    mFirebaseRemoteConfig.fetchAndActivate();
                    initProphetConfig();
                }
            }).addOnFailureListener(new OnFailureListener() {
                @Override
                public void onFailure(Exception exception) {
                    Log.i(TAG, "engine Fetch failed" + exception);
                }
            });
            mFirebaseRemoteConfig.fetchAndActivate();
        } catch (Exception e) {
        }
    }

    public static void initProphetConfig() {
        Gson gson = new Gson();
        LocalDataSourceImpl.getInstance().saveDisableProphetAll(getBoolean("DisableProphetAll"));
        String remoteProphetConfig = getString("ProphetConfig");
        String localProphetConfig = LocalDataSourceImpl.getInstance().getProphetConfig();
        ProphetFirebaseBean finalbean = null;
        Log.w(TAG, "remoteProphetConfig = " + remoteProphetConfig);
        if (!TextUtils.isEmpty(remoteProphetConfig)) {
            ProphetFirebaseBean remoteBean = gson.fromJson(remoteProphetConfig, ProphetFirebaseBean.class);
            if(TextUtils.isEmpty(localProphetConfig)){
                LocalDataSourceImpl.getInstance().saveProphetConfig(remoteProphetConfig);
                finalbean = remoteBean;
                Log.d("task--ad","localProphetConfig ");
            }
            else{
                Log.d("task--ad","localProphetConfig2 ");

                finalbean = remoteBean;
                ProphetFirebaseBean localBean = gson.fromJson(localProphetConfig, ProphetFirebaseBean.class);
                if(remoteBean.getVersion() > localBean.getVersion()){
                    LocalDataSourceImpl.getInstance().saveProphetConfig(remoteProphetConfig);
                }
                else{
                    finalbean = localBean;
                }
            }
        }
        ProphetManager.getInstance().initFirebase(finalbean);
    }

    public static boolean getBoolean(String key) {
        if (mFirebaseRemoteConfig == null)
            return false;
        else {
            return mFirebaseRemoteConfig.getBoolean(key);
        }
    }

    public static String getString(String key) {
        if (mFirebaseRemoteConfig == null) {
            return "";
        } else {
            return mFirebaseRemoteConfig.getString(key);
        }
    }
}
