package com.android.charan.shareride;

import android.os.Parcelable;

import java.io.Serializable;
import java.util.Date;

/**
 * Created by harikanth on 5/12/17.
 */

public class RideDetails implements Serializable {

    private String rideName;
    private String source;
    private String destination;
    private String user;
    /*private String date;*/
    private long date;
    private String id;

    public RideDetails(){

    }

    public RideDetails(String u, String r,String s,String d, long da, String id){
        rideName = r;
        source = s;
        destination = d;
        user = u;
        date = da;
        this.id = id;


    }

    public String getRideName(){
        return rideName;
    }

    public String getDestination() {
        return destination;
    }

    public String getSource() {
        return source;
    }

    public String getUser() {
        return user;
    }

    public long getDate() {
        return date;
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    @Override
    public String toString() {
        return "Ride: id = " + this.id + ", " +
                "rideName = " + this.rideName + ", " +
                "source = " + this.source + ", " +
                "destination = " + this.destination + ", " +
                "user = " + this.user + ", " +
                "date = " + this.date
                ;
    }
    @Override
    public int hashCode() {
        final int prime = 31;
        int result = 1;
        result = prime * result + ((id == null) ? 0 : id.hashCode());
        return result;
    }
}
