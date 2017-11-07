package com.barbrdo.app.adapters;

import android.app.Activity;
import android.content.Context;
import android.graphics.Bitmap;
import android.support.v4.view.PagerAdapter;
import android.support.v4.view.ViewPager;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.RelativeLayout;

import com.barbrdo.app.R;
import com.barbrdo.app.dataobject.Gallery;
import com.barbrdo.app.utils.SessionManager;
import com.nostra13.universalimageloader.core.DisplayImageOptions;
import com.nostra13.universalimageloader.core.ImageLoader;
import com.nostra13.universalimageloader.core.assist.FailReason;
import com.nostra13.universalimageloader.core.listener.SimpleImageLoadingListener;

import java.util.List;

public class FullScreenImageAdapter extends PagerAdapter {
    private Activity mActivity;
    private List<Gallery> mImagePaths;
    private LayoutInflater inflater;
    private SessionManager sessionManager;
    private Bitmap bitmap;
    protected ImageLoader imageLoader;
    protected DisplayImageOptions optionsAttachment;

    public FullScreenImageAdapter(Activity activity,
                                  List<Gallery> imagePaths) {
        this.mActivity = activity;
        this.mImagePaths = imagePaths;
        sessionManager = new SessionManager(mActivity);
        initImageLoader();
    }

    public void initImageLoader() {
        optionsAttachment = new DisplayImageOptions.Builder()
                .cacheInMemory(true)
                .cacheOnDisk(true)
                .considerExifParams(true)
                .bitmapConfig(Bitmap.Config.RGB_565)
                .build();

        imageLoader = ImageLoader.getInstance();
    }

    @Override
    public int getCount() {
        return this.mImagePaths.size();
    }

    @Override
    public boolean isViewFromObject(View view, Object object) {
        return view == ((RelativeLayout) object);
    }

    @Override
    public Object instantiateItem(ViewGroup container, int position) {
        ImageView imgDisplay;
        final ProgressBar progressBar;

        inflater = (LayoutInflater) mActivity
                .getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        View viewLayout = inflater.inflate(R.layout.item_image_view, container,
                false);

        imgDisplay = (ImageView) viewLayout.findViewById(R.id.iv_photo);
        progressBar = (ProgressBar) viewLayout.findViewById(R.id.progressBar);

        if (!TextUtils.isEmpty(mImagePaths.get(position).name)) {
            progressBar.setVisibility(View.VISIBLE);
            imageLoader.displayImage(sessionManager.getImageBaseUrl() + mImagePaths.get(position).name, imgDisplay, optionsAttachment, new SimpleImageLoadingListener() {
                @Override
                public void onLoadingComplete(String imageUri, View view, Bitmap loadedImage) {
                    bitmap = loadedImage;
                    progressBar.setVisibility(View.GONE);
                }

                @Override
                public void onLoadingFailed(String imageUri, View view, FailReason failReason) {
                    super.onLoadingFailed(imageUri, view, failReason);
                    progressBar.setVisibility(View.GONE);
                }
            });
        } else {
            imgDisplay.setImageResource(R.drawable.rounded_white);
        }

        ((ViewPager) container).addView(viewLayout);
        return viewLayout;
    }

    @Override
    public void destroyItem(ViewGroup container, int position, Object object) {
        ((ViewPager) container).removeView((RelativeLayout) object);
    }

    public Bitmap getImageBitmap() {
        return bitmap;
    }
}