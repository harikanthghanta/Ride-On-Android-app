package com.android.charan.shareride;

import java.net.MalformedURLException;
import java.text.DecimalFormat;
import java.util.Date;

import com.android.charan.shareride.R;
import com.android.charan.shareride.Util.UIUtils;
import com.android.charan.shareride.tabpanel.MenuConstants;
import com.android.charan.shareride.tabpanel.MyTabHostProvider;
import com.android.charan.shareride.tabpanel.TabView;

import android.content.Context;
import android.content.Intent;
import android.graphics.Color;
import android.os.AsyncTask;
import android.os.Bundle;
import android.view.View;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

public class LogRideDetailsActivity extends BaseActivity {

	RideDetails ride;

	double distanceCovered, averageSpeed = 0.0, cadence, heartRate, timeOfRide = 0, caloriesBurned;

	TextView textViewRideName, textViewDistanceCovered, textViewTimeOfRide, textViewAverageSpeed, textViewHeartRate, textViewCadence, textViewExperience, textViewCaloriesBurned;
	Button btnSubmit;
	String blogText = "";

	LinearLayout progressBar;
	static TextView textViewLoginError;

	DecimalFormat df = new DecimalFormat("#.###");
	DecimalFormat tf = new DecimalFormat("#.##");

	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);

		Bundle b = getIntent().getExtras();
		distanceCovered = b.getDouble("DistanceCovered");
		averageSpeed = b.getDouble("AverageSpeed");
		timeOfRide = b.getDouble("TimeOfRide");

		//Draw menu
		tabProvider = new MyTabHostProvider(LogRideDetailsActivity.this);
		TabView tabView = tabProvider.getTabHost(MenuConstants.JOIN_RIDES);
		tabView.setCurrentView(R.layout.log_ride_details_activity);
		setContentView(tabView.render());

		this.getWindow().setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_STATE_ALWAYS_HIDDEN);

		setFormFields();
		textViewLoginError = (TextView) findViewById(R.id.textViewLogRideError);
		textViewLoginError.setVisibility(View.INVISIBLE);
		
		setAsteriskLabels();
	}

	@Override
	protected void setTitle() {
		ride = (RideDetails) getIntent().getSerializableExtra("Ride");



		final TextView myTitleText = (TextView)findViewById(R.id.myTitle);
		myTitleText.setText("Log Ride Details");
	}

	private void validateUserInputAndCallAsyncTask() {

		//Add validation code here
		boolean isValid = true;
		boolean isValidCadence = true;
		boolean isValidHeartrate = true;
		
		String cadenceString = ((TextView) findViewById(R.id.textViewLogRideDetailsCadence)).getText().toString().trim();
		String heartrateString = ((TextView) findViewById(R.id.textViewLogRideDetailsHeartRate)).getText().toString().trim();
		String caloriesBurnedString = ((TextView) findViewById(R.id.textViewLogRideDetailsCaloriesBurned)).getText().toString().trim();
		blogText = ((TextView) findViewById(R.id.textViewLogRideDetailsExperience)).getText().toString();

		if(cadenceString.equals("")|| heartrateString.equals("")||caloriesBurnedString.equals("") || blogText.equals(""))
			isValid = false;

		if(isValid && Integer.parseInt(cadenceString) > 150)
			isValidCadence = false;
	
		if(isValid && (Integer.parseInt(heartrateString) > 150 || Integer.parseInt(cadenceString) < 40))
			isValidHeartrate = false;
		
		
		//Validations for the textboxes
		
		if(isValid && isValidCadence && isValidHeartrate) {
			progressBar.setVisibility(View.VISIBLE);
			LinearLayout createRideForm = (LinearLayout) findViewById(R.id.logRideDetailsForm);
			createRideForm.setVisibility(View.INVISIBLE);

			new LogRideDetailsTask().execute();

		} 
		else if(!isValidCadence)
		{
			CharSequence text = "Cadence rate cannot be more than 150! Please enter again.";
			
			Toast toast = Toast.makeText(getApplicationContext(), text, Toast.LENGTH_SHORT);
			TextView v = (TextView) toast.getView().findViewById(android.R.id.message);
			v.setTextColor(getResources().getColor(R.color.red));
			toast.show();
		}
		else if(!isValidHeartrate)
		{
			CharSequence text = "Invalid Heartrate! Please enter again.";
			
			Toast toast = Toast.makeText(getApplicationContext(), text, Toast.LENGTH_SHORT);
			TextView v = (TextView) toast.getView().findViewById(android.R.id.message);
			v.setTextColor(getResources().getColor(R.color.red));
			toast.show();
		}
		else
		{
			textViewLoginError.setText("Enter all fields!");
			textViewLoginError.setVisibility(View.VISIBLE);
			Context context = getApplicationContext();
			CharSequence text = "Enter all fields!";
			int duration = Toast.LENGTH_SHORT;
			
			Toast toast = Toast.makeText(getApplicationContext(), text, Toast.LENGTH_SHORT);
			TextView v = (TextView) toast.getView().findViewById(android.R.id.message);
			v.setTextColor(getResources().getColor(R.color.red));
			toast.show();
		}
}

	private void setFormFields() {

		progressBar = (LinearLayout) findViewById(R.id.logRideDetailsSpinner);
		progressBar.setVisibility(View.INVISIBLE);

		//Get the elements on the form
		textViewRideName = (TextView) findViewById(R.id.textViewLogRideDetailsRideName);
		textViewRideName.setKeyListener(null);

		textViewDistanceCovered = (TextView) findViewById(R.id.textViewLogRideDetailsDistanceCovered);
		textViewDistanceCovered.setKeyListener(null);

		textViewTimeOfRide = (TextView) findViewById(R.id.textViewLogRideDetailsRideTime);
		textViewTimeOfRide.setKeyListener(null);

		textViewAverageSpeed = (TextView) findViewById(R.id.textViewLogRideDetailsAverageSpeed);
		textViewAverageSpeed.setKeyListener(null);

		textViewHeartRate = (TextView) findViewById(R.id.textViewLogRideDetailsHeartRate);
		textViewCadence = (TextView) findViewById(R.id.textViewLogRideDetailsCadence);
		textViewCaloriesBurned = (TextView) findViewById(R.id.textViewLogRideDetailsCaloriesBurned);

		textViewExperience = (TextView) findViewById(R.id.textViewLogRideDetailsExperience);

		//Set the parameters which are recorded automatically
		textViewRideName.setText(ride.getRideName());
		//textViewDistanceCovered.setBackgroundResource(Color.parseColor("#C0C0C0"));
		textViewDistanceCovered.setText(df.format(distanceCovered) + " mi");
		//textViewAverageSpeed.setBackgroundResource(Color.parseColor("#C0C0C0"));
		textViewAverageSpeed.setText(tf.format(averageSpeed) + " mi/hr");
		//textViewTimeOfRide.setBackgroundResource(Color.parseColor("#C0C0C0"));
		textViewTimeOfRide.setText(tf.format(timeOfRide) + " mins");

		//Set onClickListener for the submit button
		Button button  = (Button) findViewById(R.id.btnSubmit);
		button.setOnClickListener(new View.OnClickListener() {

			public void onClick(View v) {
				validateUserInputAndCallAsyncTask();
			}
		});
	}

	private void moveToJoinRidesPage() {

		progressBar.setVisibility(View.INVISIBLE);

		LinearLayout logRideDetailsForm = (LinearLayout) findViewById(R.id.logRideDetailsForm);
		logRideDetailsForm.setVisibility(View.VISIBLE);

		//clear the text boxes
		textViewRideName.setText("");
		textViewDistanceCovered.setText("");
		textViewTimeOfRide.setText("");
		textViewAverageSpeed.setText("");
		textViewHeartRate.setText("");
		textViewCadence.setText("");
		textViewCaloriesBurned.setText("");
		textViewExperience.setText("");

		/*Intent joinRidesIntent = new Intent(LogRideDetailsActivity.this, MyPastRidesActivity.class);*/
		Intent mainpage = new Intent(LogRideDetailsActivity.this, CreateRide.class);
		LogRideDetailsActivity.this.startActivity(mainpage);

	}

	private class LogRideDetailsTask extends AsyncTask<Void,Void,User> {

		Exception errorPostBlog;
		Exception errorLogDetails;

		protected User doInBackground(Void... params) {

			//Log activity to backend
			Date activityDate = new Date();
			String result = "";

			try {

				/*cadence = Double.parseDouble((TextView) findViewById(R.id.textViewLogRideDetailsCadence)).getText().toString().trim();
				heartRate = com.ncsu.edu.spinningwellness.Utils.Validator.isDouble(((TextView) findViewById(R.id.textViewLogRideDetailsHeartRate)).getText().toString().trim());
				caloriesBurned = com.ncsu.edu.spinningwellness.Utils.Validator.isDouble(((TextView) findViewById(R.id.textViewLogRideDetailsCaloriesBurned)).getText().toString().trim());
*/


				//result = UsersManager.logActivity(ride.getId(), BaseActivity.username, distanceCovered, cadence, averageSpeed, caloriesBurned, timeOfRide, heartRate, activityDate);

			} catch (NumberFormatException e) {
				//set the error message on the page
			}

			if(result.equalsIgnoreCase("Success")) {


				/*if(blogText != null && !blogText.equals("")){
					//Post to blog
					System.setProperty("org.xml.sax.driver", "org.xmlpull.v1.sax2.Driver");
					Wordpress wp;
					try {
						wp = new Wordpress(BaseActivity.username, BaseActivity.password, xmlRpcUrl);
						Page recentPost = new Page();
						recentPost.setDescription(blogText);
						recentPost.setTitle("Ride Experience: " + ride.getName() + " (posted from Spinning Wellness)");
						wp.newPost(recentPost, true);
					} catch (MalformedURLException e) {
						errorPostBlog = e;
					} catch (XmlRpcFault e) {
						errorPostBlog = e;
					}
				}*/
			} else {
				//set the error message on the page
			}
			return null;
		}

		protected void onPostExecute(User result) {
			//Redirect to join rides page
			if(errorPostBlog != null && errorLogDetails != null){
				Toast toast = Toast.makeText(getApplicationContext(), "An error occured.", Toast.LENGTH_SHORT);
				TextView v = (TextView) toast.getView().findViewById(android.R.id.message);
				v.setTextColor(getResources().getColor(R.color.red));
				toast.show();
			}else if(errorPostBlog != null){
				Toast toast = Toast.makeText(getApplicationContext(), "An error occured while posting to blog.", Toast.LENGTH_SHORT);
				TextView v = (TextView) toast.getView().findViewById(android.R.id.message);
				v.setTextColor(getResources().getColor(R.color.red));
				toast.show();
			} else if (errorLogDetails != null){
				Toast toast = Toast.makeText(getApplicationContext(), "An error occured while logging the details.", Toast.LENGTH_SHORT);
				TextView v = (TextView) toast.getView().findViewById(android.R.id.message);
				v.setTextColor(getResources().getColor(R.color.red));
				toast.show();
			}
			moveToJoinRidesPage();
			//		    textViewLoginError = (TextView) findViewById(R.id.textViewLogRideError);
			//			textViewLoginError.setVisibility(View.VISIBLE);
		}
	}

	private void setAsteriskLabels() {

		TextView textViewLogRideEnterHeartRateLabel = (TextView) findViewById(R.id.textViewLogRideEnterHeartRateLabel);
		textViewLogRideEnterHeartRateLabel.setText(
				UIUtils.buildSpannableStringWithAsterisk(getResources().getString(R.string.lbl_enter_heart_rate))
				);

		TextView textViewLogRideEnterCadenceLabel = (TextView) findViewById(R.id.textViewLogRideEnterCadenceLabel);
		textViewLogRideEnterCadenceLabel.setText(
				UIUtils.buildSpannableStringWithAsterisk(getResources().getString(R.string.lbl_enter_cadence))
				);

		TextView textViewLogRideEnterCaloriesBurnedLabel = (TextView) findViewById(R.id.textViewLogRideEnterCaloriesBurnedLabel);
		textViewLogRideEnterCaloriesBurnedLabel.setText(
				UIUtils.buildSpannableStringWithAsterisk(getResources().getString(R.string.lbl_enter_calories_burned))
				);
		
		TextView textViewLogRidePostYourExperienceToBlog = (TextView) findViewById(R.id.textViewLogRidePostYourExperienceToBlog);
		textViewLogRidePostYourExperienceToBlog.setText(
				UIUtils.buildSpannableStringWithAsterisk(getResources().getString(R.string.lbl_post_your_experience_to_blog))
				);

	}

}