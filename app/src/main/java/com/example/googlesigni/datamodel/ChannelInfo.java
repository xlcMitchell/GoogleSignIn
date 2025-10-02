package com.example.googlesigni.datamodel;

import android.os.Parcelable;

import androidx.annotation.NonNull;
import androidx.room.Entity;
import androidx.room.Ignore;
import androidx.room.PrimaryKey;

import java.io.Serializable;

import javax.annotation.Nonnull;

@Entity(tableName = "Channels")
public class ChannelInfo implements Serializable {
    @Ignore
    public static final String CHANNEL_INFO = "CHANNEL_INFO";
    @Ignore
    public static final String CHANNELS_KEY = "CHANNELS";
    @Ignore
    public static final String CHANNEL_ID_KEY = "CHANNEL_ID";
    @Ignore
    public static final String CHANNEL_TITLE_KEY = "CHANNEL_TITLE";
    @Ignore
    public static final String CHANNEL_VIDEOS_KEY = "CHANNEL_VIDEOS";
    @Ignore
    public static final String CHANNEL_VIEWS_KEY = "CHANNEL_VIEWS";
    @Ignore
    public static final String CHANNEL_SUBSCRIBERS_KEY = "CHANNEL_SUBSCRIBERS";

    @PrimaryKey
    @NonNull
    private String channelId;
    private String channelTitle;
    private String channelVideos;
    private String channelViews;
    private String channelSubscribers;

    public ChannelInfo() {
    }

    public ChannelInfo(@NonNull String channelId, String channelTitle, String channelVideos,
                       String channelView, String channelSubscribers) {
        this.channelId = channelId;
        this.channelTitle = channelTitle;
        this.channelVideos = channelVideos;
        this.channelViews = channelView;
        this.channelSubscribers = channelSubscribers;
    }

    public void setChannelId(String channelId) {
        this.channelId = channelId;
    }

    public void setChannelTitle(String channelTitle) {
        this.channelTitle = channelTitle;
    }

    public void setChannelVideos(String channelVideos) {
        this.channelVideos = channelVideos;
    }

    public void setChannelViews(String channelViews) {
        this.channelViews = channelViews;
    }

    public void setChannelSubscribers(String channelSubscribers) {
        this.channelSubscribers = channelSubscribers;
    }

    public String getChannelId() {
        return channelId;
    }

    public String getChannelTitle() {
        return channelTitle;
    }

    public String getChannelVideos() {
        return channelVideos;
    }

    public String getChannelViews() {
        return channelViews;
    }

    public String getChannelSubscribers() {
        return channelSubscribers;
    }
}