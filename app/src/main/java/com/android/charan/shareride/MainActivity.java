package com.android.charan.shareride;

import android.content.Intent;
import android.support.annotation.NonNull;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.Toast;

import com.firebase.ui.auth.AuthUI;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.UUID;

public class MainActivity extends AppCompatActivity {
    private static final String TAG = "MainActivity";
    private Button mCreateRide;
    private Button mShowMyRide;
    private FirebaseAuth mFirebaseAuth;
    private FirebaseUser mFireBaseUser;
    private FirebaseAuth.AuthStateListener mAuthStateListener;
    private FirebaseDatabase mFirebaseDatabase;
    private DatabaseReference mDatabaseReference,mTest;
    private String mUsername;
    private String mEmail;
    private String  uuid;
    public static final int  RC_SIGN_IN = 1;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        Log.d(TAG, "inside oncreate");
        //Firebase Instances
        mCreateRide = (Button)findViewById(R.id.CreateRide);
        mShowMyRide = (Button)findViewById(R.id.ShowMyRide);

        mFirebaseAuth = FirebaseAuth.getInstance();
        mFirebaseDatabase = FirebaseDatabase.getInstance();
        mDatabaseReference = mFirebaseDatabase.getReference().child("users");



        mAuthStateListener = new FirebaseAuth.AuthStateListener() {
            @Override
            public void onAuthStateChanged(@NonNull FirebaseAuth firebaseAuth) {
                Log.d(TAG, "inside auth listener");
                mFireBaseUser = mFirebaseAuth.getCurrentUser();
                if (mFireBaseUser != null) {

                    mUsername = mFireBaseUser.getDisplayName();
                    mEmail = mFireBaseUser.getEmail();


                } else {
                    //onSignedOutCleanUp();
                    startActivityForResult(
                            AuthUI.getInstance()
                                    .createSignInIntentBuilder()
                                    .setIsSmartLockEnabled(false)
                                    .setProviders(Arrays.asList(new AuthUI.IdpConfig.Builder(AuthUI.EMAIL_PROVIDER).build(),
                                            new AuthUI.IdpConfig.Builder(AuthUI.GOOGLE_PROVIDER).build(),
                                            new AuthUI.IdpConfig.Builder(AuthUI.FACEBOOK_PROVIDER).build()))
                                    .build(),
                            RC_SIGN_IN);
                }
            }
        };

        mCreateRide.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent i = new Intent(MainActivity.this,CreateRide.class);
                Bundle b = new Bundle();
                b.putString("user",mFireBaseUser.getEmail());
                i.putExtras(b);
                startActivity(i);
            }
        });
    }
    @Override
    protected void onPause(){
        super.onPause();
        if(mAuthStateListener != null)
            mFirebaseAuth.removeAuthStateListener(mAuthStateListener);

    }
    protected void onResume(){
        super.onResume();
        mFirebaseAuth.addAuthStateListener(mAuthStateListener);


    }

    @Override
    public  void onActivityResult(int requestCode, int resultCode, Intent data){

        super.onActivityResult(requestCode, resultCode, data);

        if(RC_SIGN_IN == requestCode){
            if (resultCode == RESULT_OK){

                mFireBaseUser = mFirebaseAuth.getCurrentUser();
                System.out.println(mFireBaseUser);
                uuid = mFireBaseUser.getUid();

                mDatabaseReference.child(uuid).addListenerForSingleValueEvent(
                        new ValueEventListener() {

                            @Override
                            public void onDataChange(DataSnapshot dataSnapshot) {

                                // Get user value
                                User user = dataSnapshot.getValue(User.class);

                                if (user == null){
                                    User user1 = new User(mFireBaseUser.getDisplayName(), mFireBaseUser.getEmail());
                                    mDatabaseReference.child(uuid).setValue(user1);
                                }
                                //user.email now has your email value
                                else {
                                    System.out.print(user.getEmail());
                                }

                            }
                            @Override
                            public void onCancelled(DatabaseError databaseError) {
                                // Getting Post failed, log a message
                                Log.w(TAG, "loadPost:onCancelled", databaseError.toException());
                                // ...
                            }
                        });

                Toast.makeText(MainActivity.this, "SignedIn",Toast.LENGTH_SHORT).show();
            }
            else if (resultCode == RESULT_CANCELED){
                Toast.makeText(MainActivity.this, "Signed in cancelled",Toast.LENGTH_SHORT).show();
                finish();
            }
        }

    }
    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        MenuInflater inflater = getMenuInflater();
        inflater.inflate(R.menu.main_menu, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case R.id.sign_out_menu:
                AuthUI.getInstance().signOut(this);
                return true;
            default:
                return super.onOptionsItemSelected(item);

        }

    }
}
