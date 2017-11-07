package com.barbrdo.app.activities;

import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.Typeface;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.support.design.widget.Snackbar;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentTransaction;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.View;
import android.view.ViewGroup;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;

import com.barbrdo.app.R;
import com.barbrdo.app.dataobject.AppInstance;
import com.barbrdo.app.helpers.TransparentProgressDialog;
import com.barbrdo.app.interfaces.DialogActionCallback;
import com.barbrdo.app.utils.SessionManager;
import com.barbrdo.app.utils.Utility;
import com.nostra13.universalimageloader.core.DisplayImageOptions;
import com.nostra13.universalimageloader.core.ImageLoader;
import com.nostra13.universalimageloader.core.display.RoundedBitmapDisplayer;


/**
 * Created by Anurag Sethi
 * The class will be extended by all the other activities and will contain the code
 * which is common for all the activities
 */
public abstract class AppActivity extends AppCompatActivity {

    protected Context mContext;
    public TransparentProgressDialog progressDialogObj;
    public Toolbar mToolbar;
    public Utility utilObj;
    public SessionManager sessionManager;
    public ImageLoader imageLoader;
    public DisplayImageOptions options;
    public DisplayImageOptions optionsMediumRounded;
    public DisplayImageOptions optionsMediumNoStub;
    public DisplayImageOptions optionsMediumNoStubNoRounded;
    public AppInstance appInstance;
    protected Typeface mTfRegular;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
            getWindow().addFlags(WindowManager.LayoutParams.FLAG_DRAWS_SYSTEM_BAR_BACKGROUNDS);
        }

        mTfRegular = Typeface.createFromAsset(getAssets(), "Berlin_Sans_FB_Regular.ttf");
        mContext = AppActivity.this;
        utilObj = new Utility(mContext);
        sessionManager = new SessionManager(mContext);
        appInstance = AppInstance.getInstance();
        initImageLoader();
    }

    /**
     * Initializes the objects
     *
     * @return none
     */
    abstract void initData();

    /**
     * Binds the UI controls
     *
     * @return none
     */
    abstract void bindControls();

    /**
     * Get view type and set text
     *
     * @param id
     * @param text
     * @return
     */
    public <T extends View> T getView(int id, String... text) {
        T t = (T) findViewById(id);
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
     * The method will return the network availability
     *
     * @param context context of the activity from which the method is called
     * @return true if network is available else false
     */
    public boolean isNetworkAvailable(Context context) {
        return utilObj.isNetworkAvailable(context);
    }

    /**
     * Show snackbar
     *
     * @param message
     */
    public void showSnackBar(String message) {
        Snackbar snackbar = Snackbar.make(((ViewGroup) this.findViewById(android.R.id.content)).getChildAt(0), message, Snackbar.LENGTH_LONG);
        View snackBarView = snackbar.getView();
        TextView textView = (TextView) snackBarView.findViewById(android.support.design.R.id.snackbar_text);
        textView.setMaxLines(5);
        snackbar.show();
    }

    public void showSnackBar(final int mainTextStringId, final int actionStringId,
                             View.OnClickListener listener) {
        Snackbar.make(
                findViewById(android.R.id.content),
                getString(mainTextStringId),
                Snackbar.LENGTH_INDEFINITE)
                .setAction(getString(actionStringId), listener).show();
    }


    /**
     * Get value from Edittext
     *
     * @param id
     * @return
     */
    public String getStringFromView(int id) {
        return ((TextView) findViewById(id)).getText().toString().trim();
    }

    /**
     * Fragment add and replace
     *
     * @param fragment
     */
    public void addFragment(Fragment fragment) {
        FragmentManager fragmentManager = getSupportFragmentManager();
        FragmentTransaction ft = fragmentManager.beginTransaction();
        ft.replace(R.id.content_frame, fragment);
        ft.commit();
    }

    /**
     * Fragment add to back stack
     *
     * @param fragment
     */
    public void addToBackStackFragment(Fragment fragment) {
        FragmentManager fragmentManager = getSupportFragmentManager();
        FragmentTransaction ft = fragmentManager.beginTransaction();
        ft.replace(R.id.content_frame, fragment);
//        ft.addToBackStack(null);
        ft.commit();
    }

    /**
     * Start activity
     *
     * @param cls
     */
    public void startActivity(Class<?> cls) {
        Intent intent = new Intent(this, cls);
        startActivity(intent);
    }

    public void startActivity(Class<?> cls, Bundle bundle) {
        Intent intent = new Intent(this, cls);
        intent.putExtras(bundle);
        startActivity(intent);
    }

    public void startActivityForResult(Class<?> cls, Bundle bundle, int request) {
        Intent intent = new Intent(this, cls);
        if (bundle != null)
            intent.putExtras(bundle);
        startActivityForResult(intent, request);
    }

    public void setUpToolBar(String title) {
        mToolbar = getView(R.id.toolbar);
        setSupportActionBar(mToolbar);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setHomeButtonEnabled(true);
        getSupportActionBar().setTitle("");
    }

    public void setUpToolBarNoBack() {
        mToolbar = getView(R.id.toolbar);
        setSupportActionBar(mToolbar);
        getSupportActionBar().setDisplayHomeAsUpEnabled(false);
        getSupportActionBar().setHomeButtonEnabled(false);
        getSupportActionBar().setTitle("");
    }

    /**
     * Add custom toolbar
     */
    public void setCustomUpToolBar() {
        mToolbar = getView(R.id.toolbar);
        setSupportActionBar(mToolbar);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setHomeButtonEnabled(true);
        getSupportActionBar().setDisplayShowTitleEnabled(false);
    }

    public void clearAllActivitiesFromBackStack(Context context, Class<?> cls) {
        Intent i = new Intent(context, cls);
        i.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
        startActivity(i);
    }

    public void clearAllActivitiesFromBackStack(Context context, Class<?> cls, Bundle bundle) {
        Intent i = new Intent(context, cls);
        i.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
        i.putExtras(bundle);
        startActivity(i);
    }

    public void logout(Context context, Class<?> cls) {
        sessionManager.clearRememberMe();
        Intent i = new Intent(context, cls);
        i.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
        startActivity(i);
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

    public void openGoogleMap(Context context, float latitude, float longitude, String label, String address) {
//        Intent intent = new Intent(Intent.ACTION_VIEW, Uri.parse("geo:" + latitude + "," + longitude + "?q=" + latitude + "," + longitude + "(" + label + ")"));
        Uri mapUri = Uri.parse("geo:0,0?q=" + Uri.encode(address));
        Intent intent = new Intent(Intent.ACTION_VIEW, mapUri);
        context.startActivity(intent);
    }

//    public void addToCalendar(long beginTime, long endTime, String title, String location) {
//        if (Build.VERSION.SDK_INT >= 14) {
//            Intent intent = new Intent(Intent.ACTION_INSERT)
//                    .setData(CalendarContract.Events.CONTENT_URI)
//                    .putExtra(CalendarContract.EXTRA_EVENT_BEGIN_TIME, beginTime)
//                    .putExtra(CalendarContract.EXTRA_EVENT_END_TIME, endTime)
//                    .putExtra(CalendarContract.Events.TITLE, title)
//                    .putExtra(CalendarContract.Events.EVENT_LOCATION, location)
//                    .putExtra(CalendarContract.Events.AVAILABILITY, CalendarContract.Events.AVAILABILITY_BUSY);
//            startActivity(intent);
//        } else {
//            Intent intent = new Intent(Intent.ACTION_EDIT);
//            intent.setType("vnd.android.cursor.item/event");
//            intent.putExtra("beginTime", beginTime);
//            intent.putExtra("endTime", endTime);
//            intent.putExtra("allDay", true);
//            intent.putExtra("title", title);
//            startActivity(intent);
//        }
//    }

    public boolean checkNetworkConnection(Context context) {
        if (!utilObj.isNetworkAvailable(context)) {
            utilObj.showAlertDialog(this, R.string.network_service_message_title, R.string.network_service_message, R.string.ok, 0, new DialogActionCallback() {
                @Override
                public void doOnPositive() {
                    finish();
                }

                @Override
                public void doOnNegative() {
                }
            });
        }
        return utilObj.isNetworkAvailable(context);
    }
}
