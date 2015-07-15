package com.example.andy.hugfest.tabViews;
import android.app.Activity;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.ListView;

import com.example.andy.hugfest.R;
import com.example.andy.hugfest.listAdapters.GamesListAdapter;

public class tabHome extends Activity {
    private ListView listViewGames;
    private Button buttonAddGame;
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.tabhome);
        listViewGames = (ListView)findViewById(R.id.listView6);
        listViewGames.setAdapter(new GamesListAdapter(this));
        buttonAddGame = (Button) findViewById(R.id.buttonAddGame);
        //add/create a new game, send over
        buttonAddGame.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                //TODO andy's activity code
            }
        });
    }
}