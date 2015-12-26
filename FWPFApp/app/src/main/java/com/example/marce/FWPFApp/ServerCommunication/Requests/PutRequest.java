package com.example.marce.FWPFApp.ServerCommunication.Requests;

/**
 * Created by Paddy on 25.12.2015.
 */
public abstract class PutRequest extends Request {
    public PutRequest(){
        this.requestType = "PUT";
    }
}
