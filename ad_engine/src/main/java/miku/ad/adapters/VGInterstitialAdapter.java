package miku.ad.adapters;

import android.content.Context;
import android.util.Log;
import android.view.View;
import com.vungle.warren.AdConfig;
import com.vungle.warren.LoadAdCallback;
import com.vungle.warren.PlayAdCallback;
import com.vungle.warren.Vungle;
import com.vungle.warren.VungleBanner;
import com.vungle.warren.error.VungleException;

import miku.ad.AdConstants;
import miku.ad.AdLog;
import miku.ad.AdUtils;


public class VGInterstitialAdapter extends AdAdapter {
    private final static String TAG = "VGInterstitialAdapter";
//    private final static String PLACMENT_ID = "INTERSTITIAL-0608258";
    private VungleBanner rawAd;
    private String mKey;
    private Context mContext;

    public VGInterstitialAdapter(Context context, String key, String slot) {
        super(context, key, slot);
        this.mKey = key;
        LOAD_TIMEOUT = 20 * 1000;
    }

    @Override
    public Object getAdObject() {
        return rawAd;
    }

    @Override
    public String getAdType() {
        return AdConstants.AdType.AD_SOURCE_VG_INTERSTITIAL;
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
        Log.d("VGInterstitial", "show");

        if (Vungle.isInitialized()) {
            if (Vungle.canPlayAd(mKey)){
                final AdConfig adConfig = getAdConfig();
                Vungle.playAd(mKey, adConfig, vunglePlayAdCallback);
            } else {
                AdLog.d(TAG,"Vungle ad not playable for" + mKey) ;
            }
        } else {
            AdLog.d(TAG,"Vungle SDK not initialized"  ) ;
        }

    }

    private final LoadAdCallback vungleLoadAdCallback = new LoadAdCallback() {
        @Override
        public void onAdLoad(String id) {
            // Ad has been successfully loaded for the placement
        }

        @Override
        public void onError(String id, VungleException exception) {
            // Ad has failed to load for the placement
        }
    };

    private final PlayAdCallback vunglePlayAdCallback = new PlayAdCallback() {
        @Override
        public void onAdStart(String id) {
            // Ad experience started
        }

        @Override
        public void onAdEnd(String placementId, boolean completed, boolean isCTAClicked) {

        }

        @Override
        public void onAdViewed(String id) {
            // Ad has rendered
        }

        @Override
        public void onAdEnd(String id) {
            // Ad experience ended
        }

        @Override
        public void onAdClick(String id) {
            // User clicked on ad
            AdLog.d(TAG,"onAdClick");
            AdUtils.setVungleClickNum(mContext);
            VGInterstitialAdapter.this.onAdClicked();
            FuseAdLoader.reportAdClick(VGInterstitialAdapter.this);
        }

        @Override
        public void onAdRewarded(String id) {
            // User earned reward for watching an rewarded ad
        }

        @Override
        public void onAdLeftApplication(String id) {
            // User has left app during an ad experience
        }

        @Override
        public void creativeId(String creativeId) {
            // Vungle creative ID to be displayed
        }

        @Override
        public void onError(String id, VungleException exception) {
            // Ad failed to play
        }
    };

    @Override
    public void loadAd(final Context context, int num, IAdLoadListener listener) {
        AdLog.d("VGInterstitial loadAd    " + listener);
        mStartLoadedTime = System.currentTimeMillis();
        adListener = listener;
        mContext = context;
        if (listener == null) {
            AdLog.e("listener is null!!");
            return;
        }
        if (Vungle.isInitialized()) {
            Vungle.loadAd(mKey, new LoadAdCallback() {
                @Override
                public void onAdLoad(String placementReferenceId) {

                    mLoadedTime = System.currentTimeMillis();
                    if (adListener != null) {
                        adListener.onAdLoaded(VGInterstitialAdapter.this);
                    }
                    stopMonitor();
                    if (mStartLoadedTime != 0) {
                    }
                    mStartLoadedTime = 0;
                    AdLog.d("VGInterstitial loadAd    " +placementReferenceId);
                }

                @Override
                public void onError(String placementReferenceId, VungleException exception) {
                    AdLog.d("VGInterstitial loadAd error   " +exception.getMessage());
                }
            });
        }



        startMonitor();
    }
    private AdConfig getAdConfig() {
        AdConfig adConfig = new AdConfig();

        adConfig.setAdOrientation(AdConfig.MATCH_VIDEO);
        adConfig.setMuted(true);

        return adConfig;
    }

    @Override
    protected void onTimeOut() {
        if (adListener != null) {
            adListener.onError("TIME_OUT");
        }
    }
}
