package com.example.mugiwara.youtubedownloads.fragment;

import android.app.Fragment;
import android.app.ProgressDialog;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.TextView;

import com.example.mugiwara.youtubedownloads.R;
import com.example.mugiwara.youtubedownloads.fetcher.IOnPlaylistContentRequestListener;
import com.example.mugiwara.youtubedownloads.fetcher.YoutubeInMP3Download;
import com.example.mugiwara.youtubedownloads.model.PlaylistContentAdapter;
import com.google.api.services.youtube.model.Playlist;
import com.google.api.services.youtube.model.PlaylistItem;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by Mugiwara on 23/04/2017.
 */

public class YoutubePlaylistContentFragment extends Fragment implements IOnPlaylistContentRequestListener {

    TextView textViewTitle;
    ListView listViewPlaylistContent;
    PlaylistContentAdapter adapter;
    Button buttonDownloadAll;

    Playlist currentPlaylist;
    ArrayList<PlaylistItem> contentPlaylist = new ArrayList<PlaylistItem>();
    ProgressDialog spinner;

    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, Bundle savedInstanceState) {
        View v = inflater.inflate(R.layout.fragment_playlistsitems, null);

        textViewTitle = (TextView) v.findViewById(R.id.textViewPlaylistTitle);
        listViewPlaylistContent = (ListView) v.findViewById(R.id.listViewPlaylistContent);

        buttonDownloadAll = (Button) v.findViewById(R.id.buttonDownloadAllPlaylist);
        buttonDownloadAll.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                YoutubeInMP3Download.downloadAllPlaylist(contentPlaylist, getActivity());
            }
        });

        spinner = new ProgressDialog(getActivity());
        spinner.setProgressStyle(ProgressDialog.STYLE_SPINNER);
        spinner.setCancelable(true);
        return v;
    }

    public void setCurrentPlaylist(Playlist currentPlaylist) {
        this.currentPlaylist = currentPlaylist;
        refresh();
        spinner.setMessage("Chargement de : "+currentPlaylist.getSnippet().getTitle());
        spinner.show();
    }

    public void refresh(){
        textViewTitle.setText(currentPlaylist.getSnippet().getTitle());
        adapter = new PlaylistContentAdapter(getActivity(), contentPlaylist);
        listViewPlaylistContent.setAdapter(adapter);
    }

    @Override
    public void IOnPlaylistContentRequestListener(ArrayList<PlaylistItem> playlistItems) {
        contentPlaylist = playlistItems;
        spinner.dismiss();
        refresh();
    }
}
