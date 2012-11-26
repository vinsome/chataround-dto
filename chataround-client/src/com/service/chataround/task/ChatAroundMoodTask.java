package com.service.chataround.task;

import android.app.Fragment;
import android.content.Context;
import android.os.AsyncTask;
import android.util.Log;

import com.service.chataround.ChatAroundSettingActivity;
import com.service.chataround.dto.chat.UserStatusUpdateDto;
import com.service.chataround.util.ChatAroundHttpClient;

public class ChatAroundMoodTask extends AsyncTask<Object, Integer, UserStatusUpdateDto> {
	public static String TAG = ChatAroundRegisterUserTask.class.getName();
	protected final Context mContext;
	protected final Fragment fragment;

	public ChatAroundMoodTask(Context ctx, Fragment fragment) {
		this.mContext = ctx;
		this.fragment = fragment;

	}

	@Override
	protected void onPreExecute() {
 
	}

	@Override
	protected UserStatusUpdateDto doInBackground(Object... params) {
		UserStatusUpdateDto result = null;
		try {
			if (params[0] instanceof UserStatusUpdateDto) {
				UserStatusUpdateDto dto = (UserStatusUpdateDto) params[0];
					String url2call = (String) params[1];
					ChatAroundHttpClient.postSpringData(url2call, Void.class, dto);
					//all ok
					result = new UserStatusUpdateDto();
			}
		} catch (Exception e) {
			Log.e("validateUser error.loading.datafromserver",
					"Error loading data from server", e);
		}
		return result;
	}

	@Override
	protected void onPostExecute(UserStatusUpdateDto result) {
		ChatAroundSettingActivity frag = (ChatAroundSettingActivity)mContext;
			frag.finishTaskRegisterUser(result);			
	}

}
