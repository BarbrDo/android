package com.barbrdo.app.fragments;

import android.app.Activity;
import android.graphics.Bitmap;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentTransaction;
import android.view.LayoutInflater;
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

/**
 * Created by abhinavr on 23/9/16.
 */

public abstract class BaseFragment extends Fragment {

    private int mLayoutXml;
    private View mView;
    private AppActivity mAppActivity;
    public ImageLoader imageLoader;
    public DisplayImageOptions options;
    public SessionManager sessionManager;

    public void onCreate(@Nullable Bundle savedInstanceState, int layoutXml) {
        super.onCreate(savedInstanceState);
        this.mLayoutXml = layoutXml;
    }

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        this.mView = inflater.inflate(mLayoutXml, container, false);
        this.mView.setClickable(true);
        this.mAppActivity = (AppActivity) getActivity();
        sessionManager = new SessionManager(mAppActivity);
        initImageLoader();
        initView();
        bindControls();
        return this.mView;
    }


    /**
     * Get current view
     *
     * @return
     */
    public View getView() {
        return mView;
    }

    /**
     * Init view ids
     */
    protected abstract void initView();

    /**
     * Bind view data into view
     */
    protected abstract void bindControls();

    /**
     * Get Parent activity
     *
     * @return
     */
    public AppActivity getBaseActivity() {
        return mAppActivity;
    }

    /**
     * Set TextView value from activity
     * @param id
     * @param text
     * @return
     */
    /**
     * Get view type and set text
     *
     * @param id
     * @param text
     * @return
     */
    public <T extends View> T getView(int id, String... text) {
        T t = (T) mView.findViewById(id);
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

    /**
     * Get value from Edittext
     *
     * @param id
     * @return
     */
    public String getTextFromTextView(int id) {
        return ((TextView) this.mView.findViewById(id)).getText().toString().trim();
    }

    public void addFragment(Fragment fragment) {
        FragmentManager fragmentManager = getBaseActivity().getSupportFragmentManager();
        FragmentTransaction ft = fragmentManager.beginTransaction();
        ft.replace(R.id.content_frame, fragment);
        ft.commit();
    }

    public void addFragmentToBackStack(Fragment fragment) {
        FragmentManager fragmentManager = getBaseActivity().getSupportFragmentManager();
        FragmentTransaction ft = fragmentManager.beginTransaction();
        ft.replace(R.id.content_frame, fragment);
        ft.addToBackStack(fragment.getClass().getName());
        ft.commit();
    }

    @Override
    public void onAttach(Activity activity) {
        super.onAttach(activity);
        mAppActivity = (AppActivity) activity;
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

        imageLoader = ImageLoader.getInstance();
    }
}
