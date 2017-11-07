package com.barbrdo.app.activities;

import android.content.Context;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextUtils;
import android.text.TextWatcher;
import android.view.MenuItem;
import android.view.View;
import android.widget.EditText;
import android.widget.TextView;

import com.barbrdo.app.R;
import com.barbrdo.app.dataobject.ContactInput;
import com.barbrdo.app.interfaces.ServiceRedirection;
import com.barbrdo.app.managers.CommonManager;
import com.barbrdo.app.utils.Constants;

public class ContactBarbrDoActivity extends AppActivity implements ServiceRedirection {

    private Context mContext;
    private EditText editTextMessage;
    private TextView textViewSend;
    private TextView textViewCallUs;
    private CommonManager commonManagerObj;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_contact_barbrdo);
        mContext = ContactBarbrDoActivity.this;
        initData();
        bindControls();
    }

    @Override
    void initData() {
        setUpToolBar("");
        editTextMessage = getView(R.id.et_message);
        textViewSend = getView(R.id.tv_send);
        textViewCallUs = getView(R.id.tv_call_us);

        commonManagerObj = new CommonManager(this, this);
    }


    @Override
    void bindControls() {
        utilObj.setSpannableTextView(textViewCallUs, R.drawable.call, getString(R.string.call_us));
        utilObj.setSpannableTextView(textViewSend, R.drawable.send, getString(R.string.send));

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
            case Constants.TaskID.CONTACT:
                utilObj.showToast(mContext, "Thank you! Your message has been sent to BarbrDo.", 2);
                editTextMessage.setText("");
                break;
        }
    }

    @Override
    public void onFailureRedirection(String errorMessage) {
        utilObj.stopLoader();
        showSnackBar(errorMessage);
    }

    public void contact(View view) {
        if (!TextUtils.isEmpty(editTextMessage.getText().toString().trim())) {
            utilObj.startLoader(mContext);
            ContactInput contactInput = new ContactInput();
            contactInput.name = appInstance.userDetail.user.firstName + " " + appInstance.userDetail.user.lastName;
            contactInput.email = appInstance.userDetail.user.email;
            contactInput.message = editTextMessage.getText().toString().trim();
            commonManagerObj.contact(contactInput);
        }
    }

    public void callOffice(View view) {
        Intent intent = new Intent(Intent.ACTION_DIAL);
        intent.setData(Uri.parse("tel:9177173813"));
        startActivity(intent);
    }
}
