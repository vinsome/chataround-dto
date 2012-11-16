package com.service.chataround.fragment;

import org.springframework.util.StringUtils;

import android.annotation.SuppressLint;
import android.app.Fragment;
import android.app.FragmentTransaction;
import android.app.ListFragment;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.ListView;

import com.google.android.gcm.GCMRegistrar;
import com.google.common.eventbus.Subscribe;
import com.service.chataround.ChatAroundActivity;
import com.service.chataround.R;
import com.service.chataround.dto.chat.ChatAroundDto;
import com.service.chataround.dto.register.RegisterUserRequestDto;
import com.service.chataround.event.LocationChangeEvent;
import com.service.chataround.task.ChatAroundTask;
import com.service.chataround.util.Callback;
import com.service.chataround.util.ChatConstants;

@SuppressLint("ParserError")
public class ChatAroundListFragment extends ListFragment implements Callback {
	

	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		ChatAroundActivity act = (ChatAroundActivity)getActivity();
		act.getEventBus().register(this);
	}

	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container,
			Bundle savedInstanceState) {
		return inflater.inflate(R.layout.listfragment, container, false);
	}

	@Override
	public void onActivityCreated(Bundle savedInstanceState) {
		super.onActivityCreated(savedInstanceState);
	}

	@Override
	public void onListItemClick(ListView l, View v, int position, long id) {
		Log.i("FragmentList", "Item clicked: " + id);
		onButtonBClicked();
	}

	public void onButtonBClicked() {
		Fragment anotherFragment = Fragment.instantiate(getActivity(),
				ChatFragment.class.getName());
		FragmentTransaction ft = getFragmentManager().beginTransaction();
		ft.replace(R.id.frameLayoutId, anotherFragment);
		ft.addToBackStack(null);
		ft.commit();
	}

	@Override
	public void onViewCreated(View view, Bundle savedInstanceState) {
		super.onViewCreated(view, savedInstanceState);
		fillData();
	}
	
	@Subscribe
	public void eventLocationChanged(LocationChangeEvent event) {
		final String regId = GCMRegistrar.getRegistrationId(getActivity());
		final SharedPreferences settings = getActivity().getSharedPreferences(ChatConstants.PREFS_NAME, 0);
		final String nickName=settings.getString(ChatConstants.USER_NICKNAME, "");
		final String mood=settings.getString(ChatConstants.USER_MOOD, "");
		boolean isRegisteredToServer=settings.getBoolean(ChatConstants.USER_REGISTERED_ONLINE,false);
		
		if(!isRegisteredToServer&& StringUtils.hasText(nickName)) {
			//register to server!
			RegisterUserRequestDto dto = new RegisterUserRequestDto();
				dto.setDeviceId(regId);
				dto.setEmail("email");
				dto.setLattitude(event.getLatitude().doubleValue());
				dto.setLongitude(event.getLongitude().doubleValue());
				dto.setNickName(nickName);
				dto.setPassword("");
				dto.setStatusMessage(mood);
				
			new ChatAroundTask(getActivity(),null).execute(dto,ChatConstants.REGISTER_SERVER_URL);
				
		}else{
			
		ChatAroundDto dto = new ChatAroundDto();
			dto.setDeviceId(regId);
			dto.setLattitude(String.valueOf(event.getLatitude()));
			dto.setLongitude(String.valueOf(event.getLongitude()));
			dto.setNickName(nickName);
			dto.setMood(mood);
		//param based : ("lat") Double lattitude, "long") Double longitude,@RequestParam("nn") String nickName,@RequestParam("uid") 
		new ChatAroundTask(getActivity(),null).execute(dto,ChatConstants.PING_LOCATION_SERVER_URL);
		
		}
		
	}
	
	private void fillData() {
		setListAdapter(new ArrayAdapter<String>(getActivity(),
				android.R.layout.simple_list_item_1, DummyData.PEOPLE));

	}	
	
}
