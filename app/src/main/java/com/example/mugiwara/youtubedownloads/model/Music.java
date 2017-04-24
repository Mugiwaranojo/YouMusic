package com.example.mugiwara.youtubedownloads.model;

import org.json.JSONException;
import org.json.JSONObject;

/**
 * Created by Mugiwara on 22/04/2017.
 */

public class Music {
    private String title;
    private int length;
    private String link;

    public Music() {
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public int getLength() {
        return length;
    }

    public void setLength(int length) {
        this.length = length;
    }

    public String getLink() {
        return link;
    }

    public void setLink(String link) {
        this.link = link;
    }

    public static Music decodeFromJson(JSONObject jsonObject) throws JSONException {
        Music m = new Music();
        m.setTitle(jsonObject.getString("title"));
        m.setLength(Integer.parseInt(jsonObject.getString("length")));
        m.setLink(jsonObject.getString("link"));
        return m;
    }
}
