package com.example.hash.bloodbank;

import android.support.annotation.NonNull;
import android.util.Log;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

/**
 * Created by hash on 9/27/16.
 */

public class FirebaseDbCom {


    public FirebaseDbCom() {

    }




    public void writeToDB(Object data)
    {
        FirebaseDatabase database = FirebaseDatabase.getInstance();
        DatabaseReference myRef = database.getReference();
        myRef.setValue(data);
    }

    public void writeToDBMessages(Object data)
    {
        DatabaseReference database = FirebaseDatabase.getInstance().getReference();
        DatabaseReference myRef = database.child("messages/");
        myRef.setValue(data);
    }

    public void writeToDBProfiles(Object data)
    {
        DatabaseReference database = FirebaseDatabase.getInstance().getReference();
        DatabaseReference myRef = database.child("profiles/");
        myRef.setValue(data);
    }

    public void writeToDbCustomPath(Object data , String path)
    {
        DatabaseReference database = FirebaseDatabase.getInstance().getReference();
        DatabaseReference myRef = database.child(path);
        myRef.setValue(data);
    }

    public void writeToDBUserCoords (Object data)
    {
        DatabaseReference database = FirebaseDatabase.getInstance().getReference();
        DatabaseReference myRef = database.child("userCoords/");
        myRef.setValue(data);
    }
}


