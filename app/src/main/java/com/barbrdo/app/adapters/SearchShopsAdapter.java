package com.barbrdo.app.adapters;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.barbrdo.app.R;
import com.barbrdo.app.activities.AppActivity;
import com.barbrdo.app.dataobject.Shop;
import com.barbrdo.app.interfaces.OnItemClickListener;

import java.text.DecimalFormat;

public class SearchShopsAdapter extends BaseRecyclerViewAdapter<Shop> {

    private AppActivity mAppActivity;
    private OnItemClickListener onItemClickListener;
    DecimalFormat decimalFormat;

    public SearchShopsAdapter(AppActivity context) {
        super(context);
        this.mAppActivity = context;
        decimalFormat = new DecimalFormat();
        decimalFormat.setDecimalSeparatorAlwaysShown(false);
    }

    public void setOnItemClickListener(OnItemClickListener onItemClickListener) {
        this.onItemClickListener = onItemClickListener;
    }

    @Override
    public View createView(AppActivity context, ViewGroup viewGroup, int viewType) {
        LayoutInflater inflater = (LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        View view = inflater.inflate(R.layout.item_search_shop, viewGroup, false);
        return view;
    }

    @Override
    protected void bindView(final Shop item, ViewHolder viewHolder, final int position) {
        TextView textViewShop = viewHolder.getView(R.id.tv_shop);
        TextView textViewAdd = viewHolder.getView(R.id.tv_add);
        View view = viewHolder.getView(R.id.view_2);

        textViewShop.setText(item.name + "\n" + item.address + "\n" + item.city + ", " + item.state + " " + item.zip);

        if (item.isAdded) {
            textViewAdd.setVisibility(View.GONE);
            view.setVisibility(View.GONE);
        } else {
            textViewAdd.setVisibility(View.VISIBLE);
            view.setVisibility(View.VISIBLE);
        }

        textViewAdd.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                onItemClickListener.onItemClick(item, position);
            }
        });
    }
}