package com.example.marce.FWPFApp.ServerCommunication;

import android.content.Context;
import android.location.Location;

import com.example.marce.FWPFApp.Helper.PhonebookRetriever;

/**
 * Created by Paddy on 26.12.2015.
 */
public class ServerCommunicationManager {
    private Context context;

    public ServerCommunicationManager(){

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

    //private String getMyId(){
    //    SharedPreferences settings = context.getSharedPreferences(Globals.settingFile(), context.MODE_PRIVATE);
    //    return settings.getString(Globals.settingUserId(), "-1");
    //}
}
