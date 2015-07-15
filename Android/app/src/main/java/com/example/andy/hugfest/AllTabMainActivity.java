package com.example.andy.hugfest;

import android.app.TabActivity;
import android.content.Intent;
import android.os.Bundle;
import android.support.v4.view.PagerAdapter;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.TabHost;

public class AllTabMainActivity extends TabActivity{

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_all_tab_main);

        //ActionBar a = getParent().getActionBar();
        //a.show();

        TabHost tabHost = getTabHost();
        TabHost.TabSpec homeSpec = tabHost.newTabSpec("Home");


        homeSpec.setIndicator("Home", getResources().getDrawable(R.drawable.home_selector));
        Intent homeIntent = new Intent(this, tabHome.class);
        homeSpec.setContent(homeIntent);

        TabHost.TabSpec profileSpec = tabHost.newTabSpec("Profile");
        profileSpec.setIndicator("Profile", getResources().getDrawable(R.drawable.profile_selector));
        Intent profileIntent = new Intent(this, tabProfile.class);
        profileSpec.setContent(profileIntent);

        TabHost.TabSpec friendSpec = tabHost.newTabSpec("Friends");
        friendSpec.setIndicator("Friends", getResources().getDrawable(R.drawable.friend_selector));
        Intent friendIntent = new Intent(this, tabFriends.class);
        friendSpec.setContent(friendIntent);

        tabHost.addTab(homeSpec);
        tabHost.addTab(friendSpec);
        tabHost.addTab(profileSpec);

    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_all_tab_main, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();

        //noinspection SimplifiableIfStatement
        if (id == R.id.action_settings) {
            return true;
        }
        return super.onOptionsItemSelected(item);
    }

    class SamplePagerAdapter extends PagerAdapter{

        @Override
        public CharSequence getPageTitle(int position){
            return "Item "+ (position +1);
        }

        @Override
        public int getCount() {
            return 0;
        }

        @Override
        public boolean isViewFromObject(View view, Object o) {
            return false;
        }
    }
}
