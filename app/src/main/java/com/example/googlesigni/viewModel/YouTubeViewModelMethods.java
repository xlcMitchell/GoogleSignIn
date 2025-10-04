package com.example.googlesigni.viewModel;

import com.google.api.services.youtube.model.Channel;
import androidx.lifecycle.LiveData;
import java.util.List;
import com.example.googlesigni.datamodel.ChannelInfo;

public interface YouTubeViewModelMethods {
    void setYouTubeResponse(Channel channel);
}
