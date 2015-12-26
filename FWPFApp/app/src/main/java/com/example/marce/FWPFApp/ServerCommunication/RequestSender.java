package com.example.marce.FWPFApp.ServerCommunication;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.DataOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.SocketTimeoutException;
import java.net.URL;



/**
 * Created by Patrick on 20.12.2015.
 */
public class RequestSender implements Runnable {
    String requestURL;
    String requestType;
    Object JSONToSend;


    public RequestSender(String requestURL, String requestType, Object JSONToSend) {
        this.requestURL = requestURL;
        this.requestType = requestType;
        this.JSONToSend = JSONToSend;
    }

    public String readJSONStringFromInputStream(InputStream inputStream){
        String returnJSONString = "";
        try {
            BufferedReader streamReader = new BufferedReader(new InputStreamReader(inputStream, "UTF-8"));
            StringBuilder responseStrBuilder = new StringBuilder();

            String inputStr;
            while ((inputStr = streamReader.readLine()) != null)
                responseStrBuilder.append(inputStr);

            returnJSONString = responseStrBuilder.toString();
        } catch (Exception e){
            e.printStackTrace();
        }
        return returnJSONString;
    }

    public JSONArray readJSONArrayFromInputStream(InputStream inputStream){
        JSONArray returnJSONArray = null;
        try {
            returnJSONArray = new JSONArray(readJSONStringFromInputStream(inputStream));
        } catch (JSONException e) {
            e.printStackTrace();
        }
        return returnJSONArray;
    }

    public JSONObject readJSONObjectFromInputStream(InputStream inputStream){
        JSONObject returnJSONObject = null;
        try {
            returnJSONObject =  new JSONObject(readJSONStringFromInputStream(inputStream));
        } catch (JSONException e) {
            e.printStackTrace();
        }
        return  returnJSONObject;
    }

    public void run() {
        HttpURLConnection urlConnection = null;
        try {
            // create connection
            URL urlToRequest = new URL(requestURL);
            urlConnection = (HttpURLConnection) urlToRequest.openConnection();


            urlConnection.setRequestMethod(requestType);
            urlConnection.setRequestProperty("Content-Type", "application/json; charset=UTF-8");



            urlConnection.setDoOutput(true);


            if(JSONToSend != null) {
                //Send request
                DataOutputStream wr = new DataOutputStream(urlConnection.getOutputStream());
                wr.writeBytes(JSONToSend.toString());
                wr.flush ();
                wr.close();
            } else {
                urlConnection.connect();
            }



            // handle issues
            int statusCode = urlConnection.getResponseCode();
            if (statusCode == HttpURLConnection.HTTP_UNAUTHORIZED) {
                // handle unauthorized (if service requires user login)
            } else if (statusCode != HttpURLConnection.HTTP_OK) {
                // handle any other errors, like 404, 500,..
            }




            if(requestType == "POST") {
                //Get Response
                InputStream inputStream = urlConnection.getInputStream();

                Object responseJSON;
                if (JSONToSend instanceof JSONObject) {
                    responseJSON = (JSONObject) readJSONObjectFromInputStream(inputStream);
                } else if (JSONToSend instanceof JSONArray) {
                    responseJSON = (JSONArray) readJSONArrayFromInputStream(inputStream);
                } else if (JSONToSend == null) {
                    responseJSON = (JSONObject) readJSONObjectFromInputStream(inputStream);
                }
            }





        } catch (MalformedURLException e) {
            // URL is invalid
            e.printStackTrace();
        } catch (SocketTimeoutException e) {
            // data retrieval or connection timed out
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




//BufferedReader rd = new BufferedReader(new InputStreamReader(is));
//String line;
//StringBuffer response = new StringBuffer();
//while((line = rd.readLine()) != null) {
//   response.append(line);
//    response.append('\r');
//}
//rd.close();