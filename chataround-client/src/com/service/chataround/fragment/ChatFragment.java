package com.service.chataround.fragment;

import java.util.ArrayList;

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

import com.google.common.eventbus.Subscribe;
import com.service.chataround.ChatAroundActivity;
import com.service.chataround.R;
import com.service.chataround.adapter.IconListViewAdapter;
import com.service.chataround.dto.chat.ChatMessageDto;
import com.service.chataround.dto.chat.ChatMessageResponseDto;
import com.service.chataround.task.ChatAroundSendMessageTask;
import com.service.chataround.util.ChatConstants;
import com.service.chataround.util.DatabaseUtils;
import com.service.chataround.util.PushUtils;

public class ChatFragment extends ListFragment implements OnClickListener {

	private IconListViewAdapter adapter;
	private ArrayList<ChatMessageDto> mFiles = new ArrayList<ChatMessageDto>();
	private Button sendButton;
	private Button goBackButton;
	private EditText textMessage;
	private String nickName;
	//private String regId;
	private String userId;

	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		ChatAroundActivity act = (ChatAroundActivity) getActivity();
		act.getEventBus().register(this);
		act.setFragmentPresent(ChatFragment.class.getName());
	}
	
	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container,
			Bundle savedInstanceState) {
		return inflater.inflate(R.layout.messagefragment, container, false);
	}

	@Override
	public void onViewCreated(View view, Bundle savedInstanceState) {
		super.onViewCreated(view, savedInstanceState);
		//regId = GCMRegistrar.getRegistrationId(getActivity());
		final SharedPreferences settings = getActivity().getSharedPreferences(
				ChatConstants.PREFS_NAME, 0);
		userId = settings.getString(ChatConstants.USER_ID, "");		
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
			String recipientId = ((ChatAroundActivity)getActivity()).getRecipientId();
			if (isOnline() && recipientId!=null ) {
				ChatMessageDto dto = new ChatMessageDto();
				//MY user id
				dto.setSenderId(userId);
				
				//Recipient to whom I want to seend the message
				dto.setRecipientId(recipientId);
				
				dto.setMessage(textMessage.getText().toString());

				dto.setMine(true);
				dto.setNickName(nickName);
				
				dto.setAppId(PushUtils.APP_ID);
				dto.setTime(null);

				dto = DatabaseUtils.addMessageToDb(getActivity(), dto);
				textMessage.setText("");
				// call to cloud to send message
				mFiles = DatabaseUtils.getMessageFromToDb(getActivity());

				adapter = new IconListViewAdapter(getActivity(),
						R.layout.row_foro, mFiles);

				setListAdapter(adapter);

				new ChatAroundSendMessageTask(getActivity(), this).execute(dto,
						ChatConstants.SEND_MESSAGE_USER_SERVER_URL);
			}
		}
	}

	public void finishTask(ChatMessageResponseDto result) {
		if (result != null
				&& !StringUtils.hasText(result.getServerMessage())) {
			
			// DatabaseUtils.updateMessageFieldById(getActivity(),Long.toString(result.getId()), "SENT", "1");

			// some phones are slower to get here and get the message sooner
			// than update to sent 1.
			//mFiles = DatabaseUtils.getMessageFromToDb(getActivity());
			//adapter = new IconListViewAdapter(getActivity(), R.layout.row_foro,mFiles);
			//setListAdapter(adapter);

		} else {

		}
	}
	
	@Subscribe
	public void receiveMessageFromCloud(ChatMessageDto dto){
		mFiles = DatabaseUtils.getMessageFromToDb(getActivity());
		adapter = new IconListViewAdapter(getActivity(), R.layout.row_foro,mFiles);
		setListAdapter(adapter);
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
			act.setRecipientId(null);
			act.getLocationListener().doStart();
	}	

}
