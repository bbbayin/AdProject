package miku.ad.adapters;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.content.Context;
import android.util.Log;
import android.view.View;

import androidx.annotation.NonNull;

import com.google.android.gms.ads.AdError;
import com.google.android.gms.ads.AdListener;
import com.google.android.gms.ads.AdRequest;
import com.google.android.gms.ads.FullScreenContentCallback;
import com.google.android.gms.ads.LoadAdError;
import com.google.android.gms.ads.interstitial.InterstitialAd;
import com.google.android.gms.ads.interstitial.InterstitialAdLoadCallback;

import miku.ad.AdConstants;
import miku.ad.AdLog;
import miku.ad.AdUtils;


public class AdmobInterstitialAdapter extends AdAdapter {
    private final static String TAG = "AdmobInterstitialAdapter";
    private InterstitialAd rawAd;
    private String key;
    private Activity activity ;

    public AdmobInterstitialAdapter(Context context, String key, String slot) {
        super(context, key, slot);
        this.key = key;
        LOAD_TIMEOUT = 20 * 1000;
    }

    @Override
    public Object getAdObject() {
        return rawAd;
    }

    @Override
    public String getAdType() {
        return AdConstants.AdType.AD_SOURCE_ADMOB_INTERSTITIAL;
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

        if(rawAd!=null  ){
            rawAd.show(activity);
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

        AdRequest adRequest = new AdRequest.Builder().build();
        InterstitialAd.load(
                context,
                key,
                adRequest,
                new InterstitialAdLoadCallback() {

                    @SuppressLint("LongLogTag")
                    @Override
                    public void onAdLoaded(@NonNull InterstitialAd interstitialAd) {
                        // The mInterstitialAd reference will be null until
                        // an ad is loaded.
                        rawAd = interstitialAd;
                        activity = (Activity) context;

                        Log.i(AdmobInterstitialAdapter.TAG, "onAdLoaded");
                        Log.d("fuseAdLoader", "onLoaded");

                        mLoadedTime = System.currentTimeMillis();
                        if (adListener != null) {
                            adListener.onAdLoaded(AdmobInterstitialAdapter.this);
                        }
                        stopMonitor();
                        if (mStartLoadedTime != 0) {
                        }
                        mStartLoadedTime = 0;
                        AdmobInterstitialAdapter.this.onAdLoaded();

                        interstitialAd.setFullScreenContentCallback(
                                new FullScreenContentCallback() {
                                    @Override
                                    public void onAdDismissedFullScreenContent() {
                                        // Called when fullscreen content is dismissed.
                                        // Make sure to set your reference to null so you don't
                                        // show it a second time.
                                        rawAd = null;
                                        Log.d("TAG", "The ad was dismissed.");
                                    }

                                    @Override
                                    public void onAdFailedToShowFullScreenContent(AdError adError) {
                                        // Called when fullscreen content failed to show.
                                        // Make sure to set your reference to null so you don't
                                        // show it a second time.
                                        rawAd = null;
                                        Log.d("TAG", "The ad failed to show.");
                                    }

                                    @Override
                                    public void onAdShowedFullScreenContent() {
                                        // Called when fullscreen content is shown.
                                        Log.d("TAG", "The ad was shown.");
                                    }
                                });
                    }



                    @SuppressLint("LongLogTag")
                    @Override
                    public void onAdFailedToLoad(@NonNull LoadAdError loadAdError) {
                        // Handle the error
                        Log.i(TAG, loadAdError.getMessage());
//                        rawAd = null;

                        if (adListener != null) {
                            adListener.onError("ErrorCode: " + loadAdError.getCode());
                        }

                        stopMonitor();
                        mStartLoadedTime = 0;
                        AdmobInterstitialAdapter.this.onError(String.valueOf(loadAdError.getCode()));
                        dealErrorMessage(AdmobInterstitialAdapter.this, loadAdError.getCode());

                    }
                });

//        rawAd = new InterstitialAd(context);
//        rawAd.setAdUnitId(key);
//        rawAd.setAdListener(new AdListener() {
//            @Override
//            public void onAdFailedToLoad(int i) {
//                super.onAdFailedToLoad(i);
//                if (adListener != null) {
//                    adListener.onError("ErrorCode: " + i);
//                }
//                stopMonitor();
//                mStartLoadedTime = 0;
//                AdmobInterstitialAdapter.this.onError(String.valueOf(i));
//                dealErrorMessage(AdmobInterstitialAdapter.this, i);
//            }
//
//            @Override
//            public void onAdLoaded() {
//                super.onAdLoaded();
//                Log.d("fuseAdLoader", "onLoaded");
//                mLoadedTime = System.currentTimeMillis();
//                if (adListener != null) {
//                    adListener.onAdLoaded(AdmobInterstitialAdapter.this);
//                }
//                stopMonitor();
//                if (mStartLoadedTime != 0) {
//                }
//                mStartLoadedTime = 0;
//                AdmobInterstitialAdapter.this.onAdLoaded();
//            }
//
//            @Override
//            public void onAdClicked() {
//                super.onAdClicked();
//            }
//
//            @Override
//            public void onAdOpened() {
//                super.onAdOpened();
//                if (adListener != null) {
//                    adListener.onAdClicked(AdmobInterstitialAdapter.this);
//                }
//            }
//
//            @Override
//            public void onAdLeftApplication() {
//                super.onAdLeftApplication();
//                AdmobInterstitialAdapter.this.onAdClicked();
//                FuseAdLoader.reportAdClick(AdmobInterstitialAdapter.this);
//            }
//
//            @Override
//            public void onAdClosed() {
//                super.onAdClosed();
//                AdLog.d("ad interstitial onAdClosed");
//                if (adListener != null) {
//                    adListener.onAdClosed(AdmobInterstitialAdapter.this);
//                }
//            }
//
//            @Override
//            public void onAdImpression() {
//                super.onAdImpression();
//                AdmobInterstitialAdapter.this.onAdShowed();
//            }
//        });

//        if (AdConstants.DEBUG) {
//            String android_id = AdUtils.getAndroidID(context);
//            String deviceId = AdUtils.MD5(android_id).toUpperCase();
//            AdRequest request = new AdRequest.Builder().build();
//            rawAd.loadAd(request);
//            boolean isTestDevice = request.isTestDevice(context);
//            AdLog.d("is Admob Test Device ? " + deviceId + " " + isTestDevice);
//        } else {
//            rawAd.loadAd(new AdRequest.Builder().build());
//        }
        startMonitor();
    }

    @Override
    protected void onTimeOut() {
        if (adListener != null) {
            adListener.onError("TIME_OUT");
        }
    }
}
