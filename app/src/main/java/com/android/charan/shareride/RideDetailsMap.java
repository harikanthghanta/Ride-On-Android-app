package com.android.charan.shareride;



/**
 * Created by harikanth on 5/14/17.
 */

public class RideDetailsMap implements Comparable<RideDetailsMap>{
    private RideDetails rideDetails;
    private boolean isJoin;

    public RideDetailsMap(RideDetails rD, boolean iJ){
        rideDetails = rD;
        isJoin = iJ;
    }

    public RideDetails getRideDetails() {
        return rideDetails;
    }

    public boolean isJoin() {
        return isJoin;
    }

    public void setJoin(boolean join) {
        isJoin = join;
    }

    public void setRideDetails(RideDetails rideDetails) {
        this.rideDetails = rideDetails;
    }
    @Override
    public int compareTo(RideDetailsMap another) {
        if(this.rideDetails.getDate() < another.rideDetails.getDate()){
            return -1;
        } else if(this.rideDetails.getDate() > another.rideDetails.getDate()) {
            return 1;
        } else{
            return 0;
        }
    }
}
