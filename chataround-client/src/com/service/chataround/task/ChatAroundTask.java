package com.service.chataround.task;

import java.util.HashMap;
import java.util.Map;

import android.app.Fragment;
import android.content.Context;
import android.os.AsyncTask;
import android.util.Log;

import com.google.android.gcm.GCMRegistrar;
import com.service.chataround.ChatAroundActivity;
import com.service.chataround.dto.chat.ChatAroundDto;
import com.service.chataround.dto.register.RegisterUserRequestDto;
import com.service.chataround.fragment.ChatFragment;
import com.service.chataround.util.ChatAroundHttpClient;

public class ChatAroundTask extends AsyncTask<Object, Integer, ChatAroundDto> {
	protected final Context mContext;
	protected final Fragment fragment;

	public ChatAroundTask(Context ctx, Fragment fragment) {
		this.mContext = ctx;
		this.fragment = fragment;

	}

	@Override
	protected void onPreExecute() {

	}

	@Override
	protected ChatAroundDto doInBackground(Object... params) {
		ChatAroundDto response = null;
		try {
			if (params[0] instanceof RegisterUserRequestDto) {
				RegisterUserRequestDto dto = (RegisterUserRequestDto) params[0];
				String url2call = (String) params[1];
				Map<String,String> values = new HashMap<String,String>(0);
				
				values.put("userId", dto.getUserId());
				values.put("email", dto.getEmail());
				values.put("password", dto.getPassword());
				values.put("nickName", dto.getNickName());
				values.put("longitude", dto.getLongitude().toString());
				values.put("lattitude", dto.getLattitude().toString());
				values.put("statusMessage", dto.getStatusMessage());
				values.put("deviceId", dto.getDeviceId());
				
				String result = ChatAroundHttpClient.postData(url2call,values);
				Log.i("server.response", result);

			}
		} catch (Exception e) {
			Log.e("validateUser error.loading.datafromserver",
					"Error loading data from server", e);
		}
		return response;
	}

	@Override
	protected void onPostExecute(ChatAroundDto result) {

		if (mContext instanceof ChatAroundActivity && fragment == null) {
			ChatAroundActivity activity = (ChatAroundActivity) mContext;
			if (result != null) {
				activity.finishTask(result);
			}
		} else if (fragment instanceof ChatFragment) {
			ChatFragment fg = (ChatFragment)fragment;
				if(result!=null){
					fg.finishTask(result);
				}
		} else {
			// The registration progress ends saving the regId into the device
			// preferences
			if (result != null
					&& result.getResponse().equals(
							"push.server.operation.register.ok")) {
				GCMRegistrar.setRegisteredOnServer(mContext, true);
				Log.d("HttpCall", "Call to google made!!!!! call2WebApp="
						+ result.getResponse() + "]");
			} else {
				GCMRegistrar.unregister(mContext);
			}
		}

	}

}