package com.android.charan.shareride.customadapters;

public class CustomEntry implements Comparable<CustomEntry> {
	private String rideid;
	private long startDate;
	public long getStartDate() {
		return startDate;
	}

	public void setStartDate(long startDate) {
		this.startDate = startDate;
	}

	public String getRideid() {
		return rideid;
	}

	public void setRideid(String rideid) {
		this.rideid = rideid;
	}

	private String textVal;
	private boolean isJoined;

	public CustomEntry(String rideid,long startDate,String string, boolean string2) {
		this.startDate = startDate;
		this.rideid = rideid;
		this.textVal = string;
		this.isJoined = string2;
	}

	public String getTextVal() {
		return textVal;
	}

	public void setTextVal(String textVal) {
		this.textVal = textVal;
	}

	public boolean isJoined() {
		return isJoined;
	}

	public void setJoined(boolean isJoined) {
		this.isJoined = isJoined;
	}

	@Override
	public int compareTo(CustomEntry another) {
		if(this.startDate < another.startDate){
			return -1;
		} else if(this.startDate > another.startDate) {
			return 1;	
		} else{
			return 0;
		}
	}
	
}
