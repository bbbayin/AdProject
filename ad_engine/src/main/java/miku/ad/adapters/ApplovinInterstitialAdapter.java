package miku.ad.adapters;

import android.app.Activity;
import android.content.Context;
import android.content.ContextWrapper;
import android.util.Log;
import android.view.View;

import androidx.annotation.NonNull;

import com.applovin.adview.AppLovinInterstitialAd;
import com.applovin.adview.AppLovinInterstitialAdDialog;
import com.applovin.sdk.AppLovinAd;
import com.applovin.sdk.AppLovinAdClickListener;
import com.applovin.sdk.AppLovinAdDisplayListener;
import com.applovin.sdk.AppLovinAdLoadListener;
import com.applovin.sdk.AppLovinAdVideoPlaybackListener;
import com.applovin.sdk.AppLovinSdk;

import miku.ad.AdConstants;
import miku.ad.AdLog;

public class ApplovinInterstitialAdapter extends AdAdapter {
    private final static String TAG = "ApplovinInterstitialAdapter";
//    private String zoneId= "6c967b9dbf1370dd";
    private AppLovinAd currentAd;
    private AppLovinInterstitialAdDialog interstitialAd;
    private String mKey;
    private AppLovinAdLoadListener lovinAdLoadListener;

    public ApplovinInterstitialAdapter(Context context, String key, String slot) {
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

        if ( currentAd != null && interstitialAd != null ){
            interstitialAd.showAndRender(currentAd);
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

    private  void getListener(){
        lovinAdLoadListener =   new AppLovinAdLoadListener() {
            @Override
            public void adReceived(AppLovinAd ad) {
                currentAd = ad;

                AdLog.i(ApplovinInterstitialAdapter.TAG, "adReceived");
                AdLog.d("fuseAdLoader", "adReceived");

                mLoadedTime = System.currentTimeMillis();
                if (adListener != null) {
                    adListener.onAdLoaded(ApplovinInterstitialAdapter.this);
                }
                stopMonitor();
                if (mStartLoadedTime != 0) {
                }
                mStartLoadedTime = 0;
                ApplovinInterstitialAdapter.this.onAdLoaded();
            }

            @Override
            public void failedToReceiveAd(int errorCode) {
                AdLog.d(ApplovinInterstitialAdapter.TAG, "failedToReceiveAd " + errorCode);
            }
        };

    }

    @Override
    public void loadAd(final Context context, int num, IAdLoadListener listener) {
        Log.d("fuseAdLoader", "load ApplovinInterstitialAdapter");
        mStartLoadedTime = System.currentTimeMillis();
        adListener = listener;
        if (listener == null) {
            AdLog.e("listener is null!!");
            return;
        }

        Activity activity = findActivity(context);

        if (activity instanceof Activity) {
            interstitialAd = AppLovinInterstitialAd.create( AppLovinSdk.getInstance( activity ), activity );
            getListener();
            interstitialAd.setAdLoadListener(lovinAdLoadListener);
            interstitialAd.setAdDisplayListener(new AppLovinAdDisplayListener() {
                @Override
                public void adDisplayed(AppLovinAd ad) {

                }

                @Override
                public void adHidden(AppLovinAd ad) {

                }
            });

            interstitialAd.setAdClickListener(new AppLovinAdClickListener() {
                @Override
                public void adClicked(AppLovinAd ad) {

                }
            });

            interstitialAd.setAdVideoPlaybackListener(new AppLovinAdVideoPlaybackListener() {
                @Override
                public void videoPlaybackBegan(AppLovinAd ad) {

                }

                @Override
                public void videoPlaybackEnded(AppLovinAd ad, double percentViewed, boolean fullyWatched) {

                }
            });

            AppLovinSdk.getInstance(context).getAdService().loadNextAdForZoneId( mKey, lovinAdLoadListener );

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
