package com.usc.itp476.contact.contactproject;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.location.Location;
import android.location.LocationListener;
import android.location.LocationManager;
import android.os.Bundle;
import android.view.View;
import android.widget.ImageButton;

import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.MapFragment;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.model.LatLng;

public class HomeActivity extends Activity implements OnMapReadyCallback{
    private GoogleMap map;
    private boolean gameClicked = false;
    private ImageButton btnCreate;
    private LatLng myLoc;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_home);

        Intent i = getIntent();
        btnCreate = (ImageButton) findViewById(R.id.btnCreate);
        MapFragment mapFragment = (MapFragment) getFragmentManager()
                .findFragmentById(R.id.mapFrag);
        mapFragment.getMapAsync(this);

        assignListeners();
    }
    @Override
    public void onMapReady(GoogleMap googleMap) {
        map = googleMap;
        map.setMapType(GoogleMap.MAP_TYPE_TERRAIN);
        map.setPadding(4, 4, 4, 4);
        map.setMyLocationEnabled(true);
        LocationManager LocMan = (LocationManager) getSystemService(Context.LOCATION_SERVICE);
        Location loc = LocMan.getLastKnownLocation(LocationManager.GPS_PROVIDER);
        LatLng temp = new LatLng(loc.getLatitude(), loc.getLongitude());
        map.moveCamera(CameraUpdateFactory.newLatLngZoom(temp, 16));
    }

    private void assignListeners(){
        setCreateListener();
        setLocationListener();
    }

    private void setCreateListener(){
        btnCreate.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                gameClicked = !gameClicked;
                if (gameClicked) {
                    btnCreate.setBackgroundResource(R.mipmap.ic_join);
                }else{
                    btnCreate.setBackgroundResource(R.mipmap.ic_create);
                }
            }
        });
    }

    private void setLocationListener(){
        LocationManager l = (LocationManager) getSystemService(Context.LOCATION_SERVICE);
        LocationListener locationListener = new LocationListener() {
            @Override
            public void onLocationChanged(Location location) {
                myLoc = new LatLng(location.getLatitude(), location.getLongitude());
                map.moveCamera(CameraUpdateFactory.newLatLng(myLoc));
            }

            @Override
            public void onStatusChanged(String provider, int status, Bundle extras) {
            }

            @Override
            public void onProviderEnabled(String provider) {
            }

            @Override
            public void onProviderDisabled(String provider) {
            }
        };
        l.requestLocationUpdates(
                LocationManager.GPS_PROVIDER, 5000, 3, locationListener);
    }
}