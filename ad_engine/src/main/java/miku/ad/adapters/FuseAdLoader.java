package miku.ad.adapters;

import android.app.Activity;
import android.app.Application;
import android.content.Context;
import android.os.Bundle;
import android.os.Handler;
import android.os.Looper;
import android.text.TextUtils;
import android.util.Log;

import androidx.annotation.Nullable;

import com.applovin.sdk.AppLovinSdk;
import com.applovin.sdk.AppLovinSdkConfiguration;
import com.google.android.gms.ads.AdSize;
import com.google.android.gms.ads.MobileAds;
import com.google.android.gms.ads.initialization.InitializationStatus;
import com.google.android.gms.ads.initialization.OnInitializationCompleteListener;
import com.mopub.common.MoPub;
import com.mopub.common.SdkConfiguration;
import com.mopub.common.SdkInitializationListener;
import com.vungle.warren.InitCallback;
import com.vungle.warren.Vungle;
import com.vungle.warren.error.VungleException;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;

import miku.ad.AdConfig;
import miku.ad.AdConstants;
import miku.ad.AdLog;
import miku.ad.AdUtils;
import miku.ad.SDKConfiguration;
import miku.ad.bean.AdClickInfo;
import miku.ad.imageloader.ImageLoader;
import miku.firebase.RemoteConfig;
import miku.firebase.BaseDataReportUtils;
import miku.utils.Utils;
import miku.storage.LocalDataSourceImpl;

import static miku.ad.AdConstants.AdType.AD_SOURCE_ADCOLONY_BANNER;
import static miku.ad.AdConstants.AdType.AD_SOURCE_ADCOLONY_INTERSTITIAL;
import static miku.ad.AdConstants.AdType.AD_SOURCE_ADCOLONY_REWARD;
import static miku.ad.AdConstants.AdType.AD_SOURCE_ADMOB;
import static miku.ad.AdConstants.AdType.AD_SOURCE_ADMOB_BANNER;
import static miku.ad.AdConstants.AdType.AD_SOURCE_ADMOB_H;
import static miku.ad.AdConstants.AdType.AD_SOURCE_ADMOB_INTERSTITIAL;
import static miku.ad.AdConstants.AdType.AD_SOURCE_ADMOB_INTERSTITIAL_H;
import static miku.ad.AdConstants.AdType.AD_SOURCE_ADMOB_INTERSTITIAL_M;
import static miku.ad.AdConstants.AdType.AD_SOURCE_ADMOB_M;
import static miku.ad.AdConstants.AdType.AD_SOURCE_ADMOB_REWARD;
import static miku.ad.AdConstants.AdType.AD_SOURCE_APPLOVIN_BANNER;
import static miku.ad.AdConstants.AdType.AD_SOURCE_APPLOVIN_INTERSTITIAL;
import static miku.ad.AdConstants.AdType.AD_SOURCE_APPLOVIN_MREC;
import static miku.ad.AdConstants.AdType.AD_SOURCE_APPLOVIN_REWARD;
import static miku.ad.AdConstants.AdType.AD_SOURCE_MOPUB;
import static miku.ad.AdConstants.AdType.AD_SOURCE_MOPUB_INTERSTITIAL;
import static miku.ad.AdConstants.AdType.AD_SOURCE_MOPUB_REWARD;
import static miku.ad.AdConstants.AdType.AD_SOURCE_PROPHET;
import static miku.ad.AdConstants.AdType.AD_SOURCE_VG;
import static miku.ad.AdConstants.AdType.AD_SOURCE_VG_BANNER;
import static miku.ad.AdConstants.AdType.AD_SOURCE_VG_INTERSTITIAL;

public class FuseAdLoader {
    private Context mAppContext;
    private static int defaultBrustNum = 6;
    private int keyBrustNum = 0;
    private static Context mContext;
    private List<AdConfig> mNativeAdConfigList = new ArrayList();
    private HashMap<String, IAdAdapter> mNativeAdCache = new HashMap<>();
    private IAdLoadListener mListener;
    private int lastIdx = 0;
    private String mSlot;
    private AdSize mBannerAdSize;
    private long mProtectOverTime = 0;
    private static Handler mHandler = new Handler(Looper.getMainLooper());
    private int mLoadingBits;
    private boolean mAdReturned;
    private static ConfigFetcher sConfigFetcher;
    private static SDKConfiguration sConfiguration;
    private static String sUserId;
    private static boolean sInitializedWithActivity;
    private boolean mAutoLoad;
    private static boolean mBanInvalidAd = false;
    private static boolean mAdmobFree;
    private static boolean mFanFree;
    private static boolean mInited = false;
    private static HashMap<String, AdClickInfo> mClickInfoMap = new HashMap<>();


    private static HashMap<String, FuseAdLoader> sAdLoaderMap = new HashMap<>();

    public synchronized static FuseAdLoader get(String slot, Context context) {
        FuseAdLoader adLoader = sAdLoaderMap.get(slot);
        if (adLoader == null) {
            adLoader = new FuseAdLoader(slot, context.getApplicationContext());
            sAdLoaderMap.put(slot, adLoader);
        }
        if (context instanceof Activity && !sInitializedWithActivity) {
            if (sConfiguration.hasMopub()) {
                initMopub((Activity) context);
            }
            sInitializedWithActivity = true;
        }
        return adLoader;
    }

    public static IAdAdapter getOnlyTopAd(Context context, String type, String... slotList) {
        IAdAdapter ad;
        for (String slot : slotList) {
            FuseAdLoader adLoader = get(slot, context);
            ad = adLoader.getAd(type);
            if (ad != null) {
                return ad;
            }
        }
        return null;
    }

    public static IAdAdapter getAllTopAd(Context context, String type, String... slotList) {
        IAdAdapter ad;
        for (String slot : slotList) {
            FuseAdLoader adLoader = get(slot, context);
            ad = adLoader.getAd(type);
            if (ad != null) {
                return ad;
            }
        }
        for (String slot : slotList) {
            FuseAdLoader adLoader = get(slot, context);
            ad = adLoader.getAd();
            if (ad != null) {
                return ad;
            }
        }
        return null;
    }

    public static IAdAdapter getAllTopAdByScenes(Context context, List<String> typeList, String... slotList) {
        return getAllTopAdByScenes(context, typeList, true, slotList);
    }

    public static IAdAdapter getAllTopAdByScenes(Context context, List<String> typeList, boolean useProphet, String... slotList) {
        return getAllTopAdByScenes(context, typeList, true, useProphet, slotList);
    }

    public static IAdAdapter getAllTopAdByScenes(Context context, List<String> typeList, boolean useExtra, boolean useProphet, String... slotList) {
        IAdAdapter ad;
        for (String type : typeList) {
            for (String slot : slotList) {
                FuseAdLoader adLoader = get(slot, context);
                ad = adLoader.getAd(type, useProphet);
                if (ad != null) {
                    return ad;
                }
            }
        }
        if (useExtra) {
            for (String slot : slotList) {
                FuseAdLoader adLoader = get(slot, context);
                ad = adLoader.getAd(useProphet);
                if (ad != null) {
                    return ad;
                }
            }
        }
        return null;
    }


    public static void setUserId(String userId) {
        sUserId = userId;
    }

    private static void initMopub(Activity activity) {
        SdkConfiguration.Builder builder = new SdkConfiguration.Builder(sConfiguration.mopubInitAdId);
        try {
            MoPub.initializeSdk(activity, builder.build(), new SdkInitializationListener() {
                @Override
                public void onInitializationFinished() {
                    AdLog.d("Mopub initialized");
                    MoPub.getPersonalInformationManager().grantConsent();
                    AdLog.d("initMopub = true");
                }
            });
        } catch (Exception e) {
            AdLog.d("initMopub = false");
        }
    }

    private static void initApplovin(Context context){
//        AppLovinSdk.getInstance( context ).setMediationProvider( "max" );
        AppLovinSdk.initializeSdk( context, new AppLovinSdk.SdkInitializationListener() {
            @Override
            public void onSdkInitialized(final AppLovinSdkConfiguration configuration)
            {
                // AppLovin SDK is initialized, start loading ads
            }
        } );
    }

    private static void initVungle(Context context, String vgId) {
        if (AdConstants.DEBUG) {
            vgId = "616155990221f159a6d5cad6";
        }
        Log.i("VGInterstitial", "Vungle initialized = " + vgId);
        Vungle.init(vgId, context, new InitCallback() {
            @Override
            public void onSuccess() {
                Log.i("VGInterstitial", "Vungle initialized onSuccess");
                // Initialization has succeeded and SDK is ready to load an ad or play one if there
                // is one pre-cached already
            }

            @Override
            public void onError(VungleException exception) {
                AdLog.d("Vungle initialized onError = " + exception);
            }

            @Override
            public void onAutoCacheAdAvailable(String placementId) {
                // Callback to notify when an ad becomes available for the cache optimized placement
                // NOTE: This callback works only for the cache optimized placement. Otherwise, please use
                // LoadAdCallback with loadAd API for loading placements.
            }
        });
    }

    public static Context getContext() {
        return mContext;
    }

    public static void setDebug(boolean debug) {
        AdConstants.DEBUG = debug;
    }

    public static void init(final ConfigFetcher depends, Context context, final SDKConfiguration configuration) {
        mContext = context.getApplicationContext();
        Utils.init(mContext);
        sConfigFetcher = depends;
        sConfiguration = configuration;
        if (sConfiguration.hasAdmob()) {
            MobileAds.initialize(context, new OnInitializationCompleteListener() {
                @Override
                public void onInitializationComplete(InitializationStatus initializationStatus) {

                }
            });
        }
        if (sConfiguration.hasVG()) {
            initVungle(context, configuration.vgId);
        }

        if(sConfiguration.hasApplovin()){
            initApplovin(context );
        }

        if(sConfiguration.hasAdcolony()){

        }

        if (context instanceof Activity) {
            sInitializedWithActivity = true;
            if (sConfiguration.hasMopub()) {
                initMopub((Activity) context);
            }
        }
        RemoteConfig.init();

        Application application = (Application) context.getApplicationContext();
        application.registerActivityLifecycleCallbacks(new Application.ActivityLifecycleCallbacks() {
            @Override
            public void onActivityCreated(Activity activity, Bundle savedInstanceState) {

            }

            @Override
            public void onActivityStarted(Activity activity) {

            }

            @Override
            public void onActivityResumed(Activity activity) {

            }

            @Override
            public void onActivityPaused(Activity activity) {

            }

            @Override
            public void onActivityStopped(Activity activity) {

            }

            @Override
            public void onActivitySaveInstanceState(Activity activity, Bundle outState) {

            }

            @Override
            public void onActivityDestroyed(Activity activity) {

            }
        });
        BaseDataReportUtils.getInstance().reportAdClickAndShowIfNeed();
        checkShouldBanSource();
        mInited = true;
    }

    public static void checkShouldBanSource() {
        if (mBanInvalidAd) {
            if (LocalDataSourceImpl.getInstance().getAdNumByKey("admob_click_num") >= 5) {
                FuseAdLoader.setAdmobFree(true);
            } else {
                FuseAdLoader.setAdmobFree(false);
            }
            if (LocalDataSourceImpl.getInstance().getAdNumByKey("fan_click_num") >= 10) {
                FuseAdLoader.setFanFree(true);
            } else {
                FuseAdLoader.setFanFree(false);
            }
        } else {
            FuseAdLoader.setAdmobFree(false);
            FuseAdLoader.setFanFree(false);
        }
    }

    public static boolean isAdmob(String type) {
        if (type.equals(AD_SOURCE_ADMOB) ||type.equals(AD_SOURCE_ADMOB_H) ||type.equals(AD_SOURCE_ADMOB_M) || type.equals(AD_SOURCE_ADMOB_INTERSTITIAL)|| type.equals(AD_SOURCE_ADMOB_INTERSTITIAL_H)|| type.equals(AD_SOURCE_ADMOB_INTERSTITIAL_M) || type.equals(AD_SOURCE_ADMOB_BANNER) || type.equals(AD_SOURCE_ADMOB_REWARD)  ) {
            return true;
        }
        return false;
    }

    public static boolean isAdmob(IAdAdapter ad) {
        return isAdmob(ad.getAdType());
    }


    public static boolean isMopub(IAdAdapter ad) {
        if (ad.getAdType() == AD_SOURCE_MOPUB || ad.getAdType() == AD_SOURCE_MOPUB_INTERSTITIAL || ad.getAdType() == AD_SOURCE_MOPUB_REWARD) {
            return true;
        }
        return false;
    }

    public static boolean isVungle(IAdAdapter ad) {
        if (ad.getAdType() == AD_SOURCE_VG || ad.getAdType() == AD_SOURCE_VG_INTERSTITIAL || ad.getAdType() == AD_SOURCE_VG_BANNER) {
            return true;
        }
        return false;
    }
    public static boolean isApplovin(IAdAdapter ad) {
        if (ad.getAdType() == AD_SOURCE_APPLOVIN_BANNER || ad.getAdType() == AD_SOURCE_APPLOVIN_INTERSTITIAL || ad.getAdType() == AD_SOURCE_APPLOVIN_MREC || ad.getAdType() == AD_SOURCE_APPLOVIN_REWARD) {
            return true;
        }
        return false;
    }
    public static boolean isAdcolony(IAdAdapter ad) {
        if (ad.getAdType() == AD_SOURCE_ADCOLONY_BANNER || ad.getAdType() == AD_SOURCE_ADCOLONY_INTERSTITIAL || ad.getAdType() == AD_SOURCE_ADCOLONY_REWARD) {
            return true;
        }
        return false;
    }

    public interface ConfigFetcher {
        public boolean isAdFree(String slot);

        public List<AdConfig> getAdConfigList(String slot);
    }

    private FuseAdLoader(String slot, Context context) {
        this.mAppContext = context;
        mSlot = slot;
        List<AdConfig> adSources;
        if (sConfigFetcher != null) {
            adSources = sConfigFetcher.getAdConfigList(mSlot);
        } else {
            adSources = new ArrayList<>(0);
        }
        addAdConfigList(adSources);
    }

    public static final HashSet<String> SUPPORTED_TYPES = new HashSet<>();

    public void preLoadAd(Context context) {
        preLoadAd(context, getBrustNum());
    }

    public FuseAdLoader setBannerAdSize(AdSize adSize) {
        mBannerAdSize = adSize;
        return this;
    }

    public void loadAd(Context context, IAdLoadListener listener) {
        loadAdNoProphet(context, getBrustNum(), 1000, listener);
    }

    public void setAutoLoad(boolean autoLoad) {
        this.mAutoLoad = autoLoad;
    }

    public void preLoadAd(Context context, int burstNum) {
        preLoadAd(context, burstNum, null);
    }

    public void preLoadAd(Context context, int burstNum, String adSource) {
        AdLog.d("FuseAdLoader preLoadAd :" + mSlot + " load ad: " + burstNum);
        if (!AdUtils.hasConnectedNetwork(context)) {
            AdLog.d("FuseAdLoader preLoadAd: AD no network");
            return;
        }
        if (Looper.getMainLooper() != Looper.myLooper()) {
            throw new IllegalStateException("Load ad not from main thread");
        }
        if (sConfigFetcher.isAdFree(mSlot)) {
            AdLog.d("FuseAdLoader preLoadAd: AD free version");
            return;
        }
        if (burstNum <= 0 || mNativeAdConfigList.size() == 0) {
            AdLog.d("FuseAdLoader preLoadAd:" + mSlot + " load num wrong: " + burstNum);
            return;
        }
        for (int i = 0; i < burstNum; i++) {
            if (loadNextNativeAd(context, i, adSource)) {
                AdLog.d("Stop burst as already find cache at: " + i);
                break;
            }
        }
        lastIdx = burstNum;
        layeredLoadAd(context, 3000, burstNum);
    }

    public void loadAdNoProphet(Context context, int burstNum, long protectTime, IAdLoadListener listener) {
        loadAd(context, burstNum, protectTime, false, listener);
    }

    public void loadAd(Context context, int burstNum, long protectTime, IAdLoadListener listener) {
        loadAd(context, burstNum, protectTime, false, listener);
    }

    public void loadAd(Context context, int burstNum, long protectTime, final boolean useProphet, IAdLoadListener listener) {
        AdLog.d("FuseAdLoader :" + mSlot + " load ad: " + burstNum + " listener: " + listener);
        if (!AdUtils.hasConnectedNetwork(context)) {
            AdLog.d("FuseAdLoader: AD no network");
            return;
        }
        if (Looper.getMainLooper() != Looper.myLooper()) {
            throw new IllegalStateException("Load ad not from main thread");
        }
        if (sConfigFetcher == null || sConfigFetcher.isAdFree(mSlot)) {
            AdLog.d("FuseAdLoader : AD free version");
            if (listener != null) {
                listener.onError("AD free version");
            }
            return;
        }
        if (burstNum <= 0 || mNativeAdConfigList.size() == 0) {
            AdLog.d("FuseAdLoader :" + mSlot + " load num wrong: " + burstNum);
            if (listener != null) {
                listener.onError("Wrong config");
            }
            return;
        }
        //if (burstNum == 1) { protectTime = 0;}
        mProtectOverTime = System.currentTimeMillis() + protectTime;
        mListener = listener;
        mAdReturned = false;
        lastIdx = 0;
        if (protectTime > 0) {
            mHandler.postDelayed(new Runnable() {
                @Override
                public void run() {
                    if (mListener != null) {
                        if (!mAdReturned) {
                            AdLog.d(mSlot + " cache return to " + mListener);
                            if (hasValidCache(useProphet)) {
                                mAdReturned = true;
                                mListener.onAdLoaded(null);
                            }
                        } else {
                            AdLog.d(mSlot + " already returned");
                        }
                        //mListener = null;
                    }
                }
            }, protectTime);
        }
        for (int i = 0; i < burstNum; i++) {
            if (loadNextNativeAd(context)) {
                AdLog.d("Stop burst as already find cache at: " + i);
                break;
            }
        }
        layeredLoadAd(context, 3000, burstNum);
    }


    public void layeredLoadAd(final Context context, final long layerTime, final int layerNum) {
        if (lastIdx < mNativeAdConfigList.size() && !hasValidCache()) {
            mHandler.postDelayed(new Runnable() {
                @Override
                public void run() {
                    if (hasValidCache()) {
                        return;
                    }
                    for (int i = 0; i < layerNum; i++) {
                        if (loadNextNativeAd(context)) {
                            break;
                        }
                    }
                    layeredLoadAd(context, layerTime, layerNum);
                }
            }, layerTime);
        }
    }

    public IAdAdapter getAd(String type) {
        return getAd(type, true);
    }

    public IAdAdapter getAd(boolean useProphet) {
        return getAd("", useProphet);
    }

    public IAdAdapter getAd() {
        return getAd("", true);
    }

    @Nullable
    public IAdAdapter getAd(String type, boolean useProphet) {
        if (sConfigFetcher.isAdFree(mSlot) || !mInited) {
            return null;
        }
        IAdAdapter cache = getValidCache(type, useProphet);
        if (cache != null) {
            AdLog.d(mSlot + " get cache return " + cache);
            return cache;
        }
        return null;
    }

    private boolean isLoading(int idx) {
        return (mLoadingBits & (0x1 << idx)) != 0;
    }

    private void markLoading(int idx) {
        mLoadingBits |= (0x1 << idx);
    }

    private void finishLoading(Context context, int idx) {
        mLoadingBits &= (~(0x1 << idx));
        if (mAdReturned) {
            AdLog.d("Ad already returned " + mSlot);
            return;
        }
        long now = System.currentTimeMillis();
        if (!hasValidCache()) {
            //need load next or no fill;
            AdLog.d("No valid ad returned " + mSlot);
            if (idx == mNativeAdConfigList.size() - 1) {
                boolean betterLoading = false;
                for (int i = idx - 1; i >= 0; i--) {
                    if (isLoading(i)) {
                        betterLoading = true;
                        break;
                    }
                }
                if (!betterLoading && mListener != null) {
                    AdLog.d("Loaded all adapter, no fill in time");
                    mListener.onError("No Fill");
                    //In case ad loaded after time out
                    //mListener = null;
                }
            } else {
                loadNextNativeAd(context);
            }
        } else {
            // no need load next, fill or just wait timeout;
            int i;
            for (i = idx - 1; i >= 0; i--) {
                if (isLoading(i)) {
                    break;
                }
            }
            AdLog.d("loaded index: " + idx + " i: " + i + " wait: " + (now - mProtectOverTime));
            if (now >= mProtectOverTime || i < 0) {
                if (mListener != null) {
                    if (hasValidCache()) {
                        mAdReturned = true;
                        AdLog.d(mSlot + " return to " + mListener);
                        mListener.onAdLoaded(null);
                    }
                    //mListener = null;
                }
            } else {
                AdLog.d("Wait for protect time over");
            }
        }
    }

    private int nextLoadingIdx() {
        return lastIdx++;
    }

    private IAdAdapter getValidCache(boolean useProphet) {
        return getValidCache("", useProphet);
    }

    private IAdAdapter getValidCache() {
        return getValidCache("", true);
    }

    private IAdAdapter getValidCache(String type, boolean useProphet) {
        IAdAdapter cache = null;
        if (sConfigFetcher.isAdFree(mSlot)) {
            return null;
        }
        for (AdConfig config : mNativeAdConfigList) {
            if (!TextUtils.isEmpty(type) && !type.equals(config.source)) {
                continue;
            }
            if (useProphet == false && config.source.equals(AD_SOURCE_PROPHET)) {
                continue;
            }
            cache = mNativeAdCache.get(config.key);
            if (cache != null) {
                if ((FuseAdLoader.isAdmob(cache) && FuseAdLoader.getAdmobFree()) || cache.isShowed() || ((System.currentTimeMillis() - cache.getLoadedTime()) / 1000) > config.cacheTime) {
                    long delta = (System.currentTimeMillis() - cache.getLoadedTime()) / 1000;
                    AdLog.d("AdAdapter cache time out : " + delta + " config: " + config.cacheTime + " type: " + cache.getAdType());
                    mNativeAdCache.remove(config.key);
                    cache = null;
                } else {
                    mNativeAdCache.remove(config.key);
                    break;
                }
            }
        }
        if (mAutoLoad) {
            mHandler.postDelayed(new Runnable() {
                @Override
                public void run() {
                    preLoadAd();
                }
            }, 500);
        }
        return cache;
    }

    public void preLoadAd(Context mContext, String adSource) {
        preLoadAd(mContext, getBrustNum(), adSource);
    }

    private void preLoadAd() {
        preLoadAd(mContext);
    }

    public boolean hasValidCache(boolean useProphet) {
        for (AdConfig config : mNativeAdConfigList) {
            if (hasValidCache(config)) {
                if (useProphet == false && config.source.equals(AD_SOURCE_PROPHET)) {
                    continue;
                }
                return true;
            }
        }
        return false;
    }

    public boolean hasValidCache() {
        return hasValidCache(true);
    }

    private boolean hasValidCache(AdConfig config) {
        IAdAdapter cache = mNativeAdCache.get(config.key);
        if (cache != null) {
            if (cache.isShowed() || ((System.currentTimeMillis() - cache.getLoadedTime()) / 1000) > config.cacheTime) {
                AdLog.d("AdAdapter cache time out : " + cache.getTitle() + " type: " + cache.getAdType());
                mNativeAdCache.remove(config.key);
            } else {
                return true;
            }
        }
        return false;
    }

    @Deprecated
    public void loadAd(Context context, int burstNum, IAdLoadListener listener) {
        AdLog.d("load " + mSlot + " listen: " + listener);
        loadAdNoProphet(context, burstNum, 1000, listener);
    }

    public void addAdConfig(AdConfig adConfig) {
        if (adConfig != null && !TextUtils.isEmpty(adConfig.source) && !TextUtils.isEmpty(adConfig.key)) {
            Log.d("task--ad","adConfig-- "+adConfig.toString());
            if (sConfiguration.supportedFuseAdType.contains(adConfig.source)) {
                mNativeAdConfigList.add(adConfig);
                AdLog.d("add adConfig : " + adConfig.toString());
            } else {
                if (AdConstants.DEBUG) {
//                    throw new RuntimeException("error adconfig = " + adConfig.source);
                }
            }
        } else {
            if (AdConstants.DEBUG) {
                throw new RuntimeException("error adconfig = " + adConfig);
            }
        }
    }

    public void addAdConfigList(List<AdConfig> adConfigList) {
        if (adConfigList != null) {
            for (AdConfig adConfig : adConfigList) {
                addAdConfig(adConfig);
            }
        }
    }

    public boolean hasValidAdSource() {
        return mNativeAdConfigList != null && mNativeAdConfigList.size() > 0;
    }

    class IndexAdListener implements IAdLoadListener {
        int index;
        Context loadingContext;

        public IndexAdListener(Context context, int index) {
            this.index = index;
            loadingContext = context;
        }

        @Override
        public void onRewarded(IAdAdapter ad) {
            if (mListener != null) {
                mListener.onRewarded(ad);
            }
        }

        public void onAdLoaded(IAdAdapter ad) {
            mNativeAdCache.put(mNativeAdConfigList.get(index).key, ad);
            AdLog.d(mSlot + " ad loaded " + ad.getAdType() + " index: " + index);
            if (ad.getCoverImageUrl() != null) {
                AdLog.d("preload " + ad.getCoverImageUrl());
                ImageLoader.getInstance().doPreLoad(mAppContext, ad.getCoverImageUrl());
            }
            if (ad.getIconImageUrl() != null) {
                AdLog.d("preload " + ad.getIconImageUrl());
                ImageLoader.getInstance().doPreLoad(mAppContext, ad.getIconImageUrl());
            }
            finishLoading(loadingContext, index);
        }

        @Override
        public void onAdClicked(IAdAdapter ad) {
            if (mListener != null) {
                mListener.onAdClicked(ad);
            }
        }

        @Override
        public void onAdClosed(IAdAdapter ad) {
            if (mListener != null) {
                AdLog.d("Ad closed");
                mListener.onAdClosed(ad);
            }
        }

        @Override
        public void onAdListLoaded(List<IAdAdapter> ads) {
            //not support list yet
        }

        @Override
        public void onError(String error) {
            AdLog.e("Load current source " + mNativeAdConfigList.get(index).source + " error : " + error);
            finishLoading(loadingContext, index);
        }

    }

    private boolean loadNextNativeAd(Context context) {
        final int idx = nextLoadingIdx();
        return loadNextNativeAd(context, idx);
    }

    private boolean loadNextNativeAd(Context context, int idx) {
        return loadNextNativeAd(context, idx, null);
    }

    private boolean loadNextNativeAd(Context context, int idx, String adSource) {
        if (idx < 0 || idx >= mNativeAdConfigList.size()) {
            AdLog.d(mSlot + " tried to load all source . Index : " + idx);
            return false;
        }
        AdConfig config = mNativeAdConfigList.get(idx);

        if (!TextUtils.isEmpty(adSource) && !adSource.equals(config.source)) {
            return false;
        }

        if (getAdmobFree() && FuseAdLoader.isAdmob(config.source)) {
            return false;
        }
        if (isLoading(idx)) {
            AdLog.d(mSlot + " already loading . Index : " + idx);
            return false;
        }
        AdLog.d("loadNextNativeAd for " + idx);
        markLoading(idx);
        if (hasValidCache(config)) {
            AdLog.d(mSlot + " already have cache for : " + config.key);
            finishLoading(context, idx);
            return true;
        }
        //Do load
        IAdAdapter loader = getNativeAdAdapter(config);
        if (loader == null) {
            finishLoading(context, idx);
            return false;
        }
        AdLog.d(mSlot + " start load for : " + config.source + " index : " + idx);
        try {
            loader.loadAd(context, 1, new IndexAdListener(context, idx));
        } catch (Exception e) {
            finishLoading(context, idx);
            if (AdConstants.DEBUG) {
                throw new RuntimeException("loadNextNativeAd = " + e);
            }
        }
        return false;
    }

    static public Handler getHandler() {
        return mHandler;
    }

    private IAdAdapter getNativeAdAdapter(AdConfig config) {
        if (config == null || config.source == null) {
            return null;
        }
        if (!sConfiguration.hasSupport(config.source)) {
            return null;
        }
        if (sConfigFetcher.isAdFree(mSlot)) {
            return null;
        }
        try {
            Log.d("FuseAdLoader", "getNativeAdAdapter:  " + config.source + "   " + config.key);
            switch (config.source) {
                // admob native广告
                case AD_SOURCE_ADMOB:
                    return new AdmobNativeAdapter(mAppContext, config.key, mSlot);
                case AD_SOURCE_ADMOB_M:
                    return new AdmobNativeMAdapter(mAppContext, config.key, mSlot);
                case AD_SOURCE_ADMOB_H:
                    return new AdmobNativeHAdapter(mAppContext, config.key, mSlot);
                // admob 横幅广告
                case AD_SOURCE_ADMOB_BANNER:
                    AdSize bannerSize = config.bannerAdSize == null ? mBannerAdSize : config.bannerAdSize;
                    return bannerSize == null ? null : new AdmobBannerAdapter(mAppContext, config.key, bannerSize, mSlot);
                // admob 插屏广告
                case AD_SOURCE_ADMOB_INTERSTITIAL:
                    return new AdmobInterstitialAdapter(mAppContext, config.key, mSlot);
                case AD_SOURCE_ADMOB_INTERSTITIAL_H:
                    return new AdmobInterstitialHAdapter(mAppContext, config.key, mSlot);
                case AD_SOURCE_ADMOB_INTERSTITIAL_M:
                    return new AdmobInterstitialMAdapter(mAppContext, config.key, mSlot);
                // admob 激励广告
                case AD_SOURCE_ADMOB_REWARD:
                    return new AdmobRewardVideoAdapter(mAppContext, config.key, mSlot);
                // mopub 插屏广告
                case AD_SOURCE_MOPUB_INTERSTITIAL:
                    return new MopubInterstitialAdapter(mAppContext, config.key, mSlot);
                // mopub  native广告
                case AD_SOURCE_MOPUB:
                    return new MopubNativeAdapter(mAppContext, config.key, mSlot);
                // mopub  激励视频广告
//                case AdConstants.AdType.AD_SOURCE_MOPUB_REWARD:
//                    return new MopubRewardVideoAdapter(mAppContext, config.key, mSlot);
                //vungle 横幅广告
                case AD_SOURCE_VG:
                 return  new VGBannerAdapter(mAppContext, config.key, mSlot);
                // vungle  插屏广告
                case AD_SOURCE_VG_INTERSTITIAL:
                    if(AdUtils.checkTimes(mAppContext,"vungle")){
                        return new VGInterstitialAdapter(mAppContext, config.key, mSlot);
                    }else{
                        return null;
                    }
                case AD_SOURCE_PROPHET:
                    return new ProphetNativeAdapter(mAppContext, config.key, mSlot);
                //applovin
                case AD_SOURCE_APPLOVIN_BANNER:
                    return new ApplovinMaxBannerAdapter(mAppContext,config.key,mSlot);
                case AD_SOURCE_APPLOVIN_INTERSTITIAL:
                    if(AdUtils.checkTimes(mAppContext,"applovin")){
                        return new ApplovinMaxInterstitialAdapter(mAppContext, config.key, mSlot);
                    }else{
                        return null;
                    }
//                    return new ApplovinMaxInterstitialAdapter(mAppContext,config.key,mSlot);
//                case AD_SOURCE_APPLOVIN_MREC:
//                    return null;
//                case AD_SOURCE_APPLOVIN_REWARD:
//                    return null;
                //adcolony
                case AD_SOURCE_ADCOLONY_BANNER:
                    return new AdcolonyBannerAdapter(mAppContext,config.key,mSlot);
                case AD_SOURCE_ADCOLONY_INTERSTITIAL:
                    if(AdUtils.checkTimes(mAppContext,"adcolony")){
                        return new AdcolonyInterstitialAdapter(mAppContext, config.key, mSlot);
                    }else{
                        return null;
                    }
//                    return new AdcolonyInterstitialAdapter(mAppContext,config.key,mSlot);

                default:
                    AdLog.e("not suppported source " + config.source);
                    return null;
            }
        } catch (Throwable ex) {
            AdLog.e("Error to get loader for " + config);
            return null;
        }
    }

    public static void setAdClickListener(IAdAdapter ad, String key) {
        if (TextUtils.isEmpty(key) || ad == null) {
            return;
        }
        AdClickInfo clickInfo = new AdClickInfo();
        clickInfo.setAd(ad);
        clickInfo.setKey(key);
        mClickInfoMap.put(ad.getAdType(), clickInfo);
    }

    public static void reportAdClick(AdAdapter ad) {
        LocalDataSourceImpl.getInstance().addAdClickNum(ad);
        AdClickInfo clickInfo = mClickInfoMap.get(ad.getAdType());
        if (clickInfo == null) {
            return;
        }
        BaseDataReportUtils.getInstance().reportAdClickType(ad, clickInfo.getKey());
    }

    public static SDKConfiguration getConfiguration() {
        return sConfiguration;
    }

    static {
        SUPPORTED_TYPES.add(AD_SOURCE_ADMOB);
        SUPPORTED_TYPES.add(AD_SOURCE_ADMOB_H);
        SUPPORTED_TYPES.add(AD_SOURCE_ADMOB_M);
        SUPPORTED_TYPES.add(AD_SOURCE_ADMOB_INTERSTITIAL);
        SUPPORTED_TYPES.add(AD_SOURCE_ADMOB_INTERSTITIAL_H);
        SUPPORTED_TYPES.add(AD_SOURCE_ADMOB_INTERSTITIAL_M);
        SUPPORTED_TYPES.add(AD_SOURCE_ADMOB_BANNER);
        SUPPORTED_TYPES.add(AD_SOURCE_ADMOB_REWARD);
        SUPPORTED_TYPES.add(AD_SOURCE_MOPUB);
        SUPPORTED_TYPES.add(AD_SOURCE_MOPUB_INTERSTITIAL);
        SUPPORTED_TYPES.add(AD_SOURCE_VG_INTERSTITIAL);
        SUPPORTED_TYPES.add(AD_SOURCE_VG);
        SUPPORTED_TYPES.add(AD_SOURCE_PROPHET);
        SUPPORTED_TYPES.add(AD_SOURCE_APPLOVIN_BANNER);
        SUPPORTED_TYPES.add(AD_SOURCE_APPLOVIN_INTERSTITIAL);
        SUPPORTED_TYPES.add(AD_SOURCE_APPLOVIN_MREC);
        SUPPORTED_TYPES.add(AD_SOURCE_APPLOVIN_REWARD);
        SUPPORTED_TYPES.add(AD_SOURCE_ADCOLONY_BANNER);
        SUPPORTED_TYPES.add(AD_SOURCE_ADCOLONY_INTERSTITIAL);
        SUPPORTED_TYPES.add(AD_SOURCE_ADCOLONY_REWARD);

    }

    public void setDefaultBrustNum(int num) {
        defaultBrustNum = num;
    }

    public void setKeyBrustNum(int num) {
        keyBrustNum = num;
    }

    public int getBrustNum() {
        if (keyBrustNum > 0) {
            return keyBrustNum;
        }
        if (defaultBrustNum > 0) {
            return defaultBrustNum;
        } else {
            return 2;
        }
    }

    public static void setAdmobFree(boolean admobFree) {
        FuseAdLoader.mAdmobFree = admobFree;
    }

    public static boolean getAdmobFree() {
        return FuseAdLoader.mAdmobFree;
    }

    public static void setFanFree(boolean fanFree) {
        FuseAdLoader.mFanFree = fanFree;
    }


    public static void setBanInvalidAd(boolean banInvalidAd) {
        FuseAdLoader.mBanInvalidAd = banInvalidAd;
    }

    public static boolean isInited() {
        return mInited;
    }

}
