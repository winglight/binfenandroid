package cc.binfen.android.common.tools;

import cc.binfen.android.common.tools.GpsTask.GpsData;

/**
 * @author sunny
 * 通过gps获取经纬度的回调接口
 */
public interface GpsTaskCallBack {
	
	public void gpsConnected(GpsData gpsdata);
	
	public void gpsConnectedTimeOut();
}
