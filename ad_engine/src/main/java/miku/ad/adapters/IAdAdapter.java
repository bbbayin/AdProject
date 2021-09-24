package miku.ad.adapters;

import android.app.Activity;
import android.content.Context;
import android.view.View;

import miku.ad.AdViewBinder;

public interface IAdAdapter {
    String getAdType();

    String getCoverImageUrl();

    String getIconImageUrl();

    String getSubtitle();

    double getStarRating();

    String getTitle();

    String getCallToActionText();

    Object getAdObject();

    String getId();

    String getBody();

    void registerViewForInteraction(View view);

    void registerPrivacyIconView(View view);

    String getPrivacyIconUrl();

    String getPlacementId();

    int getShowCount();

    boolean isShowed();

    long getLoadedTime();

    boolean isExpired();

    long getExpiredTime();

    void show();

    void destroy();

    View getAdView(Context context, AdViewBinder viewBinder);

    View getAdViewStrict(Context context, AdViewBinder viewBinder);

    /**
     * @param num      number of Ads; if <= 0 , will use the default value 1s
     * @param listener
     */
    void loadAd(Context context, final int num, IAdLoadListener listener);

    boolean isInterstitialAd();

    void setAdListener(IAdLoadListener listener);

    void onActivityResume(Activity activity);

    void onActivityPause(Activity activity);

    String getSlot();
}
