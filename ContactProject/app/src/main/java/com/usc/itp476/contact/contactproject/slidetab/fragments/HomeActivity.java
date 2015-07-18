package com.usc.itp476.contact.contactproject.slidetab.fragments;

import android.support.v4.app.Fragment;
import android.content.Context;
import android.location.Location;
import android.location.LocationListener;
import android.location.LocationManager;
import android.os.Bundle;
import android.support.v4.app.FragmentTransaction;
import android.util.Log;
import android.view.InflateException;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageButton;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.LatLng;
import com.usc.itp476.contact.contactproject.R;

public class HomeActivity extends Fragment implements OnMapReadyCallback{
    private GoogleMap map;
    private boolean gameClicked = false;
    private ImageButton btnCreate;
    private LatLng myLoc;
    private SupportMapFragment mapFragment;
    private View rootView;

    @Override
    public View onCreateView(LayoutInflater inflater,
                             ViewGroup container,
                             Bundle savedInstanceState) {

        rootView = (View) inflater.inflate(
                    R.layout.activity_home, container, false);
        Log.wtf(this.getClass().getSimpleName(), "onCreateView container's children count: " + container.getChildCount());
        //if (savedInstanceState == null) {
        btnCreate = (ImageButton) rootView.findViewById(R.id.btnCreate);
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
        LatLng temp = new LatLng(loc.getLatitude(), loc.getLongitude());
        map.moveCamera(CameraUpdateFactory.newLatLngZoom(temp, 16));

        btnCreate.bringToFront();
        rootView.requestLayout();
        rootView.invalidate();
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
        LocationManager l =
                (LocationManager) getActivity().getSystemService(Context.LOCATION_SERVICE);
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