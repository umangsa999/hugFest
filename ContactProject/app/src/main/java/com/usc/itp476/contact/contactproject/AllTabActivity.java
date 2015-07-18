package com.usc.itp476.contact.contactproject;

import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentActivity;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentStatePagerAdapter;
import android.support.v4.view.PagerAdapter;
import android.support.v4.view.ViewPager;

import java.util.ArrayList;
import java.util.List;

public class AllTabActivity extends FragmentActivity {


    private static final int NUM_PAGES = 3;
    private ViewPager mPager;
    private PagerAdapter mPagerAdapter;
    private SlidingTabLayout mSlidingTabLayout;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_all_tab);

        ArrayList<Fragment> tabs = new ArrayList<Fragment>();

        //add tabs here
        ProfileActivity p = new ProfileActivity();
        p.setTitle("Profile");
        tabs.add( p );


        tabs.add( new ProfileActivity() );
        tabs.add( new ProfileActivity() );
        // Instantiate a ViewPager and a PagerAdapter.

        mPager = (ViewPager) findViewById(R.id.viewPager);
        mPagerAdapter = new ScreenSlidePagerAdapter( getSupportFragmentManager(), tabs );
        mPager.setAdapter(mPagerAdapter);

        mSlidingTabLayout = (SlidingTabLayout) findViewById(R.id.slidingTabLayout);
        mSlidingTabLayout.setViewPager(mPager);

    }

    @Override
    public void onBackPressed() {
        if (mPager.getCurrentItem() == 0) {
            // If the user is currently looking at the first step, allow the system to handle the
            // Back button. This calls finish() on this activity and pops the back stack.
            super.onBackPressed();
        } else {
            // Otherwise, select the previous step.
            mPager.setCurrentItem(mPager.getCurrentItem() - 1);
        }
    }

    class ScreenSlidePagerAdapter extends FragmentStatePagerAdapter {

        List<Fragment> mTabs;
        public ScreenSlidePagerAdapter(FragmentManager fm, ArrayList<Fragment> tabs) {
            super(fm);
            mTabs = tabs;
        }

        @Override
        public Fragment getItem(int position) {
            return mTabs.get(position);
        }

        @Override
        public int getCount() {
            return mTabs.size();
        }
    }
}
