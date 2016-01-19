package com.example.marce.FWPFApp.ServerCommunication.Requests;

/*
*
* this class is the superclass for all post requests in the app
*
* Datei: PostRequest  Autor: Patrick
* Datum: 25.12.2015
* Historie:
* 25.12.15: class created and implemented
*/

public abstract class PostRequest extends Request {
    public PostRequest() {
        this.requestType = "POST";
    }
}
