package com.barbrdo.app.fragments;


import android.os.Bundle;
import android.support.v4.widget.SwipeRefreshLayout;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.View;
import android.widget.TextView;

import com.barbrdo.app.R;
import com.barbrdo.app.adapters.FavoriteBarberAdapter;
import com.barbrdo.app.dataobject.CustomerBarbers;
import com.barbrdo.app.interfaces.DialogActionCallback;
import com.barbrdo.app.interfaces.OnItemClickListener;
import com.barbrdo.app.interfaces.ServiceRedirection;
import com.barbrdo.app.managers.CustomerManager;
import com.barbrdo.app.utils.Constants;


public class FavoriteBarbersFragment extends BaseFragment implements OnItemClickListener, ServiceRedirection,
        SwipeRefreshLayout.OnRefreshListener {

    private SwipeRefreshLayout swipeRefreshLayout;
    private RecyclerView recyclerViewServices;
    private CustomerManager customerManagerObj;
    private FavoriteBarberAdapter favoriteBarberAdapter;
    private TextView textViewNoResults;
    private CustomerBarbers.Datum datum;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState, R.layout.fragment_favorite_barbers);
    }

    @Override
    protected void initView() {
        swipeRefreshLayout = getView(R.id.swipe_refresh_layout);
        recyclerViewServices = getView(R.id.rv_list);
        textViewNoResults = getView(R.id.tv_no_results);

        customerManagerObj = new CustomerManager(getBaseActivity(), this);
    }

    @Override
    protected void bindControls() {
        recyclerViewServices.setHasFixedSize(true);
        LinearLayoutManager layoutManager = new LinearLayoutManager(getActivity());
        layoutManager.setOrientation(LinearLayoutManager.VERTICAL);
        recyclerViewServices.setLayoutManager(layoutManager);

        favoriteBarberAdapter = new FavoriteBarberAdapter(getBaseActivity());
        favoriteBarberAdapter.setOnItemClickListener(this);
        recyclerViewServices.setAdapter(favoriteBarberAdapter);

        swipeRefreshLayout.setOnRefreshListener(this);
        swipeRefreshLayout.post(new Runnable() {
            @Override
            public void run() {
                getFavoriteBarbers();
            }
        });
    }

    private void getFavoriteBarbers() {
        swipeRefreshLayout.setRefreshing(true);
        customerManagerObj.getFavoriteBarbers();
    }

    @Override
    public void onItemClick(Object item, int position) {
        datum = (CustomerBarbers.Datum) item;
        deleteFavBarber(datum.id);
    }

    private void deleteFavBarber(final String barberId) {
        getBaseActivity().utilObj.showAlertDialog(getBaseActivity(), R.string.remove_barber, R.string.are_you_sure_you_want_to_remove_barber, R.string.ok, R.string.cancel, new DialogActionCallback() {
            @Override
            public void doOnPositive() {
                getBaseActivity().utilObj.startLoader(getBaseActivity());
                customerManagerObj.deleteFavBarber(barberId);
            }

            @Override
            public void doOnNegative() {
            }
        });
    }

    @Override
    public void onSuccessRedirection(int taskID) {
        getBaseActivity().utilObj.stopLoader();
        swipeRefreshLayout.setRefreshing(false);
        switch (taskID) {
            case Constants.TaskID.FAVORITE_BARBER:
                if (getBaseActivity().appInstance.customerBarbers.data.size() > 0) {
                    favoriteBarberAdapter.setList(getBaseActivity().appInstance.customerBarbers.data);
                    favoriteBarberAdapter.notifyDataSetChanged();
                    recyclerViewServices.setVisibility(View.VISIBLE);
                    textViewNoResults.setVisibility(View.GONE);
                } else {
                    recyclerViewServices.setVisibility(View.GONE);
                    textViewNoResults.setVisibility(View.VISIBLE);
                    textViewNoResults.setText(R.string.no_favorite_barbers_added);
                }
                break;

            case Constants.TaskID.DELETE_FAVORITE_BARBER:
                getBaseActivity().utilObj.showToast(getBaseActivity(), getBaseActivity().appInstance.message, 1);
                getBaseActivity().appInstance.customerBarbers.data.remove(datum);
                favoriteBarberAdapter.setList(getBaseActivity().appInstance.customerBarbers.data);
                favoriteBarberAdapter.notifyDataSetChanged();

                if (getBaseActivity().appInstance.customerBarbers.data.size() == 0) {
                    recyclerViewServices.setVisibility(View.GONE);
                    textViewNoResults.setVisibility(View.VISIBLE);
                    textViewNoResults.setText(R.string.no_favorite_barbers_added);
                }
                break;
        }
    }

    @Override
    public void onFailureRedirection(String errorMessage) {
        getBaseActivity().utilObj.stopLoader();
        swipeRefreshLayout.setRefreshing(false);
        getBaseActivity().showSnackBar(errorMessage);
    }

    @Override
    public void onRefresh() {
        getFavoriteBarbers();
    }
}
