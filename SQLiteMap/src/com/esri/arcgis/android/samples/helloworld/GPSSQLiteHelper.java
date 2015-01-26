package com.esri.arcgis.android.samples.helloworld;

import java.util.LinkedList;
import java.util.List;

import model.GPSCoordinates;
import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.util.Log;

public class GPSSQLiteHelper extends SQLiteOpenHelper {

	// Database Version
	private static final int DATABASE_VERSION = 1;
	// Database Name
	private static final String DATABASE_NAME = "CoordinatesDB";

	public GPSSQLiteHelper(Context context) {
		super(context, DATABASE_NAME, null, DATABASE_VERSION);
		// context.deleteDatabase(DATABASE_NAME);
	}

	@Override
	public void onCreate(SQLiteDatabase db) {

		// SQL statement to create Coordinates table
		String CREATE_Coordinates_TABLE = "CREATE TABLE Coordinates ( "
				+ "id INTEGER PRIMARY KEY AUTOINCREMENT, "
				+ "Longitude DOUBLE, " + "Latitude DOUBLE)";

		// create Coordinates table
		db.execSQL(CREATE_Coordinates_TABLE);
	}

	@Override
	public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
		// Drop older Coordinates table if existed
		db.execSQL("DROP TABLE IF EXISTS Coordinates");

		// create Coordinates table
		this.onCreate(db);
	}

	// ---------------------------------------------------------------------

	
	private static final String TABLE_COORDINATES = "coordinates";


	private static final String KEY_ID = "id";
	private static final String KEY_Longitude = "Longitude";
	private static final String KEY_Latitude = "Latitude";

	private static final String[] COLUMNS = { KEY_ID, KEY_Longitude,
			KEY_Latitude };

	public void AddCoordinates(GPSCoordinates gPSCoordinates) {
		Log.d("addCoordinates", gPSCoordinates.toString());
		// 1. get reference to writable DB
		SQLiteDatabase db = this.getWritableDatabase();

		// 2. create ContentValues to add key "column"/value
		ContentValues values = new ContentValues();
		values.put(KEY_Longitude, gPSCoordinates.getLongitude());
		values.put(KEY_Latitude, gPSCoordinates.getLatitude());

		// 3. insert
		db.insert(TABLE_COORDINATES, // table
				null, // nullColumnHack
				values); // key/value -> keys = column names/ values = column
							// values

		// 4. close
		db.close();
	}

	public GPSCoordinates getLocation(int id) {

		// 1. get reference to readable DB
		SQLiteDatabase db = this.getReadableDatabase();

		// 2. build query
		Cursor cursor = db.query(TABLE_COORDINATES, // a. table
				COLUMNS, // b. column names
				" id = ?", // c. selections
				new String[] { String.valueOf(id) }, // d. selections args
				null, // e. group by
				null, // f. having
				null, // g. order by
				null); // h. limit

		// 3. if we got results get the first one
		if (cursor != null)
			cursor.moveToFirst();

		// 4. build book object
		GPSCoordinates gPSCoordinates = new GPSCoordinates();
		gPSCoordinates.setId(Integer.parseInt(cursor.getString(0)));
		gPSCoordinates.setLongitude(cursor.getFloat(1));
		gPSCoordinates.setLatitude(cursor.getFloat(2));

		Log.d("getLocation(" + id + ")", gPSCoordinates.toString());

		// 5. return book
		return gPSCoordinates;
	}

	// Get All GPSCoordinates
	public List<GPSCoordinates> getAllCoordinates() {
		List<GPSCoordinates> gPSCoordinates = new LinkedList<GPSCoordinates>();

		// 1. build the query
		String query = "SELECT * FROM " + TABLE_COORDINATES;

		// 2. get reference to writable DB
		SQLiteDatabase db = this.getWritableDatabase();
		Cursor cursor = db.rawQuery(query, null);

		// 3. go over each row, build coordinates and add it to list
		GPSCoordinates coordinates = null;
		if (cursor.moveToFirst()) {
			do {
				coordinates = new GPSCoordinates();
				coordinates.setId(Integer.parseInt(cursor.getString(0)));
				coordinates.setLongitude(cursor.getDouble(1));
				coordinates.setLatitude(cursor.getDouble(2));

				gPSCoordinates.add(coordinates);
			} while (cursor.moveToNext());
		}
		if (coordinates != null) {
			Log.d("getAllCoordinates()", coordinates.toString());
		}
		return gPSCoordinates;
	}

	// Updating single coordinate
	public int updateCoordinates(GPSCoordinates gPSCoordinates) {

		// 1. get reference to writable DB
		SQLiteDatabase db = this.getWritableDatabase();

		// 2. create ContentValues to add key "column"/value
		ContentValues values = new ContentValues();
		values.put("Longitude", gPSCoordinates.getLongitude()); // get Longitude
		values.put("Latitude", gPSCoordinates.getLatitude()); // get Latitude

		// 3. updating row
		int i = db.update(TABLE_COORDINATES, // table
				values, // column/value
				KEY_ID + " = ?", // selections
				new String[] { String.valueOf(gPSCoordinates.getId()) });

		// 4. close
		db.close();

		return i;
	}

	// Deleting single Coordinate
	public void deleteCoordinate(GPSCoordinates gPSCoordinates) {

		// 1. get reference to writable DB
		SQLiteDatabase db = this.getWritableDatabase();

		// 2. delete
		db.delete(TABLE_COORDINATES, KEY_ID + " = ?",
				new String[] { String.valueOf(gPSCoordinates.getId()) });

		db.close();

		Log.d("TABLE_COORDINATES", gPSCoordinates.toString());
	}

	// Deleting all Coordinates
	public void deleteAllCoordinates() {

		// 1. get reference to writable DB
		SQLiteDatabase db = this.getWritableDatabase();

		// 2. delete
		db.delete(TABLE_COORDINATES, null, null);

		// 3. close
		db.close();

		Log.d("TABLE_COORDINATES", "deleted all records");

	}

}