package com.example.marce.FWPFApp.ServerCommunication;

import android.util.Log;

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
    String serverURL;
    String requestString;

    public RequestSender(String serverURL, String requestString) {
        this.serverURL = serverURL;
        this.requestString = requestString;
    }

    public JSONObject readJSONFromInputStream(InputStream inputStream){
        try {
            BufferedReader streamReader = new BufferedReader(new InputStreamReader(inputStream, "UTF-8"));
            StringBuilder responseStrBuilder = new StringBuilder();

            String inputStr;
            while ((inputStr = streamReader.readLine()) != null)
                responseStrBuilder.append(inputStr);

            JSONObject jsonObject = new JSONObject(responseStrBuilder.toString());

            //returns the json object
            return jsonObject;

        } catch (IOException e) {
            e.printStackTrace();
        } catch (JSONException e) {
            e.printStackTrace();
        }
        return null;
    }

    public void run() {
        HttpURLConnection urlConnection = null;
        try {
            // create connection
            URL urlToRequest = new URL(serverURL);
            urlConnection = (HttpURLConnection) urlToRequest.openConnection();

            urlConnection.setDoOutput(true);
            urlConnection.setRequestMethod("POST");
            urlConnection.setRequestProperty("Content-Type", "application/json; charset=UTF-8");

            //Send request
            DataOutputStream wr = new DataOutputStream(urlConnection.getOutputStream());
            wr.writeBytes(requestString);
            wr.flush ();
            wr.close ();



            // handle issues
            int statusCode = urlConnection.getResponseCode();
            if (statusCode == HttpURLConnection.HTTP_UNAUTHORIZED) {
                // handle unauthorized (if service requires user login)
            } else if (statusCode != HttpURLConnection.HTTP_OK) {
                // handle any other errors, like 404, 500,..
            }


            //Get Response
            InputStream is = urlConnection.getInputStream();

            JSONObject jsonObject = readJSONFromInputStream(is);


        } catch (MalformedURLException e) {
            // URL is invalid
        } catch (SocketTimeoutException e) {
            // data retrieval or connection timed out
        } catch (IOException e) {
            // could not read response body
            // (could not create input stream)
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