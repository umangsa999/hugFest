// THIS CODE AND INFORMATION ARE PROVIDED "AS IS" WITHOUT WARRANTY OF ANY
// KIND, EITHER EXPRESSED OR IMPLIED, INCLUDING BUT NOT LIMITED TO THE
// IMPLIED WARRANTIES OF MERCHANTABILITY AND/OR FITNESS FOR A
// PARTICULAR PURPOSE.
//
// <author>Ryan Zhou and Chris Lee</author>
// <email>wannabedev.ta@gmail.com</email>
// <date>2015-08-14</date>

package com.usc.itp476.contact.contactproject.slidetab;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
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
import com.usc.itp476.contact.contactproject.ContactApplication;
import com.usc.itp476.contact.contactproject.R;
import com.usc.itp476.contact.contactproject.StartActivity;
import com.usc.itp476.contact.contactproject.ingamescreen.ResultActivity;
import com.usc.itp476.contact.contactproject.ingamescreen.TargetActivity;
import com.usc.itp476.contact.contactproject.slidetab.fragments.FriendsFragment;
import com.usc.itp476.contact.contactproject.slidetab.fragments.MapDisplayFragment;
import com.usc.itp476.contact.contactproject.slidetab.fragments.ProfileFragment;
import com.usc.itp476.contact.contactproject.CustomParsePushBroadcastReceiver;
import com.usc.itp476.contact.contactproject.slidetab.helper.SlidingTabLayout;
import java.util.ArrayList;
import java.util.List;

public class AllTabActivity extends FragmentActivity {
    private final String TAG = this.getClass().getSimpleName();
    public static int currentTab = 0;
    private ViewPager viewPager = null;
    private PagerAdapter pagerAdapter = null;
    private ArrayList<Fragment> tabs = null;
    public ProfileFragment profileFragment = null;
    public FriendsFragment friendFragment = null;

    @Override
    protected void onCreate(Bundle savedInstanceState) {

        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_all_tab);

        tabs = new ArrayList<>();
        ArrayList<String> titles = new ArrayList<>();

        // Instantiate a ViewPager and a PagerAdapter.
        viewPager = (ViewPager) findViewById(R.id.viewPager);
        SlidingTabLayout mSlidingTabLayout = (SlidingTabLayout) findViewById(R.id.slidingTabLayout);

        titles.add("Games");
        titles.add("Friends");
        titles.add("Profile");

        tabs.add(new MapDisplayFragment());
        friendFragment = new FriendsFragment();
        friendFragment.setAllTabActivity(this);
        tabs.add(friendFragment);

        profileFragment = new ProfileFragment();
        tabs.add(profileFragment);

        pagerAdapter = new ScreenSlidePagerAdapter( getSupportFragmentManager(), tabs, titles );
        viewPager.setAdapter(pagerAdapter);

        mSlidingTabLayout.setDistributeEvenly(true);
        mSlidingTabLayout.setViewPager(viewPager);

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
                    grabRealFriends(list);
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
                    ContactApplication.getFriendsList().put(friend.getObjectId(), friend); //add our friend locally
                }else{
                    Log.wtf(TAG, "COULD NOT ADD: " + u.getObjectId());
                }
            } catch (ParseException e) {
                Log.wtf(TAG, e.getLocalizedMessage());
            }
        }
        friendFragment.updateAdapter();
    }

    public void showFriendProfile(String id){
        //setting the middle tab to profile of a friend
        profileFragment = new ProfileFragment();
        profileFragment.friendProfileTrue(id, true);
        tabs.set(1, profileFragment);
        pagerAdapter.notifyDataSetChanged();
        viewPager.setAdapter(pagerAdapter);
        viewPager.setCurrentItem(1);
    }

    public void returnToList(){
        tabs.set(1, friendFragment);
        profileFragment = null;
        pagerAdapter.notifyDataSetChanged();
        viewPager.setAdapter(pagerAdapter);
        viewPager.setCurrentItem(1);
    }

    @Override
    public void onBackPressed() {

        if (viewPager.getCurrentItem() == 0) {
            // If the user is currently looking at the first step, allow the system to handle the
            // Back button. This calls finish() on this activity and pops the back stack.
            Intent i = new Intent();
            setResult(StartActivity.RESULT_ALLTABS_QUIT_STAY_LOGIN, i);
            finish();
        } else if( viewPager.getCurrentItem() == 1 && (profileFragment != null) ){
            //if the user has clicked to view one of his friend's profile in friendsfragment then we want to
            //set the the current fragment back to the gridview of his friends
            returnToList();
        }
        else {
            // Otherwise, select the previous step.
            viewPager.setCurrentItem(viewPager.getCurrentItem() - 1);
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
            AllTabActivity.currentTab = viewPager.getCurrentItem();
            return mTabs.get(position);
        }

        @Override
        public int getCount() {
            return mTabs.size();
        }
    }

    @Override
    public void onResume(){
        super.onResume();
        SharedPreferences prefs = getSharedPreferences(ContactApplication.SHARED_PREF_FILE, MODE_PRIVATE);
        String restoredText = prefs.getString(CustomParsePushBroadcastReceiver.ACTION, null);
        if (restoredText != null) {
            String action = prefs.getString(CustomParsePushBroadcastReceiver.ACTION, null);
            if (action != null) {
                if (action.equals(CustomParsePushBroadcastReceiver.INVITE)) {
                    Intent i = new Intent(this.getApplicationContext(), TargetActivity.class);
                    startActivity(i);
                } else if (action.equals(CustomParsePushBroadcastReceiver.END)) {
                    Intent i = new Intent(this.getApplicationContext(), ResultActivity.class);
                    startActivity(i);
                }
            }
        }
        this.getApplicationContext().getSharedPreferences( ContactApplication.SHARED_PREF_FILE,
                Context.MODE_PRIVATE).edit().clear().apply();
    }
}
