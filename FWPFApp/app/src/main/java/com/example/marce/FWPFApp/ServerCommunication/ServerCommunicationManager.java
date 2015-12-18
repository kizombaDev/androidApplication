package com.example.marce.FWPFApp.ServerCommunication;

import com.example.marce.FWPFApp.DataObjects.Contact;
import com.example.marce.FWPFApp.Helper.PhonebookRetriever;

import android.location.Location;
import android.util.Log;

import android.content.Context;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.*;
import java.net.*;

import java.io.DataOutputStream;

import org.json.JSONObject;

/**
 * Created by Patrick on 18.12.2015.
 */
public class ServerCommunicationManager {

    private String serverURL = "www.myServer.de";
    private PhonebookRetriever phonebookRetriever;
    private Context context;

    public ServerCommunicationManager() {

    }

    public void setContext(Context context) {
        this.context = context;
    }

    public void sendCurrentPhoneBook() {

        if(phonebookRetriever == null) {
            phonebookRetriever = new PhonebookRetriever(context);
        }

        Contact[] contacts = phonebookRetriever.getAllPhonebookContacts();

        //TODO: send to server


    }

    public void sendCurrentLocation(Location location) {
        //TODO: send location to server
    }

    public void sendPostRequest(JSONObject jsonObject) {

        HttpURLConnection urlConnection = null;
        try {
            // create connection
            URL urlToRequest = new URL(serverURL);
            urlConnection = (HttpURLConnection) urlToRequest.openConnection();

            urlConnection.setDoOutput(true);
            urlConnection.setRequestMethod("POST");
            urlConnection.setRequestProperty("Content-Type",
                    "application/json; charset=UTF-8");

            urlConnection.connect();

            OutputStreamWriter writer = new OutputStreamWriter(urlConnection.getOutputStream());
            String output = jsonObject.toString();
            writer.write(output);
            writer.flush();
            writer.close();





            // handle issues
            int statusCode = urlConnection.getResponseCode();
            if (statusCode == HttpURLConnection.HTTP_UNAUTHORIZED) {
                // handle unauthorized (if service requires user login)
            } else if (statusCode != HttpURLConnection.HTTP_OK) {
                // handle any other errors, like 404, 500,..
            }

        } catch (MalformedURLException e) {
            // URL is invalid
        } catch (SocketTimeoutException e) {
            // data retrieval or connection timed out
        } catch (IOException e) {
            // could not read response body
            // (could not create input stream)
        } finally {
            if (urlConnection != null) {
                urlConnection.disconnect();
            }
        }
    }

}
