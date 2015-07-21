package com.usc.itp476.contact.contactproject.POJO;

import org.json.JSONException;
import org.json.JSONObject;

public class GameData {
    public static final String PREFFILE = "com.usc.itp476.contact.contactproject.POJO.GameData.PREFFILE";
    public static final String USER_ID = "com.usc.itp476.contact.contactproject.POJO.GameData.USER_ID";
    public static final String FULL_NAME = "com.usc.itp476.contact.contactproject.POJO.GameData.FULL_NAME";
    public static final String TOTAL_HUGS = "com.usc.itp476.contact.contactproject.POJO.GameData.TOTAL_HUGS";
    private String hostName;
    private String gameEndTime;
    private int playersInGame;
    private String gameID;

    public GameData(){
        gameID = hostName = gameEndTime = "";
        playersInGame = 0;
    }

    //TODO incorporate images
    public GameData(String id, String host, String endTime, int numPlayers){
        gameID = id;
        hostName = host;
        gameEndTime = endTime;
        playersInGame = numPlayers;
    }

    public GameData(JSONObject obj) throws JSONException{
        gameID = obj.getString("id");
        hostName = obj.getString("host");
        gameEndTime = obj.getString("end");
        playersInGame = obj.getInt("players");
    }

    public GameData(GameData other){
        hostName = other.getHostName();
        gameEndTime = other.getEndTime();
        playersInGame = other.getPlayerCount();
    }

    public String getGameID(){
        return gameID;
    }

    public String getHostName(){
        return hostName;
    }

    public String getEndTime(){
        return gameEndTime;
    }

    public int getPlayerCount(){
        return playersInGame;
    }
}