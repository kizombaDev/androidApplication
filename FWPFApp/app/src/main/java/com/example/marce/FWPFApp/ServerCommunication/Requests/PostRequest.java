package com.example.marce.FWPFApp.ServerCommunication.Requests;

import java.util.ArrayList;
import java.util.Observer;

/**
 * Created by Paddy on 25.12.2015.
 */
public abstract class PostRequest extends Request {
    public PostRequest(){
        setRequestType();
    }

    public PostRequest(ArrayList<Observer> requestResponseObservers){
        super(requestResponseObservers);
        setRequestType();
    }

    public void setRequestType(){
        this.requestType = "POST";
    }
}
