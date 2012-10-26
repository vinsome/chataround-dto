package com.service.chataround.fragment;

import android.app.ListFragment;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.ListView;

public class ArrayListFragment extends ListFragment {

	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onActivityCreated(savedInstanceState);
		//ListView peopleListView =  (ListView) getActivity().findViewById(R.id.peopleChatAroundListId);
		
		//ArrayAdapter<String> adapter = new ArrayAdapter<String>(getActivity().getApplicationContext(),R.id.peopleChatAroundListId,Shakespeare.TITLES);
			
		setListAdapter(new ArrayAdapter<String>(getActivity(),android.R.layout.simple_list_item_1, Shakespeare.TITLES));
		
		//setListAdapter(adapter);
	}

	@Override
	public void onListItemClick(ListView l, View v, int position, long id) {
		Log.i("FragmentList", "Item clicked: " + id);
	}
}
