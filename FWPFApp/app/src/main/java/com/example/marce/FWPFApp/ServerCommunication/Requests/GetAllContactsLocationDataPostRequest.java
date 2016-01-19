package com.example.marce.FWPFApp.ServerCommunication.Requests;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.DataOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.SocketTimeoutException;
import java.net.URL;

/*
*
* this class represents a http request to get current location data for all contacts of the phonebook
* that use the app to use this data in the contactListViewActivity
*
* Datei: GetAllContactsLocationDataPostRequest  Autor: Patrick
* Datum: 25.12.2015
* Historie:
* 31.12.16: another bug fix with the json
* 30.12.16: bug fix
* 29.12.15: class implemented
* 25.12.15: class created
*/
public class GetAllContactsLocationDataPostRequest extends PostRequest {
    private String urlPath = "/contact-locations";
    private JSONArray jsonArrayToSend;
    private JSONArray responseJsonArray;

    public GetAllContactsLocationDataPostRequest(String[] phoneNumbers) {
        this.requestUrl = this.serverUrl + urlPath;
        setJsonArrayToSend(phoneNumbers);
    }

    /**
     * execute request and read response
     */
    public JSONArray execute() {
        HttpURLConnection urlConnection = null;
        try {
            URL urlToRequest = new URL(this.requestUrl);
            urlConnection = (HttpURLConnection) urlToRequest.openConnection();
            urlConnection.setRequestMethod(requestType);
            urlConnection.setRequestProperty("Content-Type", "application/json; charset=UTF-8");
            urlConnection.setDoOutput(true);
            DataOutputStream dataOutputStream = new DataOutputStream(urlConnection.getOutputStream());
            dataOutputStream.writeBytes(jsonArrayToSend.toString());
            dataOutputStream.flush();
            dataOutputStream.close();
            int responseCode = urlConnection.getResponseCode();
            if (responseCode == HttpURLConnection.HTTP_OK) {
                InputStream inputStream = urlConnection.getInputStream();
                responseJsonArray = (JSONArray) readJSONArrayFromInputStream(inputStream); //use function of superclass to read the response as json
            } else {
                throw new Exception("not implemented");
            }
        } catch (MalformedURLException e) {
            e.printStackTrace();
        } catch (SocketTimeoutException e) {
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
        return responseJsonArray;
    }

    /**
     * set the data to send
     */
    public void setJsonArrayToSend(String[] phoneNumbers) {
        jsonArrayToSend = new JSONArray();
        JSONObject currentPhonenumberJson;
        for (int i = 0; i < phoneNumbers.length; i++) {
            currentPhonenumberJson = new JSONObject();
            try {
                currentPhonenumberJson.put("PhoneNumber", phoneNumbers[i]);
                jsonArrayToSend.put(currentPhonenumberJson);
            } catch (JSONException e) {
                e.printStackTrace();
            }
        }
    }
}
