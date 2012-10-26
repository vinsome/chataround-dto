package com.service.chataround.task;

import android.content.Context;
import android.os.AsyncTask;
import android.util.Log;

import com.service.chataround.dto.chat.ChatAroundDto;
import com.service.chataround.util.ChatAroundHttpClient;

public class ChatAroundTask extends AsyncTask<Object, Integer, ChatAroundDto> {
	protected final Context mContext;

	public ChatAroundTask(Context ctx) {
		this.mContext = ctx;
	}

	@Override
	protected void onPreExecute() {

	}

	@Override
	protected ChatAroundDto doInBackground(Object... params) {
		ChatAroundDto response = null;
		try {
			if (params[0] instanceof ChatAroundDto) {
				ChatAroundDto dto = (ChatAroundDto) params[0];
				String url2call = (String) params[1];

				response = ChatAroundHttpClient.postSpringData(url2call,
						ChatAroundDto.class, dto);

			}
		} catch (Exception e) {
			Log.e("validateUser error.loading.datafromserver",
					"Error loading data from server", e);
		}
		return response;
	}

	@Override
	protected void onPostExecute(ChatAroundDto result) {
		/*
		if (mContext instanceof MessageActivity) {
			MessageActivity activity = (MessageActivity) mContext;
			if (result != null) {
				activity.finishTask(result);
			}
		} else if (mContext instanceof MessageActivity) {
			EvangelioActivity activity = (EvangelioActivity) mContext;
			if (result != null) {
				activity.finishTask(result);
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
		*/
	}

}