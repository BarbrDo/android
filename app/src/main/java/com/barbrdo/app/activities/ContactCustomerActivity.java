package com.barbrdo.app.activities;

import android.app.Activity;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AlertDialog;
import android.text.Editable;
import android.text.TextUtils;
import android.text.TextWatcher;
import android.view.MenuItem;
import android.view.View;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.RadioButton;
import android.widget.TextView;

import com.barbrdo.app.R;
import com.barbrdo.app.dataobject.BarberHome;
import com.barbrdo.app.dataobject.CancelReasonInput;
import com.barbrdo.app.dataobject.MessageInput;
import com.barbrdo.app.interfaces.ServiceRedirection;
import com.barbrdo.app.managers.BarberManager;
import com.barbrdo.app.utils.Constants;

public class ContactCustomerActivity extends AppActivity implements ServiceRedirection, View.OnClickListener {

    private Context mContext;
    private ImageView imageViewProfilePic;
    private TextView textViewBarberName;
    private TextView textViewDateTime;
    private TextView textViewSend;
    private TextView textViewCancelAppointment;
    private EditText editTextMessage;
    private BarberManager barberManagerObj;
    private BarberHome.Appointment appointmentData;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_contact_customer);
        mContext = ContactCustomerActivity.this;
        initData();
        bindControls();

        Bundle extras = getIntent().getExtras();
        if (extras != null) {
            appointmentData = (BarberHome.Appointment) extras.getSerializable(Constants.BundleKeyTag.SERIALIZED_DATA);
            setData();
        }
    }

    @Override
    void initData() {
        setUpToolBar("");
        imageViewProfilePic = getView(R.id.iv_profile_picture);
        textViewBarberName = getView(R.id.tv_username);
        textViewDateTime = getView(R.id.tv_date_time);
        textViewSend = getView(R.id.tv_send);
        editTextMessage = getView(R.id.et_message);
        textViewCancelAppointment = getView(R.id.tv_cancel_appointment);

        barberManagerObj = new BarberManager(this, this);
    }

    private void setData() {
        textViewBarberName.setText(appointmentData.customerInfo.get(0).firstName + " " + appointmentData.customerInfo.get(0).lastName);
        textViewDateTime.setText(utilObj.getMyDateFormat(appointmentData.appointmentDate, Constants.DateFormat.YYYYMMDDTHHMMSS,
                Constants.DateFormat.DDMMYYHHMMA).toUpperCase());

        if (!TextUtils.isEmpty(appointmentData.customerInfo.get(0).picture)) {
            imageLoader.displayImage(sessionManager.getImageBaseUrl() + appointmentData.customerInfo.get(0).picture, imageViewProfilePic, options);
        }

        imageViewProfilePic.setOnClickListener(this);
    }

    @Override
    void bindControls() {
        utilObj.setSpannableTextView(textViewSend, R.drawable.send, getString(R.string.send));
        utilObj.setSpannableTextView(textViewCancelAppointment, R.drawable.ic_clear_white_18dp, getString(R.string.cancel_request));
        textViewSend.setEnabled(false);
        textViewSend.setBackgroundResource(R.color.button_dark_gray);

        editTextMessage.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {

            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                if (s.length() > 0) {
                    textViewSend.setEnabled(true);
                    textViewSend.setBackgroundResource(R.color.button_blue);
                } else {
                    textViewSend.setEnabled(false);
                    textViewSend.setBackgroundResource(R.color.button_dark_gray);
                }
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
            case Constants.TaskID.CANCEL_APPOINTMENT_BARBER:
                Intent iCancel = new Intent(mContext, NavigationDrawerActivity.class);
                iCancel.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
                startActivity(iCancel);
                break;

            case Constants.TaskID.MESSAGE_CUSTOMER:
                utilObj.showToast(mContext, appInstance.message, 1);
                editTextMessage.setText("");
                textViewSend.setEnabled(false);
                textViewSend.setBackgroundResource(R.color.button_dark_gray);
                break;
        }
    }

    @Override
    public void onFailureRedirection(String errorMessage) {
        utilObj.stopLoader();
        showSnackBar(errorMessage);
    }

    public void sendMessage(View view) {
        utilObj.startLoader(mContext);
        MessageInput messageInput = new MessageInput();
        messageInput.text = editTextMessage.getText().toString();
        messageInput.customerId = appointmentData.customerInfo.get(0).id;
        barberManagerObj.messageToCustomer(messageInput);
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.iv_profile_picture:
                Bundle bundle = new Bundle();
                bundle.putString(Constants.BundleKeyTag.BARBER_ID, appointmentData.customerInfo.get(0).id);
                startActivity(CustomerProfileActivity.class, bundle);
                break;

            case R.id.tv_mins:
                break;
        }
    }

    public void cancelAppointment(View view) {
        AlertDialog.Builder dialog = new AlertDialog.Builder(mContext, R.style.AppCompatAlertDialogStyle);
        dialog.setCancelable(false);
        View dialogView = View.inflate(this, R.layout.dialog_reason_cancel_appt, null);
        dialog.setView(dialogView);
        final RadioButton radioButtonNoShow = (RadioButton) dialogView.findViewById(R.id.rb_no_show);
        radioButtonNoShow.setChecked(true);
        final RadioButton radioButtonOther = (RadioButton) dialogView.findViewById(R.id.rb_other);
        final EditText editTextReason = (EditText) dialogView.findViewById(R.id.et_reason);

        radioButtonNoShow.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                radioButtonOther.setChecked(false);
                radioButtonNoShow.setChecked(true);
                editTextReason.setVisibility(View.GONE);
                editTextReason.setText("");
            }
        });

        radioButtonOther.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                radioButtonOther.setChecked(true);
                radioButtonNoShow.setChecked(false);
                editTextReason.setVisibility(View.VISIBLE);
            }
        });

        dialog.setPositiveButton("Ok", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int whichButton) {
                dialog.dismiss();
                if (!radioButtonNoShow.isChecked())
                    if (!TextUtils.isEmpty(editTextReason.getText().toString().trim()))
                        cancelAppointment(appointmentData.id, editTextReason.getText().toString().trim());
                    else
                        utilObj.showToast(mContext, "Please enter reason.", 2);
                else{
                    cancelAppointment(appointmentData.id, "Customer was a no show");
                }
            }

        });

        dialog.setNegativeButton("Cancel", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                dialog.dismiss();
            }
        });

        if (!((Activity) mContext).isFinishing()) {
            dialog.show();
        }
    }

    private void cancelAppointment(String apptId, String reason) {
        utilObj.startLoader(mContext);
        CancelReasonInput cancelReasonInput = new CancelReasonInput();
        cancelReasonInput.cancelReason = reason;
        cancelReasonInput.requestCancelOn = utilObj.getCurrentDateToString(Constants.DateFormat.YYYYMMDDTHHMMSS);

        barberManagerObj.cancelAppointmentBarber(apptId, cancelReasonInput);
    }
}
