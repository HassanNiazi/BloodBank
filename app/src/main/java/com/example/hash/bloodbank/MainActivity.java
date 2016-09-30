package com.example.hash.bloodbank;

import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.RequiresApi;
import android.support.design.widget.CoordinatorLayout;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.Snackbar;
import android.telephony.TelephonyManager;
import android.util.Log;
import android.view.View;
import android.support.design.widget.NavigationView;
import android.support.v4.view.GravityCompat;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.ActionBarDrawerToggle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.TextView;
import android.widget.Toast;

import com.digits.sdk.android.Digits;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.UploadTask;

import java.io.File;
import java.io.FileInputStream;
import java.util.Locale;
import java.util.Objects;

public class MainActivity extends AppCompatActivity
        implements NavigationView.OnNavigationItemSelectedListener {

    private static final String TAG = "MainActivity";
    private FirebaseAuth mAuth;
    private FirebaseAuth.AuthStateListener authStateListener;
    private StorageReference mStorageRef;
    Uri downloadUrl;

    RoundedImageView roundedImageView;
    TextView userNameTextView, phoneNoTextView, userCityTextView;

    //Capital Initials Used Intentionally
    String userName, phoneNo, imagePath, cityName, bloodGroup, callingActivity, country;

    @RequiresApi(api = Build.VERSION_CODES.KITKAT)
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        phoneNo = Digits.getActiveSession().getPhoneNumber();
        FirebaseDbCom firebaseDbCom = new FirebaseDbCom();

        Snackbar.make(findViewById(R.id.fab), "Hello " + phoneNo, Snackbar.LENGTH_LONG).show();

        //
        // Start of [Firebase Authentication as Anonymous User]
        //
        mAuth = FirebaseAuth.getInstance();
        authStateListener = new FirebaseAuth.AuthStateListener() {
            @Override
            public void onAuthStateChanged(@NonNull FirebaseAuth firebaseAuth) {
                FirebaseUser firebaseUser = firebaseAuth.getCurrentUser();
                if (firebaseUser != null) {
                    Log.d(TAG, "onAuthStateChanged: User Auth -> " + firebaseUser.getUid());
                } else {
                    Log.d(TAG, "onAuthStateChanged: Null User");
                }
            }
        };

        mAuth.signInAnonymously()
                .addOnCompleteListener(this, new OnCompleteListener<AuthResult>() {
                    @Override
                    public void onComplete(@NonNull Task<AuthResult> task) {
                        Log.d(TAG, "signInAnonymously:onComplete:" + task.isSuccessful());

                        if (!task.isSuccessful()) {
                            Log.w(TAG, "signInAnonymously", task.getException());
                            Toast.makeText(MainActivity.this, "Authentication failed.", Toast.LENGTH_SHORT).show();
                        }

                    }
                });

        mStorageRef = FirebaseStorage.getInstance().getReference();


        //
        // End Of [Firebase Authentication as Anonymous User]
        //


        //
        // Start of [getIntent and conditional Db queries]
        //

        Intent intent = getIntent();
        userName = intent.getStringExtra(getResources().getString(R.string.userNameKeyValue));
        phoneNo = intent.getStringExtra(getResources().getString(R.string.phoneNo));
        imagePath = intent.getStringExtra(getResources().getString(R.string.userImageKey));
        cityName = intent.getStringExtra(getResources().getString(R.string.cityNameKey));
        bloodGroup = intent.getStringExtra(getResources().getString(R.string.blood_group));
        callingActivity = intent.getStringExtra(getString(R.string.callingActivity));


        Log.d(TAG, "onCreate: Calling Activity is " + callingActivity);
        String signUpActivity = getString(R.string.SignUpActivity);
        Log.d(TAG, "onCreate: SignUpactivity : " + signUpActivity);
        if (Objects.equals(callingActivity, signUpActivity)) {
            //New User <> Save To DB

            try {
                Uri file = Uri.fromFile(new File(imagePath));
                StorageReference storageReference = mStorageRef.child("images/" + phoneNo + ".storageReference");

                storageReference.putFile(file)
                        .addOnSuccessListener(new OnSuccessListener<UploadTask.TaskSnapshot>() {
                            @Override
                            public void onSuccess(UploadTask.TaskSnapshot taskSnapshot) {
                                // Get a URL to the uploaded content
                                downloadUrl = taskSnapshot.getDownloadUrl();
                            }
                        })
                        .addOnFailureListener(new OnFailureListener() {
                            @Override
                            public void onFailure(@NonNull Exception exception) {
                                // Handle unsuccessful uploads
                                // ...
                            }
                        });
            } catch (Exception ex) {
                Toast.makeText(this, "Online Storage Access Failed", Toast.LENGTH_SHORT).show();
            }

            User user = new User(userName, downloadUrl, bloodGroup, cityName, country, 0, 0, true);
            firebaseDbCom.writeToDBProfiles(user, phoneNo);

        } else {
            phoneNo = Digits.getActiveSession().getPhoneNumber();
            if (phoneNo != null) {

                Log.d(TAG, "onCreate: PhoneNo" + phoneNo);
                User user = firebaseDbCom.readFromDBUserProfile(phoneNo);

                userName = user.getName();
                //imagePath =
                cityName = user.getCity();
                bloodGroup = user.getBloodGroup();
                country = user.getCountry();


                userNameTextView.setText(user.getName());
                phoneNoTextView.setText(phoneNo);
                userCityTextView.setText(user.getCity());

            } else {
                Toast.makeText(this, "Unable to resolve User", Toast.LENGTH_SHORT).show();
            }
        }


        //
        // End of [getIntent and conditional Db queries]
        //


        //
        // Start of [Some UI Stuff]
        //

        FloatingActionButton fab = (FloatingActionButton) findViewById(R.id.fab);
        fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                FirebaseDatabase database = FirebaseDatabase.getInstance();
                DatabaseReference myRef = database.getReference("message");
                myRef.setValue("Hello, World!");
            }
        });

        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        ActionBarDrawerToggle toggle = new ActionBarDrawerToggle(
                this, drawer, toolbar, R.string.navigation_drawer_open, R.string.navigation_drawer_close);
        drawer.setDrawerListener(toggle);
        toggle.syncState();

        NavigationView navigationView = (NavigationView) findViewById(R.id.nav_view);
        navigationView.setNavigationItemSelectedListener(this);
        View v = navigationView.getHeaderView(0);

        roundedImageView = (RoundedImageView) v.findViewById(R.id.userImageNavMenu);
        userNameTextView = (TextView) v.findViewById(R.id.userNameNavmenu);
        phoneNoTextView = (TextView) v.findViewById(R.id.phoneNumberNavMenu);
        userCityTextView = (TextView) v.findViewById(R.id.cityNavMenu);

        try {
            Bitmap bitmap = getThumbnail(imagePath);
            roundedImageView.setImageBitmap(bitmap);
        } catch (Exception e) {
            Toast.makeText(this, e.getMessage(), Toast.LENGTH_SHORT).show();
        }


        userNameTextView.setText(userName);
        phoneNoTextView.setText(phoneNo);
        userCityTextView.setText(cityName);

        Toast.makeText(this, "Someone with blood group " + bloodGroup + " Signed In", Toast.LENGTH_SHORT).show();

        //
        // End of [Some UI Stuff]
        //


    }

    @Override
    protected void onStart() {
        super.onStart();
        mAuth.addAuthStateListener(authStateListener);
    }

    @Override
    protected void onStop() {
        super.onStop();
        mAuth.removeAuthStateListener(authStateListener);
    }

    @Override
    public void onBackPressed() {
        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        if (drawer.isDrawerOpen(GravityCompat.START)) {
            drawer.closeDrawer(GravityCompat.START);
        } else {
            super.onBackPressed();
        }
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.main, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();

        //noinspection SimplifiableIfStatement
        if (id == R.id.action_settings) {
            return true;
        }

        return super.onOptionsItemSelected(item);
    }

    @SuppressWarnings("StatementWithEmptyBody")
    @Override
    public boolean onNavigationItemSelected(MenuItem item) {
        // Handle navigation view item clicks here.
        int id = item.getItemId();

        if (id == R.id.nav_chat) {
            // Handle the camera action
        } else if (id == R.id.nav_map) {

        } else if (id == R.id.nav_personlization) {

        }

        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        drawer.closeDrawer(GravityCompat.START);
        return true;
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
            } else if (tm.getPhoneType() != TelephonyManager.PHONE_TYPE_CDMA) { // device is not 3G (would be unreliable)
                String networkCountry = tm.getNetworkCountryIso();
                if (networkCountry != null && networkCountry.length() == 2) { // network country code is available
                    return networkCountry.toLowerCase(Locale.US);
                }
            }
        } catch (Exception e) {
        }
        return null;
    }

    private Uri saveToFirebaseStorage(Uri file) {
        //
        // Start of [Firebase Storage]
        //

        mStorageRef = FirebaseStorage.getInstance().getReference();
//        Uri file = Uri.fromFile(new File("path/to/images/rivers.jpg"));
        StorageReference riversRef = mStorageRef.child("images/rivers.jpg");
        final Uri[] downloadUrl = new Uri[1];
        riversRef.putFile(file)
                .addOnSuccessListener(new OnSuccessListener<UploadTask.TaskSnapshot>() {
                    @Override
                    public void onSuccess(UploadTask.TaskSnapshot taskSnapshot) {
                        // Get a URL to the uploaded content
                        downloadUrl[0] = taskSnapshot.getDownloadUrl();
                    }
                })
                .addOnFailureListener(new OnFailureListener() {
                    @Override
                    public void onFailure(@NonNull Exception exception) {
                        Toast.makeText(MainActivity.this, "Failed to connect to server. Enable Data Connectivity", Toast.LENGTH_SHORT).show();
                    }
                });

        return downloadUrl[0];
        //
        // End of [Firebase Storage]
        //

    }
}
