package com.barbrdo.app.activities;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.os.Bundle;
import android.support.v4.content.LocalBroadcastManager;
import android.text.TextUtils;
import android.view.MenuItem;
import android.view.View;
import android.view.WindowManager;
import android.widget.ImageView;
import android.widget.TextView;

import com.barbrdo.app.R;
import com.barbrdo.app.dataobject.CustomerAppointment;
import com.barbrdo.app.dataobject.CustomerBarbers;
import com.barbrdo.app.dataobject.Rating;
import com.barbrdo.app.dataobject.RequestAccepted;
import com.barbrdo.app.dataobject.RequestCheckInInput;
import com.barbrdo.app.interfaces.DialogActionCallback;
import com.barbrdo.app.interfaces.ServiceRedirection;
import com.barbrdo.app.managers.CustomerManager;
import com.barbrdo.app.utils.Constants;
import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;

import java.lang.reflect.Type;

public class RequestAcceptedActivity extends AppActivity implements ServiceRedirection, View.OnClickListener {
    private Context mContext;
    private ImageView imageViewProfilePicture;
    private ImageView imageViewFavorite;
    private TextView textViewUsername;
    private TextView textViewTime;
    private TextView textViewAddress;
    private TextView textViewRatings;
    private TextView textViewLocation;
    private TextView textViewCancel;
    private TextView textViewSendMessage;
    private RequestAccepted requestAccepted;
    private CustomerBarbers.Datum barberData;
    private CustomerManager customerManagerObj;
    private boolean isRequestAccepted = false;
    private CustomerAppointment.Confirm confirmObj;
    private Bundle extras;
    public LocalBroadcastManager bManager;
    public static final String RECEIVE_BARBER_MESSAGES = "com.barbrdo.app.barber";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_request_accepted);
        getWindow().addFlags(WindowManager.LayoutParams.FLAG_KEEP_SCREEN_ON);
        mContext = RequestAcceptedActivity.this;
        initData();
        bindControls();

        AwaitingBarberApprovalActivity.IS_DENIED = false;

        extras = getIntent().getExtras();
        if (extras != null) {
            isRequestAccepted = extras.getBoolean(Constants.BundleKeyTag.IS_REQUEST_ACCEPTED);
            if (!isRequestAccepted) {
                Gson gson = new Gson();
                Type collectionType = new TypeToken<RequestAccepted>() {
                }.getType();
                requestAccepted = gson.fromJson(extras.getString(Constants.BundleKeyTag.SERIALIZED_DATA), collectionType);
                barberData = (CustomerBarbers.Datum) extras.getSerializable(Constants.BundleKeyTag.BARBER_DATA);
            }
            setData();
        }

        bManager = LocalBroadcastManager.getInstance(this);
        IntentFilter intentFilter = new IntentFilter();
        intentFilter.addAction(RECEIVE_BARBER_MESSAGES);
        bManager.registerReceiver(bReceiver, intentFilter);
    }

    @Override
    void initData() {
        imageViewProfilePicture = getView(R.id.iv_profile_picture);
        textViewUsername = getView(R.id.tv_username);
        textViewRatings = getView(R.id.tv_ratings);
        textViewCancel = getView(R.id.tv_cancel);
        textViewSendMessage = getView(R.id.tv_send_message);
        textViewLocation = getView(R.id.tv_location);
        textViewTime = getView(R.id.tv_time);
        textViewAddress = getView(R.id.tv_address);
        imageViewFavorite = getView(R.id.iv_favourite);

        customerManagerObj = new CustomerManager(this, this);
    }

    @Override
    void bindControls() {
        utilObj.setSpannableTextView(textViewCancel, R.drawable.ic_deny_red_small, getString(R.string.cancel_));
        utilObj.setSpannableTextView(textViewSendMessage, R.drawable.contact, getString(R.string.send_message));

        textViewCancel.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                cancel(v);
            }
        });

        textViewSendMessage.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Bundle bundle = new Bundle();
                if (!isRequestAccepted) {
                    bundle.putSerializable(Constants.BundleKeyTag.SERIALIZED_DATA, requestAccepted);
                    bundle.putSerializable(Constants.BundleKeyTag.BARBER_DATA, barberData);
                } else
                    bundle.putSerializable(Constants.BundleKeyTag.SERIALIZED_DATA, confirmObj);
                bundle.putBoolean(Constants.BundleKeyTag.IS_REQUEST_ACCEPTED, isRequestAccepted);
                startActivity(ContactBarberActivity.class, bundle);
            }
        });
    }

    private void setData() {
        if (!isRequestAccepted) {
            if (requestAccepted != null) {
                textViewRatings.setText(String.format("%.1f", requestAccepted.barberInfo.ratingScore));
                textViewUsername.setText(requestAccepted.barberInfo.firstName + " " + requestAccepted.barberInfo.lastName);
                if (barberData.barberShops != null)
                    textViewLocation.setText(barberData.barberShops.name + " (" + Math.round(barberData.distance) + " mi)");

                if (!TextUtils.isEmpty(barberData.picture)) {
                    imageLoader.displayImage(sessionManager.getImageBaseUrl() + barberData.picture, imageViewProfilePicture, options);
                } else {
                    imageViewProfilePicture.setImageResource(R.drawable.placeholder_user);
                }

                if (barberData.isFavourite)
                    imageViewFavorite.setVisibility(View.VISIBLE);
                else
                    imageViewFavorite.setVisibility(View.GONE);

                textViewTime.setText("Be there @ " + utilObj.getMyDateFormat(requestAccepted.appointmentInfo.appointmentDate,
                        Constants.DateFormat.YYYYMMDDTHHMMSS, Constants.DateFormat.HHMMA).toUpperCase() + "\n" + "and be next in the chair");

                textViewAddress.setText(barberData.barberShops.name + "\n" + barberData.barberShops.address + "\n"
                        + barberData.barberShops.city + ", " + barberData.barberShops.state + " " + barberData.barberShops.zip);

                imageViewProfilePicture.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        Bundle bundle = new Bundle();
                        bundle.putString(Constants.BundleKeyTag.BARBER_ID, barberData.id);
                        startActivity(BarberProfileActivity.class, bundle);
                    }
                });
            }
        } else {
            confirmObj = (CustomerAppointment.Confirm) extras.getSerializable(Constants.BundleKeyTag.SERIALIZED_DATA);
            if (confirmObj != null) {
                if (confirmObj.barberId.ratings.size() > 0) {
                    float avg = 0;
                    for (Rating rating : confirmObj.barberId.ratings) {
                        avg = avg + rating.score;
                    }
                    avg = avg / confirmObj.barberId.ratings.size();
                    textViewRatings.setText(String.format("%.1f", avg));
                } else {
                    textViewRatings.setText("0.0");
                }
                textViewUsername.setText(confirmObj.barberId.firstName + " " + confirmObj.barberId.lastName);
                if (confirmObj.shopId != null)
                    textViewLocation.setText(confirmObj.shopId.name);

                if (!TextUtils.isEmpty(confirmObj.barberId.picture)) {
                    imageLoader.displayImage(sessionManager.getImageBaseUrl() + confirmObj.barberId.picture, imageViewProfilePicture, options);
                } else {
                    imageViewProfilePicture.setImageResource(R.drawable.placeholder_user);
                }

                imageViewFavorite.setVisibility(View.GONE);

                textViewAddress.setText(confirmObj.shopId.name + "\n" + confirmObj.shopId.address + "\n"
                        + confirmObj.shopId.city + ", " + confirmObj.shopId.state + " " + confirmObj.shopId.zip);

                textViewTime.setText("Be there @ " + utilObj.getMyDateFormat(confirmObj.appointmentDate,
                        Constants.DateFormat.YYYYMMDDTHHMMSS, Constants.DateFormat.HHMMA).toUpperCase() + "\n" + "and be next in the chair");

                imageViewProfilePicture.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        Bundle bundle = new Bundle();
                        bundle.putString(Constants.BundleKeyTag.BARBER_ID, confirmObj.barberId.id);
                        startActivity(BarberProfileActivity.class, bundle);
                    }
                });
            }
        }
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
        switch (taskID) {
            case Constants.TaskID.CANCEL_APPOINTMENT:
                utilObj.showToast(mContext, appInstance.message, 1);
                clearAllActivitiesFromBackStack(mContext, NavigationDrawerActivity.class);
                break;
        }
    }

    @Override
    public void onFailureRedirection(String errorMessage) {
        utilObj.stopLoader();
        showSnackBar(errorMessage);
        finish();
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
        }
    }

    public void mapIt(View view) {
        if (!isRequestAccepted)
            openGoogleMap(mContext, barberData.barberShops.latLong.get(1), barberData.barberShops.latLong.get(0), barberData.barberShops.name,
                    barberData.barberShops.address + " " + barberData.barberShops.city + " " + barberData.barberShops.state + " " + barberData.barberShops.zip);
        else
            openGoogleMap(mContext, confirmObj.shopId.latLong.get(1), confirmObj.shopId.latLong.get(0), confirmObj.shopId.name,
                    confirmObj.shopId.address + " " + confirmObj.shopId.city + " " + confirmObj.shopId.state + " " + confirmObj.shopId.zip);
    }

    public void cancel(View view) {
        utilObj.showAlertDialog(this, R.string.cancel_request, R.string.are_you_sure_you_want_to_cancel_request, R.string.ok, R.string.cancel, new DialogActionCallback() {
            @Override
            public void doOnPositive() {
                utilObj.startLoader(mContext);
                RequestCheckInInput requestCheckInInput = new RequestCheckInInput();
                requestCheckInInput.requestCancelOn = utilObj.getCurrentDateToString(Constants.DateFormat.YYYYMMDDTHHMMSS);
                if (!isRequestAccepted)
                    customerManagerObj.cancelAppointment(requestAccepted.appointmentInfo.id, requestCheckInInput);
                else
                    customerManagerObj.cancelAppointment(confirmObj.id, requestCheckInInput);
            }

            @Override
            public void doOnNegative() {
            }
        });
    }

    private BroadcastReceiver bReceiver = new BroadcastReceiver() {
        @Override
        public void onReceive(Context context, Intent intent) {
            if (intent.getAction().equals(RECEIVE_BARBER_MESSAGES)) {
                if (AwaitingBarberApprovalActivity.IS_DENIED) {
                    return;
                }
                String message = intent.getStringExtra(Constants.BundleKeyTag.MESSAGE);
                if (message.equalsIgnoreCase("barber_cancel_appointment")) {
                    utilObj.showToast(mContext, "We are sorry, but it looks like the barber has cancelled your request.", 2);
                    clearAllActivitiesFromBackStack(mContext, NavigationDrawerActivity.class);
                    finish();
                }
            }
        }
    };
}
