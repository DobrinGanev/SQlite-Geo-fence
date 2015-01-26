
package com.esri.arcgis.android.samples.helloworld;

import java.util.ArrayList;
import java.util.List;

import model.GPSCoordinates;
import android.app.Activity;
import android.database.Cursor;
import android.graphics.Color;
import android.net.Uri;
import android.os.Bundle;
import android.util.Log;
import android.view.MotionEvent;
import android.widget.Toast;
import android.app.TabActivity;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.widget.TabHost;
import android.widget.TabHost.TabSpec;

import com.esri.android.map.GraphicsLayer;
import com.esri.android.map.MapOnTouchListener;
import com.esri.android.map.MapView;
import com.esri.android.map.ags.ArcGISTiledMapServiceLayer;
import com.esri.core.geometry.GeometryEngine;
import com.esri.core.geometry.MultiPath;
import com.esri.core.geometry.Point;
import com.esri.core.geometry.Polygon;
import com.esri.core.geometry.Polyline;
import com.esri.core.geometry.SpatialReference;
import com.esri.core.map.Graphic;
import com.esri.core.symbol.SimpleFillSymbol;
import com.esri.core.symbol.SimpleLineSymbol;
import com.esri.core.symbol.SimpleMarkerSymbol;
import com.esri.core.symbol.SimpleMarkerSymbol.STYLE;


public class OnlineMap extends Activity {
	MapView mMapView = null;
	GPSSQLiteHelper db;
	ArcGISTiledMapServiceLayer tileLayer;
	GraphicsLayer graphicsLayer = null;
	MyTouchListener myListener = null;
	int spreference = SpatialReference.WKID_WGS84_WEB_MERCATOR;

	/** Called when the activity is first created. */
	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.map_layout);
		graphicsLayer = new GraphicsLayer();
		this.db = new GPSSQLiteHelper(this);

		mMapView = (MapView) findViewById(R.id.map);

		myListener = new MyTouchListener(OnlineMap.this, mMapView);
		mMapView.setOnTouchListener(myListener);

		tileLayer = new ArcGISTiledMapServiceLayer(
				"http://services.arcgisonline.com/ArcGIS/rest/services/World_Street_Map/MapServer");
		// Add tiled layer to MapView
		mMapView.addLayer(tileLayer);

		mMapView.addLayer(graphicsLayer);
	}

	class MyTouchListener extends MapOnTouchListener {
		
		public MyTouchListener(Context context, MapView view) {
			super(context, view);
		}
		
		public boolean onSingleTap(MotionEvent e) {
		 ArrayList<Point> polylinePoints = new ArrayList<Point>();
		 
	
			graphicsLayer.removeAll();
			
			SimpleMarkerSymbol resultSymbol1 = new SimpleMarkerSymbol(Color.RED,
					10, STYLE.CIRCLE);
			Point madpPoint = (Point) GeometryEngine.project(
					-114.058101,51.045325 ,mMapView.getSpatialReference());
			
							
			List<GPSCoordinates> coordinate = db.getAllCoordinates();

			for (int index = 0; index < coordinate.size(); index++) {

				Point mapPoint = (Point) GeometryEngine.project(
						coordinate.get(index).getLongitude(), coordinate.get(index)
								.getLatitude(),mMapView.getSpatialReference());
				polylinePoints.add(mapPoint);
				
				SimpleMarkerSymbol resultSymbol = new SimpleMarkerSymbol(Color.RED,
						10, STYLE.CIRCLE);
				
				Graphic resultLocation = new Graphic(mapPoint, resultSymbol);
				graphicsLayer.addGraphic(resultLocation);
			}
			return true;
		}
	}

	@Override
	protected void onPause() {
		super.onPause();
		mMapView.pause();
	}

	@Override
	protected void onResume() {
		super.onResume();
		mMapView.unpause();
	}

}