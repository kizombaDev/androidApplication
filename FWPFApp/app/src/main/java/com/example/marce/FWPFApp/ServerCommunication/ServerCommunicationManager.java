package com.example.marce.FWPFApp.ServerCommunication;

import android.content.Context;
import android.content.SharedPreferences;
import android.location.Location;

import com.example.marce.FWPFApp.Helper.Globals;
import com.example.marce.FWPFApp.Helper.PhonebookRetriever;

import java.util.ArrayList;
import java.util.Observer;

/**
 * Created by Paddy on 26.12.2015.
 */
public class ServerCommunicationManager {
    private Context context;

    private ArrayList<Observer> myIdObservers;

    public ServerCommunicationManager(){
        myIdObservers = new ArrayList<Observer>();

    }

    public void test(){
        sendRegisterMyselfAndGetMyIdPostRequest("Patrick", "1234");

        Location testLocation = new Location("");
        testLocation.setLatitude(41.895623);
        testLocation.setLongitude(12.482269);
        //sendUpdateMyCurrentLocationPutRequest(testLocation);

        //sendGetContactLocationDataPostRequest("4");

        //sendGetAllContactsLocationDataPostRequest();
    }

    public void setContext(Context context){
        this.context = context;
    }

    public void sendGetAllContactsLocationDataPostRequest(){
        PhonebookRetriever phonebookRetriever = new PhonebookRetriever(context);
        String[] allPhonebookNumbers = phonebookRetriever.getAllPhonebookNumbers();

        //Runnable requestToSend = new GetAllContactsLocationDataPostRequest(allPhonebookNumbers);
        //new Thread(requestToSend).start();
    }

    public void sendGetContactLocationDataPostRequest(String contactId){
        //Runnable requestToSend = new GetContactLocationDataPostRequest(contactId);
        //new Thread(requestToSend).start();
    }

    public void sendRegisterMyselfAndGetMyIdPostRequest(String myName, String myPhoneNumber){
        //Runnable requestToSend = new RegisterMyselfAndGetMyIdPostRequest(myName, myPhoneNumber, myIdObservers);
        //new Thread(requestToSend).start();
    }

    public void sendUpdateMyCurrentLocationPutRequest(Location myLocation) {
        //Runnable requestToSend = new UpdateMyCurrentLocationPutRequest(getMyId(), myLocation);
        //new Thread(requestToSend).start();
    }

    public void registerForMyIdResponse(Observer observer){
        myIdObservers.add(observer);
    }

    private String getMyId(){
        SharedPreferences settings = context.getSharedPreferences(Globals.settingFile(), context.MODE_PRIVATE);
        return settings.getString(Globals.settingUserId(), "-1");
    }
}
