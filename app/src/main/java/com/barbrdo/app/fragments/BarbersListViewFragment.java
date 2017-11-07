package com.barbrdo.app.fragments;


import android.os.Bundle;
import android.support.v4.widget.SwipeRefreshLayout;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.KeyEvent;
import android.view.View;
import android.view.inputmethod.EditorInfo;
import android.widget.EditText;
import android.widget.TextView;

import com.barbrdo.app.R;
import com.barbrdo.app.activities.RequestBarberActivity;
import com.barbrdo.app.adapters.BarberAdapter;
import com.barbrdo.app.dataobject.CustomerBarbers;
import com.barbrdo.app.interfaces.OnItemClickListener;
import com.barbrdo.app.interfaces.ServiceRedirection;
import com.barbrdo.app.managers.CustomerManager;
import com.barbrdo.app.utils.Constants;


public class BarbersListViewFragment extends BaseFragment implements OnItemClickListener, ServiceRedirection,
        SwipeRefreshLayout.OnRefreshListener {

    private SwipeRefreshLayout swipeRefreshLayout;
    private RecyclerView recyclerViewBarbers;
    private CustomerManager customerManagerObj;
    private BarberAdapter barberAdapter;
    private TextView textViewNoResults;
    private EditText editTextSearch;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState, R.layout.fragment_search_barbers);
    }

    @Override
    protected void initView() {
        swipeRefreshLayout = getView(R.id.swipe_refresh_layout);
        recyclerViewBarbers = getView(R.id.rv_list);
        textViewNoResults = getView(R.id.tv_no_results);
        editTextSearch = getView(R.id.et_search);

        customerManagerObj = new CustomerManager(getBaseActivity(), this);
    }

    @Override
    protected void bindControls() {
        recyclerViewBarbers.setHasFixedSize(true);
        LinearLayoutManager layoutManager = new LinearLayoutManager(getActivity());
        layoutManager.setOrientation(LinearLayoutManager.VERTICAL);
        recyclerViewBarbers.setLayoutManager(layoutManager);

        barberAdapter = new BarberAdapter(getBaseActivity());
        barberAdapter.setOnItemClickListener(this);
        recyclerViewBarbers.setAdapter(barberAdapter);

        swipeRefreshLayout.setOnRefreshListener(this);
        swipeRefreshLayout.post(new Runnable() {
            @Override
            public void run() {
                getBarbers();
            }
        });

        editTextSearch.setOnEditorActionListener(new TextView.OnEditorActionListener() {
            @Override
            public boolean onEditorAction(TextView v, int actionId, KeyEvent event) {
                if (actionId == EditorInfo.IME_ACTION_SEARCH) {
                    getBarbers();
                    return true;
                }
                return false;
            }
        });

        editTextSearch.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {

            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                if (s.length() == 0)
                    getBarbers();
            }

            @Override
            public void afterTextChanged(Editable s) {

            }
        });
    }

    private void getBarbers() {
        swipeRefreshLayout.setRefreshing(true);
        customerManagerObj.getCustomerBarbers();
    }

    @Override
    public void onItemClick(Object item, int position) {
        CustomerBarbers.Datum datum = (CustomerBarbers.Datum) item;
        Bundle bundle = new Bundle();
        bundle.putSerializable(Constants.BundleKeyTag.SERIALIZED_DATA, datum);
        getBaseActivity().startActivity(RequestBarberActivity.class, bundle);
    }

    @Override
    public void onSuccessRedirection(int taskID) {
        swipeRefreshLayout.setRefreshing(false);
        switch (taskID) {
            case Constants.TaskID.CUSTOMER_BARBERS:
                if (getBaseActivity().appInstance.customerBarbers.data.size() > 0) {
                    barberAdapter.setList(getBaseActivity().appInstance.customerBarbers.data);
                    barberAdapter.notifyDataSetChanged();
                    textViewNoResults.setVisibility(View.GONE);
                    recyclerViewBarbers.setVisibility(View.VISIBLE);
                } else {
                    textViewNoResults.setVisibility(View.VISIBLE);
                    recyclerViewBarbers.setVisibility(View.GONE);
                    textViewNoResults.setText(R.string.no_barbers_could_be_found);
                }
                break;
        }
    }

    @Override
    public void onFailureRedirection(String errorMessage) {
        swipeRefreshLayout.setRefreshing(false);
        getBaseActivity().showSnackBar(errorMessage);
    }

    @Override
    public void onRefresh() {
        getBarbers();
    }
}
