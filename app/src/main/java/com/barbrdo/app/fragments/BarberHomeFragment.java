package com.barbrdo.app.fragments;


import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.os.Bundle;
import android.support.v4.content.ContextCompat;
import android.support.v4.content.LocalBroadcastManager;
import android.support.v4.widget.SwipeRefreshLayout;
import android.support.v7.widget.CardView;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.text.Editable;
import android.text.InputFilter;
import android.text.TextUtils;
import android.text.TextWatcher;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.AdapterView;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.Spinner;
import android.widget.TextView;

import com.barbrdo.app.R;
import com.barbrdo.app.activities.ContactCustomerActivity;
import com.barbrdo.app.activities.FinanceCenterActivity;
import com.barbrdo.app.activities.ManageShopsActivity;
import com.barbrdo.app.activities.NavigationDrawerActivity;
import com.barbrdo.app.adapters.ShopAdapter;
import com.barbrdo.app.adapters.ShopSpinnerAdapter;
import com.barbrdo.app.dataobject.BarberHome;
import com.barbrdo.app.dataobject.GoOnlineInput;
import com.barbrdo.app.dataobject.RequestCheckInInput;
import com.barbrdo.app.dataobject.SendInviteInput;
import com.barbrdo.app.dataobject.Service;
import com.barbrdo.app.interfaces.DialogActionCallback;
import com.barbrdo.app.interfaces.OnItemClickListener;
import com.barbrdo.app.interfaces.ServiceRedirection;
import com.barbrdo.app.managers.BarberManager;
import com.barbrdo.app.managers.CommonManager;
import com.barbrdo.app.utils.Constants;
import com.barbrdo.app.utils.DecimalDigitsInputFilter;
import com.google.gson.Gson;

import java.text.DecimalFormat;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;


public class BarberHomeFragment extends BaseFragment implements OnItemClickListener, View.OnClickListener, ServiceRedirection,
        SwipeRefreshLayout.OnRefreshListener {

    private SwipeRefreshLayout swipeRefreshLayout;
    private LinearLayout linearLayoutParent;
    private CardView cardViewNextInChair;
    private CardView cardViewFinanceCenter;
    private CardView cardViewShops;
    private ImageView imageViewProfilePicture;
    private TextView textViewUsername;
    private TextView textViewServices;
    private TextView textViewTime;
    private TextView textViewCheckIn;
    private TextView textViewSendMessage;
    private TextView textViewGoOnline;
    private TextView textViewGoOnlineNow;
    private TextView textViewCancel;
    private RelativeLayout relativeLayoutGoOnline;
    private LinearLayout linearLayoutServices;
    private RecyclerView recyclerViewShops;
    private Spinner spinnerShops;
    private TextView textViewTotalSale;
    private TextView textViewTotalCuts;
    private TextView textViewNoResults;
    private ImageView imageViewCustomer;
    private ImageView imageViewBarber;
    private TextView textViewCustomer;
    private TextView textViewBarber;
    private EditText editTextEmailPhone;
    private TextView textViewSendInvitation;
    private String mUserType = "customer";
    private BarberManager barberManagerObj;
    private CommonManager commonManagerObj;
    private ShopAdapter shopAdapter;
    private DecimalFormat decimalFormat;
    private String mShopId;
    public LocalBroadcastManager bManager;
    public static final String RECEIVE_CUSTOMER_MESSAGES = "com.barbrdo.app.customer";
    private BarberHome barberHome;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState, R.layout.fragment_barber_home);

        bManager = LocalBroadcastManager.getInstance(getBaseActivity());
        IntentFilter intentFilter = new IntentFilter();
        intentFilter.addAction(RECEIVE_CUSTOMER_MESSAGES);
        bManager.registerReceiver(bReceiver, intentFilter);
    }

    @Override
    protected void initView() {
        swipeRefreshLayout = getView(R.id.swipe_refresh_layout);
        linearLayoutParent = getView(R.id.ll_parent);
        cardViewNextInChair = getView(R.id.card_view_next_in_chair);
        cardViewFinanceCenter = getView(R.id.card_view_financial_center);
        cardViewShops = getView(R.id.card_view_shops);
        imageViewProfilePicture = getView(R.id.iv_profile_picture);
        textViewUsername = getView(R.id.tv_username);
        textViewServices = getView(R.id.tv_services);
        textViewTime = getView(R.id.tv_time);
        textViewCheckIn = getView(R.id.tv_check_in);
        textViewSendMessage = getView(R.id.tv_send_message);
        textViewGoOnline = getView(R.id.tv_go_online);
        textViewGoOnlineNow = getView(R.id.tv_go_online_now);
        textViewCancel = getView(R.id.tv_cancel);
        relativeLayoutGoOnline = getView(R.id.rl_go_online);
        recyclerViewShops = getView(R.id.rv_shops);
        linearLayoutServices = getView(R.id.ll_services);
        spinnerShops = getView(R.id.spinner_shop);
        textViewTotalSale = getView(R.id.tv_total_sale);
        textViewTotalCuts = getView(R.id.tv_total_cuts);
        imageViewCustomer = getView(R.id.iv_customer);
        imageViewBarber = getView(R.id.iv_barber);
        textViewCustomer = getView(R.id.tv_customer);
        textViewBarber = getView(R.id.tv_barber);
        editTextEmailPhone = getView(R.id.et_phone_email);
        textViewSendInvitation = getView(R.id.tv_send);

        barberManagerObj = new BarberManager(getBaseActivity(), this);
        commonManagerObj = new CommonManager(getBaseActivity(), this);
        decimalFormat = new DecimalFormat();
        decimalFormat.setDecimalSeparatorAlwaysShown(false);
    }

    @Override
    protected void bindControls() {
        swipeRefreshLayout.setOnRefreshListener(this);
        recyclerViewShops.setHasFixedSize(true);
        LinearLayoutManager layoutManager = new LinearLayoutManager(getActivity());
        layoutManager.setOrientation(LinearLayoutManager.VERTICAL);
        recyclerViewShops.setLayoutManager(layoutManager);
        shopAdapter = new ShopAdapter(getBaseActivity());
        shopAdapter.setOnItemClickListener(this);

        textViewSendInvitation.setBackgroundResource(R.color.button_dark_gray);
        textViewSendInvitation.setEnabled(false);
        getBaseActivity().utilObj.setSpannableTextView(textViewSendInvitation, R.drawable.send, getString(R.string.send_invitation));
        getBaseActivity().utilObj.setSpannableTextView(textViewGoOnlineNow, R.drawable.go_online, getString(R.string.go_online_now));
        getBaseActivity().utilObj.setSpannableTextView(textViewCancel, R.drawable.ic_deny_red_small, getString(R.string.cancel_));

        textViewGoOnline.setOnClickListener(this);
        textViewGoOnlineNow.setOnClickListener(this);
        imageViewCustomer.setOnClickListener(this);
        imageViewBarber.setOnClickListener(this);
        textViewCancel.setOnClickListener(this);
        textViewCheckIn.setOnClickListener(this);
        textViewSendInvitation.setOnClickListener(this);
        cardViewFinanceCenter.setOnClickListener(this);
        cardViewShops.setOnClickListener(this);
        recyclerViewShops.setOnClickListener(this);

        spinnerShops.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parentView, View selectedItemView, int position, long id) {
                BarberHome.AssociateShop associateShop = (BarberHome.AssociateShop) parentView.getItemAtPosition(position);
                mShopId = associateShop.shopId;
            }

            @Override
            public void onNothingSelected(AdapterView<?> parentView) {
            }
        });

        textViewSendMessage.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Bundle bundle = new Bundle();
                bundle.putSerializable(Constants.BundleKeyTag.SERIALIZED_DATA, barberHome.appointment);
                getBaseActivity().startActivity(ContactCustomerActivity.class, bundle);
            }
        });

        editTextEmailPhone.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {

            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                if (s.length() > 0) {
                    textViewSendInvitation.setBackgroundResource(R.color.button_blue);
                    textViewSendInvitation.setEnabled(true);
                } else {
                    textViewSendInvitation.setBackgroundResource(R.color.button_dark_gray);
                    textViewSendInvitation.setEnabled(false);
                }
            }

            @Override
            public void afterTextChanged(Editable s) {

            }
        });
    }

    @Override
    public void onResume() {
        super.onResume();
        getBaseActivity().utilObj.startLoader(getBaseActivity());
        getBarberHome();
    }

    @Override
    public void onItemClick(Object item, int position) {
        showManageShopScreen();
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.iv_customer:
                imageViewCustomer.setImageResource(R.drawable.customer_active_blue);
                imageViewBarber.setImageResource(R.drawable.barber);
                textViewCustomer.setTextColor(ContextCompat.getColor(getBaseActivity(), R.color.text_dark));
                textViewBarber.setTextColor(ContextCompat.getColor(getBaseActivity(), R.color.text_dark));
                mUserType = getString(R.string.customer_);
                ((TextView) getView(R.id.tv_message_content)).setText(R.string.invite_10_customers);
                break;

            case R.id.iv_barber:
                imageViewCustomer.setImageResource(R.drawable.customer);
                imageViewBarber.setImageResource(R.drawable.barber_active_blue);
                textViewCustomer.setTextColor(ContextCompat.getColor(getBaseActivity(), R.color.text_dark));
                textViewBarber.setTextColor(ContextCompat.getColor(getBaseActivity(), R.color.text_dark));
                mUserType = getString(R.string.barber_);
                ((TextView) getView(R.id.tv_message_content)).setText(R.string.invite_barbers_to_download_the_barbrdo_app_and_when_they_book_their_first_appointment_you_receive_a_5_amazon_gift_card_as_a_token_of_our_appreciation);
                break;

            case R.id.tv_go_online:
                if (textViewGoOnline.getText().toString().equalsIgnoreCase(getString(R.string.go_online))) {
                    if (relativeLayoutGoOnline.getVisibility() == View.GONE) {
                        relativeLayoutGoOnline.setVisibility(View.VISIBLE);
                        textViewGoOnline.setBackgroundResource(R.drawable.rounded_dark_gray);
                    } else {
                        relativeLayoutGoOnline.setVisibility(View.GONE);
                        textViewGoOnline.setBackgroundResource(R.drawable.rounded_blue);
                        getBaseActivity().utilObj.hideVirtualKeyboard(getBaseActivity());
                    }
                } else {
                    goOffline();
                }
                break;

            case R.id.tv_cancel:
                relativeLayoutGoOnline.setVisibility(View.GONE);
                textViewGoOnline.setBackgroundResource(R.drawable.rounded_blue);
                getBaseActivity().utilObj.hideVirtualKeyboard(getBaseActivity());
                break;

            case R.id.tv_go_online_now:
                goOnline();
                break;

            case R.id.tv_check_in:
                checkIn();
                break;

            case R.id.tv_send:
                sendInvitation();
                break;

            case R.id.card_view_financial_center:
                getBaseActivity().startActivity(FinanceCenterActivity.class);
                break;

            case R.id.rv_shops:
                showManageShopScreen();
                break;

            case R.id.card_view_shops:
                showManageShopScreen();
                break;
        }
    }

    private void showManageShopScreen() {
        getBaseActivity().startActivity(ManageShopsActivity.class);
    }

    private void sendInvitation() {
        String input = editTextEmailPhone.getText().toString().trim();
        if (!TextUtils.isEmpty(input)) {
            if (getBaseActivity().utilObj.checkEmail(input)) {
                invite(true, input);
            } else if (isValidMobile(input)) {
                invite(false, input);
            } else {
                getBaseActivity().utilObj.showError("Invalid input", editTextEmailPhone);
            }
        }
    }

    private void invite(boolean isEmailInvite, String input) {
        getBaseActivity().utilObj.startLoader(getBaseActivity());
        SendInviteInput sendInviteInput = new SendInviteInput();
        if (isEmailInvite) {
            sendInviteInput.inviteAs = mUserType;
            sendInviteInput.refereeEmail = input;
        } else {
            sendInviteInput.inviteAs = mUserType;
            sendInviteInput.refereePhoneNumber = input;
        }
        commonManagerObj.sendInvite(sendInviteInput);
    }

    private boolean isValidMobile(String phone) {
        return android.util.Patterns.PHONE.matcher(phone).matches();
    }

    public void getBarberHome() {
        barberManagerObj.barberHome();
    }

    private void setData() {
        barberHome = getBaseActivity().appInstance.barberHome;

        if (barberHome.associateShops.size() > 0) {
            Collections.sort(barberHome.associateShops);
            shopAdapter.setList(barberHome.associateShops);
            if (barberHome.onlineWithShop != null)
                shopAdapter.setOnlineShopId(barberHome.onlineWithShop.id, barberHome.isOnline);
            recyclerViewShops.setAdapter(shopAdapter);
            getView(R.id.tv_no_results).setVisibility(View.GONE);
            recyclerViewShops.setVisibility(View.VISIBLE);
        } else {
            getView(R.id.tv_no_results).setVisibility(View.VISIBLE);
            recyclerViewShops.setVisibility(View.GONE);
        }

        textViewTotalSale.setText("$" + String.format("%.2f", barberHome.revenue.revenue));
        textViewTotalCuts.setText("" + barberHome.revenue.totalCuts);

        ShopSpinnerAdapter shopSpinnerAdapter = new ShopSpinnerAdapter(getBaseActivity(), 0, 0, barberHome.associateShops);
        spinnerShops.setAdapter(shopSpinnerAdapter);

        linearLayoutServices.removeAllViews();
        for (final Service service : barberHome.services) {
            LayoutInflater inflater = (LayoutInflater) getBaseActivity().getSystemService(Context.LAYOUT_INFLATER_SERVICE);
            View myView = inflater.inflate(R.layout.item_add_service, null);
            TextView textViewServiceName = (TextView) myView.findViewById(R.id.tv_title);
            TextView textViewOptional = (TextView) myView.findViewById(R.id.tv_optional);
            EditText editTextPrice = (EditText) myView.findViewById(R.id.et_price);
            editTextPrice.setFilters(new InputFilter[]{new DecimalDigitsInputFilter(3, 2)});
            textViewServiceName.setText(service.name);

            if (service.isOptional)
                textViewOptional.setVisibility(View.VISIBLE);
            else
                textViewOptional.setVisibility(View.GONE);

            editTextPrice.addTextChangedListener(new TextWatcher() {
                @Override
                public void beforeTextChanged(CharSequence s, int start, int count, int after) {

                }

                @Override
                public void onTextChanged(CharSequence s, int start, int before, int count) {
                    if (!TextUtils.isEmpty(s.toString())) {
                        try {
                            service.price = Float.parseFloat(s.toString());
                        } catch (NumberFormatException ex) {
                            service.price = 0;
                        }
                    } else {
                        service.price = 0;
                    }

                    boolean isValid = false;
                    for (Service service : getBaseActivity().appInstance.barberHome.services) {
                        if (!service.isOptional) {
                            if (service.price > 0) {
                                isValid = true;
                            } else {
                                isValid = false;
                            }
                        }
                    }

                    if (isValid && !TextUtils.isEmpty(mShopId))
                        textViewGoOnlineNow.setBackgroundResource(R.color.button_blue);
                    else
                        textViewGoOnlineNow.setBackgroundResource(R.color.button_dark_gray);
                }

                @Override
                public void afterTextChanged(Editable s) {

                }
            });
            linearLayoutServices.addView(myView);
        }

        if (barberHome.isOnline) {
            linearLayoutParent.setBackgroundResource(R.drawable.green_bg);
            textViewGoOnline.setText(R.string.go_offline);
            textViewGoOnline.setBackgroundResource(R.drawable.rounded_blue);
            relativeLayoutGoOnline.setVisibility(View.GONE);
        } else {
            linearLayoutParent.setBackgroundResource(R.drawable.red_bg);
            textViewGoOnline.setText(R.string.go_online);
            textViewGoOnline.setBackgroundResource(R.drawable.rounded_blue);
            relativeLayoutGoOnline.setVisibility(View.GONE);
        }

        if (barberHome.appointment != null) {
            if (barberHome.appointment.customerInfo != null) {
                cardViewNextInChair.setVisibility(View.VISIBLE);
                textViewUsername.setText(barberHome.appointment.customerInfo.get(0).firstName);
                textViewTime.setText("Coming in @ " + getBaseActivity().utilObj.getMyDateFormat(barberHome.appointment.appointmentDate,
                        Constants.DateFormat.YYYYMMDDTHHMMSS, Constants.DateFormat.HHMMA).toUpperCase());
                if (barberHome.appointment.services.size() > 0) {
                    StringBuilder stringBuilder = new StringBuilder();
                    for (Service barberService : barberHome.appointment.services) {
                        stringBuilder.append(barberService.name + ", ");
                    }
                    String services = stringBuilder.toString();
                    if (services.endsWith(", ")) {
                        services = services.substring(0, services.length() - 2);
                    }
                    textViewServices.setText(services);
                }
                if (!TextUtils.isEmpty(barberHome.appointment.customerInfo.get(0).picture)) {
                    imageLoader.displayImage(sessionManager.getImageBaseUrl() + barberHome.appointment.customerInfo.get(0).picture, imageViewProfilePicture, options);
                } else {
                    imageViewProfilePicture.setImageResource(R.drawable.placeholder_user);
                }
            } else {
                cardViewNextInChair.setVisibility(View.GONE);
            }
        } else {
            cardViewNextInChair.setVisibility(View.GONE);
        }
    }

    @Override
    public void onSuccessRedirection(int taskID) {
        swipeRefreshLayout.setRefreshing(false);
        getBaseActivity().utilObj.stopLoader();
        switch (taskID) {
            case Constants.TaskID.BARBER_HOME:
                if (getBaseActivity().appInstance.barberHome != null) {
                    setData();
                }
                break;

            case Constants.TaskID.GO_ONLINE:
                linearLayoutParent.setBackgroundResource(R.drawable.green_bg);
                textViewGoOnline.setText(R.string.go_offline);
                textViewGoOnline.setBackgroundResource(R.drawable.rounded_blue);
                relativeLayoutGoOnline.setVisibility(View.GONE);
                shopAdapter.setOnlineShopId(mShopId, true);
                shopAdapter.notifyDataSetChanged();
                break;

            case Constants.TaskID.GO_OFFLINE:
                linearLayoutParent.setBackgroundResource(R.drawable.red_bg);
                textViewGoOnline.setText(R.string.go_online);
                textViewGoOnline.setBackgroundResource(R.drawable.rounded_blue);
                relativeLayoutGoOnline.setVisibility(View.GONE);
                shopAdapter.setOnlineShopId("", false);
                shopAdapter.notifyDataSetChanged();
                break;

            case Constants.TaskID.BARBER_CHECK_IN:
                getBaseActivity().utilObj.showToast(getBaseActivity(), getBaseActivity().appInstance.message, 1);
                linearLayoutParent.setBackgroundResource(R.drawable.green_bg);
                textViewGoOnline.setText(R.string.go_offline);
                textViewGoOnline.setBackgroundResource(R.drawable.rounded_blue);
                relativeLayoutGoOnline.setVisibility(View.GONE);
                cardViewNextInChair.setVisibility(View.GONE);
                getBarberHome();
                break;

            case Constants.TaskID.INVITE_TO_APP:
                getBaseActivity().utilObj.showToast(getBaseActivity(), "Invite sent successfully.", 1);
                editTextEmailPhone.setText("");
                textViewSendInvitation.setEnabled(false);
                textViewSendInvitation.setBackgroundResource(R.color.button_dark_gray);
                break;
        }
    }

    @Override
    public void onFailureRedirection(String errorMessage) {
        swipeRefreshLayout.setRefreshing(false);
        getBaseActivity().utilObj.stopLoader();
        getBaseActivity().showSnackBar(errorMessage);
    }

    private void goOnline() {
        boolean isValid = false;
        List<Service> listService = new ArrayList<>();
        for (Service service : getBaseActivity().appInstance.barberHome.services) {
            if (!service.isOptional) {
                if (service.price > 0) {
                    isValid = true;
                } else {
                    isValid = false;
                }
            }
            if (service.price > 0) {
                listService.add(new Service(service.id, service.name, service.price));
            }
        }

        if (isValid) {
            getBaseActivity().utilObj.startLoader(getBaseActivity());
            GoOnlineInput goOnlineInput = new GoOnlineInput();
            goOnlineInput.shopId = mShopId;
            goOnlineInput.listServices = listService;

            Gson gson = new Gson();
            String json = gson.toJson(goOnlineInput);
            barberManagerObj.goOnline(goOnlineInput);
        } else {
            getBaseActivity().utilObj.showToast(getBaseActivity(), "Price for a haircut is required to go online.", 1);
        }
    }

    private void goOffline() {
        getBaseActivity().utilObj.startLoader(getBaseActivity());
        barberManagerObj.goOffline();
    }

    private void checkIn() {
        getBaseActivity().utilObj.startLoader(getBaseActivity());
        RequestCheckInInput requestCheckInInput = new RequestCheckInInput();
        requestCheckInInput.requestCheckIn = getBaseActivity().utilObj.getCurrentDateToString(Constants.DateFormat.YYYYMMDDTHHMMSS);

        barberManagerObj.barberCheckIn(getBaseActivity().appInstance.barberHome.appointment.id, requestCheckInInput);
    }

    private BroadcastReceiver bReceiver = new BroadcastReceiver() {
        @Override
        public void onReceive(Context context, Intent intent) {
            if (intent.getAction().equals(RECEIVE_CUSTOMER_MESSAGES)) {
                String message = intent.getStringExtra(Constants.BundleKeyTag.MESSAGE);
                if (message.equalsIgnoreCase("customer_cancel_appointment")) {
                    getBaseActivity().runOnUiThread(new Runnable() {
                        @Override
                        public void run() {
                            getBaseActivity().runOnUiThread(new Runnable() {
                                @Override
                                public void run() {
                                    cardViewNextInChair.setVisibility(View.GONE);
                                }
                            });
                        }
                    });
                }
            }
        }
    };

    @Override
    public void onRefresh() {
        swipeRefreshLayout.setRefreshing(true);
        getBarberHome();
    }
}
