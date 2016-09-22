package com.example.hash.bloodbank;

import android.support.annotation.NonNull;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;


public class LoginActivity extends AppCompatActivity implements View.OnClickListener{

    //Firebase Declarations
    private FirebaseAuth mAuth;
    private FirebaseAuth.AuthStateListener mAuthListener;

    // UI Controls

    Button emailSignIn , emailSignUp;
    EditText emailEditText,passwordEditText;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);
        //UI Initialization
        emailSignIn = (Button) findViewById(R.id.signInWithEmailButton);
        emailSignUp = (Button) findViewById(R.id.signUpWithEmailButton);
        emailEditText = (EditText) findViewById(R.id.emailAddressText);
        passwordEditText = (EditText) findViewById(R.id.passwordText);

        //UI Listeners
        emailSignIn.setOnClickListener(this);
        emailSignUp.setOnClickListener(this);

        // Firebase Auth Listener
        mAuth = FirebaseAuth.getInstance();
        mAuthListener = new FirebaseAuth.AuthStateListener() {
            @Override
            public void onAuthStateChanged(@NonNull FirebaseAuth firebaseAuth) {
                FirebaseUser user = firebaseAuth.getCurrentUser();
                if (user != null) {
                    // User is signed in
//                    Log.d(TAG, "onAuthStateChanged:signed_in:" + user.getUid());
                } else {
                    // User is signed out
//                    Log.d(TAG, "onAuthStateChanged:signed_out");
                }

            }
        };


    }


    @Override
    public void onStart() {
        super.onStart();
        mAuth.addAuthStateListener(mAuthListener);
    }

    @Override
    public void onStop() {
        super.onStop();
        if (mAuthListener != null) {
            mAuth.removeAuthStateListener(mAuthListener);
        }
    }

    @Override
    public void onClick(View v) {
        switch (v.getId())
        {
            case R.id.signInWithEmailButton:

                if(loginFieldsNotEmpty())
                {
                    signInUserWithEmail(emailEditText.getText().toString(),passwordEditText.getText().toString());
                }
                break;

            case R.id.signUpWithEmailButton:

                if(loginFieldsNotEmpty())
                {
                    registerNewUserToDatabase(emailEditText.getText().toString(),passwordEditText.getText().toString());
                }
                break;
        }

    }

    private boolean loginFieldsNotEmpty(){

        if(emailEditText.getText().toString().trim().equals("") || passwordEditText.getText().toString().trim().equals("")){
            if(emailEditText.getText().toString().trim().equals("") )
            {
                emailEditText.setError("Please enter Email Address");
            }
            if(passwordEditText.getText().toString().trim().equals("") )
            {
                passwordEditText.setError("Please enter Password");
            }

            return false;
        }
        else {
            return  true;
        }

    }

    private void registerNewUserToDatabase(String email, String password)
    {
        mAuth.createUserWithEmailAndPassword(email, password)
                .addOnCompleteListener(this, new OnCompleteListener<AuthResult>() {
                    @Override
                    public void onComplete(@NonNull Task<AuthResult> task) {
                        Log.d("1000", "createUserWithEmail:onComplete:" + task.isSuccessful());
                        // If sign in fails, display a message to the user. If sign in succeeds
                        // the auth state listener will be notified and logic to handle the
                        // signed in user can be handled in the listener.
                        if (!task.isSuccessful()) {
                            Toast.makeText(LoginActivity.this, "Authentication failed.",
                                    Toast.LENGTH_SHORT).show();
                        }


                    }
                });
    }

    private void signInUserWithEmail(String email, String password){
        mAuth.signInWithEmailAndPassword(email, password)
                .addOnCompleteListener(this, new OnCompleteListener<AuthResult>() {
                    @Override
                    public void onComplete(@NonNull Task<AuthResult> task) {
                        Log.d("1001", "signInWithEmail:onComplete:" + task.isSuccessful());

                        // If sign in fails, display a message to the user. If sign in succeeds
                        // the auth state listener will be notified and logic to handle the
                        // signed in user can be handled in the listener.
                        if (!task.isSuccessful()) {
                            Log.w("1001", "signInWithEmail", task.getException());
                            Toast.makeText(LoginActivity.this, "Authentication failed.",
                                    Toast.LENGTH_SHORT).show();
                        }

                        // ...
                    }
                });
    }

}
