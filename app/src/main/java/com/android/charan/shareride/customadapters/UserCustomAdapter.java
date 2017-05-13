package com.android.charan.shareride.customadapters;

import java.util.List;

import com.android.charan.shareride.R;

import android.app.Activity;
import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.TextView;

public class UserCustomAdapter extends ArrayAdapter<String> {
	
	private List<String> users;
	private Activity activity;

	public UserCustomAdapter(Activity a, int textViewResourceId, List<String> users) {
		super(a, textViewResourceId, users);
		this.users = users;
		this.activity = a;
	}

	@Override
	public View getView(int position, View convertView, ViewGroup parent) {
		View v = convertView;
		ViewHolder holder;
		if (v == null) {
			LayoutInflater vi = (LayoutInflater)activity.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
			v = vi.inflate(R.layout.common_users_list, null);
			holder = new ViewHolder();
			holder.userName = (TextView) v.findViewById(R.id.userTextVal);
			v.setTag(holder);
		} else {
			holder = (ViewHolder) v.getTag();
		}

		final String custom = users.get(position);
		if (custom != null) {
			holder.userName.setText(custom);
		}

		return v;
	}
	
	public class ViewHolder {
		public TextView userName;
	}
	
	
}