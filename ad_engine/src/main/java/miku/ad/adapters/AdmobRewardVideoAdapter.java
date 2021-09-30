package miku.ad.adapters;

import android.app.Activity;
import android.content.Context;
import android.util.Log;
import android.view.View;

import androidx.annotation.NonNull;

import com.google.android.gms.ads.AdRequest;
import com.google.android.gms.ads.LoadAdError;
import com.google.android.gms.ads.OnUserEarnedRewardListener;
import com.google.android.gms.ads.rewarded.RewardItem;
import com.google.android.gms.ads.rewarded.RewardedAd;
import com.google.android.gms.ads.rewarded.RewardedAdLoadCallback;

import miku.ad.AdConstants;
import miku.ad.AdLog;
import miku.ad.AdUtils;

public class AdmobRewardVideoAdapter extends AdAdapter {
    private RewardedAd rewardAd;
    private final static String TAG = "AdmobRewardVideoAdapter";

    private String KEY ;
    boolean isLoading;
    private Activity activity ;

    public AdmobRewardVideoAdapter(Context context, String key, String slot) {
        super(context, key, slot);
        LOAD_TIMEOUT = 20 * 1000;
        KEY = key;
    }

    @Override
    public void registerPrivacyIconView(View view) {

    }

    @Override
    public void loadAd(final Context context, int num, IAdLoadListener listener) {
        mStartLoadedTime = System.currentTimeMillis();
        adListener = listener;
        if (listener == null) {
            AdLog.e("Not set listener!");
            return;
        }

        if (rewardAd == null) {
            isLoading = true;
            AdRequest adRequest = new AdRequest.Builder().build();
            RewardedAd.load(
                    context,
                    KEY,
                    adRequest,
                    new RewardedAdLoadCallback() {

                        @Override
                        public void onAdFailedToLoad(@NonNull LoadAdError loadAdError) {
                            // Handle the error.
                            rewardAd = null;
                            isLoading = false;

                            if (adListener != null) {
                                adListener.onError("ErrorCode: " + loadAdError.getCode());
                            }
                            stopMonitor();
                            mStartLoadedTime = 0;
                            AdmobRewardVideoAdapter.this.onError(String.valueOf(loadAdError.getCode()));

                        }

                        @Override
                        public void onAdLoaded(@NonNull RewardedAd rewardedAd) {
                            AdLog.d("onRewardedVideoAdLoaded");
                            rewardAd = rewardedAd;
                            isLoading = false;
                            activity = (Activity) context;
                            stopMonitor();

                            mLoadedTime = System.currentTimeMillis();
                            if (adListener != null) {
                                adListener.onAdLoaded(AdmobRewardVideoAdapter.this);
                            }
                            if (mStartLoadedTime != 0) {
                            }
                            mStartLoadedTime = 0;
                            AdmobRewardVideoAdapter.this.onAdLoaded();

                        }
                    });
        }
        
        
//        rewardedVideoAd = MobileAds.getRewardedVideoAdInstance(context);
//        rewardedVideoAd.setRewardedVideoAdListener(new RewardedVideoAdListener() {
//            @Override
//            public void onRewardedVideoAdLoaded() {
//                AdLog.d("onRewardedVideoAdLoaded");
//                stopMonitor();
//                mLoadedTime = System.currentTimeMillis();
//                if (adListener != null) {
//                    adListener.onAdLoaded(AdmobRewardVideoAdapter.this);
//                }
//                if (mStartLoadedTime != 0) {
//                }
//                mStartLoadedTime = 0;
//                AdmobRewardVideoAdapter.this.onAdLoaded();
//            }
//
//            @Override
//            public void onRewardedVideoAdOpened() {
//                AdLog.d("onRewardedVideoAdOpened");
//                AdmobRewardVideoAdapter.this.onAdShowed();
//            }
//
//            @Override
//            public void onRewardedVideoStarted() {
//                AdLog.d("onRewardedVideoStarted");
//            }
//
//            @Override
//            public void onRewardedVideoAdClosed() {
//                AdLog.d("onRewardedVideoAdClosed");
//                if (adListener != null) {
//                    adListener.onAdClosed(AdmobRewardVideoAdapter.this);
//                }
//            }
//
//            @Override
//            public void onRewarded(RewardItem rewardItem) {
//                AdLog.d("onRewarded " + rewardItem.getType());
//                if (adListener != null) {
//                    adListener.onRewarded(AdmobRewardVideoAdapter.this);
//                }
//                AdmobRewardVideoAdapter.this.onRewarded();
//            }
//
//            @Override
//            public void onRewardedVideoAdLeftApplication() {
//                AdLog.d("onRewardedVideoAdLeftApplication");
//                if (adListener != null) {
//                    adListener.onAdClicked(AdmobRewardVideoAdapter.this);
//                }
//            }
//
//            @Override
//            public void onRewardedVideoAdFailedToLoad(int i) {
//                if (adListener != null) {
//                    adListener.onError("ErrorCode: " + i);
//                }
//                stopMonitor();
//                mStartLoadedTime = 0;
//                AdmobRewardVideoAdapter.this.onError(String.valueOf(i));
//            }
//
//            @Override
//            public void onRewardedVideoCompleted() {
//                AdLog.d("onRewardedVideoCompleted");
//            }
//        });
//
//        if (AdConstants.DEBUG) {
//            String android_id = AdUtils.getAndroidID(context);
//            String deviceId = AdUtils.MD5(android_id).toUpperCase();
//            AdRequest request = new AdRequest.Builder().addTestDevice(deviceId).build();
//            rewardedVideoAd. loadAd(mKey, request);
//            boolean isTestDevice = request.isTestDevice(context);
//            AdLog.d("is Admob Test Device ? " + deviceId + " " + isTestDevice);
//        } else {
//            rewardedVideoAd.loadAd(mKey, new AdRequest.Builder().build());
//        }
        startMonitor();
    }

    @Override
    public boolean isInterstitialAd() {
        return true;
    }


    @Override
    public Object getAdObject() {
        return rewardAd;
    }

    @Override
    public String getAdType() {
        return AdConstants.AdType.AD_SOURCE_ADMOB_REWARD;
    }

    @Override
    protected void onTimeOut() {
        if (adListener != null) {
            adListener.onError("TIME_OUT");
        }
    }

    @Override
    public void show() {
        if (rewardAd != null
                && !isLoading) {
            registerViewForInteraction(null);

            rewardAd.show(
                    activity,
                    new OnUserEarnedRewardListener() {
                        @Override
                        public void onUserEarnedReward(@NonNull RewardItem rewardItem) {
                            // Handle the reward.
                            Log.d(TAG, "The user earned the reward.");
//                            int rewardAmount = rewardItem.getAmount();
//                            String rewardType = rewardItem.getType();
                        }
                    });

        }
    }
}
