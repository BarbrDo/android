package com.barbrdo.app.activities;

import android.Manifest;
import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.content.IntentSender;
import android.content.pm.PackageManager;
import android.content.res.Configuration;
import android.location.Location;
import android.net.Uri;
import android.os.Bundle;
import android.os.Handler;
import android.os.Looper;
import android.provider.Settings;
import android.support.annotation.NonNull;
import android.support.v4.app.ActivityCompat;
import android.support.v4.view.GravityCompat;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.ActionBarDrawerToggle;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.text.TextUtils;
import android.util.Log;
import android.view.MenuItem;
import android.view.View;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.barbrdo.app.BuildConfig;
import com.barbrdo.app.R;
import com.barbrdo.app.adapters.NavigationAdapter;
import com.barbrdo.app.async.ApiClient;
import com.barbrdo.app.dataobject.CustomerAppointment;
import com.barbrdo.app.dataobject.Navigation;
import com.barbrdo.app.fragments.BarberHomeFragment;
import com.barbrdo.app.fragments.FavoriteBarbersFragment;
import com.barbrdo.app.fragments.GalleryFragment;
import com.barbrdo.app.fragments.ManageShopsFragment;
import com.barbrdo.app.fragments.SelectBarbersFragment;
import com.barbrdo.app.interfaces.DialogActionCallback;
import com.barbrdo.app.interfaces.OnItemClickListener;
import com.barbrdo.app.interfaces.ServiceRedirection;
import com.barbrdo.app.managers.CommonManager;
import com.barbrdo.app.managers.CustomerManager;
import com.barbrdo.app.utils.Constants;
import com.google.android.gms.common.api.ApiException;
import com.google.android.gms.common.api.ResolvableApiException;
import com.google.android.gms.location.FusedLocationProviderClient;
import com.google.android.gms.location.LocationCallback;
import com.google.android.gms.location.LocationRequest;
import com.google.android.gms.location.LocationResult;
import com.google.android.gms.location.LocationServices;
import com.google.android.gms.location.LocationSettingsRequest;
import com.google.android.gms.location.LocationSettingsResponse;
import com.google.android.gms.location.LocationSettingsStatusCodes;
import com.google.android.gms.location.SettingsClient;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;

import java.util.ArrayList;
import java.util.List;

public class NavigationDrawerActivity extends AppActivity implements OnItemClickListener, View.OnClickListener, ServiceRedirection {
    private static final String TAG = NavigationDrawerActivity.class.getName();
    private Context mContext;
    private DrawerLayout mDrawerLayout;
    private RecyclerView mDrawerList;
    private Toolbar toolbar;
    private RelativeLayout relativeLayoutDrawer;
    private ActionBarDrawerToggle mDrawerToggle;
    private ImageView imageViewToggleMenu;
    private TextView textViewUserName;
    private ImageView imageViewProfilePicture;
    private ImageView imageViewMenuProfile;
    boolean doubleBackToExitPressedOnce = false;
    private boolean isPageSelect;
    private boolean isTabChange;
    private int USER_TYPE;
    private CustomerManager customerManagerObj;
    private CommonManager commonManagerObj;
    private static final int REQUEST_PERMISSIONS_REQUEST_CODE = 34;
    private static final int REQUEST_CHECK_SETTINGS = 0x1;
    private static final long UPDATE_INTERVAL_IN_MILLISECONDS = 10000;
    private static final long FASTEST_UPDATE_INTERVAL_IN_MILLISECONDS =
            UPDATE_INTERVAL_IN_MILLISECONDS / 2;
    private FusedLocationProviderClient mFusedLocationClient;
    private SettingsClient mSettingsClient;
    private LocationRequest mLocationRequest;
    private LocationSettingsRequest mLocationSettingsRequest;
    private LocationCallback mLocationCallback;
    private Location mCurrentLocation;
    private boolean mRequestingLocationUpdates;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_navigation_drawer);
        mContext = NavigationDrawerActivity.this;

        mFusedLocationClient = LocationServices.getFusedLocationProviderClient(this);
        mSettingsClient = LocationServices.getSettingsClient(this);
        createLocationCallback();
        createLocationRequest();
        buildLocationSettingsRequest();

        if (sessionManager.getIsLoggedIn()) {
            if (sessionManager.getUser() != null) {
                appInstance.userDetail = sessionManager.getUser();
                sessionManager.setUserId(appInstance.userDetail.user.id);
            }
        }

        initData();
        bindControls();

        if (appInstance.userDetail.user.userType.equalsIgnoreCase(getString(R.string.customer_))) {
            if (!checkNetworkConnection(mContext))
                return;
            getCustomerAppointments();
        }

        if (getIntent().hasExtra(Constants.BundleKeyTag.HOME)) {
            if (getIntent().getIntExtra(Constants.BundleKeyTag.HOME, 0) == Constants.Screen.FINANCIAL_CENTER) {
                selectItem(2);
                return;
            } else if (getIntent().getIntExtra(Constants.BundleKeyTag.HOME, 0) == Constants.Screen.MANAGE_SHOPS) {
                selectItem(3);
                return;
            }
        }

        if (!checkTabChange()) {
            if (savedInstanceState == null) {
                selectItem(0);
            }
        }
    }

    private void createLocationRequest() {
        mLocationRequest = new LocationRequest();
        mLocationRequest.setInterval(UPDATE_INTERVAL_IN_MILLISECONDS);
        mLocationRequest.setFastestInterval(FASTEST_UPDATE_INTERVAL_IN_MILLISECONDS);
        mLocationRequest.setPriority(LocationRequest.PRIORITY_HIGH_ACCURACY);
    }

    private void createLocationCallback() {
        mLocationCallback = new LocationCallback() {
            @Override
            public void onLocationResult(LocationResult locationResult) {
                super.onLocationResult(locationResult);
                mCurrentLocation = locationResult.getLastLocation();
                sessionManager.setDeviceLatitude("" + mCurrentLocation.getLatitude());
                sessionManager.setDeviceLongitude("" + mCurrentLocation.getLongitude());
                ApiClient.LATITUDE = "" + mCurrentLocation.getLatitude();
                ApiClient.LONGITUDE = "" + mCurrentLocation.getLongitude();
            }
        };
    }

    private void buildLocationSettingsRequest() {
        LocationSettingsRequest.Builder builder = new LocationSettingsRequest.Builder();
        builder.addLocationRequest(mLocationRequest);
        mLocationSettingsRequest = builder.build();
    }

    @Override
    void initData() {
        initToolBar();
        mDrawerLayout = getView(R.id.drawer_layout);
        relativeLayoutDrawer = getView(R.id.rl_drawer);
        mDrawerList = getView(R.id.left_drawer);
        textViewUserName = getView(R.id.tv_username);
        imageViewProfilePicture = getView(R.id.iv_profile_pic);
        imageViewMenuProfile = getView(R.id.iv_profile_picture);

        initializeBindDrawer();
        customerManagerObj = new CustomerManager(this, this);
        commonManagerObj = new CommonManager(this, this);
    }

    @Override
    void bindControls() {
        mDrawerList.setHasFixedSize(true);
        final LinearLayoutManager layoutManager = new LinearLayoutManager(mContext);
        layoutManager.setOrientation(LinearLayoutManager.VERTICAL);
        mDrawerList.setLayoutManager(layoutManager);

        getView(R.id.iv_edit).setOnClickListener(this);
        getView(R.id.menu_profile).setOnClickListener(this);
        getView(R.id.tv_username).setOnClickListener(this);
        getView(R.id.iv_profile_pic).setOnClickListener(this);

        NavigationAdapter navigationAdapter = new NavigationAdapter((AppActivity) mContext);
        navigationAdapter.setOnItemClickListener(this);
        mDrawerList.setAdapter(navigationAdapter);

        if (appInstance.userDetail.user.userType.equalsIgnoreCase(getString(R.string.customer_))) {
            navigationAdapter.setList(prepareCustomerNavigationItems());
            USER_TYPE = Constants.UserType.CUSTOMER;
        } else if (appInstance.userDetail.user.userType.equalsIgnoreCase(getString(R.string.barber_))) {
            navigationAdapter.setList(prepareBarberNavigationItems());
            USER_TYPE = Constants.UserType.BARBER;
        }
        navigationAdapter.notifyDataSetChanged();
    }

    @Override
    public void onResume() {
        super.onResume();
        if (!TextUtils.isEmpty(appInstance.userDetail.user.picture)) {
            imageLoader.displayImage(sessionManager.getImageBaseUrl() + appInstance.userDetail.user.picture, imageViewProfilePicture, options);
            imageLoader.displayImage(sessionManager.getImageBaseUrl() + appInstance.userDetail.user.picture, imageViewMenuProfile, options);
        }

        String fullName = "";
        if (!TextUtils.isEmpty(appInstance.userDetail.user.firstName))
            fullName = appInstance.userDetail.user.firstName;
        if (!TextUtils.isEmpty(appInstance.userDetail.user.firstName) && !TextUtils.isEmpty(appInstance.userDetail.user.lastName))
            fullName = appInstance.userDetail.user.firstName + " " + appInstance.userDetail.user.lastName;
        textViewUserName.setText(fullName);

        if (checkPermissions()) {
            startLocationUpdates();
        } else if (!checkPermissions()) {
            requestPermissions();
        }
    }

    private void initToolBar() {
        toolbar = getView(R.id.toolbar);
        setSupportActionBar(toolbar);
        getSupportActionBar().setHomeButtonEnabled(true);
        getSupportActionBar().setTitle("");
        imageViewToggleMenu = getView(R.id.iv_toggle_menu);
    }

    private void initializeBindDrawer() {
        mDrawerToggle = new ActionBarDrawerToggle(this, mDrawerLayout, toolbar, R.string.drawer_open, R.string.drawer_close) {
            public void onDrawerClosed(View view) {

            }

            public void onDrawerOpened(View drawerView) {

            }

            public void onDrawerSlide(View drawerView, float slideOffset) {
                super.onDrawerSlide(drawerView, slideOffset);
            }
        };
        mDrawerLayout.setDrawerListener(mDrawerToggle);

        mDrawerToggle.setDrawerIndicatorEnabled(false);
        imageViewToggleMenu.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (mDrawerLayout.isDrawerVisible(GravityCompat.START)) {
                    mDrawerLayout.closeDrawer(GravityCompat.START);
                } else {
                    mDrawerLayout.openDrawer(GravityCompat.START);
                }
            }
        });
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        if (mDrawerToggle.onOptionsItemSelected(item)) {
            return true;
        }
        return super.onOptionsItemSelected(item);
    }

    @Override
    public void onBackPressed() {
        if (!isTabChange) {
            if (doubleBackToExitPressedOnce) {
                super.onBackPressed();
                return;
            }
            this.doubleBackToExitPressedOnce = true;
            showSnackBar(getString(R.string.please_tap_back_again_to_exit));
            new Handler().postDelayed(new Runnable() {
                @Override
                public void run() {
                    doubleBackToExitPressedOnce = false;
                }
            }, 2000);
        } else {
            super.onBackPressed();
        }
    }

    @Override
    public void onItemClick(Object item, int position) {
        selectItem(position);
    }

    private List<Navigation> prepareCustomerNavigationItems() {
        List<Navigation> listNavigation = new ArrayList<>();
        listNavigation.add(new Navigation(getString(R.string.home), R.drawable.home));
        listNavigation.add(new Navigation(getString(R.string.gallery), R.drawable.gallery));
        listNavigation.add(new Navigation(getString(R.string.favorite_barbers), R.drawable.star));
        listNavigation.add(new Navigation(getString(R.string.invite_barber), R.drawable.invite_customer));
        listNavigation.add(new Navigation(getString(R.string.contact_babrdo), R.drawable.call));
        listNavigation.add(new Navigation(getString(R.string.logout), R.drawable.logout));

        return listNavigation;
    }

    private List<Navigation> prepareBarberNavigationItems() {
        List<Navigation> listNavigation = new ArrayList<>();
        listNavigation.add(new Navigation(getString(R.string.home), R.drawable.home));
        listNavigation.add(new Navigation(getString(R.string.gallery), R.drawable.gallery));
        listNavigation.add(new Navigation(" " + getString(R.string.financial_center), R.drawable.finance));
        listNavigation.add(new Navigation(getString(R.string.manage_shop), R.drawable.shop));
        listNavigation.add(new Navigation(getString(R.string.invite_to_barbrdo), R.drawable.invite_customer));
        listNavigation.add(new Navigation(getString(R.string.contact_babrdo), R.drawable.call));
        listNavigation.add(new Navigation(" " + getString(R.string.logout), R.drawable.logout));

        return listNavigation;
    }

    private void selectItem(int position) {
        switch (USER_TYPE) {
            case Constants.UserType.CUSTOMER:
                switch (position) {
                    case 0:
                        final SelectBarbersFragment selectBarbersFragment = new SelectBarbersFragment();
                        addFragment(selectBarbersFragment);
                        break;
                    case 1:
                        GalleryFragment galleryFragment = new GalleryFragment();
                        addFragment(galleryFragment);
                        break;
                    case 2:
                        FavoriteBarbersFragment favoriteBarbersFragment = new FavoriteBarbersFragment();
                        addFragment(favoriteBarbersFragment);
                        break;
                    case 3:
                        startActivity(InviteCustomerActivity.class);
                        break;
                    case 4:
                        startActivity(ContactBarbrDoActivity.class);
                        break;
                    case 5:
                        utilObj.showAlertDialog(mContext, getString(R.string.logout), getString(R.string.do_you_really_want_to_logout_application),
                                getString(R.string.ok), getString(R.string.cancel), new DialogActionCallback() {
                                    @Override
                                    public void doOnPositive() {
                                        utilObj.startLoader(mContext);
                                        commonManagerObj.logOut();
                                    }

                                    @Override
                                    public void doOnNegative() {

                                    }
                                });
                        break;
                }
                break;

            case Constants.UserType.BARBER:
                switch (position) {
                    case 0:
                        BarberHomeFragment barberHomeFragment = new BarberHomeFragment();
                        addFragment(barberHomeFragment);
                        break;
                    case 1:
                        GalleryFragment galleryFragment = new GalleryFragment();
                        addFragment(galleryFragment);
                        break;
                    case 2:
                        startActivity(FinanceCenterActivity.class);
                        break;
                    case 3:
                        startActivity(ManageShopsActivity.class);
                        break;
                    case 4:
                        startActivity(InviteCustomerActivity.class);
                        break;
                    case 5:
                        startActivity(ContactBarbrDoActivity.class);
                        break;
                    case 6:
                        utilObj.showAlertDialog(mContext, getString(R.string.logout), getString(R.string.do_you_really_want_to_logout_application),
                                getString(R.string.ok), getString(R.string.cancel), new DialogActionCallback() {
                                    @Override
                                    public void doOnPositive() {
                                        utilObj.startLoader(mContext);
                                        commonManagerObj.logOut();
                                    }

                                    @Override
                                    public void doOnNegative() {

                                    }
                                });
                        break;
                }
                break;
        }

        mDrawerLayout.closeDrawer(relativeLayoutDrawer);
    }

    @Override
    protected void onPostCreate(Bundle savedInstanceState) {
        super.onPostCreate(savedInstanceState);
        mDrawerToggle.syncState();
    }

    @Override
    public void onConfigurationChanged(Configuration newConfig) {
        super.onConfigurationChanged(newConfig);
        mDrawerToggle.onConfigurationChanged(newConfig);
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.iv_edit:
                mDrawerLayout.closeDrawer(relativeLayoutDrawer);
                startActivity(UserProfileActivity.class);
                break;

            case R.id.menu_profile:
                startActivity(UserProfileActivity.class);
                break;

            case R.id.tv_username:
                mDrawerLayout.closeDrawer(relativeLayoutDrawer);
                startActivity(UserProfileActivity.class);
                break;

            case R.id.iv_profile_pic:
                mDrawerLayout.closeDrawer(relativeLayoutDrawer);
                startActivity(UserProfileActivity.class);
                break;
        }
    }

    private boolean checkTabChange() {
        Bundle extras = getIntent().getExtras();
        if (extras != null) {
            isTabChange = true;
            if (extras.getString(Constants.BundleKeyTag.TAB).equalsIgnoreCase(Constants.CustomerTab.GALLERY)) {
                selectItem(1);
            } else if (extras.getString(Constants.BundleKeyTag.TAB).equalsIgnoreCase(Constants.CustomerTab.HOME)) {
                isPageSelect = true;
                selectItem(0);
            }
            return true;
        }
        return false;
    }

    private void getCustomerAppointments() {
        utilObj.startLoader(mContext);
        customerManagerObj.getAppointments();
    }

    @Override
    public void onSuccessRedirection(int taskID) {
        utilObj.stopLoader();
        switch (taskID) {
            case Constants.TaskID.APPOINTMENT:
                if (appInstance.customerAppointment.data.confirm.size() > 0) {
                    CustomerAppointment.Confirm confirm = appInstance.customerAppointment.data.confirm.get(0);
                    Bundle bundle = new Bundle();
                    bundle.putSerializable(Constants.BundleKeyTag.SERIALIZED_DATA, confirm);
                    bundle.putBoolean(Constants.BundleKeyTag.IS_REQUEST_ACCEPTED, true);
                    startActivity(RequestAcceptedActivity.class, bundle);
                    break;
                }

                if (appInstance.customerAppointment.data.complete.size() > 0) {
                    CustomerAppointment.Complete complete = appInstance.customerAppointment.data.complete.get(0);
                    if (!complete.isRatingGiven) {
                        Bundle bundle = new Bundle();
                        bundle.putSerializable(Constants.BundleKeyTag.SERIALIZED_DATA, complete);
                        startActivity(RateBarberActivity.class, bundle);
                        break;
                    }
                }
                break;

            case Constants.TaskID.SHOP_PROFILE:

                break;
            case Constants.TaskID.LOGOUT:
                logout(mContext, LogInActivity.class);
                NavigationDrawerActivity.this.finish();
                break;
        }
    }

    @Override
    public void onFailureRedirection(String errorMessage) {
        utilObj.stopLoader();
        showSnackBar(errorMessage);
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        switch (requestCode) {
            case REQUEST_CHECK_SETTINGS:
                switch (resultCode) {
                    case Activity.RESULT_OK:
                        Log.i(TAG, "User agreed to make required location settings changes.");
                        startLocationUpdates();
                        break;
                    case Activity.RESULT_CANCELED:
                        Log.i(TAG, "User chose not to make required location settings changes.");
                        mRequestingLocationUpdates = false;
                        finish();
                        break;
                }
                break;
        }
    }

    private void startLocationUpdates() {
        mSettingsClient.checkLocationSettings(mLocationSettingsRequest)
                .addOnSuccessListener(this, new OnSuccessListener<LocationSettingsResponse>() {
                    @Override
                    public void onSuccess(LocationSettingsResponse locationSettingsResponse) {
                        Log.i(TAG, "All location settings are satisfied.");

                        //noinspection MissingPermission
                        mFusedLocationClient.requestLocationUpdates(mLocationRequest,
                                mLocationCallback, Looper.myLooper());

                    }
                })
                .addOnFailureListener(this, new OnFailureListener() {
                    @Override
                    public void onFailure(@NonNull Exception e) {
                        int statusCode = ((ApiException) e).getStatusCode();
                        switch (statusCode) {
                            case LocationSettingsStatusCodes.RESOLUTION_REQUIRED:
                                Log.i(TAG, "Location settings are not satisfied. Attempting to upgrade " +
                                        "location settings ");
                                try {
                                    ResolvableApiException rae = (ResolvableApiException) e;
                                    rae.startResolutionForResult(NavigationDrawerActivity.this, REQUEST_CHECK_SETTINGS);
                                } catch (IntentSender.SendIntentException sie) {
                                    Log.i(TAG, "PendingIntent unable to execute request.");
                                }
                                break;
                            case LocationSettingsStatusCodes.SETTINGS_CHANGE_UNAVAILABLE:
                                String errorMessage = "Location settings are inadequate, and cannot be " +
                                        "fixed here. Fix in Settings.";
                                Log.e(TAG, errorMessage);
                                Toast.makeText(NavigationDrawerActivity.this, errorMessage, Toast.LENGTH_LONG).show();
                                mRequestingLocationUpdates = false;
                        }
                    }
                });
    }

    private void stopLocationUpdates() {
        if (!mRequestingLocationUpdates) {
            Log.d(TAG, "stopLocationUpdates: updates never requested, no-op.");
            return;
        }

        mFusedLocationClient.removeLocationUpdates(mLocationCallback)
                .addOnCompleteListener(this, new OnCompleteListener<Void>() {
                    @Override
                    public void onComplete(@NonNull Task<Void> task) {
                        mRequestingLocationUpdates = false;
                    }
                });
    }

    @Override
    protected void onPause() {
        super.onPause();
        stopLocationUpdates();
    }

    private boolean checkPermissions() {
        int permissionState = ActivityCompat.checkSelfPermission(this,
                Manifest.permission.ACCESS_FINE_LOCATION);
        return permissionState == PackageManager.PERMISSION_GRANTED;
    }

    private void requestPermissions() {
        boolean shouldProvideRationale =
                ActivityCompat.shouldShowRequestPermissionRationale(this,
                        Manifest.permission.ACCESS_FINE_LOCATION);
        if (shouldProvideRationale) {
            Log.i(TAG, "Displaying permission rationale to provide additional context.");
            showSnackBar(R.string.permission_rationale,
                    android.R.string.ok, new View.OnClickListener() {
                        @Override
                        public void onClick(View view) {
                            ActivityCompat.requestPermissions(NavigationDrawerActivity.this,
                                    new String[]{Manifest.permission.ACCESS_FINE_LOCATION},
                                    REQUEST_PERMISSIONS_REQUEST_CODE);
                        }
                    });
        } else {
            Log.i(TAG, "Requesting permission");
            ActivityCompat.requestPermissions(NavigationDrawerActivity.this,
                    new String[]{Manifest.permission.ACCESS_FINE_LOCATION},
                    REQUEST_PERMISSIONS_REQUEST_CODE);
        }
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions,
                                           @NonNull int[] grantResults) {
        Log.i(TAG, "onRequestPermissionResult");
        if (requestCode == REQUEST_PERMISSIONS_REQUEST_CODE) {
            if (grantResults.length <= 0) {
                Log.i(TAG, "User interaction was cancelled.");
            } else if (grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                if (mRequestingLocationUpdates) {
                    Log.i(TAG, "Permission granted, updates requested, starting location updates");
                    startLocationUpdates();
                }
            } else {
                showSnackBar(R.string.permission_denied_explanation,
                        R.string.settings, new View.OnClickListener() {
                            @Override
                            public void onClick(View view) {
                                Intent intent = new Intent();
                                intent.setAction(
                                        Settings.ACTION_APPLICATION_DETAILS_SETTINGS);
                                Uri uri = Uri.fromParts("package",
                                        BuildConfig.APPLICATION_ID, null);
                                intent.setData(uri);
                                intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                                startActivity(intent);
                            }
                        });
                return;
            }
        }
    }
}
