package com.android.charan.shareride;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.os.AsyncTask;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;

import com.android.charan.shareride.Util.EmailDispatcher;
import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.api.GoogleApiClient;
import com.google.android.gms.common.api.GoogleApiClient.OnConnectionFailedListener;
import android.support.v4.app.FragmentActivity;
import android.view.View;
import android.widget.Button;
import android.widget.DatePicker;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.TimePicker;
import android.widget.Toast;

import com.google.android.gms.common.api.Status;
import com.google.android.gms.location.places.Place;
import com.google.android.gms.location.places.Places;


import com.google.android.gms.location.places.ui.PlaceAutocompleteFragment;
import com.google.android.gms.location.places.ui.PlaceSelectionListener;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.List;

public  class CreateRide extends Activity{
    private GoogleApiClient mGoogleApiClient;

    private EditText S;
    private Button BtnCreateRide;
    private FirebaseDatabase mFirebaseDatabase;
    private DatabaseReference mMessagesDatabaseReference;
    private EditText RideName;
    private EditText RideSource;
    private EditText RideDestination;
    private String mUsername;
    private DatePicker dPicker;
    private int day,month,year;
    private TimePicker tPicker;
    TextView textViewCreateError;
    private DatabaseReference userref;
    private RideDetails rideDetails;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_create_ride);

        Bundle b = getIntent().getExtras();
        mUsername = b.getString("user");


        mFirebaseDatabase = FirebaseDatabase.getInstance();
        mMessagesDatabaseReference = mFirebaseDatabase.getReference().child("rides");
        userref = mFirebaseDatabase.getReference().child("users");

        BtnCreateRide = (Button) findViewById(R.id.btnCreateRide);
        RideName = (EditText) findViewById(R.id.textViewCreateRideRideName);
        RideSource = (EditText) findViewById(R.id.textViewCreateRideSource);
        RideDestination = (EditText) findViewById(R.id.textViewCreateRideDestination);
        dPicker = (DatePicker) findViewById(R.id.createRideDatePicker);


        day = dPicker.getDayOfMonth();
        month = dPicker.getMonth()+1;
        year = dPicker.getYear();

        int hour = ((TimePicker) findViewById(R.id.createRideTimePicker)).getCurrentHour();
        int minute = ((TimePicker) findViewById(R.id.createRideTimePicker)).getCurrentMinute();

        Calendar calendar = Calendar.getInstance();
        calendar.set(year, month, day, hour, minute);
        final Date startDate = calendar.getTime();

        BtnCreateRide.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                boolean valid = validateUserInputAndCallAsyncTask();
                if(valid) {

                     rideDetails = new RideDetails(mUsername, RideName.getText().toString(),
                            RideSource.getText().toString(),
                            RideDestination.getText().toString(), startDate.getTime());
                    //System.out.print(startDate.toString());

                       mMessagesDatabaseReference.push().setValue(rideDetails);
                    getUsersList();
                    moveToJoinRidesPage();

                }

            }


        });



    }

    private boolean validateUserInputAndCallAsyncTask() {

        boolean isValid = true;


        //Add validation code here
        String rideName = ((TextView) findViewById(R.id.textViewCreateRideRideName)).getText().toString().trim();
        String source = ((TextView) findViewById(R.id.textViewCreateRideSource)).getText().toString().trim();
        String destination = ((TextView) findViewById(R.id.textViewCreateRideDestination)).getText().toString().trim();

        //		if(rideName.equals(""))
        //			missing= missing + "Ride Name, ";
        //		if(source.equals(""))
        //			missing= missing + "Source, ";
        //		if(destination.equals(""))
        //			missing= missing + "Destination! ";
        //
        if(rideName.equals("") || source.equals("")||destination.equals("")){

            //Show the error message to user

            //			textViewCreateError.append("\nEnter "+missing+"\n");
            isValid = false;
            textViewCreateError.setVisibility(View.VISIBLE);
            Context context = getApplicationContext();
            CharSequence text = "Enter all fields!";
            Toast toast = Toast.makeText(context, text, Toast.LENGTH_SHORT);
            TextView v = (TextView) toast.getView().findViewById(android.R.id.message);
            /*v.setTextColor(getResources().getColor(R.color.red));*/
            toast.show();
        } else if(dateInPast()){
            isValid = false;
            textViewCreateError.setVisibility(View.VISIBLE);
            Context context = getApplicationContext();
            CharSequence text = "Start date and time is past!";
            Toast toast = Toast.makeText(context, text, Toast.LENGTH_SHORT);
            TextView v = (TextView) toast.getView().findViewById(android.R.id.message);
            /*v.setTextColor(getResources().getColor(R.color.red));*/
            toast.show();
        }

       /* if(isValid) {
            *//*progressBar.setVisibility(View.VISIBLE);*//**//*
            LinearLayout createRideForm = (LinearLayout) findViewById(R.id.createRideForm);
            createRideForm.setVisibility(View.INVISIBLE);

            new CreateRideTask().execute();*//*
        }*/
            return isValid;
    }

    private boolean dateInPast(){
        int day = ((DatePicker) findViewById(R.id.createRideDatePicker)).getDayOfMonth();
        int month = ((DatePicker) findViewById(R.id.createRideDatePicker)).getMonth();
        int year = ((DatePicker) findViewById(R.id.createRideDatePicker)).getYear();

        int hour = ((TimePicker) findViewById(R.id.createRideTimePicker)).getCurrentHour();
        int minute = ((TimePicker) findViewById(R.id.createRideTimePicker)).getCurrentMinute();

        Calendar calendar = Calendar.getInstance();
        System.out.println(day + " " + month + " " + year + " " + hour + " : " + minute);  //6 4 2013
        calendar.set(year, month, day, hour, minute);

        SimpleDateFormat formatter = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
        String formattedDate = formatter.format(calendar.getTime());
        System.out.println("entered:" + formattedDate);

        Calendar now = Calendar.getInstance();
        formattedDate = formatter.format(now.getTime());
        System.out.println("now:" + formattedDate);

        if(calendar.before(now)){
            return true;
        }else{
            return false;
        }
    }

    public void getUsersList(){


        userref.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {


                List<String> Userlist = new ArrayList<String>();


                // Result will be holded Here
                for (DataSnapshot dsp : dataSnapshot.getChildren()) {
                    Userlist.add(dsp.getValue(User.class).getEmail()); //add result into array list


                }
                new SendEmailTask(rideDetails,Userlist).execute();


            }
            @Override
            public void onCancelled(DatabaseError databaseError) {
                // Getting Post failed, log a message
                // Log.w(TAG, "loadPost:onCancelled", databaseError.toException());
                // ...
            }
        });

    }
    private class SendEmailTask extends AsyncTask<Void, Void, Void> {
        Exception error;
        RideDetails r;
        List<String> list;


        SendEmailTask(RideDetails ride,List<String> list){
            r = ride;
            this.list = list;
        }

        protected Void doInBackground(Void... params) {

            //TODO: get from the webservice
            new EmailDispatcher().sendEmailToAll(list, r);
            return null;
            //send email

        }

        protected void onPostExecute(RideDetails result) {
        }
    }

    private void moveToJoinRidesPage() {

       // progressBar.setVisibility(View.INVISIBLE);

        LinearLayout createRideForm = (LinearLayout) findViewById(R.id.createRideForm);
        createRideForm.setVisibility(View.VISIBLE);

        //clear the text boxes
        TextView textViewRideName = (TextView) findViewById(R.id.textViewCreateRideRideName);
        textViewRideName.setText("");

        TextView textViewSource = (TextView) findViewById(R.id.textViewCreateRideSource);
        textViewSource.setText("");

        TextView textViewDestination = (TextView) findViewById(R.id.textViewCreateRideDestination);
        textViewDestination.setText("");

        //Date picker and time picker have to be reset

        Intent joinRidesIntent = new Intent(CreateRide.this, JoinRidesActivity.class);
        CreateRide.this.startActivity(joinRidesIntent);
    }











}
//public  class CreateRide extends FragmentActivity
//        implements OnConnectionFailedListener {
//    private GoogleApiClient mGoogleApiClient;
//    private EditText S;
//
//    @Override
//    protected void onCreate(Bundle savedInstanceState) {
//        super.onCreate(savedInstanceState);
//        setContentView(R.layout.activity_create_ride);
//        S = (EditText)findViewById(R.id.textViewCreateRideSource);
//        GoogleApiClient mGoogleApiClient = new GoogleApiClient
//                .Builder(this)
//                .addApi(Places.GEO_DATA_API)
//                .addApi(Places.PLACE_DETECTION_API)
//                .enableAutoManage(this, this)
//                .build();
//
//
//        PlaceAutocompleteFragment autocompleteFragment = (PlaceAutocompleteFragment)
//                getFragmentManager().findFragmentById(R.id.place_autocomplete_fragment);
//
//        autocompleteFragment.setOnPlaceSelectedListener(new PlaceSelectionListener() {
//            @Override
//            public void onPlaceSelected(Place place) {
//                // TODO: Get info about the selected place.
//                //Log.i(TAG, "Place: " + place.getName());
//                String placeDetailsStr = place.getName() + "\n"
//                        + place.getId() + "\n"
//                        + place.getLatLng().toString() + "\n"
//                        + place.getAddress() + "\n"
//                        + place.getAttributions();
//                S.setText(placeDetailsStr);
//            }
//
//            @Override
//            public void onError(Status status) {
//
//            }
//
//
//            /*@Override
//            public void onError(AsyncTask.Status status) {
//                // TODO: Handle the error.
//               // Log.i(TAG, "An error occurred: " + status);
//            }*/
//        });
//    }
//    @Override
//    public void onConnectionFailed(ConnectionResult result) {
//        // An unresolvable error has occurred and a connection to Google APIs
//        // could not be established. Display an error message, or handle
//        // the failure silently
//
//        // ...
//    }
//    }
//}
