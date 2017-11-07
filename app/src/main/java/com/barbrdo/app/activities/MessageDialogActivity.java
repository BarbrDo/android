package com.barbrdo.app.activities;

import android.content.Context;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextUtils;
import android.text.TextWatcher;
import android.view.MenuItem;
import android.view.View;
import android.view.WindowManager;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;

import com.barbrdo.app.R;
import com.barbrdo.app.dataobject.MessageInput;
import com.barbrdo.app.dataobject.MessageToBarber;
import com.barbrdo.app.interfaces.ServiceRedirection;
import com.barbrdo.app.managers.BarberManager;
import com.barbrdo.app.managers.CustomerManager;
import com.barbrdo.app.utils.Constants;
import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;

import java.lang.reflect.Type;

public class MessageDialogActivity extends AppActivity implements ServiceRedirection {

    private Context mContext;
    private ImageView imageViewProfilePicture;
    private TextView textViewUserName;
    private TextView textViewMessage;
    private EditText editTextMessage;
    private TextView textViewCancel;
    private TextView textViewReply;
    private MessageToBarber messageToBarberObj;
    private boolean isMessageToBarber = false;
    private CustomerManager customerManagerObj;
    private BarberManager barberManagerObj;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.dialog_message);
        getWindow().addFlags(WindowManager.LayoutParams.FLAG_SHOW_WHEN_LOCKED | WindowManager.LayoutParams.FLAG_DISMISS_KEYGUARD);
        getWindow().addFlags(WindowManager.LayoutParams.FLAG_KEEP_SCREEN_ON | WindowManager.LayoutParams.FLAG_TURN_SCREEN_ON);
        this.setFinishOnTouchOutside(false);
        mContext = MessageDialogActivity.this;
        initData();
        bindControls();

        if (getIntent().hasExtra(Constants.BundleKeyTag.SERIALIZED_DATA)) {
            Gson gson = new Gson();
            Type collectionType = new TypeToken<MessageToBarber>() {
            }.getType();
            messageToBarberObj = gson.fromJson(getIntent().getStringExtra(Constants.BundleKeyTag.SERIALIZED_DATA), collectionType);
            isMessageToBarber = getIntent().getBooleanExtra(Constants.BundleKeyTag.MESSAGE_TO_BARBER, false);
            setData();
        }
    }

    @Override
    void initData() {
        imageViewProfilePicture = getView(R.id.iv_profile_picture);
        textViewUserName = getView(R.id.tv_username);
        textViewMessage = getView(R.id.tv_message);
        editTextMessage = getView(R.id.et_message);
        textViewCancel = getView(R.id.tv_cancel);
        textViewReply = getView(R.id.tv_reply);

        customerManagerObj = new CustomerManager(this, this);
        barberManagerObj = new BarberManager(this, this);

    }

    @Override
    void bindControls() {
        textViewReply.setEnabled(false);
        editTextMessage.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {

            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                if (s.length() > 0) {
                    textViewReply.setBackgroundResource(R.color.button_blue);
                    textViewReply.setEnabled(true);
                } else {
                    textViewReply.setBackgroundResource(R.color.button_dark_gray);
                    textViewReply.setEnabled(false);
                }
            }

            @Override
            public void afterTextChanged(Editable s) {

            }
        });

        textViewReply.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                sendMessage();
            }
        });

        textViewCancel.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();
            }
        });
    }

    private void setData() {
        if (messageToBarberObj != null) {
            textViewUserName.setText(messageToBarberObj.customerInfo.firstName + " " + messageToBarberObj.customerInfo.lastName);

            if (!TextUtils.isEmpty(messageToBarberObj.customerInfo.picture)) {
                imageLoader.displayImage(sessionManager.getImageBaseUrl() + messageToBarberObj.customerInfo.picture, imageViewProfilePicture, options);
            }

            textViewMessage.setText(messageToBarberObj.text);
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

    private void sendMessage() {
        utilObj.startLoader(mContext);
        MessageInput messageInput = new MessageInput();
        messageInput.text = editTextMessage.getText().toString();
        if (isMessageToBarber) {
            messageInput.customerId = messageToBarberObj.customerInfo.id;
            barberManagerObj.messageToCustomer(messageInput);
        } else {
            messageInput.barberId = messageToBarberObj.customerInfo.id;
            customerManagerObj.messageToBarber(messageInput);
        }
    }

    @Override
    public void onSuccessRedirection(int taskID) {
        utilObj.stopLoader();
        switch (taskID) {
            case Constants.TaskID.MESSAGE_CUSTOMER:
                utilObj.showToast(mContext, appInstance.message, 1);
                finish();
                break;
            case Constants.TaskID.MESSAGE_BARBER:
                utilObj.showToast(mContext, appInstance.message, 1);
                finish();
                break;
        }
    }

    @Override
    public void onFailureRedirection(String errorMessage) {
        utilObj.stopLoader();
        showSnackBar(errorMessage);
    }
}
