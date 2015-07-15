package com.example.andy.hugfest;

import android.os.AsyncTask;
import android.util.Log;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;

public class HTTPCaller extends AsyncTask<JSONObject, Void, Void> {
    private final String SERVERIP = "52.8.44.124";
    private final String PROTOCOL = "http://";
    private final int READBUFFER = 200;
    private URL url = null;
    private HttpURLConnection urlConnection = null;
    private JSONObject serverResult = null;
    private int responseCode;
    private String responseMessage;

    @Override
    protected Void doInBackground(JSONObject... input) {
        JSONObject instructions = input[0];
        String fURL = "", sURL = "", id = "", query = "", method;

        try {
            if (!instructions.isNull("fURL")) {
                fURL = instructions.getString("fURL");
            }

            if (!instructions.isNull("sURL")) {
                sURL = instructions.getString("sURL");
            }

            if (!instructions.isNull("query")){
                query = instructions.getString("query");
            }

            if (!instructions.isNull("id")) {
                id = instructions.getString("id");
            }

            method = instructions.getString("method");
            createURL(fURL, sURL, query, id);
            connectionSetup(method);

            if (!instructions.isNull("data") && !method.equalsIgnoreCase("GET")) {
                writeContent(instructions.getString("data"));
            }

            generateResult();
        } catch (Exception e) {
            Log.e(HTTPCaller.class.getSimpleName(), "in background: " + e.getMessage());
        }
        return null;
    }

    //take in any number of Strings into urlComponents
    private void createURL(String...urlComponents) throws MalformedURLException {
        String path = PROTOCOL + SERVERIP;
        for (String component : urlComponents){
            if (component.length() > 0) {
                if (component.charAt(0) != '?') { //if it's not a query, add it in with /
                    path += "/" + component;
                } else {
                    path += component;
                }
            }
        }
        Log.wtf(this.getClass().getSimpleName(), "path is: " + path);
        url = new URL(path);
    }

    private void connectionSetup(String type) throws IOException {
        urlConnection = (HttpURLConnection) url.openConnection();
        urlConnection.setReadTimeout(8000);
        urlConnection.setConnectTimeout(10000);
        if (!type.equalsIgnoreCase("GET")) { //GET cannot send a body
            urlConnection.setRequestMethod(type);
        }
        urlConnection.setRequestProperty("Accept", "application/json");
        urlConnection.setRequestProperty("Content-Type", "application/json");
        urlConnection.connect();
    }

    private void writeContent(String dataString) throws JSONException, IOException {
        JSONObject data = new JSONObject(dataString);
        urlConnection.getOutputStream().write(data.toString().getBytes("UTF-8"));
        urlConnection.getOutputStream().flush();
    }

    private void generateResult() throws JSONException, IOException {
        if (url != null) {
            responseCode = urlConnection.getResponseCode();
            responseMessage = urlConnection.getResponseMessage();
            InputStreamReader reader = new InputStreamReader(
                    urlConnection.getInputStream(),
                    "UTF-8"
            );

            char[] array = new char[READBUFFER];
            for (int i = 0; i < READBUFFER; ++i) {
                array[i] = 0;
            }

            reader.read(array);

            int count = 0;
            while (array[count] != 0) {
                count++;
            }

            String resultString = new String(array, 0, count); //only take what is filled
            if ((resultString.length() > 0) && (!resultString.equalsIgnoreCase("null"))) {
                serverResult = new JSONObject(resultString);
            }
        }
    }

    public JSONObject getServerResult(){
        return serverResult;
    }

    public int getResponseCode(){
        return responseCode;
    }

    public String getResponseMessage(){
        return responseMessage;
    }
}