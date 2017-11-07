package com.barbrdo.app.adapters;

import android.content.Context;
import android.graphics.Bitmap;
import android.support.v7.widget.RecyclerView;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;

import com.barbrdo.app.R;
import com.barbrdo.app.activities.AppActivity;
import com.barbrdo.app.utils.SessionManager;
import com.nostra13.universalimageloader.core.DisplayImageOptions;
import com.nostra13.universalimageloader.core.ImageLoader;
import com.nostra13.universalimageloader.core.display.RoundedBitmapDisplayer;

import java.util.Collections;
import java.util.List;

/**
 * Created by abhinavr on 4/10/16.
 */

public abstract class BaseRecyclerViewAdapter<T> extends RecyclerView.Adapter<BaseRecyclerViewAdapter.ViewHolder> {

    private List<T> mList = Collections.emptyList();
    private AppActivity appActivity;
    private OnViewHolderClick mListener;
    public ImageLoader imageLoader;
    public DisplayImageOptions options;
    public DisplayImageOptions optionsMediumRounded;
    public DisplayImageOptions optionsMediumNoStub;
    public SessionManager sessionManager;

    public interface OnViewHolderClick {
        void onClick(View view, int position);
    }

    public static class ViewHolder extends RecyclerView.ViewHolder implements View.OnClickListener {
        private OnViewHolderClick mListener;
        private View mView;

        public ViewHolder(View view, OnViewHolderClick listener) {
            super(view);
            mListener = listener;
            this.mView = view;
            if (mListener != null)
                view.setOnClickListener(this);
        }

        @Override
        public void onClick(View view) {
            if (mListener != null)
                mListener.onClick(view, getAdapterPosition());
        }

        public <T extends View> T getView(int viewId, String... text) {
            T t = (T) mView.findViewById(viewId);
            if (text != null) {
                StringBuilder stringBuilder = new StringBuilder();
                for (String s : text) {
                    stringBuilder.append(s).append(" ");
                    if (t instanceof TextView)
                        ((TextView) t).setText(stringBuilder.toString().trim());
                    else if (t instanceof Button)
                        ((Button) t).setText(stringBuilder.toString().trim());
                    else if (t instanceof EditText) {
                        EditText editText = ((EditText) t);
                        editText.setText(stringBuilder.toString().trim());
                        editText.setSelection(editText.getText().length());
                    }
                }
            }
            return t;
        }

    }


    protected abstract View createView(AppActivity context, ViewGroup viewGroup, int viewType);

    protected abstract void bindView(T item, ViewHolder viewHolder, int position);

    public BaseRecyclerViewAdapter(AppActivity context) {
        this(context, null);
        sessionManager = new SessionManager(context);
        initImageLoader();
    }

    public BaseRecyclerViewAdapter(AppActivity context, OnViewHolderClick listener) {
        super();
        appActivity = context;
        mListener = listener;
        initImageLoader();
    }

    @Override
    public ViewHolder onCreateViewHolder(ViewGroup viewGroup, int viewType) {
        return new ViewHolder(createView(appActivity, viewGroup, viewType), mListener);
    }

    @Override
    public void onBindViewHolder(ViewHolder viewHolder, int position) {
        bindView(getItem(position), viewHolder, position);
    }

    @Override
    public int getItemCount() {
        return mList.size();
    }

    public T getItem(int index) {
        return ((mList != null && index < mList.size()) ? mList.get(index) : null);
    }

    public Context getContext() {
        return appActivity;
    }

    public void setList(List<T> list) {
        mList = list;
    }

    public List<T> getList() {
        return mList;
    }

    public void setClickListener(OnViewHolderClick listener) {
        mListener = listener;
    }


    public void initImageLoader() {
        options = new DisplayImageOptions.Builder()
                .showImageOnLoading(R.drawable.placeholder_user)
                .showImageForEmptyUri(R.drawable.placeholder_user)
                .showImageOnFail(R.drawable.placeholder_user)
                .cacheInMemory(true)
                .cacheOnDisk(true)
                .considerExifParams(true)
                .bitmapConfig(Bitmap.Config.RGB_565)
                .build();

        optionsMediumRounded = new DisplayImageOptions.Builder()
                .showImageOnLoading(R.drawable.placeholder_user)
                .showImageForEmptyUri(R.drawable.placeholder_user)
                .showImageOnFail(R.drawable.placeholder_user)
                .cacheInMemory(true)
                .cacheOnDisk(true)
                .considerExifParams(true)
                .displayer(new RoundedBitmapDisplayer(20))
                .bitmapConfig(Bitmap.Config.RGB_565)
                .build();

        optionsMediumNoStub = new DisplayImageOptions.Builder()
                .cacheInMemory(true)
                .cacheOnDisk(true)
                .considerExifParams(true)
                .bitmapConfig(Bitmap.Config.RGB_565)
                .build();

        imageLoader = ImageLoader.getInstance();
    }

    public void updateList(List<T> newData) {
        mList = newData;
        notifyDataSetChanged();
    }

    public void add(T item) {
        mList.add(item);
        notifyDataSetChanged();
    }

    public void addList(List<T> items) {
        mList.addAll(0, items);
        notifyDataSetChanged();
    }

    public void remove(T item) {
        mList.remove(item);
        notifyDataSetChanged();
    }
}
