package com.barbrdo.app.adapters;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.PorterDuff;
import android.support.v4.content.ContextCompat;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.RelativeLayout;

import com.barbrdo.app.R;
import com.barbrdo.app.activities.AppActivity;
import com.barbrdo.app.dataobject.Gallery;
import com.barbrdo.app.interfaces.OnItemClickListener;
import com.barbrdo.app.interfaces.OnLongItemClickListener;
import com.nostra13.universalimageloader.core.assist.FailReason;
import com.nostra13.universalimageloader.core.listener.SimpleImageLoadingListener;

public class GalleryAdapter extends BaseRecyclerViewAdapter<Gallery> {

    private AppActivity mAppActivity;
    private OnItemClickListener onItemClickListener;
    private OnLongItemClickListener onLongItemClickListener;

    public GalleryAdapter(AppActivity context) {
        super(context);
        this.mAppActivity = context;
    }

    public void setOnItemClickListener(OnItemClickListener onItemClickListener) {
        this.onItemClickListener = onItemClickListener;
    }

    public void setOnLongItemClickListener(OnLongItemClickListener onLongItemClickListener) {
        this.onLongItemClickListener = onLongItemClickListener;
    }

    @Override
    public View createView(AppActivity context, ViewGroup viewGroup, int viewType) {
        LayoutInflater inflater = (LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        View view = inflater.inflate(R.layout.item_gallery, viewGroup, false);
        return view;
    }

    @Override
    protected void bindView(final Gallery item, ViewHolder viewHolder, final int position) {
        ImageView imageViewIcon = viewHolder.getView(R.id.iv_image);
        RelativeLayout relativeLayoutParent = viewHolder.getView(R.id.rl_parent);
        final ProgressBar progressBarImage = viewHolder.getView(R.id.pb_image);
        progressBarImage.getIndeterminateDrawable()
                .setColorFilter(ContextCompat.getColor(mAppActivity, R.color.colorAccent), PorterDuff.Mode.MULTIPLY);

        if (!TextUtils.isEmpty(item.name))
            imageLoader.displayImage(sessionManager.getImageBaseUrl() + item.name, imageViewIcon, optionsMediumNoStub, new SimpleImageLoadingListener() {
                @Override
                public void onLoadingComplete(String imageUri, View view, Bitmap loadedImage) {
                    progressBarImage.setVisibility(View.GONE);
                }

                @Override
                public void onLoadingFailed(String imageUri, View view, FailReason failReason) {
                    super.onLoadingFailed(imageUri, view, failReason);
                    progressBarImage.setVisibility(View.GONE);
                }
            });
        else
            imageViewIcon.setImageResource(R.drawable.white_bg);


        relativeLayoutParent.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                onItemClickListener.onItemClick(item, position);
            }
        });

        relativeLayoutParent.setOnLongClickListener(new View.OnLongClickListener() {
            @Override
            public boolean onLongClick(View v) {
                onLongItemClickListener.onLongItemClick(item, position);
                return false;
            }
        });
    }
}