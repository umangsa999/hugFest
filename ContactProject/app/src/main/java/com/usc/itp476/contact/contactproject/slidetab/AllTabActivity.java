package com.usc.itp476.contact.contactproject.slidetab;

import android.content.Intent;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentActivity;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentStatePagerAdapter;
import android.support.v4.view.PagerAdapter;
import android.support.v4.view.ViewPager;
import android.util.Log;
import android.widget.Toast;

import com.parse.FindCallback;
import com.parse.ParseException;
import com.parse.ParseQuery;
import com.parse.ParseRelation;
import com.parse.ParseUser;
import com.usc.itp476.contact.contactproject.R;
import com.usc.itp476.contact.contactproject.StartActivity;
import com.usc.itp476.contact.contactproject.slidetab.fragments.FriendsFragment;
import com.usc.itp476.contact.contactproject.slidetab.fragments.GameFragment;
import com.usc.itp476.contact.contactproject.slidetab.fragments.ProfileFragment;
import com.usc.itp476.contact.contactproject.slidetab.helper.SlidingTabLayout;

import java.util.ArrayList;
import java.util.List;

public class AllTabActivity extends FragmentActivity {
    final String TAG = this.getClass().getSimpleName();
    private static final int NUM_PAGES = 3;
    private ViewPager mPager;
    private PagerAdapter mPagerAdapter;
    private SlidingTabLayout mSlidingTabLayout;
    private ArrayList<Fragment> tabs;
    private ArrayList<String> titles;
    private ArrayList<ParseUser> friendList;
    public ProfileFragment mProfileFragment = null;
    public FriendsFragment mFriendFragment = null;
    //we need this ^ because we later need to check the profile fragment is a view of the friends or user

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_all_tab);

        tabs = new ArrayList<Fragment>();
        titles = new ArrayList<>();
        friendList = new ArrayList<>();

        // Instantiate a ViewPager and a PagerAdapter.
        mPager = (ViewPager) findViewById(R.id.viewPager);
        mSlidingTabLayout = (SlidingTabLayout) findViewById(R.id.slidingTabLayout);

        titles.add("Games");
        titles.add("Friends");
        titles.add( "Profile" );

        tabs.add(new GameFragment());
        mFriendFragment = new FriendsFragment();
        mFriendFragment.setAllTabActivity(this, friendList);
        tabs.add( mFriendFragment );

        mProfileFragment = new ProfileFragment();
        tabs.add( mProfileFragment );

        mPagerAdapter = new ScreenSlidePagerAdapter( getSupportFragmentManager(), tabs, titles );
        mPager.setAdapter(mPagerAdapter);

        mSlidingTabLayout.setDistributeEvenly(true);
        mSlidingTabLayout.setViewPager(mPager);

        updateFriends();
    }

    public void updateFriends(){
        ParseRelation<ParseUser> friends = ParseUser.getCurrentUser().getRelation("friends");
        ParseQuery<ParseUser> getAllFriends = friends.getQuery();
        getAllFriends.addDescendingOrder("totalHugs");
        getAllFriends.findInBackground(new FindCallback<ParseUser>() {
            @Override
            public void done(List<ParseUser> list, ParseException e) {
                if (e != null) {
                    Log.wtf(TAG, e.getLocalizedMessage());
                } else if (list.size() == 0) {
                    Toast.makeText(getApplicationContext(),
                            "Could not find friends", Toast.LENGTH_SHORT).show();
                } else {
                    Log.wtf(TAG, String.valueOf(list.size()));
                    grabRealFriends(list);
                    signalUpdateFriends();
                }
            }
        });
    }

    private void grabRealFriends(List<ParseUser> list){
        //for each user in the relation, we only have the ObjectId and username
        for (ParseUser u : list){
            try {
                ParseUser friend = ParseUser.getQuery().get(u.getObjectId()); //get the rest
                if (friend != null){
                    friendList.add(friend); //add our friend locally
                }else{
                    Log.wtf(TAG, "COULD NOT ADD: " + u.getObjectId());
                }
            } catch (ParseException e) {
                Log.wtf(TAG, e.getLocalizedMessage());
            }
        }
    }

    private void signalUpdateFriends(){
        mFriendFragment.updateFriends(friendList);
    }

    public void showFriendProfile(String id){
        //setting the middle tab to profile of a friend
        mProfileFragment = new ProfileFragment();
        mProfileFragment.friendProfileTrue(id, this);
        tabs.set(1, mProfileFragment);
        mPagerAdapter.notifyDataSetChanged();
        mPager.setAdapter(mPagerAdapter);
        mPager.setCurrentItem( 1 );
    }

    public void returnToList(){
        tabs.set(1, mFriendFragment);
        mProfileFragment = null;
        mPagerAdapter.notifyDataSetChanged();
        mPager.setAdapter(mPagerAdapter);
        mPager.setCurrentItem( 1 );
    }

    @Override
    public void onBackPressed() {

        if (mPager.getCurrentItem() == 0) {
            // If the user is currently looking at the first step, allow the system to handle the
            // Back button. This calls finish() on this activity and pops the back stack.
            Intent i = new Intent();
            setResult(StartActivity.RESULT_ALLTABS_QUIT_STAY_LOGIN, i);
            finish();
        } else if( mPager.getCurrentItem() == 1 && (mProfileFragment != null) ){
            //if the user has clicked to view one of his friend's profile in friendsfragment then we want to
            //set the the current fragment back to the gridview of his friends

            returnToList();
        }
        else {
            // Otherwise, select the previous step.
            mPager.setCurrentItem(mPager.getCurrentItem() - 1);
        }
    }

    class ScreenSlidePagerAdapter extends FragmentStatePagerAdapter {
        protected CharSequence[] mTitles;
        private List<Fragment> mTabs;
        public ScreenSlidePagerAdapter(FragmentManager fm, ArrayList<Fragment> tabs, List<String>titles) {
            super(fm);
            mTabs = tabs;
            mTitles = titles.toArray(new CharSequence[titles.size()]);
        }

        // This method return the titles for the Tabs in the Tab Strip
        @Override
        public CharSequence getPageTitle(int position) {
            return mTitles[position];
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
