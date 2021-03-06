package miku.ad;

import java.util.Collections;
import java.util.HashMap;
import java.util.Map;

public class AdViewBinder {
    public final static class Builder {
        private final int layoutId;
        private int titleId;
        private int textId;
        private int callToActionId;
        private int mainMediaId;
        private int admMediaId = -1;
        private int fbMediaId = -1;
        private int iconImageId = -1;
        private int iconFbId = -1;
        private int privacyInformationId;
        private int privacymopubInformationId;
        private int starLevelLayoutId = -1;
        private int adFlagId = -1;
        private Map<String, Integer> extras = Collections.emptyMap();

        public Builder(final int layoutId) {
            this.layoutId = layoutId;
            this.extras = new HashMap<String, Integer>();
        }

        public final Builder titleId(final int titleId) {
            this.titleId = titleId;
            return this;
        }


        public final Builder textId(final int textId) {
            this.textId = textId;
            return this;
        }


        public final Builder callToActionId(final int callToActionId) {
            this.callToActionId = callToActionId;
            return this;
        }

        public final Builder mainMediaId(final int mediaLayoutId) {
            this.mainMediaId = mediaLayoutId;
            return this;
        }

        public final Builder admMediaId(final int mediaLayoutId) {
            this.admMediaId = mediaLayoutId;
            return this;
        }


        public final Builder iconImageId(final int iconImageId) {
            this.iconImageId = iconImageId;
            return this;
        }

        public final Builder fbMediaId(final int mediaLayoutId) {
            this.fbMediaId = mediaLayoutId;
            return this;
        }

        public final Builder iconFbId(final int iconFbId) {
            this.iconFbId = iconFbId;
            return this;
        }

        public final Builder privacyInformationId(final int privacyInformationIconImageId) {
            this.privacyInformationId = privacyInformationIconImageId;
            return this;
        }

        public final Builder privacyMopubInformationId(final int privacyMopubInformationIconImageId) {
            this.privacymopubInformationId = privacyMopubInformationIconImageId;
            return this;
        }

        public final Builder starLevelLayoutId(final int starLevelLayoutId) {
            this.starLevelLayoutId = starLevelLayoutId;
            return this;
        }

        public final Builder adFlagId(final int adFlagId) {
            this.adFlagId = adFlagId;
            return this;
        }

        public final Builder addExtras(final Map<String, Integer> resourceIds) {
            this.extras = new HashMap<String, Integer>(resourceIds);
            return this;
        }

        public final Builder addExtra(final String key, final int resourceId) {
            this.extras.put(key, resourceId);
            return this;
        }

        public final AdViewBinder build() {
            return new AdViewBinder(this);
        }
    }

    public final int layoutId;
    public final int titleId;
    public final int textId;
    public final int callToActionId;
    //Image
    public final int mainMediaId;
    //AdmobMediaView
    public final int admMediaId;
    //FbMediaView
    public final int fbMediaId;
    public final int iconImageId;
    public final int iconFbId;
    public final int privacyInformationId;
    public final int privacymopubInformationId;
    public final int starLevelLayoutId;
    public final int adFlagId;

    public final Map<String, Integer> extras;

    private AdViewBinder(final Builder builder) {
        this.layoutId = builder.layoutId;
        this.titleId = builder.titleId;
        this.textId = builder.textId;
        this.callToActionId = builder.callToActionId;
        this.mainMediaId = builder.mainMediaId;
        this.iconImageId = builder.iconImageId;
        this.iconFbId = builder.iconFbId;
        this.privacyInformationId = builder.privacyInformationId;
        this.privacymopubInformationId = builder.privacymopubInformationId;
        this.starLevelLayoutId = builder.starLevelLayoutId;
        this.admMediaId = builder.admMediaId;
        this.fbMediaId = builder.fbMediaId;
        this.extras = builder.extras;
        this.adFlagId = builder.adFlagId;
    }
}