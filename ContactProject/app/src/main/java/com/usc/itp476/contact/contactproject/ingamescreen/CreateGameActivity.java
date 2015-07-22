package com.usc.itp476.contact.contactproject.ingamescreen;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.location.Location;
import android.location.LocationManager;
import android.os.Bundle;
import android.view.View;
import android.widget.AdapterView;
import android.widget.Button;
import android.widget.GridView;
import android.widget.ListView;
import android.widget.SeekBar;
import android.widget.TextView;
import android.widget.Toast;

import com.parse.ParseException;
import com.parse.ParseGeoPoint;
import com.parse.SaveCallback;
import com.usc.itp476.contact.contactproject.POJO.GameMarker;
import com.usc.itp476.contact.contactproject.R;
import com.usc.itp476.contact.contactproject.adapters.FriendListGridAdapter;

public class CreateGameActivity extends Activity {
    final String TAG = this.getClass().getSimpleName();
    private Button btnCreate;
    private TextView txvwMax;
    private SeekBar skbrMax;
    private ListView lsvwInvite;
    private GridView gridView;
    private int maxPoints = -1;
    public static String [] prgmNameList={"Ryan", "Chris", "Mike", "Rob", "Nathan",
            "Paulina", "Trina", "Raymond"};
    public static int [] prgmImages={ R.mipmap.large, R.mipmap.large ,R.mipmap.large,
            R.mipmap.large, R.mipmap.large, R.mipmap.large ,R.mipmap.large ,R.mipmap.large};
    public static String [] prgmPoints={"0", "1", "2", "3", "4", "5", "6", "7"};

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_create_game);

        //mContext = getActivity().getApplicationContext();
        //mActivity = getActivity();

        gridView = (GridView) findViewById(R.id.grid_view);

        // Instance of ImageAdapter Class
        gridView.setAdapter(new FriendListGridAdapter( getApplicationContext(),
                prgmNameList, prgmImages, prgmPoints, true, null, null, null, null, null) );

        gridView.setStretchMode(GridView.STRETCH_COLUMN_WIDTH);

        // On Click event for Single Gridview Item

        gridView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> adapterView, View view, int position, long l) {
                // Sending image id to FullScreenActivity
                Toast.makeText( getApplicationContext(), "Clicked", Toast.LENGTH_SHORT).show();
            }
        });

        Intent i = getIntent();

        btnCreate = (Button) findViewById(R.id.btnCreate);
        txvwMax = (TextView) findViewById(R.id.txvwMax);
        skbrMax = (SeekBar) findViewById(R.id.skbrMax);
        setListeners();
    }

    private void setListeners(){
        btnCreate.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                LocationManager locationManager =
                        (LocationManager) getSystemService(Context.LOCATION_SERVICE);
                Location l = locationManager.getLastKnownLocation(LocationManager.GPS_PROVIDER);

                if (l == null){
                    Toast.makeText(getApplicationContext(),
                            "Cannot detect location to start game",
                            Toast.LENGTH_SHORT).show();
                    finish();
                }else {
                    ParseGeoPoint pLoc = new ParseGeoPoint(l.getLatitude(), l.getLongitude());
                    createGameMarker(pLoc);
                }
            }
        });

        skbrMax.setOnSeekBarChangeListener(new SeekBar.OnSeekBarChangeListener() {
            @Override
            public void onProgressChanged(SeekBar seekBar, int progress, boolean fromUser) {
                maxPoints = 1 + progress;
                txvwMax.setText(String.valueOf(maxPoints));
            }

            @Override
            public void onStartTrackingTouch(SeekBar seekBar) {
            }

            @Override
            public void onStopTrackingTouch(SeekBar seekBar) {
            }
        });
    }

    private void createGameMarker(ParseGeoPoint pLoc){
        GameMarker marker = new GameMarker();
        marker.setLocation(pLoc);
        marker.setHostName();
        marker.setPoints(maxPoints);
        marker.setPlayerCount(1);
        marker.saveInBackground(new SaveCallback() {
            @Override
            public void done(ParseException e) {
                if (e == null) {
                    Intent i = new Intent(
                            CreateGameActivity.this.getApplicationContext(),
                            TargetActivity.class);
                    i.putExtra(TargetActivity.MAXPOINTS, maxPoints);
                    startActivity(i);
                } else {
                    Toast.makeText(getApplicationContext(),
                            "Could not make game.",
                            Toast.LENGTH_SHORT).show();
                }
            }
        });
    }
}