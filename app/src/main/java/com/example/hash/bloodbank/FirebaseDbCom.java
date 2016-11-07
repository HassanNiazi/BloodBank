package com.example.hash.bloodbank;

import android.support.annotation.NonNull;
import android.util.Log;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.List;

/**
 * Hassan Niazi - Blood Bank Application Test Project 2
 * Created by hash on 9/27/16.
 */

public class FirebaseDbCom {


    User user;

    public FirebaseDbCom() {

    }

    public void writeToDB(Object data) {
        FirebaseDatabase database = FirebaseDatabase.getInstance();
        DatabaseReference myRef = database.getReference();
        myRef.setValue(data);
    }

    public void writeToDBMessages(Object data) {
        DatabaseReference database = FirebaseDatabase.getInstance().getReference();
        DatabaseReference myRef = database.child("messages/");
        myRef.setValue(data);
    }

    public void writeToDBProfiles(Object data, String phoneNo) {
        DatabaseReference database = FirebaseDatabase.getInstance().getReference();
        DatabaseReference myRef = database.child("profiles/" + phoneNo);
        myRef.setValue(data);
    }

    public void writeToDbCustomPath(Object data, String path) {
        DatabaseReference database = FirebaseDatabase.getInstance().getReference();
        DatabaseReference myRef = database.child(path);
        myRef.setValue(data);
    }

    public void writeToDBUserCoords(String city,String phoneNo,Object data) {
        DatabaseReference database = FirebaseDatabase.getInstance().getReference();
        DatabaseReference myRef = database.child("userCoords").child(city).child(phoneNo);
        myRef.setValue(data);
    }

    public List<UserCoordinateClass> readFromDBUserCoords(String city) {
        final List<UserCoordinateClass> userCoordinateClassList = null;
        DatabaseReference databaseReference = FirebaseDatabase.getInstance().getReference().child("userCoords");
        databaseReference.child(city).addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {

                for(DataSnapshot snapshot:  dataSnapshot.getChildren())
                {
                   UserCoordinateClass coordinateClassObject = snapshot.getValue(UserCoordinateClass.class);
                   userCoordinateClassList.add(coordinateClassObject);
                }
            }
            @Override
            public void onCancelled(DatabaseError databaseError) {

            }
        });
        return userCoordinateClassList;
    }

    public User readFromDBUserProfile(String phoneNo) {

        DatabaseReference database = FirebaseDatabase.getInstance().getReference();
        DatabaseReference myRef = database.child("profiles");
//        myRef.setValue(data);
        myRef.child(phoneNo).addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                user = dataSnapshot.getValue(User.class);
                if (user == null) {
                    Log.d("readFromDBUserProfile", "user = null :(");
                }
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {

            }

        });
        return user;
    }


}


