package com.android.charan.shareride.entities;


public class Participant extends BaseEntity {

	String id;
	String rideId;
	String userName;
	
	public Participant() {}
	
	public Participant(String id, String rideId, String userName) {
		super();
		this.id = id;
		this.rideId = rideId;
		this.userName = userName;
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
	
	@Override
	public String toString() {
		return "Participant: Ride Id = " + this.rideId + ", " +
				"User Name = " + this.userName
				;
	}
}
