package com.barbrdo.app.activities;

import android.app.Activity;
import android.content.Context;
import android.graphics.Color;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.v4.content.ContextCompat;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.text.Editable;
import android.text.SpannableString;
import android.text.Spanned;
import android.text.TextPaint;
import android.text.TextUtils;
import android.text.TextWatcher;
import android.text.method.LinkMovementMethod;
import android.text.style.ClickableSpan;
import android.text.style.ForegroundColorSpan;
import android.text.style.StyleSpan;
import android.text.style.UnderlineSpan;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.AutoCompleteTextView;
import android.widget.EditText;
import android.widget.Spinner;
import android.widget.TextView;

import com.barbrdo.app.R;
import com.barbrdo.app.adapters.LocationAutoComplete;
import com.barbrdo.app.adapters.SearchShopsAdapter;
import com.barbrdo.app.adapters.StatesSpinnerAdapter;
import com.barbrdo.app.dataobject.AddShopInput;
import com.barbrdo.app.dataobject.AssociateShopInput;
import com.barbrdo.app.dataobject.PlaceDetails;
import com.barbrdo.app.dataobject.PlaceResponse;
import com.barbrdo.app.dataobject.Shop;
import com.barbrdo.app.dataobject.States;
import com.barbrdo.app.helpers.LocationLatLong;
import com.barbrdo.app.interfaces.OnItemClickListener;
import com.barbrdo.app.interfaces.ServiceRedirection;
import com.barbrdo.app.managers.BarberManager;
import com.barbrdo.app.managers.CommonManager;
import com.barbrdo.app.utils.Constants;

import java.util.ArrayList;
import java.util.List;

public class AddShopActivity extends AppActivity implements ServiceRedirection, OnItemClickListener {

    private Context mContext;
    private EditText editTextShopName;
    private AutoCompleteTextView autoCompleteTextViewStreet;
    private EditText editTextCity;
    private EditText editTextZip;
    private Spinner spinnerState;
    private TextView textViewSearch;
    private TextView textViewAddShop;
    private TextView textViewAddANewShop;
    private RecyclerView recyclerViewShops;
    private TextView textViewNoResults;
    private SearchShopsAdapter searchShopsAdapter;
    private StatesSpinnerAdapter statesSpinnerAdapter;
    private BarberManager barberManagerObj;
    private CommonManager commonManagerObj;
    private String shopName, street, city, state, zip;
    private Shop shop;
    private LocationAutoComplete locationAutoComplete;
    private double mLatitude, mLongitude;
    private List<States.Datum> listStates;
    private String mFormattedAddres;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_add_shop);
        mContext = AddShopActivity.this;
        initData();
        bindControls();
    }

    @Override
    void initData() {
        setUpToolBar("");
        editTextShopName = getView(R.id.et_shop_name);
        autoCompleteTextViewStreet = getView(R.id.act_street_address);
        editTextCity = getView(R.id.et_city);
        editTextZip = getView(R.id.et_zip_code);
        spinnerState = getView(R.id.spinner_state);
        textViewSearch = getView(R.id.tv_search);
        textViewAddShop = getView(R.id.tv_add_shop);
        recyclerViewShops = getView(R.id.rv_list);
        textViewNoResults = getView(R.id.tv_no_results);
        textViewAddANewShop = getView(R.id.tv_add_new_shop);

        barberManagerObj = new BarberManager(this, this);
        commonManagerObj = new CommonManager(this, this);
    }

    @Override
    void bindControls() {
        textViewAddShop.setBackgroundResource(R.color.button_dark_gray);
        textViewAddShop.setEnabled(false);
        utilObj.setSpannableTextView(textViewSearch, R.drawable.search_white, getString(R.string.search));

        LinearLayoutManager layoutManager = new LinearLayoutManager(mContext);
        layoutManager.setOrientation(LinearLayoutManager.VERTICAL);
        recyclerViewShops.setLayoutManager(layoutManager);

        getStates();

        spinnerState.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                States.Datum stateObj = (States.Datum) parent.getItemAtPosition(position);
                state = stateObj.abbreviation;
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {

            }
        });

        textViewAddShop.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                validateAddShop();
            }
        });

        createSpannableViews();

        locationAutoComplete = new LocationAutoComplete(mContext, R.layout.item_location);
        autoCompleteTextViewStreet.setThreshold(1);
        autoCompleteTextViewStreet.setAdapter(locationAutoComplete);
        autoCompleteTextViewStreet.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                PlaceResponse placeResponse = (PlaceResponse) locationAutoComplete.getItem(position);
//                autoCompleteTextViewStreet.setText(placeResponse.description);
                getLatLongFromPlaceId(placeResponse.place_id);
            }
        });

        autoCompleteTextViewStreet.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {

            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                if (s.toString().length() == 0) {
                    autoCompleteTextViewStreet.setAdapter(locationAutoComplete);
                }
            }

            @Override
            public void afterTextChanged(Editable s) {

            }
        });
    }

    private void getLatLongFromPlaceId(String placeId) {
        new AsyncTask<String, Void, PlaceDetails>() {
            @Override
            protected void onPreExecute() {
                super.onPreExecute();
                utilObj.startLoader(mContext);
            }

            @Override
            protected PlaceDetails doInBackground(String... strings) {
                LocationLatLong locationLatLong = new LocationLatLong();
                return locationLatLong.getLatLong(strings[0]);
            }

            @Override
            protected void onPostExecute(PlaceDetails placeDetails) {
                super.onPostExecute(placeDetails);
                utilObj.stopLoader();
                mLatitude = placeDetails.result.geometry.location.lat;
                mLongitude = placeDetails.result.geometry.location.lng;
                autoFillAddress(placeDetails);
            }
        }.execute(placeId);
    }

    private void validateAddShop() {
        String message = "";
        shopName = editTextShopName.getText().toString().trim();
        street = autoCompleteTextViewStreet.getText().toString().trim();
        city = editTextCity.getText().toString().trim();
        if (state.equalsIgnoreCase("Select state"))
            state = "";
        zip = editTextZip.getText().toString().trim();

        if (shopName.isEmpty()) {
            message = getString(R.string.please_enter_shop_name);
            utilObj.showError(message, editTextShopName);
        } else if (street.isEmpty()) {
            message = getString(R.string.please_enter_street_address);
            utilObj.showError(message, autoCompleteTextViewStreet);
        } else if (city.isEmpty()) {
            message = getString(R.string.please_enter_city);
            utilObj.showError(message, editTextCity);
        } else if (state.isEmpty()) {
            message = getString(R.string.please_select_state);
            utilObj.showToast(mContext, message, 1);
        } else if (zip.isEmpty()) {
            message = getString(R.string.please_enter_zip);
            utilObj.showError(message, editTextZip);
        }

        if (message.equalsIgnoreCase("") || message == null) {
            addShop();
        }
    }

    private void autoFillAddress(PlaceDetails placeDetails) {
        if (placeDetails.result != null) {
            mFormattedAddres = placeDetails.result.formattedAddress;
            if (placeDetails.result.addressComponents.size() > 0) {
                StringBuilder stringBuilder = new StringBuilder();
                for (PlaceDetails.AddressComponent addressComponent : placeDetails.result.addressComponents) {
                    if (addressComponent.types.get(0).equalsIgnoreCase("street_number")) {
                        stringBuilder.append(addressComponent.longName + ", ");
                    }
                    if (addressComponent.types.get(0).equalsIgnoreCase("route")) {
                        stringBuilder.append(addressComponent.longName + ", ");
                    }

                    if (!TextUtils.isEmpty(stringBuilder.toString())) {
                        autoCompleteTextViewStreet.setAdapter(null);
                        autoCompleteTextViewStreet.setText(stringBuilder.toString().substring(0, stringBuilder.toString().length() - 2));
                    }

                    if (addressComponent.types.get(0).equalsIgnoreCase("neighborhood")) {
                        editTextCity.setText(addressComponent.longName);
                    }

                    if (addressComponent.types.get(0).equalsIgnoreCase("postal_code")) {
                        editTextZip.setText(addressComponent.longName);
                    }

                    if (addressComponent.types.get(0).equalsIgnoreCase("administrative_area_level_1")) {
                        for (int i = 0; i < appInstance.states.data.size(); i++) {
                            States.Datum datum = appInstance.states.data.get(i);
                            if (datum.name.equalsIgnoreCase(addressComponent.longName)) {
                                spinnerState.setSelection(i + 1);
                            }
                        }
                    }
                }
            }
        }
    }

    private void createSpannableViews() {
        SpannableString ssNoResult = new SpannableString(getString(R.string.can_t_find_the_shop_you_are_looking_for_add_a_new_shop));
        ClickableSpan clickableSpanLogin = new ClickableSpan() {
            @Override
            public void onClick(View textView) {
                validateAddShop();
            }

            @Override
            public void updateDrawState(TextPaint ds) {
                super.updateDrawState(ds);
                ds.setUnderlineText(false);
            }
        };

        ssNoResult.setSpan(clickableSpanLogin, 41, 55, Spanned.SPAN_EXCLUSIVE_EXCLUSIVE);
        ssNoResult.setSpan(new ForegroundColorSpan(ContextCompat.getColor(mContext, R.color.white)), 41, 55, Spanned.SPAN_EXCLUSIVE_EXCLUSIVE);
        ssNoResult.setSpan(new StyleSpan(android.graphics.Typeface.BOLD), 41, 55, Spanned.SPAN_EXCLUSIVE_EXCLUSIVE);
        ssNoResult.setSpan(new UnderlineSpan(), 41, 55, Spanned.SPAN_EXCLUSIVE_EXCLUSIVE);

        textViewAddANewShop.setText(ssNoResult);
        textViewAddANewShop.setMovementMethod(LinkMovementMethod.getInstance());
        textViewAddANewShop.setHighlightColor(Color.TRANSPARENT);
    }

    private void getStates() {
        utilObj.startLoader(mContext);
        commonManagerObj.getStates();
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
            case Constants.TaskID.SEARCH_SHOPS:
                searchShopsAdapter = new SearchShopsAdapter((AppActivity) mContext);
                recyclerViewShops.setAdapter(searchShopsAdapter);
                searchShopsAdapter.setOnItemClickListener(this);

                if (appInstance.searchShops.data.size() > 0) {
                    searchShopsAdapter.setList(appInstance.searchShops.data);
                    searchShopsAdapter.notifyDataSetChanged();
                }
                getView(R.id.ll_add_new_shop).setVisibility(View.VISIBLE);

                break;

            case Constants.TaskID.STATES:
                listStates = new ArrayList<>();
                listStates.add(new States().new Datum("", "Select state", "Select state"));
                listStates.addAll(appInstance.states.data);

                statesSpinnerAdapter = new StatesSpinnerAdapter((Activity) mContext, 0, 0, listStates);
                spinnerState.setAdapter(statesSpinnerAdapter);
                break;

            case Constants.TaskID.ASSOCIATE_SHOP:
                utilObj.showToast(mContext, appInstance.message, 2);
                for (Shop s : appInstance.searchShops.data) {
                    if (shop.id.equalsIgnoreCase(s.id)) {
                        s.isAdded = true;
                    }
                }
                searchShopsAdapter.notifyDataSetChanged();
                break;

            case Constants.TaskID.ADD_SHOP:
                utilObj.showToast(mContext, appInstance.message, 2);
                finish();
                break;
        }
    }

    @Override
    public void onFailureRedirection(String errorMessage) {
        utilObj.stopLoader();
        showSnackBar(errorMessage);
    }

    public void searchShop(View view) {
        shopName = editTextShopName.getText().toString();
        street = autoCompleteTextViewStreet.getText().toString();
        city = editTextCity.getText().toString();
        if (state.equalsIgnoreCase("Select state"))
            state = "";
        zip = editTextZip.getText().toString();

        if ((city.isEmpty() || state.isEmpty()) && zip.isEmpty()) {
            utilObj.showToast(mContext, "Enter either city & state OR zip.", 2);
        } else {
            utilObj.startLoader(mContext);
            barberManagerObj.searchShops(shopName, city, state, zip);
        }
    }

    private void addShop() {
        utilObj.startLoader(mContext);
        AddShopInput addShopInput = new AddShopInput();
        addShopInput.city = city;
        addShopInput.name = shopName;
        addShopInput.state = state;
        addShopInput.zip = zip;
        addShopInput.streetAddress = street;
        if (!TextUtils.isEmpty(mFormattedAddres))
            addShopInput.address = mFormattedAddres;
        else
            addShopInput.address = street + " " + city + " " + state + " " + zip;
        addShopInput.latitude = mLatitude;
        addShopInput.longitude = mLongitude;

        barberManagerObj.addShop(addShopInput);
    }

    @Override
    public void onItemClick(Object item, int position) {
        shop = (Shop) item;
        utilObj.startLoader(mContext);
        AssociateShopInput associateShopInput = new AssociateShopInput();
        AssociateShopInput.Shop shopObj = new AssociateShopInput().new Shop();
        shopObj.shopId = shop.id;
        List<AssociateShopInput.Shop> listShopIds = new ArrayList<>();
        listShopIds.add(shopObj);
        associateShopInput.shops = listShopIds;
        barberManagerObj.associateShop(associateShopInput);
    }
}
