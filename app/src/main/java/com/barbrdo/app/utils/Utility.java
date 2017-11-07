package com.barbrdo.app.utils;

import android.app.Activity;
import android.content.Context;
import android.content.DialogInterface;
import android.content.SharedPreferences;
import android.content.res.Resources;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Matrix;
import android.media.ExifInterface;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.os.IBinder;
import android.support.v7.app.AlertDialog;
import android.text.Spannable;
import android.text.SpannableString;
import android.text.format.DateUtils;
import android.text.style.ImageSpan;
import android.util.TypedValue;
import android.view.Gravity;
import android.view.Window;
import android.view.inputmethod.InputMethodManager;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import com.barbrdo.app.R;
import com.barbrdo.app.helpers.TransparentProgressDialog;
import com.barbrdo.app.interfaces.DialogActionCallback;
import com.google.gson.Gson;
import com.google.i18n.phonenumbers.NumberParseException;
import com.google.i18n.phonenumbers.PhoneNumberUtil;
import com.google.i18n.phonenumbers.Phonenumber;

import org.json.JSONArray;
import org.json.JSONException;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.text.DecimalFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class Utility {

    Context context;
    TransparentProgressDialog progressDialogObj;

    /**
     * Constructor
     *
     * @param contextObj The Context from where the method is called
     * @return none
     */
    public Utility(Context contextObj) {
        context = contextObj;
    }

    /**
     * The method validates email address
     *
     * @param email email address to validate
     * @return true if the email entered is valid
     */
    public boolean checkEmail(String email) {
        Pattern pattern;
        Matcher matcher;
        final String EMAIL_PATTERN = "^[_A-Za-z0-9-]+(\\.[_A-Za-z0-9-]+)*@[A-Za-z0-9]+(\\.[A-Za-z0-9]+)*(\\.[A-Za-z]{2,})$";
        pattern = Pattern.compile(EMAIL_PATTERN);
        matcher = pattern.matcher(email);
        return matcher.matches();
    }

    /**
     * This method will convert object to Json String
     *
     * @param obj Object whose data needs to be converted into JSON String
     * @return object data in JSONString
     */
    public String convertObjectToJson(Object obj) {
        Gson gson = new Gson();
        return gson.toJson(obj);
    }

    /**
     * The method will start the loader till the AsynTask completes the assigned
     * task
     *
     * @param context The Context from where the method is called
     *                displayed as loader
     * @return none
     * @since 2014-08-20
     */
    public void startLoader(Context context) {
        if (progressDialogObj == null) {
            progressDialogObj = new TransparentProgressDialog(this.context);
            progressDialogObj.show();
        }
    }

    /**
     * The method will start the loader till the AsynTask completes the assigned
     * task
     *
     * @return none
     * @since 2014-08-20
     */
    public void stopLoader() {
        try {
            if ((progressDialogObj != null) && progressDialogObj.isShowing()) {
                progressDialogObj.dismiss();
            }
        } catch (final Exception e) {
            // Handle or log or ignore
        } finally {
            progressDialogObj = null;
        }
    }

    /**
     * Show a non-cancelable dialog box with a message, a positive and a
     * negative button.
     *
     * @param context               The context to show this dialog box. Can't be null.
     * @param titleStringId         A valid resource id of the text to be shown in the title bar
     *                              of dialog box. If you don't want to show a title, just pass -1
     *                              here.
     * @param messageStringId       A valid resource id of the message to display.
     * @param positiveButtonLabelId A valid resource id of the text to show on positive button.If you don't want to show a positiveButtonLabelId, just pass 0
     *                              here.
     * @param negativeButtonLabelId A valid resource id of the string to show on negative button.If you don't want to show a negativeButtonLabelId, just pass 0
     *                              here.
     * @param actionCallback        Callback interface for the positive and negative buttons for
     *                              if you want to perform some action on button clicks. Can be
     *                              null.
     */
    public static void showAlertDialog(Context context, int titleStringId, int messageStringId, int positiveButtonLabelId,
                                       int negativeButtonLabelId, final DialogActionCallback actionCallback) throws Resources.NotFoundException {


        if ((context == null) || (context.getString(messageStringId) == null || context.getString(messageStringId).trim().isEmpty())) {

            return;
        }

        String title = null;
        String message = context.getString(messageStringId);
        String positiveButtonLabel = null;
        String negativeButtonLabel = null;
        if (titleStringId > 0) {
            title = context.getString(titleStringId);
        }

        if (positiveButtonLabelId > 0) {
            positiveButtonLabel = context.getString(positiveButtonLabelId);
        }
        if (negativeButtonLabelId > 0) {
            negativeButtonLabel = context.getString(negativeButtonLabelId);
        }


        if ((context == null) || (message == null || message.trim().isEmpty())) {
            return;
        }

        AlertDialog.Builder alertDialog = new AlertDialog.Builder(context, R.style.AppCompatAlertDialogStyle);

        if (title != null && !title.trim().isEmpty()) {
            alertDialog.setTitle(title);
        }

        alertDialog.setCancelable(false);
        alertDialog.setMessage(message);
        alertDialog.setPositiveButton(positiveButtonLabel, new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int whichButton) {

                if (actionCallback != null) {
                    actionCallback.doOnPositive();
                }
            }

        });

        alertDialog.setNegativeButton(negativeButtonLabel, new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                if (actionCallback != null) {
                    actionCallback.doOnNegative();
                }
            }
        });

        if (!((Activity) context).isFinishing()) {
            alertDialog.show();
        }
    }

    public static void showAlertDialog(Context context, String title, String message, String positiveButtonLabel,
                                       String negativeButtonLabel, final DialogActionCallback actionCallback) throws Resources.NotFoundException {

        if ((context == null) || (message == null || message.trim().isEmpty())) {
            return;
        }

        AlertDialog.Builder alertDialog = new AlertDialog.Builder(context, R.style.AppCompatAlertDialogStyle);

        if (title != null && !title.trim().isEmpty()) {
            alertDialog.setTitle(title);
        }

        alertDialog.setCancelable(false);
        alertDialog.setMessage(message);
        alertDialog.setPositiveButton(positiveButtonLabel, new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int whichButton) {

                if (actionCallback != null) {
                    actionCallback.doOnPositive();
                }
            }

        });

        alertDialog.setNegativeButton(negativeButtonLabel, new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                if (actionCallback != null) {
                    actionCallback.doOnNegative();
                }
            }
        });

        if (!((Activity) context).isFinishing()) {
            alertDialog.show();
        }
    }

    public static void showAlertDialogNoTitle(Context context, String message, String positiveButtonLabel,
                                       String negativeButtonLabel, final DialogActionCallback actionCallback) throws Resources.NotFoundException {

        if ((context == null) || (message == null || message.trim().isEmpty())) {
            return;
        }

        AlertDialog.Builder alertDialog = new AlertDialog.Builder(context, R.style.AppCompatAlertDialogNoTitle);
        alertDialog.setCancelable(false);
        alertDialog.setMessage(message);
        alertDialog.setPositiveButton(positiveButtonLabel, new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int whichButton) {

                if (actionCallback != null) {
                    actionCallback.doOnPositive();
                }
            }

        });

        alertDialog.setNegativeButton(negativeButtonLabel, new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                if (actionCallback != null) {
                    actionCallback.doOnNegative();
                }
            }
        });

        if (!((Activity) context).isFinishing()) {
            alertDialog.show();
        }
    }


    /**
     * The method will return the date and time in requested format
     *
     * @param selectedDateTime to be converted to requested format
     * @param requestedFormat  the format in which the provided datetime needs to
     *                         be changed
     * @param formatString     differentiate parameter to format date or time
     * @return formated date or time
     */
    public String formatDateTime(String selectedDateTime, String requestedFormat, String formatString) {

        if (formatString.equalsIgnoreCase("time")) {
            SimpleDateFormat ft = new SimpleDateFormat("hh:mm");
            Date dateObj = null;

            try {
                dateObj = ft.parse(selectedDateTime);
            } catch (ParseException e) {
                // TODO Auto-generated catch block
                e.printStackTrace();
            }

            long millis = dateObj.getTime();
            SimpleDateFormat simpleDateFormatObj = new SimpleDateFormat(requestedFormat);
            return simpleDateFormatObj.format(millis);

        }//if
        else if (formatString.equalsIgnoreCase("date")) {
            SimpleDateFormat ft = new SimpleDateFormat("yyyy-mm-dd");
            Date dateObj = null;

            try {
                dateObj = ft.parse(selectedDateTime);

            } catch (ParseException e) {
                // TODO Auto-generated catch block
                e.printStackTrace();
            }

            SimpleDateFormat simpleDateFormatObj = new SimpleDateFormat(requestedFormat);
            return simpleDateFormatObj.format(dateObj);

        }
        return null;

    }

    /**
     * The method will return current time
     *
     * @return current time
     */
    public String getCurrentTime() {
        Calendar c = Calendar.getInstance();
        SimpleDateFormat sdf = new SimpleDateFormat("HH:mm:ss a");
        String currentTime = sdf.format(c.getTime());

        return currentTime;
    }

    /**
     * The method will return current date
     *
     * @return current date
     */
    public String getCurrentDate() {
        Calendar c = Calendar.getInstance();
        int day = c.get(Calendar.DAY_OF_MONTH);
        int month = c.get(Calendar.MONTH) + 1;
        int year = c.get(Calendar.YEAR);
        String currentDate = Integer.toString(day) + "-" + Integer.toString(month) + "-" + Integer.toString(year);
        return currentDate;
    }

    /**
     * The method show the error
     *
     * @param message     message to be displayed
     * @param editTextObj object of the editText containing the error
     */
    public void showError(String message, EditText editTextObj) {
        if (message == null || message.equalsIgnoreCase("")) {
            editTextObj.setError(null);
        } else {
            editTextObj.setError(message);
            editTextObj.requestFocus();
        }
    }

    /**
     * This method will convert json String to ArrayList
     *
     * @param jsonString string which needs to be converted to ArrayList
     * @return ArrayList of type String
     * @throws JSONException
     * @since 2014-08-13
     */
    private ArrayList<String> convertJsonStringToArray(String jsonString) throws JSONException {

        ArrayList<String> stringArray = new ArrayList<String>();

        JSONArray jsonArray = new JSONArray(jsonString);
        int jsonArrayLength = jsonArray.length();
        for (int i = 0; i < jsonArrayLength; i++) {
            stringArray.add(jsonArray.getString(i));
        }

        return stringArray;
    }

    /**
     * The method will save the data in shared preferences defined by
     * "sharedPrefName" and the key provided by "key" parameter
     *
     * @param sharedPrefName name of the container
     * @param mode           private
     * @param key            name of the key in which values are saved
     * @param value          data to be saved associated to the particular key
     * @return none
     * @since 2014-08-13
     */
    public void saveDataInSharedPreferences(String sharedPrefName, int mode, String key, String value) {
        SharedPreferences prefsObj = context.getSharedPreferences(sharedPrefName, mode);
        SharedPreferences.Editor editorObj = prefsObj.edit();
        editorObj.putString(key, value);
        editorObj.commit();
    }

    /**
     * The method will read the data in shared preferences defined by
     * "sharedPrefName" and the key provided by "key" parameter
     *
     * @param sharedPrefName name of the container
     * @param mode           private
     * @param key            name of the key in which values are saved
     * @return String
     */
    public String readDataInSharedPreferences(String sharedPrefName, int mode, String key) {
        SharedPreferences prefsObj = context.getSharedPreferences(sharedPrefName, mode);
        return prefsObj.getString(key, "");
    }

    /**
     * The method will remove the data stored in shared preferences defined by
     * "sharedPrefName" and the key provided by "key" parameter
     *
     * @param sharedPrefName name of the container
     * @param mode           private
     * @param key            name of the key in which values are saved
     * @param removeAll      if true will remove all the values stored in shared
     *                       preferences else remove as specified by key
     */
    public void removeKeyFromSharedPreferences(String sharedPrefName, int mode, String key, boolean removeAll) {

        SharedPreferences prefsObj = context.getSharedPreferences(sharedPrefName, mode);
        SharedPreferences.Editor editorObj = prefsObj.edit();
        if (removeAll) {
            editorObj.clear();
        } else {
            editorObj.remove(key);
        }
        editorObj.commit();
    }

    /**
     * show message to user using showToast
     *
     * @param mContext                   contains context of application
     * @param message                    contains text/message to show
     * @param durationForMessageToAppear 1 will show the message for short
     *                                   duration else long duration
     */
    public void showToast(Context mContext, String message, int durationForMessageToAppear) {
        Toast toast;
        if (durationForMessageToAppear == 1) {
            toast = Toast.makeText(mContext, message, Toast.LENGTH_SHORT);
        } else {
            toast = Toast.makeText(mContext, message, Toast.LENGTH_LONG);
        }
        toast.setGravity(Gravity.CENTER, 0, 0);
        toast.show();
    }

    /**
     * This method will hide virtual keyboard if opened
     *
     * @param mContext contains context of application
     */
    public void hideVirtualKeyboard(Context mContext) {

        try {

            IBinder binder = ((Activity) mContext).getWindow().getCurrentFocus()
                    .getWindowToken();

            if (binder != null) {
                InputMethodManager imm = (InputMethodManager) mContext.getSystemService(Context.INPUT_METHOD_SERVICE);
                imm.hideSoftInputFromWindow(binder, 0);
            }

        } catch (Exception e) {

            e.printStackTrace();

        }

    }

    /**
     * This method will show virtual keyboard where ever required
     *
     * @param mContext contains context of application
     */
    public void showVirtualKeyboard(Context mContext) {
        InputMethodManager imm = (InputMethodManager) mContext.getSystemService(Context.INPUT_METHOD_SERVICE);
        imm.toggleSoftInput(InputMethodManager.SHOW_FORCED, 0);
    }

    /**
     * Format value up to 2 decimal places
     *
     * @param num - number to be formatted
     */
    public float formatValueUpTo2Decimals(double num) {

        try {

            DecimalFormat df = new DecimalFormat("#.##");

            String value = df.format(num);
            String decimalPlace = ",";

            if (value.contains(decimalPlace)) {
                value = value.replace(decimalPlace, ".");
            }

            return Float.parseFloat(value);

        } catch (Exception e) {
            e.printStackTrace();
        }

        return (float) num;

    }

    public String getMyDateFormat(String date, String currentFormat, String format) {
        String finalDate;
        SimpleDateFormat curFormater = new SimpleDateFormat(currentFormat);
        Date dateObj = null;
        try {
            dateObj = curFormater.parse(date);
        } catch (ParseException e) {
            e.printStackTrace();
        }
        SimpleDateFormat postFormater;
        postFormater = new SimpleDateFormat(format);
        if (dateObj != null)
            finalDate = postFormater.format(dateObj);
        else
            finalDate = "";
        return finalDate;
    }

    public String getCurrentDateToString() {
        Calendar c = Calendar.getInstance();
        SimpleDateFormat df = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
        return df.format(c.getTime());
    }

    public String getCurrentDateToString(String format) {
        Calendar c = Calendar.getInstance();
        SimpleDateFormat df = new SimpleDateFormat(format);
        return df.format(c.getTime());
    }

    public String convertDateObjToStringDate(Date dateObj) {
        String finalDate;
        SimpleDateFormat postFormater;
        postFormater = new SimpleDateFormat("yyyy-MM-dd");
        if (dateObj != null)
            finalDate = postFormater.format(dateObj);
        else
            finalDate = "";
        return finalDate;
    }

    public boolean isDateBetween(String minDate, String maxDate, String currentDate) {
        Date dateMin = stringToDate(minDate, Constants.DateFormat.YYYYMMDDTHHMMSS);
        Date dateMax = stringToDate(maxDate, Constants.DateFormat.YYYYMMDDTHHMMSS);
        Date dateCurrent = stringToDate(currentDate, Constants.DateFormat.YYYYMMDD);

        return (dateCurrent.after(dateMin) || dateCurrent.equals(dateMin)) && (dateCurrent.before(dateMax) || dateCurrent.equals(dateMax));
    }

    public long getDateMillis(String strDate) {
        SimpleDateFormat sdf = new SimpleDateFormat(Constants.DateFormat.YYYYMMDDTHHMMSS);
        Date date = null;
        try {
            date = sdf.parse(strDate);
        } catch (ParseException e) {
            e.printStackTrace();
        }
        return date.getTime();
    }

    public long getDateMillisAddMin(String strDate, int minutes) {
        final long ONE_MINUTE_IN_MILLIS = 60000;
        SimpleDateFormat sdf = new SimpleDateFormat(Constants.DateFormat.YYYYMMDDTHHMMSS);
        Date date = null;
        try {
            date = sdf.parse(strDate);
        } catch (ParseException e) {
            e.printStackTrace();
        }
        Date afterAddingMin = new Date(date.getTime() + (minutes * ONE_MINUTE_IN_MILLIS));
        return afterAddingMin.getTime();
    }

    public String convertDateObjToStringDate(Date dateObj, String format) {
        String finalDate;
        SimpleDateFormat postFormater;
        postFormater = new SimpleDateFormat(format);
        if (dateObj != null)
            finalDate = postFormater.format(dateObj);
        else
            finalDate = "";
        return finalDate;
    }

    public int dpToPx(float v, Context context) {
        Resources r = context.getResources();
        return (int) TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_DIP, v,
                r.getDisplayMetrics());
    }

    public Bitmap decodeSampledBitmapFromResource(File file, int reqWidth, int reqHeight) {
        // First decode with inJustDecodeBounds=true to check dimensions
        final BitmapFactory.Options options = new BitmapFactory.Options();
        options.inJustDecodeBounds = true;
        BitmapFactory.decodeFile(file.getAbsolutePath(), options);

        // Calculate inSampleSize
        options.inSampleSize = calculateInSampleSize(options, reqWidth, reqHeight);

        // Decode bitmap with inSampleSize set
        options.inJustDecodeBounds = false;
        return BitmapFactory.decodeFile(file.getAbsolutePath(), options);
    }

    public int calculateInSampleSize(
            BitmapFactory.Options options, int reqWidth, int reqHeight) {
        // Raw height and width of image
        final int height = options.outHeight;
        final int width = options.outWidth;
        int inSampleSize = 1;

        if (height > reqHeight || width > reqWidth) {

            final int halfHeight = height / 2;
            final int halfWidth = width / 2;

            // Calculate the largest inSampleSize value that is a power of 2 and keeps both
            // height and width larger than the requested height and width.
            while ((halfHeight / inSampleSize) > reqHeight
                    && (halfWidth / inSampleSize) > reqWidth) {
                inSampleSize *= 2;
            }
        }
        return inSampleSize;
    }

    public String getTimeAgo(String dateFormat, String dateTime) {
        SimpleDateFormat df = new SimpleDateFormat(dateFormat);
        Date date = null;
        try {
            date = df.parse(dateTime);
            long epoch = date.getTime();
            return DateUtils.getRelativeTimeSpanString(epoch, System.currentTimeMillis(), DateUtils.SECOND_IN_MILLIS).toString();
        } catch (ParseException e) {
            e.printStackTrace();
        }
        return "";
    }

    public int compareDates(String d1, String d2, String format) {
        try {
            SimpleDateFormat sdf = new SimpleDateFormat(format);
            Date date1 = sdf.parse(d1);
            Date date2 = sdf.parse(d2);
            if (date1.after(date2)) {
                return -1;
            }
            if (date1.before(date2)) {
                return 1;
            }
            if (date1.equals(date2)) {
                return 0;
            }
        } catch (ParseException ex) {
            ex.printStackTrace();
        }
        return 0;
    }

    public int compareDates(Date date1, Date date2) {
        if (date1.after(date2)) {
            return 1;
        } else if (date1.before(date2)) {
            return -1;
        } else if (date1.equals(date2)) {
            return 0;
        }
        return 0;
    }

    public Date stringToDate(String dateStr, String format) {
        SimpleDateFormat formatter = new SimpleDateFormat(format);
        try {
            return formatter.parse(dateStr);
        } catch (ParseException e) {
            e.printStackTrace();
        }
        return null;
    }

    public long timeDifference(String time1, String time2) {
        SimpleDateFormat format = new SimpleDateFormat(Constants.DateFormat.YYYYMMDDTHHMMSS);

        Date d1 = null;
        Date d2 = null;
        try {
            d1 = format.parse(time1);
            d2 = format.parse(time2);
        } catch (ParseException e) {
            e.printStackTrace();
        }
        return d2.getTime() - d1.getTime();
    }

    public String getDayOfMonthSuffix(final int n) {
        if (n >= 11 && n <= 13) {
            return "th";
        }
        switch (n % 10) {
            case 1:
                return "st";
            case 2:
                return "nd";
            case 3:
                return "rd";
            default:
                return "th";
        }
    }

    public String getCurrentDay() {
        SimpleDateFormat sdf = new SimpleDateFormat("EEEE");
        Date d = new Date();
        return sdf.format(d);
    }

    public Object getObjectFromJsonString(String json, Class<?> classs) {
        Gson gson = new Gson();
        return gson.fromJson(json, classs);
    }

    public String formatPhoneNumber(String number) {
        String formatNumber = "";
        try {
            PhoneNumberUtil phoneUtil = PhoneNumberUtil.getInstance();
            Phonenumber.PhoneNumber phoneNumber = phoneUtil.parse(number, "US");
            formatNumber = phoneUtil.format(phoneNumber, PhoneNumberUtil.PhoneNumberFormat.NATIONAL);

        } catch (NumberParseException e) {
            e.printStackTrace();
        }
        return formatNumber;
    }

    public String removePhoneNumberFormatting(String number) {
        String phoneNumber = "";
        Pattern p = Pattern.compile("\\d+");
        Matcher m = p.matcher(number); //editText.getText().toString()
        while (m.find()) {
            phoneNumber = phoneNumber + m.group(0);
        }
        return phoneNumber;
    }

    public void setSpannableTextView(TextView textView, int icon, String text) {
        Spannable buttonLabel = new SpannableString("   " + text);
        buttonLabel.setSpan(new ImageSpan(context, icon,
                ImageSpan.ALIGN_BOTTOM), 0, 1, Spannable.SPAN_EXCLUSIVE_EXCLUSIVE);
        textView.setText(buttonLabel);
    }

    public void setSpannableTextViewRight(TextView textView, int icon, String text) {
        Spannable buttonLabel = new SpannableString(text + "   ");
        buttonLabel.setSpan(new ImageSpan(context, icon,
                ImageSpan.ALIGN_BOTTOM), text.length() + 2, text.length() + 3, Spannable.SPAN_EXCLUSIVE_EXCLUSIVE);
        textView.setText(buttonLabel);
    }

    public String formatDecimal(float decimal) {
        DecimalFormat decimalFormat = new DecimalFormat();
        decimalFormat.setDecimalSeparatorAlwaysShown(false);
        return decimalFormat.format(decimal);
    }

    public File saveBitmapToFile(File file) {
        int orientation;
        try {
            BitmapFactory.Options o = new BitmapFactory.Options();
            o.inJustDecodeBounds = true;
            final int REQUIRED_SIZE = 70;
            int width_tmp = o.outWidth, height_tmp = o.outHeight;
            int scale = 0;
            while (true) {
                if (width_tmp / 2 < REQUIRED_SIZE
                        || height_tmp / 2 < REQUIRED_SIZE)
                    break;
                width_tmp /= 2;
                height_tmp /= 2;
                scale++;
            }
            BitmapFactory.Options o2 = new BitmapFactory.Options();
            o2.inSampleSize = 6;
            Bitmap bm = BitmapFactory.decodeFile(file.getAbsolutePath(), o2);
            Bitmap bitmap = bm;
            ExifInterface exif = new ExifInterface(file.getAbsolutePath());
            orientation = exif.getAttributeInt(ExifInterface.TAG_ORIENTATION, 1);
            Matrix m = new Matrix();
            if ((orientation == ExifInterface.ORIENTATION_ROTATE_180)) {
                m.postRotate(180);
                bitmap = Bitmap.createBitmap(bm, 0, 0, bm.getWidth(),
                        bm.getHeight(), m, true);
            } else if (orientation == ExifInterface.ORIENTATION_ROTATE_90) {
                m.postRotate(90);
                bitmap = Bitmap.createBitmap(bm, 0, 0, bm.getWidth(),
                        bm.getHeight(), m, true);
            } else if (orientation == ExifInterface.ORIENTATION_ROTATE_270) {
                m.postRotate(270);
                bitmap = Bitmap.createBitmap(bm, 0, 0, bm.getWidth(),
                        bm.getHeight(), m, true);
            }

            file.createNewFile();
            FileOutputStream outputStream = new FileOutputStream(file);
            bitmap.compress(Bitmap.CompressFormat.JPEG, 100, outputStream);
            return file;
        } catch (Exception e) {
            return null;
        }
    }

    private Bitmap rotateImageIfRequired(Bitmap img, File file) throws IOException {

        ExifInterface ei = new ExifInterface(file.getPath());
        int orientation = ei.getAttributeInt(ExifInterface.TAG_ORIENTATION, ExifInterface.ORIENTATION_NORMAL);

        switch (orientation) {
            case ExifInterface.ORIENTATION_ROTATE_90:
                return rotateImage(img, 90);
            case ExifInterface.ORIENTATION_ROTATE_180:
                return rotateImage(img, 180);
            case ExifInterface.ORIENTATION_ROTATE_270:
                return rotateImage(img, 270);
            default:
                return img;
        }
    }

    private Bitmap rotateImage(Bitmap img, int degree) {
        Matrix matrix = new Matrix();
        matrix.postRotate(degree);
        Bitmap rotatedImg = Bitmap.createBitmap(img, 0, 0, img.getWidth(), img.getHeight(), matrix, true);
        img.recycle();
        return rotatedImg;
    }

    public boolean isNetworkAvailable(Context context) {
        ConnectivityManager connectivityManager
                = (ConnectivityManager) context.getSystemService(Context.CONNECTIVITY_SERVICE);
        NetworkInfo activeNetworkInfo = connectivityManager.getActiveNetworkInfo();
        return activeNetworkInfo != null && activeNetworkInfo.isConnectedOrConnecting();
    }
}