package com.barbrdo.app.activities;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.res.AssetFileDescriptor;
import android.graphics.Color;
import android.media.MediaPlayer;
import android.os.Bundle;
import android.support.v4.content.LocalBroadcastManager;
import android.text.TextUtils;
import android.util.Log;
import android.view.MenuItem;
import android.view.View;
import android.view.WindowManager;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.barbrdo.app.R;
import com.barbrdo.app.customviews.CircularProgressBar;
import com.barbrdo.app.dataobject.CustomerBarbers;
import com.barbrdo.app.dataobject.CustomerRequestDetails;
import com.barbrdo.app.dataobject.RequestCheckInInput;
import com.barbrdo.app.dataobject.Service;
import com.barbrdo.app.interfaces.DialogActionCallback;
import com.barbrdo.app.interfaces.ServiceRedirection;
import com.barbrdo.app.managers.CustomerManager;
import com.barbrdo.app.utils.Constants;

import java.io.IOException;
import java.text.DecimalFormat;
import java.util.Timer;
import java.util.TimerTask;

public class AwaitingBarberApprovalActivity extends AppActivity implements ServiceRedirection, View.OnClickListener {
    private Context mContext;
    private CircularProgressBar circularProgressBar;
    private int progress = 0;
    private RelativeLayout relativeLayoutRequest;
    private RelativeLayout relativeLayoutNotResponding;
    private TextView textViewServices;
    private TextView textViewTime;
    private ImageView imageViewProfilePicture;
    private CustomerRequestDetails customerRequestDetails;
    private String mSelectedTime;
    private DecimalFormat decimalFormat;
    private CustomerManager customerManagerObj;
    private CustomerBarbers.Datum barberData;
    private MediaPlayer mMediaPlayer;
    public LocalBroadcastManager bManager;
    private MediaPlayer mMediaPlayerAcceptDecline;
    public static final String RECEIVE_BARBER_MESSAGES = "com.barbrdo.app.barber";
    public static boolean IS_DENIED = true;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_awaiting_barber_approval);
        getWindow().addFlags(WindowManager.LayoutParams.FLAG_KEEP_SCREEN_ON);
        mContext = AwaitingBarberApprovalActivity.this;
        initData();
        bindControls();

        Bundle extras = getIntent().getExtras();
        if (extras != null) {
            customerRequestDetails = (CustomerRequestDetails) extras.getSerializable(Constants.BundleKeyTag.SERIALIZED_DATA);
            barberData = (CustomerBarbers.Datum) extras.getSerializable(Constants.BundleKeyTag.BARBER_DATA);
            mSelectedTime = extras.getString(Constants.BundleKeyTag.SELECTED_TIME);
            setData();
        }

        bManager = LocalBroadcastManager.getInstance(this);
        IntentFilter intentFilter = new IntentFilter();
        intentFilter.addAction(RECEIVE_BARBER_MESSAGES);
        bManager.registerReceiver(bReceiver, intentFilter);
    }

    @Override
    void initData() {
        circularProgressBar = getView(R.id.circularProgressbar);
        imageViewProfilePicture = getView(R.id.iv_profile_picture);
        textViewServices = getView(R.id.tv_services);
        textViewTime = getView(R.id.tv_time);
        relativeLayoutRequest = getView(R.id.rl_request);
        relativeLayoutNotResponding = getView(R.id.rl_not_responding);

        customerManagerObj = new CustomerManager(this, this);
        decimalFormat = new DecimalFormat();
        decimalFormat.setDecimalSeparatorAlwaysShown(false);
    }

    @Override
    void bindControls() {
        startStopAudioPlayer(true);
        Timer t = new Timer();
        t.scheduleAtFixedRate(new TimerTask() {
            @Override
            public void run() {
                runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        progress++;
                        if (progress == 61) {
                            startAcceptNotification(true);
                        }
                        if ((progress * 1.66f) > 100) {
                            startStopAudioPlayer(false);
                            relativeLayoutRequest.setVisibility(View.GONE);
                            relativeLayoutNotResponding.setVisibility(View.VISIBLE);
                        }
                        circularProgressBar.setProgressWithAnimation((progress * 1.66f));
                        if ((progress * 1.66f) > 83) {
                            int myColor = getResources().getColor(R.color.red_bg);
                            circularProgressBar.setColor(myColor);
                        }
                    }
                });
            }
        }, 0, 1000);
    }

    private int adjustAlpha(int color, float factor) {
        int alpha = Math.round(Color.alpha(color) * factor);
        int red = Color.red(color);
        int green = Color.green(color);
        int blue = Color.blue(color);
        return Color.argb(alpha, red, green, blue);
    }

    private void setData() {
        if (customerRequestDetails.data != null) {
            if (customerRequestDetails.data.services.size() > 0) {
                StringBuilder stringBuilder = new StringBuilder();
                for (Service barberService : customerRequestDetails.data.services) {
                    stringBuilder.append(barberService.name + " .. $" + decimalFormat.format(barberService.price) + "\n");
                }
                String services = stringBuilder.toString();
                if (services.endsWith("\n")) {
                    services = services.substring(0, services.length() - 1);
                }
                textViewServices.setText(services);
            }

            textViewTime.setText("Be there in " + mSelectedTime + " minutes");
        }

        if (barberData != null) {
            if (!TextUtils.isEmpty(barberData.picture)) {
                imageLoader.displayImage(sessionManager.getImageBaseUrl() + barberData.picture, imageViewProfilePicture, options);
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
                startStopAudioPlayer(false);
                finish();
                break;
        }
    }

    @Override
    public void onFailureRedirection(String errorMessage) {
        utilObj.stopLoader();
        showSnackBar(errorMessage);
        startStopAudioPlayer(false);
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
        }
    }

    public void cancel(View view) {
        utilObj.showAlertDialog(this, R.string.cancel_request, R.string.are_you_sure_you_want_to_cancel_request, R.string.ok, R.string.cancel, new DialogActionCallback() {
            @Override
            public void doOnPositive() {
                utilObj.startLoader(mContext);
                RequestCheckInInput requestCheckInInput = new RequestCheckInInput();
                requestCheckInInput.requestCancelOn = utilObj.getCurrentDateToString(Constants.DateFormat.YYYYMMDDTHHMMSS);
                customerManagerObj.cancelAppointment(customerRequestDetails.data.id, requestCheckInInput);
            }

            @Override
            public void doOnNegative() {
            }
        });
    }

    public void searchAgain(View view) {
        clearAllActivitiesFromBackStack(mContext, NavigationDrawerActivity.class);
    }

    @Override
    protected void onResume() {
        super.onResume();
        IS_DENIED = true;
    }

    public void startStopAudioPlayer(boolean start) {
        if (start) {
            AssetFileDescriptor afd = null;
            try {
                afd = getAssets().openFd("request_notification_sound.mp3");
            } catch (IOException e) {
                e.printStackTrace();
            }
            mMediaPlayer = new MediaPlayer();
            try {
                mMediaPlayer.setDataSource(afd.getFileDescriptor(), afd.getStartOffset(), afd.getLength());
                mMediaPlayer.prepare();
                mMediaPlayer.setVolume(1.0f, 1.0f);
                mMediaPlayer.setLooping(true);
                mMediaPlayer.start();
            } catch (Exception e) {
                e.printStackTrace();
            }
        } else {
            if (mMediaPlayer != null) {
                mMediaPlayer.stop();
                mMediaPlayer.release();
                mMediaPlayer = null;
            }
        }
    }

    public void startAcceptNotification(boolean start) {
        if (start) {
            AssetFileDescriptor afd = null;
            try {
                afd = getAssets().openFd("notification_sound_accept_decline.mp3");
            } catch (IOException e) {
                e.printStackTrace();
            }
            mMediaPlayerAcceptDecline = new MediaPlayer();
            try {
                mMediaPlayerAcceptDecline.setDataSource(afd.getFileDescriptor(), afd.getStartOffset(), afd.getLength());
                mMediaPlayerAcceptDecline.prepare();
                mMediaPlayerAcceptDecline.setVolume(1.0f, 1.0f);
                mMediaPlayerAcceptDecline.setLooping(false);
                mMediaPlayerAcceptDecline.start();
            } catch (Exception e) {
                e.printStackTrace();
            }
        } else {
            if (mMediaPlayerAcceptDecline != null) {
                mMediaPlayerAcceptDecline.stop();
                mMediaPlayerAcceptDecline.release();
                mMediaPlayerAcceptDecline = null;
            }
        }
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        bManager.unregisterReceiver(bReceiver);
    }

    private BroadcastReceiver bReceiver = new BroadcastReceiver() {
        @Override
        public void onReceive(Context context, Intent intent) {
            if (intent.getAction().equals(RECEIVE_BARBER_MESSAGES)) {
                String message = intent.getStringExtra(Constants.BundleKeyTag.MESSAGE);
                Log.e("Message", message);
                if (message.equalsIgnoreCase("barber_cancel_appointment")) {
                    IS_DENIED = true;
                    startStopAudioPlayer(false);
                    startAcceptNotification(true);
                    relativeLayoutRequest.setVisibility(View.GONE);
                    relativeLayoutNotResponding.setVisibility(View.VISIBLE);
                    return;
                } else if (message.equalsIgnoreCase("barber_confirm_appointment")) {
                    IS_DENIED = false;
                    startStopAudioPlayer(false);
                    startAcceptNotification(true);
                    Bundle bundle = new Bundle();
                    bundle.putString(Constants.BundleKeyTag.SERIALIZED_DATA, intent.getStringExtra(Constants.BundleKeyTag.SERIALIZED_DATA));
                    bundle.putSerializable(Constants.BundleKeyTag.BARBER_DATA, barberData);
                    startActivity(RequestAcceptedActivity.class, bundle);
                    finish();
                }
            }
        }
    };
}
