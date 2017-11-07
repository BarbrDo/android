package com.barbrdo.app.fragments;


import android.os.Bundle;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.View;
import android.widget.TextView;

import com.barbrdo.app.R;
import com.barbrdo.app.adapters.CutsAdapter;
import com.barbrdo.app.adapters.ReviewsAdapter;
import com.barbrdo.app.dataobject.Chair;
import com.barbrdo.app.dataobject.UserAppointment;
import com.barbrdo.app.interfaces.OnItemClickListener;

import java.util.Iterator;


public class CutsFragment extends BaseFragment implements OnItemClickListener {
    private RecyclerView recyclerViewReviews;
    private CutsAdapter cutsAdapter;
    private TextView textViewNoResults;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState, R.layout.fragment_reviews);
    }

    @Override
    protected void initView() {
        recyclerViewReviews = getView(R.id.rv_list);
        textViewNoResults = getView(R.id.tv_no_results);
    }

    @Override
    protected void bindControls() {
        recyclerViewReviews.setHasFixedSize(true);
        LinearLayoutManager layoutManager = new LinearLayoutManager(getActivity());
        layoutManager.setOrientation(LinearLayoutManager.VERTICAL);
        recyclerViewReviews.setLayoutManager(layoutManager);

        cutsAdapter = new CutsAdapter(getBaseActivity());
        cutsAdapter.setOnItemClickListener(this);
        recyclerViewReviews.setAdapter(cutsAdapter);

        if (getBaseActivity().appInstance.userProfile.user.appointments.size() > 0) {
            Iterator<UserAppointment> iterator = getBaseActivity().appInstance.userProfile.user.appointments.iterator();
            while (iterator.hasNext()) {
                UserAppointment appointment = iterator.next();
                if(!appointment.appointmentStatus.equalsIgnoreCase("completed")) {
                    iterator.remove();
                }
            }
            cutsAdapter.setList(getBaseActivity().appInstance.userProfile.user.appointments);
            cutsAdapter.notifyDataSetChanged();
            textViewNoResults.setVisibility(View.GONE);
        } else {
            textViewNoResults.setVisibility(View.VISIBLE);
            textViewNoResults.setText("No Cuts Found");
        }
    }

    @Override
    public void onItemClick(Object item, int position) {

    }
}
