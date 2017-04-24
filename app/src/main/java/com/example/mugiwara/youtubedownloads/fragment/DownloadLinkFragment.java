package com.example.mugiwara.youtubedownloads.fragment;

import android.app.DownloadManager;
import android.app.Fragment;
import android.app.ProgressDialog;
import android.content.BroadcastReceiver;
import android.content.ClipData;
import android.content.ClipboardManager;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.net.Uri;
import android.os.Bundle;
import android.os.Environment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.Toast;

import com.example.mugiwara.youtubedownloads.MainActivity;
import com.example.mugiwara.youtubedownloads.R;
import com.example.mugiwara.youtubedownloads.fetcher.DownloadJsonTask;
import com.example.mugiwara.youtubedownloads.fetcher.IOnRequestJsonListener;
import com.example.mugiwara.youtubedownloads.fetcher.YoutubeInMP3Download;
import com.example.mugiwara.youtubedownloads.model.Music;

import org.json.JSONException;
import org.json.JSONObject;

import static android.content.Context.DOWNLOAD_SERVICE;

/**
 * Created by Mugiwara on 22/04/2017.
 */

public class DownloadLinkFragment extends Fragment implements View.OnClickListener {

    ClipboardManager clipboard;
    private EditText linkYoutubeEditText;
    private ImageButton buttonPaste;
    private ImageButton buttonClear;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View v = inflater.inflate(R.layout.fragment_downladlink, null);

        clipboard = (ClipboardManager) getActivity().getSystemService(Context.CLIPBOARD_SERVICE);
        linkYoutubeEditText = (EditText) v.findViewById(R.id.edittext_linkyoutube);
        Button downloadButton = (Button) v.findViewById(R.id.button_download);
        downloadButton.setOnClickListener(this);

        buttonPaste = (ImageButton) v.findViewById(R.id.imageButtonPast);
        buttonPaste.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(clipboard.getPrimaryClip()!=null) {
                    ClipData.Item item = clipboard.getPrimaryClip().getItemAt(0);
                    String pasteData = item.getText().toString();
                    if (pasteData != null) {
                        linkYoutubeEditText.setText(pasteData);
                    }
                }
            }
        });

        buttonClear = (ImageButton) v.findViewById(R.id.imageButtonClear);
        buttonClear.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                linkYoutubeEditText.setText("");
            }
        });
        return v;
    }

    @Override
    public void onClick(View v) {
        if (!linkYoutubeEditText.getText().toString().equals("")) {
            YoutubeInMP3Download.downloandFromURL(linkYoutubeEditText.getText().toString(), getActivity());
        }
    }
}