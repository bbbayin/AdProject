package miku.ad.adapters;

import android.content.Context;

import static miku.ad.AdConstants.AdType.AD_SOURCE_ADMOB_H;


public class AdmobNativeHAdapter extends AdmobNativeAdapter {

    public AdmobNativeHAdapter(Context context, String key, String slot) {
        super(context, key, slot);
    }

    @Override
    public String getAdType() {
        return AD_SOURCE_ADMOB_H;
    }
}
