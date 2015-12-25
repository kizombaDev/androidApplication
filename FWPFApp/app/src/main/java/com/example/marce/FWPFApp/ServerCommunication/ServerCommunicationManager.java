package com.example.marce.FWPFApp.ServerCommunication;

import android.content.Context;
import android.content.SharedPreferences;
import android.location.Location;

import com.example.marce.FWPFApp.Helper.Globals;
import com.example.marce.FWPFApp.Helper.PhonebookRetriever;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.Date;

/**
 * Created by Patrick on 18.12.2015.
 */
public class ServerCommunicationManager {

    //private String serverURL = "http://192.168.2.107/friendFinder/index.php";
    //private String serverURL = "http://xml.photography-sf.de/api.php";

    private String serverURL = "http://th-app.azurewebsites.net/api";

    private String registerMyselfPostRequestURLPart = "/users";
    private String phonebookContactLocationsPostRequestURLPart = "/contact-locations";
    private String sendMyLocationPostRequestURLPart = "/locations";


    private PhonebookRetriever phonebookRetriever;
    private Context context;

    public ServerCommunicationManager() {

    }

    public void setContext(Context context) {
        this.context = context;
    }

    public void testServerCommunicationManager(){
        //registerMyselfAndGetMyId("Patrick", "1234");
        //getLocationDataForAllPhonebookContacts();

        Location testLocation = new Location("");
        testLocation.setLatitude(41.895623);
        testLocation.setLongitude(12.482269);
        updateMyCurrentLocation(testLocation);

        //getLocationDataForContact("2");
    }

    public void registerMyselfAndGetMyId(String myName, String myPhoneNumber) {
        String url = serverURL + registerMyselfPostRequestURLPart;

        JSONObject jsonToSend = new JSONObject();
        try{
            jsonToSend.put("Username", myName);
            jsonToSend.put("PhoneNumber", myPhoneNumber);
        } catch (JSONException e) {
            e.printStackTrace();
        }
        sendJSONPostRequest(url, jsonToSend);
    }

    public void getLocationDataForAllPhonebookContacts() {
        String url = serverURL + phonebookContactLocationsPostRequestURLPart;

        if(phonebookRetriever == null) {
            phonebookRetriever = new PhonebookRetriever(context);
        }
        String[] phonebookNumbers = phonebookRetriever.getAllPhonebookNumbers();

        JSONArray phonebookNumbersJSON = new JSONArray();

        JSONObject currentPhoneNumberJSON;
        for(int i = 0; i < phonebookNumbers.length; i++){
            currentPhoneNumberJSON = new JSONObject();
            try{
                currentPhoneNumberJSON.put("PhoneNumber", phonebookNumbers[i]);
                phonebookNumbersJSON.put(currentPhoneNumberJSON);
            }catch (JSONException e) {
                e.printStackTrace();
            }
        }

        sendJSONPostRequest(url, phonebookNumbersJSON);
    }

    public void getLocationDataForContact(String requestedId) {
        String url = serverURL + phonebookContactLocationsPostRequestURLPart + "/" + requestedId;
        sendEmptyPostRequest(url);
    }

    public void updateMyCurrentLocation(Location location) {
        SharedPreferences settings = context.getSharedPreferences(Globals.settingFile(), context.MODE_PRIVATE);

        //kommt raus hier
        SharedPreferences.Editor editor = settings.edit();
        editor.putString(Globals.settingUserId(), "2");
        editor.commit();


        String myId = settings.getString(Globals.settingUserId(), "-1");
        //String myId = "5";


        DateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd'T'HH:mm");
        String nowAsISO = dateFormat.format(new Date());


        String url = serverURL + sendMyLocationPostRequestURLPart + "/" + myId;

        JSONObject jsonToSend = new JSONObject();
        try{
            jsonToSend.put("Id", myId);
            jsonToSend.put("Longitude", location.getLongitude());
            jsonToSend.put("Latitude", location.getLatitude());
            jsonToSend.put("LocationUpdateTime", nowAsISO);
        }catch (JSONException e){
            e.printStackTrace();
        }

        sendJSONPutRequest(url, jsonToSend);
    }

    public void sendJSONPostRequest(String url, Object jsonToSend) {
        Runnable thread = new RequestSender(url, "POST", jsonToSend);
        new Thread(thread).start();
    }

    public void sendJSONPutRequest(String url, Object jsonToSend) {
        Runnable thread = new RequestSender(url, "PUT", jsonToSend);
        new Thread(thread).start();
    }

    public void sendEmptyPostRequest(String url) {
        Runnable thread = new RequestSender(url, "POST", null);
        new Thread(thread).start();
    }

//    public void sendJSONObjectPostRequest(String url, JSONObject jsonObject) {
//        Runnable thread = new RequestSender(url, jsonObject.toString());
//        new Thread(thread).start();
//    }
//
//    public void sendJSONArrayPostRequest(String url, JSONArray jsonArray) {
//        Runnable thread = new RequestSender(url, jsonArray.toString());
//        new Thread(thread).start();
//    }
}
