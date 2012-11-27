package com.service.chataround.fragment;

import java.util.ArrayList;
import java.util.List;

import org.springframework.util.CollectionUtils;
import org.springframework.util.StringUtils;

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

import com.google.android.apps.analytics.GoogleAnalyticsTracker;
import com.google.android.gcm.GCMRegistrar;
import com.google.common.eventbus.Subscribe;
import com.service.chataround.ChatAroundActivity;
import com.service.chataround.R;
import com.service.chataround.adapter.UserListViewAdapter;
import com.service.chataround.dto.chat.UserPingRequestDto;
import com.service.chataround.dto.chat.UserPingResponseDto;
import com.service.chataround.dto.chat.UserPublicDto;
import com.service.chataround.dto.register.RegisterUserRequestDto;
import com.service.chataround.event.LocationChangeEvent;
import com.service.chataround.task.ChatAroundPingLocationTask;
import com.service.chataround.util.Callback;
import com.service.chataround.util.ChatUtils;

public class ChatAroundListFragment extends ListFragment implements Callback {
	public static String TAG = ChatAroundListFragment.class.getName();
	private UserListViewAdapter adapter;
	private List<UserPublicDto> mFiles = new ArrayList<UserPublicDto>();
	private GoogleAnalyticsTracker tracker;

	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		ChatAroundActivity act = (ChatAroundActivity) getActivity();
		act.getEventBus().register(this);
		act.setFragmentPresent(ChatAroundListFragment.class.getName());
		tracker = GoogleAnalyticsTracker.getInstance();
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

		UserPublicDto userSelected = (UserPublicDto) l.getAdapter().getItem(
				Long.valueOf(id).intValue());
		ChatAroundActivity chat = (ChatAroundActivity) getActivity();
		//the one we are talking to
		chat.setRecipientId(userSelected.getUserId());
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
		super.onResume();
		tracker.trackPageView("/" + TAG);
		ChatAroundActivity act = (ChatAroundActivity)getActivity();
			if(!CollectionUtils.isEmpty(act.getCacheList()));
			mFiles=act.getCacheList();
		adapter = new UserListViewAdapter(getActivity(), R.layout.row_userlist,
				mFiles);
		setListAdapter(adapter);
	}

	@Subscribe
	public void eventLocationChanged(LocationChangeEvent event) {
		final SharedPreferences settings = getActivity().getSharedPreferences(
				ChatUtils.PREFS_NAME, 0);
		final String nickName = settings.getString(ChatUtils.USER_NICKNAME, "");
		final String userId = settings.getString(ChatUtils.USER_ID, "");
		final String email = settings.getString(ChatUtils.USER_EMAIL, "");
		final String password = settings.getString(ChatUtils.USER_EMAIL, "");

		boolean isRegisteredToServer = settings.getBoolean(
				ChatUtils.USER_REGISTERED_ONLINE, false);

		if (!isRegisteredToServer && nickName!=null && !"".equals(nickName) && (userId==null || "".equals(userId)) 
				&& password!=null && !"".equals(password)&&email!=null && !"".equals(email)) {
			// register to server!
				
		} else if(isRegisteredToServer && userId!=null && !"".equals(userId)) {
			pingToServerAndGetUsers(userId, event);
		} else {
			//some settings missing
		}

	}

	private void pingToServerAndGetUsers(String userId,
			LocationChangeEvent event) {
		UserPingRequestDto dto = new UserPingRequestDto();
		dto.setLattitude(event.getLatitude().doubleValue());
		dto.setLongitude(event.getLongitude().doubleValue());
		dto.setUserId(userId);

		// param based : ("lat") Double lattitude, "long") Double
		// longitude,@RequestParam("nn") String
		// nickName,@RequestParam("uid")
		new ChatAroundPingLocationTask(getActivity(), this).execute(dto,
				ChatUtils.PING_LOCATION_AND_GET_USERS_SERVER_URL);
	}

	public void finishTaskRegisterUser(RegisterUserRequestDto dto) {
		if (dto != null && StringUtils.hasText(dto.getUserId())) {
			final SharedPreferences settings = getActivity()
					.getSharedPreferences(ChatUtils.PREFS_NAME, 0);
			String userId = dto.getUserId();
			SharedPreferences.Editor editor = settings.edit();
			editor.putString(ChatUtils.USER_ID, userId);
			editor.putBoolean(ChatUtils.USER_REGISTERED_ONLINE, true);
			editor.commit();
		}
	}

	public void finishTaskPingUser(UserPingResponseDto result) {
		if (result != null && !CollectionUtils.isEmpty(result.getUserList())) {
			Log.i("ChatAroundListFragment", "size of list="
					+ result.getUserList().size());
			List<UserPublicDto> cacheList = result.getUserList();
			adapter = new UserListViewAdapter(getActivity(),
					R.layout.row_userlist, cacheList);
			setListAdapter(adapter);
			ChatAroundActivity act = (ChatAroundActivity) getActivity();
			act.setCacheList(cacheList);
		}
	}
}
