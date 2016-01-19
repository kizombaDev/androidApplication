package com.example.marce.FWPFApp.ServerCommunication.Requests;

import android.location.Location;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.DataOutputStream;
import java.io.IOException;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.SocketTimeoutException;
import java.net.URL;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.Date;

/**
 * Created by Paddy on 25.12.2015.
 */
public class UpdateMyCurrentLocationPutRequest extends PutRequest {
    private String urlPath = "/locations/";
    private JSONObject jsonToSend;

    public UpdateMyCurrentLocationPutRequest(String myId, Location myLocation) {
        this.requestUrl = this.serverUrl + urlPath + myId;
        setJsonToSend(myId, myLocation);
    }

    private void setJsonToSend(String myId, Location myLocation) {
        DateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd'T'HH:mm");
        String nowAsISO = dateFormat.format(new Date());

        jsonToSend = new JSONObject();
        try {
            jsonToSend.put("Id", myId);
            jsonToSend.put("Longitude", myLocation.getLongitude());
            jsonToSend.put("Latitude", myLocation.getLatitude());
            jsonToSend.put("LocationUpdateTime", nowAsISO);
        } catch (JSONException e) {
            e.printStackTrace();
        }
    }

    public void execute() {
        HttpURLConnection urlConnection = null;
        try {
            URL urlToRequest = new URL(this.requestUrl);
            urlConnection = (HttpURLConnection) urlToRequest.openConnection();
            urlConnection.setRequestMethod(requestType);
            urlConnection.setRequestProperty("Content-Type", "application/json; charset=UTF-8");
            urlConnection.setDoOutput(true);
            DataOutputStream dataOutputStream = new DataOutputStream(urlConnection.getOutputStream());
            dataOutputStream.writeBytes(jsonToSend.toString());
            dataOutputStream.flush();
            dataOutputStream.close();
            int responseCode = urlConnection.getResponseCode();
            if (responseCode != HttpURLConnection.HTTP_OK) {
                throw new Exception("not implemented");
            }
        } catch (MalformedURLException e) {
            e.printStackTrace();
        } catch (SocketTimeoutException e) {
            e.printStackTrace();
        } catch (IOException e) {
            // could not read response body
            // (could not create input stream)
            e.printStackTrace();
        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            if (urlConnection != null) {
                urlConnection.disconnect();
            }
        }
    }
}
