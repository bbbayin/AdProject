package miku.ad.adapters;

import android.content.Context;
import android.view.View;

import com.google.android.gms.ads.AdRequest;
import com.google.android.gms.ads.MobileAds;
import com.google.android.gms.ads.reward.RewardItem;
import com.google.android.gms.ads.reward.RewardedVideoAd;
import com.google.android.gms.ads.reward.RewardedVideoAdListener;

import miku.ad.AdConstants;
import miku.ad.AdLog;
import miku.ad.AdUtils;

public class AdmobRewardVideoAdapter extends AdAdapter {
    private RewardedVideoAd rewardedVideoAd;
    private final static String TAG = "AdmobRewardVideoAdapter";

    public AdmobRewardVideoAdapter(Context context, String key, String slot) {
        super(context, key, slot);
        LOAD_TIMEOUT = 20 * 1000;
    }

    @Override
    public void registerPrivacyIconView(View view) {

    }

    @Override
    public void loadAd(Context context, int num, IAdLoadListener listener) {
        mStartLoadedTime = System.currentTimeMillis();
        adListener = listener;
        if (listener == null) {
            AdLog.e("Not set listener!");
            return;
        }
        rewardedVideoAd = MobileAds.getRewardedVideoAdInstance(context);
        rewardedVideoAd.setRewardedVideoAdListener(new RewardedVideoAdListener() {
            @Override
            public void onRewardedVideoAdLoaded() {
                AdLog.d("onRewardedVideoAdLoaded");
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

            @Override
            public void onRewardedVideoAdOpened() {
                AdLog.d("onRewardedVideoAdOpened");
                AdmobRewardVideoAdapter.this.onAdShowed();
            }

            @Override
            public void onRewardedVideoStarted() {
                AdLog.d("onRewardedVideoStarted");
            }

            @Override
            public void onRewardedVideoAdClosed() {
                AdLog.d("onRewardedVideoAdClosed");
                if (adListener != null) {
                    adListener.onAdClosed(AdmobRewardVideoAdapter.this);
                }
            }

            @Override
            public void onRewarded(RewardItem rewardItem) {
                AdLog.d("onRewarded " + rewardItem.getType());
                if (adListener != null) {
                    adListener.onRewarded(AdmobRewardVideoAdapter.this);
                }
                AdmobRewardVideoAdapter.this.onRewarded();
            }

            @Override
            public void onRewardedVideoAdLeftApplication() {
                AdLog.d("onRewardedVideoAdLeftApplication");
                if (adListener != null) {
                    adListener.onAdClicked(AdmobRewardVideoAdapter.this);
                }
            }

            @Override
            public void onRewardedVideoAdFailedToLoad(int i) {
                if (adListener != null) {
                    adListener.onError("ErrorCode: " + i);
                }
                stopMonitor();
                mStartLoadedTime = 0;
                AdmobRewardVideoAdapter.this.onError(String.valueOf(i));
            }

            @Override
            public void onRewardedVideoCompleted() {
                AdLog.d("onRewardedVideoCompleted");
            }
        });

        if (AdConstants.DEBUG) {
            String android_id = AdUtils.getAndroidID(context);
            String deviceId = AdUtils.MD5(android_id).toUpperCase();
            AdRequest request = new AdRequest.Builder().addTestDevice(deviceId).build();
            rewardedVideoAd.loadAd(mKey, request);
            boolean isTestDevice = request.isTestDevice(context);
            AdLog.d("is Admob Test Device ? " + deviceId + " " + isTestDevice);
        } else {
            rewardedVideoAd.loadAd(mKey, new AdRequest.Builder().build());
        }
        startMonitor();
    }

    @Override
    public boolean isInterstitialAd() {
        return true;
    }


    @Override
    public Object getAdObject() {
        return rewardedVideoAd;
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
        if (rewardedVideoAd != null
                && rewardedVideoAd.isLoaded()) {
            registerViewForInteraction(null);
            rewardedVideoAd.show();
        }
    }
}
