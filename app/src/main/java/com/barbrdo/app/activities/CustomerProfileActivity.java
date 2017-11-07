package com.barbrdo.app.activities;

import android.content.Context;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.MenuItem;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

import com.barbrdo.app.R;
import com.barbrdo.app.dataobject.UserAppointment;
import com.barbrdo.app.dataobject.UserDetail;
import com.barbrdo.app.interfaces.ServiceRedirection;
import com.barbrdo.app.managers.CommonManager;
import com.barbrdo.app.utils.Constants;

public class CustomerProfileActivity extends AppActivity implements ServiceRedirection {

    private Context mContext;
    private TextView textViewUsername;
    private TextView textViewNoCuts;
    private TextView textViewNoPhotos;
    private ImageView imageViewProfilePicture;
    private TextView textViewMemberSince;
    private TextView textViewEmail;
    private TextView textViewPhone;
    private CommonManager commonManagerObj;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_customer_profile);
        mContext = CustomerProfileActivity.this;

        initData();
        bindControls();

        Bundle extras = getIntent().getExtras();
        if (extras != null) {
            String userId = extras.getString(Constants.BundleKeyTag.CUSTOMER_ID);
            getUserProfile(userId);
        }
    }

    @Override
    void initData() {
        setUpToolBar("");
        textViewUsername = getView(R.id.tv_username);
        textViewNoCuts = getView(R.id.tv_cuts);
        textViewNoPhotos = getView(R.id.tv_photos);
        imageViewProfilePicture = getView(R.id.iv_profile_picture);
        textViewMemberSince = getView(R.id.tv_member_since);
        textViewEmail = getView(R.id.tv_email);
        textViewPhone = getView(R.id.tv_phone);

        commonManagerObj = new CommonManager(this, this);
    }

    private void setData() {
        UserDetail.User user = appInstance.userProfile.user;

        String fullName = "";
        if (!TextUtils.isEmpty(user.firstName))
            fullName = user.firstName;
        if (!TextUtils.isEmpty(user.firstName) && !TextUtils.isEmpty(user.lastName))
            fullName = user.firstName + " " + user.lastName;
        textViewUsername.setText(fullName);
        textViewMemberSince.setText(utilObj.getMyDateFormat(user.createdDate, Constants.DateFormat.YYYYMMDDTHHMMSS,
                Constants.DateFormat.MMMYYYY));
        textViewEmail.setText(user.email);
        textViewPhone.setText(utilObj.formatPhoneNumber(user.mobileNumber));
        textViewNoPhotos.setText(user.gallery.size() + "");

        if (!TextUtils.isEmpty(user.picture)) {
            imageLoader.displayImage(sessionManager.getImageBaseUrl() + user.picture, imageViewProfilePicture, options);
        }

        int noOfCuts = 0;
        for (UserAppointment appointment : user.appointments) {
            if (appointment.appointmentStatus.equalsIgnoreCase("completed")) {
                noOfCuts++;
            }
        }
        textViewNoCuts.setText(noOfCuts + "");
    }

    @Override
    void bindControls() {
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

    private void getUserProfile(String userId) {
        utilObj.startLoader(mContext);
        commonManagerObj.userProfile(userId);
    }

    @Override
    public void onSuccessRedirection(int taskID) {
        utilObj.stopLoader();
        switch (taskID) {
            case Constants.TaskID.USER_PROFILE:
                setData();
                break;
        }
    }

    @Override
    public void onFailureRedirection(String errorMessage) {
        utilObj.stopLoader();
        showSnackBar(errorMessage);
    }

    public void photosClick(View view) {
        Bundle bundle = new Bundle();
        bundle.putSerializable(Constants.BundleKeyTag.SERIALIZED_DATA, appInstance.userProfile.user);
        bundle.putInt(Constants.BundleKeyTag.TAB, 0);
        startActivity(GalleryReviewsActivity.class, bundle);
    }

    public void cutsClick(View view) {
        Bundle bundle = new Bundle();
        bundle.putSerializable(Constants.BundleKeyTag.SERIALIZED_DATA, appInstance.userProfile.user);
        bundle.putInt(Constants.BundleKeyTag.TAB, 1);
        startActivity(GalleryReviewsActivity.class, bundle);
    }

    public void callUser(View view) {
        if (appInstance.userProfile.user.mobileNumber != null) {
            Intent intent = new Intent(Intent.ACTION_DIAL);
            intent.setData(Uri.parse("tel:" + appInstance.userProfile.user.mobileNumber));
            startActivity(intent);
        }
    }
}
