package com.service.chataround.task;

import android.app.Fragment;
import android.content.Context;
import android.os.AsyncTask;
import android.util.Log;

import com.service.chataround.dto.chat.ChatMessageDto;
import com.service.chataround.dto.chat.ChatMessageResponseDto;
import com.service.chataround.fragment.ChatFragment;
import com.service.chataround.util.ChatAroundHttpClient;

public class ChatAroundSendMessageTask extends
		AsyncTask<Object, Integer, ChatMessageResponseDto> {
	public static String TAG = ChatAroundSendMessageTask.class.getName();
	protected final Context mContext;
	protected final Fragment fragment;

	public ChatAroundSendMessageTask(Context ctx, Fragment fragment) {
		this.mContext = ctx;
		this.fragment = fragment;

	}

	@Override
	protected void onPreExecute() {

	}

	@Override
	protected ChatMessageResponseDto doInBackground(Object... params) {
		ChatMessageResponseDto result = null;
		try {
			if (params[0] instanceof ChatMessageDto) {
				ChatMessageDto dto = (ChatMessageDto) params[0];
				String url2call = (String) params[1];
				result = ChatAroundHttpClient
						.postSpringData(url2call, ChatMessageResponseDto.class,
								dto);
				//patch to do the trick
				//rest = "{'userList': "+ rest+"}";
				//Gson gson = new Gson();
				//result =	gson.fromJson(rest, UserPingRequestDto.class);
				if (result != null) {
					Log.i("ChatMessageResponseDto server.response",
							"Result not null");
				} else {
					Log.i("ChatMessageResponseDto server.response",
							"Result null");
				}
			}
		} catch (Exception e) {
			Log.e("Send message error.loading.datafromserver",
					"Error loading data from server", e);
		}
		return result;
	}

	@Override
	protected void onPostExecute(ChatMessageResponseDto result) {
		if (result != null) {
			ChatFragment frag = (ChatFragment) fragment;
			frag.finishTask(result);
		}
	}

}
