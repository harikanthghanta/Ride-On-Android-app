package com.android.charan.shareride;

import java.util.ArrayList;
import java.util.List;

import com.android.charan.shareride.R;
import com.android.charan.shareride.customadapters.UserCustomAdapter;
import com.android.charan.shareride.entities.Participant;
import com.android.charan.shareride.tabpanel.MenuConstants;
import com.android.charan.shareride.tabpanel.MyTabHostProvider;
import com.android.charan.shareride.tabpanel.TabView;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import android.os.AsyncTask;
import android.os.Bundle;
import android.view.View;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.TextView;

public class ViewParticipantsForARideActivity extends BaseActivity {


	private FirebaseDatabase mFirebaseDatabase;
	private DatabaseReference userref, maddId;
	private FirebaseAuth mFirebaseAuth;

	List<Participant> participants = new ArrayList<Participant>();
	List<String> users = new ArrayList<String>();
	private LinearLayout progressBar;
	RideDetails ride;

	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);

		mFirebaseAuth = FirebaseAuth.getInstance();
		mFirebaseDatabase = FirebaseDatabase.getInstance();
		maddId = mFirebaseDatabase.getReference().child("RideMap");
		userref = mFirebaseDatabase.getReference().child("users");
		//Draw menu
		tabProvider = new MyTabHostProvider(ViewParticipantsForARideActivity.this);
		
		String caller = getIntent().getStringExtra("Caller");
		
		TabView tabView;
		if(caller.equalsIgnoreCase("JoinRide"))
			tabView = tabProvider.getTabHost(MenuConstants.JOIN_RIDES);
		else 
			tabView = tabProvider.getTabHost(MenuConstants.PAST_RIDES);
		tabView.setCurrentView(R.layout.view_participants_for_a_ride_activity);
		setContentView(tabView.render());			

		progressBar = (LinearLayout) findViewById(R.id.viewParticipantsForARideSpinner);
		progressBar.setVisibility(View.VISIBLE);

		new ViewParticipantsForARideTask().execute();		
	}
	
	@Override
	protected void setTitle() {
		
		ride = (RideDetails) getIntent().getSerializableExtra("Ride");

		final TextView myTitleText = (TextView)findViewById(R.id.myTitle);
		myTitleText.setText("Participants for Ride");
		Participant p1 =new Participant("teja.charan20@gmail.com","-Kk9b5Bqp4TDk8xJnY_Y","Charan Teja");
		Participant p2 =new Participant("charan.lellaboyena@stonybrook.edu","-Kk9b5Bqp4TDk8xJnY_Y","Charan Teja");
		participants.add(p1);
		participants.add(p2);
	}	

	private void displayListView() {

		progressBar.setVisibility(View.INVISIBLE);
		
//		TextView textViewRideName = (TextView) findViewById(R.id.textViewViewParticipantsDetailsRideName);
//		textViewRideName.setText(ride.getName());

		//create an ArrayAdaptar from the String Array
		UserCustomAdapter dataAdapter = new UserCustomAdapter(this, R.id.userTextVal, users);
		final ListView listView = (ListView) findViewById(R.id.viewParticipantsForARideListView);

		// Assign adapter to ListView
		listView.setAdapter(dataAdapter);

		//enables filtering for the contents of the given ListView
		listView.setTextFilterEnabled(true);
	}

	public class ViewParticipantsForARideTask extends AsyncTask<Void,Void,List<Participant>> {
		Exception error;

		protected List<Participant> doInBackground(Void... params) {
			maddId.addValueEventListener(new ValueEventListener() {
				@Override
				public void onDataChange(DataSnapshot dataSnapshot) {
					final Participant p = new Participant();
					for (DataSnapshot messageSnapshot: dataSnapshot.getChildren()) {
						String rideId = (String) messageSnapshot.child("rideId").getValue();
						if(rideId ==ride.getId()){
							String usr = (String) messageSnapshot.child("userId").getValue();
							p.setRideId(rideId);
							userref.addListenerForSingleValueEvent(new ValueEventListener() {
								@Override
								public void onDataChange(DataSnapshot dataSnapshot) {

									// Result will be holded Here
									for (DataSnapshot dsp : dataSnapshot.getChildren()) {
									p.setId((String) dsp.child("email").getValue());
									p.setUserName((String) dsp.child("username").getValue());
									}

								}
								@Override
								public void onCancelled(DatabaseError databaseError) {
									// Getting Post failed, log a message
									// Log.w(TAG, "loadPost:onCancelled", databaseError.toException());
									// ...
								}
							});
						}
						participants.add(p);

					}
				}

				@Override
						public void onCancelled(DatabaseError databaseError) {
							// Getting Post failed, log a message
							//Log.w(TAG, "loadPost:onCancelled", databaseError.toException());
							// ...
						}
					});

			return participants;

			//return RidesManager.viewParticipantsForRide(ride.getId());
		}

		protected void onPostExecute(List<Participant> result) {
			if(error != null){

			} else{
				//List<Participant> participants = result;
				System.out.print(participants.size());
				for(Participant p:participants){
					users.add(p.getUserName());
				}
				users.add("Hari Kanth");
				users.add("Bob");
				users.add("Eve");
			}
			displayListView();
		}
	}
}