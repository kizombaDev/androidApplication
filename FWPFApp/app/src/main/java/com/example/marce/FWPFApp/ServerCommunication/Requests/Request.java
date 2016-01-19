package com.example.marce.FWPFApp.ServerCommunication.Requests;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.InputStream;
import java.io.InputStreamReader;

/**
 * Created by Paddy on 25.12.2015.
 */
public abstract class Request {
    protected String serverUrl = "http://th-app.azurewebsites.net/api";
    protected String requestUrl;
    protected String requestType;

    public Request() {

    }

    protected String readJSONStringFromInputStream(InputStream inputStream) {
        String returnJSONString = "";
        try {
            BufferedReader streamReader = new BufferedReader(new InputStreamReader(inputStream, "UTF-8"));
            StringBuilder responseStrBuilder = new StringBuilder();

            String inputStr;
            while ((inputStr = streamReader.readLine()) != null)
                responseStrBuilder.append(inputStr);

            returnJSONString = responseStrBuilder.toString();
        } catch (Exception e) {
            e.printStackTrace();
        }
        return returnJSONString;
    }

    protected JSONArray readJSONArrayFromInputStream(InputStream inputStream) {
        JSONArray returnJSONArray = null;
        try {
            returnJSONArray = new JSONArray(readJSONStringFromInputStream(inputStream));
        } catch (JSONException e) {
            e.printStackTrace();
        }
        return returnJSONArray;
    }

    protected JSONObject readJSONObjectFromInputStream(InputStream inputStream) {
        JSONObject returnJSONObject = null;
        try {
            returnJSONObject = new JSONObject(readJSONStringFromInputStream(inputStream));
        } catch (JSONException e) {
            e.printStackTrace();
        }
        return returnJSONObject;
    }
}
