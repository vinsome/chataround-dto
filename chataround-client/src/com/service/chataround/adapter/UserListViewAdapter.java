package com.service.chataround.adapter;

import java.util.List;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.TextView;

import com.next.infotech.persistance.domain.UserPublicDomain;
import com.service.chataround.ChatAroundActivity;
import com.service.chataround.R;
import com.service.chataround.dto.chat.UserPublicDto;
import com.service.chataround.util.ChatUtils;

public class UserListViewAdapter extends ArrayAdapter<UserPublicDto> {
	public static String TAG = UserListViewAdapter.class.getName();
	private List<UserPublicDto> items;
	//context is a ChatAroundActivity
	private Context ctx;

	public UserListViewAdapter(Context context, int textViewResourceId,
			List<UserPublicDto> items) {
		super(context, textViewResourceId, items);
		this.items = items;
		this.ctx = context;
	}

	@Override
	public View getView(int position, View convertView, ViewGroup parent) {
		View v = convertView;
		if (v == null) {
			LayoutInflater vi = (LayoutInflater) ctx.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
			v = vi.inflate(R.layout.row_userlist, null);
		}
		ChatAroundActivity act = (ChatAroundActivity)ctx;
		
		UserPublicDomain o = items.get(position);
		
		if (o != null) {
			TextView userNickname = (TextView) v.findViewById(R.id.userNicknameLeft);
			TextView userMood = (TextView) v.findViewById(R.id.userMoodLeft);
			TextView distanceFromMe = (TextView) v.findViewById(R.id.userDistanceFromLeft);
			
			userNickname.setText(o.getNickName());
			userMood.setText(o.getStatusMessage());
			float distance = ChatUtils.distanceFromUser(act.getLocationListener().getCurrentBestLocation().getLongitude() 
					, act.getLocationListener().getCurrentBestLocation().getLatitude(), o.getLattitude(),o.getLongitude());
			distanceFromMe.setText(distance +" km");
			
		}
		return v;
	}

}
