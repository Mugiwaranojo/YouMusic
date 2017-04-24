package com.example.mugiwara.youtubedownloads.fetcher;

import android.os.AsyncTask;
import android.util.Log;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.URL;
import java.net.URLConnection;

/**
 * Created by Fitec on 25/06/2014.
 */
public class DownloadJsonTask extends AsyncTask<String, Void, String>  {


    private IOnRequestJsonListener onConnectionResultListener;

    public DownloadJsonTask(IOnRequestJsonListener onConnectionResultListener) {
       this.onConnectionResultListener= onConnectionResultListener;
    }

    @Override
    protected void onPostExecute(String result)
    {
        try {
            JSONObject jsonObj = new JSONObject(result);
            onConnectionResultListener.onRequestResult(jsonObj, null);
        } catch (JSONException e) {
            e.printStackTrace();
            onConnectionResultListener.onRequestResult(null, e);
        }
    }

    @Override
    protected String doInBackground(String... strings) {
        if(strings.length>1){
            return download_json(strings[0], strings[1]);
        }else{
            return download_json(strings[0]);
        }
    }

    private String download_json(String url){
        String result="";
        try {
            URL aURL = new URL(url);
            URLConnection conn = aURL.openConnection();
            conn.connect();
            InputStream is = conn.getInputStream();
            result = convertStreamToString(is);
            is.close();
        } catch (IOException e) {
            Log.e("Hub", "Error getting the json from server : " + e.getMessage().toString());
            e.printStackTrace();
        }
        return result;
    }
    private String download_json(String url, String token){
        String result="";
        try {
            URL aURL = new URL(url);
            URLConnection conn = aURL.openConnection();
            conn.setRequestProperty("Authorization",  String.format("Bearer %s", token));
            conn.connect();
            InputStream is = conn.getInputStream();
            result = convertStreamToString(is);
            is.close();
        } catch (IOException e) {
            Log.e("Hub", "Error getting the json from server : " + e.getMessage().toString());
        }

        return result;
    }


    private String convertStreamToString(InputStream is) {
        BufferedReader reader = new BufferedReader(new InputStreamReader(is));
        StringBuilder sb = new StringBuilder();

        String line;
        try {
            while ((line = reader.readLine()) != null) {
                sb.append(line).append('\n');
            }
        } catch (IOException e) {
            e.printStackTrace();
        } finally {
            try {
                is.close();
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
        return sb.toString();
    }

}
