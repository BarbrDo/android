package com.barbrdo.app.fragments;


import android.os.Bundle;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.View;
import android.widget.TextView;

import com.barbrdo.app.R;
import com.barbrdo.app.adapters.ReviewsAdapter;
import com.barbrdo.app.interfaces.OnItemClickListener;


public class ReviewsFragment extends BaseFragment implements OnItemClickListener {
    private RecyclerView recyclerViewReviews;
    private ReviewsAdapter reviewsAdapter;
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

        reviewsAdapter = new ReviewsAdapter(getBaseActivity());
        reviewsAdapter.setOnItemClickListener(this);
        recyclerViewReviews.setAdapter(reviewsAdapter);

        if (getBaseActivity().appInstance.userProfile.user.ratings.size() > 0) {
            reviewsAdapter.setList(getBaseActivity().appInstance.userProfile.user.ratings);
            reviewsAdapter.notifyDataSetChanged();
            textViewNoResults.setVisibility(View.GONE);
        } else {
            textViewNoResults.setVisibility(View.VISIBLE);
            textViewNoResults.setText("No Reviews Found");
        }
    }

    @Override
    public void onItemClick(Object item, int position) {

    }
}
