package com.barbrdo.app.fragments;

import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v7.widget.GridLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.View;
import android.widget.TextView;

import com.barbrdo.app.R;
import com.barbrdo.app.activities.ImageViewActivity;
import com.barbrdo.app.adapters.GalleryAdapter;
import com.barbrdo.app.dataobject.BarberId;
import com.barbrdo.app.dataobject.Gallery;
import com.barbrdo.app.interfaces.OnItemClickListener;
import com.barbrdo.app.utils.Constants;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

public class GalleryProfileFragment extends BaseFragment implements OnItemClickListener {

    private GalleryAdapter galleryAdapter;
    private RecyclerView recyclerView;
    private List<Gallery> listGallery;
    private TextView textViewRequestAppointment;

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState, R.layout.fragment_recycler_view);
    }

    @Override
    protected void initView() {
        recyclerView = getView(R.id.rv_list);
        listGallery = new ArrayList<>();
        textViewRequestAppointment = getView(R.id.tv_request_appointment);
    }

    @Override
    protected void bindControls() {

        recyclerView.setHasFixedSize(true);
        RecyclerView.LayoutManager layoutManager = new GridLayoutManager(getActivity().getApplicationContext(), 3);
        recyclerView.setLayoutManager(layoutManager);
        galleryAdapter = new GalleryAdapter(getBaseActivity());
        galleryAdapter.setOnItemClickListener(this);
        listGallery.addAll(getBaseActivity().appInstance.userProfile.user.gallery);
        galleryAdapter.setList(listGallery);
        recyclerView.setAdapter(galleryAdapter);

        galleryAdapter.notifyDataSetChanged();

        if (getBaseActivity().appInstance.userDetail.user.userType.equalsIgnoreCase(getString(R.string.barber_)) ||
                getBaseActivity().appInstance.userDetail.user.userType.equalsIgnoreCase(getString(R.string.shop_)))
            textViewRequestAppointment.setVisibility(View.GONE);

        textViewRequestAppointment.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                BarberId barberId = new BarberId();
                barberId.firstName = getBaseActivity().appInstance.userProfile.user.firstName;
                barberId.lastName = getBaseActivity().appInstance.userProfile.user.lastName;
                barberId.id = getBaseActivity().appInstance.userProfile.user.id;
                barberId.shopId = "591be658b902f60fcc14a9d6";

                Bundle bundle = new Bundle();
                bundle.putSerializable(Constants.BundleKeyTag.SERIALIZED_DATA, barberId);
            }
        });
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
}
