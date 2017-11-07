package com.barbrdo.app.activities;

import android.content.Context;
import android.os.Bundle;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.MenuItem;
import android.view.View;
import android.widget.TextView;

import com.barbrdo.app.R;
import com.barbrdo.app.adapters.ReviewsAdapter;

public class ReviewsActivity extends AppActivity {

    private Context mContext;
    private RecyclerView recyclerViewReviews;
    private ReviewsAdapter reviewsAdapter;
    private TextView textViewNoResults;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_reviews);
        mContext = ReviewsActivity.this;
        initData();
        bindControls();
    }

    @Override
    void initData() {
        setUpToolBar("");
        recyclerViewReviews = getView(R.id.rv_list);
        textViewNoResults = getView(R.id.tv_no_results);
    }

    @Override
    void bindControls() {
        recyclerViewReviews.setHasFixedSize(true);
        LinearLayoutManager layoutManager = new LinearLayoutManager(mContext);
        layoutManager.setOrientation(LinearLayoutManager.VERTICAL);
        recyclerViewReviews.setLayoutManager(layoutManager);

        reviewsAdapter = new ReviewsAdapter((AppActivity) mContext);
        recyclerViewReviews.setAdapter(reviewsAdapter);

        if (appInstance.userProfile.user.ratings.size() > 0) {
            reviewsAdapter.setList(appInstance.userProfile.user.ratings);
            reviewsAdapter.notifyDataSetChanged();
            textViewNoResults.setVisibility(View.GONE);
        } else {
            textViewNoResults.setVisibility(View.VISIBLE);
            textViewNoResults.setText("No Reviews Found");
        }
    }


    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case android.R.id.home:
                onBackPressed();
                return true;
        }
        return super.onOptionsItemSelected(item);
    }
}
