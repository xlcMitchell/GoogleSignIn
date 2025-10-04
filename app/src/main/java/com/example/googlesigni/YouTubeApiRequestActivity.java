package com.example.googlesigni;

import android.accounts.AccountManager;
import android.app.Activity;
import android.app.Dialog;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.os.Bundle;
import android.widget.Toast;

import androidx.activity.EdgeToEdge;
import androidx.activity.result.ActivityResult;
import androidx.activity.result.ActivityResultCallback;
import androidx.activity.result.ActivityResultLauncher;
import androidx.activity.result.contract.ActivityResultContracts;
import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;
import androidx.lifecycle.Observer;
import androidx.lifecycle.ViewModelProvider;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.googlesigni.datamodel.ChannelInfo;
import com.example.googlesigni.viewModel.YouTubeApiViewModel;
import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.GoogleApiAvailability;
import com.google.api.client.googleapis.extensions.android.gms.auth.GoogleAccountCredential;
import com.google.api.client.util.ExponentialBackOff;
import com.google.api.services.youtube.YouTubeScopes;
import com.google.firebase.FirebaseApp;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import pub.devrel.easypermissions.AfterPermissionGranted;
import pub.devrel.easypermissions.EasyPermissions;

public class YouTubeApiRequestActivity extends AppCompatActivity
        implements EasyPermissions.PermissionCallbacks {

    private static final String[] SCOPES = {
            YouTubeScopes.YOUTUBE_READONLY,
            YouTubeScopes.YOUTUBE_FORCE_SSL
    };

    GoogleAccountCredential gCredential;

    static final int REQUEST_PERMISSION_GET_ACCOUNTS = 1003;
    private static final String PREF_ACCOUNT_NAME = "accountName";


    static final int REQUEST_GOOGLE_PLAY_SERVICES = 1002;


    YouTubeApiViewModel ytViewModel;
    RecyclerView channelsRecyclerView;
    ChannelsAdapter adapter;

    public ActivityResultLauncher<Intent> youTubePermissionLauncher, chooseAccountLauncher;

    @Override
    protected void onCreate(Bundle savedInstanceState) {

        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_you_tube_api_request);

        // initialise the FirebaseApp
        FirebaseApp.initializeApp(this);

        channelsRecyclerView = findViewById(R.id.channelRecycleView);

        channelsRecyclerView.setLayoutManager(new LinearLayoutManager(this));
        adapter = new ChannelsAdapter(new ArrayList<>());
        channelsRecyclerView.setAdapter(adapter);

        ytViewModel = new ViewModelProvider(this).get(YouTubeApiViewModel.class);
        ytViewModel.setApiRequestActivity(this);

        youTubePermissionLauncher = registerForActivityResult(
                new ActivityResultContracts.StartActivityForResult(),
                new ActivityResultCallback<ActivityResult>() {
                    @Override
                    public void onActivityResult(ActivityResult result) {
                        if (result.getResultCode() == Activity.RESULT_OK) {
                            getResultsFromApi();
                        }
                    }
                }
        );

        chooseAccountLauncher = registerForActivityResult(
                new ActivityResultContracts.StartActivityForResult(),
                new ActivityResultCallback<ActivityResult>() {
                    @Override
                    public void onActivityResult(ActivityResult result) {
                        if (result.getResultCode() == RESULT_OK && result.getData() != null &&
                                result.getData().getExtras() != null) {
                            String accountName =
                                    result.getData().getStringExtra(AccountManager.KEY_ACCOUNT_NAME);
                            if (accountName != null) {
                                SharedPreferences settings =
                                        getPreferences(Context.MODE_PRIVATE);
                                SharedPreferences.Editor editor = settings.edit();
                                editor.putString(PREF_ACCOUNT_NAME, accountName);
                                editor.apply();
                                gCredential.setSelectedAccountName(accountName);
                                getResultsFromApi();
                            }
                        }
                    }
                }
        );

        final Observer<List<ChannelInfo>> channelsObserver = new Observer<List<ChannelInfo>>() {
            @Override
            public void onChanged(List<ChannelInfo> channelInfo) {
                updateChannelsData(channelInfo);
            }
        };

        ytViewModel.getYtChannels().observe(this, channelsObserver);

        gCredential = GoogleAccountCredential.usingOAuth2(
                        getApplicationContext(), Arrays.asList(SCOPES))
                .setBackOff(new ExponentialBackOff());

        if (gCredential.getSelectedAccountName() == null) {
            chooseAccount();
        }
    }

    @Override
    public void onPermissionsGranted(int requestCode, @NonNull List<String> perms) {

    }

    @Override
    public void onPermissionsDenied(int requestCode, @NonNull List<String> perms) {

    }

    @Override
    public void onRequestPermissionsResult(int requestCode,
                                           @NonNull String[] permissions,
                                           @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        EasyPermissions.onRequestPermissionsResult(
                requestCode, permissions, grantResults, this);
    }

    @AfterPermissionGranted(REQUEST_PERMISSION_GET_ACCOUNTS)
    private void chooseAccount() {
        if (EasyPermissions.hasPermissions(this, android.Manifest.permission.GET_ACCOUNTS))
        {
            String accountName = getPreferences(Context.MODE_PRIVATE)
                    .getString(PREF_ACCOUNT_NAME, null);
            if (accountName != null) {
                gCredential.setSelectedAccountName(accountName);
                getResultsFromApi();
            } else {
                // Start a dialog from which the user can choose an account
                chooseAccountLauncher.launch(gCredential.newChooseAccountIntent());
            }
        } else {
            // Request the GET_ACCOUNTS permission via a user dialog
            EasyPermissions.requestPermissions(
                    this,
                    "This app needs to access your Google account (via Contacts).",
                    REQUEST_PERMISSION_GET_ACCOUNTS,
                    android.Manifest.permission.GET_ACCOUNTS);
        }
    }

    private void getResultsFromApi() {
        if (! isGooglePlayServicesAvailable()) {
            acquireGooglePlayServices();
        } else if (gCredential.getSelectedAccountName() == null) {
            chooseAccount();
        } else if (! isDeviceOnline()) {
            Toast.makeText(this, "No network connection available.", Toast.LENGTH_LONG).show();
        } else {
            // thread to request new MakeRequestTask(gCredential).execute();
            ytViewModel.makeYtRequest(gCredential);
        }}

    private boolean isGooglePlayServicesAvailable() {
        GoogleApiAvailability apiAvailability =
                GoogleApiAvailability.getInstance();
        final int connectionStatusCode =
                apiAvailability.isGooglePlayServicesAvailable(this);
        return connectionStatusCode == ConnectionResult.SUCCESS;
    }

    private void acquireGooglePlayServices() {
        GoogleApiAvailability apiAvailability =
                GoogleApiAvailability.getInstance();
        final int connectionStatusCode =
                apiAvailability.isGooglePlayServicesAvailable(this);
        if (apiAvailability.isUserResolvableError(connectionStatusCode)) {
            showGooglePlayServicesAvailabilityErrorDialog(connectionStatusCode);
        }
    }
    void showGooglePlayServicesAvailabilityErrorDialog(
            final int connectionStatusCode) {
        GoogleApiAvailability apiAvailability = GoogleApiAvailability.getInstance();
        Dialog dialog = apiAvailability.getErrorDialog(
                YouTubeApiRequestActivity.this,
                connectionStatusCode,
                REQUEST_GOOGLE_PLAY_SERVICES);
        dialog.show();
    }

    private boolean isDeviceOnline() {
        ConnectivityManager connMgr =
                (ConnectivityManager) getSystemService(Context.CONNECTIVITY_SERVICE);
        NetworkInfo networkInfo = connMgr.getActiveNetworkInfo();
        return (networkInfo != null && networkInfo.isConnected());
    }


    private void updateChannelsData(List<ChannelInfo> channels){
        adapter.updateData(channels);
    }
}