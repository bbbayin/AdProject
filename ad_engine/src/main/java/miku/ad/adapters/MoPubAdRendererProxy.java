package miku.ad.adapters;

import android.content.Context;
import android.view.View;
import android.view.ViewGroup;

import com.mopub.nativeads.BaseNativeAd;
import com.mopub.nativeads.MoPubAdRenderer;
import com.mopub.nativeads.MoPubStaticNativeAdRenderer;
import com.mopub.nativeads.StaticNativeAd;


public class MoPubAdRendererProxy implements MoPubAdRenderer<StaticNativeAd> {

    private MoPubStaticNativeAdRenderer rendererImpl;

    public void setRenderImpl(MoPubStaticNativeAdRenderer render) {
        rendererImpl = render;
    }

    public MoPubStaticNativeAdRenderer getRendererImpl() {
        return rendererImpl;
    }


    @Override
    public View createAdView( Context context,  ViewGroup parent) {
        return rendererImpl.createAdView(context, parent);
    }

    @Override
    public void renderAdView( View view,  StaticNativeAd ad) {
        rendererImpl.renderAdView(view, ad);
    }

    @Override
    public boolean supports( BaseNativeAd nativeAd) {
        return true;
    }
}
