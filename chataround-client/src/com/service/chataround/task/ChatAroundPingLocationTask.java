package com.service.chataround.task;

import android.app.Fragment;
import android.content.Context;
import android.os.AsyncTask;
import android.util.Log;

import com.service.chataround.dto.chat.UserPingRequestDto;
import com.service.chataround.dto.chat.UserPingResponseDto;
import com.service.chataround.fragment.ChatAroundListFragment;
import com.service.chataround.util.ChatAroundHttpClient;

public class ChatAroundPingLocationTask extends
		AsyncTask<Object, Integer, UserPingResponseDto> {
	public static String TAG = ChatAroundPingLocationTask.class.getName();
	protected final Context mContext;
	protected final Fragment fragment;

	public ChatAroundPingLocationTask(Context ctx, Fragment fragment) {
		this.mContext = ctx;
		this.fragment = fragment;

	}

	@Override
	protected void onPreExecute() {

	}

	@Override
	protected UserPingResponseDto doInBackground(Object... params) {
		UserPingResponseDto result = null;
		try {
			if (params[0] instanceof UserPingRequestDto) {
				UserPingRequestDto dto = (UserPingRequestDto) params[0];
				String url2call = (String) params[1];
				result = ChatAroundHttpClient
						.postSpringData(url2call, UserPingResponseDto.class,
								dto);
				//patch to do the trick
				//rest = "{'userList': "+ rest+"}";
				//Gson gson = new Gson();
				//result =	gson.fromJson(rest, UserPingRequestDto.class);
				if (result != null) {
					Log.i("RegisterUserRequestDto server.response",
							"Result not null");
				} else {
					Log.i("RegisterUserRequestDto server.response",
							"Result null");
				}
			}
		} catch (Exception e) {
			Log.e("validateUser error.loading.datafromserver",
					"Error loading data from server", e);
		}
		return result;
	}

	@Override
	protected void onPostExecute(UserPingResponseDto result) {
		if (result != null) {
			ChatAroundListFragment frag = (ChatAroundListFragment) fragment;
			frag.finishTaskPingUser(result);
		}
	}
}
