package com.example.mugiwara.youtubedownloads.fragment;

import android.app.Fragment;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ListView;

import com.example.mugiwara.youtubedownloads.R;
import com.example.mugiwara.youtubedownloads.fetcher.IOnPlaylistsRequestListener;
import com.example.mugiwara.youtubedownloads.model.PlaylistAdapter;
import com.google.api.services.youtube.model.Playlist;
import com.google.api.services.youtube.model.PlaylistItem;

import java.util.ArrayList;

/**
 * Created by Mugiwara on 23/04/2017.
 */

public class YoutubePlaylistsFragment extends Fragment implements IOnPlaylistsRequestListener {

    PlaylistAdapter adapter;
    private ListView listView;
    private ArrayList<Playlist> myPlaylists;
    private IOnPlaylistSelected playlistLister;

    public void setPlaylistLister(IOnPlaylistSelected playlistLister) {
        this.playlistLister = playlistLister;
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View v = inflater.inflate(R.layout.fragment_youtubeplaylists, null);

        listView = (ListView) v.findViewById(R.id.listViewMyPlaylists);
        listView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                    playlistLister.IOnPlaylistSelected(myPlaylists.get(position));
            }
        });
        return  v;
    }

    @Override
    public void IOnPlaylistsRequestListener(ArrayList<Playlist> myPlaylists) {
        this.myPlaylists = myPlaylists;
        adapter = new PlaylistAdapter(getActivity(), myPlaylists);
        listView.setAdapter(adapter);
    }
}
