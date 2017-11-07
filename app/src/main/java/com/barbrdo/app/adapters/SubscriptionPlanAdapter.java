package com.barbrdo.app.adapters;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.RadioButton;
import android.widget.TextView;

import com.barbrdo.app.R;
import com.barbrdo.app.activities.AppActivity;
import com.barbrdo.app.dataobject.SubscriptionPlans;
import com.barbrdo.app.interfaces.OnItemClickListener;

import java.text.DecimalFormat;

public class SubscriptionPlanAdapter extends BaseRecyclerViewAdapter<SubscriptionPlans.Datum> {

    private AppActivity mAppActivity;
    private OnItemClickListener onItemClickListener;
    DecimalFormat decimalFormat;

    public SubscriptionPlanAdapter(AppActivity context) {
        super(context);
        this.mAppActivity = context;
        decimalFormat = new DecimalFormat();
        decimalFormat.setDecimalSeparatorAlwaysShown(false);
    }

    public void setOnItemClickListener(OnItemClickListener onItemClickListener) {
        this.onItemClickListener = onItemClickListener;
    }

    public void setOnlineShopId(String shopId, boolean isOnline) {
    }

    @Override
    public View createView(AppActivity context, ViewGroup viewGroup, int viewType) {
        LayoutInflater inflater = (LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        View view = inflater.inflate(R.layout.item_subscription_plans, viewGroup, false);
        return view;
    }

    @Override
    protected void bindView(final SubscriptionPlans.Datum item, ViewHolder viewHolder, final int position) {
        RadioButton radioButtonTitle = viewHolder.getView(R.id.rb_title);
        TextView textViewPrice = viewHolder.getView(R.id.tv_price);

        radioButtonTitle.setText(item.name);
        textViewPrice.setText("$" + decimalFormat.format(item.price));

        radioButtonTitle.setChecked(item.isSelected);

        radioButtonTitle.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                for (SubscriptionPlans.Datum datum : getList()) {
                    datum.isSelected = false;
                }
                notifyDataSetChanged();

                item.isSelected = true;
            }
        });
    }
}