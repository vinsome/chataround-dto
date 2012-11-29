package com.service.chataround.task;

import android.app.Fragment;
import android.app.ProgressDialog;
import android.content.Context;
import android.os.AsyncTask;
import android.util.Log;

import com.service.chataround.ChatAroundSettingActivity;
import com.service.chataround.R;
import com.service.chataround.dto.chat.UserStatusUpdateDto;
import com.service.chataround.dto.chat.UserStatusUpdateResponseDto;
import com.service.chataround.util.ChatAroundHttpClient;

public class ChatAroundMoodTask extends AsyncTask<Object, Integer, UserStatusUpdateResponseDto> {
	public static String TAG = ChatAroundRegisterUserTask.class.getName();
	protected final Context mContext;
	protected final Fragment fragment;
	private ProgressDialog dialog;
	
	public ChatAroundMoodTask(Context ctx, Fragment fragment) {
		this.mContext = ctx;
		this.fragment = fragment;
		dialog = new ProgressDialog(mContext);
	}

	@Override
	protected void onPreExecute() {
		this.dialog.setMessage(mContext.getString(R.string.progress_updatestatus_message));
        this.dialog.show();
	}

	@Override
	protected UserStatusUpdateResponseDto doInBackground(Object... params) {
		UserStatusUpdateResponseDto result = null;
		try {
			if (params[0] instanceof UserStatusUpdateDto) {
				UserStatusUpdateDto dto = (UserStatusUpdateDto) params[0];
					String url2call = (String) params[1];
					result = ChatAroundHttpClient.postSpringData(url2call, UserStatusUpdateResponseDto.class, dto);
					//all ok
					
			}
		} catch (Exception e) {
			Log.e("validateUser error.loading.datafromserver",
					"Error loading data from server", e);
		}
		return result;
	}

	@Override
	protected void onPostExecute(UserStatusUpdateResponseDto result) {
		if (dialog.isShowing()) {
            dialog.dismiss();
        }
		ChatAroundSettingActivity frag = (ChatAroundSettingActivity)mContext;
			frag.finishTaskUpdateUserStatus(result);			
	}

}
