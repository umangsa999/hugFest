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
    public static final String PREFFILE = "com.usc.itp476.contact.contactproject.POJO.GameMarker.PREFFILE";
    public static final String USER_ID = "com.usc.itp476.contact.contactproject.POJO.GameMarker.USER_ID";
    public static final String FULL_NAME = "com.usc.itp476.contact.contactproject.POJO.GameMarker.FULL_NAME";
    public static final String TOTAL_HUGS = "com.usc.itp476.contact.contactproject.POJO.GameMarker.TOTAL_HUGS";
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