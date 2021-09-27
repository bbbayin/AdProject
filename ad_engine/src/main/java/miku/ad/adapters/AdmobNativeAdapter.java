package miku.ad.adapters;

import android.content.Context;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;

import com.google.android.gms.ads.AdListener;
import com.google.android.gms.ads.AdLoader;
import com.google.android.gms.ads.AdRequest;
import com.google.android.gms.ads.LoadAdError;
import com.google.android.gms.ads.VideoController;
import com.google.android.gms.ads.VideoOptions;
import com.google.android.gms.ads.nativead.MediaView;
import com.google.android.gms.ads.nativead.NativeAd;
import com.google.android.gms.ads.nativead.NativeAdOptions;
import com.google.android.gms.ads.nativead.NativeAdView;

import java.util.List;

import miku.ad.AdConstants;
import miku.ad.AdLog;
import miku.ad.AdUtils;
import miku.ad.AdViewBinder;
import miku.ad.view.StarLevelLayoutView;


public class AdmobNativeAdapter extends AdAdapter {
    private NativeAd mRawAd;
    private final static String TAG = "AdmobNativeAdapter";

    public AdmobNativeAdapter(Context context, String key, String slot) {
        super(context, key, slot);
    }

    @Override
    public void loadAd(Context context, int num, IAdLoadListener listener) {
        if (listener == null) {
            AdLog.e("listener not set.");
            return;
        }
        mStartLoadedTime = System.currentTimeMillis();
        adListener = listener;
        if (num > 1) {
            AdLog.d("Admob not support load for more than 1 ads. Only return 1 ad");
        }
        AdLoader.Builder builder = new AdLoader.Builder(context, mKey);
        builder.forNativeAd(new NativeAd.OnNativeAdLoadedListener() {
            @Override
            public void onNativeAdLoaded(@NonNull NativeAd nativeAd) {
                if (isValidAd(nativeAd)) {
                    Log.e("ADMOB_ENGINE", "ad title is: " + nativeAd.getHeadline());
                    postOnAdLoaded(nativeAd);
                    nativeAd.setUnconfirmedClickListener(new NativeAd.UnconfirmedClickListener() {
                        @Override
                        public void onUnconfirmedClickReceived(String s) {
                            AdmobNativeAdapter.this.onAdClicked();
                            FuseAdLoader.reportAdClick(AdmobNativeAdapter.this);
                        }

                        @Override
                        public void onUnconfirmedClickCancelled() {

                        }
                    });
                }
            }
        });

        VideoOptions videoOptions = new VideoOptions.Builder()
                .setStartMuted(true)
                .build();
        NativeAdOptions adOptions = new NativeAdOptions.Builder()
                .setVideoOptions(videoOptions).setRequestMultipleImages(false).setReturnUrlsForImageAssets(false)
                .build();
        builder.withNativeAdOptions(adOptions);
        builder.withAdListener(new AdListener() {
            @Override
            public void onAdFailedToLoad(LoadAdError loadAdError) {
                postOnAdLoadFail(loadAdError.getCode());
                mStartLoadedTime = 0;
                AdmobNativeAdapter.this.onError(String.valueOf(loadAdError.getCode()));
                dealErrorMessage(AdmobNativeAdapter.this, loadAdError.getCode());
            }

            @Override
            public void onAdClicked() {
                super.onAdClicked();
                AdmobNativeAdapter.this.onAdClicked();
                FuseAdLoader.reportAdClick(AdmobNativeAdapter.this);
            }

            @Override
            public void onAdImpression() {
                super.onAdImpression();
                AdmobNativeAdapter.this.onAdShowed();
            }
        });
        AdLoader adLoader = builder.build();
        if (AdConstants.DEBUG) {
            String android_id = AdUtils.getAndroidID(context);
            String deviceId = AdUtils.MD5(android_id).toUpperCase();
            AdRequest request = new AdRequest.Builder().build();
            adLoader.loadAd(request);
            boolean isTestDevice = request.isTestDevice(context);
            AdLog.d("is Admob Test Device ? " + deviceId + " " + isTestDevice);
        } else {
            adLoader.loadAd(new AdRequest.Builder().build());
        }
        startMonitor();
    }

    private void postOnAdLoaded(NativeAd ad) {
        mRawAd = ad;
        mLoadedTime = System.currentTimeMillis();
        if (adListener != null) {
            adListener.onAdLoaded(this);
        }
        if (mStartLoadedTime != 0) {
        }
        mStartLoadedTime = 0;
        AdmobNativeAdapter.this.onAdLoaded();
        stopMonitor();
    }

    private void postOnAdLoadFail(int i) {
        if (adListener != null) {
            adListener.onError("error" + i);
        }
        stopMonitor();
    }

    @Override
    public void registerPrivacyIconView(View view) {
    }

    @Override
    public String getAdType() {
        return AdConstants.AdType.AD_SOURCE_ADMOB;
    }

    @Override
    public String getCoverImageUrl() {
        return mRawAd.getImages() != null && mRawAd.getImages().size() > 0 ?
                mRawAd.getImages().get(0).getUri().toString() : null;
    }

    @Override
    public String getIconImageUrl() {
        return mRawAd.getIcon() == null ? null : mRawAd.getIcon().getUri().toString();
    }

    @Override
    public String getBody() {
        return (mRawAd).getBody() != null ? (mRawAd).getBody().toString() : null;

    }

    @Override
    public String getSubtitle() {
        Bundle extras = (mRawAd).getExtras();
        if (extras.containsKey("subtitle")) {
            return extras.getString("subtitle", "");
        }
        return (mRawAd).getBody() != null ? mRawAd.getBody().toString() : null;
    }

    @Override
    public double getStarRating() {
        return (mRawAd).getStarRating();
    }

    @Override
    public String getTitle() {
        return (mRawAd).getHeadline() != null ? (mRawAd).getHeadline().toString() : null;
    }

    @Override
    public String getCallToActionText() {
        return (mRawAd).getCallToAction() != null ? (mRawAd).getCallToAction().toString() : null;
    }

    @Override
    public Object getAdObject() {
        return mRawAd;
    }

    @Override
    public String getId() {
        return null;
    }

    @Override
    protected void onTimeOut() {
        if (adListener != null) {
            adListener.onError("TIME_OUT");
        }
    }

    private boolean isValidAd(NativeAd ad) {
        return (ad.getHeadline() != null && ad.getBody() != null
                && ad.getCallToAction() != null);
    }

    @Override
    public View getAdView(Context context, AdViewBinder viewBinder) {
        View actualAdView = null;
        try {
            actualAdView = LayoutInflater.from(context).inflate(viewBinder.layoutId, null);
        } catch (Exception e) {
        }
        NativeAdView nativeAdView = new NativeAdView(context);
        if (actualAdView != null) {
            ImageView iconView = actualAdView.findViewById(viewBinder.iconImageId);
            TextView titleView = actualAdView.findViewById(viewBinder.titleId);
            if (titleView != null) {
                titleView.setText(getTitle());
            }
            TextView subtitleView = actualAdView.findViewById(viewBinder.textId);
            if (subtitleView != null) {
                subtitleView.setText(getBody());
            }
            TextView ctaView = actualAdView.findViewById(viewBinder.callToActionId);
            if (ctaView != null) {
                ctaView.setText(getCallToActionText());
            }
            TextView adFlag = actualAdView.findViewById(viewBinder.adFlagId);

            MediaView mediaView = null;
            ImageView coverImageView = null;

            View main = actualAdView.findViewById(viewBinder.mainMediaId);
            if (main instanceof MediaView) {
                mediaView = (MediaView) main;
            } else if (main instanceof ImageView) {
                coverImageView = (ImageView) main;
            }
            if (mediaView == null && viewBinder.admMediaId != -1) {
                mediaView = actualAdView.findViewById(viewBinder.admMediaId);
            }
//            if (mediaView == null && coverImageView == null) {
//                AdLog.d("Wrong ad layout " + viewBinder.layoutId);
//                return null;
//            }
//            if (mediaView == null) {
//                AdLog.e("ADM: Error no mediaView!!!! " + viewBinder.admMediaId);
//                return null;
//            }
            if (mediaView instanceof MediaView) {
                ViewGroup parent = (ViewGroup) mediaView.getParent();
                parent.removeView(mediaView);
                MediaView ratioView = new MediaView(mediaView.getContext()) {
                    @Override
                    protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
                        int width = MeasureSpec.getSize(widthMeasureSpec);
                        float height = width * 9 / 16;
                        int newHeight = MeasureSpec.makeMeasureSpec((int) height, MeasureSpec.EXACTLY);
                        super.onMeasure(widthMeasureSpec, newHeight);
                    }
                };
                mediaView = ratioView;
                parent.addView(mediaView);
            }
            if (mediaView != null) {
                mediaView.setOnHierarchyChangeListener(new ViewGroup.OnHierarchyChangeListener() {
                    @Override
                    public void onChildViewAdded(View parent, View child) {
                        if (child instanceof ImageView) {
                            ImageView imageView = (ImageView) child;
                            imageView.setAdjustViewBounds(true);
                        }
                    }

                    @Override
                    public void onChildViewRemoved(View parent, View child) {
                    }
                });
            }

            StarLevelLayoutView starLevelLayout;
            if (viewBinder.starLevelLayoutId != -1) {
                starLevelLayout = actualAdView.findViewById(viewBinder.starLevelLayoutId);
                if (starLevelLayout != null && getStarRating() != 0) {
                    starLevelLayout.setRating((int) getStarRating());
                }
            }
            nativeAdView.setCallToActionView(ctaView);
            nativeAdView.setHeadlineView(titleView);
            nativeAdView.setBodyView(subtitleView);
            nativeAdView.setMediaView(mediaView);
            VideoController vc = mRawAd.getMediaContent()  .getVideoController();
            if (vc.hasVideoContent() || mRawAd.getImages() == null || mRawAd.getImages().size() == 0) {
                if (mediaView != null) {
                    mediaView.setVisibility(View.VISIBLE);
                    nativeAdView.setMediaView(mediaView);
                }
                if (coverImageView != null) {
                    coverImageView.setVisibility(View.GONE);
                }
                vc.setVideoLifecycleCallbacks(new VideoController.VideoLifecycleCallbacks() {
                    @Override
                    public void onVideoEnd() {
                        AdLog.d("onVideoEnd");
                    }
                });
            } else {
//                if (coverImageView == null) {
//                    return null;
//                }
                List<NativeAd.Image> images = mRawAd.getImages();
                try {
                    if (mediaView != null) {
                        mediaView.setVisibility(View.GONE);
                    }
                    if (coverImageView != null) {
                        coverImageView.setVisibility(View.VISIBLE);
                        nativeAdView.setImageView(coverImageView);
                        coverImageView.setImageDrawable(images.get(0).getDrawable());
                    }

                } catch (Exception e) {
                }
            }
            if (iconView != null) {
                nativeAdView.setIconView(iconView);
                if (mRawAd.getIcon() == null) {
                    nativeAdView.getIconView().setVisibility(View.GONE);
                } else {
                    ((ImageView) nativeAdView.getIconView()).setImageDrawable(
                            mRawAd.getIcon().getDrawable());
                    nativeAdView.getIconView().setVisibility(View.VISIBLE);
                }
            }
            // Google native ad view renders the AdChoices icon in one of the four corners of
            // its view. If a margin is specified on the actual ad view, the AdChoices view
            // might be rendered outside the actual ad view. Moving the margins from the
            // actual ad view to Google native ad view will make sure that the AdChoices icon
            // is being rendered within the bounds of the actual ad view.
            registerViewForInteraction(actualAdView);
            FrameLayout.LayoutParams googleNativeAdViewParams = new FrameLayout.LayoutParams(
                    ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.MATCH_PARENT);
            AdLog.d("admob:" + actualAdView.toString());
            ViewGroup.MarginLayoutParams actualViewParams = (ViewGroup.MarginLayoutParams) actualAdView.getLayoutParams();
            if (actualViewParams != null) {
                googleNativeAdViewParams.setMargins(actualViewParams.leftMargin,
                        actualViewParams.topMargin,
                        actualViewParams.rightMargin,
                        actualViewParams.bottomMargin);

                nativeAdView.setLayoutParams(googleNativeAdViewParams);
                actualViewParams.setMargins(0, 0, 0, 0);
            }
            nativeAdView.addView(actualAdView);
            nativeAdView.setNativeAd(mRawAd);
            return nativeAdView;
        }
        return null;
    }

    @Override
    public View getAdViewStrict(Context context, AdViewBinder viewBinder) {
        View actualAdView = null;
        try {
            actualAdView = LayoutInflater.from(context).inflate(viewBinder.layoutId, null);
        } catch (Exception e) {
        }
        NativeAdView nativeAdView = new NativeAdView(context);
        if (actualAdView != null) {
            ImageView iconView = actualAdView.findViewById(viewBinder.iconImageId);
            TextView titleView = actualAdView.findViewById(viewBinder.titleId);
            if (titleView != null) {
                titleView.setText(getTitle());
            }
            TextView subtitleView = actualAdView.findViewById(viewBinder.textId);
            if (subtitleView != null) {
                subtitleView.setText(getBody());
            }
            TextView ctaView = actualAdView.findViewById(viewBinder.callToActionId);
            if (ctaView != null) {
                ctaView.setText(getCallToActionText());
            }
            TextView adFlag = actualAdView.findViewById(viewBinder.adFlagId);

            MediaView mediaView = null;
            ImageView coverImageView = null;

            View main = actualAdView.findViewById(viewBinder.mainMediaId);
            if (main instanceof MediaView) {
                mediaView = (MediaView) main;
            } else if (main instanceof ImageView) {
                coverImageView = (ImageView) main;
            }
            if (mediaView == null && viewBinder.admMediaId != -1) {
                mediaView = actualAdView.findViewById(viewBinder.admMediaId);
            }
            if (mediaView instanceof MediaView) {
                ViewGroup parent = (ViewGroup) mediaView.getParent();
                parent.removeView(mediaView);
                MediaView ratioView = new MediaView(mediaView.getContext()) {
                    @Override
                    protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
                        int width = MeasureSpec.getSize(widthMeasureSpec);
                        float height = width * 9 / 16;
                        int newHeight = MeasureSpec.makeMeasureSpec((int) height, MeasureSpec.EXACTLY);
                        super.onMeasure(widthMeasureSpec, newHeight);
                    }
                };
                mediaView = ratioView;
                parent.addView(mediaView);
            }
            if (mediaView != null) {
                mediaView.setOnHierarchyChangeListener(new ViewGroup.OnHierarchyChangeListener() {
                    @Override
                    public void onChildViewAdded(View parent, View child) {
                        if (child instanceof ImageView) {
                            ImageView imageView = (ImageView) child;
                            imageView.setAdjustViewBounds(true);
                        }
                    }

                    @Override
                    public void onChildViewRemoved(View parent, View child) {
                    }
                });
            }

            StarLevelLayoutView starLevelLayout;
            if (viewBinder.starLevelLayoutId != -1) {
                starLevelLayout = actualAdView.findViewById(viewBinder.starLevelLayoutId);
                if (starLevelLayout != null && getStarRating() != 0) {
                    starLevelLayout.setRating((int) getStarRating());
                }
            }
            nativeAdView.setCallToActionView(ctaView);
            nativeAdView.setMediaView(mediaView);
            VideoController vc = mRawAd.getMediaContent().getVideoController();
            if (vc.hasVideoContent() || mRawAd.getImages() == null || mRawAd.getImages().size() == 0) {
                if (mediaView != null) {
                    mediaView.setVisibility(View.VISIBLE);
                    nativeAdView.setMediaView(mediaView);
                }
                if (coverImageView != null) {
                    coverImageView.setVisibility(View.GONE);
                }
                vc.setVideoLifecycleCallbacks(new VideoController.VideoLifecycleCallbacks() {
                    @Override
                    public void onVideoEnd() {
                        AdLog.d("onVideoEnd");
                    }
                });
            } else {
                List<NativeAd.Image> images = mRawAd.getImages();
                try {
                    if (mediaView != null) {
                        mediaView.setVisibility(View.GONE);
                    }
                    if (coverImageView != null) {
                        coverImageView.setVisibility(View.VISIBLE);
//                        nativeAdView.setImageView(coverImageView);
                        coverImageView.setImageDrawable(images.get(0).getDrawable());
                    }

                } catch (Exception e) {
                }
            }
            if (mediaView != null) {
                mediaView.setOnClickListener(null);
            }
            if (iconView != null) {
//                nativeAdView.setIconView(iconView);
                if (mRawAd.getIcon() == null) {
                    iconView.setVisibility(View.GONE);
                } else {
                    iconView.setImageDrawable(
                            mRawAd.getIcon().getDrawable());
                    iconView.setVisibility(View.VISIBLE);
                }
                iconView.setOnClickListener(null);
            }
            // Google native ad view renders the AdChoices icon in one of the four corners of
            // its view. If a margin is specified on the actual ad view, the AdChoices view
            // might be rendered outside the actual ad view. Moving the margins from the
            // actual ad view to Google native ad view will make sure that the AdChoices icon
            // is being rendered within the bounds of the actual ad view.
            registerViewForInteraction(actualAdView);
            FrameLayout.LayoutParams googleNativeAdViewParams = new FrameLayout.LayoutParams(
                    ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.MATCH_PARENT);
            AdLog.d("admob:" + actualAdView.toString());
            ViewGroup.MarginLayoutParams actualViewParams = (ViewGroup.MarginLayoutParams) actualAdView.getLayoutParams();
            if (actualViewParams != null) {
                googleNativeAdViewParams.setMargins(actualViewParams.leftMargin,
                        actualViewParams.topMargin,
                        actualViewParams.rightMargin,
                        actualViewParams.bottomMargin);

                nativeAdView.setLayoutParams(googleNativeAdViewParams);
                actualViewParams.setMargins(0, 0, 0, 0);
            }
            nativeAdView.addView(actualAdView);
            nativeAdView.setNativeAd(mRawAd);
            return nativeAdView;
        }
        return null;
    }

    @Override
    public void registerViewForInteraction(View view) {
        super.registerViewForInteraction(view);
    }

    @Override
    public void destroy() {
        super.destroy();
        mRawAd.destroy();
    }
}
