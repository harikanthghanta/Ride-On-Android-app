package com.android.charan.shareride.entities;


public class UserActivity extends BaseEntity {
	
	String id;
	String rideId;
	String userName;
	double distaceCovered;
	double cadence;
	double averageSpeed;
	double caloriesBurned;
	double timeOfRide;
	double heartRate;
	long activityDate;
	
	public UserActivity() {}

	public UserActivity(String id, String rideId, String userName, double distaceCovered,
			double cadence, double averageSpeed, double caloriesBurned, double timeOfRide, double heartRate, long activityDate) {
		super();
		this.id = id;
		this.rideId = rideId;
		this.userName = userName;
		this.distaceCovered = distaceCovered;
		this.cadence = cadence;
		this.averageSpeed = averageSpeed;
		this.caloriesBurned = caloriesBurned;
		this.timeOfRide = timeOfRide;
		this.heartRate = heartRate;
		this.activityDate = activityDate;
	}

	public String getId() {
		return id;
	}

	public void setId(String id) {
		this.id = id;
	}

	public String getRideId() {
		return rideId;
	}

	public void setRideId(String rideId) {
		this.rideId = rideId;
	}

	public String getUserName() {
		return userName;
	}

	public void setUserName(String userName) {
		this.userName = userName;
	}

	public double getDistaceCovered() {
		return distaceCovered;
	}

	public void setDistaceCovered(double distaceCovered) {
		this.distaceCovered = distaceCovered;
	}

	public double getCadence() {
		return cadence;
	}

	public void setCadence(double cadence) {
		this.cadence = cadence;
	}

	public double getAverageSpeed() {
		return averageSpeed;
	}

	public void setAverageSpeed(double averageSpeed) {
		this.averageSpeed = averageSpeed;
	}

	public double getCaloriesBurned() {
		return caloriesBurned;
	}

	public void setCaloriesBurned(double caloriesBurned) {
		this.caloriesBurned = caloriesBurned;
	}
	
	public double getTimeOfRide() {
		return timeOfRide;
	}

	public void setTimeOfRide(double timeOfRide) {
		this.timeOfRide = timeOfRide;
	}
	
	public long getActivityDate() {
		return activityDate;
	}

	public void setActivityDate(long activityDate) {
		this.activityDate = activityDate;
	}
	
	public double getHeartRate() {
		return heartRate;
	}

	public void setHeartRate(long heartRate) {
		this.heartRate = heartRate;
	}

	@Override
	public String toString() {
		return "UserActivity: Id = " + this.id + ", " + 
				"Ride Id = " + this.rideId + ", " +
				"User Name = " + this.userName + ", " +
				"Distance Covered = " + this.distaceCovered + ", " +
				"Creator = " + this.caloriesBurned + ", " +
				"Cadence = " + this.cadence + ", " +
				"Avg speed = " + this.averageSpeed + ", " +
				"Date = " + this.activityDate
				;
	}
}