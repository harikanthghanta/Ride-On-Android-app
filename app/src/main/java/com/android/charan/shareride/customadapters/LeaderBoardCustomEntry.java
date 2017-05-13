package com.android.charan.shareride.customadapters;

public class LeaderBoardCustomEntry implements Comparable<LeaderBoardCustomEntry> {

	private String userName;
	private double distanceCovered;
	private int position;

	public LeaderBoardCustomEntry(String userName, double distanceCovered, int position) {
		super();
		this.userName = userName;
		this.distanceCovered = distanceCovered;
		this.position = position;
	}

	public String getUserName() {
		return userName;
	}

	public void setUserName(String userName) {
		this.userName = userName;
	}

	public double getDistanceCovered() {
		return distanceCovered;
	}

	public void setDistanceCovered(double distanceCovered) {
		this.distanceCovered = distanceCovered;
	}
	
	public int getPosition() {
		return position;
	}

	public void setPosition(int position) {
		this.position = position;
	}

	@Override
	public int compareTo(LeaderBoardCustomEntry another) {
		if(this.distanceCovered < another.distanceCovered){
			return 1;
		} else if(this.distanceCovered > another.distanceCovered) {
			return -1;	
		} else{
			return 0;
		}
	}
}