package com.barbrdo.app.activities;

import android.app.DatePickerDialog;
import android.content.Context;
import android.graphics.Color;
import android.os.Bundle;
import android.text.SpannableString;
import android.text.style.UnderlineSpan;
import android.view.MenuItem;
import android.view.View;
import android.widget.DatePicker;
import android.widget.TextView;

import com.barbrdo.app.R;
import com.barbrdo.app.dataobject.BarberFinance;
import com.barbrdo.app.dataobject.ShopFinance;
import com.barbrdo.app.interfaces.ServiceRedirection;
import com.barbrdo.app.managers.BarberManager;
import com.barbrdo.app.utils.Constants;
import com.github.mikephil.charting.charts.BarChart;
import com.github.mikephil.charting.components.XAxis;
import com.github.mikephil.charting.components.YAxis;
import com.github.mikephil.charting.data.BarData;
import com.github.mikephil.charting.data.BarDataSet;
import com.github.mikephil.charting.data.BarEntry;
import com.github.mikephil.charting.formatter.YAxisValueFormatter;

import java.text.DecimalFormat;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Collections;
import java.util.Date;
import java.util.List;
import java.util.Locale;

public class FinanceCenterActivity extends AppActivity implements ServiceRedirection, View.OnClickListener {

    private Context mContext;
    private TextView textViewTotalSales;
    private TextView textViewCurrentMonth;
    private TextView textViewCurrentWeek;
    private TextView textViewStartDate;
    private TextView textViewEndDate;
    private TextView textViewTotalSaleWeekly;
    private TextView textViewTotalAppointments;
    private DatePickerDialog datePickerDialog;
    private SimpleDateFormat dateFormatter;
    private BarChart mChart;
    private BarberManager barberManagerObj;
    private DecimalFormat decimalFormat;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_finance_center);
        mContext = FinanceCenterActivity.this;

        initData();
        bindControls();
    }

    @Override
    void initData() {
        setUpToolBar("");
        textViewTotalSales = getView(R.id.tv_total_sale);
        textViewCurrentMonth = getView(R.id.tv_current_month);
        textViewCurrentWeek = getView(R.id.tv_current_week);
        textViewStartDate = getView(R.id.tv_start_date);
        textViewEndDate = getView(R.id.tv_end_date);
        textViewTotalSaleWeekly = getView(R.id.tv_total_sale_week);
        textViewTotalAppointments = getView(R.id.tv_total_appointments);
        mChart = getView(R.id.chart);

        dateFormatter = new SimpleDateFormat(Constants.DateFormat.MMMMDDYYYY, Locale.ENGLISH);
        barberManagerObj = new BarberManager(this, this);

        decimalFormat = new DecimalFormat();
        decimalFormat.setDecimalSeparatorAlwaysShown(false);
    }

    @Override
    void bindControls() {
        mChart.setDrawBarShadow(false);
        mChart.setTouchEnabled(false);
        mChart.setDrawValueAboveBar(false);
        mChart.setMaxVisibleValueCount(60);
        mChart.setPinchZoom(false);
        mChart.setDrawGridBackground(false);
        mChart.getAxisRight().setDrawLabels(false);
        mChart.getLegend().setEnabled(false);
        mChart.setScaleEnabled(false);
        mChart.getAxisLeft().setDrawGridLines(false);
        mChart.getXAxis().setDrawGridLines(false);

        XAxis xAxis = mChart.getXAxis();
        xAxis.setPosition(XAxis.XAxisPosition.BOTTOM);
        xAxis.setTypeface(mTfRegular);
        xAxis.setDrawGridLines(false);

        YAxis leftAxis = mChart.getAxisLeft();
        leftAxis.setTypeface(mTfRegular);
        leftAxis.setPosition(YAxis.YAxisLabelPosition.OUTSIDE_CHART);
        leftAxis.setSpaceTop(15f);
        leftAxis.setValueFormatter(new YAxisValueFormatter() {
            @Override
            public String getFormattedValue(float value, YAxis yAxis) {
                return "$" + decimalFormat.format(value);
            }
        });

        textViewStartDate.setOnClickListener(this);
        textViewEndDate.setOnClickListener(this);

        Calendar calendar = Calendar.getInstance();
        int mYear = calendar.get(Calendar.YEAR);
        int mMonth = calendar.get(Calendar.MONTH);
        int mDay = calendar.get(Calendar.DAY_OF_MONTH);

        Calendar newStartDate = Calendar.getInstance();
        newStartDate.set(mYear, mMonth, mDay - 6);
        textViewStartDate.setText(dateFormatter.format(newStartDate.getTime()));

        Calendar newEndDate = Calendar.getInstance();
        newEndDate.set(mYear, mMonth, mDay);
        textViewEndDate.setText(dateFormatter.format(newEndDate.getTime()));

        if (appInstance.userDetail.user.userType.equalsIgnoreCase(getString(R.string.barber_)))
            getBarberFinance();
        else
            getShopFinance();

    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case android.R.id.home:
                finish();
                break;
        }
        return super.onOptionsItemSelected(item);
    }

    private void getBarberFinance() {
        utilObj.startLoader(mContext);
        barberManagerObj.getBarberFinance(utilObj.getMyDateFormat(textViewStartDate.getText().toString(), Constants.DateFormat.MMMMDDYYYY, Constants.DateFormat.YYYYMMDD),
                utilObj.getMyDateFormat(textViewEndDate.getText().toString(), Constants.DateFormat.MMMMDDYYYY, Constants.DateFormat.YYYYMMDD));
    }

    private void getShopFinance() {
        if (appInstance.userProfileShop != null) {
            utilObj.startLoader(mContext);
        }
    }

    @Override
    public void onSuccessRedirection(int taskID) {
        utilObj.stopLoader();
        switch (taskID) {
            case Constants.TaskID.BARBER_FINANCE:
                if (appInstance.barberFinance != null) {
                    BarberFinance financeObj = appInstance.barberFinance;
                    if (financeObj.data.monthSale.size() > 0) {
                        textViewCurrentMonth.setText("$" + String.format("%.2f",financeObj.data.monthSale.get(0).totalSale));
                    }
                    if (financeObj.data.totalSale.size() > 0) {
                        textViewTotalSales.setText("$" + String.format("%.2f",financeObj.data.totalSale.get(0).totalSale));
                    }
                    if (financeObj.data.weekSale.size() > 0) {
                        textViewCurrentWeek.setText("$" + String.format("%.2f",financeObj.data.weekSale.get(0).totalSale));
                    }

                    Collections.sort(financeObj.data.custom);
                    if (financeObj.data.custom.size() > 0) {
                        float totalSales = 0;
                        float appointments = 0;
                        for (BarberFinance.Custom custom : financeObj.data.custom) {
                            totalSales = totalSales + custom.sale;
                            appointments = appointments + custom.appointments;
                        }
                        textViewTotalSaleWeekly.setText("$" + String.format("%.2f",totalSales));
                        textViewTotalAppointments.setText(decimalFormat.format(appointments) + "");
                        setBarberGraphData(financeObj.data.custom);
                    } else {
                        textViewTotalSaleWeekly.setText("$0");
                        textViewTotalAppointments.setText("0");
                        mChart.invalidate();
                        mChart.clear();
                    }
                }
                break;
        }
    }

    @Override
    public void onFailureRedirection(String errorMessage) {
        utilObj.stopLoader();
        showSnackBar(errorMessage);
    }

    private void setBarberGraphData(List<BarberFinance.Custom> listGraphData) {
        ArrayList<String> labels = new ArrayList<>();
        ArrayList<BarEntry> entries = new ArrayList<>();
        for (int i = 0; i < listGraphData.size(); i++) {
            BarberFinance.Custom customObj = listGraphData.get(i);
            labels.add(utilObj.getMyDateFormat(customObj.appointmentDate, Constants.DateFormat.YYYYMMDD, Constants.DateFormat.MMMDD));
            entries.add(new BarEntry(new float[]{customObj.sale, customObj.appointments}, i));
        }
        BarDataSet bardataset = new BarDataSet(entries, "Cells");
        bardataset.setDrawValues(false);
        BarData data = new BarData(labels, bardataset);
        mChart.setData(data);
        bardataset.setColors(new int[]{Color.parseColor("#9ab6fd"), Color.parseColor("#4962C1")});
        mChart.setDescription("");
        mChart.animateY(1000);
    }

    private void initDatePicker(final TextView textViewObj, int year, int month, int day) {
        datePickerDialog = new DatePickerDialog(this, new DatePickerDialog.OnDateSetListener() {
            public void onDateSet(DatePicker view, int year, int monthOfYear, int dayOfMonth) {
                Calendar newDate = Calendar.getInstance();
                newDate.set(year, monthOfYear, dayOfMonth);

                if (textViewObj.getId() == R.id.tv_end_date) {
                    if (utilObj.compareDates(textViewStartDate.getText().toString(),
                            dateFormatter.format(newDate.getTime()), Constants.DateFormat.MMMMDDYYYY) < 0) {
                        utilObj.showToast(mContext, "Select valid dates", 1);
                        return;
                    }
                } else {
                    if (utilObj.compareDates(dateFormatter.format(newDate.getTime()),
                            textViewEndDate.getText().toString(), Constants.DateFormat.MMMMDDYYYY) < 0) {
                        utilObj.showToast(mContext, "Select valid dates", 1);
                        return;
                    }
                }

                textViewObj.setText(dateFormatter.format(newDate.getTime()));
                if (appInstance.userDetail.user.userType.equalsIgnoreCase(getString(R.string.barber_)))
                    getBarberFinance();
                else
                    getShopFinance();
            }
        }, year, month, day);
        datePickerDialog.getDatePicker().setMaxDate(new Date().getTime());
    }

    private void addUnderline(TextView textView, String text) {
        SpannableString content = new SpannableString(text);
        content.setSpan(new UnderlineSpan(), 0, content.length(), 0);
        textView.setText(content);
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.tv_start_date:
                String startDate = textViewStartDate.getText().toString();
                startDate = utilObj.getMyDateFormat(startDate, Constants.DateFormat.MMMMDDYYYY, Constants.DateFormat.YYYYMMDD);
                initDatePicker(textViewStartDate, Integer.parseInt(startDate.split("-")[0]), Integer.parseInt(startDate.split("-")[1]), Integer.parseInt(startDate.split("-")[2]));
                datePickerDialog.show();
                break;
            case R.id.tv_end_date:
                String endDate = textViewEndDate.getText().toString();
                endDate = utilObj.getMyDateFormat(endDate, Constants.DateFormat.MMMMDDYYYY, Constants.DateFormat.YYYYMMDD);
                initDatePicker(textViewEndDate, Integer.parseInt(endDate.split("-")[0]), Integer.parseInt(endDate.split("-")[1]), Integer.parseInt(endDate.split("-")[2]));
                datePickerDialog.show();
                break;
        }
    }
}
