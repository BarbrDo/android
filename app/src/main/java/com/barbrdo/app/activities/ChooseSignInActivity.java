package com.barbrdo.app.activities;

import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;
import android.content.pm.Signature;
import android.os.Bundle;
import android.util.Base64;
import android.util.Log;
import android.view.View;

import com.barbrdo.app.R;

import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;


public class ChooseSignInActivity extends AppActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        getWindow().setBackgroundDrawableResource(R.drawable.bg);
        setContentView(R.layout.activity_choose_login_register);
        initData();
        bindControls();
    }

    public void initData() {
    }

    @Override
    void bindControls() {
    }

    public void login(View view) {
        startActivity(LogInActivity.class);
    }

    public void register(View view) {
        startActivity(SignUpActivity.class);
    }
}
