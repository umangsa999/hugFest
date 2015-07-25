package com.usc.itp476.contact.contactproject.slidetab.fragments;

import android.content.Context;
import android.content.Intent;
import android.graphics.Color;
import android.location.Location;
import android.location.LocationListener;
import android.location.LocationManager;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentTransaction;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.facebook.login.LoginManager;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.BitmapDescriptorFactory;
import com.google.android.gms.maps.model.Circle;
import com.google.android.gms.maps.model.CircleOptions;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.Marker;
import com.google.android.gms.maps.model.MarkerOptions;
import com.parse.FindCallback;
import com.parse.ParseException;
import com.parse.ParseGeoPoint;
import com.parse.ParseObject;
import com.parse.ParseQuery;
import com.parse.ParseUser;
import com.usc.itp476.contact.contactproject.POJO.GameMarker;
import com.usc.itp476.contact.contactproject.R;
import com.usc.itp476.contact.contactproject.StartActivity;
import com.usc.itp476.contact.contactproject.ingamescreen.CreateGameActivity;
import com.usc.itp476.contact.contactproject.ingamescreen.TargetActivity;

import java.util.HashMap;
import java.util.List;

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
    public static final int MAX_PLAYERS = 20;
    private final int backgroundColor = Color.argb(128, 0, 128, 128);
    private HashMap<Marker, GameMarker> markerToGame;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        markerToGame = new HashMap<>();
        mapFragment = SupportMapFragment.newInstance();
        FragmentTransaction fragmentTransaction =
                getChildFragmentManager().beginTransaction();
        fragmentTransaction.add(R.id.mapHolder, mapFragment);
        fragmentTransaction.commit();
        mapFragment.getMapAsync(this);
    }

    @Override
    public View onCreateView(LayoutInflater inflater,
                             ViewGroup container,
                             Bundle savedInstanceState) {

        rootView = (View) inflater.inflate(
                R.layout.activity_home, container, false);

        btnGame = (ImageButton) rootView.findViewById(R.id.btnGame);

        //TODO make sure this is same map as last time
        //TODO it seems to just create new since getMapAsync again

        //async call to create and set up map.
        assignListeners();

        if (map == null) {
            if (mapFragment != null){
                map = mapFragment.getMap();
            }else{
                mapFragment = SupportMapFragment.newInstance();
                FragmentTransaction fragmentTransaction =
                        getChildFragmentManager().beginTransaction();
                fragmentTransaction.add(R.id.mapHolder, mapFragment);
                fragmentTransaction.commit();
                mapFragment.getMapAsync(this);
            }
        }

        return rootView;
    }

    @Override
    public void onResume() {
        super.onResume();
        if (map != null) {
            createRadius();
            //findPoints();
        }
    }

    @Override
    public void onPause() {
        super.onPause();
        if (map != null) {
            map.clear();
            markerToGame.clear();
        }
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

        setupLocationChecks();
        //make sure the create button is still at top
        btnGame.bringToFront();
        rootView.requestLayout();
        rootView.invalidate();
        setLocationListener();
    }

    private void setupLocationChecks(){
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
    }

    //this method is suppose to do server call to find games
    private void findPoints() {
        ParseGeoPoint myLocParse = new ParseGeoPoint(myLoc.latitude, myLoc.longitude);
        ParseQuery<ParseObject> query = ParseQuery.getQuery("Marker");
        query.whereNotEqualTo("host", ParseUser.getCurrentUser() );
        query.whereLessThan("numberPlayers", MAX_PLAYERS);
        query.whereWithinRadians("start", myLocParse, maxDistance);
        query.findInBackground(new FindCallback<ParseObject>() {
            @Override
            public void done(List<ParseObject> list, ParseException e) {
                if (e != null || list.size() < 1){
                    Toast.makeText(getActivity().getApplicationContext(),
                            "Could find any games nearby", Toast.LENGTH_SHORT).show();
                }else{
                    for (ParseObject p : list){
                        addPoint(p);
                    }
                }
            }
        });
    }

    //this may switch to taking in a LatLng depending on API
    private void addPoint(ParseObject p){
        GameMarker gm = (GameMarker) p;
        LatLng markerPoint = new LatLng(gm.getLocation().getLatitude(),
                gm.getLocation().getLongitude());
        //only add if the start location is within our radius
        if (checkDistance(markerPoint)) {
            markerToGame.put(map.addMarker(new MarkerOptions()
                    .position(markerPoint)
                    .draggable(false)
                    .icon(BitmapDescriptorFactory
                        .defaultMarker(BitmapDescriptorFactory.HUE_RED)))
            ,gm);
        }
    }

    private boolean checkDistance(LatLng markerPoint) {
        double distance = Math.sqrt(Math.pow(myLoc.latitude - markerPoint.latitude, 2) +
                Math.pow(myLoc.longitude - markerPoint.longitude, 2));
        return maxDistance >= distance;
    }

    private void assignListeners(){
        btnGame.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent i = new Intent(GameFragment.this.getActivity().getApplicationContext(),
                        CreateGameActivity.class);
                startActivity(i);
//                ParseUser.logOut();
//                LoginManager.getInstance().logOut();
//
//                Intent i = new Intent();
//                getActivity().setResult(StartActivity.RESULT_LOGOUT);
//                getActivity().finish();

            }
        });
    }

    private void createRadius(){
        CircleOptions circleOptions = new CircleOptions()
                .center(myLoc)
                .radius(maxDistanceDraw).fillColor(backgroundColor)
                .strokeWidth(5).strokeColor(backgroundColor);
//        radiusCircle = map.addCircle(circleOptions); //keep reference so we can move it
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
                    findPoints();
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
        TextView points = (TextView) view.findViewById(R.id.infoWindowPointsToWin);
        ImageView image = (ImageView) view.findViewById(R.id.infoWindowImage);

        GameMarker data = markerToGame.get(marker);
        name.setText(data.getHostName());
        points.setText(String.valueOf(data.getPoints()));
        players.setText(String.valueOf(data.getPlayerCount()));
        //TODO make images dynamic

        return view;
    }
}