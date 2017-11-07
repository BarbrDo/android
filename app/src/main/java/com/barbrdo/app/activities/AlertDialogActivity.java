package com.barbrdo.app.activities;

import android.os.Bundle;

import com.barbrdo.app.R;
import com.barbrdo.app.interfaces.DialogActionCallback;

public class AlertDialogActivity extends AppActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_transparent);

        utilObj.showAlertDialogNoTitle(this,
                getString(R.string.oops_we_are_sorry_but_it_looks_like_the_customer_had_to_cancel_the_request), "Ok", "", new DialogActionCallback() {
                    @Override
                    public void doOnPositive() {
                        finish();
                    }

                    @Override
                    public void doOnNegative() {

                    }
                });
    }

    @Override
    void initData() {

    }

    @Override
    void bindControls() {

    }
}