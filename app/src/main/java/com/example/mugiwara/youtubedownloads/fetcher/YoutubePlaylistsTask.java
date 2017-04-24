package com.example.mugiwara.youtubedownloads.fetcher;

import android.os.AsyncTask;
import android.util.Log;

import com.google.api.client.googleapis.json.GoogleJsonResponseException;
import com.google.api.services.youtube.YouTube;
import com.google.api.services.youtube.model.Playlist;
import com.google.api.services.youtube.model.PlaylistListResponse;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by Mugiwara on 22/04/2017.
 */

public class YoutubePlaylistsTask extends AsyncTask<Void, Void, ArrayList<Playlist>>{

    private YouTube youtube;
    private IOnPlaylistsRequestListener listener;

    public YoutubePlaylistsTask(YouTube client, IOnPlaylistsRequestListener listener) {
        this.youtube = client;
        this.listener = listener;

    }

    @Override
    protected ArrayList<Playlist> doInBackground(Void... params) {
        return getPlaylists();
    }

    private ArrayList<Playlist>  getPlaylists(){
        List<Playlist> myPlaylists= new ArrayList<>();
        try {
            // This object is used to make YouTube Data API requests.
            YouTube.Playlists.List playlistRequest = youtube.playlists().list("snippet,contentDetails");
            playlistRequest.setMine(true);
            playlistRequest.setMaxResults((long) 20);
            PlaylistListResponse playlistListResponse = playlistRequest.execute();
            myPlaylists = playlistListResponse.getItems();


        } catch (GoogleJsonResponseException e) {
            e.printStackTrace();
            Log.e("youtebeFetcher", "There was a service error: " + e.getDetails().getCode() + " : "
                    + e.getDetails().getMessage());
        } catch (Throwable t) {
            t.printStackTrace();
        }
        return new ArrayList<Playlist>(myPlaylists);
    }

    @Override
    protected void onPostExecute(ArrayList<Playlist> playlists) {
        listener.IOnPlaylistsRequestListener(playlists);
    }

    public static void run(YouTube client, IOnPlaylistsRequestListener listener) {
        YoutubePlaylistsTask fetcher = new YoutubePlaylistsTask(client, listener);
        fetcher.execute();
    }

}
