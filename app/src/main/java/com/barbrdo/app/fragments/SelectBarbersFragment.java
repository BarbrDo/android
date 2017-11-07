package com.barbrdo.app.fragments;

import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.design.widget.TabLayout;
import android.support.v4.view.ViewPager;
import android.view.LayoutInflater;
import android.widget.TextView;

import com.barbrdo.app.R;
import com.barbrdo.app.adapters.ViewPagerAdapter;

public class SelectBarbersFragment extends BaseFragment {

    private TabLayout tabLayout;
    private ViewPager viewPager;
    private TextView tabOne;
    private TextView tabTwo;
    private ViewPagerAdapter adapter;

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState, R.layout.fragment_select_barbers);
    }

    @Override
    protected void initView() {
        viewPager = getView(R.id.view_pager);
        tabLayout = getView(R.id.tabs);
    }

    @Override
    protected void bindControls() {
        setupViewPager(viewPager);
        tabLayout.setupWithViewPager(viewPager);
        setupTabIcons();

        viewPager.addOnPageChangeListener(new ViewPager.OnPageChangeListener() {
            @Override
            public void onPageScrolled(int position, float positionOffset, int positionOffsetPixels) {

            }

            @Override
            public void onPageSelected(int position) {
                if (position == 0) {
                    ((BarberMapViewFragment) adapter.getItem(0)).showBarbersOnMap(false);
                } else {
                    ((BarberMapViewFragment) adapter.getItem(0)).hideBarbersOnMap();
                }
            }

            @Override
            public void onPageScrollStateChanged(int state) {

            }
        });
    }

    private void setupViewPager(ViewPager viewPager) {
        adapter = new ViewPagerAdapter(getActivity().getSupportFragmentManager());
        adapter.addFrag(new BarberMapViewFragment(), getString(R.string.map_view));
        adapter.addFrag(new BarbersListViewFragment(), getString(R.string.list_view));
        viewPager.setAdapter(adapter);
    }

    private void setupTabIcons() {
        tabOne = (TextView) LayoutInflater.from(getActivity()).inflate(R.layout.custom_tab, null);
        tabOne.setText(R.string.map_view);
        tabLayout.getTabAt(0).setCustomView(tabOne);

        tabTwo = (TextView) LayoutInflater.from(getActivity()).inflate(R.layout.custom_tab, null);
        tabTwo.setText(R.string.list_view);
        tabLayout.getTabAt(1).setCustomView(tabTwo);
    }

    public void setPage(final int page) {
        if (viewPager != null) {
            viewPager.postDelayed(new Runnable() {
                @Override
                public void run() {
                    viewPager.setCurrentItem(page, true);
                }
            }, 100);
        }
    }
}
