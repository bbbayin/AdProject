package miku.ad;

public class AdConstants {
    public static boolean DEBUG = BuildConfig.DEBUG;
    public static final long PROPHET_REFRESH_TIME = 3 * 24 * 60 * 60 * 1000;

    public static final class AdType {
        public static final String AD_SOURCE_ADMOB = "adm";
        public static final String AD_SOURCE_ADMOB_INTERSTITIAL = "ab_interstitial";
        public static final String AD_SOURCE_ADMOB_BANNER = "ab_banner";
        public static final String AD_SOURCE_ADMOB_REWARD = "adm_reward";
        public static final String AD_SOURCE_ADMOB_INTERSTITIAL_H = "ab_interstitial_h";
        public static final String AD_SOURCE_ADMOB_INTERSTITIAL_M = "ab_interstitial_m";
        public static final String AD_SOURCE_ADMOB_H = "adm_h";
        public static final String AD_SOURCE_ADMOB_M = "adm_m";

        public static final String AD_SOURCE_VG = "vg";
        public static final String AD_SOURCE_VG_INTERSTITIAL = "vg_interstitial";
        public static final String AD_SOURCE_VG_BANNER = "vg_banner";
        public static final String AD_SOURCE_VG_REWARD = "vg_reward";

        public static final String AD_SOURCE_MOPUB = "mp";
        public static final String AD_SOURCE_MOPUB_INTERSTITIAL = "mp_interstitial";
        public static final String AD_SOURCE_MOPUB_REWARD = "mp_reward";

        public static final String AD_SOURCE_PROPHET = "pp";
    }
}

