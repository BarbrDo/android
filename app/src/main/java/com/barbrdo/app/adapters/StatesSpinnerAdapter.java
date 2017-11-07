package com.barbrdo.app.adapters;

import android.app.Activity;
import android.content.Context;
import android.support.v4.content.ContextCompat;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.TextView;

import com.barbrdo.app.R;
import com.barbrdo.app.dataobject.BarberHome;
import com.barbrdo.app.dataobject.States;

import java.util.List;

public class StatesSpinnerAdapter extends ArrayAdapter<States.Datum> {
    LayoutInflater inflater;
    List<States.Datum> list;
    Context context;

    public StatesSpinnerAdapter(Activity context, int resouceId, int textviewId, List<States.Datum> list) {
        super(context, resouceId, textviewId, list);
        this.list = list;
        this.context = context;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        return rowView(convertView, position);
    }

    @Override
    public View getDropDownView(int position, View convertView, ViewGroup parent) {
        return rowView(convertView, position);
    }

    public States.Datum getItem(int position) {
        return list.get(position);
    }

    private View rowView(View convertView, int position) {
        States.Datum rowItem = getItem(position);
        ViewHolder holder;
        View rowView = convertView;
        if (rowView == null) {

            holder = new ViewHolder();
            inflater = (LayoutInflater) getContext().getSystemService(Context.LAYOUT_INFLATER_SERVICE);
            rowView = inflater.inflate(R.layout.item_state_spinner, null, false);

            holder.txtTitle = (TextView) rowView.findViewById(R.id.tv_state_name);
            rowView.setTag(holder);
        } else {
            holder = (ViewHolder) rowView.getTag();
        }
        holder.txtTitle.setText(rowItem.name);

        if(position == 0){
            holder.txtTitle.setTextColor(ContextCompat.getColor(context, R.color.text_light));
        }else{
            holder.txtTitle.setTextColor(ContextCompat.getColor(context, R.color.text_dark));
        }

        return rowView;
    }

    private class ViewHolder {
        TextView txtTitle;
    }
}