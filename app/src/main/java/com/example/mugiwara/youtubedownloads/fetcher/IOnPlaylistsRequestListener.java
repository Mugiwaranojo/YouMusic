package com.example.mugiwara.youtubedownloads.fetcher;

import com.google.api.services.youtube.model.Playlist;
import com.google.api.services.youtube.model.PlaylistItem;

import java.util.ArrayList;

/**
 * Created by Mugiwara on 23/04/2017.
 */

public interface IOnPlaylistsRequestListener {
    public void IOnPlaylistsRequestListener(ArrayList<Playlist> myPlaylists);
}
