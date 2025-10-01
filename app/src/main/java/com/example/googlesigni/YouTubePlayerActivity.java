package com.example.googlesigni;

import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.activity.EdgeToEdge;
import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;

import com.google.android.gms.auth.api.identity.SignInCredential;
import com.pierfrancescosoffritti.androidyoutubeplayer.core.player.YouTubePlayer;
import com.pierfrancescosoffritti.androidyoutubeplayer.core.player.listeners.AbstractYouTubePlayerListener;
import com.pierfrancescosoffritti.androidyoutubeplayer.core.player.views.YouTubePlayerView;
import com.squareup.picasso.Picasso;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class YouTubePlayerActivity extends AppCompatActivity {

    SignInCredential credential;
    YouTubePlayerView youTubePlayerView;
    EditText videoUrlEt;
    String videoId;

    final String youTubeUrlRegEx = "^(https?)?(://)?(www.)?(m.)?((youtube.com)|(youtu.be))/";
    final String[] videoIdRegex = { "\\?vi?=([^&]*)","watch\\?.*v=([^&]*)", "(?:embed|vi?)/([^/?]*)", "^([A-Za-z0-9\\-]*)"};

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_you_tube_player);

        credential = getIntent().getParcelableExtra("CREDENTIAL");

        TextView nameTv = findViewById(R.id.userNameTV);
        ImageView avatarView = findViewById(R.id.avatarImage);
        nameTv.setText(credential.getDisplayName());
        Picasso.with(this).load(credential.getProfilePictureUri().toString()).into(avatarView);

        youTubePlayerView = findViewById(R.id.youtube_player_view);
        youTubePlayerView.setEnableAutomaticInitialization(false);;
        videoUrlEt = findViewById(R.id.ytVideoUrlEt);
        Button playBtn = findViewById(R.id.ytPlayVideoBtn);

        playBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                playVideoButtonClick();
            }
        });

        //YoutubePlayer is a lifecycle aware widget
        //so he video only plays when it is visible
        getLifecycle().addObserver(youTubePlayerView);

        youTubePlayerView.initialize(new AbstractYouTubePlayerListener() {
            @Override
            public void onReady(@NonNull YouTubePlayer youTubePlayer) {
                super.onReady(youTubePlayer);
                videoId = "JzSeC5sFyT8"; //video id to play on default before the user enters a url
                youTubePlayer.loadVideo(videoId,0);
            }
        });

    }

    public String extractVideoIdFromUrl(String url) {
        String youTubeLinkWithoutProtocolAndDomain = youTubeLinkWithoutProtocolAndDomain(url);
        // extract the VideoID and return it
        String[] videoIdRegex = {
                "(?<=v=)[^&]+",
                "(?<=be/)[^&?]+",
                "(?<=embed/)[^&?]+",
                "(?<=/v/)[^&?]+"
        };

        for (String regex : videoIdRegex) {
            Pattern compiledPattern = Pattern.compile(regex);
            Matcher matcher = compiledPattern.matcher(youTubeLinkWithoutProtocolAndDomain);
            if (matcher.find()) {
                return matcher.group(0);
            }
        }
        return null;
    }

    private String youTubeLinkWithoutProtocolAndDomain(String url) {
        // matches the domain and protocol part like the first part of these URLs and deletes them
        // input: https://www.youtube.com/watch?v=KAbJnGLDxnE
        // → returns: watch?v=KAbJnGLDxnE
        // input: https://www.youtube.com/watch?v=evFP410gmzg&list=PLWCISDYLXVeBpURXfKJ5RzsyTNRsm4DYs&index=2&t=44s
        // → returns: watch?v=evFP410gmzg&list=PLWCISDYLXVeBpURXfKJ5RzsyTNRsm4DYs&index=2&t=44s
        // input: https://youtu.be/NJLWVYyEYL8
        // → returns: NJLWVYyEYL8

        String youTubeUrlRegEx = "^(https?://)?(www\\.)?(youtube\\.com|youtu\\.be)/";
        Pattern compiledPattern = Pattern.compile(youTubeUrlRegEx);
        Matcher matcher = compiledPattern.matcher(url);

        if (matcher.find()) {
            return url.replaceFirst(youTubeUrlRegEx, "");
        }
        Log.d("YOUTUBE_LINK",url);
        return url;
    }

    public void playVideoButtonClick() {
        // check if the user has entered a video url
        String urlstr = videoUrlEt.getText().toString();

        // if they haven't and it's empty, load the default video
        if (urlstr.isEmpty()) {
            videoId = "JzSeC5sFyT8";
        } else {
            // otherwise, get the videoID from the URL entered and store it in videoId
            videoId = extractVideoIdFromUrl(urlstr);
        }

        // if a valid URL is provided, play the video
        if (videoId != null) {
            youTubePlayerView.getYouTubePlayerWhenReady(this::playVideo);
        } else {
            // otherwise, let the user know the URL is not correct
            Toast.makeText(this, "Enter a valid YouTube video URL to play a video", Toast.LENGTH_LONG).show();
        }
    }

    // Method to play the video in the Video Player
    private void playVideo(YouTubePlayer player) {
        Log.d("YOUTUBE_LINK", videoId); //check id
        player.loadVideo(videoId, 0);
    }
}