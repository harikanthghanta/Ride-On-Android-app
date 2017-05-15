package com.android.charan.shareride.Util;

import com.android.charan.shareride.RideDetails;
import com.android.charan.shareride.User;

import java.io.UnsupportedEncodingException;
import java.sql.Timestamp;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.List;
import java.util.Locale;
import java.util.Properties;

import javax.mail.Authenticator;
import javax.mail.BodyPart;
import javax.mail.Message;
import javax.mail.Message.RecipientType;
import javax.mail.MessagingException;
import javax.mail.Multipart;
import javax.mail.PasswordAuthentication;
import javax.mail.Session;
import javax.mail.Transport;
import javax.mail.internet.AddressException;
import javax.mail.internet.InternetAddress;
import javax.mail.internet.MimeBodyPart;
import javax.mail.internet.MimeMessage;
import javax.mail.internet.MimeMultipart;



public class EmailDispatcherJoined extends Authenticator{

    private static String mUserName;
    private static String mPassword;
    private static String mHostName;
    private boolean canSend = false;

    public void sendEmailToAll(List<String> u, RideDetails ride) {
        mUserName="shareride045@gmail.com";
        mPassword="harikanthcharan";
        mHostName="smtp.gmail.com";

        try {
            Properties props = new Properties();
            props.put("mail.smtp.host", mHostName);
            props.put("mail.smtp.auth", "true");
            props.put("mail.smtp.starttls.enable", "true");
            props.put("mail.smtp.port", "587");
            // this object will handle the authentication
            Session session=Session.getInstance(props,this);
            Message msg = new MimeMessage(session);
            msg.setFrom(new InternetAddress("shareride", "Harikanth"));
            InternetAddress[] addressTo = new InternetAddress[u.size()];
            int i=0;
            for(String user:u){
                if(user != null){
//					msg.addRecipient(Message.RecipientType.TO,
//							new InternetAddress(user.getEmail(), user.getName()));
                    addressTo[i++] = new InternetAddress(user);
                    canSend = true;
                }
            }
            if(canSend){
                msg.setRecipients(Message.RecipientType.TO, addressTo);
                //Build message subject string
                StringBuilder msgSubject = new StringBuilder();
                msgSubject.append("Joined this ride : " + ride.getRideName());
                msg.setSubject(msgSubject.toString());

                //Build message
                StringBuilder msgBody = new StringBuilder();
                msgBody.append("Hello, \n");
                msgBody.append("\n");
                msgBody.append("you joined this ride created by " + ride.getUser() + "\n");
                msgBody.append("Ride Details - \n");
                msgBody.append("\tRide Name: " + ride.getRideName() +" \n");
                msgBody.append("\tStarting Point: " + ride.getSource() +" \n");
                msgBody.append("\tEnd Point: " + ride.getDestination() +" \n");
//TODO
                Date rideDate = new Date(ride.getDate());
                String[] formats = new String[] {"dd-MMM-yy", "HH:mm"};

                SimpleDateFormat dfForRideDate = new SimpleDateFormat(formats[0], Locale.US);
                msgBody.append("\tDate: " + dfForRideDate.format(rideDate) +" \n");

                SimpleDateFormat dfForRideTime = new SimpleDateFormat(formats[1], Locale.US);
                msgBody.append("\tTime: " + dfForRideTime.format(rideDate) +" \n");


                msgBody.append("\n");
                msgBody.append("You can join the ride using ShareRide app...\n");

                msgBody.append("\n");
                msgBody.append("- Admin\n");

                msg.setText(msgBody.toString());
                Transport transport = session.getTransport("smtp");

                transport.connect(mUserName, mPassword);
                transport.sendMessage(msg, msg.getAllRecipients());
                transport.close();
            }
        } catch (AddressException e) {
            e.printStackTrace();
        } catch (MessagingException e) {
            e.printStackTrace();
        } catch (UnsupportedEncodingException e) {
            e.printStackTrace();
        }

    }

    @Override
    public PasswordAuthentication getPasswordAuthentication() {
        return new PasswordAuthentication(mUserName, mPassword);
    }
}