package com.example.andy.hugfest;

/**
 * Created by ryanzhou on 7/9/15.
 */
import android.app.Activity;
import android.os.Bundle;
import android.widget.ListView;

public class tabFriends extends Activity {
    private ListView listViewFriends;
    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.tabfriends);

        listViewFriends = (ListView)findViewById(R.id.listView5);
        listViewFriends.setAdapter( new customAdapter( this ) );
    }
}