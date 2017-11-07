/**
 * @author Anurag Sethi <anurags@smartdatainc.net>
 * @version 1.0.0
 * @since 2014-07-28
 */
package com.barbrdo.app.helpers;

import android.app.Dialog;
import android.content.Context;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.os.Build;
import android.view.Gravity;
import android.view.View;
import android.view.WindowManager;
import android.widget.ImageView;

import com.barbrdo.app.R;

/**
 * Created by Anurag Sethi on 08-04-2015.
 * The class handles the project dialog operations
 */

public class TransparentProgressDialog extends Dialog {

    @SuppressWarnings("unused")
    private ImageView iv;

    /**
     * Constructor
     *
     * @param context The Context from which the dialog is called
     * @return none
     * @since 2014-07-28
     */
    public TransparentProgressDialog(Context context) {
        super(context);
        WindowManager.LayoutParams wlmp = getWindow().getAttributes();
        wlmp.gravity = Gravity.CENTER_HORIZONTAL;
        getWindow().setAttributes(wlmp);
        getWindow().setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));
        if (Build.VERSION.SDK_INT < 21) {
            int divierId = getContext().getResources()
                    .getIdentifier("android:id/titleDivider", null, null);
            View divider = findViewById(divierId);
            divider.setBackgroundColor(Color.TRANSPARENT);
        }
        setTitle(null);
        setCancelable(false);
        setOnCancelListener(null);
        setContentView(R.layout.progresslayout);
    }

    /**
     * Start the dialog and display it on screen.
     *
     * @return none
     * @since 2014-07-28
     */
    @Override
    public void show() {
        super.show();

    }

    /**
     * Dismiss the dialog
     *
     * @return none
     * @since 2014-07-28
     */
    @Override
    public void dismiss() {
        super.dismiss();

    }
}