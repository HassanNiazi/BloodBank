package com.example.hash.bloodbank;

import android.*;
import android.Manifest;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.location.Address;
import android.location.Geocoder;
import android.support.design.widget.Snackbar;
import android.support.v4.app.ActivityCompat;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.telephony.TelephonyManager;
import android.view.View;
import android.widget.AutoCompleteTextView;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.util.List;
import java.util.Locale;

public class PreferencesActivity extends AppCompatActivity {
    String country = "null";
    double lat,lng;

//    EditText userName,email,countryTextBox;
            EditText city;
    RoundedImageView roundedImageView;
//    AutoCompleteTextView gender;
          AutoCompleteTextView  bloodGroup;
    Button buttonDone;



    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_preferences);
        country = getUserCountry(PreferencesActivity.this);

        Intent intent = getIntent();
//        lat=0;
//        lng=0;
//        userName = (EditText) findViewById(R.id.editTextUserNamePrefs);
//        email = (EditText) findViewById(R.id.editTextEmailPrefs);
//        countryTextBox = (EditText) findViewById(R.id.editTextCountryPrefs);
        city = (EditText) findViewById(R.id.editTextCity);
        roundedImageView = (RoundedImageView) findViewById(R.id.roundedImageViewPrefs);
//        gender = (AutoCompleteTextView) findViewById(R.id.autoCompleteTextViewGender);
        bloodGroup = (AutoCompleteTextView) findViewById(R.id.autoCompleteTextViewBloodGroup);
        buttonDone = (Button) findViewById(R.id.buttonDonePrefs);


//        userName.setText(intent.getStringExtra(getResources().getString(R.string.userNameKeyValue)));
//        countryTextBox.setText(country);

        try {

            String filename = intent.getStringExtra(getResources().getString(R.string.userImageKey));
            Bitmap bitmap = getThumbnail(filename);
            roundedImageView.setImageBitmap(bitmap);
        }

        catch (Exception e)
        {
            Toast.makeText(this, e.getMessage(), Toast.LENGTH_SHORT).show();
        }
        buttonDone.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {


                if (ContextCompat.checkSelfPermission(PreferencesActivity.this, Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED) {

                    if (ActivityCompat.shouldShowRequestPermissionRationale(PreferencesActivity.this, Manifest.permission.ACCESS_FINE_LOCATION)) {

                    } else {

                        ActivityCompat.requestPermissions(PreferencesActivity.this, new String[]{Manifest.permission.ACCESS_FINE_LOCATION}, 180);

                    }
                }

                if (ContextCompat.checkSelfPermission(PreferencesActivity.this,  Manifest.permission.ACCESS_FINE_LOCATION) == PackageManager.PERMISSION_GRANTED) {


                    GPSTracker gpsTracker = new GPSTracker(PreferencesActivity.this);

                    if (gpsTracker.getIsGPSTrackingEnabled())
                    {
                        lat = gpsTracker.getLatitude();
                        lng = gpsTracker.getLongitude();
//                        countryTextBox.setText(gpsTracker.getCountryName(PreferencesActivity.this));
                        city.setText(gpsTracker.getLocality(PreferencesActivity.this));
                        Toast.makeText(PreferencesActivity.this, "You have Permission " + lat +" , "+ lng, Toast.LENGTH_LONG).show();
                    }
                    else
                    {
                        // can't get location
                        // GPS or Network is not enabled
                        // Ask user to enable GPS/network in settings
                        gpsTracker.showSettingsAlert();
                    }


                }
                else {
                    Toast.makeText(PreferencesActivity.this, "You dont have Permission", Toast.LENGTH_SHORT).show();
                }


            }
        });





    }

    public Bitmap getThumbnail(String filename) {

        Bitmap thumbnail = null;
        try {
            File filePath = this.getFileStreamPath(filename);
            FileInputStream fi = new FileInputStream(filePath);
            thumbnail = BitmapFactory.decodeStream(fi);
        } catch (Exception ex) {
        }

        return thumbnail;
    }

    public static String getUserCountry(Context context) {
        try {
            final TelephonyManager tm = (TelephonyManager) context.getSystemService(Context.TELEPHONY_SERVICE);
            final String simCountry = tm.getSimCountryIso();
            if (simCountry != null && simCountry.length() == 2) { // SIM country code is available
                return simCountry.toLowerCase(Locale.US);
            }
            else if (tm.getPhoneType() != TelephonyManager.PHONE_TYPE_CDMA) { // device is not 3G (would be unreliable)
                String networkCountry = tm.getNetworkCountryIso();
                if (networkCountry != null && networkCountry.length() == 2) { // network country code is available
                    return networkCountry.toLowerCase(Locale.US);
                }
            }
        }
        catch (Exception e) { }
        return null;
    }

    private String getCityName(double MyLat, double MyLong) throws IOException {
        Geocoder geocoder = new Geocoder(this, Locale.getDefault());
        List<Address> addresses = geocoder.getFromLocation(MyLat, MyLong, 1);
        String cityName = addresses.get(0).getAddressLine(0);
       String stateName = addresses.get(0).getAddressLine(1);
        String countryName = addresses.get(0).getAddressLine(2);
        return cityName;
    }
}
