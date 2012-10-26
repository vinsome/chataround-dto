package com.service.chataround.fragment;

import android.app.Fragment;
import android.app.FragmentTransaction;
import android.app.ListFragment;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.ListView;

import com.service.chataround.R;
import com.service.chataround.util.Callback;

public class ChatAroundListFragment extends ListFragment implements Callback{

    //private ScheduleDBAdapter mDBHelper;
	@Override
	public void onCreate(Bundle savedInstanceState) {
	    super.onCreate(savedInstanceState);
	    
	}
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
            Bundle savedInstanceState) {
        return inflater.inflate(R.layout.fragment1, container, false);
    }

    @Override
    public void onActivityCreated(Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
        //mDBHelper = new ScheduleDBAdapter(getActivity());
        //mDBHelper.open();
        fillData();
    }
    
    @Override
	public void onListItemClick(ListView l, View v, int position, long id) {
		Log.i("FragmentList", "Item clicked: " + id);
		onButtonBClicked();
	}
    
    private void fillData() {
        //Cursor jobsCursor = mDBHelper.fetchAllJobs();
    //    getActivity().startManagingCursor(jobsCursor);
     //   String[] from = new String[] { ScheduleDBAdapter.JOB_NUMBER,
      //          ScheduleDBAdapter.JOB_PART };
       // int[] to = new int[] { R.id.ListItem1, R.id.ListItem2 };
       // SimpleCursorAdapter jobs = new SimpleCursorAdapter(getActivity(),
       //         R.layout.listlayoutdouble, jobsCursor, from, to);
      //  setListAdapter(jobs);
     setListAdapter(new ArrayAdapter<String>(getActivity(),android.R.layout.simple_list_item_1, Shakespeare.TITLES));
    }
    
    @Override
    public void onButtonBClicked() {
        Fragment anotherFragment = Fragment.instantiate(getActivity(), ChatFragment.class.getName());
        FragmentTransaction ft = getFragmentManager().beginTransaction();
        ft.add(R.layout.chatfragment, anotherFragment);
        ft.addToBackStack(null);
        ft.commit();
    }
}
