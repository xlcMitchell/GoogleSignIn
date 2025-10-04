package com.example.googlesigni.viewModel;

import android.util.Log;

import androidx.annotation.NonNull;
import androidx.lifecycle.LiveData;
import androidx.lifecycle.ViewModel;

import com.example.googlesigni.YouTubeApiRequestActivity;
import com.example.googlesigni.datamodel.ChannelInfo;
import com.example.googlesigni.datamodel.MakeYouTubeApiRequest;
import com.example.googlesigni.datamodel.YouTubeDatabase;
import com.example.googlesigni.datamodel.YouTubeDatabaseAccessObject;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.api.client.googleapis.extensions.android.gms.auth.GoogleAccountCredential;
import com.google.api.services.youtube.model.Channel;
import com.google.firebase.firestore.CollectionReference;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.FirebaseFirestore;

import java.util.ArrayList;
import java.util.List;

public class YouTubeApiViewModel extends ViewModel implements YouTubeViewModelMethods{
    private LiveData<List<ChannelInfo>> channels;
    private YouTubeDatabaseAccessObject ytDao;
    YouTubeApiRequestActivity apiRequestActivity;
    @Override
    public void setYouTubeResponse(Channel channel) {
        ChannelInfo info = new ChannelInfo(
                channel.getId(),
                channel.getSnippet().getTitle(),
                channel.getStatistics().getVideoCount().toString(),
                channel.getStatistics().getViewCount().toString(),
                channel.getStatistics().getSubscriberCount().toString());
                writeToFirebase(info);
    }

    public void setApiRequestActivity(YouTubeApiRequestActivity activity){
        this.apiRequestActivity = activity;
        ytDao = YouTubeDatabase.getDBInstance(activity.getApplicationContext()).getDatabaseDao();
        if(channels == null){
            channels = ytDao.getChannelsInfo();
        }
    }

    public LiveData<List<ChannelInfo>> getYtChannels(){
        return channels;
    }


    public void makeYtRequest(GoogleAccountCredential credential){
        List<Thread> threads = new ArrayList<>();
        // getting information of your own YouTube channel info
        Thread thread = new Thread(new MakeYouTubeApiRequest(credential, this,
                apiRequestActivity, true, ""));
        threads.add(thread);
        threads.get(0).start();


        String [] channels = {"GoogleDevelopers", "AndroidDevelopers", "derekbanas", "programmingwithmosh"};
        for (String name : channels) {
            Thread nThread = new Thread(new MakeYouTubeApiRequest(credential, this,
                    apiRequestActivity, false, name));
            threads.add(nThread);
            Log.d("Thread", "Starting Thread for " + name);
            threads.get(threads.size()-1).start();
        }

    }

    private void writeToFirebase(ChannelInfo channelInfo){
        // Connecting with the Firebase Realtime Database
        FirebaseFirestore db = FirebaseFirestore.getInstance();
        CollectionReference channelCollection = db.collection("yt_channel_data");
        DocumentReference channelRef = channelCollection.document(channelInfo.getChannelId());
        channelRef.set(channelInfo)
                .addOnSuccessListener(
                        new OnSuccessListener<Void>() {
                            @Override
                            public void onSuccess(Void unused) {
                                ytDao.insertChannelInfo(channelInfo);
                            }
                        }
                )
                .addOnFailureListener(new OnFailureListener() {
                    @Override
                    public void onFailure(@NonNull Exception e) {
                        Log.e("FireStoreError", "cannot add data in FireStore");
                    }
                });
    }
}
