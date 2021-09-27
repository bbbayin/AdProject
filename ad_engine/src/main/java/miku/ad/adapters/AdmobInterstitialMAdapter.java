package miku.ad.adapters;

import android.content.Context;

import com.google.android.gms.ads.interstitial.InterstitialAd;

import static miku.ad.AdConstants.AdType.AD_SOURCE_ADMOB_INTERSTITIAL_M;


public class AdmobInterstitialMAdapter extends AdmobInterstitialAdapter {
    private final static String TAG = "AdmobInterstitialAdapter";
    private InterstitialAd rawAd;
    private String key;

    public AdmobInterstitialMAdapter(Context context, String key, String slot) {
        super(context, key, slot);
        this.key = key;
        LOAD_TIMEOUT = 20 * 1000;
    }

    @Override
    public String getAdType() {
        return AD_SOURCE_ADMOB_INTERSTITIAL_M;
    }
}
