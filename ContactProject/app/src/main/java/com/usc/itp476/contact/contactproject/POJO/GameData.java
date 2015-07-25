package com.usc.itp476.contact.contactproject.POJO;

import com.parse.ParseClassName;
import com.parse.ParseGeoPoint;
import com.parse.ParseObject;
import com.parse.ParseQuery;
import com.parse.ParseUser;

@ParseClassName("Game")
public class GameData extends ParseObject {
	final String TAG = this.getClass().getSimpleName();
	
	public String getGameID(){
		return getObjectId();
	}
	
	public String getMarkerID(){
        return getString("marker");
    }

    public String getHostName(){
        return getParseUser("host").getUsername();
    }

    public int getPointsToWin(){
        return getInt("pointsToWin");
    }

    public ParseGeoPoint getLocation(){
        return getParseGeoPoint("start");
    }

    public ParseQuery<ParseObject> getPlayersQuery(){
        return getRelation("players").getQuery();
    }

    public int getNumberPlayers(){
        return getInt("numberPlayers");
    }

    public void setLocation(ParseGeoPoint location){
        put("start", location);
    }

    public void setHostName(){
        put("host", ParseUser.getCurrentUser().getUsername());
    }

    public void setPointsToWin(int numPoints){
        put("pointsToWin", numPoints);
    }

    public void setMarker(String m){
        put("marker", m);
    }

    public void setNumPlayers(int numPlayers){
        put("numberPlayers", numPlayers);
    }
}