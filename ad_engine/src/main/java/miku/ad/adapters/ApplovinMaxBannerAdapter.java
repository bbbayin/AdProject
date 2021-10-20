package miku.ad.adapters;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.content.Context;
import android.content.ContextWrapper;
import android.util.Log;
import android.view.View;

import androidx.annotation.NonNull;

import com.applovin.mediation.MaxAd;
import com.applovin.mediation.MaxAdRevenueListener;
import com.applovin.mediation.MaxAdViewAdListener;
import com.applovin.mediation.MaxError;
import com.applovin.mediation.ads.MaxAdView;

import miku.ad.AdConstants;
import miku.ad.AdLog;
import miku.ad.AdUtils;
import miku.ad.AdViewBinder;

public class ApplovinMaxBannerAdapter extends AdAdapter {
    private final static String TAG = "ApplovinMaxBannerAdapter";
    private MaxAdView appAd;
    private String mKey;

    public ApplovinMaxBannerAdapter(Context context, String key, String slot) {
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
        ApplovinMaxBannerAdapter.this.onAdShowed();
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
        Log.d("fuseAdLoader", "load interstitialAd");
        mStartLoadedTime = System.currentTimeMillis();
        adListener = listener;
        if (listener == null) {
            AdLog.e("listener is null!!");
            return;
        }

        Activity activity = findActivity(context);

        if(activity instanceof  Activity){
            appAd = new MaxAdView(mKey,activity);

            appAd.setListener( new MaxAdViewAdListener(){

                @Override
                public void onAdExpanded(MaxAd ad) {

                }

                @Override
                public void onAdCollapsed(MaxAd ad) {

                }

                @SuppressLint("LongLogTag")
                @Override
                public void onAdLoaded(MaxAd ad) {
                    AdLog.i(ApplovinMaxBannerAdapter.TAG, "onAdLoaded");
                    AdLog.d("fuseAdLoader", "onLoaded");

                    mLoadedTime = System.currentTimeMillis();
                    if (adListener != null) {
                        adListener.onAdLoaded(ApplovinMaxBannerAdapter.this);
                    }
                    stopMonitor();
                    if (mStartLoadedTime != 0) {
                    }
                    mStartLoadedTime = 0;
                    ApplovinMaxBannerAdapter.this.onAdLoaded();
                }

                @Override
                public void onAdDisplayed(MaxAd ad) {

                }

                @Override
                public void onAdHidden(MaxAd ad) {

                }

                @Override
                public void onAdClicked(MaxAd ad) {
                    AdUtils.setApplovinBannerClickNum(context);
                    ApplovinMaxBannerAdapter.this.onAdClicked();
                    FuseAdLoader.reportAdClick(ApplovinMaxBannerAdapter.this);
                }

                @Override
                public void onAdLoadFailed(String adUnitId, MaxError error) {
                    AdLog.d(ApplovinMaxBannerAdapter.TAG, "onAdLoadFailed " + adUnitId + "  " + error.getCode()+error.getMessage());
                    stopMonitor();
                    mStartLoadedTime = 0;
                    ApplovinMaxBannerAdapter.this.onError(error.getCode() + error.getMessage());
                }

                @Override
                public void onAdDisplayFailed(MaxAd ad, MaxError error) {
                    AdLog.d(ApplovinMaxBannerAdapter.TAG, "onAdDisplayFailed" + error.getCode()+error.getMessage());

                }

            } );
            appAd.setRevenueListener(new MaxAdRevenueListener() {
                @Override
                public void onAdRevenuePaid(MaxAd ad) {

                }
            });

            appAd.loadAd();
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
