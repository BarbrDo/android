package com.barbrdo.app.activities;

import android.content.Context;
import android.os.Bundle;
import android.support.v4.widget.SwipeRefreshLayout;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.MenuItem;
import android.view.View;
import android.widget.TextView;

import com.barbrdo.app.R;
import com.barbrdo.app.adapters.AssociatedShopsAdapter;
import com.barbrdo.app.dataobject.Shop;
import com.barbrdo.app.dataobject.ShopIdInput;
import com.barbrdo.app.interfaces.DialogActionCallback;
import com.barbrdo.app.interfaces.OnItemClickListener;
import com.barbrdo.app.interfaces.OnItemDelete;
import com.barbrdo.app.interfaces.ServiceRedirection;
import com.barbrdo.app.managers.BarberManager;
import com.barbrdo.app.utils.Constants;

public class ManageShopsActivity extends AppActivity implements OnItemClickListener, ServiceRedirection,
        SwipeRefreshLayout.OnRefreshListener, OnItemDelete {

    private Context mContext;
    private SwipeRefreshLayout swipeRefreshLayout;
    private RecyclerView recyclerViewServices;
    private BarberManager barberManagerObj;
    private AssociatedShopsAdapter associatedShopsAdapter;
    private TextView textViewNoResults;
    private TextView textViewAddShop;
    private Shop datum;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_manage_shops);
        mContext = ManageShopsActivity.this;
        initData();
        bindControls();
    }

    @Override
    void initData() {
        setUpToolBar("");
        swipeRefreshLayout = getView(R.id.swipe_refresh_layout);
        recyclerViewServices = getView(R.id.rv_list);
        textViewNoResults = getView(R.id.tv_no_results);
        textViewAddShop = getView(R.id.tv_add_shop);

        barberManagerObj = new BarberManager(this, this);
    }

    @Override
    void bindControls() {
        utilObj.setSpannableTextView(textViewAddShop, R.drawable.add_shop, getString(R.string.add_a_shop));
        recyclerViewServices.setHasFixedSize(true);
        LinearLayoutManager layoutManager = new LinearLayoutManager(mContext);
        layoutManager.setOrientation(LinearLayoutManager.VERTICAL);
        recyclerViewServices.setLayoutManager(layoutManager);

        associatedShopsAdapter = new AssociatedShopsAdapter((AppActivity) mContext);
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
        barberManagerObj.barberDetails(appInstance.userDetail.user.id);
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
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case android.R.id.home:
                onBackPressed();
                return true;
        }
        return super.onOptionsItemSelected(item);
    }


    @Override
    public void onItemClick(Object item, int position) {
        datum = (Shop) item;
        makeDefaultShop(datum.id);
    }

    @Override
    public void onSuccessRedirection(int taskID) {
        utilObj.stopLoader();
        swipeRefreshLayout.setRefreshing(false);
        switch (taskID) {
            case Constants.TaskID.BARBER_DETAIL:
                if (appInstance.barberDetail.data != null) {
                    if (appInstance.barberDetail.data.get(0).associateShops.size() > 0) {
                        associatedShopsAdapter.setList(appInstance.barberDetail.data.get(0).associateShops);
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
                utilObj.showToast(mContext, appInstance.message, 2);
                appInstance.barberDetail.data.get(0).associateShops.remove(datum);
                associatedShopsAdapter.notifyDataSetChanged();
                break;
        }
    }

    @Override
    public void onFailureRedirection(String errorMessage) {
        utilObj.stopLoader();
        swipeRefreshLayout.setRefreshing(false);
        showSnackBar(errorMessage);
    }

    @Override
    public void onRefresh() {
        getAssociatedShops();
    }

    public void addShop(View view) {
        startActivity(AddShopActivity.class);
    }

    private void makeDefaultShop(String shopId) {
        utilObj.startLoader(mContext);
        ShopIdInput shopIdInput = new ShopIdInput();
        shopIdInput.shopId = shopId;
        barberManagerObj.defaultShop(shopIdInput);
    }

    @Override
    public void onDelete(Object o) {
        datum = (Shop) o;

        utilObj.showAlertDialog(mContext, R.string.delete_shop, R.string.are_you_sure_you_want_to_delete_shop, R.string.ok, R.string.cancel, new DialogActionCallback() {
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
        utilObj.startLoader(mContext);
        ShopIdInput shopIdInput = new ShopIdInput();
        shopIdInput.shopId = shopId;
        barberManagerObj.deleteShop(shopIdInput);
    }
}
