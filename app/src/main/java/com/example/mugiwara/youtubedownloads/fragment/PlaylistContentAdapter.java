package com.example.mugiwara.youtubedownloads.fragment;

import android.app.Activity;
import android.app.DownloadManager;
import android.content.Context;
import android.net.Uri;
import android.os.Environment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.CheckedTextView;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.example.mugiwara.youtubedownloads.MainActivity;
import com.example.mugiwara.youtubedownloads.R;
import com.example.mugiwara.youtubedownloads.fetcher.DownloadImagesTask;
import com.example.mugiwara.youtubedownloads.fetcher.DownloadJsonTask;
import com.example.mugiwara.youtubedownloads.fetcher.IOnRequestJsonListener;
import com.example.mugiwara.youtubedownloads.fetcher.YoutubeInMP3Download;
import com.example.mugiwara.youtubedownloads.fragment.DownloadLinkFragment;
import com.example.mugiwara.youtubedownloads.model.Music;
import com.google.api.services.youtube.model.PlaylistItem;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;

import static android.content.Context.DOWNLOAD_SERVICE;

/**
 * Created by Mugiwara on 23/04/2017.
 */

public class PlaylistContentAdapter extends BaseAdapter {

    private Activity context;
    private ArrayList<PlaylistItem> playlistItems;


    public PlaylistContentAdapter(Activity context, ArrayList<PlaylistItem> playlistItems) {
        this.context = context;
        this.playlistItems = playlistItems;
    }

    @Override
    public int getCount() {
        return playlistItems.size();
    }

    @Override
    public Object getItem(int position) {
        return playlistItems.get(position);
    }

    @Override
    public long getItemId(int position) {
        return position;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        LayoutInflater inflater = (LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        View v = inflater.inflate(R.layout.item_playlistcontent, null);
        final PlaylistItem item = playlistItems.get(position);

        CheckedTextView textView = (CheckedTextView) v.findViewById(R.id.textViewItemPlaylistContentTitle);
        textView.setText(item.getSnippet().getTitle());

        if(item.getSnippet().getThumbnails()!=null) {
            ImageView imageView = (ImageView) v.findViewById(R.id.imageViewItemPlaylistContentThumbnails);
            DownloadImagesTask imagesTask = new DownloadImagesTask(imageView);
            imagesTask.execute(item.getSnippet().getThumbnails().getDefault().getUrl());
        }

        final ImageButton imageButton = (ImageButton) v.findViewById(R.id.imageButtonDowloadItem);
        imageButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                imageButton.setEnabled(false);
                YoutubeInMP3Download.downloandFromItem(item, context);
            }
        });
        String path = Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_DOWNLOADS).getPath();
        if(Music.fileExist(path, item.getSnippet().getTitle())){
            textView.setChecked(true);
            imageButton.setEnabled(false);
        }else{
            textView.setChecked(false);
            imageButton.setEnabled(true);
        }
        return v;
    }
}
