package video.report.mediaplayer.billing;

import android.app.Activity;
import android.os.Bundle;
import android.util.Log;

import androidx.annotation.Nullable;

import com.android.billingclient.api.AcknowledgePurchaseParams;
import com.android.billingclient.api.AcknowledgePurchaseResponseListener;
import com.android.billingclient.api.BillingClient;
import com.android.billingclient.api.BillingClientStateListener;
import com.android.billingclient.api.BillingResult;
import com.android.billingclient.api.Purchase;
import com.android.billingclient.api.PurchasesUpdatedListener;
import com.android.billingclient.api.SkuDetails;
import com.android.billingclient.api.SkuDetailsParams;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.List;

import javax.inject.Inject;

import video.report.mediaplayer.MyApplication;
import video.report.mediaplayer.constant.Constants;
import video.report.mediaplayer.firebase.FireBaseEventUtils;
import video.report.mediaplayer.preference.UserPreferences;

import static video.report.mediaplayer.constant.Constants.REMOVE_AD;
import static video.report.mediaplayer.firebase.Events.AD_FREE_RESULT;

public class BillingManager implements PurchasesUpdatedListener {

    private BillingClient billingClient;
    private AcknowledgePurchaseResponseListener acknowledgePurchaseResponseListener;

    @Inject
    public UserPreferences userPreference;

    private static final String TAG = "jsonTag";

    public BillingManager(Activity activity) {
        try {
            MyApplication.getAppComponent().inject(this);
        } catch (Exception e) {
            // MyApplication.e
        }
        billingClient = BillingClient.newBuilder(activity).enablePendingPurchases().setListener(this).build();
        acknowledgePurchaseResponseListener = billingResult -> {
            if (billingResult == null)
                return;
        };
    }

    public void checkUserBuyedState() {
        if (billingClient == null) {
            return;
        }
        try {
            billingClient.startConnection(new BillingClientStateListener() {
                @Override
                public void onBillingSetupFinished(BillingResult billingResult) {
                    if (billingResult.getResponseCode() == BillingClient.BillingResponseCode.OK) {
                        List<String> skuList = new ArrayList<>();
                        skuList.add(REMOVE_AD);
                        
                        SkuDetailsParams.Builder params = SkuDetailsParams.newBuilder();
                        params.setSkusList(skuList).setType(BillingClient.SkuType.INAPP);
                        billingClient.querySkuDetailsAsync(params.build(),
                                (billingResult1, skuDetailsList) -> {
                                    if (billingResult1.getResponseCode() == BillingClient.BillingResponseCode.OK && skuDetailsList != null) {
                                        for (SkuDetails skuDetails : skuDetailsList) {
                                                userPreference.setPurchasePrice(skuDetails.getPrice());
                                        }
                                    }
                                });

                                    Purchase.PurchasesResult purchaseResult = billingClient.queryPurchases(BillingClient.SkuType.INAPP);
                                    if (purchaseResult == null || purchaseResult.getPurchasesList() == null||purchaseResult.getPurchasesList().size()==0) {
                                        userPreference.setAlreadyPurchase(false);
                                        return;
                                    }

                                    for (int i = 0; i < purchaseResult.getPurchasesList().size(); i++) {
                                        if (purchaseResult.getPurchasesList().get(i).getPurchaseState() == Purchase.PurchaseState.PURCHASED) {
                                            String jsonString = purchaseResult.getPurchasesList().get(i).getOriginalJson();
                                            String reFoundID = "******";
                                            try {
                                                JSONObject obj = new JSONObject(jsonString);
                                                reFoundID = obj.getString("orderId");
                                            } catch (JSONException e) {
                                                e.printStackTrace();
                                            }
                                            for(String sku : purchaseResult.getPurchasesList().get(i).getSkus()){
                                                if ( REMOVE_AD .equals(sku)) {
                                                    userPreference.setAlreadyPurchase(!Constants.REFOUND_USERS.contains(reFoundID));
                                                }
                                            }
                                        } else {
                                            for(String sku : purchaseResult.getPurchasesList().get(i).getSkus()){
                                                if ( REMOVE_AD .equals(sku)) {
                                                    userPreference.setAlreadyPurchase(false);
                                                }
                                            }
                                        }
                                    }
                                }
                    }

                @Override
                public void onBillingServiceDisconnected() {
                    
                } 
               
            });
        } catch (Exception e) {
            //
        }
    }

    @Override
    public void onPurchasesUpdated(BillingResult billingResult, @Nullable List<Purchase> purchases) {
        if (billingResult.getResponseCode() == BillingClient.BillingResponseCode.OK
                && purchases != null) {
            Log.d(TAG, "onPurchasesUpdated:  " + billingResult.getResponseCode());
            for (Purchase purchase : purchases) {
                handlePurchase(purchase);
            }
        } else if (billingResult.getResponseCode() == BillingClient.BillingResponseCode.USER_CANCELED) {
            // Handle an error caused by a user cancelling the purchase flow.
        }
        Bundle values = new Bundle();
        int resultCode = billingResult.getResponseCode();
        String reason = "";
        switch (resultCode) {
            case BillingClient.BillingResponseCode.OK:
                reason = "success";
                break;
            case BillingClient.BillingResponseCode.USER_CANCELED:
                reason = "user cancel";
                break;
            case BillingClient.BillingResponseCode.SERVICE_UNAVAILABLE:
                reason = "service unavailable";
                break;
            case BillingClient.BillingResponseCode.BILLING_UNAVAILABLE:
                reason = "billing api not support";
                break;
            case BillingClient.BillingResponseCode.ITEM_UNAVAILABLE:
                reason = "product not available";
                break;
        }
        values.putString("result", reason);
        FireBaseEventUtils.getInstance().report(AD_FREE_RESULT, values);
    }

    private void handlePurchase(Purchase purchase) {
        if (purchase.getPurchaseState() == Purchase.PurchaseState.PURCHASED) {

            for(String sku : purchase.getSkus()){
                if (REMOVE_AD.equals(sku)) {
                    userPreference.setAlreadyPurchase(true);
                }
            }
            if (!purchase.isAcknowledged()) {
                AcknowledgePurchaseParams acknowledgePurchaseParams =
                        AcknowledgePurchaseParams.newBuilder()
                                .setPurchaseToken(purchase.getPurchaseToken())
                                .build();
                billingClient.acknowledgePurchase(acknowledgePurchaseParams, acknowledgePurchaseResponseListener);
            }
        }
    }

    public void onDestroy() {
        if (billingClient != null) {
            billingClient.endConnection();
        }
    }
}

