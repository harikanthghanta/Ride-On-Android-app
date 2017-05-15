package com.android.charan.shareride;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Locale;

import com.android.charan.shareride.R;
import com.android.charan.shareride.Util.Utils;
import com.android.charan.shareride.tabpanel.MenuConstants;
import com.android.charan.shareride.tabpanel.MyTabHostProvider;
import com.android.charan.shareride.tabpanel.TabView;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.TextView;

public class ViewRideDetailsActivity extends BaseActivity {

	RideDetails ride;
	LinearLayout progressBar;

	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);

		//Draw menu
		tabProvider = new MyTabHostProvider(ViewRideDetailsActivity.this);
		TabView tabView = tabProvider.getTabHost(MenuConstants.JOIN_RIDES);
		tabView.setCurrentView(R.layout.view_ride_details_activity);
		setContentView(tabView.render());

		progressBar = (LinearLayout) findViewById(R.id.rideDetailsSpinner);
		progressBar.setVisibility(View.VISIBLE);

		addListenerOnButton();

		fillRideDetails();

		progressBar = (LinearLayout) findViewById(R.id.rideDetailsSpinner);
		progressBar.setVisibility(View.INVISIBLE);
	}

	@Override
	protected void setTitle() {
		ride = (RideDetails) getIntent().getSerializableExtra("Ride");

		final TextView myTitleText = (TextView)findViewById(R.id.myTitle);
		myTitleText.setText("Ride Details");
	}

	void fillRideDetails() {

		((TextView) findViewById(R.id.textViewRideDetailsRideName)).setText(ride.getRideName());
		((TextView) findViewById(R.id.textViewRideDetailsSource)).setText(ride.getSource());
		((TextView) findViewById(R.id.textViewRideDetailsDestination)).setText(ride.getDestination());
		((TextView) findViewById(R.id.textViewRideDetailsCreator)).setText(ride.getUser()	);
		
		Date rideDate = Utils.convertStringToDate(ride.getDate());

		String[] formats = new String[] {"dd-MMM-yy", "HH:mm"};
		
		SimpleDateFormat dfForRideDate = new SimpleDateFormat(formats[0], Locale.US);
		((TextView) findViewById(R.id.textViewRideDetailsDate)).setText(dfForRideDate.format(rideDate));

		SimpleDateFormat dfForRideTime = new SimpleDateFormat(formats[1], Locale.US);
		((TextView) findViewById(R.id.textViewRideDetailsTime)).setText(dfForRideTime.format(rideDate));
	}

	public void addListenerOnButton() {

		final Context context = this;

		Button member_button  = (Button) findViewById(R.id.btnViewParticipants);
		member_button.setOnClickListener(new OnClickListener() {
			@Override
			public void onClick(View v) {
				Intent member_intent = new Intent(context, ViewParticipantsForARideActivity.class);
				member_intent.putExtra("Ride", ride);
				member_intent.putExtra("Caller", "JoinRide");
				startActivity(member_intent);   
			}
		});
	}	
}