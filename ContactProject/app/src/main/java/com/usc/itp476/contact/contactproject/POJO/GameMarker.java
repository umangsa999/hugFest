// THIS CODE AND INFORMATION ARE PROVIDED "AS IS" WITHOUT WARRANTY OF ANY
// KIND, EITHER EXPRESSED OR IMPLIED, INCLUDING BUT NOT LIMITED TO THE
// IMPLIED WARRANTIES OF MERCHANTABILITY AND/OR FITNESS FOR A
// PARTICULAR PURPOSE.
//
// <author>Chris Lee and Ryan Zhou</author>
// <email>wannabedev.ta@gmail.com</email>
// <date>2015-08-14</date>

package com.usc.itp476.contact.contactproject.POJO;

import android.util.Log;

import com.parse.ParseClassName;
import com.parse.ParseException;
import com.parse.ParseGeoPoint;
import com.parse.ParseObject;
import com.parse.ParseUser;

@ParseClassName("Marker")
public class GameMarker extends ParseObject {
    final String TAG = this.getClass().getSimpleName();
    private ParseUser host = null;
    private GameData game = null;

    public String getMarkerID(){
        return getGameID();
    }

    public String getGameID(){
        if (game == null){
            game = (GameData) get("game");
            try{
                game.fetch();
            }catch(ParseException pe){
                Log.wtf(TAG, pe);
            }
        }
        return game.getObjectId();
    }

    public void setGameID(GameData game){
        put("game", game);
    }

    public void setGameOver(boolean isOver){
        put("isOver", isOver);
    }

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
        fetchHost();
        return host.getString("name");
    }

    public ParseUser getHost(){
        fetchHost();
        return host;
    }

    private void fetchHost(){
        if (host == null){
            ParseUser obj = (ParseUser) get("host");
            try {
                obj.fetch();
            } catch (ParseException e) {
                Log.wtf(TAG, e.getLocalizedMessage());
            }
            host =  obj;
        }
    }

    public String getPlayerCount(){
        try {
            return String.valueOf(fetch().getInt("numberPlayers"));
        } catch (ParseException e) {
            e.printStackTrace();
            return null;
        }
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

    public boolean getIsOver(){
        return getBoolean("isOver");
    }
}