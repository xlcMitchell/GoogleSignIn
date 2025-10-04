package com.example.googlesigni.datamodel;

import android.util.Log;

import com.example.googlesigni.YouTubeApiRequestActivity;
import com.example.googlesigni.viewModel.YouTubeApiViewModel;
import com.example.googlesigni.viewModel.YouTubeViewModelMethods;
import com.google.api.client.extensions.android.http.AndroidHttp;
import com.google.api.client.googleapis.extensions.android.gms.auth.GoogleAccountCredential;
import com.google.api.client.googleapis.extensions.android.gms.auth.UserRecoverableAuthIOException;
import com.google.api.client.http.HttpTransport;
import com.google.api.client.json.JsonFactory;
import com.google.api.client.json.jackson2.JacksonFactory;
import com.google.api.services.youtube.YouTube;
import com.google.api.services.youtube.model.Channel;
import com.google.api.services.youtube.model.ChannelListResponse;

import java.io.IOException;
import java.util.List;

public class MakeYouTubeApiRequest implements Runnable {
    private YouTube ytService;
    private boolean mine;
    private String channelUserName;
    YouTubeViewModelMethods viewModelMethods;
    YouTubeApiRequestActivity apiRequestActivity;

    public MakeYouTubeApiRequest(GoogleAccountCredential credential,
                                 YouTubeApiViewModel viewModel,
                                 YouTubeApiRequestActivity activity,
                                 boolean mine,
                                 String userName){
        this.apiRequestActivity = activity;
        HttpTransport transport = AndroidHttp.newCompatibleTransport();
        JsonFactory jsonFactory = JacksonFactory.getDefaultInstance();
        ytService = new YouTube.Builder(
                transport, jsonFactory, credential
        ).setApplicationName("YouTubeAPI App")
                .build();
        viewModelMethods = viewModel;
        this.mine = mine;
        this.channelUserName = userName;
    }

    @Override
    public void run() {
        try {
            ChannelListResponse result;
            if(mine) {
                result = ytService.channels().list("snippet, contentDetails, statistics")
                        .setMine(true)
                        .execute();
                Log.d("ChannelInfo", "Got my channel info");
            } else {
                result = ytService.channels().list("snippet, contentDetails, statistics")
                        .setForUsername(channelUserName)
                        .execute();
                Log.d("ChannelInfo", "Got channel inf of " + channelUserName);
            }
            List<Channel> channels = result.getItems();
            if(channels != null){
                Channel channel = channels.get(0);
                Log.d("SET_UI", "Setting UI with Info for " + channel.getSnippet().getTitle());
                viewModelMethods.setYouTubeResponse(channel);
            }
        } catch (UserRecoverableAuthIOException e) {
            e.printStackTrace();
            apiRequestActivity.youTubePermissionLauncher.launch(e.getIntent());
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}
