package miku.ad.adapters;


import android.app.Activity;
import android.content.Context;
import android.os.Handler;
import android.os.Looper;
import android.util.Log;
import android.view.View;
import android.widget.Toast;

import miku.ad.AdConstants;
import miku.ad.AdLog;
import miku.ad.AdUtils;
import miku.ad.AdViewBinder;

import static miku.ad.adapters.FuseAdLoader.isAdmob;
import static miku.ad.adapters.FuseAdLoader.isMopub;


public abstract class AdAdapter implements IAdAdapter {
    protected final static String ERR = "AdERR_";

    protected final static String ADLOAD = "LOAD";
    protected final static String ADCLICK = "CLICK";
    protected final static String ADCLOSED = "CLOSED";
    protected final static String ADERROR = "ERROR";
    protected final static String ADREWARD = "REWARD";
    protected final static String ADLEFT = "LEFT";
    protected final static String ADSHOW = "SHOW";
    protected final static String ADCOMPLETE = "COMPLETE";

    protected String mKey;
    protected String mSlot;
    protected long mLoadedTime = -1;
    protected long mStartLoadedTime = 0;
    protected int mShowCount = 0;
    protected long LOAD_TIMEOUT = 15 * 1000;
    protected IAdLoadListener adListener;
    protected IAdLoadListener adCustomerListener;

    protected long EXPIRED_TIMEOUT = 45 * 60 * 1000; // 45 min


    protected Handler mHandler = new Handler(Looper.myLooper());

    private Runnable timeoutRunner = new Runnable() {
        @Override
        public void run() {
            onTimeOut();
        }
    };

    protected void startMonitor() {
        mHandler.postDelayed(timeoutRunner, LOAD_TIMEOUT);
    }

    protected void stopMonitor() {
        mHandler.removeCallbacks(timeoutRunner);
    }

    protected void onTimeOut() {

    }

    public AdAdapter(Context context, String key, String slot) {
        mKey = key;
        mSlot = slot;
    }

    @Override
    public void setAdListener(IAdLoadListener listener) {
        adCustomerListener = listener;
    }

    void onAdShowed() {
        AdLog.d(mSlot + "_" + getAdType() + "_" + ADSHOW);
    }

    void onAdLoaded() {
        if (adCustomerListener != null) {
            adCustomerListener.onAdLoaded(this);
        }
        AdLog.d(mSlot + "_" + getAdType() + "_" + ADLOAD);
    }

    void onAdClicked() {
        if (adCustomerListener != null) {
            adCustomerListener.onAdClicked(this);
        }
        AdLog.d(mSlot + "_" + getAdType() + "_" + ADCLICK);
    }

    void onError(String error) {
        if (adCustomerListener != null) {
            adCustomerListener.onError(error);
        }
        AdLog.d(mSlot + "_" + getAdType() + "_" + ADERROR + error);
    }

    void onRewarded() {
        if (adCustomerListener != null) {
            adCustomerListener.onRewarded(this);
        }
        AdLog.d(mSlot + "_" + getAdType() + "_" + ADREWARD);
    }

    @Override
    public boolean isInterstitialAd() {
        return false;
    }

    @Override
    public long getLoadedTime() {
        return mLoadedTime;
    }

    @Override
    public long getExpiredTime() {
        // by default using 45 mins, each ad source can have different expired time
        return EXPIRED_TIMEOUT;
    }

    @Override
    public boolean isExpired() {
        return System.currentTimeMillis() - getLoadedTime() > getExpiredTime();
    }

    @Override
    public int getShowCount() {
        return mShowCount;
    }

    @Override
    public boolean isShowed() {
        return mShowCount > 0;
    }

    @Override
    public void registerViewForInteraction(View view) {
        mShowCount++;
    }

    @Override
    public String getAdType() {
        return "";
    }

    @Override
    public String getBody() {
        return null;
    }

    @Override
    public String getCoverImageUrl() {
        return null;
    }

    @Override
    public String getIconImageUrl() {
        return null;
    }

    @Override
    public String getSubtitle() {
        return null;
    }

    @Override
    public double getStarRating() {
        return 5.0;
    }

    @Override
    public String getTitle() {
        return null;
    }

    @Override
    public String getCallToActionText() {
        return null;
    }

    @Override
    public Object getAdObject() {
        return null;
    }

    @Override
    public String getId() {
        return null;
    }

    @Override
    public String getPrivacyIconUrl() {
        return null;
    }

    @Override
    public String getPlacementId() {
        return mKey;
    }


    @Override
    public void show() {

    }

    @Override
    public void destroy() {

    }

    @Override
    public View getAdView(Context context, AdViewBinder viewBinder) {
        return null;
    }

    @Override
    public void onActivityResume(Activity activity) {
    }

    @Override
    public void onActivityPause(Activity activity) {
    }

    protected void trackImpression() {
        AdUtils.trackAdEvent(mKey, "imp_" + getId());
    }

    protected void trackClick() {
        AdUtils.trackAdEvent(mKey, "clk_" + getId());
    }

    @Override
    public String getSlot() {
        return mSlot;
    }

    @Override
    public View getAdViewStrict(Context context, AdViewBinder viewBinder) {
        return getAdView(context, viewBinder);
    }

    public static void dealErrorMessage(AdAdapter ad, int errorCode) {
        String errorMsg = null;
        boolean shouldCrash = false;
        boolean shouldToast = false;
        if (isAdmob(ad)) {
            switch (errorCode) {
                case 0:
                    errorMsg = "internal error";
                    break;
                case 1:
                    errorMsg = "invalid id";
                    shouldCrash = true;
                    break;
                case 2:
                    errorMsg = "network id";
                    break;
                case 3:
                    errorMsg = "no fill";
                    break;
                case 4:
                    errorMsg = "internal error";
                    break;
                default:
                    errorMsg = "unknow error";
            }
            shouldToast = true;
        }else
            if (isMopub(ad)) {
            switch (errorCode) {
                case 0:
                    errorMsg = "internal error";
                    break;
                case 1:
                    errorMsg = "invalid id";
                    shouldCrash = true;
                    break;
                case 2:
                    errorMsg = "network id";
                    break;
                case 3:
                    errorMsg = "no fill";
                    break;
                case 4:
                    errorMsg = "internal error";
                    break;
                default:
                    errorMsg = "unknow error";
            }
            shouldToast = true;
        }
        final String toastMsg = ad.getSlot() + " " + ad.getAdType() + " " + errorMsg;
        Log.d("task--ad"," "+ad.getSlot() + " " + ad.getAdType() + " " + errorMsg);
        if (AdConstants.DEBUG) {
            if (shouldCrash) {
//                throw new RuntimeException(toastMsg + errorCode);
            }
            if (shouldToast) {
                FuseAdLoader.getHandler().post(new Runnable() {
                    @Override
                    public void run() {
                        Toast.makeText(FuseAdLoader.getContext(), toastMsg, Toast.LENGTH_SHORT).show();
                    }
                });
            }
        }
        AdLog.i(toastMsg);
    }
}
