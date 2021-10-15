package miku.ad;

import android.text.TextUtils;
import android.util.Log;

import com.mopub.common.MoPub;

import java.util.HashSet;
import java.util.Set;

import miku.ad.adapters.FuseAdLoader;

public class SDKConfiguration {
    public String vgId;
    public String admobAppId;
    public String mopubInitAdId;
    public String prophetId;
    public String applovinId;

    public String adcolonyId = "app30a92c741c3a48b092";
    public Set<String> supportedFuseAdType;
    public boolean needReward;

    public boolean hasAdmob() {
        return !TextUtils.isEmpty(admobAppId) && (supportedFuseAdType.contains(AdConstants.AdType.AD_SOURCE_ADMOB)
                || supportedFuseAdType.contains(AdConstants.AdType.AD_SOURCE_ADMOB_H)
                || supportedFuseAdType.contains(AdConstants.AdType.AD_SOURCE_ADMOB_M)
                || supportedFuseAdType.contains(AdConstants.AdType.AD_SOURCE_ADMOB_BANNER)
                || supportedFuseAdType.contains(AdConstants.AdType.AD_SOURCE_ADMOB_INTERSTITIAL_H)
                || supportedFuseAdType.contains(AdConstants.AdType.AD_SOURCE_ADMOB_INTERSTITIAL_M)
                || supportedFuseAdType.contains(AdConstants.AdType.AD_SOURCE_ADMOB_INTERSTITIAL)
                || supportedFuseAdType.contains(AdConstants.AdType.AD_SOURCE_ADMOB_REWARD));
    }

    public boolean hasVG() {
        return !TextUtils.isEmpty(vgId) && (supportedFuseAdType.contains(AdConstants.AdType.AD_SOURCE_VG)
                || supportedFuseAdType.contains(AdConstants.AdType.AD_SOURCE_VG_INTERSTITIAL)
                || supportedFuseAdType.contains(AdConstants.AdType.AD_SOURCE_VG_BANNER)
                || supportedFuseAdType.contains(AdConstants.AdType.AD_SOURCE_VG_REWARD));
    }
    public boolean hasApplovin() {
        return (supportedFuseAdType.contains(AdConstants.AdType.AD_SOURCE_APPLOVIN_BANNER)
                || supportedFuseAdType.contains(AdConstants.AdType.AD_SOURCE_APPLOVIN_INTERSTITIAL)
                || supportedFuseAdType.contains(AdConstants.AdType.AD_SOURCE_APPLOVIN_MREC)
                || supportedFuseAdType.contains(AdConstants.AdType.AD_SOURCE_APPLOVIN_REWARD));
    }
    public boolean hasAdcolony() {
        return  (supportedFuseAdType.contains(AdConstants.AdType.AD_SOURCE_ADCOLONY_BANNER)
                || supportedFuseAdType.contains(AdConstants.AdType.AD_SOURCE_ADCOLONY_INTERSTITIAL)
                || supportedFuseAdType.contains(AdConstants.AdType.AD_SOURCE_ADCOLONY_REWARD));
    }

    public boolean hasMopub() {
        try {
            Class mopub = Class.forName(MoPub.class.getName());
            Log.d("MMMM", "mopub != null   " + (mopub != null) + "   " + mopubInitAdId + "  " + supportedFuseAdType.contains(AdConstants.AdType.AD_SOURCE_MOPUB) + "   " +
                    supportedFuseAdType.contains(AdConstants.AdType.AD_SOURCE_MOPUB_INTERSTITIAL));
            return mopub != null && !TextUtils.isEmpty(mopubInitAdId) && (supportedFuseAdType.contains(AdConstants.AdType.AD_SOURCE_MOPUB)
                    || supportedFuseAdType.contains(AdConstants.AdType.AD_SOURCE_MOPUB_INTERSTITIAL));
        } catch (Throwable ex) {
            Log.d("MMMM", "mopub = null   " + ex.getMessage());
            AdLog.e(ex);
        }
        return false;
    }

    public String getAdcolonyId() {
        return adcolonyId;
    }

    public String getProphetId() {
        return prophetId;
    }

    public boolean hasProphet() {
        return !TextUtils.isEmpty(prophetId) && (supportedFuseAdType.contains(AdConstants.AdType.AD_SOURCE_PROPHET));
    }

    public boolean hasSupport(String adType) {
        return supportedFuseAdType.contains(adType);
    }

    private SDKConfiguration() {

    }

    static public class Builder {
        private SDKConfiguration configuration;

        public Builder() {
            configuration = new SDKConfiguration();
            configuration.supportedFuseAdType = new HashSet<>(FuseAdLoader.SUPPORTED_TYPES);
        }

        public SDKConfiguration build() {
            if (!configuration.hasMopub()) {
                configuration.supportedFuseAdType.remove(AdConstants.AdType.AD_SOURCE_MOPUB);
                configuration.supportedFuseAdType.remove(AdConstants.AdType.AD_SOURCE_MOPUB_INTERSTITIAL);
                configuration.supportedFuseAdType.remove(AdConstants.AdType.AD_SOURCE_MOPUB_REWARD);
                AdLog.e("Mopub not built in. Disabled");
            }
            if (!configuration.hasVG()) {
                configuration.supportedFuseAdType.remove(AdConstants.AdType.AD_SOURCE_VG);
                configuration.supportedFuseAdType.remove(AdConstants.AdType.AD_SOURCE_VG_INTERSTITIAL);
                configuration.supportedFuseAdType.remove(AdConstants.AdType.AD_SOURCE_VG_BANNER);
                configuration.supportedFuseAdType.remove(AdConstants.AdType.AD_SOURCE_VG_REWARD);
                AdLog.e("vungle not built in. Disabled");
            }

            if(!configuration.hasApplovin()){
                configuration.supportedFuseAdType.remove(AdConstants.AdType.AD_SOURCE_APPLOVIN_BANNER);
                configuration.supportedFuseAdType.remove(AdConstants.AdType.AD_SOURCE_APPLOVIN_INTERSTITIAL);
                configuration.supportedFuseAdType.remove(AdConstants.AdType.AD_SOURCE_APPLOVIN_REWARD);
                configuration.supportedFuseAdType.remove(AdConstants.AdType.AD_SOURCE_APPLOVIN_MREC);
                AdLog.e("applovin not built in. Disabled");
            }

            if(!configuration.hasAdcolony()){
                configuration.supportedFuseAdType.remove(AdConstants.AdType.AD_SOURCE_ADCOLONY_INTERSTITIAL);
                configuration.supportedFuseAdType.remove(AdConstants.AdType.AD_SOURCE_ADCOLONY_BANNER);
                configuration.supportedFuseAdType.remove(AdConstants.AdType.AD_SOURCE_ADCOLONY_REWARD);
                AdLog.e("adcolony not built in. Disabled");
            }

            if (!configuration.hasProphet()) {
                configuration.supportedFuseAdType.remove(AdConstants.AdType.AD_SOURCE_PROPHET);
                AdLog.e("Prophet Disabled");
            }
            return configuration;
        }

        public Builder admobAppId(String s) {
            configuration.admobAppId = s;
            return this;
        }

        public Builder mopubAdUnit(String s) {
            configuration.mopubInitAdId = s;
            return this;
        }
        public Builder vgAdUnit(String s) {
            configuration.vgId = s;
            return this;
        }
        public Builder prophetId(String s) {
            configuration.prophetId = s;
            return this;
        }
        public Builder applovinAd(String s){
            configuration.applovinId = s;
            return this;
        }

    }

}
