package com.example.hash.bloodbank;

import android.content.Intent;
import android.content.pm.PackageManager;
import android.support.v4.app.ActivityCompat;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.Manifest;
import android.view.View;
import android.widget.Toast;

import com.digits.sdk.android.AuthCallback;
import com.digits.sdk.android.Digits;
import com.digits.sdk.android.DigitsAuthButton;
import com.digits.sdk.android.DigitsException;
import com.digits.sdk.android.DigitsSession;

import com.twitter.sdk.android.core.TwitterAuthConfig;
import com.twitter.sdk.android.core.TwitterCore;

import io.fabric.sdk.android.Fabric;


public class LoginActivity extends AppCompatActivity implements View.OnClickListener {


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        setContentView(R.layout.activity_login);
        TwitterAuthConfig authConfig = new TwitterAuthConfig(getResources().getString(R.string.TWITTER_KEY), getResources().getString(R.string.TWITTER_SECRET));
        Digits.Builder digitsBuilder = new Digits.Builder().withTheme(R.style.CustomDigitsTheme);
        Fabric.with(this, new TwitterCore(authConfig), digitsBuilder.build());

        if (Digits.getActiveSession() != null) {
            Log.d("Digits", "onCreate: Digits.getActiveSession().getPhoneNumber()  = " + Digits.getActiveSession().getPhoneNumber());
            Intent intent = new Intent(this,MainActivity.class);
            intent.putExtra(getString(R.string.phoneNo),Digits.getActiveSession().getPhoneNumber());
            intent.putExtra(getString(R.string.callingActivity),R.string.LoginActivity);
            startActivity(intent);
//        Direct To MainActivity

        } else {

        DigitsAuthButton digitsButton = (DigitsAuthButton) findViewById(R.id.auth_button);
        digitsButton.setText("Sign Up Using Phone Number");
        digitsButton.setCallback(new AuthCallback() {
            @Override
            public void success(DigitsSession session, String phoneNumber) {
                // TODO: associate the session userID with your user model
                Toast.makeText(getApplicationContext(), "Authentication successful for " + phoneNumber, Toast.LENGTH_LONG).show();
                Intent intent = new Intent(LoginActivity.this, SignUpActivity.class);
                intent.putExtra(getResources().getString(R.string.phoneNo), phoneNumber);
                startActivity(intent);
            }
            @Override
            public void failure(DigitsException exception) {
                Log.d("Digits", "Sign in with Digits failure", exception);
            }
        });


        }
    }

    @Override
    public void onClick(View v) {

    }


}
