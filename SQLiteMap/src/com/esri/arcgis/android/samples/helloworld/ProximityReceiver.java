package com.esri.arcgis.android.samples.helloworld;

import android.R;
import android.app.Notification;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.graphics.Color;
import android.location.LocationManager;
import android.util.Log;
import android.widget.Toast;
import android.app.Activity;
public class ProximityReceiver extends BroadcastReceiver {

	private static final int NOTIFICATION_ID = 1000;

	@Override
	public void onReceive(Context context, Intent intent) {

		String key = LocationManager.KEY_PROXIMITY_ENTERING;

		Boolean entering = intent.getBooleanExtra(key, false);

		if (entering) {
			Log.d(getClass().getSimpleName(), "entering");
			Toast.makeText(context,"entering area",
					Toast.LENGTH_LONG).show();
							
		} else {
			Toast.makeText(context,"exiting area ",
					Toast.LENGTH_LONG).show();
			Log.d(getClass().getSimpleName(), "exiting");
		}
	}
}
