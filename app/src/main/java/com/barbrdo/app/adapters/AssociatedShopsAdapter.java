package com.barbrdo.app.adapters;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.barbrdo.app.R;
import com.barbrdo.app.activities.AppActivity;
import com.barbrdo.app.dataobject.Shop;
import com.barbrdo.app.interfaces.OnItemClickListener;
import com.barbrdo.app.interfaces.OnItemDelete;
import com.daimajia.swipe.SwipeLayout;

import java.text.DecimalFormat;

public class AssociatedShopsAdapter extends BaseRecyclerViewAdapter<Shop> {

    private AppActivity mAppActivity;
    private OnItemClickListener onItemClickListener;
    private OnItemDelete onItemDelete;
    DecimalFormat decimalFormat;

    public AssociatedShopsAdapter(AppActivity context) {
        super(context);
        this.mAppActivity = context;
        decimalFormat = new DecimalFormat();
        decimalFormat.setDecimalSeparatorAlwaysShown(false);
    }

    public void setOnItemClickListener(OnItemClickListener onItemClickListener) {
        this.onItemClickListener = onItemClickListener;
    }

    public void setOnItemDelete(OnItemDelete onItemDelete) {
        this.onItemDelete = onItemDelete;
    }

    @Override
    public View createView(AppActivity context, ViewGroup viewGroup, int viewType) {
        LayoutInflater inflater = (LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        View view = inflater.inflate(R.layout.item_associated_shop, viewGroup, false);
        return view;
    }

    @Override
    protected void bindView(final Shop item, ViewHolder viewHolder, final int position) {
        SwipeLayout swipeLayout = viewHolder.getView(R.id.swipe);
        TextView textViewShop = viewHolder.getView(R.id.tv_shop);
        TextView textViewDefaultShop = viewHolder.getView(R.id.tv_default_shop);
        LinearLayout linearLayoutDelete = viewHolder.getView(R.id.ll_delete);

        textViewShop.setText(item.name + "\n" + item.address + "\n" + item.city + ", " + item.state + " " + item.zip);

        if (item.isDefault) {
            textViewDefaultShop.setCompoundDrawablesWithIntrinsicBounds(0, R.drawable.ic_green_check4x, 0, 0);
        } else {
            textViewDefaultShop.setCompoundDrawablesWithIntrinsicBounds(0, R.drawable.radio_off, 0, 0);
        }

        textViewDefaultShop.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (!item.isDefault)
                    onItemClickListener.onItemClick(item, position);
            }
        });

        linearLayoutDelete.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                onItemDelete.onDelete(item);
            }
        });
    }
}