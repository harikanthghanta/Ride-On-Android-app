package com.android.charan.shareride;

import java.text.DecimalFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;

import com.android.charan.shareride.R;
import com.android.charan.shareride.Util.Utils;
import com.android.charan.shareride.entities.UserActivity;
import com.android.charan.shareride.tabpanel.MenuConstants;
import com.android.charan.shareride.tabpanel.MyTabHostProvider;
import com.android.charan.shareride.tabpanel.TabView;

import android.app.Activity;
import android.app.AlertDialog;
import android.app.Dialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.location.Location;
import android.location.LocationListener;
import android.location.LocationManager;
import android.os.AsyncTask;
import android.os.Bundle;
import android.os.SystemClock;
import android.support.v4.app.ActivityCompat;
import android.support.v4.content.ContextCompat;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.View.OnClickListener;
import android.widget.ArrayAdapter;
import android.widget.CheckBox;
import android.widget.Chronometer;
import android.widget.CompoundButton;
import android.widget.ImageButton;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;
import android.widget.CompoundButton.OnCheckedChangeListener;

public class RecordRideStatsActivity extends BaseActivity {

	private static final long MIN_DISTANCE_CHANGE_FOR_UPDATES = 50; // 50 meters
	private static final long MIN_TIME_BW_UPDATES = 30000; // 30 sec

	RideDetails ride;
	DecimalFormat df = new DecimalFormat("#.###");
	DecimalFormat tf = new DecimalFormat("#.##");

	TextView textViewDistance, textViewAverageSpeed, textViewRideName;
	ImageButton btnPlay, btnPause, btnStop, btnStopDisable;
	Chronometer chronometer;

	LocationManager locationManager;
	String locationManagerType;
	LocationListener myLocationListener;
	UserActivity userDetails;

	double previousLatitude = 0.0, previousLongitude = 0.0;
	double distanceCovered_met = 0.0;
	double distanceCovered = 0.0;
	long timeOfRide = 0;
	double timeRead_mins = 0;
	double averageSpeed = 0.0;

	boolean isGPSEnabled = false, isNetworkEnabled = false;
	boolean isStarted = false;
	boolean wasPaused = false;
	boolean wasStopped = false;
	boolean flag_cancel = false;
	Context context;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		App.setContext(this);
		context = App.getContext();
		Date currentTimestamp = new Date();


		long currTime = Utils.convertDateToString(currentTimestamp);
		//long rideTime = ride.getDate();

		//Draw menu
		tabProvider = new MyTabHostProvider(RecordRideStatsActivity.this);
		TabView tabView = tabProvider.getTabHost(MenuConstants.JOIN_RIDES);
		tabView.setCurrentView(R.layout.record_ride_stats_activity);
		setContentView(tabView.render());

		btnStopDisable = (ImageButton) findViewById(R.id.btnStopDisable);
		btnPause = (ImageButton) findViewById(R.id.btnPause);
		btnStop = (ImageButton) findViewById(R.id.btnStop);
		btnPlay = (ImageButton) findViewById(R.id.btnPlay);

		//
		//		if(currTime < rideTime )
		//		{
		//			btnPlayDisable.setVisibility(View.VISIBLE);
		//			btnStopDisable.setVisibility(View.VISIBLE);
		//			btnPause.setVisibility(View.INVISIBLE);
		//			btnStop.setVisibility(View.INVISIBLE);
		//			earlyLateRide("Early");
		//		}

		new isRideLoggedTask().execute();

		btnPlay.setVisibility(View.VISIBLE);
		btnStopDisable.setVisibility(View.VISIBLE);
		btnStop.setVisibility(View.INVISIBLE);
		btnPause.setVisibility(View.INVISIBLE);

		locationManager = (LocationManager) this.getSystemService(LOCATION_SERVICE);

		isGPSEnabled = locationManager.isProviderEnabled(LocationManager.GPS_PROVIDER);

		isNetworkEnabled = locationManager.isProviderEnabled(LocationManager.NETWORK_PROVIDER);
		setPageElementsAndOnClickListeners();

	}

	@Override
	public void onSaveInstanceState(Bundle savedInstanceState) {
		super.onSaveInstanceState(savedInstanceState);

		if (isStarted) {
			timeOfRide = chronometer.getBase() - SystemClock.elapsedRealtime();
		}

		savedInstanceState.putDouble("previousLatitude", previousLatitude);
		savedInstanceState.putDouble("previousLongitude", previousLongitude);
		savedInstanceState.putDouble("distanceCovered", distanceCovered);
		savedInstanceState.putLong("timeOfRide", timeOfRide);

		savedInstanceState.putBoolean("isStarted", isStarted);
	}

	@Override
	public void onRestoreInstanceState(Bundle savedInstanceState) {
		super.onRestoreInstanceState(savedInstanceState);

		previousLatitude = savedInstanceState.getDouble("previousLatitude");
		previousLongitude = savedInstanceState.getDouble("previousLongitude");
		distanceCovered = savedInstanceState.getDouble("distanceCovered");

		//Adjust time of ride in chronometer
		timeOfRide = savedInstanceState.getLong("timeOfRide");

		isStarted = savedInstanceState.getBoolean("isStarted");
		if (isStarted) {
			btnPlay.setVisibility(View.INVISIBLE);
			btnStopDisable.setVisibility(View.INVISIBLE);
			btnPause.setVisibility(View.VISIBLE);
			btnStop.setVisibility(View.VISIBLE);

			chronometer.setBase(SystemClock.elapsedRealtime() + timeOfRide);
			chronometer.start();
		} else {
			btnPlay.setVisibility(View.VISIBLE);
			btnStopDisable.setVisibility(View.VISIBLE);
			btnStop.setVisibility(View.INVISIBLE);
			btnPause.setVisibility(View.INVISIBLE);


			//Hack to make sure that chronometer shows correct reading even when 
			//the phone's orientation is changes and chronometer not on
			chronometer.setBase(SystemClock.elapsedRealtime() + timeOfRide);
			chronometer.start();
			timeOfRide = chronometer.getBase() - SystemClock.elapsedRealtime();
			chronometer.stop();
		}
	}


	@Override
	protected void setTitle() {
		ride = (RideDetails) getIntent().getSerializableExtra("Ride");

		final TextView myTitleText = (TextView) findViewById(R.id.myTitle);
		myTitleText.setText("Start Ride");
	}


	private void registerLocationListener() {
		myLocationListener = new LocationListener() {
			public void onLocationChanged(Location location) {

				if (isStarted) {

					if (previousLatitude == 0.0 && previousLongitude == 0.0) {
						previousLatitude = location.getLatitude();
						previousLongitude = location.getLongitude();
						distanceCovered = 0.5;

					} else {

						float[] dist = {0};

						try {
							Location.distanceBetween(previousLatitude, previousLongitude, location.getLatitude(), location.getLongitude(), dist);

						} catch (IllegalArgumentException e) {

							dist[0] = 0;
						}

						previousLatitude = location.getLatitude();
						previousLongitude = location.getLongitude();
						System.out.println(previousLongitude);
						distanceCovered_met += (float) dist[0];  //distance in meters
						distanceCovered = (float) ((distanceCovered_met / 1000) * 0.62137);   //distance in miles

						timeRead_mins = readChronometer();  //time read in mins

						try {
							averageSpeed = distanceCovered / (timeRead_mins / 60); //Avg speed in miles/hr
						} catch (NumberFormatException e) {
							averageSpeed = 0.0;
						}

						setDistanceCovered();
						setAvgSpeed();
					}
				}
			}

			@Override
			public void onProviderDisabled(String provider) {
				// TODO Auto-generated method stub
			}

			@Override
			public void onProviderEnabled(String provider) {
				// TODO Auto-generated method stub
			}

			@Override
			public void onStatusChanged(String provider, int status,
										Bundle extras) {
				// TODO Auto-generated method stub
			}
		};
	}

	private void setPageElementsAndOnClickListeners() {

		chronometer = (Chronometer) findViewById(R.id.chronometer);

		textViewDistance = (TextView) findViewById(R.id.textViewDistance);
		textViewAverageSpeed = (TextView) findViewById(R.id.textViewAverageSpeed);

		textViewRideName = (TextView) findViewById(R.id.textViewRecordRideDetailsRideName);
		textViewRideName.setText(ride.getRideName());
		int permissionCheck = ContextCompat.checkSelfPermission(RecordRideStatsActivity.this,
				android.Manifest.permission.ACCESS_FINE_LOCATION);

		btnPlay.setOnClickListener(new OnClickListener() {

			public void onClick(View v) {
				isGPSEnabled = locationManager.isProviderEnabled(LocationManager.GPS_PROVIDER);
				isNetworkEnabled = locationManager.isProviderEnabled(LocationManager.NETWORK_PROVIDER);


				if (!isGPSEnabled && !isNetworkEnabled) {
					Toast toast = Toast.makeText(getApplicationContext(), "No Network/GPS", Toast.LENGTH_SHORT);
					TextView tv = (TextView) toast.getView().findViewById(android.R.id.message);
					tv.setTextColor(getResources().getColor(R.color.red));
					toast.show();
					flag_cancel = true;
					createGpsDisabledAlert();
				}

				if (isGPSEnabled ) {
					registerLocationListener();
					flag_cancel = false;
					if (ActivityCompat.checkSelfPermission(context, android.Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
						// TODO: Consider calling
						//    ActivityCompat#requestPermissions
						// here to request the missing permissions, and then overriding
						//   public void onRequestPermissionsResult(int requestCode, String[] permissions,
						//                                          int[] grantResults)
						// to handle the case where the user grants the permission. See the documentation
						// for ActivityCompat#requestPermissions for more details.
						return;
					}
					locationManager.requestLocationUpdates(LocationManager.GPS_PROVIDER, MIN_TIME_BW_UPDATES, MIN_DISTANCE_CHANGE_FOR_UPDATES, myLocationListener);
					locationManagerType = LocationManager.GPS_PROVIDER;
				} else if(isNetworkEnabled) {
					registerLocationListener();
					flag_cancel = false;
					locationManager.requestLocationUpdates(LocationManager.NETWORK_PROVIDER, MIN_TIME_BW_UPDATES, MIN_DISTANCE_CHANGE_FOR_UPDATES, myLocationListener);
					locationManagerType = LocationManager.NETWORK_PROVIDER;
				}

				if(!flag_cancel){

					if(wasStopped){
						timeOfRide = 0;
						wasStopped = false;
					}

					chronometer.setBase(SystemClock.elapsedRealtime() + timeOfRide);
					chronometer.start();

					isStarted = true;

					locationManager.requestLocationUpdates(locationManagerType, MIN_TIME_BW_UPDATES, MIN_DISTANCE_CHANGE_FOR_UPDATES, myLocationListener);

					btnPlay.setVisibility(View.INVISIBLE);
					btnStopDisable.setVisibility(View.INVISIBLE);
					btnPause.setVisibility(View.VISIBLE);
					btnStop.setVisibility(View.VISIBLE);

				}
			}
		});
		btnPlay.setVisibility(View.VISIBLE);
		//btnStopDisable.setVisibility(View.VISIBLE);
		btnStop.setVisibility(View.VISIBLE);
		btnPause.setVisibility(View.INVISIBLE);

		btnPause.setOnClickListener(new OnClickListener() {

			public void onClick(View v) {
				timeOfRide = chronometer.getBase() - SystemClock.elapsedRealtime();
				chronometer.stop();

				isStarted = false;
				wasPaused = true;

				locationManager.removeUpdates(myLocationListener);

				btnPlay.setVisibility(View.VISIBLE);
				//btnStopDisable.setVisibility(View.VISIBLE);
				btnPause.setVisibility(View.INVISIBLE);		
				btnStop.setVisibility(View.VISIBLE);

			}
		});

		btnPlay.setVisibility(View.VISIBLE);
		//btnStopDisable.setVisibility(View.VISIBLE);
		btnPause.setVisibility(View.INVISIBLE);		
		btnStop.setVisibility(View.VISIBLE);


		btnStop.setOnClickListener(new OnClickListener() {

			public void onClick(View v) {	
				createConfirmStopAlert();
			}
		});
	}

	private void stopAction(){
		if(wasPaused){
			//use the previous timeOfRide value
		}
		else {
			timeOfRide = chronometer.getBase() - SystemClock.elapsedRealtime();
		}

		chronometer.stop();

		isStarted = false;
		wasPaused = false;
		wasStopped = true;

		btnPlay.setVisibility(View.VISIBLE);
		//btnStopDisable.setVisibility(View.VISIBLE);
		btnStop.setVisibility(View.VISIBLE);
		btnPause.setVisibility(View.INVISIBLE);				

		//		Toast.makeText(RecordRideStatsActivity.this, "Time: " + tf.format(readChronometer()) + "mins", Toast.LENGTH_SHORT).show();
		locationManager.removeUpdates(myLocationListener);

		//Redirect to next page with all the fields in intent
		Intent i = new Intent(getApplicationContext(), LogRideDetailsActivity.class);
		i.putExtra("Ride", ride);

		Bundle b = new Bundle();
		b.putDouble("DistanceCovered", Double.parseDouble(df.format(distanceCovered)));
		if(Double.isInfinite(averageSpeed))
			b.putDouble("AverageSpeed", Double.parseDouble(df.format(0.0)));
		else 
			b.putDouble("AverageSpeed", Double.parseDouble(df.format(averageSpeed)));
		b.putDouble("TimeOfRide", Double.parseDouble(df.format(readChronometer())));
		i.putExtras(b);

		startActivity(i);
	}

	protected void createConfirmStopAlert() {
		AlertDialog alertDialog = new AlertDialog.Builder(this).create();
		alertDialog.setMessage("Do you really want to stop?");
		alertDialog.setButton( Dialog.BUTTON_POSITIVE, "Ok", new DialogInterface.OnClickListener() {
			public void onClick(DialogInterface dialog, int which) {
				stopAction();
			}});

		alertDialog.setButton( Dialog.BUTTON_NEGATIVE, "Cancel", new DialogInterface.OnClickListener()    {
			public void onClick(DialogInterface dialog, int which) {
				dialog.cancel();
			}});
		alertDialog.show(); 
	}

	protected void lateRide() {
		AlertDialog alertDialog = new AlertDialog.Builder(this).create();
		//		if(status.equals("Early"))
		//		{
		//			alertDialog.setMessage("You are early! Scheduled ride is yet to start!");
		//			alertDialog.setButton( Dialog.BUTTON_NEGATIVE, "Ok", new DialogInterface.OnClickListener()    {
		//				public void onClick(DialogInterface dialog, int which) {
		//					Intent i = new Intent(getApplicationContext(), JoinRidesActivity.class);
		//					startActivity(i);
		//				}});
		//
		//		}

		alertDialog.setMessage("Are you you sure you want to overwrite your stats?");

		alertDialog.setButton( Dialog.BUTTON_POSITIVE, "Ok", new DialogInterface.OnClickListener() {
			public void onClick(DialogInterface dialog, int which) {
				dialog.cancel();
			}});

		alertDialog.setButton( Dialog.BUTTON_NEGATIVE, "Cancel", new DialogInterface.OnClickListener()    {
			public void onClick(DialogInterface dialog, int which) {

				Intent i = new Intent(getApplicationContext(), JoinRidesActivity.class);
				startActivity(i);

			}});



		alertDialog.show(); 
	}

	private double readChronometer(){
		double stoppedMins = 0;
		String chronoText = chronometer.getText().toString();
		String array[] = chronoText.split(":");

		if (array.length == 2) {
			stoppedMins = Integer.parseInt(array[0]) + (float) (Float.parseFloat(array[1]) / 60);
		} 
		else if (array.length == 3) {
			stoppedMins = Integer.parseInt(array[0]) * 60 + Integer.parseInt(array[1]) + (float) (Float.parseFloat(array[2]) / 60);
		}

		//	Toast.makeText(RecordRideStatsActivity.this, "Time: " + stoppedMins + "mins", Toast.LENGTH_SHORT).show();
		return stoppedMins;
	}


	private void setDistanceCovered() {
		textViewDistance.setText("Distance: " + df.format(distanceCovered) + " mi");
	}

	private void setAvgSpeed() {
		textViewAverageSpeed.setText("Avg Speed: " + tf.format(averageSpeed) + " mi/hr");
	}

	protected void createGpsDisabledAlert() {
		AlertDialog builder = new AlertDialog.Builder(this).create();
		builder.setMessage("Your GPS is disabled! Would you like to enable it?");

		builder.setButton("Enable GPS", new DialogInterface.OnClickListener() {

			public void onClick(DialogInterface dialog, int id) {
				showGpsOptions();
			}
		});

		builder.setButton2("Back", new DialogInterface.OnClickListener() {
			public void onClick(DialogInterface dialog, int id) {
				//	flag_cancel = true;
				return;
			}
		});
		builder.show();
	}


	private void showGpsOptions() {
		Intent gpsOptionsIntent = new Intent(
				android.provider.Settings.ACTION_LOCATION_SOURCE_SETTINGS);
		startActivity(gpsOptionsIntent);
	}


	//AsynTask for getting user details
	public class isRideLoggedTask extends AsyncTask<Void, Void, UserActivity> {
		Exception error;

		protected UserActivity doInBackground(Void... params) {
			return null;
			//return UsersManager.viewPastActivityForARide(BaseActivity.username, ride.getId());
		}

		protected void onPostExecute(UserActivity result) {
			if(error != null) {

			} else {
				userDetails = (UserActivity) result;
				if(userDetails != null && userDetails.getId() != null) {
					lateRide();					
				}
			}
		}
	}

	private void executeNormalBackPressed() {
		super.onBackPressed();
	}

	@Override
	public void onBackPressed() {
		AlertDialog alertDialog = new AlertDialog.Builder(this).create();
		alertDialog.setMessage("Do you want to stop recording the ride?");
		alertDialog.setButton( Dialog.BUTTON_POSITIVE, "Ok", new DialogInterface.OnClickListener() {
			public void onClick(DialogInterface dialog, int which) {
				executeNormalBackPressed();
			}});

		alertDialog.setButton( Dialog.BUTTON_NEGATIVE, "Cancel", new DialogInterface.OnClickListener()    {
			public void onClick(DialogInterface dialog, int which) {
				dialog.cancel();
			}});
		alertDialog.show();								
	}


}