// THIS CODE AND INFORMATION ARE PROVIDED "AS IS" WITHOUT WARRANTY OF ANY
// KIND, EITHER EXPRESSED OR IMPLIED, INCLUDING BUT NOT LIMITED TO THE
// IMPLIED WARRANTIES OF MERCHANTABILITY AND/OR FITNESS FOR A
// PARTICULAR PURPOSE.
//
// <author>Chris Lee and Ryan Zhou</author>
// <email>wannabedev.ta@gmail.com</email>
// <date>2015-08-14</date>
package com.usc.itp476.contact.contactproject.POJO;

import com.parse.ParseClassName;
import com.parse.ParseGeoPoint;
import com.parse.ParseObject;
import com.parse.ParseQuery;
import com.parse.ParseUser;

@ParseClassName("Game")
public class GameData extends ParseObject {
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

    public boolean getIsOver(){
        return getBoolean("isOver");
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
        put("host", ParseUser.getCurrentUser());
    }

    public void setPointsToWin(int numPoints){
        put("pointsToWin", numPoints);
    }

    public void setMarker(GameMarker m){
        put("marker", m);
    }

    public void setNumPlayers(int numPlayers){
        put("numberPlayers", numPlayers);
    }

    public void setGameOver(boolean isOver){
        put("isOver", isOver);
    }
}