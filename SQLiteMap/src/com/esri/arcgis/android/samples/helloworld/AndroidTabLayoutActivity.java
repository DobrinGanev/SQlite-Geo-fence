package com.esri.arcgis.android.samples.helloworld;


import android.app.TabActivity;
import android.content.Intent;
import android.os.Bundle;
import android.widget.TabHost;
import android.widget.TabHost.TabSpec;
 
public class AndroidTabLayoutActivity extends TabActivity {
    /** Called when the activity is first created. */
    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.main);
         
        TabHost tabHost = getTabHost();
         
        // Tab for Photos
        TabSpec configurationSection = tabHost.newTabSpec("Configuration");
        
        // setting Title and Icon for the Tab
       configurationSection.setIndicator("", getResources().getDrawable(R.drawable.applications_internet));
        Intent photosIntent = new Intent(this, OnlineMap.class);
        configurationSection.setContent(photosIntent);
                 
        // Tab for Songs
        TabSpec songspec = tabHost.newTabSpec("Map");       
        songspec.setIndicator("", getResources().getDrawable(R.drawable.applications_system));
        Intent songsIntent = new Intent(this, GPSActivity.class);
        songspec.setContent(songsIntent);
         
           
        // Adding all TabSpec to TabHost
        tabHost.addTab(configurationSection); // Adding photos tab
        tabHost.addTab(songspec); // Adding songs tab

    }
}