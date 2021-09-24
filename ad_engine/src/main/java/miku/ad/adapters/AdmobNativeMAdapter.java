package miku.ad.adapters;

import android.content.Context;

import miku.ad.AdConstants;


public class AdmobNativeMAdapter extends AdmobNativeAdapter {

    public AdmobNativeMAdapter(Context context, String key, String slot) {
        super(context, key, slot);
    }

    @Override
    public String getAdType() {
        return AdConstants.AdType.AD_SOURCE_ADMOB_M;
    }
}
