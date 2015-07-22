package com.usc.itp476.contact.contactproject.POJO;

import com.parse.ParseClassName;
import com.parse.ParseGeoPoint;
import com.parse.ParseObject;
import com.parse.ParseUser;

import org.json.JSONException;
import org.json.JSONObject;

@ParseClassName("Marker")
public class GameMarker extends ParseObject {
    final String TAG = this.getClass().getSimpleName();
    public static final String PREFFILE = "com.usc.itp476.contact.contactproject.POJO.GameMarker.PREFFILE";
    public static final String USER_ID = "com.usc.itp476.contact.contactproject.POJO.GameMarker.USER_ID";
    public static final String FULL_NAME = "com.usc.itp476.contact.contactproject.POJO.GameMarker.FULL_NAME";
    public static final String TOTAL_HUGS = "com.usc.itp476.contact.contactproject.POJO.GameMarker.TOTAL_HUGS";

    public void setHostName(){
        put("host", ParseUser.getCurrentUser());
    }

    public void setPlayerCount(int numPlayers){
        put("numberPlayers", numPlayers);
    }

    public void setPoints(int numPoints){
        put("pointsToWin", numPoints);
    }

    public String getHostName(){
        return getParseUser("host").getUsername();
    }

    public String getPlayerCount(){
        return String.valueOf(getInt("numberPlayers"));
    }

    public String getPoints(){
        return String.valueOf(getInt("pointsToWin"));
    }

    public void setLocation(ParseGeoPoint location){
        put("start", location );
    }

    public ParseGeoPoint getLocation(){
        return getParseGeoPoint("start");
    }
}