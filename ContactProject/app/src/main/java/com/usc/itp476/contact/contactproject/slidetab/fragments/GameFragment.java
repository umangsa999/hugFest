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
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.games.Game;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.model.BitmapDescriptorFactory;
import com.google.android.gms.maps.model.Circle;
import com.google.android.gms.maps.model.CircleOptions;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.Marker;
import com.google.android.gms.maps.model.MarkerOptions;
import com.usc.itp476.contact.contactproject.ingamescreen.CreateGameActivity;
import com.usc.itp476.contact.contactproject.R;
import com.usc.itp476.contact.contactproject.ingamescreen.TargetActivity;

import org.w3c.dom.Text;

import java.util.ArrayList;

public class GameFragment extends Fragment
        implements /*GoogleMap.InfoWindowAdapter,*/ OnMapReadyCallback {
    private ArrayList<Marker> availableGames;
    private GoogleMap map;
    private boolean gameClicked;
    private ImageButton btnGame = null;
    private LatLng myLoc;
    private SupportMapFragment mapFragment;
    private View rootView;
    private Circle radiusCircle = null;
    private final int maxDistanceDraw = 700;
    private final double maxDistance = 0.0075;
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

        availableGames = new ArrayList<>();

        if (map == null && mapFragment != null) {
            map = mapFragment.getMap();
        }

        gameClicked = false;
        if (btnGame != null)
            btnGame.setBackgroundResource(R.mipmap.ic_create);

        mapFragment.getMapAsync(this);
        assignListeners();
        return rootView;
    }

    @Override
    public void onMapReady(GoogleMap googleMap) {
        map = googleMap;
        //map.setInfoWindowAdapter(this);
        map.setMapType(GoogleMap.MAP_TYPE_TERRAIN);
        map.setPadding(4, 4, 4, 4);
        map.setMyLocationEnabled(true);
        map.setOnMarkerClickListener(new GoogleMap.OnMarkerClickListener() {
            @Override
            public boolean onMarkerClick(Marker marker) {
                gameClicked = true;
                btnGame.setBackgroundResource(R.mipmap.ic_join);
                checkDistance(marker.getPosition());
                return true;
            }
        });

        map.setOnMapClickListener(new GoogleMap.OnMapClickListener() {
            @Override
            public void onMapClick(LatLng latLng) {
                gameClicked = false;
                btnGame.setBackgroundResource(R.mipmap.ic_create);
            }
        });

        LocationManager LocMan =
                (LocationManager) getActivity().getSystemService(Context.LOCATION_SERVICE);
        Location loc = LocMan.getLastKnownLocation(LocationManager.GPS_PROVIDER);

        if (loc != null) {
            myLoc = new LatLng(loc.getLatitude(), loc.getLongitude());
            map.moveCamera(CameraUpdateFactory.newLatLngZoom(myLoc, 15));

            findPoints();

            CircleOptions circleOptions = new CircleOptions()
                    .center(myLoc)
                    .radius(maxDistanceDraw).fillColor(backgroundColor)
                    .strokeWidth(5).strokeColor(backgroundColor);
            radiusCircle = map.addCircle(circleOptions);
        }else{
            Toast.makeText(GameFragment.this.getActivity().getApplicationContext(),
                    "No connection to find games.", Toast.LENGTH_SHORT).show();
        }
        btnGame.bringToFront();
        rootView.requestLayout();
        rootView.invalidate();
    }

    private void findPoints() {
        addPoint(myLoc.latitude + 0.001, myLoc.longitude + 0.001);
        addPoint(myLoc.latitude + 0.002, myLoc.longitude - 0.001);
        addPoint(myLoc.latitude + 0.001, myLoc.longitude + 0.004);
        addPoint(myLoc.latitude + 0.012, myLoc.longitude + 0.001);
        addPoint(myLoc.latitude + 0.002, myLoc.longitude - 0.011);
        addPoint(myLoc.latitude + 0.031, myLoc.longitude + 0.024);
        addPoint(myLoc.latitude + 0.003, myLoc.longitude + 0.007);
        addPoint(myLoc.latitude - 0.005, myLoc.longitude + 0.002);
        addPoint(myLoc.latitude - 0.008, myLoc.longitude - 0.003);
        addPoint(myLoc.latitude - 0.002, myLoc.longitude - 0.004);
        addPoint(myLoc.latitude, myLoc.longitude - 0.005);
        addPoint(myLoc.latitude - 0.007, myLoc.longitude - 0.003);
        addPoint(myLoc.latitude + 0.008, myLoc.longitude - 0.001);
        addPoint(myLoc.latitude + 0.004, myLoc.longitude - 0.002);
    }

    private void addPoint(double lat, double lng){
        LatLng markerPoint = new LatLng(lat, lng);
        if (checkDistance(markerPoint)) {
            availableGames.add(
                map.addMarker(new MarkerOptions()
                    .position(markerPoint)
                    .draggable(false)
                    .icon(BitmapDescriptorFactory
                        .defaultMarker(BitmapDescriptorFactory.HUE_RED))));
        }
    }

    private boolean checkDistance(LatLng markerPoint) {
        double distance = Math.sqrt(Math.pow(myLoc.latitude - markerPoint.latitude, 2) +
                Math.pow(myLoc.longitude - markerPoint.longitude, 2));
        boolean result = maxDistance >= distance;
        Toast.makeText(getActivity().getApplicationContext(), "Distance: " + distance, Toast.LENGTH_SHORT).show();
        return result;
    }

    private void assignListeners(){
        setCreateListener();
        setLocationListener();
    }

    private void setCreateListener(){
        btnGame.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent i;
                if(gameClicked){
                    i = new Intent(GameFragment.this.getActivity().getApplicationContext(),
                            TargetActivity.class);
                }else {
                    i = new Intent(GameFragment.this.getActivity().getApplicationContext(),
                            CreateGameActivity.class);
                }
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
                            .radius(maxDistanceDraw).fillColor(backgroundColor)
                            .strokeWidth(5).strokeColor(backgroundColor);
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

//    public class Info{
//        TextView name;
//        TextView players;
//        TextView endTime;
//        ImageView image;
//    }
//
//    @Override
//    public View getInfoWindow(Marker marker) {
//        return null;
//    }
//
//    @Override
//    public View getInfoContents(Marker marker) {
//        LayoutInflater inflater = GameFragment.this.getActivity().getLayoutInflater();
//        View view = inflater.inflate(R.layout.infowindowlayout, null);
//        Info info = new Info();
//
//        info.name = (TextView) view.findViewById(R.id.infoWindowName);
//        info.players = (TextView) view.findViewById(R.id.infoWindowNumPlayer);
//        info.endTime = (TextView) view.findViewById(R.id.infoWindowEndTime);
//        info.image = (ImageView) view.findViewById(R.id.infoWindowImage);
//        return view;
//    }
}