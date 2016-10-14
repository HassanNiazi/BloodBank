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
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.NavigationView;
import android.support.v4.view.GravityCompat;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.ActionBarDrawerToggle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.telephony.TelephonyManager;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.ImageView;
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
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.google.firebase.storage.FileDownloadTask;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.twitter.sdk.android.core.TwitterAuthConfig;
import com.twitter.sdk.android.core.TwitterCore;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.util.Locale;

import io.fabric.sdk.android.Fabric;

public class MainActivity extends AppCompatActivity
        implements NavigationView.OnNavigationItemSelectedListener {

    private static final String TAG = "MainActivity";
    private static final int OPEN_CAMERA = 120;
    Uri downloadUrl;
    User user;
    RoundedImageView roundedImageView;
    TextView userNameTextView, phoneNoTextView, userCityTextView;
    String userName, phoneNo, imagePath, cityName, bloodGroup, callingActivity, country;
    private FirebaseAuth mAuth;
    private FirebaseAuth.AuthStateListener authStateListener;
    private StorageReference mStorageRef;

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

    // TODO Handle Dual Sim Users Scenerio


    @RequiresApi(api = Build.VERSION_CODES.KITKAT)
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        TwitterAuthConfig authConfig = new TwitterAuthConfig(getResources().getString(R.string.TWITTER_KEY), getResources().getString(R.string.TWITTER_SECRET));
        Digits.Builder digitsBuilder = new Digits.Builder().withTheme(R.style.CustomDigitsTheme);
        Fabric.with(this, new TwitterCore(authConfig), digitsBuilder.build());


//        Snackbar.make(findViewById(R.id.fab), "Hello " + phoneNo, Snackbar.LENGTH_LONG).show();

//        //
//        // Start of [Firebase Authentication as Anonymous User]
//        //
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

//
//        //
//        // End Of [Firebase Authentication as Anonymous User]
//        //
//
//
//        //
//        // Start of [getIntent and conditional Db queries]
//        //

//        Intent intent = getIntent();
//        userName = intent.getStringExtra(getResources().getString(R.string.userNameKeyValue));
//        phoneNo = intent.getStringExtra(getResources().getString(R.string.phoneNo));
//        imagePath = intent.getStringExtra(getResources().getString(R.string.userImageKey));
//        cityName = intent.getStringExtra(getResources().getString(R.string.cityNameKey));
//        bloodGroup = intent.getStringExtra(getResources().getString(R.string.blood_group));
//        callingActivity = intent.getStringExtra(getResources().getString(R.string.callingActivity));

//
//        Log.d(TAG, "onCreate: Calling Activity is " + callingActivity);
//        String signUpActivity = getString(R.string.SignUpActivity);
//        Log.d(TAG, "onCreate: SignUpactivity : " + signUpActivity);
//        if (Objects.equals(callingActivity, signUpActivity)) {
//            //New User <> Save To DB
//
//
////            downloadUrl = "NoUrl";
//            country = getUserCountry(this);
//            User user = new User(userName, bloodGroup, cityName, country, 0, 0, true);
//            firebaseDbCom.writeToDBProfiles(user, phoneNo);
//
//        } else {
        phoneNo = Digits.getActiveSession().getPhoneNumber();
        if (phoneNo != null) {

            Log.d(TAG, "onCreate: PhoneNo" + phoneNo);
            try {

                DatabaseReference database = FirebaseDatabase.getInstance().getReference();
                DatabaseReference myRef = database.child("profiles");

                myRef.child(phoneNo).addListenerForSingleValueEvent(new ValueEventListener() {
                    @Override
                    public void onDataChange(DataSnapshot dataSnapshot) {
                        user = dataSnapshot.getValue(User.class);
                        userName = user.getName();
                        cityName = user.getCity();
                        bloodGroup = user.getBloodGroup();
                        country = user.getCountry();


                        userNameTextView.setText(user.getName());
                        phoneNoTextView.setText(phoneNo);
                        userCityTextView.setText(user.getCity());

                        try {
                            final File localFile = File.createTempFile(phoneNo, ".png");
                            StorageReference storageReference = FirebaseStorage.getInstance().getReference();
                            StorageReference fileRef = storageReference.child("images/" + phoneNo + ".png");
                            fileRef.getFile(localFile)
                                    .addOnSuccessListener(new OnSuccessListener<FileDownloadTask.TaskSnapshot>() {
                                        @Override
                                        public void onSuccess(FileDownloadTask.TaskSnapshot taskSnapshot) {
                                            // Successfully downloaded data to local file
                                            BitmapFactory.Options o = new BitmapFactory.Options();
                                            o.inSampleSize = 1;
                                            Bitmap b = BitmapFactory.decodeFile(localFile.getPath(), o);
                                            roundedImageView.setImageBitmap(b);
                                        }
                                    }).addOnFailureListener(new OnFailureListener() {
                                @Override
                                public void onFailure(@NonNull Exception exception) {
                                    // Handle failed download
                                    // ...
                                }
                            });
                        } catch (IOException e) {
                            e.printStackTrace();
                        }

                    }

                    @Override
                    public void onCancelled(DatabaseError databaseError) {

                    }

                });


            } catch (Exception ex) {
                Log.d(TAG, "onCreate: " + ex.getMessage());
            }
        } else {
            Toast.makeText(this, "Unable to resolve User", Toast.LENGTH_SHORT).show();
        }
//        }
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

//        try {
//            Bitmap bitmap = getThumbnail(imagePath);
//            roundedImageView.setImageBitmap(bitmap);
//        } catch (Exception e) {
//            Toast.makeText(this, e.getMessage(), Toast.LENGTH_SHORT).show();
//        }
//        userNameTextView.setText(userName);
//        phoneNoTextView.setText(phoneNo);
//        userCityTextView.setText(cityName);

        //
        // End of [Some UI Stuff]
        //


    }

    @Override
    protected void onStart() {
        super.onStart();
//        mAuth.addAuthStateListener(authStateListener);
    }

    @Override
    protected void onStop() {
        super.onStop();
//        mAuth.removeAuthStateListener(authStateListener);
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

        } else if (id == R.id.nav_map) {

            Intent intent = new Intent(this, MapsActivity.class);
            startActivity(intent);

        } else if (id == R.id.nav_personlization) {

        }

        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        drawer.closeDrawer(GravityCompat.START);
        return true;
    }

    private File readUserImageFromFirebaseDatabase(String userPhoneNo) throws IOException {
        File localFile = File.createTempFile(userPhoneNo, ".png");
        StorageReference storageReference = FirebaseStorage.getInstance().getReference();
        StorageReference fileRef = storageReference.child("images/" + userPhoneNo + ".png");
        fileRef.getFile(localFile)
                .addOnSuccessListener(new OnSuccessListener<FileDownloadTask.TaskSnapshot>() {
                    @Override
                    public void onSuccess(FileDownloadTask.TaskSnapshot taskSnapshot) {
                        // Successfully downloaded data to local file
                        // ...
                    }
                }).addOnFailureListener(new OnFailureListener() {
            @Override
            public void onFailure(@NonNull Exception exception) {
                // Handle failed download
                // ...
            }
        });
        return localFile;
    }

    public String saveImageToInternalStorage(Bitmap image, String filename) {

        try {
            FileOutputStream fos = this.openFileOutput(filename + ".png", Context.MODE_PRIVATE);
            image.compress(Bitmap.CompressFormat.PNG, 100, fos);
            fos.close();
        } catch (Exception e) {
            Log.e("saveToInternalStorage()", e.getMessage());
        }
        return filename + ".png";

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

//    private void saveToFirebaseStorage(Bitmap bitmap) {
//        try {
//            mStorageRef = FirebaseStorage.getInstance().getReference();
//
////                Uri file = Uri.fromFile(new File(imagePath));
////            Bitmap bitmap = getThumbnail(_imagePath);
//            ByteArrayOutputStream baos = new ByteArrayOutputStream();
//            bitmap.compress(Bitmap.CompressFormat.JPEG, 100, baos);
//            byte[] data = baos.toByteArray();
//            StorageReference storageReference = mStorageRef.child("images/" + phoneNo + ".jpg");
//            UploadTask uploadTask = storageReference.putBytes(data);
//            uploadTask.addOnSuccessListener(new OnSuccessListener<UploadTask.TaskSnapshot>() {
//                @Override
//                public void onSuccess(UploadTask.TaskSnapshot taskSnapshot) {
//                    // Get a URL to the uploaded content
//                    downloadUrl = taskSnapshot.getDownloadUrl();
//                }
//            })
//                    .addOnFailureListener(new OnFailureListener() {
//                        @Override
//                        public void onFailure(@NonNull Exception exception) {
//                            // Handle unsuccessful uploads
//                            // ...
//                        }
//                    });
//        } catch (Exception ex) {
//            Toast.makeText(this, "Online Storage Access Failed", Toast.LENGTH_SHORT).show();
//        }
//    }

//    public void takePhoto() {
//
//        Intent cameraIntent = new Intent(android.provider.MediaStore.ACTION_IMAGE_CAPTURE);
//        startActivityForResult(cameraIntent, OPEN_CAMERA);
//
//    }

//    @Override
//    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
//        super.onActivityResult(requestCode, resultCode, data);
//
//        if (requestCode == OPEN_CAMERA) {
//
//            Bitmap photo = (Bitmap) data.getExtras().get("data");
//            saveToFirebaseStorage(photo);
////                bitmapUserImage = photo;
////                imageView.setImageBitmap(photo);
//
//
//        }
//    }
}

