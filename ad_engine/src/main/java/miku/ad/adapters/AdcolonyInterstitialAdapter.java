package miku.ad.adapters;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.content.Context;
import android.content.ContextWrapper;
import android.util.Log;
import android.view.View;

import androidx.annotation.NonNull;

import com.adcolony.sdk.AdColony;
import com.adcolony.sdk.AdColonyAdOptions;
import com.adcolony.sdk.AdColonyAppOptions;
import com.adcolony.sdk.AdColonyInterstitial;
import com.adcolony.sdk.AdColonyInterstitialListener;
import com.adcolony.sdk.AdColonyZone;

import miku.ad.AdConstants;
import miku.ad.AdLog;
import miku.ad.AdUtils;

public class AdcolonyInterstitialAdapter extends AdAdapter {
    private final static String TAG = "ApplovinInterstitialAdapter";
    private String mKey;

    private AdColonyInterstitial adInterstitial;
    private AdColonyInterstitialListener interstitialListener;
    private AdColonyAdOptions adOptions;

    public AdcolonyInterstitialAdapter(Context context, String key, String slot) {
        super(context, key, slot);
        this.mKey = key;
        LOAD_TIMEOUT = 20 * 1000;
    }

    @Override
    public Object getAdObject() {
        return adInterstitial;
    }

    @Override
    public String getAdType() {
        return AdConstants.AdType.AD_SOURCE_ADCOLONY_INTERSTITIAL;
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
        AdLog.d(AdcolonyInterstitialAdapter.TAG, "show" );
        if (adInterstitial != null ) {
            adInterstitial.show();
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

    @SuppressLint("LongLogTag")
    @Override
    public void loadAd(final Context context, int num, IAdLoadListener listener) {
        Log.d("fuseAdLoader", "load adcolony  interstitialAd");
        mStartLoadedTime = System.currentTimeMillis();
        adListener = listener;
        if (listener == null) {
            AdLog.e("listener is null!!");
            return;
        }

        Activity activity = findActivity(context);

        if (activity instanceof Activity) {

            // Construct optional app options object to be sent with configure
            AdColonyAppOptions appOptions = new AdColonyAppOptions()
                    .setUserID("unique_user_id")
                    .setKeepScreenOn(true);

            // Configure AdColony in your launching Activity's onCreate() method so that cached ads can
            // be available as soon as possible.
            AdColony.configure(activity, appOptions, FuseAdLoader.getConfiguration().getAdcolonyId(), mKey);

            // Ad specific options to be sent with request
            adOptions = new AdColonyAdOptions();

            interstitialListener = new AdColonyInterstitialListener() {
                @Override
                public void onRequestFilled(AdColonyInterstitial ad) {
                    // Ad passed back in request filled callback, ad can now be shown
                    AdLog.i(AdcolonyInterstitialAdapter.TAG, "onRequestFilled");
                    AdLog.d("fuseAdLoader", "onRequestFilled");

                    adInterstitial = ad;

                    mLoadedTime = System.currentTimeMillis();
                    if (adListener != null) {
                        adListener.onAdLoaded(AdcolonyInterstitialAdapter.this);
                    }
                    stopMonitor();
                    if (mStartLoadedTime != 0) {
                    }
                    mStartLoadedTime = 0;

                    AdcolonyInterstitialAdapter.this.onAdLoaded();
                }

                @Override
                public void onRequestNotFilled(AdColonyZone zone) {
                    // Ad request was not filled
                    AdLog.d(AdcolonyInterstitialAdapter.TAG, "onRequestNotFilled" );

                }

                @Override
                public void onOpened(AdColonyInterstitial ad) {
                    // Ad opened, reset UI to reflect state change
                    AdLog.d(TAG, "onOpened");
                    AdUtils.setAdcolonyClickNum(context);
                    AdcolonyInterstitialAdapter.this.onAdClicked();
                    FuseAdLoader.reportAdClick(AdcolonyInterstitialAdapter.this);
                }

                @Override
                public void onExpiring(AdColonyInterstitial ad) {
                    // Request a new ad if ad is expiring
                    AdColony.requestInterstitial(mKey, this, adOptions);
                    Log.d(TAG, "onExpiring");
                }
            };



            AdColony.requestInterstitial(mKey, interstitialListener, adOptions);
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
