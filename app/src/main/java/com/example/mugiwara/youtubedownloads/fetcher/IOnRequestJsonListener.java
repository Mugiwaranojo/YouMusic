package com.example.mugiwara.youtubedownloads.fetcher;


import org.json.JSONObject;

/**
 * Created by Fitec on 27/06/2014.
 * Interface result listener
 */
public interface IOnRequestJsonListener {

    public void onRequestResult(JSONObject result, Exception e);
}
