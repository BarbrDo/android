package com.barbrdo.app.activities;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.res.AssetFileDescriptor;
import android.graphics.Color;
import android.media.MediaPlayer;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.v4.content.LocalBroadcastManager;
import android.util.Log;
import android.view.MenuItem;
import android.view.View;
import android.view.WindowManager;
import android.widget.ImageView;
import android.widget.SeekBar;
import android.widget.TextView;

import com.barbrdo.app.R;
import com.barbrdo.app.customviews.CircularProgressBar;
import com.barbrdo.app.dataobject.RequestCheckInInput;
import com.barbrdo.app.dataobject.RequestDateInput;
import com.barbrdo.app.dataobject.RequestToBarber;
import com.barbrdo.app.dataobject.Service;
import com.barbrdo.app.helpers.GoogleMapRoute;
import com.barbrdo.app.interfaces.DialogActionCallback;
import com.barbrdo.app.interfaces.ServiceRedirection;
import com.barbrdo.app.managers.BarberManager;
import com.barbrdo.app.utils.Constants;
import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;

import java.io.IOException;
import java.lang.reflect.Type;
import java.text.DecimalFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.Timer;
import java.util.TimerTask;

public class AcceptRejectRequestActivity extends AppActivity implements ServiceRedirection, View.OnClickListener {
    private Context mContext;
    private CircularProgressBar circularProgressBar;
    private int progress = 0;
    private TextView textViewServices;
    private TextView textViewTime;
    private ImageView imageViewMapImage;
    private SeekBar seekBarSlider;
    private DecimalFormat decimalFormat;
    private BarberManager barberManagerObj;
    private MediaPlayer mMediaPlayer;
    private RequestToBarber requestToBarber;
    public LocalBroadcastManager bManager;
    public static final String RECEIVE_CUSTOMER_MESSAGES = "com.barbrdo.app.customer";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_accept_decline_request);
        getWindow().addFlags(WindowManager.LayoutParams.FLAG_KEEP_SCREEN_ON);
        getWindow().addFlags(WindowManager.LayoutParams.FLAG_SHOW_WHEN_LOCKED | WindowManager.LayoutParams.FLAG_DISMISS_KEYGUARD);
        getWindow().addFlags(WindowManager.LayoutParams.FLAG_KEEP_SCREEN_ON | WindowManager.LayoutParams.FLAG_TURN_SCREEN_ON);
        mContext = AcceptRejectRequestActivity.this;
        initData();
        bindControls();

        if (getIntent().hasExtra(Constants.BundleKeyTag.SERIALIZED_DATA)) {
            Gson gson = new Gson();
            Type collectionType = new TypeToken<RequestToBarber>() {
            }.getType();
            requestToBarber = gson.fromJson(getIntent().getStringExtra(Constants.BundleKeyTag.SERIALIZED_DATA), collectionType);
            setData();
        }

        bManager = LocalBroadcastManager.getInstance(this);
        IntentFilter intentFilter = new IntentFilter();
        intentFilter.addAction(RECEIVE_CUSTOMER_MESSAGES);
        bManager.registerReceiver(bReceiver, intentFilter);
    }

    @Override
    void initData() {
        circularProgressBar = getView(R.id.circularProgressbar);
        imageViewMapImage = getView(R.id.iv_map_image);
        textViewServices = getView(R.id.tv_services);
        textViewTime = getView(R.id.tv_time);
        seekBarSlider = getView(R.id.sb_slider);

        barberManagerObj = new BarberManager(this, this);
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
                        if ((progress * 1.66f) > 100) {
                            startStopAudioPlayer(false);
                            finish();
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

        seekBarSlider.setOnSeekBarChangeListener(new SeekBar.OnSeekBarChangeListener() {
            @Override
            public void onProgressChanged(SeekBar seekBar, int progress, boolean fromUser) {

            }

            @Override
            public void onStartTrackingTouch(SeekBar seekBar) {

            }

            @Override
            public void onStopTrackingTouch(SeekBar seekBar) {
//                if (seekBar.getProgress() > 85) {
//                    utilObj.startLoader(mContext);
//                    barberManagerObj.acceptAppointment(requestToBarber.id);
//                } else if (seekBar.getProgress() < 15) {
//                    utilObj.startLoader(mContext);
//                    barberManagerObj.declineAppointment(requestToBarber.id);
//                } else {
//                    seekBarSlider.setProgress(50);
//                }
            }
        });
    }

    private int adjustAlpha(int color, float factor) {
        int alpha = Math.round(Color.alpha(color) * factor);
        int red = Color.red(color);
        int green = Color.green(color);
        int blue = Color.blue(color);
        return Color.argb(alpha, red, green, blue);
    }

    private void setData() {
        if (requestToBarber != null) {
            if (requestToBarber.services.size() > 0) {
                StringBuilder stringBuilder = new StringBuilder();
                for (Service barberService : requestToBarber.services) {
                    stringBuilder.append(barberService.name + " .. $" + decimalFormat.format(barberService.price) + "\n");
                }
                String services = stringBuilder.toString();
                if (services.endsWith("\n")) {
                    services = services.substring(0, services.length() - 1);
                }
                textViewServices.setText(services);
            }

            textViewTime.setText("Customer will be there at " + getAppointmentDateTime(Integer.parseInt(requestToBarber.reachIn), Constants.DateFormat.HHMMA).toUpperCase());

            getLatLongFromPlaceId(String.valueOf(requestToBarber.shopLatLong.get(1)), String.valueOf(requestToBarber.shopLatLong.get(0)),
                    String.valueOf(requestToBarber.customerLatLong.get(1)), String.valueOf(requestToBarber.customerLatLong.get(0)));
        }

//        imageLoader.displayImage("https://maps.googleapis.com/maps/api/staticmap?size=600x400&path=enc%3AohqfIc_jpC|@}LoEmA`@aFvBuRZqBx@_FvAwDUYiACoLeLgQoV^sIfF}RvBfBb@yBnAcFhBoHeDiD]yAz@}NkBaGMiX`Ccs@rEit@dGocApEqXhTkaAvPk~A|b@adHb\\\\kdFfvAeqEvjAqoDzd@y{A|i@{bC|m@mnCptAe`Ibg@ozC|Eoh@_B_wBrBe}@xJw{@xLqt@`Es^jCcu@uDmhA_SstAiAua@tAiYhf@unHhHcoAc@mW}b@yhHmTq}DkGuhDaQs_FaM_uAml@uhFyYwgBqMaoCkIuzEgGexBkPwoCgP}vBk[w`Bck@coCaLy`@qL_x@a_@qhBkb@qfC_Vkn@crAqkCee@aaAcMqWsc@kjAwZsj@a^}n@sSwk@u~@qhEkrBeiJsVkgAcGoNq_BetDkk@oqAqRcl@w\\\\mmA{Js\\\\oM}Sga@on@qf@yu@wwAyxBkoAooBi{@utA}p@eoAyl@ogAan@ei@uHyOiS_v@}}AeyFc\\\\elAoJ_i@gBad@dA_a@f]i`Elp@g}Hjd@krFvE_rB|BsdGxBiuFkAeq@wF{}@iTi|Akh@yjB}Poz@yGa{@sBsbA~Jc{BzLw~B~Be~AqAyt@md@_gEmLkr@s[cjA{jAmeCcv@a}Aub@ifAcLol@qGur@[{lBrFoyEnBg|CuGo`AyHk`@aPcg@s^{l@gc@ap@uSso@oKas@ag@mzIi@{z@zE{dAfMuxAxt@qpFxBq_@}Coo@ok@gdCcLkj@cEq`@mBsfA~Le_Dz@}y@wIikAw[ozB{Os}BiJufAiLwj@cYqu@ca@qm@oiBkmCePq`@aM}g@sm@qiEopB{cLqXihCkJciB{K{lB}LgcAeb@urBsY}{@qc@q_BiQy^cYi[_^ca@kJ}O{Nql@mF}`@kEa{@oLasBkZ_w@c{AidDiaBsqDih@uoAyDoj@xEim@~{Be~GtQks@fJejAvK{iGDqn@kCmz@}LslEsFyoBcH}dCyHeeA}WijBwFsm@wBme@}@_sA|CsjAtL{oAdScdAtTa`AprAe|FttAq~F~pB_mHxv@wqCdLor@lRccCtXo{DX{tAhFmaAzNqeA~Umt@h^gk@tL}Zdf@crB`L}Srk@ob@pUkXjZyn@hw@wiBhr@asCbYy{DlUodEvF{n@vGsi@l@q^iFmt@cTmyByJw|BRekAtKmzApj@woDtIok@hAi_@yUgyD{@gv@hHgj@xKk|@dFmiBdAytAyEi_AsIoqC{EgcBbIi_@`LakBvEsgA`GiXvQ_i@bB}GcI}UsJsY}C}I&key=AIzaSyCAdiKFv9h3yLZl9USmbZ5_PYsvLPO9SW8", imageViewMapImage, options);
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
            case Constants.TaskID.BARBER_DECLINE_REQUEST:
                utilObj.showToast(mContext, appInstance.message, 1);
                startStopAudioPlayer(false);
                finish();
                break;

            case Constants.TaskID.BARBER_ACCEPT_REQUEST:
//                utilObj.showToast(mContext, appInstance.message, 1);
                startStopAudioPlayer(false);
                clearAllActivitiesFromBackStack(mContext, NavigationDrawerActivity.class);
                break;
        }
    }

    @Override
    public void onFailureRedirection(String errorMessage) {
        utilObj.stopLoader();
        showSnackBar(errorMessage);
        startStopAudioPlayer(false);
        clearAllActivitiesFromBackStack(mContext, NavigationDrawerActivity.class);
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
        }
    }

    public void accept(View view) {
        utilObj.startLoader(mContext);
        RequestDateInput requestDateInput = new RequestDateInput();
        requestDateInput.appointmentDate = getAppointmentDateTime(Integer.parseInt(requestToBarber.reachIn), Constants.DateFormat.YYYYMMDDHHMM);
        barberManagerObj.acceptAppointment(requestToBarber.id, requestDateInput);
    }

    private String getAppointmentDateTime(int minutes, String format) {
        SimpleDateFormat df = new SimpleDateFormat(format);
        Date d = null;
        try {
            d = df.parse(utilObj.getCurrentDateToString(format));
        } catch (ParseException e) {
            e.printStackTrace();
        }
        Calendar cal = Calendar.getInstance();
        cal.setTime(d);
        cal.add(Calendar.MINUTE, minutes);
        return df.format(cal.getTime());
    }

    public void decline(View view) {
        utilObj.showAlertDialog(this, R.string.decline_request, R.string.are_you_sure_you_want_to_decline_request, R.string.ok, R.string.cancel, new DialogActionCallback() {
            @Override
            public void doOnPositive() {
                utilObj.startLoader(mContext);
                RequestCheckInInput requestCheckInInput = new RequestCheckInInput();
                requestCheckInInput.requestCancelOn = utilObj.getCurrentDateToString(Constants.DateFormat.YYYYMMDDTHHMMSS);
                barberManagerObj.declineAppointment(requestToBarber.id, requestCheckInInput);
            }

            @Override
            public void doOnNegative() {
            }
        });
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

    @Override
    protected void onDestroy() {
        super.onDestroy();
        bManager.unregisterReceiver(bReceiver);
    }

    private BroadcastReceiver bReceiver = new BroadcastReceiver() {
        @Override
        public void onReceive(Context context, Intent intent) {
            if (intent.getAction().equals(RECEIVE_CUSTOMER_MESSAGES)) {
                String message = intent.getStringExtra(Constants.BundleKeyTag.MESSAGE);
                if (message.equalsIgnoreCase("customer_cancel_appointment")) {
                    utilObj.showToast(mContext, "Request cancelled by customer.", 1);
                    startStopAudioPlayer(false);
                    finish();
                }
            }
        }
    };

    private void getLatLongFromPlaceId(final String lat1, final String lon1, final String lat2, final String lon2) {
        new AsyncTask<String, Void, String>() {
            @Override
            protected void onPreExecute() {
                super.onPreExecute();
            }

            @Override
            protected String doInBackground(String... strings) {
                GoogleMapRoute googleMapRoute = new GoogleMapRoute();
                return googleMapRoute.getRoutePath(strings[0], strings[1], strings[2], strings[3]);
            }

            @Override
            protected void onPostExecute(final String route) {
                super.onPostExecute(route);
                Log.d("Static map url", "https://maps.googleapis.com/maps/api/staticmap?size=200x200&path=enc%3A" + route + "&markers=color:blue%7Clabel:S%7C" + lat1 + "," + lon1 + "&markers=color:green%7Clabel:G%7C" + lat2 + "," + lon2 + "&key=AIzaSyBWFq_UoUG_zusW-CL2nRXhwqhPQ8gVpuc");
                runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        imageLoader.displayImage("https://maps.googleapis.com/maps/api/staticmap?size=200x200&path=enc%3A" + route + "&markers=color:red%7Clabel:C%7C" + lat1 + "," + lon1 + "&markers=color:blue%7Clabel:B%7C" + lat2 + "," + lon2 + "&sensor=false&key=AIzaSyBWFq_UoUG_zusW-CL2nRXhwqhPQ8gVpuc", imageViewMapImage, options);
                    }
                });

            }
        }.execute(lat1, lon1, lat2, lon2);
    }
}
