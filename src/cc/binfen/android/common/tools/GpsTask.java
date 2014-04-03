package cc.binfen.android.common.tools;

import java.util.Timer;
import java.util.TimerTask;

import android.app.Activity;
import android.content.Context;
import android.location.Criteria;
import android.location.Location;
import android.location.LocationListener;
import android.location.LocationManager;
import android.os.AsyncTask;
import android.os.Bundle;

public class GpsTask extends AsyncTask{
	private GpsTaskCallBack callBk = null;
	private Activity context = null;
	private LocationManager locationManager = null;
	private LocationListener locationListener = null;
	private Location location = null;
	private boolean TIME_OUT = false;
	private boolean DATA_CONNTECTED = false;
	private long TIME_DURATION = 5000;

	public GpsTask(Activity context, GpsTaskCallBack callBk) {
		this.callBk = callBk;
		this.context = context;
		gpsInit();
	}

	public GpsTask(Activity context, GpsTaskCallBack callBk, long time_out) {
		this.callBk = callBk;
		this.context = context;
		this.TIME_DURATION = time_out;
//		this.location=location;
		gpsInit();
	}

	private void gpsInit() {
//		if(location==null){
			locationManager = (LocationManager) context
					.getSystemService(Context.LOCATION_SERVICE);
			
			locationListener = new LocationListener() {
	
				@Override
				public void onStatusChanged(String provider, int status,
						Bundle extras) {
				}
	
				@Override
				public void onProviderEnabled(String provider) {
				}
	
				@Override
				public void onProviderDisabled(String provider) {
				}
	
				@Override
				public void onLocationChanged(Location l) {
					DATA_CONNTECTED = true;
					location=l;
				}
			};
			
			locationManager.requestLocationUpdates(LocationManager.GPS_PROVIDER, 0,
					0, locationListener);
			locationManager.requestLocationUpdates(LocationManager.NETWORK_PROVIDER, 0,
					0, locationListener);
			locationManager.requestLocationUpdates(LocationManager.PASSIVE_PROVIDER, 0,
					0, locationListener);
	}

	@Override
	protected GpsData doInBackground(Object... params) {
		GpsData gpsData=null;
		while (!TIME_OUT ) {
			if(location==null){
				location=locationManager.getLastKnownLocation(LocationManager.GPS_PROVIDER);
				if(location==null){
					location=locationManager.getLastKnownLocation(LocationManager.NETWORK_PROVIDER);
					if(location==null){
						location=locationManager.getLastKnownLocation(LocationManager.PASSIVE_PROVIDER);
					}
				}
			}
			if (location != null && callBk != null) {
				gpsData = transData(location);
				break;
			}
		}
		
		return gpsData;
	}

	@Override
	protected void onPreExecute() {
		Timer timer = new Timer();
		timer.schedule(new TimerTask() {

			@Override
			public void run() {
				TIME_OUT = true;
			}
		}, TIME_DURATION);
		
		super.onPreExecute();
		
	}

	@SuppressWarnings("unchecked")
	@Override
	protected void onPostExecute(Object result) {
		locationManager.removeUpdates(locationListener);
		GpsData gpsData=(GpsData)result;
		// 获取超时
		if (TIME_OUT && callBk != null)
			callBk.gpsConnectedTimeOut();
		//获取成功
		if(gpsData!=null){
			callBk.gpsConnected(gpsData);
		}else{
			callBk.gpsConnected(gpsData);
		}
		super.onPostExecute(result);
	}

	private GpsData transData(Location location) {
		GpsData gpsData = new GpsData();
		gpsData.setLatitude(location.getLatitude());
		gpsData.setLongitude(location.getLongitude());
		return gpsData;
	}

	public static class GpsData {
		private double latitude = 0;
		private double longitude = 0;

		public double getLatitude() {
			return latitude;
		}

		public void setLatitude(double latitude) {
			this.latitude = latitude;
		}

		public double getLongitude() {
			return longitude;
		}

		public void setLongitude(double longitude) {
			this.longitude = longitude;
		}
	}
}
