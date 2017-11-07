package com.barbrdo.app.customviews;

import android.content.Context;
import android.graphics.Typeface;
import android.util.AttributeSet;
import android.widget.AutoCompleteTextView;

import com.barbrdo.app.R;


public class AutoCompleteTextViewRegular extends AutoCompleteTextView {

    public AutoCompleteTextViewRegular(Context context, AttributeSet attrs, int defStyle) {
        super(context, attrs, defStyle);
        init();
    }

    public AutoCompleteTextViewRegular(Context context, AttributeSet attrs) {
        super(context, attrs);
        init();
    }

    public AutoCompleteTextViewRegular(Context context) {
        super(context);
        init();
    }

    private void init() {
        Typeface tf = Typeface.createFromAsset(getContext().getAssets(),
                getContext().getString(R.string.berlin_sans_fb_regular));
        setTypeface(tf, Typeface.NORMAL);
    }
}
