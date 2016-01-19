package com.example.marce.FWPFApp.ServerCommunication.Requests;

/*
*
* this class is the superclass for all put requests in the app
*
* Datei: PutRequest  Autor: Patrick
* Datum: 25.12.2015 Version: 1.0
* Historie:
* 25.12.15: class created and implemented
*/

public abstract class PutRequest extends Request {
    public PutRequest() {
        this.requestType = "PUT";
    }
}
