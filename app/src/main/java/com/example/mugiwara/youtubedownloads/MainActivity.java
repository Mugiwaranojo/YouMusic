package com.example.mugiwara.youtubedownloads;

import android.accounts.AccountManager;
import android.app.Activity;
import android.app.Dialog;
import android.app.DownloadManager;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.widget.Toast;

import com.example.mugiwara.youtubedownloads.fetcher.YoutubePlaylistContentTask;
import com.example.mugiwara.youtubedownloads.fetcher.YoutubePlaylistsTask;
import com.example.mugiwara.youtubedownloads.fragment.DownloadLinkFragment;
import com.example.mugiwara.youtubedownloads.fragment.IOnPlaylistSelected;
import com.example.mugiwara.youtubedownloads.fragment.YoutubePlaylistContentFragment;
import com.example.mugiwara.youtubedownloads.fragment.YoutubePlaylistsFragment;
import com.google.android.gms.common.GooglePlayServicesUtil;
import com.google.api.client.extensions.android.http.AndroidHttp;
import com.google.api.client.googleapis.extensions.android.gms.auth.GoogleAccountCredential;
import com.google.api.client.http.HttpTransport;
import com.google.api.client.json.JsonFactory;
import com.google.api.client.json.gson.GsonFactory;
import com.google.api.services.youtube.YouTube;
import com.google.api.services.youtube.YouTubeScopes;
import com.google.api.services.youtube.model.Playlist;

import java.util.Collections;

public class MainActivity extends Activity implements IOnPlaylistSelected {

    static final int REQUEST_GOOGLE_PLAY_SERVICES = 0;
    static final int REQUEST_AUTHORIZATION = 1;
    static final int REQUEST_ACCOUNT_PICKER = 2;

    private static final String PREF_ACCOUNT_NAME = "accountName";

    final HttpTransport transport = AndroidHttp.newCompatibleTransport();
    final JsonFactory jsonFactory = GsonFactory.getDefaultInstance();

    GoogleAccountCredential credential;

    YouTube client;

    YoutubePlaylistsFragment playlistsFragment;
    YoutubePlaylistContentFragment playlistContentFragment;
    DownloadLinkFragment downloadLinkFragment;


    public static long downloadReference;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        downloadLinkFragment = (DownloadLinkFragment) getFragmentManager().findFragmentById(R.id.fragment_downladlink);

        playlistsFragment= (YoutubePlaylistsFragment) getFragmentManager().findFragmentById(R.id.fragment_youtubeplaylist);
        playlistsFragment.setPlaylistLister(this);

        playlistContentFragment = (YoutubePlaylistContentFragment) getFragmentManager().findFragmentById(R.id.fragment_youtubeplaylistContent);
        getFragmentManager().beginTransaction()
                .hide(playlistContentFragment)
                .commit();

        credential = GoogleAccountCredential.usingOAuth2(this, Collections.singleton(YouTubeScopes.YOUTUBE_READONLY));
        SharedPreferences settings = getPreferences(Context.MODE_PRIVATE);
        credential.setSelectedAccountName(settings.getString(PREF_ACCOUNT_NAME, null));
        client = new YouTube.Builder(
                transport, jsonFactory, credential).setApplicationName(getString(R.string.app_name))
                .build();

        //set filter to only when download is complete and register broadcast receiver
        IntentFilter filter = new IntentFilter(DownloadManager.ACTION_DOWNLOAD_COMPLETE);
        registerReceiver(downloadReceiver, filter);
    }


    @Override
    public void IOnPlaylistSelected(Playlist playlist) {
        playlistContentFragment.setCurrentPlaylist(playlist);
        getFragmentManager().beginTransaction()
                .show(playlistContentFragment)
                .hide(playlistsFragment)
                .hide(downloadLinkFragment)
                .commit();
        YoutubePlaylistContentTask.run(client,playlist, playlistContentFragment);
    }

    @Override
    public void onBackPressed() {
        if(playlistContentFragment.isVisible()) {
            getFragmentManager().beginTransaction()
                    .hide(playlistContentFragment)
                    .show(downloadLinkFragment)
                    .show(playlistsFragment)
                    .commit();
        }else{
            super.onBackPressed();
        }
    }

    void showGooglePlayServicesAvailabilityErrorDialog(final int connectionStatusCode) {
        runOnUiThread(new Runnable() {
            public void run() {
                Dialog dialog = GooglePlayServicesUtil.getErrorDialog(
                        connectionStatusCode, MainActivity.this, REQUEST_GOOGLE_PLAY_SERVICES);
                dialog.show();
            }
        });
    }

    @Override
    protected void onResume() {
        super.onResume();
        if (checkGooglePlayServicesAvailable()) {
            haveGooglePlayServices();
        }
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        switch (requestCode) {
            case REQUEST_GOOGLE_PLAY_SERVICES:
                if (resultCode == Activity.RESULT_OK) {
                    haveGooglePlayServices();
                } else {
                    checkGooglePlayServicesAvailable();
                }
                break;
            case REQUEST_AUTHORIZATION:
                if (resultCode == Activity.RESULT_OK) {
                    // load youtube playlists
                    YoutubePlaylistsTask.run(client, playlistsFragment);
                } else {
                    chooseAccount();
                }
                break;
            case REQUEST_ACCOUNT_PICKER:
                if (resultCode == Activity.RESULT_OK && data != null && data.getExtras() != null) {
                    String accountName = data.getExtras().getString(AccountManager.KEY_ACCOUNT_NAME);
                    if (accountName != null) {
                        credential.setSelectedAccountName(accountName);
                        SharedPreferences settings = getPreferences(Context.MODE_PRIVATE);
                        SharedPreferences.Editor editor = settings.edit();
                        editor.putString(PREF_ACCOUNT_NAME, accountName);
                        editor.commit();
                        // load youtube playlists
                        YoutubePlaylistsTask.run(client, playlistsFragment);
                    }
                }
                break;
        }
    }
    /** Check that Google Play services APK is installed and up to date. */
    private boolean checkGooglePlayServicesAvailable() {
        final int connectionStatusCode = GooglePlayServicesUtil.isGooglePlayServicesAvailable(this);
        if (GooglePlayServicesUtil.isUserRecoverableError(connectionStatusCode)) {
            showGooglePlayServicesAvailabilityErrorDialog(connectionStatusCode);
            return false;
        }
        return true;
    }

    private void haveGooglePlayServices() {
        // check if there is already an account selected
        if (credential.getSelectedAccountName() == null) {
            // ask user to choose account
            chooseAccount();
        } else {
            // load youtube playlists
            YoutubePlaylistsTask.run(client, playlistsFragment);
        }
    }

    private void chooseAccount() {
        startActivityForResult(credential.newChooseAccountIntent(), REQUEST_ACCOUNT_PICKER);
    }

    private BroadcastReceiver downloadReceiver = new BroadcastReceiver() {
        @Override
        public void onReceive(Context context, Intent intent) {
            //check if the broadcast message is for our Enqueued download
            long referenceId = intent.getLongExtra(DownloadManager.EXTRA_DOWNLOAD_ID, -1);
            if (downloadReference == referenceId) {
                playlistContentFragment.refresh();
                Toast.makeText(getApplicationContext(), "Téléchargement terminé ", Toast.LENGTH_LONG).show();
            }
        }
    };
}
