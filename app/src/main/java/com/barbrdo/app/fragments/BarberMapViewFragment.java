package com.barbrdo.app.fragments;


import android.Manifest;
import android.content.pm.PackageManager;
import android.os.Bundle;
import android.os.Handler;
import android.support.v4.app.FragmentTransaction;
import android.support.v4.content.ContextCompat;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.View;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.barbrdo.app.R;
import com.barbrdo.app.activities.RequestBarberActivity;
import com.barbrdo.app.adapters.BarberAdapter;
import com.barbrdo.app.dataobject.CustomerBarbers;
import com.barbrdo.app.interfaces.OnItemClickListener;
import com.barbrdo.app.interfaces.ServiceRedirection;
import com.barbrdo.app.managers.CustomerManager;
import com.barbrdo.app.utils.Constants;
import com.google.android.gms.maps.CameraUpdate;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.BitmapDescriptorFactory;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.LatLngBounds;
import com.google.android.gms.maps.model.Marker;
import com.google.android.gms.maps.model.MarkerOptions;

import java.text.DecimalFormat;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;


public class BarberMapViewFragment extends BaseFragment implements ServiceRedirection, OnMapReadyCallback, OnItemClickListener {
    private CustomerManager customerManagerObj;
    private LinearLayout linearLayoutMap;
    private RecyclerView recyclerViewBarbers;
    private ImageView imageViewProfilePic;
    private ImageView imageViewFavourite;
    private TextView textViewUsername;
    private TextView textViewLocation;
    private TextView textViewRatings;
    private TextView textViewRequest;
    private TextView textViewServices;
    private GoogleMap mGoogleMap;
    private SupportMapFragment supportMapFragment;
    private List<Marker> listMarkers;
    private Marker previousMarker;
    DecimalFormat decimalFormat;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState, R.layout.fragment_barber_map_view);
    }

    @Override
    protected void initView() {
        recyclerViewBarbers = getView(R.id.rv_barbers);
        imageViewProfilePic = getView(R.id.iv_profile_picture);
        imageViewFavourite = getView(R.id.iv_favourite);
        textViewUsername = getView(R.id.tv_username);
        textViewLocation = getView(R.id.tv_location);
        textViewRatings = getView(R.id.tv_ratings);
        textViewRequest = getView(R.id.tv_request);
        textViewServices = getView(R.id.tv_services);

        customerManagerObj = new CustomerManager(getBaseActivity(), this);

        linearLayoutMap = getView(R.id.map_layout);
        if (supportMapFragment == null) {
            supportMapFragment = SupportMapFragment.newInstance();
            supportMapFragment.getMapAsync(this);
        }
        FragmentTransaction mTransaction = getChildFragmentManager().beginTransaction();
        mTransaction.replace(R.id.map_layout, supportMapFragment).commit();

        decimalFormat = new DecimalFormat();
        decimalFormat.setDecimalSeparatorAlwaysShown(false);
    }

    @Override
    protected void bindControls() {
        LinearLayoutManager layoutManager = new LinearLayoutManager(getActivity());
        layoutManager.setReverseLayout(true);
        layoutManager.setOrientation(LinearLayoutManager.VERTICAL);
        recyclerViewBarbers.setLayoutManager(layoutManager);
    }

    private void getBarbers() {
        getBaseActivity().utilObj.startLoader(getBaseActivity());
        customerManagerObj.getCustomerBarbers();
    }

    @Override
    public void onSuccessRedirection(int taskID) {
        getBaseActivity().utilObj.stopLoader();
        switch (taskID) {
            case Constants.TaskID.CUSTOMER_BARBERS:
                showBarbersOnMap(true);
                break;
        }
    }

    @Override
    public void onFailureRedirection(String errorMessage) {
        getBaseActivity().utilObj.stopLoader();
        getBaseActivity().showSnackBar(errorMessage);
    }

    @Override
    public void onMapReady(GoogleMap googleMap) {
        mGoogleMap = googleMap;

        if (ContextCompat.checkSelfPermission(getBaseActivity(), Manifest.permission.ACCESS_FINE_LOCATION)
                == PackageManager.PERMISSION_GRANTED) {
            mGoogleMap.setMyLocationEnabled(true);
        }

        getBarbers();

        mGoogleMap.setOnMapClickListener(new GoogleMap.OnMapClickListener() {
            @Override
            public void onMapClick(LatLng point) {
                recyclerViewBarbers.setVisibility(View.INVISIBLE);
            }
        });

        mGoogleMap.setOnMarkerClickListener(
                new GoogleMap.OnMarkerClickListener() {
                    @Override
                    public boolean onMarkerClick(Marker marker) {
                        int zoom = (int) mGoogleMap.getCameraPosition().zoom;
                        CameraUpdate cu = CameraUpdateFactory.newLatLngZoom(new LatLng(marker.getPosition().latitude + (double) 90 / Math.pow(2, zoom), marker.getPosition().longitude), zoom);
                        mGoogleMap.animateCamera(cu);
                        recyclerViewBarbers.setVisibility(View.VISIBLE);
                        final CustomerBarbers.Datum barberObj = (CustomerBarbers.Datum) marker.getTag();
                        List<CustomerBarbers.Datum> datumList = new ArrayList<>();
                        for (int i = 0; i < getBaseActivity().appInstance.customerBarbers.data.size(); i++) {
                            if (getBaseActivity().appInstance.customerBarbers.data.get(i).isOnline) {
                                float lat1 = getBaseActivity().appInstance.customerBarbers.data.get(i).barberShops.latLong.get(1);
                                float lat2 = barberObj.barberShops.latLong.get(1);
                                float lon1 = getBaseActivity().appInstance.customerBarbers.data.get(i).barberShops.latLong.get(0);
                                float lon2 = barberObj.barberShops.latLong.get(0);
                                if (lat1 == lat2 && lon1 == lon2) {
                                    datumList.add(getBaseActivity().appInstance.customerBarbers.data.get(i));
                                }
                            }
                        }

                        Collections.sort(datumList, new Comparator<CustomerBarbers.Datum>() {
                            public int compare(CustomerBarbers.Datum obj1, CustomerBarbers.Datum obj2) {
                                return obj1.isFavourite.compareTo(obj2.isFavourite);
                            }
                        });
                        BarberAdapter barberAdapter = new BarberAdapter(getBaseActivity());
                        barberAdapter.setOnItemClickListener(BarberMapViewFragment.this);
                        barberAdapter.setList(datumList);
                        recyclerViewBarbers.setAdapter(barberAdapter);

                        if (!barberObj.isFavourite)
                            marker.setIcon(BitmapDescriptorFactory.fromResource(R.drawable.shop_location_active));
                        else
                            marker.setIcon(BitmapDescriptorFactory.fromResource(R.drawable.glow_star));
                        previousMarker = marker;
                        return true;
                    }
                }
        );
    }

    @Override
    public void onItemClick(Object item, int position) {
        CustomerBarbers.Datum datum = (CustomerBarbers.Datum) item;
        Bundle bundle = new Bundle();
        bundle.putSerializable(Constants.BundleKeyTag.SERIALIZED_DATA, datum);
        getBaseActivity().startActivity(RequestBarberActivity.class, bundle);
    }

    public void hideBarbersOnMap() {
        recyclerViewBarbers.setVisibility(View.INVISIBLE);
    }

    public void showBarbersOnMap(boolean animate) {
        listMarkers = new ArrayList<>();
        if (mGoogleMap != null) {
            mGoogleMap.clear();
            if (getBaseActivity().appInstance.customerBarbers != null) {
                LatLng latLng;
                if (getBaseActivity().appInstance.customerBarbers.data.size() > 0) {
                    Collections.sort(getBaseActivity().appInstance.customerBarbers.data, new Comparator<CustomerBarbers.Datum>() {
                        public int compare(CustomerBarbers.Datum obj1, CustomerBarbers.Datum obj2) {
                            return obj1.isFavourite.compareTo(obj2.isFavourite);
                        }
                    });

                    for (int i = 0; i < getBaseActivity().appInstance.customerBarbers.data.size(); i++) {
                        CustomerBarbers.Datum barberObj = getBaseActivity().appInstance.customerBarbers.data.get(i);
                        if (barberObj.isOnline) {
                            if (barberObj.barberShops.latLong.get(0) != 0 && barberObj.barberShops.latLong.get(1) != 0) {
                                latLng = new LatLng(barberObj.barberShops.latLong.get(1), barberObj.barberShops.latLong.get(0));
                                Marker marker;
                                if (barberObj.isFavourite)
                                    marker = mGoogleMap.addMarker(new MarkerOptions().position(latLng).title(barberObj.barberShops.name).icon(BitmapDescriptorFactory.
                                            fromResource(R.drawable.glow_star)));
                                else
                                    marker = mGoogleMap.addMarker(new MarkerOptions().position(latLng).title(barberObj.barberShops.name).icon(BitmapDescriptorFactory.
                                            fromResource(R.drawable.shop_location_active)));
                                marker.setTag(barberObj);
                                listMarkers.add(marker);
                            }
                        }
                    }
                }

                if (animate) {
                    Handler handler = new Handler();
                    handler.postDelayed(new Runnable() {
                        @Override
                        public void run() {
                            LatLng latLngMyLocation = new LatLng(Double.parseDouble(sessionManager.getDeviceLatitude()), Double.parseDouble(sessionManager.getDeviceLongitude()));
                            if (listMarkers.size() > 0) {
                                LatLngBounds.Builder builder = new LatLngBounds.Builder();
                                for (Marker marker : listMarkers) {
                                    builder.include(marker.getPosition());
                                    builder.include(latLngMyLocation);
                                }
                                LatLngBounds bounds = builder.build();
                                int width = getResources().getDisplayMetrics().widthPixels;
                                int height = getResources().getDisplayMetrics().heightPixels;
                                int padding = (int) (width * 0.12);
//                            CameraUpdate cu = CameraUpdateFactory.newLatLngBounds(bounds, width, height, padding);
                                CameraUpdate zoom = CameraUpdateFactory.zoomTo(12);
                                mGoogleMap.moveCamera(CameraUpdateFactory.newLatLng(latLngMyLocation));
                                mGoogleMap.animateCamera(zoom);
                            } else {
                                CameraUpdate zoom = CameraUpdateFactory.zoomTo(12);
                                mGoogleMap.moveCamera(CameraUpdateFactory.newLatLng(latLngMyLocation));
                                mGoogleMap.animateCamera(zoom);
                            }
                        }
                    }, 300);
                }
            }
        }
    }
}