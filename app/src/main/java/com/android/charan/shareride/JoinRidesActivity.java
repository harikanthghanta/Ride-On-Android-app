package com.android.charan.shareride;

//import java.awt.Image;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

//import javax.swing.text	.Position;

import com.android.charan.shareride.tabpanel.MenuConstants;
import com.android.charan.shareride.tabpanel.MyTabHostProvider;
import com.android.charan.shareride.tabpanel.TabView;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;


import android.app.Activity;
import android.content.Context;
import android.graphics.Typeface;
import android.os.AsyncTask;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.CheckBox;
import android.widget.CompoundButton;
import android.widget.ImageButton;
import android.widget.CompoundButton.OnCheckedChangeListener;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;
import android.widget.AdapterView.OnItemClickListener;

public class JoinRidesActivity extends BaseActivity {

	private FirebaseDatabase mFirebaseDatabase;
	private DatabaseReference mDatabaseReference, maddId;
	private FirebaseAuth mFirebaseAuth;
	private String currentUserid;

// commented on request
	List<RideDetails> rideList = new ArrayList<RideDetails>();
	ArrayList<RideDetailsMap> rideEntry = new ArrayList<RideDetailsMap>();
	private LinearLayout progressBar;
	RideDetails selectedRideSave;
	int selectedRidePosition;
	public List<String> myJoinedRidesList = new ArrayList<String>();

	@Override
	public void onCreate(Bundle savedInstanceState) {
	
		super.onCreate(savedInstanceState);
		//Draw menu
		tabProvider = new MyTabHostProvider(JoinRidesActivity.this);
		TabView tabView = tabProvider.getTabHost(MenuConstants.JOIN_RIDES);
		tabView.setCurrentView(R.layout.join_rides_activity);
		setContentView(tabView.render());


		mFirebaseAuth = FirebaseAuth.getInstance();
		currentUserid = mFirebaseAuth.getCurrentUser().getUid();
		mFirebaseDatabase = FirebaseDatabase.getInstance();
		mDatabaseReference = mFirebaseDatabase.getReference().child("rides");
		maddId = mFirebaseDatabase.getReference().child("RideMap");
		progressBar = (LinearLayout) findViewById(R.id.Spinner);
		progressBar.setVisibility(View.INVISIBLE);

		new GetUpcomingRidesTask().execute();
		//GetUpcomingRidesTask();
		//TestTask();
	//	displayListView();

	}

	private void displayListView() {
		//System.out.println("inside list view");

		//progressBar.setVisibility(View.INVISIBLE);

		ListView listView1 = (ListView) findViewById(R.id.listView1);
		listView1.setVisibility(View.VISIBLE);

		//sort by date
		List<RideDetailsMap> tempJoined = new ArrayList<RideDetailsMap>();
		List<RideDetailsMap> tempNotJoined = new ArrayList<RideDetailsMap>();
		for(RideDetailsMap ce:rideEntry){
			if(ce.isJoin()){
				tempJoined.add(ce);
			}else{
				tempNotJoined.add(ce);
			}
		}

		Collections.sort(tempJoined);
		Collections.sort(tempNotJoined);

		rideEntry.clear();
		rideEntry.addAll(tempJoined);
		rideEntry.addAll(tempNotJoined);

		List<RideDetails> temp = new ArrayList<RideDetails>();
		for(RideDetailsMap r:rideEntry){
			for(RideDetails r1:rideList){
				if(r1.getId().equals(r.getRideDetails().getId())){
					temp.add(r1);
				}
			}
		}
		rideList.clear();
		rideList.addAll(temp);
		//create an ArrayAdaptar from the String Array
		CustomAdapter dataAdapter = new CustomAdapter(this, R.id.textVal,  rideEntry);
		final ListView listView = (ListView) findViewById(R.id.listView1);

		// Assign adapter to ListView
		listView.setAdapter(dataAdapter);

		//  listView.setLayoutParams(new ListView.LayoutParams(ListView.LayoutParams.FILL_PARENT, ListView.LayoutParams.WRAP_CONTENT));

		//enables filtering for the contents of the given ListView
		listView.setTextFilterEnabled(true);
		//listView.setItemsCanFocus(false);
		listView.setOnItemClickListener(new OnItemClickListener() {
			@Override
			public void onItemClick(AdapterView<?> parent, View view,
					int position, long id) {


//				Toast.makeText(getApplicationContext(), " Clicked " , Toast.LENGTH_SHORT).show();

				RideDetails selectedRide = null;
				String Caller = "JoinRide";
				int positionView = listView.getPositionForView(view);
				if (positionView != ListView.INVALID_POSITION) {
					//start view activity
					selectedRide = rideList.get(positionView);
					/*Intent i = new Intent(getApplicationContext(), ViewRideDetailsActivity.class);
					i.putExtra("Caller", Caller);
					i.putExtra("Ride", selectedRide);

					startActivity(i);*/
				}else{
					Toast toast = Toast.makeText(getApplicationContext(), "An error occured.", Toast.LENGTH_SHORT);
					TextView v = (TextView) toast.getView().findViewById(android.R.id.message);
					v.setTextColor(getResources().getColor(R.color.red));
					toast.show();
				}
			}
		});
	}

	public class CustomAdapter extends ArrayAdapter<RideDetailsMap> {
		private ArrayList<RideDetailsMap> entries;
		private Activity activity;

		public CustomAdapter(Activity a, int textViewResourceId, ArrayList<RideDetailsMap> entries) {
			super(a, textViewResourceId, entries);
			this.entries = entries;
			this.activity = a;
			//System.out.println("in constructor"+entries.size());
		}

		public class ViewHolder{
			public TextView item1;
			public CheckBox item2;
			public TextView textFieldDateTime;
			public ImageButton start;
		}

		@Override
		public View getView(int position, View convertView, ViewGroup parent) {
			View v = convertView;
			ViewHolder holder;
			//			if (v == null) {
			LayoutInflater vi =
					(LayoutInflater)activity.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
			v = vi.inflate(R.layout.join_rides_list, null);
			holder = new ViewHolder();
			holder.item1 = (TextView) v.findViewById(R.id.textVal);
			holder.textFieldDateTime = (TextView) v.findViewById(R.id.textValDateTime);
			holder.item2 = (CheckBox) v.findViewById(R.id.isJoined);

			holder.start = (ImageButton) v.findViewById(R.id.img_start);
			final RideDetailsMap custom = entries.get(position);
			if (custom != null) {
				/*Date rideDate = Utils.convertStringToDate(custom.getStartDate());

				String[] formats = new String[] {"dd-MMM-yy", "HH:mm"};
				SimpleDateFormat dfForRideDate = new SimpleDateFormat(formats[0], Locale.US);
				SimpleDateFormat dfForRideTime = new SimpleDateFormat(formats[1], Locale.US);

*/
				holder.item1.setText(custom.getRideDetails().getRideName());
				/*holder.textFieldDateTime.setText(dfForRideDate.format(rideDate) + "   " + dfForRideTime.format(rideDate));*/
				holder.textFieldDateTime.setText(String.valueOf(custom.getRideDetails().getDate()));
				holder.item2.setChecked(custom.isJoin());
				if(custom.isJoin()){
					holder.item1.setTypeface(null, Typeface.BOLD);
					holder.start.setEnabled(true);
					holder.start.setImageResource(R.drawable.button_start);
				}else{
					holder.start.setEnabled(false);
					holder.start.setImageResource(R.drawable.button_start_disabled);
				}
			}
			/*holder.start.setOnClickListener(mStartButtonClickListener);*/
			holder.item2.setOnCheckedChangeListener(mStarCheckedChangeListener);
			//			}
			//			else
			holder=(ViewHolder)v.getTag();
			v.setTag(holder);
			return v;
		}

//		private OnClickListener mStartButtonClickListener = new OnClickListener() {
//			@Override
//			public void onClick(View v) {
//				Date currentTimestamp = new Date();
//				Calendar cal = Calendar.getInstance();
//				cal.setTime(currentTimestamp);
//				cal.add(Calendar.HOUR, 1);
//				currentTimestamp = cal.getTime();
//
//				long currTime = Utils.convertDateToString(currentTimestamp);
//				long rideTime;
//
//				final ListView listView = (ListView) findViewById(R.id.listView1);
//				final int position = listView.getPositionForView(v);
//				if (position != ListView.INVALID_POSITION) {
//					rideTime = rideList.get(position).getStartTime();
//					if(currTime < rideTime )
//					{
//						earlyRide();
//					}
//					else
//					{
//						Intent i = new Intent(getApplicationContext(), RecordRideStatsActivity.class);
//						i.putExtra("Ride", rideList.get(position));
//						startActivity(i);
//					}
//
//				}
//			}
//		};
//
		private OnCheckedChangeListener mStarCheckedChangeListener = new OnCheckedChangeListener() {
			@Override
			public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
				final ListView listView = (ListView) findViewById(R.id.listView1);
				final int position = listView.getPositionForView(buttonView);
				if (position != ListView.INVALID_POSITION) {
					//todo:update mStarStates[position] = isChecked;
					RideDetails r = rideList.get(position);
					selectedRideSave = r;
					selectedRidePosition = position;
					/*progressBar = (LinearLayout) findViewById(R.id.Spinner);
					progressBar.setVisibility(View.VISIBLE);*/

					/*listView.setVisibility(View.INVISIBLE);*/
					if(isChecked){
						new JoinRideTask().execute();
					}else{
						//new UnJoinRideTask().execute();
					}
				}
			}
		};
	}

	@Override
	public void setTitle() {
		final TextView myTitleText = (TextView)findViewById(R.id.myTitle);
		myTitleText.setText("Join Ride");
	}
	public void  GetUpcomingRidesTask(){
		mDatabaseReference.addListenerForSingleValueEvent(
				new ValueEventListener() {

					@Override
					public void onDataChange(DataSnapshot dataSnapshot) {

						// Get user value

						for (DataSnapshot postSnapshot: dataSnapshot.getChildren()) {
							// TODO: handle the post
							RideDetails rides = postSnapshot.getValue(RideDetails.class);
							rideList.add(rides);

						}


					}
					@Override
					public void onCancelled(DatabaseError databaseError) {
						// Getting Post failed, log a message
						//Log.w(TAG, "loadPost:onCancelled", databaseError.toException());
						// ...
					}
				});
		new GetJoinedRidesForUser().execute();


	}
	public void TestTask(){
		mDatabaseReference.orderByChild("rideName").equalTo("Did").addListenerForSingleValueEvent(
				new ValueEventListener() {

					@Override
					public void onDataChange(DataSnapshot dataSnapshot) {

						// Get user value

						for (DataSnapshot postSnapshot: dataSnapshot.getChildren()) {
							// TODO: handle the post
							RideDetails rides = postSnapshot.getValue(RideDetails.class);
							//System.out.println(rides.getDestination());

						}



					}
					@Override
					public void onCancelled(DatabaseError databaseError) {
						// Getting Post failed, log a message
						//Log.w(TAG, "loadPost:onCancelled", databaseError.toException());
						// ...
					}
				});

	}


	//AsynTask for getting the list of rides
	public class GetUpcomingRidesTask extends AsyncTask<Void,Void,List<RideDetails>> {
		Exception error;

		protected List<RideDetails> doInBackground(Void... params) {
			//final List<RideDetails> ridesList = new ArrayList<RideDetails>();
			mDatabaseReference.addListenerForSingleValueEvent(
					new ValueEventListener() {

						@Override
						public void onDataChange(DataSnapshot dataSnapshot) {

							// Get user value

							for (DataSnapshot postSnapshot: dataSnapshot.getChildren()) {
								// TODO: handle the post
								RideDetails rides = postSnapshot.getValue(RideDetails.class);
								rideList.add(rides);

							}


						}
						@Override
						public void onCancelled(DatabaseError databaseError) {
							// Getting Post failed, log a message
							//Log.w(TAG, "loadPost:onCancelled", databaseError.toException());
							// ...
						}
					});
			return rideList;
		}

		protected void onPostExecute(List<RideDetails> result) {
			//System.out.println(result.size());
			if(error != null){
				//System.out.println("if");
			} else {
				rideList = result;
				new GetJoinedRidesForUser().execute();
			}
		}
	}

	//AsynTask for getting the list of rides
	public class GetJoinedRidesForUser extends AsyncTask<Void,Void,List<String>> {
		Exception error;
		List<String> myJoinedRidesList1 = new ArrayList<String>();
		protected List<String> doInBackground(Void... params) {
			//myJoinedRidesList = new ArrayList<String>();
			//System.out.println("inside getjoinedrides");
			maddId.orderByChild("userId").equalTo(currentUserid).addListenerForSingleValueEvent(
					new ValueEventListener() {

						@Override
						public void onDataChange(DataSnapshot dataSnapshot) {

							// Get user value

							for (DataSnapshot postSnapshot: dataSnapshot.getChildren()) {
								// TODO: handle the post
								RideMap jointRides = postSnapshot.getValue(RideMap.class);
								myJoinedRidesList1.add(jointRides.getRideId());
								//System.out.println(jointRides.getRideId());

							}



						}
						@Override
						public void onCancelled(DatabaseError databaseError) {
							// Getting Post failed, log a message
							//Log.w(TAG, "loadPost:onCancelled", databaseError.toException());
							// ...
						}
					});

			return myJoinedRidesList1;
		}

		protected void onPostExecute(List<String> result) {
			myJoinedRidesList = result;
			System.out.println(myJoinedRidesList.size());
			List<RideDetailsMap> temp = new ArrayList<RideDetailsMap>();
			for(RideDetails r:rideList){

				if(myJoinedRidesList.contains(r.getId())){
					temp.add(new RideDetailsMap(r,true));
				}else{
					temp.add(new RideDetailsMap(r,false));
				}
			}
			for(RideDetailsMap r:temp){
				if(r.isJoin()){
					rideEntry.add(r);
				}
			}
			for(RideDetailsMap r:temp){
				if(!r.isJoin()){
					rideEntry.add(r);
				}
			}


			displayListView();
		}
	}
//	protected void earlyRide() {
//		AlertDialog alertDialog = new AlertDialog.Builder(this).create();
//		alertDialog.setMessage("You are early! Scheduled ride is yet to start!");
//		alertDialog.setButton( Dialog.BUTTON_NEGATIVE, "Ok", new DialogInterface.OnClickListener()    {
//			public void onClick(DialogInterface dialog, int which) {
//				dialog.cancel();
//			}});
//		alertDialog.show();
//	}
	//AsynTask for joining the rides
	public class JoinRideTask extends AsyncTask<Void,Void,String> {
		Exception error;
		protected String doInBackground(Void... params) {
			//maddedUsers.push().setValue()
			RideMap rideMap = new RideMap();
			rideMap.setRideId(selectedRideSave.getId());
			rideMap.setUserId(currentUserid);
			maddId.push().setValue(rideMap);


			return  "Success";
		}


		protected void onPostExecute(String result) {
			if(result.equalsIgnoreCase("Success")){
				Toast.makeText(getApplicationContext(), "Joined the ride successfully." , Toast.LENGTH_SHORT).show();
				//					selectedStar.setSelected(true);
			}else{
				Toast toast = Toast.makeText(getApplicationContext(), "An error occured.", Toast.LENGTH_SHORT);
				TextView v = (TextView) toast.getView().findViewById(android.R.id.message);
				v.setTextColor(getResources().getColor(R.color.red));
				toast.show();
			}
			//change order in rideEntry
			/*ArrayList<RideDetailsMap> temp = new ArrayList<RideDetailsMap>();
			temp.add(rideEntry.get(selectedRidePosition));
			temp.get(0).setJoined(true);
			for(RideDetailsMap r:rideEntry){
				if(!selectedRideSave.getId().equals(r.getRideid())){
					temp.add(r);
				}
			}
			rideEntry = temp;
			new GetLoggedinUserTask(true).execute();
			displayListView();*/
		}
	}
//
//	private class GetLoggedinUserTask extends AsyncTask<Void, Void, User> {
//		Exception error;
//		boolean isJoin;
//		GetLoggedinUserTask(boolean isJoin){
//			this.isJoin = isJoin;
//		}
//
//		@Override
//		protected User doInBackground(Void... arg0) {
//			User u = UsersManager.getUser(username);
//			return u;
//		}
//
//		protected void onPostExecute(User result) {
//			if(isJoin){
//				new AddToCalendarTask(result).execute();
//			}else{
//				new DeleteFromCalendar(result).execute();
//			}
//		}
//	}
//
//
//	private class AddToCalendarTask extends AsyncTask<Void, Void, Void> {
//		User user;
//		AddToCalendarTask(User user){
//			this.user = user;
//		}
//
//		@Override
//		protected Void doInBackground(Void... arg0) {
//			try{
//				EventsCalendar.pushAppointmentsToCalender(JoinRidesActivity.this, selectedRideSave, 0, true, true,user.getName(),user.getEmail());
//				//Toast.makeText(getApplicationContext(), "Event added to Calendar.", Toast.LENGTH_SHORT).show();
//			}catch(Exception e){
//				//Toast.makeText(getApplicationContext(), "Please configure your Calendar to get ride notifications.", Toast.LENGTH_SHORT).show();
//			}
//			return null;
//		}
//
//		protected void onPostExecute(Void result) {
//		}
//
//	}
//
//	private class DeleteFromCalendar extends AsyncTask<Void, Void, Void> {
//		User user;
//		DeleteFromCalendar(User user){
//			this.user = user;
//		}
//
//		@Override
//		protected Void doInBackground(Void... arg0) {
//			try{
//				EventsCalendar.removeAppointmentsFromCalender(JoinRidesActivity.this, selectedRideSave, 0, user.getName(),user.getEmail());
//				//Toast.makeText(getApplicationContext(), "Event removed from Calendar.", Toast.LENGTH_SHORT).show();
//			}catch(Exception e){
//				//Toast.makeText(getApplicationContext(), "Event added to Calendar.", Toast.LENGTH_SHORT).show();
//			}
//			return null;
//		}
//
//		protected void onPostExecute(Void result) {
//		}
//
//	}
//
//	//AsynTask for joining the rides
//	public class UnJoinRideTask extends AsyncTask<Void,Void,String> {
//		Exception error;
//
//		protected String doInBackground(Void... params) {
//			return  RidesManager.deleteParticipantToRide(selectedRideSave.getId(), username);
//		}
//
//		protected void onPostExecute(String result) {
//			if(result.equalsIgnoreCase("Success")){
//				Toast.makeText(getApplicationContext(), "Unjoined the ride successfully." , Toast.LENGTH_SHORT).show();
//				//							selectedStar.setSelected(true);
//			}else{
//				Toast toast = Toast.makeText(getApplicationContext(), "An error occured.", Toast.LENGTH_SHORT);
//				TextView v = (TextView) toast.getView().findViewById(android.R.id.message);
//				v.setTextColor(getResources().getColor(R.color.red));
//				toast.show();
//			}
//			//change order in rideEntry
//			ArrayList<RideDetailsMap> temp = new ArrayList<RideDetailsMap>();
//
//			for(RideDetailsMap r:rideEntry){
//				if(!selectedRideSave.getId().equals(r.getRideid())){
//					temp.add(r);
//				}
//			}
//			temp.add(rideEntry.get(selectedRidePosition));
//			temp.get(temp.size()-1).setJoined(false);
//			rideEntry = temp;
//			displayListView();
//		}
//	}
}