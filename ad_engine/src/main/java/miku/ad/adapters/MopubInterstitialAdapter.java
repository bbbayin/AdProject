package miku.ad.adapters;

import android.app.Activity;
import android.content.Context;
import android.view.View;

import com.mopub.mobileads.MoPubErrorCode;
import com.mopub.mobileads.MoPubInterstitial;

import miku.ad.AdConstants;
import miku.ad.AdLog;

public class MopubInterstitialAdapter extends AdAdapter implements MoPubInterstitial.InterstitialAdListener {
    private MoPubInterstitial mInterstitial;
    private final static String TAG = "MopubInterstitialAdapter";

    public MopubInterstitialAdapter(Context context, String key, String slot) {
        super(context, key, slot);
        LOAD_TIMEOUT = 20 * 1000;
    }

    @Override
    public boolean isInterstitialAd() {
        return true;
    }

    @Override
    public void onInterstitialLoaded(MoPubInterstitial interstitial) {
        mLoadedTime = System.currentTimeMillis();
        if (adListener != null) {
            adListener.onAdLoaded(this);
        }
        stopMonitor();
        AdLog.d("Mopub interstitial loaded");
        if (mStartLoadedTime != 0) {
        }
        mStartLoadedTime = 0;
        MopubInterstitialAdapter.this.onAdLoaded();
    }

    @Override
    public void onInterstitialFailed(MoPubInterstitial interstitial, MoPubErrorCode errorCode) {
        AdLog.d("Mopub interstitial load error: " + errorCode);
        if (adListener != null) {
            adListener.onError("" + errorCode);
        }
        stopMonitor();
        mStartLoadedTime = 0;
        MopubInterstitialAdapter.this.onError(String.valueOf(errorCode));
    }

    @Override
    public void onInterstitialShown(MoPubInterstitial interstitial) {
        MopubInterstitialAdapter.this.onAdShowed();
    }

    @Override
    public void onInterstitialClicked(MoPubInterstitial interstitial) {
        if (adListener != null) {
            adListener.onAdClicked(MopubInterstitialAdapter.this);
        }
        MopubInterstitialAdapter.this.onAdClicked();
        FuseAdLoader.reportAdClick(MopubInterstitialAdapter.this);
    }

    @Override
    public void onInterstitialDismissed(MoPubInterstitial interstitial) {
        if (adListener != null) {
            adListener.onAdClosed(MopubInterstitialAdapter.this);
        }
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
//        if (AdConstants.DEBUG) {
//            mKey = "24534e1901884e398f1253216226017e";
//        }
        if (!(context instanceof Activity)) {
            adListener.onError("No activity context found!");
            return;
        }
        mInterstitial = new MoPubInterstitial((Activity) context, mKey);
        mInterstitial.setInterstitialAdListener(this);
        mInterstitial.load();
        startMonitor();
    }

    @Override
    public void show() {
        if (mInterstitial.isReady()) {
            registerViewForInteraction(null);
            mInterstitial.show();
        }
    }

    @Override
    public String getAdType() {
        return AdConstants.AdType.AD_SOURCE_MOPUB_INTERSTITIAL;
    }

    @Override
    protected void onTimeOut() {
        if (adListener != null) {
            adListener.onError("TIME_OUT");
        }
    }
}
