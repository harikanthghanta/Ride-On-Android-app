package com.android.charan.shareride.Util;

import java.util.Calendar;
import java.util.Date;
import java.util.TimeZone;

import com.android.charan.shareride.RideDetails;

import android.app.Activity;
import android.content.ContentUris;
import android.content.ContentValues;
import android.database.Cursor;
import android.net.Uri;
/**
 * 
 * @author Owner
 *
 */
public class EventsCalendar {

	public static long pushAppointmentsToCalender(Activity curActivity, RideDetails r, int status, boolean needReminder, boolean needMailService, String username, String email) throws Exception{
		String eventUriString = "content://com.android.calendar/events";
		ContentValues eventValues = new ContentValues();

		eventValues.put("calendar_id", 1); // id, We need to choose from
		// our mobile for primary
		// its 1
		eventValues.put("title", r.getRideName());
		eventValues.put("description", "Ride:" + r.getRideName() );
		eventValues.put("eventLocation", r.getSource() + " To " + r.getDestination());
		long endDate = r.getDate() + 1000 * 60 * 60; // For next 1hr
		Calendar c  = Calendar.getInstance();
		c.setTime(new Date(r.getDate()));

		Calendar c1 = Calendar.getInstance();
		c1.setTime(new Date(endDate));

		eventValues.put("dtstart", r.getDate());
		eventValues.put("dtend", endDate);

		// values.put("allDay", 1); //If it is bithday alarm or such
		// kind (which should remind me for whole day) 0 for false, 1
		// for true
		eventValues.put("eventStatus", status); // This information is
		// sufficient for most
		// entries tentative (0),
		// confirmed (1) or canceled
		// (2):
		//	    eventValues.put("visibility", 3); // visibility to default (0),
		//	                                        // confidential (1), private
		// (2), or public (3):
		//	    eventValues.put("transparency", 0); // You can control whether
		// an event consumes time
		// opaque (0) or transparent
		// (1).
		eventValues.put("hasAlarm", 1); // 0 for false, 1 for true
		eventValues.put("eventTimezone", TimeZone.getDefault().getID());

		// Uri eventUri = curActivity.getApplicationContext().getContentResolver().insert(Uri.parse(eventUriString), eventValues);
		Uri url = curActivity.getApplicationContext().getContentResolver().insert(Uri.parse(eventUriString), eventValues);
		long eventID = Long.parseLong(url.getLastPathSegment());

		if (needReminder) {
			String reminderUriString = "content://com.android.calendar/reminders";

			ContentValues reminderValues = new ContentValues();

			reminderValues.put("event_id", eventID);
			reminderValues.put("minutes", 5); // Default value of the
			// system. Minutes is a
			// integer
			reminderValues.put("method", 1); // Alert Methods: Default(0),
			// Alert(1), Email(2),
			// SMS(3)

			Uri reminderUri = curActivity.getApplicationContext().getContentResolver().insert(Uri.parse(reminderUriString), reminderValues);
		}

		/***************** Event: Meeting(without alert) Adding Attendies to the meeting *******************/

		if (needMailService) {
			String attendeuesesUriString = "content://com.android.calendar/attendees";

			/********
			 * To add multiple attendees need to insert ContentValues multiple
			 * times
			 ***********/
			ContentValues attendeesValues = new ContentValues();

			attendeesValues.put("event_id", eventID);
			attendeesValues.put("attendeeName", username); // Attendees name
			attendeesValues.put("attendeeEmail", email);// Attendee email
			attendeesValues.put("attendeeRelationship", 0); // Relationship_Attendee(1),
			// Relationship_None(0),
			// Organizer(2),
			// Performer(3),
			// Speaker(4)
			attendeesValues.put("attendeeType", 0); // None(0), Optional(1),
			// Required(2), Resource(3)
			attendeesValues.put("attendeeStatus", 0); // NOne(0), Accepted(1),
			// Decline(2),
			// Invited(3),
			// Tentative(4)

			Uri attendeesUri = curActivity.getApplicationContext().getContentResolver().insert(Uri.parse(attendeuesesUriString), attendeesValues);
		}
		return eventID;
	}

	public static void removeAppointmentsFromCalender(Activity curActivity, RideDetails r, int status, String username, String email) throws Exception{
		String eventUriString = "content://com.android.calendar/events";
	
		Cursor cursors = curActivity.getApplicationContext().getContentResolver().query(Uri.parse(eventUriString), null, null, null, null);
		if (cursors.moveToFirst()){
			while (cursors.moveToNext())
			{
				String desc = cursors.getString(cursors.getColumnIndex("description"));
				String location = cursors.getString(cursors.getColumnIndex("eventLocation"));   
				// event id
				String id = cursors.getString(cursors.getColumnIndex("_id"));
				long stDate = cursors.getLong(cursors.getColumnIndex("dtstart"));
				//				long endDate = cursors.getLong(cursors.getColumnIndex("dtend"));
				if (desc.equals("Ride:" + r.getId()) && (stDate == r.getDate())){
					Uri uri = ContentUris.withAppendedId(Uri.parse(eventUriString), Long.parseLong(id));
					curActivity.getApplicationContext().getContentResolver().delete(uri, null, null);
				}

			}
		}
	}
}
