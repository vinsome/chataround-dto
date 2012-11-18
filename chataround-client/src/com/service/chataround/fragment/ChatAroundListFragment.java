package com.service.chataround.fragment;

import java.util.ArrayList;

import org.springframework.util.CollectionUtils;
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
import android.widget.ListView;

import com.google.android.gcm.GCMRegistrar;
import com.google.common.eventbus.Subscribe;
import com.service.chataround.ChatAroundActivity;
import com.service.chataround.R;
import com.service.chataround.adapter.UserListViewAdapter;
import com.service.chataround.dto.chat.UserPingRequestDto;
import com.service.chataround.dto.chat.UserPublicDto;
import com.service.chataround.dto.register.RegisterUserRequestDto;
import com.service.chataround.event.LocationChangeEvent;
import com.service.chataround.task.ChatAroundPingLocationTask;
import com.service.chataround.task.ChatAroundRegisterUserTask;
import com.service.chataround.util.Callback;
import com.service.chataround.util.ChatConstants;

@SuppressLint("ParserError")
public class ChatAroundListFragment extends ListFragment implements Callback {
	private UserListViewAdapter adapter;
	private ArrayList<UserPublicDto> mFiles = new ArrayList<UserPublicDto>();
	
	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		ChatAroundActivity act = (ChatAroundActivity) getActivity();
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
	}
	
	@Override
	public void onResume() {
		//mFiles = DatabaseUtils.getMessageFromToDb(getActivity());
		super.onResume();
		adapter = new UserListViewAdapter(getActivity(), R.layout.row_userlist,
				mFiles);
		setListAdapter(adapter);
	}

	@Subscribe
	public void eventLocationChanged(LocationChangeEvent event) {
		final String regId = GCMRegistrar.getRegistrationId(getActivity());
		final SharedPreferences settings = getActivity().getSharedPreferences(
				ChatConstants.PREFS_NAME, 0);
		final String nickName = settings.getString(ChatConstants.USER_NICKNAME,
				"");
		final String mood = settings.getString(ChatConstants.USER_MOOD, "");
		final String userId = settings.getString(ChatConstants.USER_ID, "");
		boolean isRegisteredToServer = settings.getBoolean(
				ChatConstants.USER_REGISTERED_ONLINE, false);

		if (!isRegisteredToServer && !StringUtils.hasText(userId)) {
			// register to server!
			RegisterUserRequestDto dto = new RegisterUserRequestDto();
			dto.setDeviceId(regId);
			dto.setEmail("email"+System.currentTimeMillis());//validating user
			dto.setLattitude(event.getLatitude().doubleValue());
			dto.setLongitude(event.getLongitude().doubleValue());
			dto.setNickName(nickName+System.currentTimeMillis());//validating nickname
			dto.setPassword("");
			dto.setStatusMessage(mood);
			// register to server
			new ChatAroundRegisterUserTask(getActivity(), this).execute(dto,
					ChatConstants.REGISTER_SERVER_URL);

		} else {

			UserPingRequestDto dto = new UserPingRequestDto();
			dto.setLattitude(event.getLatitude().doubleValue());
			dto.setLongitude(event.getLongitude().doubleValue());
			dto.setUserId(userId);

			// param based : ("lat") Double lattitude, "long") Double
			// longitude,@RequestParam("nn") String
			// nickName,@RequestParam("uid")
			new ChatAroundPingLocationTask(getActivity(), this).execute(dto,
					ChatConstants.PING_LOCATION_AND_GET_USERS_SERVER_URL);

		}

	}

	public void finishTaskRegisterUser(RegisterUserRequestDto dto) {
		if (dto!=null && StringUtils.hasText(dto.getUserId())) {
			final SharedPreferences settings = getActivity()
					.getSharedPreferences(ChatConstants.PREFS_NAME, 0);
			String userId = dto.getUserId();
			SharedPreferences.Editor editor = settings.edit();
			editor.putString(ChatConstants.USER_ID, userId);
			editor.putBoolean(ChatConstants.USER_REGISTERED_ONLINE, true);
			editor.commit();
		}
	}

	public void finishTaskPingUser(UserPingRequestDto result) {
		if(result!=null && !CollectionUtils.isEmpty(result.getUserList())){
			Log.i("ChatAroundListFragment", "size of list="+result.getUserList().size());
			adapter = new UserListViewAdapter(getActivity(), R.layout.row_userlist,
					result.getUserList());
			setListAdapter(adapter);			
		}
	}
}
