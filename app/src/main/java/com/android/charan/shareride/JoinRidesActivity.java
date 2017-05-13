package com.android.charan.shareride;

//import java.awt.Image;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Collections;
import java.util.Date;
import java.util.List;
import java.util.Locale;

//import javax.swing.text	.Position;

import com.android.charan.shareride.R;
import com.android.charan.shareride.customadapters.CustomEntry;
import com.android.charan.shareride.tabpanel.MenuConstants;
import com.android.charan.shareride.tabpanel.MyTabHostProvider;
import com.android.charan.shareride.tabpanel.TabView;


import android.app.Activity;
import android.app.AlertDialog;
import android.app.Dialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.Typeface;
import android.os.AsyncTask;
import android.os.Bundle;
import android.os.UserManager;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.View.OnClickListener;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
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

	List<RideDetails> rideList = new ArrayList<RideDetails>();
	ArrayList<CustomEntry> rideEntry = new ArrayList<CustomEntry>();
	private LinearLayout progressBar;
	RideDetails selectedRideSave;
	int selectedRidePosition;
	public List<RideDetails> myJoinedRidesList;

	@Override
	public void onCreate(Bundle savedInstanceState) {
	
		super.onCreate(savedInstanceState);
		//Draw menu
		tabProvider = new MyTabHostProvider(JoinRidesActivity.this);
		TabView tabView = tabProvider.getTabHost(MenuConstants.JOIN_RIDES);
		tabView.setCurrentView(R.layout.join_rides_activity);
		setContentView(tabView.render());			

		progressBar = (LinearLayout) findViewById(R.id.Spinner);
		progressBar.setVisibility(View.VISIBLE);

		//new GetUpcomingRidesTask().execute();
	
	}

//	private void displayListView() {
//
//		progressBar.setVisibility(View.INVISIBLE);
//
//		ListView listView1 = (ListView) findViewById(R.id.listView1);
//		listView1.setVisibility(View.VISIBLE);
//
//		//sort by date
//		List<CustomEntry> tempJoined = new ArrayList<CustomEntry>();
//		List<CustomEntry> tempNotJoined = new ArrayList<CustomEntry>();
//		for(CustomEntry ce:rideEntry){
//			if(ce.isJoined()){
//				tempJoined.add(ce);
//			}else{
//				tempNotJoined.add(ce);
//			}
//		}
//
//		Collections.sort(tempJoined);
//		Collections.sort(tempNotJoined);
//
//		rideEntry.clear();
//		rideEntry.addAll(tempJoined);
//		rideEntry.addAll(tempNotJoined);
//
//		List<RideDetails> temp = new ArrayList<RideDetails>();
//		for(CustomEntry r:rideEntry){
//			for(RideDetails r1:rideList){
//				/*if(r1.getId().equals(r.getRideid())){
//					temp.add(r1);
//				}*/
//			}
//		}
//		rideList.clear();
//		rideList.addAll(temp);
//		//create an ArrayAdaptar from the String Array
//		CustomAdapter dataAdapter = new CustomAdapter(this, R.id.textVal, rideEntry);
//		final ListView listView = (ListView) findViewById(R.id.listView1);
//
//		// Assign adapter to ListView
//		listView.setAdapter(dataAdapter);
//
//		//  listView.setLayoutParams(new ListView.LayoutParams(ListView.LayoutParams.FILL_PARENT, ListView.LayoutParams.WRAP_CONTENT));
//
//		//enables filtering for the contents of the given ListView
//		listView.setTextFilterEnabled(true);
//		//listView.setItemsCanFocus(false);
//		listView.setOnItemClickListener(new OnItemClickListener() {
//			@Override
//			public void onItemClick(AdapterView<?> parent, View view,
//					int position, long id) {
//
//
////				Toast.makeText(getApplicationContext(), " Clicked " , Toast.LENGTH_SHORT).show();
//
//				RideDetails selectedRide = null;
//				String Caller = "JoinRide";
//				int positionView = listView.getPositionForView(view);
//				if (positionView != ListView.INVALID_POSITION) {
//					//start view activity
//					selectedRide = rideList.get(positionView);
//					/*Intent i = new Intent(getApplicationContext(), ViewRideDetailsActivity.class);
//					i.putExtra("Caller", Caller);
//					i.putExtra("Ride", selectedRide);
//
//					startActivity(i);*/
//				}else{
//					Toast toast = Toast.makeText(getApplicationContext(), "An error occured.", Toast.LENGTH_SHORT);
//					TextView v = (TextView) toast.getView().findViewById(android.R.id.message);
//					v.setTextColor(getResources().getColor(R.color.red));
//					toast.show();
//				}
//			}
//		});
//	}
//
//	public class CustomAdapter extends ArrayAdapter<CustomEntry> {
//		private ArrayList<CustomEntry> entries;
//		private Activity activity;
//
//		public CustomAdapter(Activity a, int textViewResourceId, ArrayList<CustomEntry> entries) {
//			super(a, textViewResourceId, entries);
//			this.entries = entries;
//			this.activity = a;
//		}
//
//		public class ViewHolder{
//			public TextView item1;
//			public CheckBox item2;
//			public TextView textFieldDateTime;
//			public ImageButton start;
//		}
//
//		@Override
//		public View getView(int position, View convertView, ViewGroup parent) {
//			View v = convertView;
//			ViewHolder holder;
//			//			if (v == null) {
//			LayoutInflater vi =
//					(LayoutInflater)activity.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
//			v = vi.inflate(R.layout.join_rides_list, null);
//			holder = new ViewHolder();
//			holder.item1 = (TextView) v.findViewById(R.id.textVal);
//			holder.textFieldDateTime = (TextView) v.findViewById(R.id.textValDateTime);
//			holder.item2 = (CheckBox) v.findViewById(R.id.isJoined);
//
//			holder.start = (ImageButton) v.findViewById(R.id.img_start);
//			final CustomEntry custom = entries.get(position);
//			if (custom != null) {
//
//				Date rideDate = Utils.convertStringToDate(custom.getStartDate());
//
//				String[] formats = new String[] {"dd-MMM-yy", "HH:mm"};
//				SimpleDateFormat dfForRideDate = new SimpleDateFormat(formats[0], Locale.US);
//				SimpleDateFormat dfForRideTime = new SimpleDateFormat(formats[1], Locale.US);
//
//
//				holder.item1.setText(custom.getTextVal());
//				holder.textFieldDateTime.setText(dfForRideDate.format(rideDate) + "   " + dfForRideTime.format(rideDate));
//
//				holder.item2.setChecked(custom.isJoined());
//				if(custom.isJoined()){
//					holder.item1.setTypeface(null, Typeface.BOLD);
//					holder.start.setEnabled(true);
//					holder.start.setImageResource(R.drawable.button_start);
//				}else{
//					holder.start.setEnabled(false);
//					holder.start.setImageResource(R.drawable.button_start_disabled);
//				}
//			}
//			holder.start.setOnClickListener(mStartButtonClickListener);
//			holder.item2.setOnCheckedChangeListener(mStarCheckedChangeListener);
//
//			//			}
//			//			else
//			holder=(ViewHolder)v.getTag();
//			v.setTag(holder);
//			return v;
//		}
//
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
//		private OnCheckedChangeListener mStarCheckedChangeListener = new OnCheckedChangeListener() {
//			@Override
//			public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
//				final ListView listView = (ListView) findViewById(R.id.listView1);
//				final int position = listView.getPositionForView(buttonView);
//				if (position != ListView.INVALID_POSITION) {
//					//todo:update mStarStates[position] = isChecked;
//					Ride r = rideList.get(position);
//					selectedRideSave = r;
//					selectedRidePosition = position;
//					progressBar = (LinearLayout) findViewById(R.id.Spinner);
//					progressBar.setVisibility(View.VISIBLE);
//
//					listView.setVisibility(View.INVISIBLE);
//					if(isChecked){
//						new JoinRideTask().execute();
//					}else{
//						new UnJoinRideTask().execute();
//					}
//				}
//			}
//		};
//	}
//
	@Override
	public void setTitle() {
		final TextView myTitleText = (TextView)findViewById(R.id.myTitle);
		myTitleText.setText("Join Ride");
	}
//
//	//AsynTask for getting the list of rides
//	public class GetUpcomingRidesTask extends AsyncTask<Void,Void,List<Ride>> {
//		Exception error;
//
//		protected List<Ride> doInBackground(Void... params) {
//			return RidesManager.viewUpcomingRides();
//		}
//
//		protected void onPostExecute(List<Ride> result) {
//			if(error != null){
//
//			} else {
//				rideList = result;
//				new GetJoinedRidesForUser().execute();
//			}
//		}
//	}
//
//	//AsynTask for getting the list of rides
//	public class GetJoinedRidesForUser extends AsyncTask<Void,Void,List<Ride>> {
//		Exception error;
//
//		protected List<Ride> doInBackground(Void... params) {
//			return RidesManager.viewMyUpcomingRides(username);
//		}
//
//		protected void onPostExecute(List<Ride> result) {
//			myJoinedRidesList = result;
//			List<CustomEntry> temp = new ArrayList<CustomEntry>();
//			for(Ride r:rideList){
//				if(myJoinedRidesList.contains(r)){
//					temp.add(new CustomEntry(r.getId(),r.getStartTime(),r.getName(), true));
//				}else{
//					temp.add(new CustomEntry(r.getId(),r.getStartTime(),r.getName(), false));
//				}
//			}
//			for(CustomEntry r:temp){
//				if(r.isJoined()){
//					rideEntry.add(r);
//				}
//			}
//			for(CustomEntry r:temp){
//				if(!r.isJoined()){
//					rideEntry.add(r);
//				}
//			}
//
//
//			displayListView();
//		}
//	}
//
//	protected void earlyRide() {
//		AlertDialog alertDialog = new AlertDialog.Builder(this).create();
//		alertDialog.setMessage("You are early! Scheduled ride is yet to start!");
//		alertDialog.setButton( Dialog.BUTTON_NEGATIVE, "Ok", new DialogInterface.OnClickListener()    {
//			public void onClick(DialogInterface dialog, int which) {
//				dialog.cancel();
//			}});
//		alertDialog.show();
//	}
//	//AsynTask for joining the rides
//	public class JoinRideTask extends AsyncTask<Void,Void,String> {
//		Exception error;
//
//		protected String doInBackground(Void... params) {
//			return  RidesManager.addParticipantToRide(selectedRideSave.getId(), username);
//		}
//
//		protected void onPostExecute(String result) {
//			if(result.equalsIgnoreCase("Success")){
//				Toast.makeText(getApplicationContext(), "Joined the ride successfully." , Toast.LENGTH_SHORT).show();
//				//					selectedStar.setSelected(true);
//			}else{
//				Toast toast = Toast.makeText(getApplicationContext(), "An error occured.", Toast.LENGTH_SHORT);
//				TextView v = (TextView) toast.getView().findViewById(android.R.id.message);
//				v.setTextColor(getResources().getColor(R.color.red));
//				toast.show();
//			}
//			//change order in rideEntry
//			ArrayList<CustomEntry> temp = new ArrayList<CustomEntry>();
//			temp.add(rideEntry.get(selectedRidePosition));
//			temp.get(0).setJoined(true);
//			for(CustomEntry r:rideEntry){
//				if(!selectedRideSave.getId().equals(r.getRideid())){
//					temp.add(r);
//				}
//			}
//			rideEntry = temp;
//			new GetLoggedinUserTask(true).execute();
//			displayListView();
//		}
//	}
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
//			ArrayList<CustomEntry> temp = new ArrayList<CustomEntry>();
//
//			for(CustomEntry r:rideEntry){
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