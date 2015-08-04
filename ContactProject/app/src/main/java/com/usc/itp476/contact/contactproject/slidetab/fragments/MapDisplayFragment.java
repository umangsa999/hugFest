package com.usc.itp476.contact.contactproject.slidetab.fragments;

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

enum Messages{
    NoGame, FullGame, CouldNotJoinGame, NoConnection
}

public class MapDisplayFragment extends Fragment
        implements GoogleMap.InfoWindowAdapter, OnMapReadyCallback {
    private final String TAG = this.getClass().getSimpleName();
    private GoogleMap map = null;
    private ImageButton buttonNewGame = null;
    private LatLng myLatLng = null;
    private View rootView = null;
    private Circle radiusCircle = null;
    private final int maxDistanceDraw = 700;
    private final double maxDistance = 0.0075;
    private final int backgroundColor = Color.argb(128, 0, 128, 128);
    private HashMap<Marker, GameMarker> markerToGame = null;
    private LocationListener locationListener = null;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        markerToGame = new HashMap<>();
    }

    @Override
    public View onCreateView(LayoutInflater inflater,
                             ViewGroup container,
                             Bundle savedInstanceState) {

        rootView = inflater.inflate(R.layout.activity_map_display, container, false);

        buttonNewGame = (ImageButton) rootView.findViewById(R.id.buttonStartGameCreation);
        assignButtonListeners();
        return rootView;
    }

    @Override
    public void onResume() {
        super.onResume();
        generateMapFragment();
    }

    private void generateMapFragment(){
        Log.wtf(TAG, "generate a map fragment");
        SupportMapFragment mapFragment = SupportMapFragment.newInstance();
        FragmentTransaction fragmentTransaction = getChildFragmentManager().beginTransaction();
        fragmentTransaction.add(R.id.mapHolder, mapFragment);
        fragmentTransaction.commit();
        mapFragment.getMapAsync(this);
    }

    @Override
    public void onPause() {
        super.onPause();
        ContactApplication.locationManager.removeUpdates(locationListener);}

    @Override
    public void onStop() {
        super.onStop();
        markerToGame.clear();
    }

    private void setMapListeners(){
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
                            displayMessage(Messages.FullGame);
                        } else {
                            displayMessage(Messages.CouldNotJoinGame);
                        }
                    }
                });
            }
        });

        map.setOnMarkerClickListener(new GoogleMap.OnMarkerClickListener() {
            @Override
            public boolean onMarkerClick(Marker marker) {
                map.moveCamera(CameraUpdateFactory.newLatLng(marker.getPosition()));
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
    }

    @Override
    public void onMapReady(GoogleMap googleMap) {
        Log.wtf(TAG, "called onMapReady");
        map = googleMap;
        setupMap();
    }

    private void setupMap(){
        Log.wtf(TAG, "called setupMap");
        map.setInfoWindowAdapter(this);
        map.setMapType(GoogleMap.MAP_TYPE_TERRAIN);
        map.setMyLocationEnabled(true);
        map.setBuildingsEnabled(true);
        map.setIndoorEnabled(true);
        setMapListeners();
        //make sure the create button is still at top
        buttonNewGame.bringToFront();
        rootView.requestLayout();
        rootView.invalidate();
        setLocationListener();
    }

    //this may switch to taking in a LatLng depending on API
    private void addPoint(ParseObject p){
        GameMarker gm = (GameMarker) p;
        LatLng markerPoint = new LatLng(gm.getLocation().getLatitude(),
                gm.getLocation().getLongitude());
        //only add if the start location is within our radius
        markerToGame.put(map.addMarker(new MarkerOptions()
                .position(markerPoint)
                .draggable(false)
                .icon(BitmapDescriptorFactory
                        .defaultMarker(BitmapDescriptorFactory.HUE_RED)))
                , gm);
    }

    private void assignButtonListeners(){
        buttonNewGame.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent i = new Intent(MapDisplayFragment.this.getActivity().getApplicationContext(),
                        CreateGameActivity.class);
                startActivity(i);
            }
        });
    }

    private void setLocationListener() {
        locationListener = new LocationListener() {
            @Override
            public void onLocationChanged(Location location) {
                updateCurrentLocation(location);
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
        ContactApplication.locationManager.requestLocationUpdates(
                LocationManager.GPS_PROVIDER, 5000, 3, locationListener);
        updateCurrentLocation(ContactApplication.locationManager.getLastKnownLocation(LocationManager.GPS_PROVIDER));
    }

    private void updateCurrentLocation(Location currentLocation){
        if (currentLocation != null) {
            myLatLng = new LatLng(currentLocation.getLatitude(), currentLocation.getLongitude());
            //only when we have location access
            if (map != null) {
                map.moveCamera(CameraUpdateFactory.newLatLngZoom(myLatLng, 15));

                createRadius();
                findPoints(); //do a server call for all games
            }
        }else{
            displayMessage(Messages.NoConnection);
        }
    }

    //TODO see if this can be pushed to parse
    //this method is suppose to do server call to find games
    private void findPoints() {
        ParseGeoPoint myLocParse = new ParseGeoPoint(myLatLng.latitude, myLatLng.longitude);
        ParseQuery<ParseObject> query = ParseQuery.getQuery("Marker");
        query.whereEqualTo("isOver", false);
        query.whereLessThan("numberPlayers", ContactApplication.MAX_PLAYERS);
        query.whereWithinRadians("start", myLocParse, maxDistance);
        query.findInBackground(new FindCallback<ParseObject>() {
            @Override
            public void done(List<ParseObject> list, ParseException e) {
                if (e == null && list.isEmpty()) {
                    //If there is no done Parse error but we found no games
                    displayMessage(Messages.NoGame);
                } else if (e != null) {
                    //We found games but there is a parse error
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

    private void createRadius(){
        if (myLatLng != null){
            CircleOptions circleOptions = new CircleOptions()
                    .center(myLatLng)
                    .radius(maxDistanceDraw).fillColor(backgroundColor)
                    .strokeWidth(5).strokeColor(backgroundColor);
            if (radiusCircle != null){
                map.clear();
            }
            radiusCircle = map.addCircle(circleOptions); //keep reference so we can move it
        }
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

    private void displayMessage(Messages message){
        switch(message){
            case NoGame: {
                Toast.makeText(MapDisplayFragment.this.getActivity().getApplicationContext(),
                        "Could not find any games nearby", Toast.LENGTH_SHORT).show();
            }
                break;
            case CouldNotJoinGame: {
                Toast.makeText(getActivity().getApplicationContext(),
                        "Could not join game", Toast.LENGTH_SHORT).show();
            }
                break;
            case FullGame:{
                Toast.makeText(getActivity().getApplicationContext(),
                        "That game is full", Toast.LENGTH_SHORT).show();
            }
                break;
            case NoConnection:{
                Toast.makeText(MapDisplayFragment.this.getActivity().getApplicationContext(),
                        "Please check internet connection", Toast.LENGTH_SHORT).show();
            }
                break;
            default:
                break;
        }
    }
}