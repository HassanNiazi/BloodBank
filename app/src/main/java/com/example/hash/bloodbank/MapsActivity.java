package com.example.hash.bloodbank;

import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.support.v4.app.FragmentActivity;
import android.os.Bundle;
import android.widget.Switch;
import android.widget.Toast;

import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.BitmapDescriptor;
import com.google.android.gms.maps.model.BitmapDescriptorFactory;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.Marker;
import com.google.android.gms.maps.model.MarkerOptions;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import static android.R.attr.bitmap;

public class MapsActivity extends FragmentActivity implements OnMapReadyCallback {

    private GoogleMap mMap;
    String userCity = null;
    LatLng userLatLng = null;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_maps);
        // Obtain the SupportMapFragment and get notified when the map is ready to be used.
        SupportMapFragment mapFragment = (SupportMapFragment) getSupportFragmentManager()
                .findFragmentById(R.id.map);
        mapFragment.getMapAsync(this);
        Intent intent = getIntent();
        userCity = intent.getStringExtra(getString(R.string.cityNameKey));
        Double lat = intent.getDoubleExtra(getString(R.string.latitude),0.0);
        Double lng = intent.getDoubleExtra(getString(R.string.longitude),0.0);
        userLatLng = new LatLng(lat,lng);

    }


    @Override
    public void onMapReady(GoogleMap googleMap) {
        mMap = googleMap;
        mMap.setOnInfoWindowClickListener(new GoogleMap.OnInfoWindowClickListener() {
            @Override
            public void onInfoWindowClick(Marker marker) {
                Toast.makeText(MapsActivity.this, "Info Window Clicked", Toast.LENGTH_SHORT).show();
            }
        });
        userLatLng = new LatLng(33.6847011,73.0156131); // TODO Replcae with the actual position of the user
        readFromDBUserCoords(userCity,userLatLng);
    }

    public void readFromDBUserCoords(String city,LatLng userLocation) {
        DatabaseReference databaseReference = FirebaseDatabase.getInstance().getReference().child("userCoords");
        databaseReference.child(city).addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                Bitmap bitmap = BitmapFactory.decodeResource(getResources(),R.drawable.apositive);
                for(DataSnapshot snapshot:  dataSnapshot.getChildren())
                {
                    UserCoordinateClass coordinateClassObject = snapshot.getValue(UserCoordinateClass.class);
                    switch (coordinateClassObject.getBloodGroup())
                    {
                        case "A+":
                            bitmap = BitmapFactory.decodeResource(getResources(),R.drawable.apositive);

                            return;
                        case "B+":
                            bitmap = BitmapFactory.decodeResource(getResources(),R.drawable.bpositive);

                            return;
                        case "A-":
                            bitmap = BitmapFactory.decodeResource(getResources(),R.drawable.anegative);

                            return;
                        case "B-":
                            bitmap = BitmapFactory.decodeResource(getResources(),R.drawable.bnegative);

                            return;
                        case "AB+":
                            bitmap = BitmapFactory.decodeResource(getResources(),R.drawable.abpositive);

                            return;
                        case "AB-":
                            bitmap = BitmapFactory.decodeResource(getResources(),R.drawable.abnegative);

                            return;
                        case "O+":
                            bitmap = BitmapFactory.decodeResource(getResources(),R.drawable.opositive);

                            return;
                        case "O-":
                            bitmap = BitmapFactory.decodeResource(getResources(),R.drawable.onegative);

                            return;
                    }
                    bitmap = Bitmap.createScaledBitmap(bitmap, 120, 120, false);
                    mMap.addMarker(new MarkerOptions().position(new LatLng(coordinateClassObject.getLat(),
                            coordinateClassObject.getLng())).title(coordinateClassObject.getName()).
                            snippet("Blood Group : "+coordinateClassObject.getBloodGroup())
                            .icon(BitmapDescriptorFactory.fromBitmap(bitmap))
                    );
                }
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {

            }
        });
        mMap.moveCamera(CameraUpdateFactory.newLatLngZoom(userLocation,16.0f));

    }
}
