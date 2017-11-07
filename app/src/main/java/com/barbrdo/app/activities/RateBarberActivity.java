package com.barbrdo.app.activities;

import android.content.Context;
import android.os.Bundle;
import android.text.TextUtils;
import android.util.Log;
import android.view.MenuItem;
import android.view.View;
import android.widget.CheckBox;
import android.widget.ImageView;
import android.widget.RatingBar;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.barbrdo.app.R;
import com.barbrdo.app.dataobject.CustomerAppointment;
import com.barbrdo.app.dataobject.RateBarberInput;
import com.barbrdo.app.dataobject.Rating;
import com.barbrdo.app.interfaces.ServiceRedirection;
import com.barbrdo.app.managers.CustomerManager;
import com.barbrdo.app.utils.Constants;

public class RateBarberActivity extends AppActivity implements ServiceRedirection, View.OnClickListener {

    private Context mContext;
    private ImageView imageViewProfilePicture;
    private ImageView imageViewFavorite;
    private TextView textViewUsername;
    private TextView textViewTime;
    private TextView textViewRatings;
    private TextView textViewLocation;
    private TextView textViewSubmit;
    private ImageView imageViewYes;
    private ImageView imageViewNo;
    private RelativeLayout relativeLayoutYes;
    private RelativeLayout relativeLayoutNo;
    private CheckBox checkBoxAddToFavorites;
    private RatingBar ratingBarRateBarber;
    private CustomerManager customerManagerObj;
    private boolean isCheckIn;
    private CustomerAppointment.Complete appointmentData;
    private boolean isSelected;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_rate_barber);
        mContext = RateBarberActivity.this;
        initData();
        bindControls();

        Bundle extras = getIntent().getExtras();
        if (extras != null) {
            isCheckIn = extras.getBoolean(Constants.BundleKeyTag.IS_CHECK_IN);
            if (!isCheckIn) {
                appointmentData = (CustomerAppointment.Complete) extras.getSerializable(Constants.BundleKeyTag.SERIALIZED_DATA);
                setData(appointmentData);
            } else {
                getCustomerAppointments();
            }
        }
    }

    @Override
    void initData() {
        setUpToolBarNoBack();
        textViewLocation = getView(R.id.tv_location);
        textViewTime = getView(R.id.tv_time);
        imageViewFavorite = getView(R.id.iv_favourite);
        imageViewProfilePicture = getView(R.id.iv_profile_picture);
        textViewUsername = getView(R.id.tv_username);
        textViewRatings = getView(R.id.tv_ratings);
        ratingBarRateBarber = getView(R.id.rb_rate_barber);
        relativeLayoutYes = getView(R.id.rl_yes);
        relativeLayoutNo = getView(R.id.rl_no);
        checkBoxAddToFavorites = getView(R.id.cb_add_to_favourite);
        textViewSubmit = getView(R.id.tv_submit);
        imageViewYes = getView(R.id.iv_yes);
        imageViewNo = getView(R.id.iv_no);

        customerManagerObj = new CustomerManager(this, this);
    }

    private void getCustomerAppointments() {
        utilObj.startLoader(mContext);
        customerManagerObj.getAppointments();
    }

    private void setData(CustomerAppointment.Complete appointmentData) {
        if (appointmentData.barberId.ratings.size() > 0) {
            float avg = 0;
            for (Rating rating : appointmentData.barberId.ratings) {
                avg = avg + rating.score;
            }
            avg = avg / appointmentData.barberId.ratings.size();
            textViewRatings.setText(String.format("%.1f", avg));
        } else {
            textViewRatings.setText("0.0");
        }
        textViewUsername.setText(appointmentData.barberId.firstName + " " + appointmentData.barberId.lastName);
        if (appointmentData.shopId != null)
            textViewLocation.setText(appointmentData.shopId.name);

        if (!TextUtils.isEmpty(appointmentData.barberId.picture)) {
            imageLoader.displayImage(sessionManager.getImageBaseUrl() + appointmentData.barberId.picture, imageViewProfilePicture, options);
        } else {
            imageViewProfilePicture.setImageResource(R.drawable.placeholder_user);
        }
        imageViewFavorite.setVisibility(View.GONE);

        textViewTime.setText(utilObj.getMyDateFormat(appointmentData.appointmentDate,
                Constants.DateFormat.YYYYMMDDTHHMMSS, Constants.DateFormat.MMMDD) + " @ " + utilObj.getMyDateFormat(appointmentData.appointmentDate,
                Constants.DateFormat.YYYYMMDDTHHMMSS, Constants.DateFormat.HHMMA).toUpperCase());
    }

    @Override
    void bindControls() {
        textViewSubmit.setEnabled(false);
        utilObj.setSpannableTextView(textViewSubmit, R.drawable.submit_rating, getString(R.string.submit_rating));
        ratingBarRateBarber.setOnRatingBarChangeListener(new RatingBar.OnRatingBarChangeListener() {
            @Override
            public void onRatingChanged(RatingBar ratingBar, float rating, boolean fromUser) {
                if (rating < 1.0f)
                    ratingBar.setRating(1.0f);
            }
        });

        relativeLayoutYes.setOnClickListener(this);
        relativeLayoutNo.setOnClickListener(this);
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
            case Constants.TaskID.RATE_BARBER:
                utilObj.showToast(mContext, appInstance.message, 2);
//                if (this.isTaskRoot()) {
                clearAllActivitiesFromBackStack(mContext, NavigationDrawerActivity.class);
//                }
                finish();
                break;

            case Constants.TaskID.APPOINTMENT:
                if (appInstance.customerAppointment.data.complete.size() > 0) {
                    appointmentData = appInstance.customerAppointment.data.complete.get(0);
                    setData(appointmentData);
                }
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
            case R.id.rl_yes:
                imageViewYes.setImageResource(R.drawable.green_circle_tick);
                imageViewNo.setImageResource(R.drawable.red_circle);
                textViewSubmit.setBackgroundResource(R.color.button_blue);
                textViewSubmit.setEnabled(true);
                isSelected = true;
                break;

            case R.id.rl_no:
                imageViewYes.setImageResource(R.drawable.green_circle);
                imageViewNo.setImageResource(R.drawable.red_circle_cross);
                textViewSubmit.setBackgroundResource(R.color.button_blue);
                textViewSubmit.setEnabled(true);
                isSelected = false;
                break;
        }
    }

    public void submitRatings(View view) {
        utilObj.startLoader(mContext);
        RateBarberInput rateBarberInput = new RateBarberInput();
        rateBarberInput.score = ratingBarRateBarber.getRating();
        rateBarberInput.appointmentId = appointmentData.id;
        rateBarberInput.barberId = appointmentData.barberId.id;
        rateBarberInput.isFavourite = checkBoxAddToFavorites.isChecked();
        rateBarberInput.nextInChair = isSelected;
        rateBarberInput.appointmentDate = appointmentData.appointmentDate;

        customerManagerObj.rateBarber(rateBarberInput);
    }
}
