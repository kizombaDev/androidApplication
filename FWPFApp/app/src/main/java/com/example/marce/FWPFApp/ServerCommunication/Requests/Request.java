package com.example.marce.FWPFApp.ServerCommunication.Requests;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.InputStream;
import java.io.InputStreamReader;

/*
*
* this class represents is superclass for all types of requests in the app
* it stores the serverurl and offers functions to transform the request responses to json objects/arrays
*
* Datei: Request  Autor: Patrick
* Datum: 25.12.2015 Version: 1.2
* Historie:
* 31.12.15: the json reading functions were moved from the subclasses to this class
* 29.12.15: class implemented
* 25.12.15: class created
*/

public abstract class Request {
    protected String serverUrl = "http://th-app.azurewebsites.net/api";
    protected String requestUrl;
    protected String requestType;

    public Request() {

    }

    /**
     * read the (json) string from the input stream
     */
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

    /**
     * transform the json string to a jsonArray
     */
    protected JSONArray readJSONArrayFromInputStream(InputStream inputStream) {
        JSONArray returnJSONArray = null;
        try {
            returnJSONArray = new JSONArray(readJSONStringFromInputStream(inputStream));
        } catch (JSONException e) {
            e.printStackTrace();
        }
        return returnJSONArray;
    }

    /**
     * transform the json string to a jsonObject
     */
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
