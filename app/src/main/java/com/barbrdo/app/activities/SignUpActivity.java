package com.barbrdo.app.activities;

import android.Manifest;
import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.content.IntentSender;
import android.content.pm.PackageManager;
import android.graphics.Typeface;
import android.location.Location;
import android.net.Uri;
import android.os.Bundle;
import android.os.Looper;
import android.provider.Settings;
import android.support.annotation.NonNull;
import android.support.design.widget.TextInputLayout;
import android.support.v4.app.ActivityCompat;
import android.support.v4.content.ContextCompat;
import android.util.Log;
import android.view.MenuItem;
import android.view.View;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.barbrdo.app.BuildConfig;
import com.barbrdo.app.R;
import com.barbrdo.app.async.ApiClient;
import com.barbrdo.app.dataobject.User;
import com.barbrdo.app.interfaces.DialogActionCallback;
import com.barbrdo.app.interfaces.ServiceRedirection;
import com.barbrdo.app.managers.LoginManager;
import com.barbrdo.app.utils.Constants;
import com.barbrdo.app.utils.PhoneNumberFormattingTextWatcher;
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
import com.google.gson.Gson;

public class SignUpActivity extends AppActivity implements ServiceRedirection, View.OnClickListener {

    private static final String TAG = SignUpActivity.class.getName();
    private Context mContext;
    private ImageView imageViewCustomer;
    private ImageView imageViewBarber;
    private ImageView imageViewBarberShop;
    private TextView textViewCustomer;
    private TextView textViewBarber;
    private TextView textViewBarberShop;
    private EditText editTextEmail;
    private EditText editTextFirstName;
    private EditText editTextLastName;
    private TextInputLayout textInputLayoutPassword;
    private TextInputLayout textInputLayoutConfirmPassword;
    private TextInputLayout textInputLayoutLicenceNumber;
    private EditText editTextPassword;
    private EditText editTextConfirmPassword;
    private EditText editTextMobileNumber;
    private EditText editTextLicenceNumber;
    private String email;
    private String password;
    private String confirmPassword;
    private String mobileNumber;
    private String firstName;
    private String lastName;
    private String licenceNumber;
    private String message;
    private LoginManager loginManager;
    private User userObj;
    private Bundle extras;
    private String mUserType;
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
        setContentView(R.layout.activity_sign_up);

        mFusedLocationClient = LocationServices.getFusedLocationProviderClient(this);
        mSettingsClient = LocationServices.getSettingsClient(this);
        createLocationCallback();
        createLocationRequest();
        buildLocationSettingsRequest();


        mContext = SignUpActivity.this;
        initData();
        bindControls();

        extras = getIntent().getExtras();
        if (extras != null) {
            userObj = (User) extras.getSerializable(Constants.BundleKeyTag.SERIALIZED_DATA);
            textInputLayoutPassword.setVisibility(View.GONE);
            textInputLayoutConfirmPassword.setVisibility(View.GONE);
            editTextEmail.setText(userObj.email);
            editTextFirstName.setText(userObj.firstName);
            editTextLastName.setText(userObj.lastName);
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
        setUpToolBar("");
        editTextEmail = getView(R.id.et_email);
        textInputLayoutPassword = getView(R.id.input_layout_password);
        textInputLayoutConfirmPassword = getView(R.id.input_layout_confirm_password);
        editTextPassword = getView(R.id.et_password);
        editTextConfirmPassword = getView(R.id.et_confirm_password);
        editTextMobileNumber = getView(R.id.et_mobile_number);
        imageViewCustomer = getView(R.id.iv_customer);
        imageViewBarber = getView(R.id.iv_barber);
        imageViewBarberShop = getView(R.id.iv_barber_shop);
        textViewCustomer = getView(R.id.tv_customer);
        textViewBarber = getView(R.id.tv_barber);
        textViewBarberShop = getView(R.id.tv_barber_shop);
        editTextFirstName = getView(R.id.et_first_name);
        editTextLastName = getView(R.id.et_last_name);
        textInputLayoutLicenceNumber = getView(R.id.input_layout_licence);
        editTextLicenceNumber = getView(R.id.et_licence_number);

        loginManager = new LoginManager(this, this);
        userObj = new User();
        mUserType = getString(R.string.customer_);
    }

    @Override
    void bindControls() {
        textInputLayoutPassword.setTypeface(Typeface.createFromAsset(getAssets(), getString(R.string.berlin_sans_fb_regular)));
        textInputLayoutConfirmPassword.setTypeface(Typeface.createFromAsset(getAssets(), getString(R.string.berlin_sans_fb_regular)));

        editTextMobileNumber.addTextChangedListener(new PhoneNumberFormattingTextWatcher(editTextMobileNumber));

        imageViewCustomer.setOnClickListener(this);
        imageViewBarber.setOnClickListener(this);
        imageViewBarberShop.setOnClickListener(this);
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
    protected void onStart() {
        super.onStart();
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
        }
    }

    public void signUpUser(View view) {
        email = editTextEmail.getText().toString();
        password = editTextPassword.getText().toString();

        if (validateInputFields()) {
            if (!checkNetworkConnection(mContext))
                return;
            utilObj.startLoader(mContext);
            userObj.firstName = firstName;
            userObj.lastName = lastName;
            userObj.email = email;
            userObj.password = password;
            userObj.mobileNumber = utilObj.removePhoneNumberFormatting(mobileNumber);
            userObj.userType = mUserType;
            if (mUserType.equalsIgnoreCase(getString(R.string.barber_))
                    || mUserType.equalsIgnoreCase(getString(R.string.shop_))) {
                userObj.licenseNumber = licenceNumber;
            }

            Gson gson = new Gson();
            Log.d("Input JSON", gson.toJson(userObj));

            loginManager.signUpUser(userObj);
        }
    }

    public void login(View view) {
        startActivity(LogInActivity.class);
        finish();
    }

    private boolean validateInputFields() {
        message = "";
        email = editTextEmail.getText().toString();
        password = editTextPassword.getText().toString();
        confirmPassword = editTextConfirmPassword.getText().toString();
        mobileNumber = editTextMobileNumber.getText().toString();
        firstName = editTextFirstName.getText().toString();
        lastName = editTextLastName.getText().toString();
        licenceNumber = editTextLicenceNumber.getText().toString();

        //validate the content
        if (firstName.isEmpty()) {
            message = getString(R.string.error_first_name_empty);
            utilObj.showError(message, editTextFirstName);
        } else if (lastName.isEmpty()) {
            message = getString(R.string.error_last_name_empty);
            utilObj.showError(message, editTextLastName);
        } else if (email.isEmpty()) {
            message = getString(R.string.error_email_empty);
            utilObj.showError(message, editTextEmail);
        } else if (!utilObj.checkEmail(email)) {
            message = getString(R.string.error_email_invalid);
            utilObj.showError(message, editTextEmail);
        } else if (password.isEmpty() && textInputLayoutPassword.getVisibility() == View.VISIBLE) {
            message = getString(R.string.error_empty_password);
            utilObj.showError(message, editTextPassword);
        } else if (password.length() < 6 && textInputLayoutPassword.getVisibility() == View.VISIBLE) {
            message = getString(R.string.error_password_length);
            utilObj.showError(message, editTextPassword);
        } else if (confirmPassword.isEmpty() && textInputLayoutConfirmPassword.getVisibility() == View.VISIBLE) {
            message = getString(R.string.error_empty_password);
            utilObj.showError(message, editTextConfirmPassword);
        } else if (!password.equalsIgnoreCase(confirmPassword) && textInputLayoutPassword.getVisibility() == View.VISIBLE) {
            message = getString(R.string.error_password_match);
            utilObj.showError(message, editTextPassword);
        } else if (mobileNumber.isEmpty()) {
            message = getString(R.string.error_mobile_number_empty);
            utilObj.showError(message, editTextMobileNumber);
        }

        if (mUserType.equalsIgnoreCase(getString(R.string.barber_))
                || mUserType.equalsIgnoreCase(getString(R.string.shop_))) {
            if (licenceNumber.isEmpty()) {
                message = getString(R.string.error_mobile_licence_empty);
                utilObj.showError(message, editTextLicenceNumber);
            }
        }

        if (message.equalsIgnoreCase("") || message == null) {
            return true;
        } else {
            return false;
        }
    }

    @Override
    public void onSuccessRedirection(int taskID) {
        utilObj.stopLoader();
        switch (taskID) {
            case Constants.TaskID.SIGN_UP:
                if (extras == null) {
                    utilObj.showAlertDialog(mContext, "Registration Successful", appInstance.userDetail.msg, getString(R.string.ok), "", new DialogActionCallback() {
                        @Override
                        public void doOnPositive() {
//                            startActivity(LogInActivity.class);
//                            SignUpActivity.this.finish();
                            sessionManager.saveUser(appInstance.userDetail);
                            sessionManager.setIsLoggedIn(true);
                            sessionManager.setUserId(appInstance.userDetail.user.id);
                            sessionManager.setImageBaseUrl(appInstance.userDetail.imagesPath);
                            sessionManager.setAuthorizationToken(appInstance.userDetail.token);
                            sessionManager.setUserType(appInstance.userDetail.user.userType);
                            clearAllActivitiesFromBackStack(mContext, NavigationDrawerActivity.class);
                        }

                        @Override
                        public void doOnNegative() {
                        }
                    });
                } else {
                    sessionManager.saveUser(appInstance.userDetail);
                    sessionManager.setIsLoggedIn(true);
                    sessionManager.setUserId(appInstance.userDetail.user.id);
                    sessionManager.setImageBaseUrl(appInstance.userDetail.imagesPath);
                    sessionManager.setAuthorizationToken(appInstance.userDetail.token);
                    sessionManager.setUserType(appInstance.userDetail.user.userType);
                    clearAllActivitiesFromBackStack(mContext, NavigationDrawerActivity.class);
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
            case R.id.iv_customer:
                imageViewCustomer.setImageResource(R.drawable.customer_active);
                imageViewBarber.setImageResource(R.drawable.barber);
                imageViewBarberShop.setImageResource(R.drawable.barber_shop);
                textViewCustomer.setTextColor(ContextCompat.getColor(mContext, R.color.white));
                textViewBarber.setTextColor(ContextCompat.getColor(mContext, R.color.text_gray));
                textViewBarberShop.setTextColor(ContextCompat.getColor(mContext, R.color.text_gray));
                mUserType = getString(R.string.customer_);
                textInputLayoutLicenceNumber.setVisibility(View.GONE);
                break;

            case R.id.iv_barber:
                imageViewCustomer.setImageResource(R.drawable.customer);
                imageViewBarber.setImageResource(R.drawable.barber_active);
                imageViewBarberShop.setImageResource(R.drawable.barber_shop);
                textViewCustomer.setTextColor(ContextCompat.getColor(mContext, R.color.text_gray));
                textViewBarber.setTextColor(ContextCompat.getColor(mContext, R.color.white));
                textViewBarberShop.setTextColor(ContextCompat.getColor(mContext, R.color.text_gray));
                mUserType = getString(R.string.barber_);
                textInputLayoutLicenceNumber.setVisibility(View.VISIBLE);
                break;

            case R.id.iv_barber_shop:
                imageViewCustomer.setImageResource(R.drawable.customer);
                imageViewBarber.setImageResource(R.drawable.barber);
                imageViewBarberShop.setImageResource(R.drawable.barber_shop_active);
                textViewCustomer.setTextColor(ContextCompat.getColor(mContext, R.color.text_gray));
                textViewBarber.setTextColor(ContextCompat.getColor(mContext, R.color.text_gray));
                textViewBarberShop.setTextColor(ContextCompat.getColor(mContext, R.color.white));
                mUserType = getString(R.string.shop_);
                textInputLayoutLicenceNumber.setVisibility(View.VISIBLE);
                break;
        }
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
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
                                    rae.startResolutionForResult(SignUpActivity.this, REQUEST_CHECK_SETTINGS);
                                } catch (IntentSender.SendIntentException sie) {
                                    Log.i(TAG, "PendingIntent unable to execute request.");
                                }
                                break;
                            case LocationSettingsStatusCodes.SETTINGS_CHANGE_UNAVAILABLE:
                                String errorMessage = "Location settings are inadequate, and cannot be " +
                                        "fixed here. Fix in Settings.";
                                Log.e(TAG, errorMessage);
                                Toast.makeText(SignUpActivity.this, errorMessage, Toast.LENGTH_LONG).show();
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
    public void onResume() {
        super.onResume();
        if (checkPermissions()) {
            startLocationUpdates();
        } else if (!checkPermissions()) {
            requestPermissions();
        }
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
                            ActivityCompat.requestPermissions(SignUpActivity.this,
                                    new String[]{Manifest.permission.ACCESS_FINE_LOCATION},
                                    REQUEST_PERMISSIONS_REQUEST_CODE);
                        }
                    });
        } else {
            Log.i(TAG, "Requesting permission");
            ActivityCompat.requestPermissions(SignUpActivity.this,
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
