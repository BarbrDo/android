package com.barbrdo.app.adapters;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.barbrdo.app.R;
import com.barbrdo.app.activities.AppActivity;
import com.barbrdo.app.dataobject.BarberHome;
import com.barbrdo.app.interfaces.OnItemClickListener;

import java.text.DecimalFormat;

public class ShopAdapter extends BaseRecyclerViewAdapter<BarberHome.AssociateShop> {

    private AppActivity mAppActivity;
    private OnItemClickListener onItemClickListener;
    DecimalFormat decimalFormat;
    String shopId;
    boolean isOnline;

    public ShopAdapter(AppActivity context) {
        super(context);
        this.mAppActivity = context;
        decimalFormat = new DecimalFormat();
        decimalFormat.setDecimalSeparatorAlwaysShown(false);
    }

    public void setOnItemClickListener(OnItemClickListener onItemClickListener) {
        this.onItemClickListener = onItemClickListener;
    }

    public void setOnlineShopId(String shopId, boolean isOnline){
        this.shopId = shopId;
        this.isOnline = isOnline;
    }

    @Override
    public View createView(AppActivity context, ViewGroup viewGroup, int viewType) {
        LayoutInflater inflater = (LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        View view = inflater.inflate(R.layout.item_shop, viewGroup, false);
        return view;
    }

    @Override
    protected void bindView(final BarberHome.AssociateShop item, ViewHolder viewHolder, final int position) {
        LinearLayout linearLayoutParent = viewHolder.getView(R.id.ll_parent);
        TextView textViewShop = viewHolder.getView(R.id.tv_shop);
        TextView textViewDefaultShop = viewHolder.getView(R.id.tv_default_shop);

        if (item.shopInfo.size() > 0) {
            textViewShop.setText(item.shopInfo.get(0).name + "\n" + item.shopInfo.get(0).address + "\n" + item.shopInfo.get(0).city + ", " + item.shopInfo.get(0).state + " " + item.shopInfo.get(0).zip);
        }

        if(!isOnline) {
            if (item.isDefault) {
                textViewDefaultShop.setVisibility(View.VISIBLE);
                textViewDefaultShop.setText("Default Shop");
            } else {
                textViewDefaultShop.setVisibility(View.INVISIBLE);
            }
        }else{
            if(item.shopInfo.get(0).id.equalsIgnoreCase(shopId)){
                textViewDefaultShop.setVisibility(View.VISIBLE);
                textViewDefaultShop.setText("Active Shop");
            } else {
                textViewDefaultShop.setVisibility(View.INVISIBLE);
            }
        }

        linearLayoutParent.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                onItemClickListener.onItemClick(item, position);
            }
        });
    }
}