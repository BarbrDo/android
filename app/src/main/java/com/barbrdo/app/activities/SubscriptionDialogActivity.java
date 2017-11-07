package com.barbrdo.app.activities;

import android.app.Activity;
import android.app.PendingIntent;
import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.content.IntentSender;
import android.content.ServiceConnection;
import android.os.Bundle;
import android.os.IBinder;
import android.os.RemoteException;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.MenuItem;
import android.view.View;
import android.widget.TextView;

import com.android.vending.billing.IInAppBillingService;
import com.barbrdo.app.R;
import com.barbrdo.app.adapters.SubscriptionPlanAdapter;
import com.barbrdo.app.dataobject.SubscriptionPlans;
import com.barbrdo.app.dataobject.SubscriptionSuccessInput;
import com.barbrdo.app.dataobject.TransactionResponse;
import com.barbrdo.app.interfaces.DialogActionCallback;
import com.barbrdo.app.interfaces.ServiceRedirection;
import com.barbrdo.app.managers.CommonManager;
import com.barbrdo.app.utils.Constants;
import com.google.gson.Gson;

import org.json.JSONObject;

import java.util.ArrayList;
import java.util.List;

public class SubscriptionDialogActivity extends AppActivity implements ServiceRedirection {

    private Context mContext;
    private RecyclerView recyclerViewPlans;
    private CommonManager commonManagerObj;
    protected static final int REQUEST_IN_APP = 10001;
    protected static final int REQUEST_UPGRADE = 10002;
    private SubscriptionPlans.Datum selectedPlan;
    private boolean isUpgrade;
    private boolean isUserProfile;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.dialog_subscription_plans);
        this.setFinishOnTouchOutside(false);
        mContext = SubscriptionDialogActivity.this;

        isUpgrade = getIntent().getBooleanExtra(Constants.BundleKeyTag.IS_UPGRADE, false);
        isUserProfile = getIntent().getBooleanExtra(Constants.BundleKeyTag.IS_USER_PROFILE, false);

        initData();
        bindControls();
    }

    @Override
    void initData() {
        recyclerViewPlans = getView(R.id.rv_plans);
        recyclerViewPlans.setHasFixedSize(true);
        LinearLayoutManager layoutManager = new LinearLayoutManager(mContext);
        layoutManager.setOrientation(LinearLayoutManager.VERTICAL);
        recyclerViewPlans.setLayoutManager(layoutManager);

        commonManagerObj = new CommonManager(this, this);
    }

    @Override
    void bindControls() {
        getSubscriptionPlans();

        getView(R.id.tv_cancel).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (!isUserProfile) {
                    Intent i = new Intent(mContext, LogInActivity.class);
                    i.putExtra(Constants.BundleKeyTag.IS_SUBSCRIPTION_EXPIRED, true);
                    i.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
                    startActivity(i);
                } else {
                    finish();
                }
            }
        });

        getView(R.id.tv_subscribe).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                boolean isItemSelected = false;
                for (SubscriptionPlans.Datum datum : appInstance.subscriptionPlans.data) {
                    if (datum.isSelected) {
                        selectedPlan = datum;
                        if (!isUserProfile) {
//                            subscribeUser(null);
                            purchaseProduct(datum.googleId);
                        } else {
//                            subscribeUser(null);
                            if (appInstance.userProfile.user.subscriptionPrice == 0) {
                                purchaseProduct(datum.googleId);
                            } else {
                                upgradePlan(appInstance.userProfile.user.transactionResponse.get(0).productId, datum.googleId);
                            }
                        }
                    }
                }
            }
        });

        if (isUserProfile) {
            ((TextView) getView(R.id.tv_title)).setText("Please select BarbrDo subscription plan.");
        }
    }

    private void setData() {
        SubscriptionPlanAdapter subscriptionPlanAdapter = new SubscriptionPlanAdapter((AppActivity) mContext);
        SubscriptionPlans.Datum datum = appInstance.subscriptionPlans.data.get(0);
        datum.isSelected = true;
        subscriptionPlanAdapter.setList(appInstance.subscriptionPlans.data);
        recyclerViewPlans.setAdapter(subscriptionPlanAdapter);
    }


    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case android.R.id.home:
                onBackPressed();
                return true;
        }
        return super.onOptionsItemSelected(item);
    }

    @Override
    public void onBackPressed() {

    }

    @Override
    public void onSuccessRedirection(int taskID) {
        utilObj.stopLoader();
        if (taskID == Constants.TaskID.SUBSCRIPTION_PLANS) {
            if (mService == null) {
                bindInAppService();
            }
            if (appInstance.subscriptionPlans.data.size() > 0)
                setData();
        } else if (taskID == Constants.TaskID.SUBSCRIBE_USER) {
            getView(R.id.rl_parent).setVisibility(View.GONE);
            getIntent().removeExtra(Constants.BundleKeyTag.IS_SUBSCRIPTION_EXPIRED);
            utilObj.showAlertDialogNoTitle(mContext, "Subscribed successfully.", "Ok", "", new DialogActionCallback() {
                @Override
                public void doOnPositive() {
                    Intent returnIntent = new Intent();
                    setResult(Activity.RESULT_OK, returnIntent);
                    finish();
                }

                @Override
                public void doOnNegative() {

                }
            });
        }
    }

    @Override
    public void onFailureRedirection(String errorMessage) {
        utilObj.stopLoader();
        showSnackBar(errorMessage);
    }

    public void bindInAppService() {
        Intent serviceIntent =
                new Intent("com.android.vending.billing.InAppBillingService.BIND");
        serviceIntent.setPackage("com.android.vending");
        bindService(serviceIntent, mServiceConn, Context.BIND_AUTO_CREATE);
    }

    private IInAppBillingService mService;

    private ServiceConnection mServiceConn = new ServiceConnection() {
        @Override
        public void onServiceDisconnected(ComponentName name) {
            mService = null;
        }

        @Override
        public void onServiceConnected(ComponentName name,
                                       IBinder service) {
            mService = IInAppBillingService.Stub.asInterface(service);
        }
    };

    @Override
    protected void onDestroy() {
        super.onDestroy();
        if (mService != null) {
            unbindService(mServiceConn);
        }
    }

    public void purchaseProduct(String skuItem) {
        try {
            if (mService != null) {
                Bundle buyIntentBundle = mService.getBuyIntent(5, getPackageName(),
                        skuItem, Constants.InApp.PURCHASE_TYPE, appInstance.userDetail.user.id);
                PendingIntent pendingIntent = buyIntentBundle.getParcelable("BUY_INTENT");

                if (buyIntentBundle.getInt("RESPONSE_CODE") == 0 && pendingIntent != null) {
                    startIntentSenderForResult(pendingIntent.getIntentSender(),
                            REQUEST_IN_APP, new Intent(), 0, 0, 0);
                }
            }
        } catch (RemoteException | IntentSender.SendIntentException e) {
            e.printStackTrace();
        }
    }

    public void upgradePlan(String skuItemOld, String skuItemNew) {
        List<String> listSkuItems = new ArrayList<>();
        listSkuItems.add(skuItemOld);
        try {
            if (mService != null) {
                Bundle buyIntentBundle = mService.getBuyIntentToReplaceSkus(5, getPackageName(), listSkuItems,
                        skuItemNew, Constants.InApp.PURCHASE_TYPE, appInstance.userDetail.user.id);
                PendingIntent pendingIntent = buyIntentBundle.getParcelable("BUY_INTENT");

                if (buyIntentBundle.getInt("RESPONSE_CODE") == 0 && pendingIntent != null) {
                    startIntentSenderForResult(pendingIntent.getIntentSender(),
                            REQUEST_UPGRADE, new Intent(), 0, 0, 0);
                }
            }
        } catch (RemoteException | IntentSender.SendIntentException e) {
            e.printStackTrace();
        }
    }

    public Bundle getProductDetail(String sku_item) {
        try {
            if (mService != null) {
                ArrayList<String> skuList = new ArrayList<>();
                skuList.add(sku_item);
                Bundle skuBundle = new Bundle();
                skuBundle.putStringArrayList("ITEM_ID_LIST", skuList);

                return mService.getSkuDetails(3,
                        getPackageName(), Constants.InApp.PURCHASE_TYPE, skuBundle);
            }
        } catch (RemoteException e) {
            e.printStackTrace();
        }
        return null;
    }

    private void fetchProductDetailFromBundle(Bundle skuDetails) {
        try {
            int response = skuDetails.getInt("RESPONSE_CODE");
            String price = "";

            if (response == 0) {

                ArrayList<String> responseList
                        = skuDetails.getStringArrayList("DETAILS_LIST");

                if (responseList != null && responseList.size() > 0) {

                    for (String thisResponse : responseList) {
                        JSONObject object = new JSONObject(thisResponse);
                        price = object.getString("price");
                    }
                }
            }
            String priceText = " - " + price + " a month";
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        if (requestCode == REQUEST_IN_APP) {
            switch (resultCode) {
                case Activity.RESULT_OK:
                    String json = data.getStringExtra("INAPP_PURCHASE_DATA");
                    Gson gson = new Gson();
                    TransactionResponse transactionResponse = gson.fromJson(json, TransactionResponse.class);
                    SubscriptionSuccessInput subscriptionSuccessInput = new SubscriptionSuccessInput();
                    subscriptionSuccessInput.planId = selectedPlan.id;
                    List<TransactionResponse> listTransactionResponse = new ArrayList<>();
                    listTransactionResponse.add(transactionResponse);
                    subscriptionSuccessInput.transactionResponse = listTransactionResponse;
                    subscribeUser(subscriptionSuccessInput);
                    break;
                case Activity.RESULT_CANCELED:
                    utilObj.showToast(mContext, "Transaction failed.", 1);
                    break;
            }
        } else if (requestCode == REQUEST_UPGRADE) {
            switch (resultCode) {
                case Activity.RESULT_OK:
                    String json = data.getStringExtra("INAPP_PURCHASE_DATA");
                    Gson gson = new Gson();
                    TransactionResponse transactionResponse = gson.fromJson(json, TransactionResponse.class);
                    SubscriptionSuccessInput subscriptionSuccessInput = new SubscriptionSuccessInput();
                    subscriptionSuccessInput.planId = selectedPlan.id;
                    List<TransactionResponse> listTransactionResponse = new ArrayList<>();
                    listTransactionResponse.add(transactionResponse);
                    subscriptionSuccessInput.transactionResponse = listTransactionResponse;
                    subscribeUser(subscriptionSuccessInput);
                    break;
                case Activity.RESULT_CANCELED:
                    utilObj.showToast(mContext, "Transaction failed.", 1);
                    break;
            }
        }
    }

    private void getSubscriptionPlans() {
        utilObj.startLoader(mContext);
        commonManagerObj.getSubscriptionPlans();
    }

    private void subscribeUser(SubscriptionSuccessInput subscriptionSuccessInput) {
        utilObj.startLoader(mContext);

//        SubscriptionSuccessInput si = new SubscriptionSuccessInput();
//        si.planId = selectedPlan.id;
//        TransactionResponse transactionResponse = new TransactionResponse();
//        transactionResponse.orderId = "GPA.1234-5678-9012-34567";
//        transactionResponse.packageName = "com.app.barbrdo";
//        transactionResponse.productId = selectedPlan.id;
//        transactionResponse.purchaseTime = "1345678900000";
//        transactionResponse.purchaseState = 0;
//        transactionResponse.developerPayload = "bGoa+V7g/yqDXvKRqq+JTFn4uQZbPiQJo4pf9RzJ";
//        transactionResponse.purchaseToken = "123-token-up-to-1000-characters";

//    {
//            "orderId":"GPA.1234-5678-9012-34567",
//            "packageName":"com.example.app",
//            "productId":"exampleSku",
//            "purchaseTime":1345678900000,
//            "purchaseState":0,
//            "developerPayload":"bGoa+V7g/yqDXvKRqq+JTFn4uQZbPiQJo4pf9RzJ",
//            "purchaseToken":"opaque-token-up-to-1000-characters"
//    }

//        List<TransactionResponse> rtr = new ArrayList<>();
//        rtr.add(transactionResponse);
//        si.transactionResponse = rtr;
//
//        Gson gson = new Gson();
//        String json = gson.toJson(si);
//
//        Log.e("INPUT", json);

        commonManagerObj.subscribeUser(subscriptionSuccessInput);
    }
}
