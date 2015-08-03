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
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

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
import com.parse.FunctionCallback;
import com.parse.ParseCloud;
import com.parse.ParseException;
import com.parse.ParseGeoPoint;
import com.parse.ParseObject;
import com.parse.ParseQuery;
import com.parse.ParseUser;
import com.usc.itp476.contact.contactproject.ContactApplication;
import com.usc.itp476.contact.contactproject.POJO.GameMarker;
import com.usc.itp476.contact.contactproject.R;
import com.usc.itp476.contact.contactproject.ingamescreen.CreateGameActivity;
import com.usc.itp476.contact.contactproject.ingamescreen.TargetActivity;

import java.util.HashMap;
import java.util.List;

public class MapDisplayFragment extends Fragment
        implements GoogleMap.InfoWindowAdapter, OnMapReadyCallback {
    private final String TAG = this.getClass().getSimpleName();
    private GoogleMap map = null;
    private ImageButton buttonNewGame = null;
    private LatLng myLoc = null;
    private SupportMapFragment mapFragment = null;
    private View rootView = null;
    private Circle radiusCircle = null;
    private final int maxDistanceDraw = 700;
    private final double maxDistance = 0.0075;
    private final int backgroundColor = Color.argb(128, 0, 128, 128);
    private HashMap<Marker, GameMarker> markerToGame;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        markerToGame = new HashMap<>();
        mapFragment = SupportMapFragment.newInstance();
        FragmentTransaction fragmentTransaction = getChildFragmentManager().beginTransaction();
        fragmentTransaction.add(R.id.mapHolder, mapFragment);
        fragmentTransaction.commit();
        mapFragment.getMapAsync(this);
    }

    @Override
    public View onCreateView(LayoutInflater inflater,
                             ViewGroup container,
                             Bundle savedInstanceState) {

        rootView = inflater.inflate(
                R.layout.activity_map_display, container, false);

        buttonNewGame = (ImageButton) rootView.findViewById(R.id.btnGame);

        //TODO make sure this is same map as last time
        //TODO it seems to just create new since getMapAsync again

        //async call to create and set up map.
        assignListeners();

        return rootView;
    }

    @Override
    public void onResume() {
        super.onResume();
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
        }else {
            createRadius();
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
        map.setMyLocationEnabled(true);
        map.setOnInfoWindowClickListener(new GoogleMap.OnInfoWindowClickListener() {
            @Override
            public void onInfoWindowClick(Marker marker) {
                HashMap<String, Object> params = new HashMap<>();
                params.put("userID", ParseUser.getCurrentUser().getObjectId());
                params.put("gameID", markerToGame.get(marker).getGameID());
                ParseCloud.callFunctionInBackground("joinGame", params, new FunctionCallback<HashMap<String, Object>>() {
                    @Override
                    public void done(HashMap<String, Object> map, ParseException e) {
                        if (e == null) {
                            Intent i = new Intent(
                                    MapDisplayFragment.this.getActivity().getApplicationContext(),
                                    TargetActivity.class);
                            i.putExtra(ContactApplication.JOINEDGAME, true);
                            i.putExtra(ContactApplication.MAXPOINTS, (Integer) map.get("points"));
                            i.putExtra(ContactApplication.GAMEID, (String) map.get("gameID"));
                            markerToGame.clear();
                            startActivity(i);
                        } else if (e.getCode() == ContactApplication.MAX_PLAYERS) {
                            Toast.makeText(getActivity().getApplicationContext(),
                                    "That game is full", Toast.LENGTH_SHORT).show();
                        }else{
                            Toast.makeText(getActivity().getApplicationContext(),
                                    "Could not join game", Toast.LENGTH_SHORT).show();
                        }
                    }
                });
            }
        });

        map.setOnMarkerClickListener(new GoogleMap.OnMarkerClickListener() {
            @Override
            public boolean onMarkerClick(Marker marker) {
                map.moveCamera(CameraUpdateFactory.newLatLngZoom(marker.getPosition(), 15));
                try {
                    GameMarker gm = markerToGame.get(marker).fetchIfNeeded();
                    markerToGame.put(marker, gm);
                } catch (ParseException e) {
                    e.printStackTrace();
                }
                marker.showInfoWindow();
                return true; //done processing user press so don't have Google do any work
            }
        });

        setupLocationChecks();
        //make sure the create button is still at top
        buttonNewGame.bringToFront();
        rootView.requestLayout();
        rootView.invalidate();
        setLocationListener();
    }

    private void setupLocationChecks(){
        LocationManager locationManager =
                (LocationManager) getActivity().getSystemService(Context.LOCATION_SERVICE);
        Location location = locationManager.getLastKnownLocation(LocationManager.GPS_PROVIDER);

        //only when we have location access
        if (location != null && map != null) {
            myLoc = new LatLng(location.getLatitude(), location.getLongitude());
            map.moveCamera(CameraUpdateFactory.newLatLngZoom(myLoc, 15));

            findPoints(); //do a server call for all games
            createRadius();
        }else{
            Toast.makeText(MapDisplayFragment.this.getActivity().getApplicationContext(),
                    "No connection to find games.", Toast.LENGTH_SHORT).show();
        }
    }

    //TODO see if this can be pushed to parse
    //this method is suppose to do server call to find games
    private void findPoints() {
        ParseGeoPoint myLocParse = new ParseGeoPoint(myLoc.latitude, myLoc.longitude);
        ParseQuery<ParseObject> query = ParseQuery.getQuery("Marker");
        query.whereEqualTo("isOver", false);
        query.whereLessThan("numberPlayers", ContactApplication.MAX_PLAYERS);
        query.whereWithinRadians("start",myLocParse,maxDistance);
        query.findInBackground(new FindCallback<ParseObject>() {
            @Override
            public void done(List<ParseObject> list, ParseException e) {
                if (e == null && list.size() < 1) {
                    //If there is no done Parse error but we found no games
                    Toast.makeText(MapDisplayFragment.this.getActivity().getApplicationContext(),
                            "Could find any games nearby", Toast.LENGTH_SHORT).show();
                } else if (e != null) {
                    //We found games but there is a parse error
                    Toast.makeText(MapDisplayFragment.this.getActivity().getApplicationContext(),
                            "Please check internet connection", Toast.LENGTH_SHORT).show();
                    Log.wtf(TAG, e.getLocalizedMessage());
                } else {
                    //There is no done parse error and we found games
                    for (ParseObject p : list) {
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
        buttonNewGame.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent i = new Intent(MapDisplayFragment.this.getActivity().getApplicationContext(),
                        CreateGameActivity.class);
                startActivity(i);
            }
        });
    }

    private void createRadius(){
        if (map != null) {
            CircleOptions circleOptions = new CircleOptions()
                    .center(myLoc)
                    .radius(maxDistanceDraw).fillColor(backgroundColor)
                    .strokeWidth(5).strokeColor(backgroundColor);
            radiusCircle = map.addCircle(circleOptions); //keep reference so we can move it
        }
    }

    private void setLocationListener() {
        LocationManager locationManager =
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
                    if (map != null)
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
        locationManager.requestLocationUpdates(
                LocationManager.GPS_PROVIDER, 5000, 3, locationListener);
    }

    @Override
    public View getInfoWindow(Marker marker) {
        return null;
    }

    @Override
    public View getInfoContents(Marker marker) {
        LayoutInflater inflater = MapDisplayFragment.this.getActivity().getLayoutInflater();
        View view = inflater.inflate(R.layout.infowindowlayout, null);

        TextView name = (TextView) view.findViewById(R.id.infoWindowName);
        TextView players = (TextView) view.findViewById(R.id.infoWindowNumPlayer);
        TextView points = (TextView) view.findViewById(R.id.infoWindowPointsToWin);

        GameMarker data = markerToGame.get(marker);
        name.setText(data.getHostName());
        points.setText(String.valueOf(data.getPoints()));
        players.setText(String.valueOf(data.getPlayerCount()));

        return view;
    }
}