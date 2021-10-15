package miku.ad.adapters;

import android.content.Context;
import android.view.View;

import com.vungle.warren.AdConfig;
import com.vungle.warren.Banners;
import com.vungle.warren.LoadAdCallback;
import com.vungle.warren.PlayAdCallback;
import com.vungle.warren.Vungle;
import com.vungle.warren.VungleBanner;
import com.vungle.warren.error.VungleException;

import miku.ad.AdConstants;
import miku.ad.AdLog;
import miku.ad.AdViewBinder;

public class VGBannerAdapter extends AdAdapter {
    private VungleBanner mBannerAd;
//    private final static String PLACEMENT_ID = "BANNER-3999823";
    private final static String TAG = "VGBannerAdapter";
    private final String mKey;

    public VGBannerAdapter(Context context, String key,  String slot) {
        super(context, key, slot);
        this.mKey = key;

    }

    @Override
    public String getAdType() {
        return AdConstants.AdType.AD_SOURCE_VG;
    }

    @Override
    public View getAdView(Context context, AdViewBinder viewBinder) {
        registerViewForInteraction(mBannerAd);
        VGBannerAdapter.this.onAdShowed();
        return mBannerAd;
    }


    @Override
    public void loadAd(Context context, int num, IAdLoadListener listener) {
        mStartLoadedTime = System.currentTimeMillis();
        adListener = listener;

        AdLog.d("VGBanner loadAd " + listener);

        if (Vungle.isInitialized()) {
            Banners.loadBanner(mKey, AdConfig.AdSize.BANNER, new LoadAdCallback() {
                @Override
                public void onAdLoad(String placementReferenceId) {
                    // id is placementReferenceId

                    AdLog.d("VGBanner loadAd    " +placementReferenceId);

                    if (Vungle.isInitialized()) {
                        if (Banners.canPlayAd(mKey, AdConfig.AdSize.BANNER)) {
                            AdLog.d(TAG,"VGBanner canPlayAd " + mKey);
                            mBannerAd = Banners.getBanner(mKey, AdConfig.AdSize.BANNER,new PlayAdCallback() {

                                @Override
                                public void creativeId(String creativeId) {

                                }

                                @Override
                                public void onAdStart(String placementId) {

                                }

                                @Override
                                public void onAdEnd(String placementId, boolean completed, boolean isCTAClicked) {

                                }

                                @Override
                                public void onAdEnd(String placementId) {

                                }

                                @Override
                                public void onAdClick(String placementId) {

                                }

                                @Override
                                public void onAdRewarded(String placementId) {

                                }

                                @Override
                                public void onAdLeftApplication(String placementId) {

                                }

                                @Override
                                public void onError(String placementId, VungleException exception) {

                                }

                                @Override
                                public void onAdViewed(String placementId) {

                                }
                            } );

                            mLoadedTime = System.currentTimeMillis();
                            if (adListener != null) {
                                adListener.onAdLoaded(VGBannerAdapter.this);
                            }
                            stopMonitor();
                            if (mStartLoadedTime != 0) {
                            }
                            mStartLoadedTime = 0;

                        }else{
                            AdLog.d(TAG,"Vungle ad not playable for" + mKey);
                        }
                    }else{
                        AdLog.d(TAG,"Vungle SDK not initialized");
                    }

                }

                @Override
                public void onError(String placementReferenceId, VungleException e) {
                    // Load ad error occurred - e.getLocalizedMessage() contains error message
                    if (adListener != null) {
                        adListener.onError("ErrorCode: " + e.getMessage());
                    }
                    stopMonitor();
                    mStartLoadedTime = 0;
                    AdLog.d("VGBanner loadAd error  " +  e.getCause().getMessage());

                    VGBannerAdapter.this.onError(String.valueOf(e.getCause().getMessage()));
                }
            });
        }


        startMonitor();


    }

    @Override
    public void registerPrivacyIconView(View view) {

    }

    @Override
    public Object getAdObject() {
        return mBannerAd;
    }

    @Override
    protected void onTimeOut() {
        if (adListener != null) {
            adListener.onError("TIME_OUT");
        }
    }
}
