package com.barbrdo.app.fragments;


import android.os.Bundle;
import android.support.v4.widget.SwipeRefreshLayout;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.View;
import android.widget.TextView;

import com.barbrdo.app.R;
import com.barbrdo.app.activities.AddShopActivity;
import com.barbrdo.app.adapters.AssociatedShopsAdapter;
import com.barbrdo.app.dataobject.Shop;
import com.barbrdo.app.dataobject.ShopIdInput;
import com.barbrdo.app.interfaces.DialogActionCallback;
import com.barbrdo.app.interfaces.OnItemClickListener;
import com.barbrdo.app.interfaces.OnItemDelete;
import com.barbrdo.app.interfaces.ServiceRedirection;
import com.barbrdo.app.managers.BarberManager;
import com.barbrdo.app.utils.Constants;


public class ManageShopsFragment extends BaseFragment implements OnItemClickListener, ServiceRedirection,
        SwipeRefreshLayout.OnRefreshListener, OnItemDelete {

    private SwipeRefreshLayout swipeRefreshLayout;
    private RecyclerView recyclerViewServices;
    private BarberManager barberManagerObj;
    private AssociatedShopsAdapter associatedShopsAdapter;
    private TextView textViewNoResults;
    private TextView textViewAddShop;
    private Shop datum;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState, R.layout.fragment_manage_shops);
    }

    @Override
    protected void initView() {
        swipeRefreshLayout = getView(R.id.swipe_refresh_layout);
        recyclerViewServices = getView(R.id.rv_list);
        textViewNoResults = getView(R.id.tv_no_results);
        textViewAddShop = getView(R.id.tv_add_shop);

        barberManagerObj = new BarberManager(getBaseActivity(), this);
    }

    @Override
    protected void bindControls() {
        getBaseActivity().utilObj.setSpannableTextView(textViewAddShop, R.drawable.add_shop, getString(R.string.add_a_shop));
        recyclerViewServices.setHasFixedSize(true);
        LinearLayoutManager layoutManager = new LinearLayoutManager(getActivity());
        layoutManager.setOrientation(LinearLayoutManager.VERTICAL);
        recyclerViewServices.setLayoutManager(layoutManager);

        associatedShopsAdapter = new AssociatedShopsAdapter(getBaseActivity());
        associatedShopsAdapter.setOnItemClickListener(this);
        associatedShopsAdapter.setOnItemDelete(this);
        recyclerViewServices.setAdapter(associatedShopsAdapter);

        swipeRefreshLayout.setOnRefreshListener(this);

        textViewAddShop.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                addShop(v);
            }
        });
    }

    private void getAssociatedShops() {
        swipeRefreshLayout.setRefreshing(true);
        barberManagerObj.barberDetails(getBaseActivity().appInstance.userDetail.user.id);
    }

    @Override
    public void onResume() {
        super.onResume();
        swipeRefreshLayout.post(new Runnable() {
            @Override
            public void run() {
                getAssociatedShops();
            }
        });
    }

    @Override
    public void onItemClick(Object item, int position) {
        datum = (Shop) item;
        makeDefaultShop(datum.id);
    }

    @Override
    public void onSuccessRedirection(int taskID) {
        getBaseActivity().utilObj.stopLoader();
        swipeRefreshLayout.setRefreshing(false);
        switch (taskID) {
            case Constants.TaskID.BARBER_DETAIL:
                if (getBaseActivity().appInstance.barberDetail.data != null) {
                    if (getBaseActivity().appInstance.barberDetail.data.get(0).associateShops.size() > 0) {
                        associatedShopsAdapter.setList(getBaseActivity().appInstance.barberDetail.data.get(0).associateShops);
                        associatedShopsAdapter.notifyDataSetChanged();
                        recyclerViewServices.setVisibility(View.VISIBLE);
                        textViewNoResults.setVisibility(View.GONE);
                    } else {
                        recyclerViewServices.setVisibility(View.GONE);
                        textViewNoResults.setVisibility(View.VISIBLE);
                        textViewNoResults.setText(R.string.no_shops_associated);
                    }
                } else {
                    recyclerViewServices.setVisibility(View.GONE);
                    textViewNoResults.setVisibility(View.VISIBLE);
                    textViewNoResults.setText(R.string.no_shops_associated);
                }
                break;

            case Constants.TaskID.BARBER_DEFAULT_SHOP:
                getAssociatedShops();
                break;

            case Constants.TaskID.DELETE_SHOP:
                getBaseActivity().utilObj.showToast(getBaseActivity(), getBaseActivity().appInstance.message, 2);
                getBaseActivity().appInstance.barberDetail.data.get(0).associateShops.remove(datum);
                associatedShopsAdapter.notifyDataSetChanged();
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
        getAssociatedShops();
    }

    public void addShop(View view) {
        getBaseActivity().startActivity(AddShopActivity.class);
    }

    private void makeDefaultShop(String shopId) {
        getBaseActivity().utilObj.startLoader(getBaseActivity());
        ShopIdInput shopIdInput = new ShopIdInput();
        shopIdInput.shopId = shopId;
        barberManagerObj.defaultShop(shopIdInput);
    }

    @Override
    public void onDelete(Object o) {
        datum = (Shop) o;

        getBaseActivity().utilObj.showAlertDialog(getBaseActivity(), R.string.delete_shop, R.string.are_you_sure_you_want_to_delete_shop, R.string.ok, R.string.cancel, new DialogActionCallback() {
            @Override
            public void doOnPositive() {
                deleteShop(datum.id);
            }

            @Override
            public void doOnNegative() {
            }
        });
    }

    private void deleteShop(String shopId) {
        getBaseActivity().utilObj.startLoader(getBaseActivity());
        ShopIdInput shopIdInput = new ShopIdInput();
        shopIdInput.shopId = shopId;
        barberManagerObj.deleteShop(shopIdInput);
    }
}
