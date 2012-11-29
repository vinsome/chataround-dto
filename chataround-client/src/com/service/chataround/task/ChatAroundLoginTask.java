package com.service.chataround.task;

import android.app.Fragment;
import android.app.ProgressDialog;
import android.content.Context;
import android.os.AsyncTask;
import android.util.Log;

import com.service.chataround.ChatAroundSettingActivity;
import com.service.chataround.R;
import com.service.chataround.dto.chat.LoginDto;
import com.service.chataround.dto.chat.UserPublicDto;
import com.service.chataround.util.ChatAroundHttpClient;

public class ChatAroundLoginTask extends AsyncTask<Object, Integer, UserPublicDto> {
	public static String TAG = ChatAroundRegisterUserTask.class.getName();
	protected final Context mContext;
	protected final Fragment fragment;
	private ProgressDialog dialog;
	public ChatAroundLoginTask(Context ctx, Fragment fragment) {
		this.mContext = ctx;
		this.fragment = fragment;
		dialog = new ProgressDialog(mContext);
	}

	@Override
	protected void onPreExecute() {
		this.dialog.setMessage(mContext.getString(R.string.progress_login_message));
        this.dialog.show();
	}

	@Override
	protected UserPublicDto doInBackground(Object... params) {
		UserPublicDto result = null;
		try {
			if (params[0] instanceof LoginDto) {
				LoginDto dto = (LoginDto) params[0];
					String url2call = (String) params[1];
					result = ChatAroundHttpClient.postSpringData(url2call, UserPublicDto.class, dto);
					//all ok
					
			}
		} catch (Exception e) {
			Log.e("validateUser error.loading.datafromserver",
					"Error loading data from server", e);
		}
		return result;
	}

	@Override
	protected void onPostExecute(UserPublicDto result) {
		if (dialog.isShowing()) {
            dialog.dismiss();
        }
		ChatAroundSettingActivity frag = (ChatAroundSettingActivity)mContext;
			frag.finishTaskLoginUser(result);			
	}

}
