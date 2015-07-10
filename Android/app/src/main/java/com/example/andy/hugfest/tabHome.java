package com.example.andy.hugfest;

/**
 * Created by ryanzhou on 7/9/15.
 */
import android.app.Activity;
import android.os.Bundle;
import android.widget.ListView;

public class tabHome extends Activity {
    private ListView listViewGames;
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.tabhome);
        listViewGames = (ListView)findViewById(R.id.listView6);
        listViewGames.setAdapter(new gameCustomAdapter(this));
    }
}