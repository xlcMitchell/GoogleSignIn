package com.example.googlesigni;

import android.os.Bundle;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;

import com.google.android.gms.auth.api.identity.SignInCredential;
import com.squareup.picasso.Picasso;

public class YouTubePlayerActivity extends AppCompatActivity {

    SignInCredential credential;

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

    }
}