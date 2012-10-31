package com.service.chataround.fragment;

import java.util.ArrayList;
import java.util.Calendar;

import android.app.Dialog;
import android.app.ListFragment;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;

import com.service.chataround.ChatAroundActivity;
import com.service.chataround.R;
import com.service.chataround.adapter.IconListViewAdapter;
import com.service.chataround.dto.chat.ChatAroundDto;
import com.service.chataround.util.Constants;
import com.service.chataround.util.DatabaseUtils;

public class ChatFragment extends ListFragment implements OnClickListener {
	
	private IconListViewAdapter adapter;
	private ArrayList<ChatAroundDto> mFiles = new ArrayList<ChatAroundDto>();
	private Button sendButton;
	private EditText textMessage;
	private String nickName;
	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container,
			Bundle savedInstanceState) {
		return inflater.inflate(R.layout.messagefragment, container, false);
	}
	
	@Override
	public void onViewCreated(View view, Bundle savedInstanceState) {
		super.onViewCreated(view, savedInstanceState);
		adapter = new IconListViewAdapter(getActivity(), R.layout.row_foro, mFiles);
		setListAdapter(adapter);
		
		sendButton = (Button)getView().findViewById(R.id.sendButton);
			   sendButton.setOnClickListener(this);
		getListView().setStackFromBottom(true);
		
		textMessage = (EditText)getView().findViewById(R.id.textMessage);
		
		final SharedPreferences settings = getActivity().getSharedPreferences(Constants.PREFS_NAME, 0);
		nickName = settings.getString(Constants.USER_NICKNAME, "");		
	}
	
	@Override
	public void onClick(View v) {
		
		ChatAroundDto dto = new ChatAroundDto();
			dto.setDeviceId("deviceId");
			dto.setMessage(textMessage.getText().toString());
			dto.setMine(true);
			dto.setNickName(nickName);
			dto.setSent(false);
			dto.setTime(Calendar.getInstance().getTime());
			
			dto = DatabaseUtils.addMessageToDb(getActivity(), dto);
			
			
			//call to cloud to send message
			
			mFiles = DatabaseUtils.getMessageFromToDb(getActivity());
			
			adapter = new IconListViewAdapter(getActivity(),
					R.layout.row_foro, mFiles);
			
			setListAdapter(adapter);
	
	}
}
