package com.android.charan.shareride;

import java.util.Date;

/**
 * Created by harikanth on 5/12/17.
 */

public class RideDetails {

    private String rideName;
    private String source;
    private String destination;
    private String User;
    /*private String date;*/
    private long date;

    public RideDetails(){

    }

    public RideDetails(String u, String r,String s,String d, long da){
        rideName = r;
        source = s;
        destination = d;
        User = u;
        date = da;


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
        return User;
    }

    public long getDate() {
        return date;
    }
}
