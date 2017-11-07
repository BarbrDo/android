package com.barbrdo.app.adapters;

import android.app.Activity;
import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.TextView;

import com.barbrdo.app.R;
import com.barbrdo.app.dataobject.BarberHome;

import java.util.List;

public class ShopSpinnerAdapter extends ArrayAdapter<BarberHome.AssociateShop> {
    LayoutInflater inflater;
    List<BarberHome.AssociateShop> list;

    public ShopSpinnerAdapter(Activity context, int resouceId, int textviewId, List<BarberHome.AssociateShop> list) {
        super(context, resouceId, textviewId, list);
        this.list = list;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        return rowView(convertView, position);
    }

    @Override
    public View getDropDownView(int position, View convertView, ViewGroup parent) {
        return rowView(convertView, position);
    }

    public BarberHome.AssociateShop getItem(int position) {
        return list.get(position);
    }

    private View rowView(View convertView, int position) {
        BarberHome.AssociateShop rowItem = getItem(position);
        ViewHolder holder;
        View rowView = convertView;
        if (rowView == null) {

            holder = new ViewHolder();
            inflater = (LayoutInflater) getContext().getSystemService(Context.LAYOUT_INFLATER_SERVICE);
            rowView = inflater.inflate(R.layout.item_shop_spinner, null, false);

            holder.txtTitle = (TextView) rowView.findViewById(R.id.tv_shop_name);
            rowView.setTag(holder);
        } else {
            holder = (ViewHolder) rowView.getTag();
        }
        holder.txtTitle.setText(rowItem.shopInfo.get(0).name);
        return rowView;
    }

    private class ViewHolder {
        TextView txtTitle;
    }
}