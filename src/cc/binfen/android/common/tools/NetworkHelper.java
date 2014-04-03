package cc.binfen.android.common.tools;

import android.content.Context;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;

/**
 * 检查网络
 * @author sunny
 */
public class NetworkHelper{
	private final Context context;
	private static NetworkHelper instance;
	
	public NetworkHelper(Context c){
		this.context=c;
	}
	
	public static NetworkHelper getInstance(Context c){
		if(instance==null){
			instance=new NetworkHelper(c);
		}
		return instance;
	}
	
	/**
	 * 检查网络是否连接正常
	 * @return
	 */
	public boolean checkNetworkConnect(){
		ConnectivityManager cm = (ConnectivityManager) context.getSystemService(context.CONNECTIVITY_SERVICE);
		NetworkInfo ni = cm.getActiveNetworkInfo();
		NetworkInfo mobNetInfo = cm.getNetworkInfo( ConnectivityManager.TYPE_MOBILE ); 
		if(ni != null && mobNetInfo!=null){
			return true;
		}else{
			return false;
		}
	}
}