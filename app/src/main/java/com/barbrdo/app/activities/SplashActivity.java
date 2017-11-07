package com.barbrdo.app.activities;

import android.app.NotificationManager;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;
import android.content.pm.Signature;
import android.graphics.PorterDuff;
import android.os.Bundle;
import android.os.Handler;
import android.support.v4.content.ContextCompat;
import android.util.Base64;
import android.util.Log;
import android.view.Window;
import android.widget.ProgressBar;

import com.barbrdo.app.R;
import com.barbrdo.app.interfaces.DialogActionCallback;
import com.barbrdo.app.utils.Constants;

import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;

public class SplashActivity extends AppActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        requestWindowFeature(Window.FEATURE_NO_TITLE);
        setContentView(R.layout.activity_splash);
        initData();

        try {
            PackageInfo info = getPackageManager().getPackageInfo(
                    "com.barbrdo.application",
                    PackageManager.GET_SIGNATURES);
            for (Signature signature : info.signatures) {
                MessageDigest md = MessageDigest.getInstance("SHA");
                md.update(signature.toByteArray());
                Log.d("KeyHash:", Base64.encodeToString(md.digest(), Base64.DEFAULT));
            }
        } catch (PackageManager.NameNotFoundException e) {

        } catch (NoSuchAlgorithmException e) {

        }

        NotificationManager notificationManager = (NotificationManager) getSystemService(NOTIFICATION_SERVICE);
        notificationManager.cancelAll();
    }

    @Override
    protected void onResume() {
        super.onResume();
        if (!utilObj.isNetworkAvailable(this)) {
            utilObj.showAlertDialog(this, R.string.network_service_message_title, R.string.network_service_message, R.string.ok, 0, new DialogActionCallback() {
                @Override
                public void doOnPositive() {
                    finish();
                }

                @Override
                public void doOnNegative() {
                }
            });
        } else {
            if (sessionManager.getIsLoggedIn()) {
                if (sessionManager.getUser() != null) {
                    appInstance.userDetail = sessionManager.getUser();
                    sessionManager.setUserId(appInstance.userDetail.user.id);
                    new Handler().postDelayed(new Runnable() {
                        @Override
                        public void run() {
                            startActivity(NavigationDrawerActivity.class);
                            SplashActivity.this.finish();
                        }
                    }, Constants.SplashScreen.SPLASH_DELAY_LENGTH);
                }
            } else {
                new Handler().postDelayed(new Runnable() {
                    @Override
                    public void run() {
                        startActivity(ChooseSignInActivity.class);
                        SplashActivity.this.finish();
                    }
                }, Constants.SplashScreen.SPLASH_DELAY_LENGTH);
            }
        }
    }

    /**
     * The method is used to initialize the objects
     *
     * @return none
     */
    public void initData() {
        ((ProgressBar) getView(R.id.progressBar)).getIndeterminateDrawable()
                .setColorFilter(ContextCompat.getColor(SplashActivity.this, R.color.colorAccent), PorterDuff.Mode.MULTIPLY);
    }

    @Override
    void bindControls() {
    }
}
