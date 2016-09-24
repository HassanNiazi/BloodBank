package com.example.hash.bloodbank;
import android.*;
import android.content.DialogInterface;
import android.content.pm.PackageManager;
import android.database.Cursor;
import android.content.Intent;
import android.net.Uri;
import android.provider.MediaStore;
import android.support.v4.app.ActivityCompat;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.graphics.Bitmap;
import android.widget.Toast;
import android.Manifest;
import android.Manifest.permission;


public class SignUp extends AppCompatActivity implements View.OnClickListener{
    private static final int SELECT_PICTURE = 100;
    private static final int OPEN_CAMERA = 120;
    private static final int MY_PERMISSIONS_REQUEST_CAMERA = 140;
    String path="";
    RoundedImageView imageView;
    EditText userName;
    String[] imageSource = {"Gallery","Camera"};
    String phoneNumber;
    Button uploadPhoto,done;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_sign_up);
        imageView = (RoundedImageView) findViewById(R.id.roundedImageView);
        userName = (EditText) findViewById(R.id.userName);
        Intent intent = getIntent();
        phoneNumber = intent.getStringExtra(getResources().getString(R.string.phoneNo));
        uploadPhoto = (Button) findViewById(R.id.uploadPhoto);
        done = (Button) findViewById(R.id.done_sign_up);
        uploadPhoto.setOnClickListener(this);
        done.setOnClickListener(this);

    }


    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        if (resultCode == RESULT_OK) {
            if (requestCode == SELECT_PICTURE) {
                // Get the url from data
                Uri selectedImageUri = data.getData();
                if (null != selectedImageUri) {
                    // Get the path from the Uri
                    String path = getPathFromURI(selectedImageUri);
                    Log.i("ImageLoaded", "Image Path : " + path);
                    // Set the image in ImageView
                    imageView.setImageURI(selectedImageUri);
                }
            }
            else if (requestCode == OPEN_CAMERA){

                Bitmap photo = (Bitmap) data.getExtras().get("data");
                imageView.setImageBitmap(photo);

            }
        }
    }

    public String getPathFromURI(Uri contentUri) {
        String res = null;
        String[] proj = {MediaStore.Images.Media.DATA};
        Cursor cursor = getContentResolver().query(contentUri, proj, null, null, null);
        if (cursor.moveToFirst()) {
            int column_index = cursor.getColumnIndexOrThrow(MediaStore.Images.Media.DATA);
            res = cursor.getString(column_index);
        }
        cursor.close();
        return res;
    }

    void openImageChooser() {
        Intent intent = new Intent();
        intent.setType("image/*");
        intent.setAction(Intent.ACTION_GET_CONTENT);
        startActivityForResult(Intent.createChooser(intent, "Select Picture"), SELECT_PICTURE);
    }

    public void takePhoto()
    {

        Intent cameraIntent = new Intent(android.provider.MediaStore.ACTION_IMAGE_CAPTURE);
        startActivityForResult(cameraIntent, OPEN_CAMERA);

    }


    @Override
    public void onClick(View v) {
        switch (v.getId())
        {
            case R.id.uploadPhoto:
//                Toast.makeText(this, "Clicked", Toast.LENGTH_SHORT).show();
                AlertDialog.Builder builder = new AlertDialog.Builder(this);
                builder.setTitle("Choose Image Source")
                        .setItems(imageSource, new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialog, int which) {

                                switch (which){
                                    case 0:
                                        openImageChooser();
                                        break;
                                    case 1:


                                        if (ContextCompat.checkSelfPermission(SignUp.this,
                                                permission.CAMERA)
                                                != PackageManager.PERMISSION_GRANTED) {

                                            // Should we show an explanation?
                                            if (ActivityCompat.shouldShowRequestPermissionRationale(SignUp.this,
                                                    Manifest.permission.CAMERA)) {

                                                // Show an expanation to the user *asynchronously* -- don't block
                                                // this thread waiting for the user's response! After the user
                                                // sees the explanation, try again to request the permission.

                                            } else {

                                                // No explanation needed, we can request the permission.

                                                ActivityCompat.requestPermissions(SignUp.this,
                                                        new String[]{Manifest.permission.CAMERA},
                                                        MY_PERMISSIONS_REQUEST_CAMERA);

                                                // MY_PERMISSIONS_REQUEST_READ_CONTACTS is an
                                                // app-defined int constant. The callback method gets the
                                                // result of the request.
                                            }
                                        }

                                        if (ContextCompat.checkSelfPermission(SignUp.this,
                                                permission.CAMERA)
                                                == PackageManager.PERMISSION_GRANTED) {
                                            takePhoto();
                                        }
                                        else {
                                            Toast.makeText(SignUp.this, "You dont have Permission to take Pictures", Toast.LENGTH_SHORT).show();
                                        }


                                        break;
                                }

                            }
                        });
                builder.show();

                break;
            case R.id.done_sign_up:
                if(userName.getText().toString().trim().equals(""))
                {
                    userName.setError( "Name is required!" );
                }
                else
                {
                    // Mission is a Go
                }
                break;
        }
    }

}
