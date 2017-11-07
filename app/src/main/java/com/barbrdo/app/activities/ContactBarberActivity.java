package com.barbrdo.app.activities;

import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AlertDialog;
import android.text.Editable;
import android.text.SpannableString;
import android.text.TextUtils;
import android.text.TextWatcher;
import android.text.style.UnderlineSpan;
import android.view.MenuItem;
import android.view.View;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;

import com.barbrdo.app.R;
import com.barbrdo.app.dataobject.CustomerAppointment;
import com.barbrdo.app.dataobject.CustomerBarbers;
import com.barbrdo.app.dataobject.MessageInput;
import com.barbrdo.app.dataobject.Rating;
import com.barbrdo.app.dataobject.RequestAccepted;
import com.barbrdo.app.dataobject.RequestCheckInInput;
import com.barbrdo.app.interfaces.DialogActionCallback;
import com.barbrdo.app.interfaces.ServiceRedirection;
import com.barbrdo.app.managers.BarberManager;
import com.barbrdo.app.managers.CustomerManager;
import com.barbrdo.app.utils.Constants;

public class ContactBarberActivity extends AppActivity implements ServiceRedirection, View.OnClickListener {

    private Context mContext;
    private ImageView imageViewBarberProfilePic;
    private TextView textViewUsername;
    private TextView textViewLocation;
    private TextView textViewRatings;
    private TextView textViewTime;
    private TextView textViewMins;
    private EditText editTextMessage;
    private TextView textViewSend;
    private CustomerManager customerManagerObj;
    private BarberManager barberManagerObj;
    private boolean isRequestAccepted;
    private CustomerBarbers.Datum barberData;
    private CustomerAppointment.Confirm confirmObj;
    private RequestAccepted requestAccepted;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_contact_barber);
        mContext = ContactBarberActivity.this;
        initData();
        bindControls();

        Bundle extras = getIntent().getExtras();
        if (extras != null) {
            isRequestAccepted = extras.getBoolean(Constants.BundleKeyTag.IS_REQUEST_ACCEPTED);
            if (!isRequestAccepted) {
                barberData = (CustomerBarbers.Datum) extras.getSerializable(Constants.BundleKeyTag.BARBER_DATA);
                requestAccepted = (RequestAccepted) extras.getSerializable(Constants.BundleKeyTag.SERIALIZED_DATA);
            } else {
                confirmObj = (CustomerAppointment.Confirm) extras.getSerializable(Constants.BundleKeyTag.SERIALIZED_DATA);
            }
            setData();
        }
    }

    @Override
    void initData() {
        setUpToolBar("");
        imageViewBarberProfilePic = getView(R.id.iv_profile_picture);
        textViewUsername = getView(R.id.tv_username);
        textViewLocation = getView(R.id.tv_location);
        textViewRatings = getView(R.id.tv_ratings);
        textViewTime = getView(R.id.tv_services);
        textViewMins = getView(R.id.tv_mins);
        editTextMessage = getView(R.id.et_message);
        textViewSend = getView(R.id.tv_send);

        customerManagerObj = new CustomerManager(this, this);
        barberManagerObj = new BarberManager(this, this);
    }

    private void setData() {
        if (!isRequestAccepted) {
            textViewUsername.setText(barberData.firstName);
            textViewLocation.setText(barberData.barberShops.name);

            if (barberData.ratings.size() > 0) {
                float avg = 0;
                for (Rating rating : barberData.ratings) {
                    avg = avg + rating.score;
                }
                avg = avg / barberData.ratings.size();
                textViewRatings.setText(String.format("%.1f", avg));
            } else {
                textViewRatings.setText("0.0");
            }

            textViewTime.setText("At: " + utilObj.getMyDateFormat(requestAccepted.appointmentInfo.appointmentDate,
                    Constants.DateFormat.YYYYMMDDTHHMMSS, Constants.DateFormat.HHMMA).toUpperCase());

            if (!TextUtils.isEmpty(barberData.picture)) {
                imageLoader.displayImage(sessionManager.getImageBaseUrl() + barberData.picture, imageViewBarberProfilePic, options);
            }
        } else {
            textViewUsername.setText(confirmObj.barberId.firstName);
            textViewLocation.setText(confirmObj.shopId.name);

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

            if (!TextUtils.isEmpty(confirmObj.barberId.picture)) {
                imageLoader.displayImage(sessionManager.getImageBaseUrl() + confirmObj.barberId.picture, imageViewBarberProfilePic, options);
            }

            textViewTime.setText("At: " + utilObj.getMyDateFormat(confirmObj.appointmentDate,
                    Constants.DateFormat.YYYYMMDDTHHMMSS, Constants.DateFormat.HHMMA).toUpperCase());
        }

        imageViewBarberProfilePic.setOnClickListener(this);
    }

    @Override
    void bindControls() {
        addUnderline("5");
        utilObj.setSpannableTextView(textViewSend, R.drawable.send, getString(R.string.send));
        textViewMins.setOnClickListener(this);
        textViewSend.setEnabled(true);
        textViewSend.setBackgroundResource(R.color.button_blue);

        editTextMessage.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {

            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
//                if (s.length() > 0) {
//                    textViewSend.setEnabled(true);
//                    textViewSend.setBackgroundResource(R.drawable.rounded_bottom_red);
//                } else {
//                    textViewSend.setEnabled(false);
//                    textViewSend.setBackgroundResource(R.drawable.rounded_bottom_gray);
//                }
            }

            @Override
            public void afterTextChanged(Editable s) {

            }
        });
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
    public void onSuccessRedirection(int taskID) {
        utilObj.stopLoader();
        switch (taskID) {
            case Constants.TaskID.MESSAGE_BARBER:
                utilObj.showToast(mContext, appInstance.message, 1);
                finish();
                break;

            case Constants.TaskID.BARBER_CANCEL_APPOINTMENT:
                Intent iCancel = new Intent(mContext, NavigationDrawerActivity.class);
                iCancel.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
                startActivity(iCancel);
                break;

            case Constants.TaskID.BARBER_RESCHEDULE_APPOINTMENT:
                Intent iReschedule = new Intent(mContext, NavigationDrawerActivity.class);
                iReschedule.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
                startActivity(iReschedule);
                break;

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
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.iv_profile_picture:
                Bundle bundle = new Bundle();
                if (!isRequestAccepted) {
                    bundle.putString(Constants.BundleKeyTag.BARBER_ID, barberData.id);
                } else {
                    bundle.putString(Constants.BundleKeyTag.BARBER_ID, confirmObj.barberId.id);
                }
                startActivity(BarberProfileActivity.class, bundle);
                break;

            case R.id.tv_mins:
                showTimeDialog();
                break;
        }
    }

    private void showTimeDialog() {
        AlertDialog.Builder builder = new AlertDialog.Builder(mContext, R.style.AppCompatAlertDialogStyle);
        builder.setItems(R.array.late_min, new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int position) {
                if (position == 0)
                    addUnderline("5");
                else if (position == 1)
                    addUnderline("10");
                else
                    addUnderline("15");
            }
        });
        builder.show();
    }

    private void addUnderline(String text) {
        SpannableString content = new SpannableString(text);
        content.setSpan(new UnderlineSpan(), 0, content.length(), 0);
        textViewMins.setText(content);
    }

    public void sendMessage(View view) {
        utilObj.startLoader(mContext);
        String text = "I am running late by " + textViewMins.getText().toString() + " min.\n" + editTextMessage.getText().toString();
        MessageInput messageInput = new MessageInput();
        messageInput.text = text;
        if (!isRequestAccepted)
            messageInput.barberId = barberData.id;
        else
            messageInput.barberId = confirmObj.barberId.id;
        customerManagerObj.messageToBarber(messageInput);
    }

    public void cancelAppointment(View view) {
        utilObj.showAlertDialog(this, R.string.cancel_request, R.string.are_you_sure_you_want_to_cancel_request, R.string.ok, R.string.cancel, new DialogActionCallback() {
            @Override
            public void doOnPositive() {
                utilObj.startLoader(mContext);
                RequestCheckInInput requestCheckInInput = new RequestCheckInInput();
                requestCheckInInput.requestCancelOn = utilObj.getCurrentDateToString(Constants.DateFormat.YYYYMMDDTHHMMSS);
                if (!isRequestAccepted) {
                    customerManagerObj.cancelAppointment(requestAccepted.appointmentInfo.id, requestCheckInInput);
                } else {
                    customerManagerObj.cancelAppointment(confirmObj.id, requestCheckInInput);
                }

            }

            @Override
            public void doOnNegative() {
            }
        });
    }
}
