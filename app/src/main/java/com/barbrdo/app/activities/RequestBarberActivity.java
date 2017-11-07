package com.barbrdo.app.activities;

import android.content.Context;
import android.os.Bundle;
import android.support.v4.content.ContextCompat;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.text.TextUtils;
import android.util.DisplayMetrics;
import android.view.Gravity;
import android.view.MenuItem;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

import com.barbrdo.app.R;
import com.barbrdo.app.adapters.ServicesAdapter;
import com.barbrdo.app.customviews.TextViewRegular;
import com.barbrdo.app.dataobject.BarberSlots;
import com.barbrdo.app.dataobject.CustomerBarbers;
import com.barbrdo.app.dataobject.CustomerRequestInput;
import com.barbrdo.app.dataobject.Rating;
import com.barbrdo.app.dataobject.Service;
import com.barbrdo.app.interfaces.OnItemClickListener;
import com.barbrdo.app.interfaces.ServiceRedirection;
import com.barbrdo.app.managers.CustomerManager;
import com.barbrdo.app.utils.Constants;
import com.google.gson.Gson;
import com.wefika.flowlayout.FlowLayout;

import java.text.DecimalFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.List;

public class RequestBarberActivity extends AppActivity implements ServiceRedirection, View.OnClickListener, OnItemClickListener {

    private Context mContext;
    private ImageView imageViewBarberProfilePic;
    private TextView textViewUsername;
    private TextView textViewLocation;
    private TextView textViewShopAddress;
    private TextView textViewRatings;
    private TextView textViewTotal;
    private RecyclerView recyclerViewServices;
    private FlowLayout flowLayoutTimeSlots;
    private TextView textViewSubmit;
    private CustomerBarbers.Datum barberData;
    private CustomerManager customerManagerObj;
    private ServicesAdapter servicesAdapter;
    private float totalPrice;
    private List<Service> listServices;
    private boolean isTimeSelected;
    private DecimalFormat decimalFormat;
    private String mTime;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_request_barber);
        mContext = RequestBarberActivity.this;
        initData();
        bindControls();

        Bundle extras = getIntent().getExtras();
        if (extras != null) {
            barberData = (CustomerBarbers.Datum) extras.getSerializable(Constants.BundleKeyTag.SERIALIZED_DATA);
            setData();
        }
    }

    @Override
    void initData() {
        setUpToolBar("");
        imageViewBarberProfilePic = getView(R.id.iv_profile_picture);
        textViewUsername = getView(R.id.tv_username);
        textViewLocation = getView(R.id.tv_location);
        textViewShopAddress = getView(R.id.tv_shop_address);
        textViewRatings = getView(R.id.tv_ratings);
        textViewTotal = getView(R.id.tv_total);
        recyclerViewServices = getView(R.id.rv_services);
        flowLayoutTimeSlots = getView(R.id.fl_time);
        textViewSubmit = getView(R.id.tv_submit);

        customerManagerObj = new CustomerManager(this, this);
        decimalFormat = new DecimalFormat();
        decimalFormat.setDecimalSeparatorAlwaysShown(false);
    }

    @Override
    void bindControls() {
        utilObj.setSpannableTextView(textViewSubmit, R.drawable.send, getString(R.string.submit_request));
        textViewSubmit.setEnabled(false);

        recyclerViewServices.setHasFixedSize(true);
        LinearLayoutManager layoutManager = new LinearLayoutManager(mContext);
        layoutManager.setOrientation(LinearLayoutManager.VERTICAL);
        recyclerViewServices.setLayoutManager(layoutManager);

        servicesAdapter = new ServicesAdapter((AppActivity) mContext);
        servicesAdapter.setOnItemClickListener(this);
        recyclerViewServices.setAdapter(servicesAdapter);

        setTimeSlots(prepareTimeSlots());

        imageViewBarberProfilePic.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Bundle bundle = new Bundle();
                bundle.putString(Constants.BundleKeyTag.BARBER_ID, barberData.id);
                startActivity(BarberProfileActivity.class, bundle);
            }
        });

        getView(R.id.rl_main).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Bundle bundle = new Bundle();
                bundle.putString(Constants.BundleKeyTag.BARBER_ID, barberData.id);
                startActivity(BarberProfileActivity.class, bundle);
            }
        });
    }

    private void setData() {
        textViewUsername.setText(barberData.firstName + " " + barberData.lastName);
        textViewLocation.setText(barberData.barberShops.name + " (" + Math.round(barberData.distance) + " mi)");
        if (!TextUtils.isEmpty(barberData.picture)) {
            imageLoader.displayImage(sessionManager.getImageBaseUrl() + barberData.picture, imageViewBarberProfilePic, options);
        }

        textViewShopAddress.setText(barberData.barberShops.address + " " + barberData.barberShops.city + " " + barberData.barberShops.state + " " + barberData.barberShops.zip);

        if (barberData.ratings.size() > 0) {
            float avg = 0;
            for (Rating rating : barberData.ratings) {
                avg = avg + rating.score;
            }
            avg = avg / barberData.ratings.size();
            textViewRatings.setText(String.format("%.1f", avg));
        } else {
            textViewRatings.setText("0.0");
        }

        textViewTotal.setText("Total:    $-");
        servicesAdapter.setList(barberData.barberServices);
        servicesAdapter.notifyDataSetChanged();
    }

    private List<BarberSlots.TimeSlots> prepareTimeSlots() {
        final List<BarberSlots.TimeSlots> listSlots = new ArrayList<>();
        listSlots.add(new BarberSlots().new TimeSlots("10", false));
        listSlots.add(new BarberSlots().new TimeSlots("20", false));
        listSlots.add(new BarberSlots().new TimeSlots("30", false));
        listSlots.add(new BarberSlots().new TimeSlots("45", false));
        return listSlots;
    }

    private void setTimeSlots(final List<BarberSlots.TimeSlots> listSlots) {
        DisplayMetrics displayMetrics = new DisplayMetrics();
        getWindowManager().getDefaultDisplay().getMetrics(displayMetrics);
        int width = displayMetrics.widthPixels;

        flowLayoutTimeSlots.removeAllViews();
        for (final BarberSlots.TimeSlots slots : listSlots) {
            final TextViewRegular textViewSlot = new TextViewRegular(this);
            textViewSlot.setText(slots.time + " Minutes");
            textViewSlot.setBackgroundResource(R.drawable.textview_selector);
            textViewSlot.setGravity(Gravity.CENTER);
            textViewSlot.setTextSize(12);
            textViewSlot.setTextColor(ContextCompat.getColor(mContext, R.color.text_dark));
            textViewSlot.setTag(slots);

            if (!slots.isSelected) {
                textViewSlot.setSelected(false);
                textViewSlot.setTextColor(ContextCompat.getColor(mContext, R.color.text_dark));
            } else {
                textViewSlot.setSelected(true);
                textViewSlot.setTextColor(ContextCompat.getColor(mContext, R.color.white));
            }

            textViewSlot.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    BarberSlots.TimeSlots slot = (BarberSlots.TimeSlots) textViewSlot.getTag();
                    if (!textViewSlot.isSelected()) {
                        textViewSlot.setSelected(true);
                        textViewSlot.setTextColor(ContextCompat.getColor(mContext, R.color.white));
                        slot.isSelected = true;
                    } else {
                        textViewSlot.setSelected(false);
                        textViewSlot.setTextColor(ContextCompat.getColor(mContext, R.color.text_dark));
                        slot.isSelected = false;
                    }

                    for (BarberSlots.TimeSlots slots1 : listSlots) {
                        if (slots1.time.equalsIgnoreCase(slot.time)) {
                            slots1.isSelected = true;
                        } else {
                            slots1.isSelected = false;
                        }
                    }
                    setTimeSlots(listSlots);
                    mTime = slot.time;

                    isTimeSelected = true;
                    if (totalPrice > 0) {
                        textViewSubmit.setEnabled(true);
                        textViewSubmit.setBackgroundResource(R.color.button_blue);
                    }
                }
            });

            int padding5 = utilObj.dpToPx(5, mContext);
            textViewSlot.setPadding(padding5, padding5, padding5, padding5);

            FlowLayout.LayoutParams params = new FlowLayout.LayoutParams(
                    width / 4 - utilObj.dpToPx(18, mContext),
                    utilObj.dpToPx(35, mContext));
            params.setMargins(padding5, padding5, padding5, padding5);
            textViewSlot.setLayoutParams(params);
            flowLayoutTimeSlots.addView(textViewSlot);
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
            case Constants.TaskID.CUSTOMER_REQUEST:
                Bundle bundle = new Bundle();
                bundle.putSerializable(Constants.BundleKeyTag.SERIALIZED_DATA, appInstance.customerRequestDetails);
                bundle.putSerializable(Constants.BundleKeyTag.BARBER_DATA, barberData);
                bundle.putString(Constants.BundleKeyTag.SELECTED_TIME, mTime);
                startActivity(AwaitingBarberApprovalActivity.class, bundle);
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
        }
    }

    private void postRequest() {
        utilObj.startLoader(mContext);
        CustomerRequestInput customerRequestInput = new CustomerRequestInput();
        customerRequestInput.barberId = barberData.id;
        customerRequestInput.shopId = barberData.barberShops.id;
        customerRequestInput.services = listServices;
        customerRequestInput.totalPrice = decimalFormat.format(totalPrice);
        customerRequestInput.appointmentDate = utilObj.getCurrentDateToString(Constants.DateFormat.YYYYMMDDHHMM);
        customerRequestInput.reachIn = mTime;

        Gson gson = new Gson();
        String json = gson.toJson(customerRequestInput);

        customerManagerObj.postCustomerRequest(customerRequestInput);
    }

    private String getAppointmentDateTime(int minutes) {
        SimpleDateFormat df = new SimpleDateFormat(Constants.DateFormat.YYYYMMDDHHMM);
        Date d = null;
        try {
            d = df.parse(utilObj.getCurrentDateToString(Constants.DateFormat.YYYYMMDDHHMM));
        } catch (ParseException e) {
            e.printStackTrace();
        }
        Calendar cal = Calendar.getInstance();
        cal.setTime(d);
        cal.add(Calendar.MINUTE, minutes);
        return df.format(cal.getTime());
    }

    @Override
    public void onItemClick(Object item, int position) {
        listServices = new ArrayList<>();
        totalPrice = 0;
        for (Service data : barberData.barberServices) {
            if (data.isSelected) {
                totalPrice = totalPrice + data.price;
                listServices.add(new Service(data.serviceId, data.name, data.price));
            }
        }

        String price = String.format("%.2f", totalPrice);
        textViewTotal.setText("Total:    $" + price);

        if (totalPrice > 0 && isTimeSelected) {
            textViewSubmit.setBackgroundResource(R.color.button_blue);
            textViewSubmit.setEnabled(true);
        } else {
            textViewSubmit.setBackgroundResource(R.color.button_dark_gray);
            textViewSubmit.setEnabled(false);
        }
    }

    public void sendRequest(View view) {
        postRequest();
    }
}
