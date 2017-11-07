package com.barbrdo.app.adapters;

import android.content.Context;
import android.os.Bundle;
import android.support.v4.content.ContextCompat;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.barbrdo.app.R;
import com.barbrdo.app.activities.AppActivity;
import com.barbrdo.app.activities.BarberProfileActivity;
import com.barbrdo.app.dataobject.CustomerBarbers;
import com.barbrdo.app.dataobject.Rating;
import com.barbrdo.app.dataobject.Service;
import com.barbrdo.app.interfaces.OnItemClickListener;
import com.barbrdo.app.utils.Constants;

import java.text.DecimalFormat;

public class BarberAdapter extends BaseRecyclerViewAdapter<CustomerBarbers.Datum> {

    private AppActivity mAppActivity;
    private OnItemClickListener onItemClickListener;
    DecimalFormat decimalFormat;

    public BarberAdapter(AppActivity context) {
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
        View view = inflater.inflate(R.layout.item_select_barber, viewGroup, false);
        return view;
    }

    @Override
    protected void bindView(final CustomerBarbers.Datum item, ViewHolder viewHolder, final int position) {
        ImageView imageViewProfilePic = viewHolder.getView(R.id.iv_profile_picture);
        ImageView imageViewFavourite = viewHolder.getView(R.id.iv_favourite);
        TextView textViewUsername = viewHolder.getView(R.id.tv_username);
        TextView textViewLocation = viewHolder.getView(R.id.tv_location);
        TextView textViewRatings = viewHolder.getView(R.id.tv_ratings);
        TextView textViewServices = viewHolder.getView(R.id.tv_services);
        TextView textViewRequest = viewHolder.getView(R.id.tv_request);
        RelativeLayout mContainer = viewHolder.getView(R.id.rl_main);
        if (item.ratings.size() > 0) {
            float avg = 0;
            for (Rating rating : item.ratings) {
                avg = avg + rating.score;
            }
            avg = avg / item.ratings.size();
            textViewRatings.setText(String.format("%.1f", avg));
        } else {
            textViewRatings.setText("0.0");
        }
        textViewUsername.setText(item.firstName + " " + item.lastName.charAt(0));
        if (item.barberShops != null)
            textViewLocation.setText(item.barberShops.name + " (" + Math.round(item.distance) + " mi)");

        if (!TextUtils.isEmpty(item.picture)) {
            imageLoader.displayImage(sessionManager.getImageBaseUrl() + item.picture, imageViewProfilePic, options);
        } else {
            imageViewProfilePic.setImageResource(R.drawable.placeholder_user);
        }

        if (item.barberServices.size() > 0) {
            StringBuilder stringBuilder = new StringBuilder();
            for (Service barberService : item.barberServices) {
                stringBuilder.append(barberService.name + ": $" + String.format("%.2f", barberService.price) + ", ");
            }
            String services = stringBuilder.toString();
            if (services.endsWith(", ")) {
                services = services.substring(0, services.length() - 2);
            }
            textViewServices.setText(services);
        }

        if (!item.isOnline) {
            textViewRequest.setText("OFFLINE");
            textViewRequest.setTextColor(ContextCompat.getColor(mAppActivity, R.color.button_dark_gray));
            textViewRequest.setEnabled(false);
            textViewLocation.setVisibility(View.GONE);
            textViewServices.setVisibility(View.GONE);
        } else {
            textViewRequest.setText("REQUEST");
            textViewRequest.setTextColor(ContextCompat.getColor(mAppActivity, R.color.button_blue));
            textViewRequest.setEnabled(true);
            textViewLocation.setVisibility(View.VISIBLE);
            textViewServices.setVisibility(View.VISIBLE);
        }

        if (item.isFavourite)
            imageViewFavourite.setVisibility(View.VISIBLE);
        else
            imageViewFavourite.setVisibility(View.GONE);

        textViewRequest.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                onItemClickListener.onItemClick(item, position);
            }
        });

        mContainer.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Bundle bundle = new Bundle();
                bundle.putString(Constants.BundleKeyTag.BARBER_ID, item.id);
                mAppActivity.startActivity(BarberProfileActivity.class, bundle);
            }
        });

//        textViewUsername.setOnClickListener(new View.OnClickListener() {
//            @Override
//            public void onClick(View v) {
//                Bundle bundle = new Bundle();
//                bundle.putString(Constants.BundleKeyTag.BARBER_ID, item.id);
//                mAppActivity.startActivity(BarberProfileActivity.class, bundle);
//            }
//        });
    }
}