package com.example.hash.bloodbank;

import android.content.Intent;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.Manifest;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import com.digits.sdk.android.AuthCallback;
import com.digits.sdk.android.Digits;
import com.digits.sdk.android.DigitsAuthButton;
import com.digits.sdk.android.DigitsException;
import com.digits.sdk.android.DigitsSession;

import com.twitter.sdk.android.core.TwitterAuthConfig;
import com.twitter.sdk.android.core.TwitterCore;
import io.fabric.sdk.android.Fabric;


public class LoginActivity extends AppCompatActivity implements View.OnClickListener{


    // Note: Your consumer key and secret should be obfuscated in your source code before shipping.
    private static final String TWITTER_KEY = "Fto25uhjdYYi7hvrLHiZTq7xa";
    private static final String TWITTER_SECRET = "Fa7xPIip9FxSNzowmyRcBCtXh7EXh8GCGSWYHmUvBZdCDpENek";


    // UI Controls


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        TwitterAuthConfig authConfig = new TwitterAuthConfig(TWITTER_KEY, TWITTER_SECRET);
        Digits.Builder digitsBuilder = new Digits.Builder().withTheme(R.style.CustomDigitsTheme);
        Fabric.with(this, new TwitterCore(authConfig), digitsBuilder.build());


        final Intent intent = new Intent(this,SignUp.class);
        setContentView(R.layout.activity_login);
        //UI Initialization


        //UI Listeners


        //Digits

        int permissionCheck = ContextCompat.checkSelfPermission(LoginActivity.this,
                Manifest.permission.WRITE_CALENDAR);

        DigitsAuthButton digitsButton = (DigitsAuthButton) findViewById(R.id.auth_button);
        digitsButton.setText("Sign Up Using Phone Number");
        digitsButton.setCallback(new AuthCallback() {
            @Override
            public void success(DigitsSession session, String phoneNumber) {
                // TODO: associate the session userID with your user model
                Toast.makeText(getApplicationContext(), "Authentication successful for "
                        + phoneNumber, Toast.LENGTH_LONG).show();
                intent.putExtra(getResources().getString(R.string.phoneNo),phoneNumber);
                startActivity(intent);
            }

            @Override
            public void failure(DigitsException exception) {
                Log.d("Digits", "Sign in with Digits failure", exception);
            }
        });


    }


    @Override
    public void onClick(View v) {

    }



}
