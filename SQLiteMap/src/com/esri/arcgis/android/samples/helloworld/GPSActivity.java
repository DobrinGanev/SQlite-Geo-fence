package com.esri.arcgis.android.samples.helloworld;

import java.beans.PropertyChangeEvent;
import java.beans.PropertyChangeListener;
import java.beans.PropertyChangeSupport;
import java.util.ArrayList;

import java.util.LinkedList;
import java.util.List;

import android.R.layout;
import android.content.Intent;
import android.content.IntentFilter;
import android.app.PendingIntent;
import model.GPSCoordinates;
import android.app.Activity;
import android.content.Context;
import android.location.Location;
import android.location.LocationListener;
import android.location.LocationManager;
import android.os.Bundle;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.ListView;
import android.widget.Toast;

public class GPSActivity extends Activity {

	private static final long MINIMUM_DISTANCE_CHANGE_FOR_UPDATES = 1; // in
	private static final String PROX_ALERT_INTENT = "suncorgps.ProximityAlert";
	private static final long MINIMUM_TIME_BETWEEN_UPDATES = 1000; // in //
																	// Milliseconds

	private static final long POINT_RADIUS = 1; // in Meters
	private static final long PROX_ALERT_EXPIRATION = -1;
	protected PropertyChangeSupport propertyChangeSupport = new PropertyChangeSupport(
			this);

	public boolean inGeofenceBoundary;
	GPSSQLiteHelper db;
	protected LocationManager locationManager;
	protected Button retrieveLocationButton;
	protected Button deleteLocationsButton;
	protected Button proximityAlertButton;
	protected ListView locListView;
	PendingIntent pi;
	ArrayAdapter<String> adapter;
	ArrayList<String> listItems = new ArrayList<String>();

	public void setInGeofenceBoundary(boolean inGeofenceBoundary) {
		boolean oldInGeofenceBoundary = this.inGeofenceBoundary;
		this.inGeofenceBoundary = inGeofenceBoundary;
		propertyChangeSupport.firePropertyChange("InGeofenceBoundary",
				oldInGeofenceBoundary, inGeofenceBoundary);
	}

	public void addPropertyChangeListener(PropertyChangeListener listener) {
		propertyChangeSupport.addPropertyChangeListener(listener);
	}

	@Override
	public void onCreate(Bundle savedInstanceState) {

		super.onCreate(savedInstanceState);
		setContentView(R.layout.conf);

		this.db = new GPSSQLiteHelper(this);
		proximityAlertButton = (Button) findViewById(R.id.proximityAlert_button);
		retrieveLocationButton = (Button) findViewById(R.id.retrieve_location_button);
		deleteLocationsButton = (Button) findViewById(R.id.delete_location_button);
		locListView = (ListView) findViewById(R.id.location_listView);

		adapter = new ArrayAdapter<String>(this,
				android.R.layout.simple_list_item_1, listItems);

		locListView.setAdapter(adapter);

		locationManager = (LocationManager) getSystemService(Context.LOCATION_SERVICE);

		locationManager.requestLocationUpdates(LocationManager.GPS_PROVIDER,
				MINIMUM_TIME_BETWEEN_UPDATES,
				MINIMUM_DISTANCE_CHANGE_FOR_UPDATES, new GPSLocationListener());

		retrieveLocationButton.setOnClickListener(new OnClickListener() {
			@Override
			public void onClick(View v) {
				SaveCurrentLocation();
			}
		});

		deleteLocationsButton.setOnClickListener(new OnClickListener() {
			@Override
			public void onClick(View v) {
				deleteAllLocations();
			}
		});
		proximityAlertButton.setOnClickListener(new OnClickListener() {
			@Override
			public void onClick(View v) {
				saveProximityAlertPoint();
			}
		});
	}

	private void saveProximityAlertPoint() {
		Location location = locationManager
				.getLastKnownLocation(LocationManager.GPS_PROVIDER);
		if (location == null) {
			Toast.makeText(this, "No last known location. Aborting...",
					Toast.LENGTH_LONG).show();
			return;
		}
		Intent email = new Intent(Intent.ACTION_SEND);
		email.putExtra(Intent.EXTRA_EMAIL,
				new String[] { "dobringanev@gmail.com" });
		email.putExtra(Intent.EXTRA_SUBJECT, "Coordinates");
		email.putExtra(Intent.EXTRA_TEXT, "Latitude:" + location.getLatitude()
				+ "\nLongitude: " + location.getLongitude());
		email.setType("message/rfc822");

		startActivity(Intent.createChooser(email, "Choose an Email client: "));
		addProximityAlert(location.getLatitude(), location.getLongitude());
		SaveCurrentLocation();
	}

	private void addProximityAlert(double latitude, double longitude) {

		Intent intent = new Intent(PROX_ALERT_INTENT);
		PendingIntent proximityIntent = PendingIntent.getBroadcast(this, 0,
				intent, 0);

		locationManager.addProximityAlert(latitude, // the latitude of the
													// central point of the
													// alert region
				longitude, // the longitude of the central point of the alert
							// region
				POINT_RADIUS, // the radius of the central point of the alert
								// region, in meters
				PROX_ALERT_EXPIRATION, // time for this proximity alert, in
										// milliseconds, or -1 to indicate no
										// expiration
				proximityIntent // will be used to generate an Intent to fire
								// when entry to or exit from the alert region
								// is detected
				);

		IntentFilter filter = new IntentFilter(PROX_ALERT_INTENT);
		registerReceiver(new ProximityReceiver(), filter);

	}

	protected void deleteAllLocations() {
		this.db.deleteAllCoordinates();
		adapter.clear();
		adapter.notifyDataSetChanged();
		Toast.makeText(GPSActivity.this, "Deleting all locations from DB",
				Toast.LENGTH_SHORT).show();
	}

	protected boolean InBoundary(Point currentLocation) {

		List<GPSCoordinates> coordinate = db.getAllCoordinates();
		Point[] points = new Point[coordinate.size()];

		for (int index = 0; index < coordinate.size(); index++) {
			points[index] = new Point();
			points[index].X = coordinate.get(index).getLatitude();
			points[index].Y = coordinate.get(index).getLongitude();
		}
		int lentgh = 4;
		PointTest[] points1 = new PointTest[lentgh];
		points1[0] = new PointTest();
		points1[1] = new PointTest();
		points1[2] = new PointTest();
		points1[3] = new PointTest();

		points1[0].X = -1.0;
		points1[0].Y = 3.0;

		points1[1].X = -1.0;
		points1[1].Y = 1.0;

		points1[2].X = -4.0;
		points1[2].Y = 1.0;

		points1[3].X = -4.0;
		points1[3].Y = 3.0;

		PointTest test = new PointTest();

		test.X = -2.0;
		test.Y = 2.0;

		boolean isInBoundary = GeofenceBoundary.PointInPolygon(currentLocation,
				points);
		return isInBoundary;
	}

	protected void SaveCurrentLocation() {

		Location location = locationManager
				.getLastKnownLocation(LocationManager.GPS_PROVIDER);

		if (location != null) {
			String message = String
					.format("Current Location \n Longitude: %1$s \n Latitude: %2$s Accuracy: %3$s metters",
							location.getLongitude(), location.getLatitude(),
							location.getAccuracy());
			listItems.add(message);
			adapter.notifyDataSetChanged();

			this.db.AddCoordinates(new GPSCoordinates(location.getLongitude(),
					location.getLatitude()));

			Toast.makeText(GPSActivity.this, message, Toast.LENGTH_SHORT)
					.show();
		}
	}

	private class GPSLocationListener implements LocationListener {

		public void onLocationChanged(Location location) {
			Point curentLocation = new Point();
			curentLocation.X = location.getLatitude();
			curentLocation.Y = location.getLongitude();

			GPSActivity notifyOnInBoundary = new GPSActivity();
			IsInBoundaryListener listener = new IsInBoundaryListener();
			notifyOnInBoundary.addPropertyChangeListener(listener);

			inGeofenceBoundary = InBoundary(curentLocation);
			String inGeofence = String.format("Entering polygon : %1$s",
					inGeofenceBoundary);

			notifyOnInBoundary.setInGeofenceBoundary(inGeofenceBoundary);

			Toast.makeText(GPSActivity.this, inGeofence, Toast.LENGTH_SHORT)
					.show();
		}

		public void onStatusChanged(String s, int i, Bundle b) {
			Toast.makeText(GPSActivity.this, "Provider status changed",
					Toast.LENGTH_LONG).show();
		}

		public void onProviderDisabled(String s) {
			Toast.makeText(GPSActivity.this, "GPS turned off",
					Toast.LENGTH_LONG).show();
		}

		public void onProviderEnabled(String s) {
			Toast.makeText(GPSActivity.this, "GPS turned on", Toast.LENGTH_LONG)
					.show();
		}
	}

	public class IsInBoundaryListener implements PropertyChangeListener {
		@Override
		public void propertyChange(PropertyChangeEvent event) {
			if (event.getPropertyName().equals("InGeofenceBoundary")) {

				Toast.makeText(GPSActivity.this,
						event.getNewValue().toString(), Toast.LENGTH_SHORT)
						.show();
			}
		}
	}

	public class PointTest {
		public double X;
		public double Y;
	}
}