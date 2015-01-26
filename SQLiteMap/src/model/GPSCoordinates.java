package model;

public class GPSCoordinates {

	private int id;
	private double longitude;
	private double latitude;

	public GPSCoordinates() {
	}

	public GPSCoordinates(double longitude, double latitude) {
		super();
		this.longitude = longitude;
		this.latitude = latitude;
	}

	public int getId() {
		return id;
	}

	public void setId(int id) {
		this.id = id;
	}

	public double getLongitude() {
		return longitude;
	}

	public void setLongitude(double longitude) {
		this.longitude = longitude;
	}

	public double getLatitude() {
		return latitude;
	}

	public void setLatitude(double latitude) {
		this.latitude = latitude;
	}

	@Override
	public String toString() {
		return "GPSCoordinates id=" + id + ", Longitude =" + longitude
				+ ", Latitude =" + latitude;
	}

}
