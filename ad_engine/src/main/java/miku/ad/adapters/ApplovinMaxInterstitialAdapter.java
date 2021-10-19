package miku.ad.adapters;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.content.Context;
import android.content.ContextWrapper;
import android.nfc.Tag;
import android.util.Log;
import android.view.View;

import androidx.annotation.NonNull;

import com.applovin.mediation.MaxAd;
import com.applovin.mediation.MaxAdListener;
import com.applovin.mediation.MaxError;
import com.applovin.mediation.ads.MaxInterstitialAd;

import miku.ad.AdConstants;
import miku.ad.AdLog;
import miku.ad.AdUtils;

public class ApplovinMaxInterstitialAdapter extends AdAdapter {
    private final static String TAG = "ApplovinMaxInterstitialAdapter";
    private MaxInterstitialAd interstitialAd;
    private String mKey;

    public ApplovinMaxInterstitialAdapter(Context context, String key, String slot) {
        super(context, key, slot);
        this.mKey = key;
        LOAD_TIMEOUT = 20 * 1000;
    }

    @Override
    public Object getAdObject() {
        return interstitialAd;
    }

    @Override
    public String getAdType() {
        return AdConstants.AdType.AD_SOURCE_APPLOVIN_INTERSTITIAL;
    }

    @Override
    public void registerPrivacyIconView(View view) {

    }

    @Override
    public boolean isInterstitialAd() {
        return true;
    }

    @Override
    public void show() {
        registerViewForInteraction(null);
        Log.d("fuseAdLoader", "show");

        if (interstitialAd != null && interstitialAd.isReady()) {
            interstitialAd.showAd();
        }

    }

    private static Activity findActivity(@NonNull Context context) {
        if (context instanceof Activity) {
            return (Activity) context;
        } else if (context instanceof ContextWrapper) {
            return findActivity(((ContextWrapper) context).getBaseContext());
        } else {
            return null;
        }
    }

    @Override
    public void loadAd(final Context context, int num, IAdLoadListener listener) {
        Log.d("fuseAdLoader", "load interstitialAd");
        mStartLoadedTime = System.currentTimeMillis();
        adListener = listener;
        if (listener == null) {
            AdLog.e("listener is null!!");
            return;
        }

        Activity activity = findActivity(context);

        if (activity instanceof Activity) {
            interstitialAd = new MaxInterstitialAd(mKey, activity);
            interstitialAd.setListener(new MaxAdListener() {

                @SuppressLint("LongLogTag")
                @Override
                public void onAdLoaded(MaxAd ad) {
                    AdLog.i(ApplovinMaxInterstitialAdapter.TAG, "onAdLoaded");
                    AdLog.d("fuseAdLoader", "onLoaded");

                    mLoadedTime = System.currentTimeMillis();
                    if (adListener != null) {
                        adListener.onAdLoaded(ApplovinMaxInterstitialAdapter.this);
                    }
                    stopMonitor();
                    if (mStartLoadedTime != 0) {
                    }
                    mStartLoadedTime = 0;
                    ApplovinMaxInterstitialAdapter.this.onAdLoaded();
                }

                @Override
                public void onAdDisplayed(MaxAd ad) {

                }

                @Override
                public void onAdHidden(MaxAd ad) {
                    interstitialAd.loadAd();
                }

                @Override
                public void onAdClicked(MaxAd ad) {
                    AdLog.d(TAG,"onAdClicked");
                    AdUtils.setApplovinClickNum(context);
                    ApplovinMaxInterstitialAdapter.this.onAdClicked();
                    FuseAdLoader.reportAdClick(ApplovinMaxInterstitialAdapter.this);
                }

                @Override
                public void onAdLoadFailed(String adUnitId, MaxError error) {
                    AdLog.d(ApplovinMaxInterstitialAdapter.TAG, "onAdLoadFailed" + adUnitId);
                    interstitialAd.loadAd();
                }

                @Override
                public void onAdDisplayFailed(MaxAd ad, MaxError error) {

                }
            });

            interstitialAd.loadAd();

            startMonitor();
        }
    }

    @Override
    protected void onTimeOut() {
        if (adListener != null) {
            adListener.onError("TIME_OUT");
        }
    }
}
