package com.wbhackathon.friendfinder;

public class MapPoint {

	public static final int TYPE_USER = 1;
	public static final int TYPE_POI = 2;
	
	protected double latitude;
	protected double longitude;
	private int type;
	protected String description;
	
	public MapPoint() {
		this.latitude = 0;
		this.longitude = 0;
		this.type = TYPE_USER;
		this.description = "";
	}
	
	public MapPoint(double latitude, double longitude) {
		this.latitude = latitude;
		this.longitude = longitude;
		type = TYPE_USER;
		this.description = "";
	}
	
	public MapPoint(double latitude, double longitude, int type) {
		this.latitude = latitude;
		this.longitude = longitude;
		this.type = type;
		this.description = "";
	}
	
	public double getLatitude() {
		return latitude;
	}
	
	public double getLongitude() {
		return longitude;
	}
	
	public int getType() {
		return type;
	}
	
	public String getDescription() {
		return description;
	}
}
