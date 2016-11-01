package com.example.hash.bloodbank;

import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.location.Address;
import android.location.Geocoder;
import android.location.Location;
import android.location.LocationListener;
import android.location.LocationManager;
import android.os.Bundle;
import android.support.v4.app.ActivityCompat;
import android.support.v4.app.FragmentActivity;
import android.support.v7.app.AlertDialog;
import android.view.View;
import android.widget.Toast;

import com.digits.sdk.android.Digits;
import com.google.android.gms.auth.api.signin.SignInAccount;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.MarkerOptions;

import java.io.IOException;
import java.util.List;
import java.util.Locale;

public class UserLocationRegistrationActivity extends FragmentActivity implements OnMapReadyCallback {

    private static final int MY_PERMISSIONS_REQUEST_LOCATION = 160;
    double latitude;
    double longitude;
    private GoogleMap mMap;
    String phoneNumber;
    String cityName = "-_-";

// TODO Add a waiting circular notation while the device is getting data from the gps

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_user_location_registration);
        // Obtain the SupportMapFragment and get notified when the map is ready to be used.
        SupportMapFragment mapFragment = (SupportMapFragment) getSupportFragmentManager()
                .findFragmentById(R.id.userRegistrationMap);
        mapFragment.getMapAsync(this);
        Intent intent = getIntent();
        phoneNumber = intent.getStringExtra(getResources().getString(R.string.phoneNo));
        findViewById(R.id.autoDetectButton).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                checkForUserLocation();
            }
        });

        findViewById(R.id.doneUserLocation).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                Intent signUpActivity =  new Intent(UserLocationRegistrationActivity.this, SignUpActivity.class);
                Bundle bundle = new Bundle();
                bundle.putDouble(getString(R.string.latitude),latitude);
                bundle.putDouble(getString(R.string.longitude),longitude);
                bundle.putString(getString(R.string.phoneNo),phoneNumber);
                bundle.putString(getString(R.string.cityNameKey),cityName);
                signUpActivity.putExtras(bundle);
                startActivity(signUpActivity);
            }
        });
    }


    /**
     * Manipulates the map once available.
     * This callback is triggered when the map is ready to be used.
     * This is where we can add markers or lines, add listeners or move the camera. In this case,
     * we just add a marker near Sydney, Australia.
     * If Google Play services is not installed on the device, the user will be prompted to install
     * it inside the SupportMapFragment. This method will only be triggered once the user has
     * installed Google Play services and returned to the app.
     */
    @Override
    public void onMapReady(GoogleMap googleMap) {
        mMap = googleMap;

        // Add a marker in Sydney and move the camera
        LatLng sydney = new LatLng(-34, 151);
        mMap.addMarker(new MarkerOptions().position(sydney).title("Marker in Sydney"));
        mMap.moveCamera(CameraUpdateFactory.newLatLng(sydney));
        mMap.setOnMapClickListener(new GoogleMap.OnMapClickListener() {
            @Override
            public void onMapClick(LatLng latLng) {
                mMap.clear();
                mMap.addMarker(new MarkerOptions().position(latLng).title("My Default Location"));
                mMap.moveCamera(CameraUpdateFactory.newLatLng(latLng));
                try {
                     cityName = getCityName(latLng,UserLocationRegistrationActivity.this);
                    Toast.makeText(UserLocationRegistrationActivity.this, cityName + "1", Toast.LENGTH_SHORT).show();

                } catch (IOException e) {
                    e.printStackTrace();
                }
                findViewById(R.id.autoDetectButton).setEnabled(true);
                findViewById(R.id.doneUserLocation).setEnabled(true);
            }
        });
    }

    public void checkForUserLocation() {

        LocationManager locationManager = (LocationManager) getSystemService(Context.LOCATION_SERVICE);
        LocationListener mLocationListener = new LocationListener() {
            @Override
            public void onLocationChanged(final Location location) {
                //your code here
                mMap.clear();
                latitude = location.getLatitude();
                longitude = location.getLongitude();
                LatLng latLng = new LatLng(latitude, longitude);
                mMap.addMarker(new MarkerOptions().position(latLng).title("My Default Location"));
                mMap.moveCamera(CameraUpdateFactory.newLatLngZoom(latLng, 16.0f));
                try {
                    cityName = getCityName(latLng,UserLocationRegistrationActivity.this);
                    Toast.makeText(UserLocationRegistrationActivity.this, cityName, Toast.LENGTH_SHORT).show();
                } catch (IOException e) {
                    e.printStackTrace();
                }
                findViewById(R.id.autoDetectButton).setEnabled(true);
                findViewById(R.id.doneUserLocation).setEnabled(true);
            }

            @Override
            public void onStatusChanged(String provider, int status, Bundle extras) {

            }

            @Override
            public void onProviderEnabled(String provider) {

            }

            @Override
            public void onProviderDisabled(String provider) {

            }
        };

        if (ActivityCompat.checkSelfPermission(UserLocationRegistrationActivity.this, android.Manifest.permission.ACCESS_FINE_LOCATION)
                != PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission(UserLocationRegistrationActivity.this,
                android.Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
            // TODO: Consider calling
            //    ActivityCompat#requestPermissions
            // here to request the missing permissions, and then overriding
            //   public void onRequestPermissionsResult(int requestCode, String[] permissions,
            //                                          int[] grantResults)
            // to handle the case where the user grants the permission. See the documentation
            // for ActivityCompat#requestPermissions for more details.

            ActivityCompat.requestPermissions(UserLocationRegistrationActivity.this,
                    new String[]{android.Manifest.permission.ACCESS_FINE_LOCATION, android.Manifest.permission.ACCESS_COARSE_LOCATION},
                    MY_PERMISSIONS_REQUEST_LOCATION);


            //  return;
        }
        if (ActivityCompat.checkSelfPermission(UserLocationRegistrationActivity.this, android.Manifest.permission.ACCESS_FINE_LOCATION)
                == PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission(UserLocationRegistrationActivity.this,
                android.Manifest.permission.ACCESS_COARSE_LOCATION) == PackageManager.PERMISSION_GRANTED) {
            if (!locationManager.isProviderEnabled(LocationManager.GPS_PROVIDER)) {
                askToTurnOnGPS();
            }
            locationManager.requestLocationUpdates(LocationManager.NETWORK_PROVIDER, 0, 0, mLocationListener);
            findViewById(R.id.autoDetectButton).setEnabled(false);
            findViewById(R.id.doneUserLocation).setEnabled(false);
        }
    }


    private void askToTurnOnGPS() {
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setMessage("The application needs to enable GPS.")
                .setTitle("GPS");
        builder.setPositiveButton(getResources().getString(R.string.OK), new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                Intent gpsOptionsIntent = new Intent(android.provider.Settings.ACTION_LOCATION_SOURCE_SETTINGS);
                startActivity(gpsOptionsIntent);
            }
        });

        builder.setNegativeButton(getResources().getString(R.string.cancel), new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                // User Didn't allowed gps access now let them chose the location for themselves...   *******
                Toast.makeText(UserLocationRegistrationActivity.this, "GPS Access Denied. Please select the location manually ", Toast.LENGTH_LONG).show();
            }
        });
        builder.show();
    }

    private String getCityName(LatLng latLng, Context context) throws IOException {
        Geocoder gcd = new Geocoder(context, Locale.getDefault());
        List<Address> addresses = gcd.getFromLocation(latLng.latitude, latLng.longitude, 1);
        if (addresses.size() > 0) {
            return addresses.get(0).getLocality();
        } else {
            return null;
        }
    }
}
