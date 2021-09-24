package miku.ad.prophet;

import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.content.res.Resources;
import android.text.TextUtils;
import android.util.Log;

import androidx.core.os.ConfigurationCompat;
import androidx.core.os.LocaleListCompat;

import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;

import java.util.Iterator;
import java.util.List;

import miku.ad.adapters.FuseAdLoader;
import miku.ad.imageloader.FileUtils;
import miku.storage.LocalDataSourceImpl;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class ProphetManager {
    public static final String TAG = "ProphetManager";
    public static final String PROPHET_TYPE = "prophet_type.json";
    public static final String ADS_CONFIGS = "ads_configs.json";
    public static final String RECOURCE_EN = "recource_en.json";
    public static final String CUSTOM_ADS_CONFIGS = "custom_ads_configs.json";
    public static final String CUSTOM_RECOURCE_EN = "custom_recource_en.json";

    private Context mContext;
    private ProphetType prophetType;
    private AdConfigBean prophetConfig;
    private List<ProphetSrcBean> mProphetSrcList;
    private ProphetFirebaseBean firebaseConfigbean;


    static ProphetManager instance;

    public ProphetManager() {
    }

    public synchronized static ProphetManager getInstance() {
        if (instance == null) {
            instance = new ProphetManager();
        }
        return instance;
    }

    public synchronized void prophetInit(Context context) {
        Log.d("task--ad","prophetInit1 ");

        if (!FuseAdLoader.getConfiguration().hasProphet() || LocalDataSourceImpl.getInstance().getDisableProphetAll()) {
            return;
        }
        Log.d("task--ad","prophetInit ");

        mContext = context;
        if (firebaseConfigbean == null) {
            String firebaseConfigString = LocalDataSourceImpl.getInstance().getProphetConfig();
            if (!TextUtils.isEmpty(firebaseConfigString)) {
                firebaseConfigbean = new Gson().fromJson(firebaseConfigString, ProphetFirebaseBean.class);
            }
        }
        prophetConfig = LocalDataSourceImpl.getInstance().getAdConfigEntity();
        mProphetSrcList = LocalDataSourceImpl.getInstance().getProphetSrcEntity();
        prophetType = new Gson().fromJson(FileUtils.getJson(context, PROPHET_TYPE), ProphetType.class);
        if (prophetConfig == null || mProphetSrcList == null) {
            String configJson = FileUtils.getJson(context, CUSTOM_ADS_CONFIGS);
            if (TextUtils.isEmpty(configJson)) {
                configJson = FileUtils.getJson(context, ADS_CONFIGS);
            }
            prophetConfig = new Gson().fromJson(configJson, AdConfigBean.class);
            String srcListJson = FileUtils.getJson(context, CUSTOM_RECOURCE_EN);
            if (TextUtils.isEmpty(srcListJson)) {
                srcListJson = FileUtils.getJson(context, RECOURCE_EN);
            }
            mProphetSrcList = new Gson().fromJson(srcListJson, new TypeToken<List<ProphetSrcBean>>() {
            }.getType());
            mProphetSrcList = initValidBean(mProphetSrcList);
            LocalDataSourceImpl.getInstance().saveAdConfigEntity(prophetConfig);
            LocalDataSourceImpl.getInstance().saveProphetSrcEntity(mProphetSrcList);
        } else {
            mProphetSrcList = initValidBean(mProphetSrcList);
        }
        if (firebaseConfigbean != null && firebaseConfigbean.getVersion() > prophetConfig.getVersion()) {
            Log.w(TAG, "firebaseConfigbean = " + firebaseConfigbean);
            mProphetSrcList = firebaseConfigbean.getProphetSrcList();
            mProphetSrcList = initValidBean(mProphetSrcList);
            prophetConfig.setVersion(firebaseConfigbean.getVersion());
            LocalDataSourceImpl.getInstance().saveAdConfigEntity(prophetConfig);
            LocalDataSourceImpl.getInstance().saveProphetSrcEntity(mProphetSrcList);
        }
//        long refreshTime = prophetConfig.getRefresh() * 60 * 60 * 1000;
//        long pullTime = LocalDataSourceImpl.getInstance().getProphetPullTime() * 60 * 60 * 1000;
//        if(pullTime > 0){
//            refreshTime = pullTime;
//        }
//        if(prophetConfig == null || (System.currentTimeMillis() - refreshTime > LocalDataSourceImpl.getInstance().getAdConfigTime()) ) {
//                RetrofitClient.getInstance().create(RetrofitApiService.class).getConfig().enqueue(new Callback<AdConfigBean>() {
//                    @Override
//                    public void onResponse(Call<AdConfigBean> call, Response<AdConfigBean> response) {
//                        if(prophetConfig == null || mProphetSrcList == null || prophetConfig.getVersion() < response.body().getVersion()){
//                            prophetGetSrcList();
//                        }
//                        prophetConfig = response.body();
//                        if (!TextUtils.isEmpty(prophetConfig.toString())) {
//                            LocalDataSourceImpl.getInstance().saveAdConfigTime(System.currentTimeMillis());
//                            LocalDataSourceImpl.getInstance().saveAdConfigEntity(prophetConfig);
//                        }
//                    }
//
//                    @Override
//                    public void onFailure(Call<AdConfigBean> call, Throwable t) {
//                    }
//                });
//        }
    }

    public void prophetGetSrcList() {
        LocaleListCompat localeListCompat = ConfigurationCompat.getLocales(Resources.getSystem().getConfiguration());
        String lagu = localeListCompat.get(0).getLanguage();
        prophetGetSrcList(lagu);
    }

    public void prophetGetSrcList(final String local) {
//        RetrofitClient.getInstance().create(RetrofitApiService.class).getProphetSrc(local).enqueue(new Callback<List<ProphetSrcBean>>() {
//            @Override
//            public void onResponse(Call<List<ProphetSrcBean>> call, Response<List<ProphetSrcBean>> response) {
//                mProphetSrcList = response.body();
//                if (mProphetSrcList != null && mProphetSrcList.size() > 0) {
//                    LocalDataSourceImpl.getInstance().saveProphetSrcEntity(mProphetSrcList);
//                } else if (!local.equals("en")) {
                    prophetGetSrcList("en");
//                }
//                mProphetSrcList = initValidBean(mProphetSrcList);
//            }
//
//            @Override
//            public void onFailure(Call<List<ProphetSrcBean>> call, Throwable t) {
//            }
//        });
    }

    public List<ProphetSrcBean> initValidBean(List<ProphetSrcBean> prophetSrcList) {
        Iterator iterator = prophetSrcList.iterator();
        while (iterator.hasNext()) {
            ProphetSrcBean bean = (ProphetSrcBean) iterator.next();
            if (!prophetType.getType().contains(bean.getType())) {
                iterator.remove();
            }
            if (!TextUtils.isEmpty(bean.getPkg()) && !isNeedRecommend(bean.getPkg())) {
                iterator.remove();
            }
        }
        return prophetSrcList;
    }

    public boolean isNeedRecommend(String pkg) {
        Log.d("task--ad","pp-true");
        Intent intent;
        PackageManager pm = getContext().getPackageManager();
        if (pkg.equals(getContext().getPackageName())) {
            return false;
        }
        intent = pm.getLaunchIntentForPackage(pkg);
        if (intent != null) {
            return false;
        }
        if (pkg.equals("miku.twitter.tweet.twimate.twittervideodownloader")) {
            intent = pm.getLaunchIntentForPackage("com.twitter.android");
            if (intent != null) {
                return true;
            }
            intent = pm.getLaunchIntentForPackage("com.twitter.android.lite");
            if (intent != null) {
                return true;
            }
            return false;
        }
        return true;
    }

    public List<ProphetSrcBean> getProphetSrcList() {
        return mProphetSrcList;
    }

    public Context getContext() {
        return mContext;
    }

    public void initFirebase(ProphetFirebaseBean finalbean) {
        firebaseConfigbean = finalbean;
        if (mContext != null) {
            prophetInit(mContext);
        }
    }
}
