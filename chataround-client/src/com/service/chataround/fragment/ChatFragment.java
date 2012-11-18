package com.service.chataround.fragment;

import java.util.ArrayList;
import java.util.Calendar;

import org.springframework.util.StringUtils;

import android.app.Fragment;
import android.app.FragmentTransaction;
import android.app.ListFragment;
import android.content.Context;
import android.content.SharedPreferences;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;

import com.google.android.gcm.GCMRegistrar;
import com.service.chataround.ChatAroundActivity;
import com.service.chataround.R;
import com.service.chataround.adapter.IconListViewAdapter;
import com.service.chataround.dto.chat.ChatAroundDto;
import com.service.chataround.task.ChatAroundTask;
import com.service.chataround.util.ChatConstants;
import com.service.chataround.util.DatabaseUtils;
import com.service.chataround.util.PushUtils;

public class ChatFragment extends ListFragment implements OnClickListener {

	private IconListViewAdapter adapter;
	private ArrayList<ChatAroundDto> mFiles = new ArrayList<ChatAroundDto>();
	private Button sendButton;
	private Button goBackButton;
	private EditText textMessage;
	private String nickName;
	private String regId;

	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container,
			Bundle savedInstanceState) {
		return inflater.inflate(R.layout.messagefragment, container, false);
	}

	@Override
	public void onViewCreated(View view, Bundle savedInstanceState) {
		super.onViewCreated(view, savedInstanceState);
		regId = GCMRegistrar.getRegistrationId(getActivity());
	}

	@Override
	public void onResume() {
		super.onResume();
		mFiles = DatabaseUtils.getMessageFromToDb(getActivity());

		adapter = new IconListViewAdapter(getActivity(), R.layout.row_foro,
				mFiles);
		setListAdapter(adapter);

		sendButton = (Button) getView().findViewById(R.id.sendButton);
		sendButton.setOnClickListener(this);
		getListView().setStackFromBottom(true);

		textMessage = (EditText) getView().findViewById(R.id.textMessage);

		final SharedPreferences settings = getActivity().getSharedPreferences(
				ChatConstants.PREFS_NAME, 0);
		nickName = settings.getString(ChatConstants.USER_NICKNAME, "");

		if (!StringUtils.hasText(nickName)) {
			ChatAroundActivity chat = (ChatAroundActivity) getActivity();
			chat.settingsDialog();
		}

		goBackButton = (Button) getView().findViewById(R.id.goBackListButton);
		goBackButton.setOnClickListener(this);
	}

	@Override
	public void onClick(View v) {
		if (v.getId() == R.id.goBackListButton) {
			onButtonBClicked();
		} else {
			if (isOnline()) {
				ChatAroundDto dto = new ChatAroundDto();
				dto.setDeviceId(regId);
				dto.setMessage(textMessage.getText().toString());
				dto.setMine(true);
				dto.setNickName(nickName);
				dto.setSent(false);
				dto.setAppId(PushUtils.APP_ID);
				dto.setTime(Calendar.getInstance().getTime());

				dto = DatabaseUtils.addMessageToDb(getActivity(), dto);
				textMessage.setText("");
				// call to cloud to send message
				mFiles = DatabaseUtils.getMessageFromToDb(getActivity());

				adapter = new IconListViewAdapter(getActivity(),
						R.layout.row_foro, mFiles);

				setListAdapter(adapter);

				new ChatAroundTask(getActivity(), this).execute(dto,
						ChatConstants.SERVER_URL
								+ ChatConstants.SENDMESSAGE_URL);
			}
		}
	}

	public void finishTask(ChatAroundDto result) {
		if (result != null
				&& result.getResponse().equals(
						"push.server.operation.sendmessage.ok")) {
			DatabaseUtils.updateMessageFieldById(getActivity(),
					Long.toString(result.getId()), "SENT", "1");

			// some phones are slower to get here and get the message sooner
			// than update to sent 1.
			mFiles = DatabaseUtils.getMessageFromToDb(getActivity());
			adapter = new IconListViewAdapter(getActivity(), R.layout.row_foro,
					mFiles);
			setListAdapter(adapter);

		} else if (result != null
				&& result.getResponse().equals(
						"push.server.operation.unregister.ok")) {

			GCMRegistrar.setRegisteredOnServer(getActivity(), false);

		} else {

		}
	}

	private boolean isOnline() {
		ConnectivityManager cm = (ConnectivityManager) getActivity()
				.getSystemService(Context.CONNECTIVITY_SERVICE);
		NetworkInfo netInfo = cm.getActiveNetworkInfo();
		if (netInfo != null && netInfo.isConnectedOrConnecting()) {
			return true;
		}
		return false;
	}
	
	private void onButtonBClicked() {
		Fragment anotherFragment = Fragment.instantiate(getActivity(),
				ChatAroundListFragment.class.getName());
		FragmentTransaction ft = getFragmentManager().beginTransaction();
		ft.replace(R.id.frameLayoutId, anotherFragment);
		ft.addToBackStack(null);
		ft.commit();
		ChatAroundActivity act = (ChatAroundActivity)getActivity();
			act.getLocationListener().doStart();
	}	

}
