package com.wbhackathon.friendfinder.radar;
import java.util.Vector;

import android.R;
import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.graphics.Paint;
import android.graphics.Paint.Align;
import android.graphics.Paint.Style;
import android.graphics.Rect;
import android.graphics.drawable.BitmapDrawable;
import android.hardware.SensorListener;
import android.location.Location;
import android.location.LocationListener;
import android.location.LocationManager;
import android.location.LocationProvider;
import android.os.Bundle;
import android.os.SystemClock;
import android.util.AttributeSet;
import android.view.View;
import android.widget.TextView;

import com.wbhackathon.friendfinder.MapPoint;


public class RadarView extends View implements SensorListener, LocationListener {
    
	public class Target {
		
	    public double mDistance;
	    public double mBearing;
	    public float mDistanceRatio;
	    public long mSweepTime;
	    public boolean mSweepBefore;
	    public long mBlipTime;
	    public MapPoint mMapPoint;
	    public Bitmap mBlip;
	    
	    public Target() {
	    	mDistance = 0;
	    	mBearing = 0;
	    	mDistanceRatio = 1;
	    	mSweepTime = 0;
	    	mSweepBefore = false;
	    	mBlipTime = 0;
	    	
	    }
	};
	
    private static final long RETAIN_GPS_MILLIS = 10000L;

    private Paint mGridPaint;
    
    private Paint mErasePaint;

    private float mOrientation;

    private Vector<Target> mTargets = new Vector<Target>();

    public double mMyLocationLat;

    public double mMyLocationLon;
    
    private int mLastScale = 0;
    private String[] mDistanceScale = new String[4];
    
    private static float KM_PER_METERS = 0.001f;
    private static float METERS_PER_KM = 1000f;
    
    /**
     * These are the list of choices for the radius of the outer circle on the
     * screen when using metric units. All items are in kilometers. This array is 
     * used to choose the scale of the radar display.
     */
    private static double mMetricScaleChoices[] = {
    	25 * KM_PER_METERS,
    	50 * KM_PER_METERS,
        100 * KM_PER_METERS,
        200 * KM_PER_METERS,
        400 * KM_PER_METERS, 
        1, 
        2,
        4, 
        8, 
        20,
        40, 
        100, 
        200,
        400,
        1000, 
        2000,
        4000,
        10000, 
        20000,
        40000, 
        80000 };

    /**
     * Once the scale is chosen, this array is used to convert the number of
     * kilometers on the screen to an integer. (Note that for short distances we
     * use meters, so we multiply the distance by {@link #METERS_PER_KM}. (This
     * array is for metric measurements.)
     */
    private static float mMetricDisplayUnitsPerKm[] = { 
    	METERS_PER_KM, 
    	METERS_PER_KM, 
    	METERS_PER_KM, 
        METERS_PER_KM,
        METERS_PER_KM,
        METERS_PER_KM, 
        METERS_PER_KM, 
        1.0f, 
        1.0f, 
        1.0f, 
        1.0f,
        1.0f,
        1.0f, 
        1.0f, 
        1.0f,
        1.0f,
        1.0f, 
        1.0f, 
        1.0f,
        1.0f,
        1.0f };

    /**
     * This array holds the formatting string used to display the distance to
     * the target. (This array is for metric measurements.)
     */
    private static String mMetricDisplayFormats[] = {
        "%.0fm",
        "%.0fm",
        "%.0fm",
        "%.0fm",
        "%.0fm", 
        "%.0fm", 
        "%.0fm", 
        "%.1fkm", 
        "%.1fkm", 
        "%.0fkm",
        "%.0fkm",
        "%.0fkm",
        "%.0fkm",
        "%.0fkm",
        "%.0fkm", 
        "%.0fkm",
        "%.0fkm",
        "%.0fkm",
        "%.0fkm",
        "%.0fkm", 
        "%.0fkm" };
    
    /**
     * This array holds the formatting string used to display the distance on
     * each ring of the radar screen. (This array is for metric measurements.)
     */
    private static String mMetricScaleFormats[] = {
        "%.0fm",
        "%.0fm",
        "%.0fm",
        "%.0fm", 
        "%.0fm", 
        "%.0fm", 
        "%.0fm", 
        "%.0fkm", 
        "%.0fkm", 
        "%.0fkm",
        "%.0fkm",
        "%.0fkm",
        "%.0fkm", 
        "%.0fkm", 
        "%.0fkm",
        "%.0fkm",
        "%.0fkm",
        "%.0fkm", 
        "%.0fkm",
        "%.0fkm",
        "%.0fkm", 
        "%.0fkm" };
    
    private static float KM_PER_YARDS = 0.0009144f;
    private static float KM_PER_MILES = 1.609344f;
    private static float YARDS_PER_KM = 1093.6133f;
    private static float MILES_PER_KM = 0.621371192f;
    
    /**
     * These are the list of choices for the radius of the outer circle on the
     * screen when using standard units. All items are in kilometers. This array is 
     * used to choose the scale of the radar display.
     */
    private static double mEnglishScaleChoices[] = { 
        100 * KM_PER_YARDS,
        200 * KM_PER_YARDS,
        400 * KM_PER_YARDS, 
        1000 * KM_PER_YARDS, 
        1 * KM_PER_MILES, 
        2 * KM_PER_MILES, 
        4 * KM_PER_MILES, 
        8 * KM_PER_MILES, 
        20 * KM_PER_MILES, 
        40 * KM_PER_MILES, 
        100 * KM_PER_MILES, 
        200 * KM_PER_MILES, 
        400 * KM_PER_MILES, 
        1000 * KM_PER_MILES, 
        2000 * KM_PER_MILES, 
        4000 * KM_PER_MILES, 
        10000 * KM_PER_MILES, 
        20000 * KM_PER_MILES, 
        40000 * KM_PER_MILES, 
        80000 * KM_PER_MILES };

    /**
     * Once the scale is chosen, this array is used to convert the number of
     * kilometers on the screen to an integer. (Note that for short distances we
     * use meters, so we multiply the distance by {@link #YARDS_PER_KM}. (This
     * array is for standard measurements.)
     */
    private static float mEnglishDisplayUnitsPerKm[] = { 
        YARDS_PER_KM, 
        YARDS_PER_KM,
        YARDS_PER_KM,
        YARDS_PER_KM, 
        MILES_PER_KM, 
        MILES_PER_KM, 
        MILES_PER_KM, 
        MILES_PER_KM,
        MILES_PER_KM,
        MILES_PER_KM, 
        MILES_PER_KM, 
        MILES_PER_KM,
        MILES_PER_KM,
        MILES_PER_KM,
        MILES_PER_KM,
        MILES_PER_KM, 
        MILES_PER_KM, 
        MILES_PER_KM,
        MILES_PER_KM,
        MILES_PER_KM };

    /**
     * This array holds the formatting string used to display the distance to
     * the target. (This array is for standard measurements.)
     */
    private static String mEnglishDisplayFormats[] = {
        "%.0fyd",
        "%.0fyd", 
        "%.0fyd", 
        "%.0fyd", 
        "%.1fmi", 
        "%.1fmi", 
        "%.1fmi", 
        "%.1fmi",
        "%.0fmi",
        "%.0fmi",
        "%.0fmi",
        "%.0fmi",
        "%.0fmi", 
        "%.0fmi",
        "%.0fmi",
        "%.0fmi",
        "%.0fmi",
        "%.0fmi", 
        "%.0fmi",
        "%.0fmi" };
    
    /**
     * This array holds the formatting string used to display the distance on
     * each ring of the radar screen. (This array is for standard measurements.)
     */
    private static String mEnglishScaleFormats[] = {
        "%.0fyd",
        "%.0fyd", 
        "%.0fyd", 
        "%.0fyd", 
        "%.2fmi", 
        "%.1fmi", 
        "%.0fmi", 
        "%.0fmi",
        "%.0fmi",
        "%.0fmi",
        "%.0fmi",
        "%.0fmi",
        "%.0fmi",
        "%.0fmi",
        "%.0fmi",
        "%.0fmi",
        "%.0fmi",
        "%.0fmi", 
        "%.0fmi", 
        "%.0fmi" };
    
    /**
     * True when we have know our own location
     */
    private boolean mHaveLocation = true;

    /**
     * The view that will display the distance text
     */
    private TextView mDistanceView;

    /**
     * Distance to target, in KM
     */
    //private double mDistance;

    /**
     * Bearing to target, in degrees
     */
    //private double mBearing;

    /**
     * Ratio of the distance to the target to the radius of the outermost ring on the radar screen
     */
    //private float mDistanceRatio;
    
    /**
     * Utility rect for calculating the ring labels
     */
    private Rect mTextBounds = new Rect();
    
    /**
     * The bitmap used to draw the target
     */
    private Bitmap mBlip;
    private Bitmap mPOI;

    /**
     * Used to draw the animated ring that sweeps out from the center
     */
    private Paint mSweepPaint0;
    
    /**
     * Used to draw the animated ring that sweeps out from the center
     */
    private Paint mSweepPaint1;

    /**
     * Used to draw the animated ring that sweeps out from the center
     */
    private Paint mSweepPaint2;

    /**
     * Time in millis when the most recent sweep began
     */
    //private long mSweepTime;

    /**
     * True if the sweep has not yet intersected the blip
     */
    //private boolean mSweepBefore;

    /**
     * Time in millis when the sweep last crossed the blip
     */
    //private long mBlipTime;

    /**
     * True if the display should use metric units; false if the display should use standard
     * units
     */
    //private boolean mUseMetric;

    /**
     * Time in millis for the last time GPS reported a location
     */
    private long mLastGpsFixTime = 0L;

    /**
     * The last location reported by the network provider. Use this if we can't get a location from
     * GPS
     */
    private Location mNetworkLocation;

    /**
     * True if GPS is reporting a location
     */
    private boolean mGpsAvailable;

    /**
     * True if the network provider is reporting a location
     */
    private boolean mNetworkAvailable;

    public RadarView(Context context) {
        this(context, null);
    }
    
    public RadarView(Context context, AttributeSet attrs) {
        this(context, attrs, 0);
    }
    
    public RadarView(Context context, AttributeSet attrs, int defStyle) {
        super(context, attrs, defStyle);
        
        // Paint used for the rings and ring text
        mGridPaint = new Paint();
        mGridPaint.setColor(0xFF00FF00);
        mGridPaint.setAntiAlias(true);
        mGridPaint.setStyle(Style.STROKE);
        mGridPaint.setStrokeWidth(1.0f);
        mGridPaint.setTextSize(10.0f);
        mGridPaint.setTextAlign(Align.CENTER);
        
        // Paint used to erase the rectangle behing the ring text
        mErasePaint = new Paint();
        mErasePaint.setColor(0xFF191919);
        mErasePaint.setStyle(Style.FILL);
        
        // Outer ring of the sweep
        mSweepPaint0 = new Paint();
        mSweepPaint0.setColor(0xFF33FF33);
        mSweepPaint0.setAntiAlias(true);
        mSweepPaint0.setStyle(Style.STROKE);
        mSweepPaint0.setStrokeWidth(2f);
        
        // Middle ring of the sweep
        mSweepPaint1 = new Paint();
        mSweepPaint1.setColor(0x7733FF33);
        mSweepPaint1.setAntiAlias(true);
        mSweepPaint1.setStyle(Style.STROKE);
        mSweepPaint1.setStrokeWidth(2f);
        
        // Inner ring of the sweep
        mSweepPaint2 = new Paint();
        mSweepPaint2.setColor(0x3333FF33);
        mSweepPaint2.setAntiAlias(true);
        mSweepPaint2.setStyle(Style.STROKE);
        mSweepPaint2.setStrokeWidth(2f);

        mBlip = ((BitmapDrawable)getResources().getDrawable(R.drawable.ic_menu_myplaces)).getBitmap();
        mPOI = ((BitmapDrawable)getResources().getDrawable(R.drawable.ic_menu_camera)).getBitmap();

    }
    
    public void setScale(int scale) {
    	mLastScale = scale;
    	updateDistance();
    }
    
    public int getScale() {
    	return mLastScale;
    }
    
    /**
     * Sets the target to track on the radar
     * @param latE6 Latitude of the target, multiplied by 1,000,000
     * @param lonE6 Longitude of the target, multiplied by 1,000,000
     */
    /*
    public void setTarget(int latE6, int lonE6) {
        mTargetLat = latE6 / (double) GeoUtils.MILLION;
        mTargetLon = lonE6 / (double) GeoUtils.MILLION;
    }
    */
    public void clearMapPoints() {
    	mTargets.clear();
    }
    
    public void addMapPoints(Vector<MapPoint> mapPoints) {
    	for (int i=0;i<mapPoints.size(); i++) {
    		Target t = new Target();
    		t.mMapPoint = mapPoints.get(i);
    		mTargets.add(t);
    	}
    }
    
    /**
     * Sets the view that we will use to report distance
     * 
     * @param t The text view used to report distance
     */
    public void setDistanceView(TextView t) {
        mDistanceView = t;
    }

    @Override
    protected void onDraw(Canvas canvas) {
        super.onDraw(canvas);
        
        
        
        int center = getWidth() / 2;
        int radius = center - 8;

        // Draw the rings
        final Paint gridPaint = mGridPaint;
        canvas.drawCircle(center, center, radius, gridPaint);
        canvas.drawCircle(center, center, radius * 3 / 4, gridPaint);
        canvas.drawCircle(center, center, radius >> 1, gridPaint);
        canvas.drawCircle(center, center, radius >> 2, gridPaint);
        
        
        // Draw horizontal and vertical lines
        canvas.drawLine(center, center - (radius >> 2) + 6, center, center - radius - 6, gridPaint);
        canvas.drawLine(center, center + (radius >> 2) - 6 , center, center + radius + 6, gridPaint);
        canvas.drawLine(center - (radius >> 2) + 6, center, center - radius - 6, center, gridPaint); 
        canvas.drawLine(center + (radius >> 2) - 6, center, center + radius + 6, center, gridPaint); 

        // Draw X in the center of the screen
        canvas.drawLine(center - 4, center - 4, center + 4, center + 4, gridPaint);
        canvas.drawLine(center - 4, center + 4, center + 4, center - 4, gridPaint); 
        
        final long now = SystemClock.uptimeMillis();
        
        for (Target t: mTargets) {
        //Target t = mTargets.get(0);
        double la = t.mMapPoint.getLatitude();
        double lo = t.mMapPoint.getLongitude();
        
        // Hide distant targets
        if (t.mDistance > mMetricScaleChoices[mLastScale]) {
        	continue;
        }
        
	        int blipRadius = (int) (t.mDistanceRatio * radius);
	        
	        
	        //if (t.mSweepTime > 0) { // && mHaveLocation) {
	            // Draw the sweep. Radius is determined by how long ago it started
	            long sweepDifference = now - t.mSweepTime;
	            if (sweepDifference < 512L) {
	                int sweepRadius = (int) (((radius + 6) * sweepDifference) >> 9);
	                canvas.drawCircle(center, center, sweepRadius, mSweepPaint0);
	                canvas.drawCircle(center, center, sweepRadius - 2, mSweepPaint1);
	                canvas.drawCircle(center, center, sweepRadius - 4, mSweepPaint2);
	                
	                // Note when the sweep has passed the blip
	                boolean before = sweepRadius < blipRadius;
	                if (!before && t.mSweepBefore) {
	                    t.mSweepBefore = false;
	                    t.mBlipTime = now;
	                }
	            } else {
	                t.mSweepTime = now + 1000;
	                t.mSweepBefore = true;
	            }
            
	        //}
            postInvalidate();

        	
            double bearingToTarget = t.mBearing - mOrientation;
            double drawingAngle = Math.toRadians(bearingToTarget) - (Math.PI / 2);
    
            float cos = (float) Math.cos(drawingAngle);
            float sin = (float) Math.sin(drawingAngle);
    
            // Draw the text for the rings
            final String[] distanceScale = mDistanceScale;
            
            addText(canvas, distanceScale[0], center, center + (radius >> 2));
            addText(canvas, distanceScale[1], center, center + (radius >> 1));
            addText(canvas, distanceScale[2], center, center + radius * 3 / 4);
            addText(canvas, distanceScale[3], center, center + radius);
    
            // Draw the blip. Alpha is based on how long ago the sweep crossed the blip
            long blipDifference = now - t.mBlipTime;
            gridPaint.setAlpha(255 - (int)((128 * blipDifference) >> 10));
            // t.mBlip
            
            Bitmap bmp = null;
            if (t.mMapPoint.getType() == MapPoint.TYPE_USER) {
            	bmp = mBlip;
            } else {
            	bmp = mPOI;
            }
            
            canvas.drawBitmap(bmp, center + (cos * blipRadius) - 16 , 
                    center + (sin * blipRadius) - 16, gridPaint);
            gridPaint.setAlpha(255);
            addTextLarge(canvas, t.mMapPoint.getDescription(), (int)(center + (cos * blipRadius) - 16) , 
                    (int)(center + (sin * blipRadius) + 16) ) ;
            
            gridPaint.setAlpha(255);
            
            // TODO: Draw the text here
        }
    }
    
    private void addText(Canvas canvas, String str, int x, int y) {
        
        mGridPaint.getTextBounds(str, 0, str.length(), mTextBounds);
        mTextBounds.offset(x - (mTextBounds.width() >> 1), y);
        mTextBounds.inset(-2, -2);
        canvas.drawRect(mTextBounds, mErasePaint);
        canvas.drawText(str, x, y, mGridPaint);
    }
    
    private void addTextLarge(Canvas canvas, String str, int x, int y) {
        
    	mGridPaint.setTextSize(14.0f);
        mGridPaint.getTextBounds(str, 0, str.length(), mTextBounds);
        mTextBounds.offset(x - (mTextBounds.width() >> 1), y);
        mTextBounds.inset(-2, -2);
        
        canvas.drawRect(mTextBounds, mErasePaint);
        canvas.drawText(str, x, y, mGridPaint);
        mGridPaint.setTextSize(10.0f);
    }

    public void onAccuracyChanged(int sensor, int accuracy) {
    }

    /**
     * Called when we get a new value from the compass
     * 
     * @see android.hardware.SensorListener#onSensorChanged(int, float[])
     */
    public void onSensorChanged(int sensor, float[] values) {
        mOrientation = values[0];
        postInvalidate();
    }

    /**
     * Called when a location provider has a new location to report
     * 
     * @see android.location.LocationListener#onLocationChanged(android.location.Location)
     */
    public void onLocationChanged(Location location) {
        
    	/*
    	if (!mHaveLocation) {
            mHaveLocation = true;
        }
        System.out.println("Location changed!");
        final long now = SystemClock.uptimeMillis();
        boolean useLocation = false;
        final String provider = location.getProvider();
        if (LocationManager.GPS_PROVIDER.equals(provider)) {
            // Use GPS if available
            mLastGpsFixTime = SystemClock.uptimeMillis();
            useLocation = true;
        } else if (LocationManager.NETWORK_PROVIDER.equals(provider)) {
            // Use network provider if GPS is getting stale
            useLocation = now - mLastGpsFixTime > RETAIN_GPS_MILLIS;
            if (mNetworkLocation == null) {
                mNetworkLocation = new Location(location);
            } else {
                mNetworkLocation.set(location);
            }
            
            mLastGpsFixTime = 0L;
        }
        */
        
        //if (useLocation) {

            mMyLocationLat = location.getLatitude();
            mMyLocationLon = location.getLongitude();
            
            System.out.println("MyLat: " + mMyLocationLat + " MyLon: " + mMyLocationLon + " ");
            for (Target t : mTargets) {
            //Target t = mTargets.get(0);
            
	            t.mDistance = GeoUtils.distanceKm(mMyLocationLat, mMyLocationLon, t.mMapPoint.getLatitude(),
	                    t.mMapPoint.getLongitude());
	            
	            
	    
	            t.mBearing = GeoUtils.bearing(mMyLocationLat, mMyLocationLon, t.mMapPoint.getLatitude(),
	            		t.mMapPoint.getLongitude());
	            
	            System.out.println(t.mMapPoint.getLatitude() + "::" + t.mMapPoint.getLongitude() +" =("+t.mDistance + ", " + t.mBearing + ") ");
	            //System.out.print("Distance: "+t.mDistance + " bearing: "+t.mBearing);
            
	        Target t2 = mTargets.get(0);
	        //System.out.println("Delta: "+ (t2.mDistance - t.mDistance));
            }
    
            updateDistance();
        //}
        
    }

    public void onProviderDisabled(String provider) {        
    }

    public void onProviderEnabled(String provider) { 
    }

    /**
     * Called when a location provider has changed its availability.
     * 
     * @see android.location.LocationListener#onStatusChanged(java.lang.String, int, android.os.Bundle)
     */
    public void onStatusChanged(String provider, int status, Bundle extras) { 
        
        if (LocationManager.GPS_PROVIDER.equals(provider)) {
            switch (status) {
            case LocationProvider.AVAILABLE:
                mGpsAvailable = true;
                break;
            case LocationProvider.OUT_OF_SERVICE:
            case LocationProvider.TEMPORARILY_UNAVAILABLE:
                mGpsAvailable = false;
                
                if (mNetworkLocation != null && mNetworkAvailable) {
                    // Fallback to network location
                    mLastGpsFixTime = 0L;
                    onLocationChanged(mNetworkLocation);
                } else {
                    handleUnknownLocation();
                }
             
                break;
            }

        } else if (LocationManager.NETWORK_PROVIDER.equals(provider)) {
            switch (status) {
            case LocationProvider.AVAILABLE:
                mNetworkAvailable = true;
                break;
            case LocationProvider.OUT_OF_SERVICE:
            case LocationProvider.TEMPORARILY_UNAVAILABLE:
                mNetworkAvailable = false;
                
                if (!mGpsAvailable) {
                    handleUnknownLocation();
                }
                break;
            }
        }
    }
    
    /**
     * Called when we no longer have a valid lcoation.
     */
    private void handleUnknownLocation() {
        mHaveLocation = false;
        mDistanceView.setText("Scanning");   
    }
    
    
    /**
     * Update our state to reflect a new distance to the target. This may require
     * choosing a new scale for the radar rings.
     * 
     * @param distanceKm The new distance to the target
     */
    public void updateDistance() {
        final double[] scaleChoices;
        final float[] displayUnitsPerKm;
        final String[] displayFormats;
        final String[] scaleFormats;
        String distanceStr = null;

        scaleChoices = mMetricScaleChoices;
        displayUnitsPerKm = mMetricDisplayUnitsPerKm;
        displayFormats = mMetricDisplayFormats;
        scaleFormats = mMetricScaleFormats;

        
        //for (Target t : mTargets) {
        //	t.mDistanceRatio = (float) (t.mDistance / scaleChoices[mLastScale]);
        //}
        float scaleDistance = (float) (scaleChoices[mLastScale] * displayUnitsPerKm[mLastScale]);
        mDistanceScale[0] = String.format(displayFormats[mLastScale], (scaleDistance / 4));
        mDistanceScale[1] = String.format(displayFormats[mLastScale], (scaleDistance / 2));
        mDistanceScale[2] = String.format(displayFormats[mLastScale], (scaleDistance * 3 / 4));
        mDistanceScale[3] = String.format(displayFormats[mLastScale], scaleDistance);
        
        //Target t = mTargets.get(0);
        for (Target t : mTargets) {
        double distanceKm = t.mDistance;
        System.out.print(distanceKm+", ");
        int count = scaleChoices.length;
        /*
        for (int i = 0; i < count; i++) {
            if (distanceKm < scaleChoices[i] || i == (count - 1)) {
                String format = displayFormats[i];
                double distanceDisplay = distanceKm * displayUnitsPerKm[i];
                if (mLastScale != i) {
                    //mLastScale = i;
                	//mLastScale = 0;
                    String scaleFormat = scaleFormats[i];
                    scaleDistance = (float) (scaleChoices[i] * displayUnitsPerKm[i]);
                    mDistanceScale[0] = String.format(scaleFormat, (scaleDistance / 4));
                    mDistanceScale[1] = String.format(scaleFormat, (scaleDistance / 2));
                    mDistanceScale[2] = String.format(scaleFormat, (scaleDistance * 3 / 4));
                    mDistanceScale[3] = String.format(scaleFormat, scaleDistance);
                }
                
                //distanceStr = String.format(format, distanceDisplay);
                break;
            }
        }
        */
        
       
        
        //System.out.println("Distance:"+t.mDistance+" ratio:"+t.mDistanceRatio+" bearing:"+t.mBearing);
        
        
        	//t.mDistanceRatio = (float) (t.mDistance / scaleChoices[mLastScale]);
        }
        for (Target t : mTargets) {
        	t.mDistanceRatio = (float) (t.mDistance / scaleChoices[mLastScale]);
        }
        	System.out.println("---");
        //System.out.println("------");
        
        //mDistanceView.setText(distanceStr);
    }

    /**
     * Turn on the sweep animation starting with the next draw
     */
    public void startSweep() {
    	for (Target t : mTargets) {
    		t.mSweepTime = SystemClock.uptimeMillis();
        	t.mSweepBefore = true;
    	}
    }
    
    /**
     * Turn off the sweep animation
     */
    public void stopSweep() {
    	for (Target t : mTargets) {
    		t.mSweepTime = 0L;
    	}
    }
}