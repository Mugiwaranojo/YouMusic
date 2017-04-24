package com.example.mugiwara.youtubedownloads.fetcher;

import com.google.api.services.youtube.model.PlaylistItem;

import java.util.ArrayList;

/**
 * Created by Mugiwara on 23/04/2017.
 */

public interface IOnPlaylistContentRequestListener {
    public void IOnPlaylistContentRequestListener(ArrayList<PlaylistItem> playlistItems);
}
