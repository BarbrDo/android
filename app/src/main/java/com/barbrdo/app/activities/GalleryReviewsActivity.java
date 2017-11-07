package com.barbrdo.app.activities;

import android.content.Context;
import android.os.Bundle;
import android.support.design.widget.TabLayout;
import android.support.v4.view.ViewPager;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.MenuItem;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.barbrdo.app.R;
import com.barbrdo.app.adapters.ViewPagerAdapter;
import com.barbrdo.app.dataobject.UserAppointment;
import com.barbrdo.app.dataobject.UserDetail;
import com.barbrdo.app.fragments.CutsFragment;
import com.barbrdo.app.fragments.GalleryProfileFragment;
import com.barbrdo.app.fragments.ReviewsFragment;
import com.barbrdo.app.interfaces.ServiceRedirection;
import com.barbrdo.app.utils.Constants;

public class GalleryReviewsActivity extends AppActivity implements ServiceRedirection {

    private Context mContext;
    private TextView textViewHeader;
    private TextView textViewUsername;
    private ImageView imageViewProfilePicture;
    private TabLayout tabLayout;
    private ViewPager viewPager;
    private LinearLayout linearLayoutTabOne;
    private LinearLayout linearLayoutTabTwo;
    private TextView textViewNumberPhotos;
    private TextView textViewNumberCuts;
    private UserDetail.User userObj;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_barber_gallery_reviews);
        mContext = GalleryReviewsActivity.this;

        Bundle extras = getIntent().getExtras();
        if (extras != null) {
            initData();
            userObj = (UserDetail.User) extras.getSerializable(Constants.BundleKeyTag.SERIALIZED_DATA);
            if (userObj.userType.equalsIgnoreCase(getString(R.string.customer_)))
                textViewHeader.setText(R.string.customer_profile);
            else
                textViewHeader.setText(R.string.barber_profile);
            bindControls();
            setData();
            viewPager.setCurrentItem(extras.getInt(Constants.BundleKeyTag.TAB, 0));
        }
    }

    @Override
    void initData() {
        setUpToolBar("");
        textViewHeader = getView(R.id.tv_header);
        textViewUsername = getView(R.id.tv_username);
        imageViewProfilePicture = getView(R.id.iv_profile_picture);
        viewPager = getView(R.id.view_pager);
        tabLayout = getView(R.id.tabs);
    }

    @Override
    void bindControls() {
        setupViewPager(viewPager);
        tabLayout.setupWithViewPager(viewPager);
        setupTabIcons();
    }

    private void setupViewPager(ViewPager viewPager) {
        ViewPagerAdapter adapter = new ViewPagerAdapter(getSupportFragmentManager());
        adapter.addFrag(new GalleryProfileFragment(), getString(R.string.photos));
        if (userObj.userType.equalsIgnoreCase(getString(R.string.customer_)))
            adapter.addFrag(new CutsFragment(), getString(R.string.cuts));
        else
            adapter.addFrag(new ReviewsFragment(), getString(R.string.cuts));
        viewPager.setAdapter(adapter);
    }

    private void setupTabIcons() {
        linearLayoutTabOne = (LinearLayout) LayoutInflater.from(mContext).inflate(R.layout.tab_barber_gallery, null);
        textViewNumberPhotos = (TextView) linearLayoutTabOne.findViewById(R.id.tv_number);
        TextView textViewTitlePhotos = (TextView) linearLayoutTabOne.findViewById(R.id.tv_title);
        textViewNumberPhotos.setText("0");
        textViewTitlePhotos.setText(getString(R.string.photos));
        tabLayout.getTabAt(0).setCustomView(linearLayoutTabOne);

        linearLayoutTabTwo = (LinearLayout) LayoutInflater.from(mContext).inflate(R.layout.tab_barber_gallery, null);
        textViewNumberCuts = (TextView) linearLayoutTabTwo.findViewById(R.id.tv_number);
        TextView textViewTitleCuts = (TextView) linearLayoutTabTwo.findViewById(R.id.tv_title);
        textViewNumberCuts.setText("0");
        textViewTitleCuts.setText(getString(R.string.cuts));
        tabLayout.getTabAt(1).setCustomView(linearLayoutTabTwo);
    }

    private void setData() {
        String fullName = "";
        if (!TextUtils.isEmpty(userObj.firstName))
            fullName = userObj.firstName;
        if (!TextUtils.isEmpty(userObj.firstName) && !TextUtils.isEmpty(userObj.lastName))
            fullName = userObj.firstName + " " + userObj.lastName;
        textViewUsername.setText(fullName);

        textViewNumberPhotos.setText(userObj.gallery.size() + "");

        if (userObj.userType.equalsIgnoreCase(getString(R.string.barber_))) {
            textViewNumberCuts.setText(userObj.ratings.size() + "");
        } else if (userObj.userType.equalsIgnoreCase(getString(R.string.customer_))) {
            int noOfCuts = 0;
            for (UserAppointment appointment : userObj.appointments) {
                if (appointment.appointmentStatus.equalsIgnoreCase("completed")) {
                    noOfCuts++;
                }
            }
            textViewNumberCuts.setText(noOfCuts + "");
        }

        if (!TextUtils.isEmpty(userObj.picture)) {
            imageLoader.displayImage(sessionManager.getImageBaseUrl() + userObj.picture, imageViewProfilePicture, options);
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
    public void onSuccessRedirection(int taskID) {
        utilObj.stopLoader();
        switch (taskID) {
        }
    }

    @Override
    public void onFailureRedirection(String errorMessage) {
        utilObj.stopLoader();
        showSnackBar(errorMessage);
    }
}
