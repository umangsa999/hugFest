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
import android.widget.Button;
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
import com.usc.itp476.contact.contactproject.POJO.GameData;
import com.usc.itp476.contact.contactproject.ingamescreen.CreateGameActivity;
import com.usc.itp476.contact.contactproject.R;
import com.usc.itp476.contact.contactproject.ingamescreen.TargetActivity;

import org.w3c.dom.Text;

import java.util.ArrayList;
import java.util.HashMap;

public class GameFragment extends Fragment
        implements GoogleMap.InfoWindowAdapter, OnMapReadyCallback {
    private GoogleMap map;
    private ImageButton btnGame = null;
    private LatLng myLoc;
    private SupportMapFragment mapFragment;
    private View rootView;
    private Circle radiusCircle = null;
    private final int maxDistanceDraw = 700;
    private final double maxDistance = 0.0075;
    private final int backgroundColor = Color.argb(128, 0, 128, 128);
    private HashMap<Marker, GameData> markerToGame;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        markerToGame = new HashMap<>();
        mapFragment = SupportMapFragment.newInstance();
        FragmentTransaction fragmentTransaction =
                getChildFragmentManager().beginTransaction();
        fragmentTransaction.add(R.id.mapHolder, mapFragment);
        fragmentTransaction.commit();
    }

    @Override
    public View onCreateView(LayoutInflater inflater,
                             ViewGroup container,
                             Bundle savedInstanceState) {

        rootView = (View) inflater.inflate(
                R.layout.activity_home, container, false);

        btnGame = (ImageButton) rootView.findViewById(R.id.btnGame);

        //returning to this fragment after sliding to another
        if (map != null){
            map.clear();
        }else if (mapFragment != null) {
            mapFragment.getMapAsync(this);
        }//TODO make sure this is same map as last time
        //TODO it seems to just create new since getMapAsync again

        btnGame.setBackgroundResource(R.mipmap.ic_create);

        //async call to create and set up map.
        assignListeners();
        return rootView;
    }

    @Override
    public void onResume() {
        super.onResume();
        if (map != null) {
            createRadius();
            findPoints();
        }
    }

    @Override
    public void onPause() {
        super.onPause();
        map.clear();
        markerToGame.clear();
    }

    @Override
    public void onMapReady(GoogleMap googleMap) {
        map = googleMap;
        map.setInfoWindowAdapter(this);
        map.setMapType(GoogleMap.MAP_TYPE_TERRAIN);
        map.setPadding(4, 4, 4, 4);
        map.setMyLocationEnabled(true);
        map.setOnInfoWindowClickListener(new GoogleMap.OnInfoWindowClickListener() {
            @Override
            public void onInfoWindowClick(Marker marker) {
                markerToGame.clear();
                Intent i = new Intent(GameFragment.this.getActivity().getApplicationContext(),
                        TargetActivity.class);
                startActivity(i);
            }
        });
        map.setOnMarkerClickListener(new GoogleMap.OnMarkerClickListener() {
            @Override
            public boolean onMarkerClick(Marker marker) {
                map.moveCamera(CameraUpdateFactory.newLatLngZoom(marker.getPosition(), 15));
                marker.showInfoWindow();
                return true; //done processing user press so don't have Google do any work
            }
        });

        LocationManager LocMan =
                (LocationManager) getActivity().getSystemService(Context.LOCATION_SERVICE);
        Location loc = LocMan.getLastKnownLocation(LocationManager.GPS_PROVIDER);

        //only when we have location access
        if (loc != null) {
            myLoc = new LatLng(loc.getLatitude(), loc.getLongitude());
            map.moveCamera(CameraUpdateFactory.newLatLngZoom(myLoc, 15));

            findPoints(); //do a server call for all games
            createRadius();
        }else{
            Toast.makeText(GameFragment.this.getActivity().getApplicationContext(),
                    "No connection to find games.", Toast.LENGTH_SHORT).show();
        }

        //make sure the create button is still at top
        btnGame.bringToFront();
        rootView.requestLayout();
        rootView.invalidate();
        setLocationListener();
    }

    //this method is suppose to do server call to find games
    private void findPoints() {
        addPoint(myLoc.latitude + 0.001, myLoc.longitude + 0.001,
                new GameData("1234", "Chris Lee", "5:00pm", 1));
        addPoint(myLoc.latitude + 0.002, myLoc.longitude - 0.001,
                new GameData("2345", "Ryan Zhou", "12:00pm", 20));
        addPoint(myLoc.latitude + 0.001, myLoc.longitude + 0.004,
                new GameData("3456", "Mike Lee", "8:00am", 2));
        addPoint(myLoc.latitude + 0.012, myLoc.longitude + 0.001,
                new GameData("4567", "Nathan Greenfield", "10:00pm", 16));
        addPoint(myLoc.latitude + 0.002, myLoc.longitude - 0.011,
                new GameData("5678", "Paulina Gray", "7:00pm", 18));
        addPoint(myLoc.latitude + 0.031, myLoc.longitude + 0.024,
                new GameData("6789", "Raymond Kim", "4:00pm", 5));
        addPoint(myLoc.latitude + 0.003, myLoc.longitude + 0.007,
                new GameData("7890", "Rob Parke", "10:00am", 15));
        addPoint(myLoc.latitude - 0.005, myLoc.longitude + 0.002,
                new GameData("0987", "Michael Crowley", "5:00pm", 20));
        addPoint(myLoc.latitude - 0.008, myLoc.longitude - 0.003,
                new GameData("9876", "Trina Gregory", "7:45pm", 14));
        addPoint(myLoc.latitude - 0.002, myLoc.longitude - 0.004,
                new GameData("8765", "Sanjay Madhav", "2:30am", 8));
        addPoint(myLoc.latitude, myLoc.longitude - 0.005,
                new GameData("7654", "Andy Tse", "1:00pm", 3));
        addPoint(myLoc.latitude - 0.007, myLoc.longitude - 0.003,
                new GameData("6543", "Manjot Chahal", "2:10pm", 13));
        addPoint(myLoc.latitude + 0.008, myLoc.longitude - 0.001,
                new GameData("5432", "Ura Shurty", "11:00pm", 1));
        addPoint(myLoc.latitude + 0.004, myLoc.longitude - 0.002,
                new GameData("4321", "Inna Peench", "1:00am", 7));
    }

    //this may switch to taking in a LatLng depending on API
    private void addPoint(double lat, double lng, GameData d){
        LatLng markerPoint = new LatLng(lat, lng);
        //only add if the start location is within our radius
        if (checkDistance(markerPoint)) {
            markerToGame.put(map.addMarker(new MarkerOptions()
                    .position(markerPoint)
                    .draggable(false)
                    .icon(BitmapDescriptorFactory
                        .defaultMarker(BitmapDescriptorFactory.HUE_RED)))
            ,d);
        }
    }

    private boolean checkDistance(LatLng markerPoint) {
        double distance = Math.sqrt(Math.pow(myLoc.latitude - markerPoint.latitude, 2) +
                Math.pow(myLoc.longitude - markerPoint.longitude, 2));
        boolean result = maxDistance >= distance;
        return result;
    }

    private void assignListeners(){
        setCreateListener();
    }

    private void setCreateListener(){
        btnGame.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent i = new Intent(GameFragment.this.getActivity().getApplicationContext(),
                            CreateGameActivity.class);
                startActivity(i);
            }
        });
    }

    private void createRadius(){
        CircleOptions circleOptions = new CircleOptions()
                .center(myLoc)
                .radius(maxDistanceDraw).fillColor(backgroundColor)
                .strokeWidth(5).strokeColor(backgroundColor);
        radiusCircle = map.addCircle(circleOptions); //keep reference so we can move it
    }

    private void setLocationListener() {
        LocationManager l =
                (LocationManager) getActivity().getSystemService(Context.LOCATION_SERVICE);
        LocationListener locationListener = new LocationListener() {
            @Override
            public void onLocationChanged(Location location) {
                myLoc = new LatLng(location.getLatitude(), location.getLongitude());
                map.moveCamera(CameraUpdateFactory.newLatLng(myLoc));

                //move the radius with the user
                if (radiusCircle != null) {
                    radiusCircle.setCenter(myLoc);
                } else {
                    //create the radius!
                    createRadius();
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

    @Override
    public View getInfoWindow(Marker marker) {
        return null;
    }

    @Override
    public View getInfoContents(Marker marker) {
        LayoutInflater inflater = GameFragment.this.getActivity().getLayoutInflater();
        View view = inflater.inflate(R.layout.infowindowlayout, null);

        TextView name = (TextView) view.findViewById(R.id.infoWindowName);
        TextView players = (TextView) view.findViewById(R.id.infoWindowNumPlayer);
        TextView endTime = (TextView) view.findViewById(R.id.infoWindowEndTime);
        ImageView image = (ImageView) view.findViewById(R.id.infoWindowImage);

        GameData data = markerToGame.get(marker);
        name.setText(data.getHostName());
        players.setText(String.valueOf(data.getPlayerCount()));
        endTime.setText(data.getEndTime());
        //TODO make images dynamic

        return view;
    }
}