package miku.ad.adapters;


import android.app.Activity;
import android.content.Context;
import android.content.ContextWrapper;
import android.util.Log;
import android.view.View;

import androidx.annotation.NonNull;
import androidx.core.view.ViewCompat;

import com.applovin.adview.AppLovinAdView;
import com.applovin.adview.AppLovinAdViewDisplayErrorCode;
import com.applovin.adview.AppLovinAdViewEventListener;
import com.applovin.sdk.AppLovinAd;
import com.applovin.sdk.AppLovinAdClickListener;
import com.applovin.sdk.AppLovinAdDisplayListener;
import com.applovin.sdk.AppLovinAdLoadListener;
import com.applovin.sdk.AppLovinAdSize;

import miku.ad.AdConstants;
import miku.ad.AdLog;
import miku.ad.AdViewBinder;

public class ApplovinBannerAdapter extends AdAdapter {
    private final static String TAG = "ApplovinBannerAdapter";
    private AppLovinAdView appAd;
    private String mKey;

    public ApplovinBannerAdapter(Context context, String key, String slot) {
        super(context, key, slot);
        this.mKey = key;
        LOAD_TIMEOUT = 20 * 1000;
    }

    @Override
    public Object getAdObject() {
        return appAd;
    }

    @Override
    public String getAdType() {
        return AdConstants.AdType.AD_SOURCE_APPLOVIN_BANNER;
    }

    @Override
    public void registerPrivacyIconView(View view) {

    }
    @Override
    public View getAdView(Context context, AdViewBinder viewBinder) {
        registerViewForInteraction(appAd);
        ApplovinBannerAdapter.this.onAdShowed();
        return appAd;
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
        Log.d("fuseAdLoader", "load ApplovinBannerAdapter");
        mStartLoadedTime = System.currentTimeMillis();
        adListener = listener;
        if (listener == null) {
            AdLog.e("listener is null!!");
            return;
        }

        Activity activity = findActivity(context);

        if(activity instanceof  Activity){
            appAd = new AppLovinAdView( AppLovinAdSize.BANNER, mKey, activity );

            appAd.setAdLoadListener(new AppLovinAdLoadListener() {
                @Override
                public void adReceived(AppLovinAd ad) {
                    AdLog.i(ApplovinBannerAdapter.TAG, "adReceived");
                    AdLog.d("fuseAdLoader", "adReceived");

                    mLoadedTime = System.currentTimeMillis();
                    if (adListener != null) {
                        adListener.onAdLoaded(ApplovinBannerAdapter.this);
                    }
                    stopMonitor();
                    if (mStartLoadedTime != 0) {
                    }
                    mStartLoadedTime = 0;
                    ApplovinBannerAdapter.this.onAdLoaded();
                }

                @Override
                public void failedToReceiveAd(int errorCode) {
                    AdLog.d(ApplovinBannerAdapter.TAG, "failedToReceiveAd " + errorCode);
                    stopMonitor();
                    mStartLoadedTime = 0;
                }
            });

            appAd.setAdDisplayListener(new AppLovinAdDisplayListener() {
                @Override
                public void adDisplayed(AppLovinAd ad) {

                }

                @Override
                public void adHidden(AppLovinAd ad) {

                }
            });


            appAd.setAdViewEventListener(new AppLovinAdViewEventListener() {
                @Override
                public void adOpenedFullscreen(AppLovinAd ad, AppLovinAdView adView) {

                }

                @Override
                public void adClosedFullscreen(AppLovinAd ad, AppLovinAdView adView) {

                }

                @Override
                public void adLeftApplication(AppLovinAd ad, AppLovinAdView adView) {

                }

                @Override
                public void adFailedToDisplay(AppLovinAd ad, AppLovinAdView adView, AppLovinAdViewDisplayErrorCode code) {

                }
            });

            appAd.setAdClickListener(new AppLovinAdClickListener() {
                @Override
                public void adClicked(AppLovinAd ad) {

                }
            });

            appAd.setId( ViewCompat.generateViewId() );
            appAd.loadNextAd();

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
