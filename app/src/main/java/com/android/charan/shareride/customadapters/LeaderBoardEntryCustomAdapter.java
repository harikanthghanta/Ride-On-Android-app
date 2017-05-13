package com.android.charan.shareride.customadapters;

import java.text.DecimalFormat;
import java.util.List;

import com.android.charan.shareride.R;

import android.app.Activity;
import android.content.Context;
import android.content.res.Resources;
import android.graphics.drawable.Drawable;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.ImageView;
import android.widget.TextView;

public class LeaderBoardEntryCustomAdapter extends ArrayAdapter<LeaderBoardCustomEntry> {

	private List<LeaderBoardCustomEntry> users;
	private Activity activity;

	public LeaderBoardEntryCustomAdapter(Activity a, int textViewResourceId, List<LeaderBoardCustomEntry> users) {
		super(a, textViewResourceId, users);
		this.users = users;
		this.activity = a;
	}

	//@Override
//	public View getView(int position, View convertView, ViewGroup parent) {
//		View v = convertView;
//		ViewHolder holder;
//		if (v == null) {
//			LayoutInflater vi = (LayoutInflater)activity.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
//			v = vi.inflate(R.layout.leader_board_users_list, null);
//			holder = new ViewHolder();
//			holder.userName = (TextView) v.findViewById(R.id.leaderBoardUserName);
//			holder.distanceCovered = (TextView) v.findViewById(R.id.leaderBoardDistanceCovered);
//			holder.medal = (TextView) v.findViewById(R.id.leaderBoardPosition);
//			v.setTag(holder);
//		} else {
//			holder = (ViewHolder) v.getTag();
//		}
//
//		final LeaderBoardCustomEntry custom = users.get(position);
//		if (custom != null) {
//			holder.userName.setText(custom.getUserName());
//			DecimalFormat tf = new DecimalFormat("#.##");
//			holder.distanceCovered.setText(tf.format(custom.getDistanceCovered()) + " mi");
//			if(custom.getPosition() == -1)
//				holder.medal.setText("-");
//			else
//				holder.medal.setText(((Integer) custom.getPosition()).toString());
//		}
//
//		return v;
//	}

	public class ViewHolder {
		public TextView userName;
		public TextView distanceCovered;
		public TextView medal;
	}
}