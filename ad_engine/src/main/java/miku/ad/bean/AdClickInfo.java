package miku.ad.bean;

import miku.ad.adapters.IAdAdapter;

public class AdClickInfo {
    private IAdAdapter ad;
    private String key;

    public IAdAdapter getAd() {
        return ad;
    }

    public void setAd(IAdAdapter ad) {
        this.ad = ad;
    }

    public String getKey() {
        return key;
    }

    public void setKey(String key) {
        this.key = key;
    }
}
