package com.barbrdo.app.customviews;

import android.content.Context;
import android.graphics.Typeface;
import android.support.v7.widget.AppCompatRadioButton;
import android.util.AttributeSet;
import android.widget.CheckBox;
import android.widget.RadioButton;

import com.barbrdo.app.R;


public class RadioButtonRegular extends AppCompatRadioButton {

    public RadioButtonRegular(Context context, AttributeSet attrs, int defStyle) {
        super(context, attrs, defStyle);
        init();
    }

    public RadioButtonRegular(Context context, AttributeSet attrs) {
        super(context, attrs);
        init();
    }

    public RadioButtonRegular(Context context) {
        super(context);
        init();
    }

    private void init() {
        Typeface tf = Typeface.createFromAsset(getContext().getAssets(),
                getContext().getString(R.string.berlin_sans_fb_regular));
        setTypeface(tf, Typeface.NORMAL);
    }
}
