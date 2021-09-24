package miku.ad.adapters;

import android.content.Context;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

import com.mopub.nativeads.RequestParameters;

import java.util.EnumSet;
import java.util.List;

import miku.ad.AdConstants;
import miku.ad.AdLog;
import miku.ad.AdViewBinder;
import miku.ad.prophet.JumpUtils;
import miku.ad.prophet.ProphetManager;
import miku.ad.prophet.ProphetSrcBean;
import miku.firebase.BaseDataReportUtils;
import miku.storage.LocalDataSourceImpl;

import static miku.ad.AdConstants.PROPHET_REFRESH_TIME;
import static miku.ad.BuildConfig.DEBUG;


public class ProphetNativeAdapter extends AdAdapter {
    private String adUnit;
    private ProphetSrcBean bean;
    private RequestParameters parameters;
    private MoPubAdRendererProxy rendererProxy;
    private final static String TAG = "ProphetNativeAdapter";

    public ProphetNativeAdapter(Context context, String adUnit, String slot) {
        super(context, adUnit, slot);
        this.adUnit = adUnit;
        if (DEBUG) {
            AdLog.d("Mopub test mode");
            this.adUnit = "11a17b188668469fb0412708c3d16813";
        }
        final EnumSet<RequestParameters.NativeAdAsset> desiredAssets = EnumSet.of(
                RequestParameters.NativeAdAsset.TITLE,
                RequestParameters.NativeAdAsset.TEXT,
                RequestParameters.NativeAdAsset.ICON_IMAGE,
                RequestParameters.NativeAdAsset.MAIN_IMAGE,
                RequestParameters.NativeAdAsset.CALL_TO_ACTION_TEXT);

        parameters = new RequestParameters.Builder()
                .desiredAssets(desiredAssets)
                .build();
    }

    @Override
    public void registerPrivacyIconView(View view) {

    }

    @Override
    public void loadAd(Context context, int num, IAdLoadListener listener) {
        mStartLoadedTime = System.currentTimeMillis();
        adListener = listener;
        AdLog.d("prophet loadAd " + listener);
        startMonitor();
        List<ProphetSrcBean> beanList;
        if (System.currentTimeMillis() - LocalDataSourceImpl.getInstance().getSlotAdRefreshTime(mSlot) > PROPHET_REFRESH_TIME) {
            beanList = ProphetManager.getInstance().getProphetSrcList();
            LocalDataSourceImpl.getInstance().saveSlotProphetSrcEntity(mSlot, beanList);
            LocalDataSourceImpl.getInstance().saveSlotAdRefreshTime(mSlot, System.currentTimeMillis());
        } else {
            beanList = LocalDataSourceImpl.getInstance().getSlotProphetSrcEntity(mSlot);
        }
        if (beanList != null && beanList.size() > 0) {
            bean = beanList.get(0);
            bean.preload(bean.getIcon());
            bean.preload(bean.getImage());
            mLoadedTime = System.currentTimeMillis();
            if (adListener != null) {
                adListener.onAdLoaded(ProphetNativeAdapter.this);
            }
            stopMonitor();
            if (mStartLoadedTime != 0) {
            }
            mStartLoadedTime = 0;
        } else {
            if (adListener != null) {
                adListener.onError("none");
            }
            stopMonitor();
            mStartLoadedTime = 0;
        }
    }

    @Override
    public String getAdType() {
        return AdConstants.AdType.AD_SOURCE_PROPHET;
    }

    @Override
    public Object getAdObject() {
        return bean;
    }

    @Override
    public void destroy() {
    }

    @Override
    public View getAdView(final Context context, AdViewBinder viewBinder) {
        View inflateView = LayoutInflater.from(context).inflate(viewBinder.layoutId, null);
        final TextView ctaView = inflateView.findViewById(viewBinder.callToActionId);
        if (ctaView != null) {
            if (!TextUtils.isEmpty(bean.getButton())) {
                ctaView.setText(bean.getButton());
            } else {
                ctaView.setText("GO");
            }

        }
        inflateView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                boolean isAlreadyInstalled = JumpUtils.checkAppInstalled(context, bean.getPkg());
                if (bean.getType().equals("app")) {
                    if (isAlreadyInstalled) {
                        JumpUtils.junmptoExistApps(context, bean.getPkg());
                    } else {
                        JumpUtils.jumptoGooglePlay(context, bean.getPkg(), mKey);
                    }
                } else if (bean.getType().equals("web")) {
                    JumpUtils.junmptoBrowser(context, bean.getLink());
                }
                BaseDataReportUtils.getInstance().reportDirect(FuseAdLoader.getConfiguration().getProphetId() + mKey + "_click_prophet");
            }
        });
        ((TextView) inflateView.findViewById(viewBinder.titleId)).setText(bean.getTitle());
        ((TextView) inflateView.findViewById(viewBinder.textId)).setText(bean.getDesprion());
        View icon = inflateView.findViewById(viewBinder.iconImageId);
        View image = inflateView.findViewById(viewBinder.mainMediaId);
        if (viewBinder.iconImageId > 0 && viewBinder.iconImageId == viewBinder.mainMediaId) {
            if (icon != null) {
                bean.showInImageView((ImageView) icon, bean.getIcon());
            }
        } else {
            if (image != null) {
                bean.showInImageView((ImageView) image, bean.getImage());
            }
            if (icon != null) {
                bean.showInImageView((ImageView) icon, bean.getIcon());
            }
        }
        ProphetNativeAdapter.this.onAdShowed();
        LocalDataSourceImpl.getInstance().removeSlotProphetSrcEntity(mSlot, bean);
        return inflateView;
    }

    @Override
    protected void onTimeOut() {
        if (adListener != null) {
            adListener.onError("TIME_OUT");
        }
    }
}
