package com.usc.itp476.contact.contactproject.POJO;

import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.location.Location;

import com.parse.ParseClassName;
import com.parse.ParseException;
import com.parse.ParseFile;
import com.parse.ParseGeoPoint;
import com.parse.ParseObject;

@ParseClassName("Picture")
public class GamePhoto extends ParseObject{

    public String getID(){
        return getObjectId();
    }

    public ParseFile getImage(){
        return getParseFile("image");
    }

    public byte[] getImageByte(){
        try {
            return getParseFile("image").getData();
        } catch (ParseException e) {
            e.printStackTrace();
        }
        return null;
    }

    public Bitmap getImageBitmap(){
        byte[] data = null;
        try {
            data = getParseFile("image").getData();
        } catch (ParseException e) {
            e.printStackTrace();
        }
        if (data == null) {
            return null;
        }else {
            return BitmapFactory.decodeByteArray(data, 0, data.length);
        }
    }

    public String getGameID(){
        return getString("gameId");
    }

    public String getHunterName(){
        return getString("hunterName");
    }

    public String getTargetName(){
        return getString("targetName");
    }

    public ParseGeoPoint getLocationTaken(){
        return getParseGeoPoint("locationTaken");
    }

    public void setImage(ParseFile file){
        put("image", file);
    }

    public void setGameID(String gameID){
        put("gameId", gameID);
    }

    public void setHunterName(String hunterName){
        put("hunterName", hunterName);
    }

    public void setTargetName(String targetName){
        put("targetName", targetName);
    }

    public void setLocationTaken(Location locationTaken){
        ParseGeoPoint geoPoint = new ParseGeoPoint(locationTaken.getLatitude(),
                                                    locationTaken.getLongitude());
        put("locationTaken", geoPoint);
    }
}
