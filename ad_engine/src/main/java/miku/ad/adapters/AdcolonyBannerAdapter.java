package miku.ad.adapters;

import android.app.Activity;
import android.content.Context;
import android.content.ContextWrapper;
import android.util.Log;
import android.view.View;

import androidx.annotation.NonNull;

import com.adcolony.sdk.AdColony;
import com.adcolony.sdk.AdColonyAdOptions;
import com.adcolony.sdk.AdColonyAdSize;
import com.adcolony.sdk.AdColonyAdView;
import com.adcolony.sdk.AdColonyAdViewListener;
import com.adcolony.sdk.AdColonyAppOptions;
import com.adcolony.sdk.AdColonyZone;

import miku.ad.AdConstants;
import miku.ad.AdLog;
import miku.ad.AdUtils;
import miku.ad.AdViewBinder;

public class AdcolonyBannerAdapter extends AdAdapter {
    private final static String TAG = "AdcolonyBannerAdapter";

    private String mKey;

    private AdColonyAdViewListener adViewListener;
    private AdColonyAdOptions adOptions;
    private AdColonyAdView adView;


    public AdcolonyBannerAdapter(Context context, String key, String slot) {
        super(context, key, slot);
        this.mKey = key;
        LOAD_TIMEOUT = 20 * 1000;
    }

    @Override
    public Object getAdObject() {
        return adView;
    }

    @Override
    public String getAdType() {
        return AdConstants.AdType.AD_SOURCE_ADCOLONY_BANNER;
    }

    @Override
    public void registerPrivacyIconView(View view) {

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
    public View getAdView(Context context, AdViewBinder viewBinder) {
        registerViewForInteraction(adView);
        AdcolonyBannerAdapter.this.onAdShowed();
        return adView;
    }

    @Override
    public void loadAd(final Context context, int num, IAdLoadListener listener) {
        Log.d("fuseAdLoader", "load AdcolonyBannerAdapter");
        mStartLoadedTime = System.currentTimeMillis();
        adListener = listener;
        if (listener == null) {
            AdLog.e("listener is null!!");
            return;
        }

        Activity activity = findActivity(context);

        if (activity instanceof Activity) {


            // Construct optional app options object to be sent with configure
            AdColonyAppOptions appOptions = new AdColonyAppOptions();

            // Configure AdColony in your launching Activity's onCreate() method so that cached ads can
            // be available as soon as possible.
            AdColony.configure(activity, appOptions, FuseAdLoader.getConfiguration().getAdcolonyId(), mKey);

            adViewListener = new AdColonyAdViewListener() {
                @Override
                public void onRequestFilled(AdColonyAdView adColonyAdView) {
                    Log.d(TAG, "onRequestFilled");

                    AdLog.i(AdcolonyBannerAdapter.TAG, "onRequestFilled");
                    AdLog.d("fuseAdLoader", "onRequestFilled");

                    adView = adColonyAdView;

                    mLoadedTime = System.currentTimeMillis();
                    if (adListener != null) {
                        adListener.onAdLoaded(AdcolonyBannerAdapter.this);
                    }
                    stopMonitor();
                    if (mStartLoadedTime != 0) {
                    }
                    mStartLoadedTime = 0;
                    AdcolonyBannerAdapter.this.onAdLoaded();

                }

                @Override
                public void onRequestNotFilled(AdColonyZone zone) {
                    super.onRequestNotFilled(zone);
                    AdLog.d(TAG, "onAdLoadFailed" + zone);
                    stopMonitor();
                    mStartLoadedTime = 0;

                }

                @Override
                public void onOpened(AdColonyAdView ad) {
                    super.onOpened(ad);
                    Log.d(TAG, "onOpened");
                }

                @Override
                public void onClosed(AdColonyAdView ad) {
                    super.onClosed(ad);
                    Log.d(TAG, "onClosed");
                }

                @Override
                public void onClicked(AdColonyAdView ad) {
                    super.onClicked(ad);
                    Log.d(TAG, "onClicked");

                    AdUtils.setAdcolonyBannerClickNum(context);
                    AdcolonyBannerAdapter.this.onAdClicked();
                    FuseAdLoader.reportAdClick(AdcolonyBannerAdapter.this);

                }

                @Override
                public void onLeftApplication(AdColonyAdView ad) {
                    super.onLeftApplication(ad);
                    Log.d(TAG, "onLeftApplication");
                }
            };

            // Optional Ad specific options to be sent with request
            adOptions = new AdColonyAdOptions();

            //Request Ad
            AdColony.requestAdView(mKey, adViewListener, AdColonyAdSize.BANNER, adOptions);

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
