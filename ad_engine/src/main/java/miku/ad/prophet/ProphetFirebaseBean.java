package miku.ad.prophet;

import com.google.gson.annotations.SerializedName;

import java.io.Serializable;
import java.util.List;

public class ProphetFirebaseBean implements Serializable {
    @SerializedName("version")
    private int version;

    @SerializedName("src_list")
    private List<ProphetSrcBean> mProphetSrcList;

    public int getVersion() {
        return version;
    }

    public void setVersion(int version) {
        this.version = version;
    }

    public List<ProphetSrcBean> getProphetSrcList() {
        return mProphetSrcList;
    }

    public void setProphetSrcList(List<ProphetSrcBean> prophetSrcList) {
        this.mProphetSrcList = prophetSrcList;
    }

    @Override
    public String toString() {
        return "ProphetFirebaseBean{" +
                "version=" + version +
                ", mProphetSrcList=" + mProphetSrcList +
                '}';
    }
}
