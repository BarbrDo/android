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
import android.util.Log;
import android.view.MenuItem;
import android.view.View;
import android.widget.EditText;
import android.widget.Toast;

import com.barbrdo.app.BuildConfig;
import com.barbrdo.app.R;
import com.barbrdo.app.async.ApiClient;
import com.barbrdo.app.dataobject.CheckFacebookInput;
import com.barbrdo.app.dataobject.FacebookUserData;
import com.barbrdo.app.dataobject.User;
import com.barbrdo.app.interfaces.DialogActionCallback;
import com.barbrdo.app.interfaces.FBActivityCallBack;
import com.barbrdo.app.interfaces.ServiceRedirection;
import com.barbrdo.app.managers.LoginManager;
import com.barbrdo.app.sociallogin.UserFacebookLogin;
import com.barbrdo.app.utils.Constants;
import com.facebook.FacebookSdk;
import com.facebook.login.widget.LoginButton;
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
import com.google.gson.JsonElement;
import com.google.gson.JsonParser;

public class LogInActivity extends AppActivity implements ServiceRedirection, FBActivityCallBack {
    private static final String TAG = LogInActivity.class.getName();
    private Context mContext;
    private EditText editTextEmail;
    private TextInputLayout textInputLayoutPassword;
    private EditText editTextPassword;
    private LoginButton buttonFacebookLogin;
    private String email;
    private String password;
    private String message;
    private LoginManager loginManagerObj;
    private UserFacebookLogin mUserFacebookLogin;
    private User userFacebookObj;
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
        FacebookSdk.sdkInitialize(this.getApplicationContext());
        setContentView(R.layout.activity_login);
        mContext = LogInActivity.this;

        mFusedLocationClient = LocationServices.getFusedLocationProviderClient(this);
        mSettingsClient = LocationServices.getSettingsClient(this);
        createLocationCallback();
        createLocationRequest();
        buildLocationSettingsRequest();

        initData();
        bindControls();
        initFacebookLogin();

        if (getIntent().hasExtra(Constants.BundleKeyTag.IS_SUBSCRIPTION_EXPIRED)) {
            sessionManager.clearRememberMe();
            if (getIntent().getBooleanExtra(Constants.BundleKeyTag.IS_SUBSCRIPTION_EXPIRED, false)) {
                utilObj.showAlertDialogNoTitle(mContext, "Subscription required.", "Ok", "", new DialogActionCallback() {
                    @Override
                    public void doOnPositive() {

                    }

                    @Override
                    public void doOnNegative() {

                    }
                });
            } else if (!getIntent().getBooleanExtra(Constants.BundleKeyTag.IS_SUBSCRIPTION_EXPIRED, false)) {
                utilObj.showAlertDialogNoTitle(mContext, "Unauthorized access.", "Ok", "", new DialogActionCallback() {
                    @Override
                    public void doOnPositive() {

                    }

                    @Override
                    public void doOnNegative() {

                    }
                });
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
                Log.e("LATLNG", mCurrentLocation.getLatitude() + "-----" + mCurrentLocation.getLongitude());
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

    /**
     * Initializes the objects
     *
     * @return none
     */
    @Override
    public void initData() {
        setUpToolBar("");
        editTextEmail = getView(R.id.et_email);
        textInputLayoutPassword = getView(R.id.input_layout_password);
        editTextPassword = getView(R.id.et_password);
        buttonFacebookLogin = getView(R.id.btn_fb);

        loginManagerObj = new LoginManager(this, this);
    }

    /**
     * Binds the UI controls
     *
     * @return none
     */
    @Override
    public void bindControls() {
        textInputLayoutPassword.setTypeface(Typeface.createFromAsset(getAssets(), getString(R.string.berlin_sans_fb_regular)));
    }

    /**
     * Initialize Facebook
     */
    private void initFacebookLogin() {
        mUserFacebookLogin = new UserFacebookLogin(this, buttonFacebookLogin);
        mUserFacebookLogin.setFBActivityCallBack(this);
        mUserFacebookLogin.initialiseFacebook();
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

    public void loginUser(View view) {
        email = editTextEmail.getText().toString();
        password = editTextPassword.getText().toString();

        if (validateInputFields()) {
            if (!checkNetworkConnection(mContext))
                return;
            utilObj.startLoader(mContext);
            User userObj = new User();
            userObj.email = email;
            userObj.password = password;
            loginManagerObj.authenticateLogin(userObj);
        }
    }

    public void register(View view) {
        startActivity(SignUpActivity.class);
        finish();
    }

    public void forgotPassword(View view) {
        startActivity(ForgotPasswordActivity.class);
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

    /**
     * The method is used to validate the required fields
     *
     * @return boolean true if fields are validated else false
     **/
    private boolean validateInputFields() {
        message = "";
        email = editTextEmail.getText().toString().trim();
        password = editTextPassword.getText().toString().trim();

        //validate the content
        if (email.isEmpty()) {
            message = getString(R.string.error_email_empty);
            utilObj.showError(message, editTextEmail);
        } else if (!utilObj.checkEmail(email)) {
            message = getString(R.string.error_email_invalid);
            utilObj.showError(message, editTextEmail);
        } else if (password.isEmpty()) {
            message = getString(R.string.error_empty_password);
            utilObj.showError(message, editTextPassword);
        }

        if (message.equalsIgnoreCase("") || message == null) {
            return true;
        } else {
            return false;
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
            default:
                mUserFacebookLogin.onActivityResult(requestCode, resultCode, data);
                break;
        }
    }
//    {
//            "orderId":"GPA.1234-5678-9012-34567",
//            "packageName":"com.example.app",
//            "productId":"exampleSku",
//            "purchaseTime":1345678900000,
//            "purchaseState":0,
//            "developerPayload":"bGoa+V7g/yqDXvKRqq+JTFn4uQZbPiQJo4pf9RzJ",
//            "purchaseToken":"opaque-token-up-to-1000-characters"
//    }

    /**
     * The interface method implemented in the java files
     *
     * @param taskID the id based on which the relevant action is performed
     * @return none
     */
    @Override
    public void onSuccessRedirection(int taskID) {
        utilObj.stopLoader();
        if (taskID == Constants.TaskID.LOGIN) {
            sessionManager.saveUser(appInstance.userDetail);
            sessionManager.setIsLoggedIn(true);
            sessionManager.setUserId(appInstance.userDetail.user.id);
            sessionManager.setImageBaseUrl(appInstance.userDetail.imagesPath);
            sessionManager.setAuthorizationToken(appInstance.userDetail.token);
            sessionManager.setUserType(appInstance.userDetail.user.userType);
            clearAllActivitiesFromBackStack(mContext, NavigationDrawerActivity.class);
        } else if (taskID == Constants.TaskID.CHECK_FACEBOOK) {
            sessionManager.saveUser(appInstance.userDetail);
            sessionManager.setIsLoggedIn(true);
            sessionManager.setUserId(appInstance.userDetail.user.id);
            sessionManager.setImageBaseUrl(appInstance.userDetail.imagesPath);
            sessionManager.setAuthorizationToken(appInstance.userDetail.token);
            sessionManager.setUserType(appInstance.userDetail.user.userType);
            clearAllActivitiesFromBackStack(mContext, NavigationDrawerActivity.class);
        }
    }

    /**
     * The interface method implemented in the java files
     *
     * @param errorMessage the error message to be displayed
     * @return none
     */
    @Override
    public void onFailureRedirection(String errorMessage) {
        utilObj.stopLoader();
        if (errorMessage.equalsIgnoreCase("This user not found in database")) {
            Bundle bundle = new Bundle();
            bundle.putSerializable(Constants.BundleKeyTag.SERIALIZED_DATA, userFacebookObj);
            startActivity(SignUpActivity.class, bundle);
            return;
        }
        showSnackBar(errorMessage);
        com.facebook.login.LoginManager.getInstance().logOut();
    }

    @Override
    public void onGetFBUserData(String userData) {
        if (!checkNetworkConnection(mContext))
            return;
        utilObj.startLoader(mContext);
        Gson gson = new Gson();
        JsonParser parser = new JsonParser();
        JsonElement mJson = parser.parse(userData);
        FacebookUserData facebookData = gson.fromJson(mJson, FacebookUserData.class);

        userFacebookObj = new User();
        userFacebookObj.firstName = facebookData.firstName;
        userFacebookObj.lastName = facebookData.lastName;
        userFacebookObj.email = facebookData.email;
        userFacebookObj.facebookId = facebookData.id;

        CheckFacebookInput checkFacebookInput = new CheckFacebookInput();
        checkFacebookInput.facebook_id = facebookData.id;
        loginManagerObj.checkFacebook(checkFacebookInput);
    }

    @Override
    public void onError(String error) {
        showSnackBar(error);
    }

    @Override
    public void onCancel() {
        showSnackBar(getString(R.string.operation_cancelled));
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
                                    rae.startResolutionForResult(LogInActivity.this, REQUEST_CHECK_SETTINGS);
                                } catch (IntentSender.SendIntentException sie) {
                                    Log.i(TAG, "PendingIntent unable to execute request.");
                                }
                                break;
                            case LocationSettingsStatusCodes.SETTINGS_CHANGE_UNAVAILABLE:
                                String errorMessage = "Location settings are inadequate, and cannot be " +
                                        "fixed here. Fix in Settings.";
                                Log.e(TAG, errorMessage);
                                Toast.makeText(LogInActivity.this, errorMessage, Toast.LENGTH_LONG).show();
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
                            ActivityCompat.requestPermissions(LogInActivity.this,
                                    new String[]{Manifest.permission.ACCESS_FINE_LOCATION},
                                    REQUEST_PERMISSIONS_REQUEST_CODE);
                        }
                    });
        } else {
            Log.i(TAG, "Requesting permission");
            ActivityCompat.requestPermissions(LogInActivity.this,
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
