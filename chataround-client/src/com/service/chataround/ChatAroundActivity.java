package com.service.chataround;

import android.app.Activity;
import android.app.Fragment;
import android.app.FragmentTransaction;
import android.content.Context;
import android.location.LocationManager;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;

import com.service.chataround.fragment.ChatAroundListFragment;
import com.service.chataround.listener.MyLocationListener;

public class ChatAroundActivity extends Activity {
	
    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_chat_around);
        
        Fragment frg = Fragment.instantiate(this, ChatAroundListFragment.class.getName());
        FragmentTransaction ft = getFragmentManager().beginTransaction();
        ft.add(R.id.frameLayoutId, frg);
        ft.addToBackStack(null);
        ft.commit();
        //getActionBar().setDisplayHomeAsUpEnabled(true);

        /*
        LocationResult locationResult = new LocationResult(){
            @Override
            public void gotLocation(Location location){
                //Got the location!
            }
        };
        MyLocation myLocation = new MyLocation();
        myLocation.getLocation(this, locationResult);
        */
        //LocationManager mlocManager = (LocationManager)getSystemService(Context.LOCATION_SERVICE);
        //LocationListener mlocListener = new MyLocationListener();
        //mlocManager.requestLocationUpdates( LocationManager.GPS_PROVIDER, 0, 0, mlocListener);
     // Create the list fragment and add it as our sole content.
        
       // if (getFragmentManager().findFragmentById(android.R.id.content) == null) {
       //     ArrayListFragment list = new ArrayListFragment();
        //    getFragmentManager().beginTransaction().add(android.R.id.content, list).commit();
       // }
        
    }
    
    @Override
    public void onResume(){
    	super.onResume();
        LocationManager locationManager = (LocationManager) getSystemService(Context.LOCATION_SERVICE);
        MyLocationListener locationListener = new MyLocationListener(locationManager,getApplicationContext());
        locationListener.start();    	
    	
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.activity_chat_around, menu);
        return true;
    }

    
    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case android.R.id.home:
                //NavUtils.navigateUpFromSameTask(this);
                return true;
        }
        return super.onOptionsItemSelected(item);
    }
    
}
