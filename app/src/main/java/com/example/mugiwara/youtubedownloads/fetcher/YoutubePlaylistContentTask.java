package com.example.mugiwara.youtubedownloads.fetcher;

import android.os.AsyncTask;
import android.util.Log;

import com.google.api.client.googleapis.json.GoogleJsonResponseException;
import com.google.api.services.youtube.YouTube;
import com.google.api.services.youtube.model.Playlist;
import com.google.api.services.youtube.model.PlaylistItem;
import com.google.api.services.youtube.model.PlaylistItemListResponse;
import com.google.api.services.youtube.model.PlaylistListResponse;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by Mugiwara on 23/04/2017.
 */

public class YoutubePlaylistContentTask extends AsyncTask<Playlist, Void, ArrayList<PlaylistItem>> {

    private YouTube youtube;
    private IOnPlaylistContentRequestListener listener;

    public YoutubePlaylistContentTask(YouTube client, IOnPlaylistContentRequestListener listener) {
        this.youtube = client;
        this.listener = listener;
    }

    @Override
    protected ArrayList<PlaylistItem> doInBackground(Playlist... params) {
        return getPlaylistContent(params[0]);
    }

    private ArrayList<PlaylistItem>  getPlaylistContent(Playlist playlist){
        List<PlaylistItem> playlistItems= null;
        try {
            // This object is used to make YouTube Data API requests.
            YouTube.PlaylistItems.List playlistItemsRequest = youtube.playlistItems().list("snippet,contentDetails");
            playlistItemsRequest.setPlaylistId(playlist.getId());
            playlistItemsRequest.setMaxResults((long) 50);
            PlaylistItemListResponse playlistItemListResponse = playlistItemsRequest.execute();
            playlistItems = playlistItemListResponse.getItems();
        } catch (GoogleJsonResponseException e) {
            e.printStackTrace();
            Log.e("youtebeFetcher", "There was a service error: " + e.getDetails().getCode() + " : "
                    + e.getDetails().getMessage());
        } catch (Throwable t) {
            t.printStackTrace();
        }
        return new ArrayList<PlaylistItem>(playlistItems);
    }

    @Override
    protected void onPostExecute(ArrayList<PlaylistItem> playlistItems) {
        listener.IOnPlaylistContentRequestListener(playlistItems);
    }

    public static void run(YouTube client, Playlist playlist, IOnPlaylistContentRequestListener listener) {
        YoutubePlaylistContentTask fetcher = new YoutubePlaylistContentTask(client, listener);
        fetcher.execute(playlist);
    }
}
