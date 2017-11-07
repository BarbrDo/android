package com.barbrdo.app.activities;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.net.Uri;
import android.os.Bundle;
import android.provider.MediaStore;
import android.support.v4.view.ViewPager;
import android.text.TextUtils;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;

import com.barbrdo.app.R;
import com.barbrdo.app.adapters.FullScreenImageAdapter;
import com.barbrdo.app.dataobject.Gallery;
import com.barbrdo.app.interfaces.DialogActionCallback;
import com.barbrdo.app.interfaces.ServiceRedirection;
import com.barbrdo.app.managers.BarberManager;
import com.barbrdo.app.managers.CustomerManager;
import com.barbrdo.app.utils.Constants;
import com.nostra13.universalimageloader.core.assist.FailReason;
import com.nostra13.universalimageloader.core.listener.SimpleImageLoadingListener;

import java.io.FileNotFoundException;
import java.util.ArrayList;
import java.util.List;

/**
 * Created by sahilsa on 28/2/17.
 */
public class ImageViewActivity extends AppActivity implements ServiceRedirection {

    private Context mContext;
    private ViewPager viewPager;
    private FullScreenImageAdapter fullScreenImageAdapter;
    private CustomerManager customerManagerObj;
    private BarberManager barberManagerObj;
    private MenuItem menuItemDelete;
    private MenuItem menuItemShare;
    private List<Gallery> listGallery;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_fullscreen_view);
        mContext = ImageViewActivity.this;

        Intent intent = getIntent();
        Bundle args = intent.getBundleExtra(Constants.BundleKeyTag.BUNDLE);
        listGallery = (ArrayList<Gallery>) args.getSerializable(Constants.BundleKeyTag.SERIALIZED_DATA);

        initData();
        bindControls();
    }

    @Override
    void initData() {
        setUpToolBar("");

        viewPager = getView(R.id.view_pager);
        customerManagerObj = new CustomerManager(this, this);
        barberManagerObj = new BarberManager(this, this);
    }

    @Override
    void bindControls() {
        fullScreenImageAdapter = new FullScreenImageAdapter((Activity) mContext, listGallery);
        viewPager.setAdapter(fullScreenImageAdapter);
        viewPager.setCurrentItem(getIntent().getIntExtra(Constants.BundleKeyTag.PAGE, 0), true);
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        MenuInflater menuInflater = getMenuInflater();
        menuInflater.inflate(R.menu.menu_image, menu);
        menuItemShare = menu.getItem(0);
        menuItemDelete = menu.getItem(1);
        menuItemDelete.setVisible(true);
        menuItemShare.setVisible(true);
        return super.onCreateOptionsMenu(menu);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case android.R.id.home:
                finish();
                break;

            case R.id.action_delete:
                deleteImage();
                break;

            case R.id.action_share:
                try {
                    shareImage();
                } catch (FileNotFoundException e) {
                    e.printStackTrace();
                }
                break;
        }
        return super.onOptionsItemSelected(item);
    }

    private void shareImage() throws FileNotFoundException {
        if (!TextUtils.isEmpty(listGallery.get(viewPager.getCurrentItem()).name)) {
            utilObj.startLoader(mContext);
            imageLoader.loadImage(sessionManager.getImageBaseUrl() + listGallery.get(viewPager.getCurrentItem()).name, new SimpleImageLoadingListener() {
                @Override
                public void onLoadingComplete(String imageUri, View view, Bitmap loadedImage) {
                    utilObj.stopLoader();
                    String pathofBmp = MediaStore.Images.Media.insertImage(getContentResolver(), loadedImage, "", null);
                    Uri bmpUri = Uri.parse(pathofBmp);
                    final Intent emailIntent1 = new Intent(Intent.ACTION_SEND);
                    emailIntent1.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                    emailIntent1.putExtra(Intent.EXTRA_STREAM, bmpUri);
                    emailIntent1.setType("image/png");
                    startActivity(emailIntent1);
                }

                @Override
                public void onLoadingFailed(String imageUri, View view, FailReason failReason) {
                    super.onLoadingFailed(imageUri, view, failReason);
                    showSnackBar(failReason.getCause().toString());
                    utilObj.stopLoader();
                }
            });
        }
    }

    private void deleteImage() {
        utilObj.showAlertDialog(this, R.string.delete_image, R.string.are_you_sure_you_want_to_delete_this_image, R.string.ok, R.string.cancel, new DialogActionCallback() {
            @Override
            public void doOnPositive() {
                utilObj.startLoader(mContext);
                if (appInstance.userDetail.user.userType.equalsIgnoreCase(getString(R.string.customer_))) {
                    customerManagerObj.deleteImage(listGallery.get(viewPager.getCurrentItem()).id);
                } else if (appInstance.userDetail.user.userType.equalsIgnoreCase(getString(R.string.barber_))) {
                    barberManagerObj.deleteImage(listGallery.get(viewPager.getCurrentItem()).id);
                }
            }

            @Override
            public void doOnNegative() {
            }
        });
    }

    @Override
    public void onSuccessRedirection(int taskID) {
        utilObj.stopLoader();
        switch (taskID) {
            case Constants.TaskID.CUSTOMER_GALLERY:
                appInstance.userDetail.user = appInstance.galleryUpdate.user;
                sessionManager.saveUser(appInstance.userDetail);
                Intent intentCustomer = getIntent();
                setResult(RESULT_OK, intentCustomer);
                finish();
                break;

            case Constants.TaskID.BARBER_GALLERY:
                appInstance.userDetail.user = appInstance.galleryUpdate.user;
                sessionManager.saveUser(appInstance.userDetail);
                Intent intentBarber = getIntent();
                setResult(RESULT_OK, intentBarber);
                finish();
                break;
        }
    }

    @Override
    public void onFailureRedirection(String errorMessage) {
        utilObj.stopLoader();
        showSnackBar(errorMessage);
    }
}
