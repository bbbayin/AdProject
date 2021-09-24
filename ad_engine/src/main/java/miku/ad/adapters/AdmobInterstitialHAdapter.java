package miku.ad.adapters;

import android.content.Context;

import com.google.android.gms.ads.InterstitialAd;

import static miku.ad.AdConstants.AdType.AD_SOURCE_ADMOB_INTERSTITIAL_H;


public class AdmobInterstitialHAdapter extends AdmobInterstitialAdapter {
    private final static String TAG = "AdmobInterstitialAdapter";
    private InterstitialAd rawAd;
    private String key;

    public AdmobInterstitialHAdapter(Context context, String key, String slot) {
        super(context, key, slot);
        this.key = key;
        LOAD_TIMEOUT = 20 * 1000;
    }

    @Override
    public String getAdType() {
        return AD_SOURCE_ADMOB_INTERSTITIAL_H;
    }
}
