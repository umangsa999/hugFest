package com.usc.itp476.contact.contactproject.slidetab.fragments;

import android.content.Intent;
import android.graphics.Color;
import android.support.v4.app.Fragment;
import android.content.Context;
import android.location.Location;
import android.location.LocationListener;
import android.location.LocationManager;
import android.os.Bundle;
import android.support.v4.app.FragmentTransaction;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageButton;
import android.widget.Toast;

import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.model.BitmapDescriptorFactory;
import com.google.android.gms.maps.model.Circle;
import com.google.android.gms.maps.model.CircleOptions;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.MarkerOptions;
import com.usc.itp476.contact.contactproject.CreateGameActivity;
import com.usc.itp476.contact.contactproject.R;

public class GameFragment extends Fragment implements OnMapReadyCallback{
    private GoogleMap map;
    private boolean gameClicked = false;
    private ImageButton btnGame;
    private LatLng myLoc;
    private SupportMapFragment mapFragment;
    private View rootView;
    private Circle radiusCircle = null;
    private final int backgroundColor = Color.argb(128, 0, 128, 128);

    @Override
    public View onCreateView(LayoutInflater inflater,
                             ViewGroup container,
                             Bundle savedInstanceState) {

        rootView = (View) inflater.inflate(
                    R.layout.activity_home, container, false);

        btnGame = (ImageButton) rootView.findViewById(R.id.btnGame);
        mapFragment = SupportMapFragment.newInstance();
        FragmentTransaction fragmentTransaction =
                getChildFragmentManager().beginTransaction();
        fragmentTransaction.add(R.id.mapHolder, mapFragment);
        fragmentTransaction.commit();

        mapFragment.getMapAsync(this);
        assignListeners();
        return rootView;
    }

    @Override
    public void onResume() {
        super.onResume();
        if (map == null) {
            map = mapFragment.getMap();
        }
    }

    @Override
    public void onMapReady(GoogleMap googleMap) {
        map = googleMap;
        map.setMapType(GoogleMap.MAP_TYPE_TERRAIN);
        map.setPadding(4, 4, 4, 4);
        map.setMyLocationEnabled(true);
        LocationManager LocMan =
                (LocationManager) getActivity().getSystemService(Context.LOCATION_SERVICE);
        Location loc = LocMan.getLastKnownLocation(LocationManager.GPS_PROVIDER);

        if (loc != null) {
            myLoc = new LatLng(loc.getLatitude(), loc.getLongitude());
            map.moveCamera(CameraUpdateFactory.newLatLngZoom(myLoc, 15));

            CircleOptions circleOptions = new CircleOptions()
                    .center(myLoc)
                    .radius(700).fillColor(backgroundColor)
                    .strokeWidth(5).strokeColor(backgroundColor);
            radiusCircle = map.addCircle(circleOptions);
            findPoints();
        }else{
            Toast.makeText(GameFragment.this.getActivity().getApplicationContext(),
                    "No connection to find games.", Toast.LENGTH_SHORT).show();
        }
        btnGame.bringToFront();
        rootView.requestLayout();
        rootView.invalidate();
    }

    private void findPoints(){
        map.addMarker(new MarkerOptions()
                .position(new LatLng(myLoc.latitude + 10, myLoc.longitude + 10))
                .title("Hello world")
                .icon(BitmapDescriptorFactory.defaultMarker(BitmapDescriptorFactory.HUE_AZURE)));
    }

    private void assignListeners(){
        setCreateListener();
        setLocationListener();
    }

    private void setCreateListener(){
        btnGame.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                /*gameClicked = !gameClicked;
                if (gameClicked) {
                    btnGame.setBackgroundResource(R.mipmap.ic_join);
                } else {
                    btnGame.setBackgroundResource(R.mipmap.ic_create);
                }*/
                Intent i = new Intent(GameFragment.this.getActivity().getApplicationContext(), CreateGameActivity.class);
                startActivity(i);
            }
        });
    }

    private void setLocationListener(){
        LocationManager l =
                (LocationManager) getActivity().getSystemService(Context.LOCATION_SERVICE);
        LocationListener locationListener = new LocationListener() {
            @Override
            public void onLocationChanged(Location location) {
                myLoc = new LatLng(location.getLatitude(), location.getLongitude());
                map.moveCamera(CameraUpdateFactory.newLatLng(myLoc));

                if (radiusCircle != null){
                    radiusCircle.setCenter(myLoc);
                }else {
                    CircleOptions circleOptions = new CircleOptions()
                            .center(myLoc)
                            .radius(700).fillColor(Color.argb(128, 0, 128, 128))
                            .strokeWidth(5).strokeColor(Color.argb(128, 0, 128, 128));
                    radiusCircle = map.addCircle(circleOptions);
                }
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