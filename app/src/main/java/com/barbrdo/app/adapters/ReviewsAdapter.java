package com.barbrdo.app.adapters;

import android.content.Context;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.RatingBar;
import android.widget.TextView;

import com.barbrdo.app.R;
import com.barbrdo.app.activities.AppActivity;
import com.barbrdo.app.activities.CustomerProfileActivity;
import com.barbrdo.app.dataobject.Rating;
import com.barbrdo.app.interfaces.OnItemClickListener;
import com.barbrdo.app.utils.Constants;

public class ReviewsAdapter extends BaseRecyclerViewAdapter<Rating> {

    private AppActivity mAppActivity;
    private OnItemClickListener onItemClickListener;

    public ReviewsAdapter(AppActivity context) {
        super(context);
        this.mAppActivity = context;
    }

    public void setOnItemClickListener(OnItemClickListener onItemClickListener) {
        this.onItemClickListener = onItemClickListener;
    }

    @Override
    public View createView(AppActivity context, ViewGroup viewGroup, int viewType) {
        LayoutInflater inflater = (LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        View view = inflater.inflate(R.layout.item_ratings, viewGroup, false);
        return view;
    }

    @Override
    protected void bindView(final Rating item, ViewHolder viewHolder, final int position) {
        ImageView imageViewProfilePic = viewHolder.getView(R.id.iv_profile_picture);
        TextView textViewUsername = viewHolder.getView(R.id.tv_username);
        TextView textViewDateTime = viewHolder.getView(R.id.tv_date_time);
        RatingBar ratingBar = viewHolder.getView(R.id.rb_ratings);

        if (!TextUtils.isEmpty(item.picture)) {
            imageLoader.displayImage(sessionManager.getImageBaseUrl() + item.picture, imageViewProfilePic, options);
        }

        textViewUsername.setText(item.ratedByName);
        if (!TextUtils.isEmpty(item.appointmentDate))
            textViewDateTime.setText("Cut: " + mAppActivity.utilObj.getMyDateFormat(item.appointmentDate, Constants.DateFormat.YYYYMMDDTHHMMSS, Constants.DateFormat.MMDDYYYY));

        ratingBar.setRating(item.score);

//        imageViewProfilePic.setOnClickListener(new View.OnClickListener() {
//            @Override
//            public void onClick(View v) {
//                Bundle bundle = new Bundle();
//                bundle.putString(Constants.BundleKeyTag.CUSTOMER_ID, item.ratedBy);
//                mAppActivity.startActivity(CustomerProfileActivity.class, bundle);
//            }
//        });
    }
}