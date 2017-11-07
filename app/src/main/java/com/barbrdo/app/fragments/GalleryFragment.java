package com.barbrdo.app.fragments;

import android.Manifest;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.design.widget.FloatingActionButton;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AlertDialog;
import android.support.v7.widget.GridLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.View;
import android.widget.ListAdapter;
import android.widget.TextView;

import com.barbrdo.app.R;
import com.barbrdo.app.activities.ImageViewActivity;
import com.barbrdo.app.adapters.ArrayAdapterWithIcon;
import com.barbrdo.app.adapters.GalleryAdapter;
import com.barbrdo.app.dataobject.Gallery;
import com.barbrdo.app.interfaces.OnItemClickListener;
import com.barbrdo.app.interfaces.OnLongItemClickListener;
import com.barbrdo.app.interfaces.ServiceRedirection;
import com.barbrdo.app.managers.BarberManager;
import com.barbrdo.app.managers.CustomerManager;
import com.barbrdo.app.utils.Constants;

import java.io.File;
import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

import pl.aprilapps.easyphotopicker.DefaultCallback;
import pl.aprilapps.easyphotopicker.EasyImage;
import pl.tajchert.nammu.Nammu;
import pl.tajchert.nammu.PermissionCallback;

public class GalleryFragment extends BaseFragment implements OnItemClickListener, OnLongItemClickListener, ServiceRedirection {

    private FloatingActionButton floatingActionButtonAddImage;
    private GalleryAdapter galleryAdapter;
    private RecyclerView recyclerView;
    private TextView textViewNoResults;
    private List<Gallery> listGallery;
    private CustomerManager customerManagerObj;
    private BarberManager barberManagerObj;

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState, R.layout.fragment_gallery);
        Nammu.init(getBaseActivity());

        int permissionCheck = ContextCompat.checkSelfPermission(getBaseActivity(), Manifest.permission.WRITE_EXTERNAL_STORAGE);
        if (permissionCheck != PackageManager.PERMISSION_GRANTED) {
            Nammu.askForPermission(getBaseActivity(), Manifest.permission.WRITE_EXTERNAL_STORAGE, new PermissionCallback() {
                @Override
                public void permissionGranted() {
                    //Nothing, this sample saves to Public gallery so it needs permission
                }

                @Override
                public void permissionRefused() {
                    getBaseActivity().finish();
                }
            });
        }

        EasyImage.configuration(getBaseActivity()).setImagesFolderName(getString(R.string.gallery_folder_name));
    }

    @Override
    protected void initView() {
        floatingActionButtonAddImage = getView(R.id.fab);
        recyclerView = getView(R.id.rv_grid);
        textViewNoResults = getView(R.id.tv_no_results);

        listGallery = new ArrayList<>();
        if (getBaseActivity().appInstance.userDetail != null) {
            listGallery.addAll(getBaseActivity().appInstance.userDetail.user.gallery);
        }

        customerManagerObj = new CustomerManager(getBaseActivity(), this);
        barberManagerObj = new BarberManager(getBaseActivity(), this);
    }

    @Override
    protected void bindControls() {
        floatingActionButtonAddImage.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                captureImage();
            }
        });
        recyclerView.setHasFixedSize(true);
        RecyclerView.LayoutManager layoutManager = new GridLayoutManager(getActivity().getApplicationContext(), 3);
        recyclerView.setLayoutManager(layoutManager);
        galleryAdapter = new GalleryAdapter(getBaseActivity());
        galleryAdapter.setOnItemClickListener(this);
        galleryAdapter.setOnLongItemClickListener(this);
        galleryAdapter.setList(listGallery);
        recyclerView.setAdapter(galleryAdapter);
        showNoResults();
    }

    private void captureImage() {
        final String[] items = new String[]{"Take Picture", "Choose from Gallery"};
        final Integer[] icons = new Integer[]{R.drawable.ic_camera, R.drawable.ic_gallery};
        ListAdapter adapter = new ArrayAdapterWithIcon(getActivity(), items, icons);

        new AlertDialog.Builder(getActivity()).setTitle(R.string.add_image)
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
        int permissionCheck = ContextCompat.checkSelfPermission(getBaseActivity(), Manifest.permission.WRITE_EXTERNAL_STORAGE);
        if (permissionCheck == PackageManager.PERMISSION_GRANTED) {
            EasyImage.openDocuments(this, 0);
        } else {
            Nammu.askForPermission(getBaseActivity(), Manifest.permission.WRITE_EXTERNAL_STORAGE, new PermissionCallback() {
                @Override
                public void permissionGranted() {
                    EasyImage.openDocuments(getBaseActivity(), 0);
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

        if (requestCode == Constants.RequestCodes.GALLERY) {
            listGallery = new ArrayList<>();
            if (getBaseActivity().appInstance.userDetail != null) {
                listGallery.addAll(getBaseActivity().appInstance.userDetail.user.gallery);
                galleryAdapter.setList(listGallery);
                galleryAdapter.notifyDataSetChanged();
                showNoResults();
            }
        } else {
            EasyImage.handleActivityResult(requestCode, resultCode, data, getBaseActivity(), new DefaultCallback() {
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
                        File photoFile = EasyImage.lastlyTakenButCanceledPhoto(getBaseActivity());
                        if (photoFile != null) photoFile.delete();
                    }
                }
            });
        }
    }

    private void onPhotosReturned(File returnedPhoto) {
        getBaseActivity().utilObj.startLoader(getBaseActivity());
        File fileDecoded = getBaseActivity().utilObj.saveBitmapToFile(returnedPhoto);
        if (getBaseActivity().appInstance.userDetail.user.userType.equalsIgnoreCase(getString(R.string.customer_)))
            customerManagerObj.updateGallery(fileDecoded);
        else
            barberManagerObj.updateBarberGallery(fileDecoded);
    }

    @Override
    public void onDestroy() {
        EasyImage.clearConfiguration(getBaseActivity());
        super.onDestroy();
    }

    @Override
    public void onItemClick(Object item, int position) {
        Gallery gallery = (Gallery) item;
        Intent intentView = new Intent(getBaseActivity(), ImageViewActivity.class);
        intentView.putExtra(Constants.BundleKeyTag.PAGE, position);
        Bundle args = new Bundle();
        args.putSerializable(Constants.BundleKeyTag.SERIALIZED_DATA, (Serializable) listGallery);
        intentView.putExtra(Constants.BundleKeyTag.BUNDLE, args);
        startActivityForResult(intentView, Constants.RequestCodes.GALLERY);
    }

    @Override
    public void onSuccessRedirection(int taskID) {
        getBaseActivity().utilObj.stopLoader();
        switch (taskID) {
            case Constants.TaskID.CUSTOMER_GALLERY:
                if (getBaseActivity().appInstance.galleryUpdate != null) {
                    getBaseActivity().appInstance.userDetail.user = getBaseActivity().appInstance.galleryUpdate.user;
                    sessionManager.saveUser(getBaseActivity().appInstance.userDetail);
                    listGallery = new ArrayList<>();
                    listGallery.addAll(getBaseActivity().appInstance.userDetail.user.gallery);
                    galleryAdapter.setList(listGallery);
                    galleryAdapter.notifyDataSetChanged();
                    showNoResults();
                }
                break;

            case Constants.TaskID.BARBER_GALLERY:
                if (getBaseActivity().appInstance.galleryUpdate != null) {
                    getBaseActivity().appInstance.userDetail.user.gallery = getBaseActivity().appInstance.galleryUpdate.user.gallery;
                    sessionManager.saveUser(getBaseActivity().appInstance.userDetail);
                    listGallery = new ArrayList<>();
                    listGallery.addAll(getBaseActivity().appInstance.userDetail.user.gallery);
                    galleryAdapter.setList(listGallery);
                    galleryAdapter.notifyDataSetChanged();
                    showNoResults();
                }
                break;
        }
    }

    @Override
    public void onFailureRedirection(String errorMessage) {
        getBaseActivity().utilObj.stopLoader();
        getBaseActivity().showSnackBar(errorMessage);
    }

    @Override
    public void onLongItemClick(Object item, int position) {

    }

    private void showNoResults() {
        if (listGallery.size() > 0) {
            textViewNoResults.setVisibility(View.GONE);
        } else {
            textViewNoResults.setVisibility(View.VISIBLE);
            textViewNoResults.setText(R.string.no_gallery_images_found);
        }
    }
}
