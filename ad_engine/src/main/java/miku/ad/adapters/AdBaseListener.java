package miku.ad.adapters;

import java.util.List;

public class AdBaseListener implements IAdLoadListener {
    public void onAdLoaded() {
    }

    @Override
    public void onAdLoaded(IAdAdapter ad) {
        onAdLoaded();
    }

    @Override
    public void onAdClicked(IAdAdapter ad) {

    }

    @Override
    public void onAdClosed(IAdAdapter ad) {

    }

    @Override
    public void onAdListLoaded(List<IAdAdapter> ads) {

    }

    @Override
    public void onError(String error) {

    }

    @Override
    public void onRewarded(IAdAdapter ad) {

    }
}
