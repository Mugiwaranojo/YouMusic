package com.example.mugiwara.youtubedownloads.fetcher;

import android.app.Activity;
import android.app.DownloadManager;
import android.net.Uri;
import android.os.Environment;
import android.widget.Toast;

import com.example.mugiwara.youtubedownloads.MainActivity;
import com.example.mugiwara.youtubedownloads.model.Music;
import com.google.api.services.youtube.model.PlaylistItem;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.File;
import java.util.ArrayList;
import java.util.regex.Matcher;

import static android.content.Context.DOWNLOAD_SERVICE;

/**
 * Created by Mugiwara on 24/04/2017.
 */

public class YoutubeInMP3Download implements IOnRequestJsonListener {

    private final String URL_MUSIC_CONVERT = "http://www.youtubeinmp3.com/fetch/?format=JSON&video=";
    private final String YOUTUBE_VIDEO_BASEURL = "https://www.youtube.com/watch?v=";

    private Activity context;
    private String currentUrl;
    private PlaylistItem currentItem;

    private YoutubeInMP3Download(Activity context) {
        this.context = context;
    }

    private void convertYoutubeURL(String videoUrl){
        currentUrl= videoUrl;
        Toast.makeText(context, "Conversion du lien youtube : "+videoUrl, Toast.LENGTH_LONG).show();
        DownloadJsonTask jsonTask = new DownloadJsonTask(this);
        jsonTask.execute(URL_MUSIC_CONVERT + videoUrl);
    }

    private void convertYoutubePlaylistItem(PlaylistItem item){
        currentItem = item;
        Toast.makeText(context, "Conversion de la video : "+item.getSnippet().getTitle(), Toast.LENGTH_LONG).show();
        DownloadJsonTask jsonTask = new DownloadJsonTask(this);
        jsonTask.execute(URL_MUSIC_CONVERT + YOUTUBE_VIDEO_BASEURL+item.getContentDetails().getVideoId());
    }


    @Override
    public void onRequestResult(JSONObject result, Exception e) {
        String currentConvert ="";
        if(currentItem!=null) currentConvert = currentItem.getSnippet().getTitle();
        else currentConvert = currentUrl;
        if(result==null){
            Toast.makeText(context, "Error de conversion youtube : "+currentConvert, Toast.LENGTH_LONG).show();
            return;
        }
        try {
            Music m = Music.decodeFromJson(result);
            downloadMp3Link(m);
        } catch (JSONException e1) {
            Toast.makeText(context, "Error de conversion youtube : "+ currentConvert, Toast.LENGTH_LONG).show();
            e1.printStackTrace();
        }
    }

    private void downloadMp3Link(Music m){
        Toast.makeText(context, "Téléchargment de "+m.getTitle(), Toast.LENGTH_LONG).show();
        Uri music_uri = Uri.parse(m.getLink());
        DownloadManager downloadManager = (DownloadManager) context.getSystemService(DOWNLOAD_SERVICE);
        DownloadManager.Request request = new DownloadManager.Request(music_uri);
        String fileName = m.titleToFileName();
        request.setMimeType("audio/mpeg");
        request.setTitle(fileName);
        request.setAllowedNetworkTypes(DownloadManager.Request.NETWORK_MOBILE | DownloadManager.Request.NETWORK_WIFI);
        request.setAllowedOverRoaming(false);
        request.setDestinationInExternalPublicDir(Environment.DIRECTORY_DOWNLOADS, fileName);
        //Enqueue a new download and same the referenceId
        MainActivity.downloadReference = downloadManager.enqueue(request);
    }

    public static void downloandFromURL(String url, Activity context){
        YoutubeInMP3Download instance = new YoutubeInMP3Download(context);
        instance.convertYoutubeURL(url);
    }

    public static void downloandFromItem(PlaylistItem item, Activity context){
        YoutubeInMP3Download instance = new YoutubeInMP3Download(context);
        instance.convertYoutubePlaylistItem(item);
    }

    public static void downloadAllPlaylist(final ArrayList<PlaylistItem> playlistItems, final Activity context){
        final Activity currentContext= context;
        Toast.makeText(currentContext, "Conversion de "+playlistItems.size()+" vidéos", Toast.LENGTH_LONG).show();
        context.runOnUiThread(new Runnable() {
            public void run() {
                for (PlaylistItem item: playlistItems){
                    String path = Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_DOWNLOADS).getPath();
                    if(!Music.fileExist(path, item.getSnippet().getTitle())) {
                        YoutubeInMP3Download instance = new YoutubeInMP3Download(currentContext);
                        instance.convertYoutubePlaylistItem(item);
                    }
                }
            }
        });
    }
}
