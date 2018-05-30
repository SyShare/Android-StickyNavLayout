package com.zhy.stickynavlayout;

import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentPagerAdapter;
import android.support.v4.view.ViewPager;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.zhy.stickynavlayout.event.EventScroll;
import com.zhy.stickynavlayout.view.SimpleViewPagerIndicator;
import com.zhy.stickynavlayout.view.LiveStickyLayout;

import org.greenrobot.eventbus.EventBus;

/**
 * Created by syb on 2018/5/29.
 */

public class DollFragment extends Fragment {

    private String[] mTitles = new String[]{"简介", "评价", "相关"};
    private SimpleViewPagerIndicator mIndicator;
    private ViewPager mViewPager;
    private FragmentPagerAdapter mAdapter;
    private TabFragment[] mFragments = new TabFragment[mTitles.length];

    private LiveStickyLayout stickLayout;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.activity_main, container, false);
        initViews(view);
        initDatas();
        initEvents();
        return view;
    }

    private void initEvents() {
        mViewPager.setOnPageChangeListener(new ViewPager.OnPageChangeListener() {
            @Override
            public void onPageSelected(int position) {
            }

            @Override
            public void onPageScrolled(int position, float positionOffset,
                                       int positionOffsetPixels) {
                mIndicator.scroll(position, positionOffset);
            }

            @Override
            public void onPageScrollStateChanged(int state) {

            }
        });

    }

    private void initDatas() {
        mIndicator.setTitles(mTitles);

        for (int i = 0; i < mTitles.length; i++) {
            mFragments[i] = (TabFragment) TabFragment.newInstance(mTitles[i]);
        }

        mAdapter = new FragmentPagerAdapter(getChildFragmentManager()) {
            @Override
            public int getCount() {
                return mTitles.length;
            }

            @Override
            public Fragment getItem(int position) {
                return mFragments[position];
            }

        };

        mViewPager.setAdapter(mAdapter);
        mViewPager.setCurrentItem(0);
    }

    private void initViews(View rootView) {
        stickLayout = (LiveStickyLayout) rootView.findViewById(R.id.stick_layout);
        mIndicator = (SimpleViewPagerIndicator) rootView.findViewById(R.id.id_stickynavlayout_indicator);
        mViewPager = (ViewPager) rootView.findViewById(R.id.id_stickynavlayout_viewpager);

        stickLayout.setScrollChangeListener(new LiveStickyLayout.ScrollChangeListener() {
            @Override
            public void enableScroll(boolean isEnable) {
                EventBus.getDefault().post(new EventScroll(isEnable));
            }
        });
//        mViewPager.post(new Runnable() {
//            @Override
//            public void run() {
//                ViewGroup.LayoutParams params = mViewPager.getLayoutParams();
//                params.height = Resources.getSystem().getDisplayMetrics().heightPixels - mIndicator.getHeight();
//                mViewPager.setLayoutParams(params);
//            }
//        });
		/*
        RelativeLayout ll = (RelativeLayout) findViewById(R.id.id_stickynavlayout_topview);
		TextView tv = new TextView(this);
		tv.setText("我的动态添加的");
		tv.setBackgroundColor(0x77ff0000);
		ll.addView(tv, new RelativeLayout.LayoutParams(
				RelativeLayout.LayoutParams.MATCH_PARENT, 600));
		*/
    }
}
