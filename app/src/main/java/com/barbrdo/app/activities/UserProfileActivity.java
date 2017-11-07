package com.barbrdo.app.activities;

import android.Manifest;
import android.app.Activity;
import android.app.DatePickerDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.net.Uri;
import android.os.Bundle;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AlertDialog;
import android.text.SpannableString;
import android.text.TextUtils;
import android.text.style.UnderlineSpan;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.inputmethod.InputMethodManager;
import android.widget.DatePicker;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.ListAdapter;
import android.widget.SeekBar;
import android.widget.TextView;

import com.barbrdo.app.R;
import com.barbrdo.app.adapters.ArrayAdapterWithIcon;
import com.barbrdo.app.dataobject.UpdateAccountInput;
import com.barbrdo.app.dataobject.UpdateShopInput;
import com.barbrdo.app.dataobject.UserAppointment;
import com.barbrdo.app.dataobject.UserDetail;
import com.barbrdo.app.interfaces.DialogActionCallback;
import com.barbrdo.app.interfaces.ServiceRedirection;
import com.barbrdo.app.managers.CommonManager;
import com.barbrdo.app.utils.Constants;
import com.barbrdo.app.utils.PhoneNumberFormattingTextWatcher;
import com.google.gson.Gson;

import java.io.File;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.Locale;

import pl.aprilapps.easyphotopicker.DefaultCallback;
import pl.aprilapps.easyphotopicker.EasyImage;
import pl.tajchert.nammu.Nammu;
import pl.tajchert.nammu.PermissionCallback;

public class UserProfileActivity extends AppActivity implements ServiceRedirection, View.OnClickListener {

    private Context mContext;
    private TextView textViewUsername;
    private TextView textViewNoCuts;
    private TextView textViewNoPhotos;
    private ImageView imageViewProfilePicture;
    private TextView textViewMemberSince;
    private TextView textViewEmail;
    private EditText editTextPhone;
    private EditText editTextBio;
    private EditText editTextFirstName;
    private EditText editTextLastName;
    private EditText editTextPassword;
    private EditText editTextConfirmPassword;
    private EditText editTextShopName;
    private EditText editTextAddress;
    private EditText editTextCity;
    private EditText editTextState;
    private EditText editTextZip;
    private TextView textViewMemberSinceTitle;
    private TextView textViewLicence;
    private TextView textViewSearchRadius;
    private TextView textViewChangePassword;
    private TextView textViewSubscriptionTitle;
    private TextView textViewExpiry;
    private TextView textViewCancel;
    private TextView textViewUpgrade;
    private CommonManager commonManagerObj;
    private CharSequence[] mOptions = {"Take Picture", "Choose from Gallery", "Cancel"};
    private boolean isEdit = false;
    private String message;
    private String phone;
    private String firstName;
    private String lastName;
    private String password;
    private String confirmPassword;
    private String shopName;
    private String address;
    private String city;
    private String state;
    private String zip;
    private UserDetail.User user;
    private DatePickerDialog datePickerDialog;
    private SimpleDateFormat dateFormatter;
    private String mLicencedSince;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_user_profile);
        mContext = UserProfileActivity.this;
        Nammu.init(mContext);
        int permissionCheck = ContextCompat.checkSelfPermission(mContext, Manifest.permission.WRITE_EXTERNAL_STORAGE);
        if (permissionCheck != PackageManager.PERMISSION_GRANTED) {
            Nammu.askForPermission((Activity) mContext, Manifest.permission.WRITE_EXTERNAL_STORAGE, new PermissionCallback() {
                @Override
                public void permissionGranted() {
                    //Nothing, this sample saves to Public gallery so it needs permission
                }

                @Override
                public void permissionRefused() {
                    finish();
                }
            });
        }

        EasyImage.configuration(mContext).setImagesFolderName(getString(R.string.gallery_folder_name));

        initData();
        bindControls();
        if (appInstance.userDetail != null)
            getUserProfile(appInstance.userDetail.user.id);

        dateFormatter = new SimpleDateFormat(Constants.DateFormat.YYYYMMDDTHHMMSS, Locale.ENGLISH);
    }

    @Override
    void initData() {
        setUpToolBar("");
        textViewUsername = getView(R.id.tv_username);
        textViewNoCuts = getView(R.id.tv_cuts);
        textViewNoPhotos = getView(R.id.tv_photos);
        imageViewProfilePicture = getView(R.id.iv_profile_picture);
        textViewMemberSinceTitle = getView(R.id.tv_member_since_title);
        textViewMemberSince = getView(R.id.tv_member_since);
        textViewLicence = getView(R.id.tv_licence_number);
        textViewSearchRadius = getView(R.id.tv_radius);
        textViewEmail = getView(R.id.tv_email);
        editTextBio = getView(R.id.et_bio);
        editTextPhone = getView(R.id.et_phone);
        editTextFirstName = getView(R.id.et_first_name);
        editTextLastName = getView(R.id.et_last_name);
        editTextPassword = getView(R.id.et_password);
        editTextConfirmPassword = getView(R.id.et_confirm_password);
        editTextShopName = getView(R.id.et_shop_name);
        editTextAddress = getView(R.id.et_address);
        editTextCity = getView(R.id.et_city);
        editTextState = getView(R.id.et_state);
        editTextZip = getView(R.id.et_pincode);
        textViewChangePassword = getView(R.id.tv_change_password);
        textViewSubscriptionTitle = getView(R.id.tv_subscription_title);
        textViewExpiry = getView(R.id.tv_expiry);
        textViewCancel = getView(R.id.tv_cancel);
        textViewUpgrade = getView(R.id.tv_upgrade);
        editTextPhone.setEnabled(false);
        editTextFirstName.setEnabled(false);
        editTextLastName.setEnabled(false);
        editTextPassword.setEnabled(false);
        editTextConfirmPassword.setEnabled(false);
        editTextShopName.setEnabled(false);
        editTextAddress.setEnabled(false);
        editTextCity.setEnabled(false);
        editTextState.setEnabled(false);
        editTextZip.setEnabled(false);
        textViewSearchRadius.setEnabled(false);
        editTextBio.setEnabled(false);

        commonManagerObj = new CommonManager(this, this);
        initDatePicker();
    }

    private void setData() {
        user = appInstance.userProfile.user;

        String fullName = "";
        if (!TextUtils.isEmpty(user.firstName))
            fullName = user.firstName;
        if (!TextUtils.isEmpty(user.firstName) && !TextUtils.isEmpty(user.lastName))
            fullName = user.firstName + " " + user.lastName;
        textViewUsername.setText(fullName);
        if (!TextUtils.isEmpty(user.firstName))
            editTextFirstName.setText(user.firstName);
        if (!TextUtils.isEmpty(user.lastName))
            editTextLastName.setText(user.lastName);
        textViewEmail.setText(user.email);
        editTextPhone.setText(utilObj.formatPhoneNumber(user.mobileNumber));
        textViewNoPhotos.setText(user.gallery.size() + "");
        editTextBio.setText(user.bio);

        if (!TextUtils.isEmpty(appInstance.userDetail.user.picture)) {
            imageLoader.displayImage(sessionManager.getImageBaseUrl() + appInstance.userDetail.user.picture, imageViewProfilePicture, options);
        }

        if (user.userType.equalsIgnoreCase(getString(R.string.customer_))) {
            int noOfCuts = 0;
            if (user.appointments != null) {
                for (UserAppointment appointment : user.appointments) {
                    if (appointment.appointmentStatus.equalsIgnoreCase("completed")) {
                        noOfCuts++;
                    }
                }
                textViewNoCuts.setText(noOfCuts + "");
            }
            textViewMemberSinceTitle.setText(getString(R.string.member_since));
            getView(R.id.view_radius).setVisibility(View.VISIBLE);
            getView(R.id.rl_radius).setVisibility(View.VISIBLE);
            if (!TextUtils.isEmpty(user.radiusSearch))
                textViewSearchRadius.setText(user.radiusSearch + " mi");
            getView(R.id.cv_bio).setVisibility(View.GONE);
            textViewMemberSince.setText(utilObj.getMyDateFormat(user.createdDate, Constants.DateFormat.YYYYMMDDTHHMMSS,
                    Constants.DateFormat.MMMYYYY));
        } else if (user.userType.equalsIgnoreCase(getString(R.string.barber_))) {
            getView(R.id.view_licence).setVisibility(View.VISIBLE);
            getView(R.id.rl_licence).setVisibility(View.VISIBLE);
            textViewMemberSinceTitle.setText(getString(R.string.licensed_since));
            textViewLicence.setText(user.licenseNumber);
            textViewNoCuts.setText(user.ratings.size() + "");
//            getView(R.id.cv_subscription).setVisibility(View.VISIBLE);
            getView(R.id.cv_bio).setVisibility(View.VISIBLE);
//            textViewSubscriptionTitle.setText(user.subscriptionPlanName + " ($" + String.format("%.2f", user.subscriptionPrice) + ")");
//            textViewExpiry.setText("Expiry: " + utilObj.getMyDateFormat(user.subscriptionEndDate, Constants.DateFormat.YYYYMMDDTHHMMSS, Constants.DateFormat.MMDDYYYY));

            if (!TextUtils.isEmpty(user.licensedSince)) {
                mLicencedSince = user.licensedSince;
                textViewMemberSince.setText(utilObj.getMyDateFormat(mLicencedSince, Constants.DateFormat.YYYYMMDDTHHMMSS,
                        Constants.DateFormat.MMDDYYYY));
            } else
                textViewMemberSince.setText("mm/dd/yyyy");

            if (user.subscriptionPrice == 0)
                textViewCancel.setVisibility(View.INVISIBLE);
            else
                textViewCancel.setVisibility(View.VISIBLE);


            textViewMemberSince.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    datePickerDialog.show();
                }
            });
        }
    }

    private void getUserProfile(String userId) {
        utilObj.startLoader(mContext);
        commonManagerObj.userProfile(userId);
    }

    @Override
    void bindControls() {
        editTextPhone.addTextChangedListener(new PhoneNumberFormattingTextWatcher(editTextPhone));
        imageViewProfilePicture.setOnClickListener(this);
        textViewSearchRadius.setOnClickListener(this);
        textViewChangePassword.setOnClickListener(this);
        SpannableString content = new SpannableString(getString(R.string.change_password));
        content.setSpan(new UnderlineSpan(), 0, content.length(), 0);
        textViewChangePassword.setText(content);

        getView(R.id.cv_password_change).setVisibility(View.GONE);
        textViewCancel.setOnClickListener(this);
        textViewUpgrade.setOnClickListener(this);
    }

    @Override
    public boolean onPrepareOptionsMenu(Menu menu) {
        MenuItem edit = menu.findItem(R.id.action_edit);
        if (isEdit) {
            edit.setIcon(R.drawable.ic_save_white_24dp);
            edit.setTitle(R.string.save);
        } else {
            edit.setIcon(R.drawable.ic_edit_white_24dp);
            edit.setTitle(R.string.edit);
        }
        return true;
    }


    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        MenuInflater inflater = getMenuInflater();
        inflater.inflate(R.menu.menu_profile, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case android.R.id.home:
                onBackPressed();
                return true;

            case R.id.action_edit:
                if (isEdit) {
                    updateAccount();
                } else {
                    isEdit = true;
                    invalidateOptionsMenu();
                    editTextFirstName.requestFocus();
                    editTextPhone.setEnabled(true);
                    editTextFirstName.setEnabled(true);
                    editTextLastName.setEnabled(true);
                    editTextPassword.setEnabled(true);
                    editTextConfirmPassword.setEnabled(true);
                    editTextBio.setEnabled(true);
                    textViewSearchRadius.setEnabled(true);
                    if (user.userType.equalsIgnoreCase(getString(R.string.barber_)))
                        textViewMemberSince.setEnabled(true);
                    InputMethodManager imm = (InputMethodManager) getSystemService(Context.INPUT_METHOD_SERVICE);
                    imm.showSoftInput(editTextFirstName, InputMethodManager.SHOW_IMPLICIT);
                    getView(R.id.cv_password_change).setVisibility(View.VISIBLE);
                }

                return true;
        }
        return super.onOptionsItemSelected(item);
    }

    @Override
    public void onSuccessRedirection(int taskID) {
        InputMethodManager imm;
        switch (taskID) {
            case Constants.TaskID.SHOP_BARBERS:
                utilObj.stopLoader();
                if (appInstance.shopDetail != null)
                    setData();
                break;

            case Constants.TaskID.PROFILE_PICTURE:
                utilObj.stopLoader();
                if (appInstance.accountUpdate != null) {
                    appInstance.userDetail.user = appInstance.accountUpdate.user;
                    sessionManager.saveUser(appInstance.userDetail);

                    if (!TextUtils.isEmpty(appInstance.userDetail.user.picture)) {
                        imageLoader.displayImage(sessionManager.getImageBaseUrl() + appInstance.userDetail.user.picture, imageViewProfilePicture, options);
                    }
                }
                break;

            case Constants.TaskID.ACCOUNT:
                isEdit = false;
                invalidateOptionsMenu();
                editTextPhone.setEnabled(false);
                editTextFirstName.setEnabled(false);
                editTextLastName.setEnabled(false);
                editTextPassword.setEnabled(false);
                editTextConfirmPassword.setEnabled(false);
                textViewSearchRadius.setEnabled(false);
                editTextBio.setEnabled(false);
                textViewMemberSince.setEnabled(false);
                editTextPassword.setText("");
                editTextConfirmPassword.setText("");
                getView(R.id.cv_password_change).setVisibility(View.GONE);
                if (appInstance.accountUpdate != null) {
                    utilObj.showToast(mContext, appInstance.message, 2);
                    appInstance.userDetail.user = appInstance.accountUpdate.user;
                    sessionManager.saveUser(appInstance.userDetail);

                    String fullName = "";
                    if (!TextUtils.isEmpty(appInstance.userDetail.user.firstName))
                        fullName = appInstance.userDetail.user.firstName;
                    if (!TextUtils.isEmpty(appInstance.userDetail.user.firstName) && !TextUtils.isEmpty(appInstance.userDetail.user.lastName))
                        fullName = appInstance.userDetail.user.firstName + " " + appInstance.userDetail.user.lastName;
                    textViewUsername.setText(fullName);
                }
                if (user.userType.equalsIgnoreCase(getString(R.string.shop_))) {
                    updateShop();
                    return;
                }
                imm = (InputMethodManager) getSystemService(Context.INPUT_METHOD_SERVICE);
                imm.showSoftInput(editTextFirstName, InputMethodManager.HIDE_IMPLICIT_ONLY);
                utilObj.stopLoader();
                break;

            case Constants.TaskID.USER_PROFILE:
                utilObj.stopLoader();
                setData();
                break;

            case Constants.TaskID.UPDATE_SHOP:
                utilObj.stopLoader();
                editTextShopName.setEnabled(false);
                editTextAddress.setEnabled(false);
                editTextCity.setEnabled(false);
                editTextState.setEnabled(false);
                editTextZip.setEnabled(false);
                imm = (InputMethodManager) getSystemService(Context.INPUT_METHOD_SERVICE);
                imm.showSoftInput(editTextFirstName, InputMethodManager.HIDE_IMPLICIT_ONLY);
                break;
        }
    }

    @Override
    public void onFailureRedirection(String errorMessage) {
        utilObj.stopLoader();
        showSnackBar(errorMessage);
    }

    private void updateAccount() {
        if (validateInputFields()) {
            utilObj.startLoader(mContext);
            UpdateAccountInput updateAccountInput = new UpdateAccountInput();
            updateAccountInput.mobileNumber = utilObj.removePhoneNumberFormatting(phone);
            updateAccountInput.firstName = firstName;
            updateAccountInput.lastName = lastName;
            updateAccountInput.licensedSince = mLicencedSince;
            updateAccountInput.bio = editTextBio.getText().toString();

            if (user.userType.equalsIgnoreCase(getString(R.string.customer_))) {
                updateAccountInput.radiusSearch = textViewSearchRadius.getText().toString().replace(" mi", "");
            }

            if (!password.isEmpty()) {
                updateAccountInput.password = password;
                updateAccountInput.confirm = confirmPassword;
            }

            Gson gson = new Gson();
            String json = gson.toJson(updateAccountInput);

            commonManagerObj.updateProfile(updateAccountInput);
        }
    }

    private void updateShop() {
        UpdateShopInput updateShopInput = new UpdateShopInput();
        updateShopInput.id = user.shop.get(0).id;
        updateShopInput.state = state;
        updateShopInput.address = address;
        updateShopInput.city = city;
        updateShopInput.name = shopName;
        updateShopInput.zip = zip;
        commonManagerObj.updateShop(updateShopInput);
    }

    private boolean validateInputFields() {
        message = "";
        firstName = editTextFirstName.getText().toString().trim();
        lastName = editTextLastName.getText().toString().trim();
        phone = editTextPhone.getText().toString().trim();
        password = editTextPassword.getText().toString().trim();
        confirmPassword = editTextConfirmPassword.getText().toString().trim();
        address = editTextAddress.getText().toString().trim();
        city = editTextCity.getText().toString().trim();
        shopName = editTextShopName.getText().toString().trim();
        state = editTextState.getText().toString().trim();
        zip = editTextZip.getText().toString().trim();

        //validate the content
        if (firstName.isEmpty()) {
            message = getString(R.string.error_first_name_empty);
            utilObj.showToast(mContext, message, 1);
        } else if (lastName.isEmpty()) {
            message = getString(R.string.error_last_name_empty);
            utilObj.showToast(mContext, message, 1);
        } else if (phone.isEmpty()) {
            message = getString(R.string.error_mobile_number_empty);
            utilObj.showToast(mContext, message, 1);
        }

        if (!password.isEmpty()) {
            if (!password.equalsIgnoreCase(confirmPassword)) {
                message = getString(R.string.error_password_match);
                utilObj.showToast(mContext, message, 1);
            }
        }

        if (user.userType.equalsIgnoreCase(getString(R.string.shop_))) {
            if (shopName.isEmpty()) {
                message = getString(R.string.error_shop_name_empty);
                utilObj.showToast(mContext, message, 1);
            } else if (address.isEmpty()) {
                message = getString(R.string.error_address_empty);
                utilObj.showToast(mContext, message, 1);
            } else if (city.isEmpty()) {
                message = getString(R.string.error_city_empty);
                utilObj.showToast(mContext, message, 1);
            } else if (state.isEmpty()) {
                message = getString(R.string.error_state_empty);
                utilObj.showToast(mContext, message, 1);
            } else if (zip.isEmpty()) {
                message = getString(R.string.error_zip_empty);
                utilObj.showToast(mContext, message, 1);
            }
        }

        if (message.equalsIgnoreCase("") || message == null) {
            return true;
        } else {
            return false;
        }
    }

    private void captureImage() {
        final String[] items = new String[]{"Take Picture", "Choose from Gallery"};
        final Integer[] icons = new Integer[]{R.drawable.ic_camera, R.drawable.ic_gallery};
        ListAdapter adapter = new ArrayAdapterWithIcon(mContext, items, icons);

        new AlertDialog.Builder(mContext).setTitle(R.string.add_image)
                .setAdapter(adapter, new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int item) {
                        if (items[item].equals(getString(R.string.take_picture))) {
                            onTakePhotoClicked();
                        } else if (items[item].equals(getString(R.string.choose_from_gallery))) {
                            onPickFromDocumentsClicked();
                        }
                    }
                }).show();
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, String[] permissions, int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        Nammu.onRequestPermissionsResult(requestCode, permissions, grantResults);
    }

    protected void onTakePhotoClicked() {
        EasyImage.openCamera(this, 0);
    }

    protected void onPickFromDocumentsClicked() {
        int permissionCheck = ContextCompat.checkSelfPermission(mContext, Manifest.permission.WRITE_EXTERNAL_STORAGE);
        if (permissionCheck == PackageManager.PERMISSION_GRANTED) {
            EasyImage.openDocuments(this, 0);
        } else {
            Nammu.askForPermission((Activity) mContext, Manifest.permission.WRITE_EXTERNAL_STORAGE, new PermissionCallback() {
                @Override
                public void permissionGranted() {
                    EasyImage.openDocuments((Activity) mContext, 0);
                }

                @Override
                public void permissionRefused() {

                }
            });
        }
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        if (requestCode == Constants.RequestCodes.APP_SUBSCRIPTION) {
            getUserProfile(appInstance.userDetail.user.id);
        } else {
            EasyImage.handleActivityResult(requestCode, resultCode, data, (Activity) mContext, new DefaultCallback() {
                @Override
                public void onImagePickerError(Exception e, EasyImage.ImageSource source, int type) {
                    e.printStackTrace();
                }

                @Override
                public void onImagePicked(File imageFile, EasyImage.ImageSource source, int type) {
                    onPhotosReturned(imageFile);
                }

                @Override
                public void onCanceled(EasyImage.ImageSource source, int type) {
                    if (source == EasyImage.ImageSource.CAMERA) {
                        File photoFile = EasyImage.lastlyTakenButCanceledPhoto(mContext);
                        if (photoFile != null) photoFile.delete();
                    }
                }
            });
        }
    }

    private void onPhotosReturned(File returnedPhoto) {
        utilObj.startLoader(mContext);
        File fileDecoded = utilObj.saveBitmapToFile(returnedPhoto);
        commonManagerObj.uploadProfilePicture(fileDecoded);
    }

    @Override
    public void onDestroy() {
        EasyImage.clearConfiguration(mContext);
        super.onDestroy();
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.iv_profile_picture:
                captureImage();
                break;

            case R.id.tv_radius:
                showRadiusDialog(textViewSearchRadius.getText().toString());
                break;

            case R.id.tv_change_password:
                getView(R.id.cv_password_change).setVisibility(View.VISIBLE);
                isEdit = true;
                invalidateOptionsMenu();
                editTextPassword.requestFocus();
                editTextPassword.setEnabled(true);
                editTextConfirmPassword.setEnabled(true);
                InputMethodManager imm = (InputMethodManager) getSystemService(Context.INPUT_METHOD_SERVICE);
                imm.showSoftInput(editTextPassword, InputMethodManager.HIDE_IMPLICIT_ONLY);
                break;

            case R.id.tv_cancel:
                utilObj.showAlertDialog(this, R.string.cancel_subscription, R.string.are_you_sure_you_want_to_cancel_subscription, R.string.ok, R.string.cancel, new DialogActionCallback() {
                    @Override
                    public void doOnPositive() {
                        final String appPackageName = getPackageName();
                        startActivity(new Intent(Intent.ACTION_VIEW, Uri.parse("https://play.google.com/store/apps/details?id=" + appPackageName)));
                    }

                    @Override
                    public void doOnNegative() {
                    }
                });
                break;

            case R.id.tv_upgrade:
                Intent intent = new Intent(mContext, SubscriptionDialogActivity.class);
                if (user.subscriptionPrice == 0)
                    intent.putExtra(Constants.BundleKeyTag.IS_UPGRADE, false);
                else
                    intent.putExtra(Constants.BundleKeyTag.IS_UPGRADE, true);
                intent.putExtra(Constants.BundleKeyTag.IS_USER_PROFILE, true);
                startActivityForResult(intent, Constants.RequestCodes.APP_SUBSCRIPTION);

                break;
        }
    }

    private void showRadiusDialog(String radius) {
        AlertDialog.Builder dialogBuilder = new AlertDialog.Builder(this, R.style.AppCompatAlertDialogStyle);
        LayoutInflater inflater = this.getLayoutInflater();
        View dialogView = inflater.inflate(R.layout.dialog_radius, null);
        dialogBuilder.setView(dialogView);

        final TextView textViewRadius = (TextView) dialogView.findViewById(R.id.tv_radius);
        SeekBar seekBarRadius = (SeekBar) dialogView.findViewById(R.id.seekBar_radius);

        seekBarRadius.setProgress(Integer.parseInt(radius.replace(" mi", "")));
        textViewRadius.setText(radius);
        final int stepSize = 5;
        seekBarRadius.setOnSeekBarChangeListener(new SeekBar.OnSeekBarChangeListener() {
            @Override
            public void onProgressChanged(SeekBar seekBar, int progress, boolean fromUser) {
                progress = ((int) Math.round(progress / stepSize)) * stepSize;
                textViewRadius.setText(progress + " mi");
            }

            @Override
            public void onStartTrackingTouch(SeekBar seekBar) {

            }

            @Override
            public void onStopTrackingTouch(SeekBar seekBar) {

            }
        });

        dialogBuilder.setPositiveButton(getString(R.string.ok), new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int whichButton) {
                textViewSearchRadius.setText(textViewRadius.getText().toString());
                dialog.dismiss();
            }
        });

        dialogBuilder.setNegativeButton(getString(R.string.cancel), new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                dialog.dismiss();
            }
        });

        AlertDialog alertDialog = dialogBuilder.create();
        alertDialog.show();

    }

    public void photosClick(View view) {
        if (user.userType.equalsIgnoreCase(getString(R.string.shop_))) {
            clearAllActivitiesFromBackStack(mContext, NavigationDrawerActivity.class);
        } else {
            Bundle bundle = new Bundle();
            bundle.putString(Constants.BundleKeyTag.TAB, Constants.CustomerTab.GALLERY);
            startActivity(NavigationDrawerActivity.class, bundle);
        }
    }

    public void cutsClick(View view) {
        if (user.userType.equalsIgnoreCase(getString(R.string.barber_))) {
            startActivity(ReviewsActivity.class);
        }
    }

    private void initDatePicker() {
        final Calendar c = Calendar.getInstance();
        int year = c.get(Calendar.YEAR);
        int month = c.get(Calendar.MONTH);
        int day = c.get(Calendar.DAY_OF_MONTH);
        datePickerDialog = new DatePickerDialog(this, new DatePickerDialog.OnDateSetListener() {
            public void onDateSet(DatePicker view, int year, int monthOfYear, int dayOfMonth) {
                Calendar newDate = Calendar.getInstance();
                newDate.set(year, monthOfYear, dayOfMonth);
                mLicencedSince = dateFormatter.format(newDate.getTime());
                textViewMemberSince.setText(utilObj.getMyDateFormat(mLicencedSince,
                        Constants.DateFormat.YYYYMMDDTHHMMSS, Constants.DateFormat.MMDDYYYY));
            }
        }, year, month, day);
        datePickerDialog.getDatePicker().setMaxDate(new Date().getTime());
    }
}
