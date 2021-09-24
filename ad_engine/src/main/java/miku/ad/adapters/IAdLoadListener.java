package miku.ad.adapters;

import java.util.List;

public interface IAdLoadListener{
    void onAdLoaded(IAdAdapter ad);

    void onAdClicked(IAdAdapter ad);

    void onAdClosed(IAdAdapter ad);

    void onAdListLoaded(List<IAdAdapter> ads);

    void onError(String error);

    void onRewarded(IAdAdapter ad);
}
