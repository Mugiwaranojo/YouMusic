package com.example.mugiwara.youtubedownloads.model;

import android.app.Activity;
import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import com.example.mugiwara.youtubedownloads.R;
import com.example.mugiwara.youtubedownloads.fetcher.DownloadImagesTask;
import com.google.api.services.youtube.model.Playlist;

import java.util.ArrayList;

/**
 * Created by Mugiwara on 23/04/2017.
 */

public class PlaylistAdapter extends BaseAdapter {

    private Activity context;
    private ArrayList<Playlist> playlists;

    public PlaylistAdapter(Activity context, ArrayList<Playlist> playlists) {
        this.context = context;
        this.playlists = playlists;
    }

    @Override
    public int getCount() {
        return playlists.size();
    }

    @Override
    public Object getItem(int position) {
        return playlists.get(position);
    }

    @Override
    public long getItemId(int position) {
        return position;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {

        LayoutInflater inflater = (LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        View rowView= inflater.inflate(R.layout.item_playlist, null);
        Playlist playlist = playlists.get(position);
        if(playlist!=null) {
            TextView textView = (TextView) rowView.findViewById(R.id.textViewItemPlaylistDescription);
            textView.setText(playlist.getSnippet().getTitle());

            TextView textViewNumberVideo = (TextView) rowView.findViewById(R.id.textViewItemPlaylistNombreVideos);
            textViewNumberVideo.setText(playlist.getContentDetails().getItemCount() + " videos");

            if(playlist.getSnippet().getThumbnails()!=null) {
                ImageView imageView = (ImageView) rowView.findViewById(R.id.imageViewItemPlaylistThumbnails);
                DownloadImagesTask imagesTask = new DownloadImagesTask(imageView);
                imagesTask.execute(playlist.getSnippet().getThumbnails().getDefault().getUrl());
            }
        }
        return rowView;
    }
}
