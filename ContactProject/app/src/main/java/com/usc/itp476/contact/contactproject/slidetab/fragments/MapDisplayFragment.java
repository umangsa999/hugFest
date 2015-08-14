// THIS CODE AND INFORMATION ARE PROVIDED "AS IS" WITHOUT WARRANTY OF ANY
// KIND, EITHER EXPRESSED OR IMPLIED, INCLUDING BUT NOT LIMITED TO THE
// IMPLIED WARRANTIES OF MERCHANTABILITY AND/OR FITNESS FOR A
// PARTICULAR PURPOSE.
//
// <author>Chris Lee and Ryan Zhou</author>
// <email>wannabedev.ta@gmail.com</email>
// <date>2015-08-14</date>

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
import com.usc.itp476.contact.contactproject.slidetab.AllTabActivity;

import java.util.HashMap;
import java.util.List;

enum Messages{
    NoGame, FullGame, CouldNotJoinGame, NoConnection
}

public class MapDisplayFragment extends Fragment
        implements GoogleMap.InfoWindowAdapter, OnMapReadyCallback {
    private final String TAG = this.getClass().getSimpleName();
    private final int MAXDISTANCEDRAW = 700;
    private final double MAXDISTANCE = 0.0075;
    private final int BACKGROUNDCOLOR = Color.argb(128, 0, 128, 128);
    private final int REQUEST_FREQUENCY = 10000;
    private final int REQUEST_DISTANCE = 3;
    private final int ZOOM_LEVEL = 15;
    private GoogleMap map = null;
    private ImageButton buttonNewGame = null;
    private LatLng myLatLng = null;
    private View rootView = null;
    private Circle radiusCircle = null;
    private HashMap<Marker, GameMarker> markerToGame = null;
    private LocationListener locationListener = null;
    private boolean isMonitoringLocation = false;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        markerToGame = new HashMap<>();
        //only when this tab is being shown, create the map
        if (AllTabActivity.currentTab == 0){
            generateMapFragment();
        }
    }

    @Override
    public View onCreateView(LayoutInflater inflater,
                             ViewGroup container,
                             Bundle savedInstanceState) {

        rootView = inflater.inflate(R.layout.activity_map_display, container, false);

        buttonNewGame = (ImageButton) rootView.findViewById(R.id.buttonStartGameCreation);
        //pressing the button will transition to creating a new game
        buttonNewGame.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent i = new Intent(MapDisplayFragment.this.getActivity().getApplicationContext(),
                        CreateGameActivity.class);
                startActivity(i);
            }
        });

        return rootView;
    }

    public void generateMapFragment(){
        //only when the current activity exists, can we make a map
        if (getActivity() != null) {
            SupportMapFragment mapFragment = SupportMapFragment.newInstance();
            FragmentTransaction fragmentTransaction = getChildFragmentManager().beginTransaction();
            fragmentTransaction.add(R.id.mapHolder, mapFragment);
            fragmentTransaction.commit();
            mapFragment.getMapAsync(this);
        }
    }

    @Override
    public void onPause() {
        super.onPause();
        //we're going to stop checking for location and games
        isMonitoringLocation = false;
        if (locationListener != null) {
            ContactApplication.locationManager.removeUpdates(locationListener);
        }
    }

    @Override
    public void onStop() {
        super.onStop();
        //since we're destroying the map, lets clear out the data of all the games
        markerToGame.clear();
    }

    private void setMapListeners(){
        //set up so clicking the info windows will join the game that matches the marker
        map.setOnInfoWindowClickListener(new GoogleMap.OnInfoWindowClickListener() {
            @Override
            public void onInfoWindowClick(Marker marker) {
                HashMap<String, Object> params = new HashMap<>();
                params.put("userID", ParseUser.getCurrentUser().getObjectId());
                params.put("gameID", markerToGame.get(marker).getGameID());
                ParseCloud.callFunctionInBackground("joinGame", params, new FunctionCallback<HashMap<String, Object>>() {
                    @Override
                    public void done(HashMap<String, Object> map, ParseException e) {
                        //successfully joined game so transition to that screen
                        if (e == null) {
                            Intent i = new Intent(
                                    MapDisplayFragment.this.getActivity().getApplicationContext(),
                                    TargetActivity.class);
                            //extras allow us to send data over to the new screen
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

        //set up so clicking on markers will show the info window
        //                                   move map to center on marker
        //                                   update the latest game marker for that info window
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
        //this is called when the map is done being generated. This is the first time we have
        //access to the map, so we should set it up to the specifications we want
        map = googleMap;
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

    private void setLocationListener() {
        isMonitoringLocation = true;
        //create a new LocationListener with these simple specifications
        locationListener = new LocationListener() {
            @Override
            public void onLocationChanged(Location location) {
                updateCurrentLocation(location);
            }

            @Override
            public void onStatusChanged(String provider, int status, Bundle extras) { }

            @Override
            public void onProviderEnabled(String provider) { }

            @Override
            public void onProviderDisabled(String provider) { }
        };
        //actually set the GPS to request update every REQUEST_FREQUENCY milliseconds
        //                                    or every REQUEST_DISTANCE meters
        ContactApplication.locationManager.requestLocationUpdates(
                LocationManager.GPS_PROVIDER, REQUEST_FREQUENCY, REQUEST_DISTANCE, locationListener);
        updateCurrentLocation(ContactApplication.locationManager.getLastKnownLocation(LocationManager.GPS_PROVIDER));
    }

    private void updateCurrentLocation(Location currentLocation){
        //we only want updates when we WANT to monitor, but even if we stop WANTING to monitor, we might
        //have set out a request some time ago that responds after we stop
        if (isMonitoringLocation) {
            //only when we have location access
            if (currentLocation != null) {
                myLatLng = new LatLng(currentLocation.getLatitude(), currentLocation.getLongitude());
                if (map != null) {
                    map.moveCamera(CameraUpdateFactory.newLatLngZoom(myLatLng, ZOOM_LEVEL));
                    createRadius();
                    findPoints();
                }
            } else {
                displayMessage(Messages.NoConnection);
            }
        }
    }

    private void createRadius(){
        //this shows the radius of effect for finding games in the area
        if (myLatLng != null){
            CircleOptions circleOptions = new CircleOptions()
                    .center(myLatLng)
                    .radius(MAXDISTANCEDRAW).fillColor(BACKGROUNDCOLOR)
                    .strokeWidth(5).strokeColor(BACKGROUNDCOLOR);
            if (radiusCircle != null){
                map.clear();
            }
            radiusCircle = map.addCircle(circleOptions); //keep reference so we can move it
        }
    }

    private void findPoints() {
        //find games that are within MAXDISTANCE range from my current location
        //                have not ended
        //                are not full
        ParseGeoPoint myLocParse = new ParseGeoPoint(myLatLng.latitude, myLatLng.longitude);
        ParseQuery<ParseObject> query = ParseQuery.getQuery("Marker");
        query.whereEqualTo("isOver", false);
        query.whereLessThan("numberPlayers", ContactApplication.MAX_PLAYERS);
        query.whereWithinRadians("start", myLocParse, MAXDISTANCE);
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
                    //we found games and no error
                    for (ParseObject p : list) {
                        addPoint(p);
                    }
                }
            }
        });
    }

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

    @Override
    public View getInfoWindow(Marker marker) {
        //this must be overridden. Modifying this will change the appearance of the info window on map
        return null;
    }

    @Override
    public View getInfoContents(Marker marker) {
        //set the appearance of the info window and give it information
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