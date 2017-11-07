package com.barbrdo.app.activities;

import android.content.Context;
import android.os.Bundle;
import android.support.v4.content.ContextCompat;
import android.text.Editable;
import android.text.TextUtils;
import android.text.TextWatcher;
import android.view.MenuItem;
import android.view.View;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;

import com.barbrdo.app.R;
import com.barbrdo.app.dataobject.SendInviteInput;
import com.barbrdo.app.interfaces.ServiceRedirection;
import com.barbrdo.app.managers.CommonManager;
import com.barbrdo.app.utils.Constants;

public class InviteCustomerActivity extends AppActivity implements ServiceRedirection, View.OnClickListener {

    private Context mContext;
    private EditText editTextEmailAddress;
    private EditText editTextMobileNumber;
    private ImageView imageViewCustomer;
    private ImageView imageViewBarber;
    private TextView textViewCustomer;
    private TextView textViewBarber;
    private TextView textViewSubmit;
    private boolean isEmailInvitation;
    private CommonManager commonManagerObj;
    private String mUserType = "customer";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_invite_customer);
        mContext = InviteCustomerActivity.this;
        initData();
        bindControls();
    }

    @Override
    void initData() {
        setUpToolBar("");
        editTextEmailAddress = getView(R.id.et_email_address);
        editTextMobileNumber = getView(R.id.et_mobile);
        imageViewCustomer = getView(R.id.iv_customer);
        imageViewBarber = getView(R.id.iv_barber);
        textViewCustomer = getView(R.id.tv_customer);
        textViewBarber = getView(R.id.tv_barber);
        textViewSubmit = getView(R.id.tv_submit);

        commonManagerObj = new CommonManager(this, this);
    }

    @Override
    void bindControls() {
        imageViewCustomer.setOnClickListener(this);
        imageViewBarber.setOnClickListener(this);

        if (appInstance.userDetail.user.userType.equalsIgnoreCase(getString(R.string.barber_))) {
            getView(R.id.ll_customer_barber).setVisibility(View.VISIBLE);
            ((TextView) getView(R.id.tv_header)).setText(R.string.invite_to_barbrdo);
            ((TextView) getView(R.id.tv_invite_text)).setText(R.string.invite_10_customers);
        } else {
            ((TextView) getView(R.id.tv_header)).setText(R.string.invite_barber_to_barbrdo);
            ((TextView) getView(R.id.tv_invite_text)).setText(R.string.invite_barbers_to_download_the_barbrdo_app_and_when_they_book_their_first_appointment_you_receive_a_5_amazon_gift_card_as_a_token_of_our_appreciation);
        }

        editTextEmailAddress.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {

            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                if (s.length() > 0) {
                    isEmailInvitation = true;
                    editTextMobileNumber.setText("");
                    textViewSubmit.setEnabled(true);
                    textViewSubmit.setBackgroundResource(R.color.button_blue);
                } else {
                    textViewSubmit.setEnabled(false);
                    textViewSubmit.setBackgroundResource(R.color.button_dark_gray);
                }
            }

            @Override
            public void afterTextChanged(Editable s) {

            }
        });

        editTextMobileNumber.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {

            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                if (s.length() > 0) {
                    isEmailInvitation = false;
                    editTextEmailAddress.setText("");
                    textViewSubmit.setEnabled(true);
                    textViewSubmit.setBackgroundResource(R.drawable.rounded_bottom_red);
                } else {
                    textViewSubmit.setEnabled(false);
                    textViewSubmit.setBackgroundResource(R.drawable.rounded_bottom_gray);
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
            case Constants.TaskID.INVITE_TO_APP:
//                utilObj.showToast(mContext, appInstance.message, 1);
                utilObj.showToast(mContext, "Invite sent successfully.", 1);
                editTextEmailAddress.setText("");
                editTextMobileNumber.setText("");
                textViewSubmit.setEnabled(false);
                textViewSubmit.setBackgroundResource(R.color.button_dark_gray);
                break;
        }
    }

    @Override
    public void onFailureRedirection(String errorMessage) {
        utilObj.stopLoader();
        showSnackBar(errorMessage);
    }

    public void sendInvitation(View view) {
        String message = "";
        String email = editTextEmailAddress.getText().toString().trim();
        String mobile = editTextMobileNumber.getText().toString().trim();

        if (!TextUtils.isEmpty(email)) {
            if (!utilObj.checkEmail(email)) {
                message = getString(R.string.error_email_invalid);
                utilObj.showError(message, editTextEmailAddress);
            }
        } else if (!TextUtils.isEmpty(mobile)) {
            if (mobile.length() < 10) {
                message = getString(R.string.error_mobile_invalid);
                utilObj.showError(message, editTextMobileNumber);
            }
        }

        if (message.isEmpty()) {
            utilObj.startLoader(mContext);
            SendInviteInput sendInviteInput = new SendInviteInput();
            if (appInstance.userDetail.user.userType.equalsIgnoreCase(getString(R.string.barber_))) {
                if (isEmailInvitation) {
                    sendInviteInput.inviteAs = mUserType;
                    sendInviteInput.refereeEmail = email;
                } else {
                    sendInviteInput.inviteAs = mUserType;
                    sendInviteInput.refereePhoneNumber = mobile;
                }
            } else {
                if (isEmailInvitation) {
                    sendInviteInput.inviteAs = "barber";
                    sendInviteInput.refereeEmail = email;
                } else {
                    sendInviteInput.inviteAs = "barber";
                    sendInviteInput.refereePhoneNumber = mobile;
                }
            }
            commonManagerObj.sendInvite(sendInviteInput);
        }
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.iv_customer:
                imageViewCustomer.setImageResource(R.drawable.customer_active_blue);
                imageViewBarber.setImageResource(R.drawable.barber);
                textViewCustomer.setTextColor(ContextCompat.getColor(mContext, R.color.text_dark));
                textViewBarber.setTextColor(ContextCompat.getColor(mContext, R.color.text_dark));
                mUserType = getString(R.string.customer_);
                ((TextView) getView(R.id.tv_invite_text)).setText(R.string.invite_10_customers);
                break;

            case R.id.iv_barber:
                imageViewCustomer.setImageResource(R.drawable.customer);
                imageViewBarber.setImageResource(R.drawable.barber_active_blue);
                textViewCustomer.setTextColor(ContextCompat.getColor(mContext, R.color.text_dark));
                textViewBarber.setTextColor(ContextCompat.getColor(mContext, R.color.text_dark));
                mUserType = getString(R.string.barber_);
                ((TextView) getView(R.id.tv_invite_text)).setText(R.string.invite_barbers_to_download_the_barbrdo_app_and_when_they_book_their_first_appointment_you_receive_a_5_amazon_gift_card_as_a_token_of_our_appreciation);
                break;
        }
    }
}
