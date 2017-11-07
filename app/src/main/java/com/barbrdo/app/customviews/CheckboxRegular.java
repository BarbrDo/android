package com.barbrdo.app.customviews;

import android.content.Context;
import android.graphics.Typeface;
import android.util.AttributeSet;
import android.widget.Button;
import android.widget.CheckBox;

import com.barbrdo.app.R;


public class CheckboxRegular extends CheckBox {

    public CheckboxRegular(Context context, AttributeSet attrs, int defStyle) {
        super(context, attrs, defStyle);
        init();
    }

    public CheckboxRegular(Context context, AttributeSet attrs) {
        super(context, attrs);
        init();
    }

    public CheckboxRegular(Context context) {
        super(context);
        init();
    }

    private void init() {
        Typeface tf = Typeface.createFromAsset(getContext().getAssets(),
                getContext().getString(R.string.berlin_sans_fb_regular));
        setTypeface(tf, Typeface.NORMAL);
    }
}
