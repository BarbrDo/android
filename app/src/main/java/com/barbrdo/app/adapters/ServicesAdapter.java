package com.barbrdo.app.adapters;

import android.content.Context;
import android.support.v4.content.ContextCompat;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.barbrdo.app.R;
import com.barbrdo.app.activities.AppActivity;
import com.barbrdo.app.dataobject.BarberServices;
import com.barbrdo.app.dataobject.Service;
import com.barbrdo.app.interfaces.OnItemClickListener;

import java.text.DecimalFormat;

public class ServicesAdapter extends BaseRecyclerViewAdapter<Service> {

    private AppActivity mAppActivity;
    private OnItemClickListener onItemClickListener;
    DecimalFormat decimalFormat;

    public ServicesAdapter(AppActivity context) {
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
        View view = inflater.inflate(R.layout.item_barber_services, viewGroup, false);
        return view;
    }

    @Override
    protected void bindView(final Service item, ViewHolder viewHolder, final int position) {
        final TextView textViewService = viewHolder.getView(R.id.tv_service);
        final TextView textViewPrice = viewHolder.getView(R.id.tv_price);
        final RelativeLayout relativeLayoutParent = viewHolder.getView(R.id.rl_parent);
        relativeLayoutParent.setTag(item);
        textViewPrice.setVisibility(View.VISIBLE);

        textViewService.setText(item.name);
        textViewPrice.setText("$" + String.format("%.2f", item.price));

        if (item.isSelected) {
            relativeLayoutParent.setBackgroundResource(R.drawable.rectangle_blue);
            textViewService.setTextColor(ContextCompat.getColor(mAppActivity, R.color.white));
            textViewPrice.setTextColor(ContextCompat.getColor(mAppActivity, R.color.white));
        } else {
            relativeLayoutParent.setBackgroundResource(R.drawable.rectangle_gray);
            textViewService.setTextColor(ContextCompat.getColor(mAppActivity, R.color.text_dark));
            textViewPrice.setTextColor(ContextCompat.getColor(mAppActivity, R.color.text_dark));
        }

        relativeLayoutParent.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Service service = (Service) relativeLayoutParent.getTag();
                if (service.isSelected) {
                    relativeLayoutParent.setBackgroundResource(R.drawable.rectangle_gray);
                    textViewService.setTextColor(ContextCompat.getColor(mAppActivity, R.color.text_dark));
                    textViewPrice.setTextColor(ContextCompat.getColor(mAppActivity, R.color.text_dark));
                    service.isSelected = false;
                } else {
                    relativeLayoutParent.setBackgroundResource(R.drawable.rectangle_blue);
                    textViewService.setTextColor(ContextCompat.getColor(mAppActivity, R.color.white));
                    textViewPrice.setTextColor(ContextCompat.getColor(mAppActivity, R.color.white));
                    service.isSelected = true;
                }
                onItemClickListener.onItemClick(item, position);
            }
        });
    }
}