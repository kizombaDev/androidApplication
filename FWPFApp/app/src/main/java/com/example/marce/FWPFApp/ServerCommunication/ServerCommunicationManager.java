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

    private String serverURL = "http://192.168.2.107/friendFinder/index.php";
    //private String serverURL = "http://xml.photography-sf.de/api.php";

    private PhonebookRetriever phonebookRetriever;
    private Context context;

    public ServerCommunicationManager() {

    }

    public void setContext(Context context) {
        this.context = context;
    }

    public void testServerCommunicationManager(){
        JSONObject obj = new JSONObject();
        try {
            obj.put("id", "3");
            obj.put("name", "NAME OF STUDENT");

        } catch (JSONException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        }

        sendPostRequest(obj);

    }

    public void sendCurrentPhoneBook() {
        if(phonebookRetriever == null) {
            phonebookRetriever = new PhonebookRetriever(context);
        }

        Contact[] contacts = phonebookRetriever.getAllPhonebookContacts();

        //sendPostRequest();
    }

    public void sendCurrentLocation(Location location) {

    }

    public void sendNameAndNumber(String name, String phoneNumber){

    }

    public void sendPostRequest(JSONObject jsonObject) {
        Runnable thread = new RequestSender(serverURL, jsonObject.toString());
        new Thread(thread).start();
    }

}
