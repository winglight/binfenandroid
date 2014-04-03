package cc.binfen.android.common.service.api.util;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.SocketException;
import java.net.URI;
import java.net.URISyntaxException;
import java.net.UnknownHostException;

import org.apache.http.HttpEntity;
import org.apache.http.HttpResponse;
import org.apache.http.client.ClientProtocolException;
import org.apache.http.client.HttpClient;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.conn.ConnectTimeoutException;
import org.apache.http.impl.client.DefaultHttpClient;
import org.apache.http.params.HttpConnectionParams;
import org.apache.http.params.HttpParams;

import android.util.Log;
import cc.binfen.android.common.service.api.UserRModel;
import cc.binfen.android.common.service.api.WSError;
import cc.binfen.android.member.BinfenMemberActivity;

/**
 * @author Michael
 */
public class Caller {
	
	/**
	 * Cache for most recent request
	 */
	private static RequestCache requestCache = null;

	/**
	 * Performs HTTP GET using Apache HTTP Client v 4
	 * 
	 * @param url
	 * @return
	 * @throws WSError 
	 */
	public static String doGet(String url,UserRModel user) throws WSError{
		
		String data = null;
		if(requestCache != null){
			data = requestCache.get(url);
			if(data != null){
				Log.d(BinfenMemberActivity.LOGTAG, "Caller.doGet [cached] "+url);
				return data;
			}
		}
		
		URI encodedUri = null;
		HttpGet httpGet = null;
		
		try {
			encodedUri = new URI(url);
			httpGet = new HttpGet(encodedUri);
		} catch (URISyntaxException e1) {
			// at least try to remove spaces
			String encodedUrl = url.replace(' ', '+');
			httpGet = new HttpGet(encodedUrl);
			e1.printStackTrace();
		}
		
		// initialize HTTP GET request objects
		HttpClient httpClient = new DefaultHttpClient();
		HttpParams httpParams =httpClient.getParams();  
		HttpConnectionParams.setConnectionTimeout(httpParams, 3000);
		HttpResponse httpResponse;
		
		try {
			// execute request
			try {
				//加入用户信息
//				List<NameValuePair> nvps = new ArrayList<NameValuePair>();
//				nvps.add(new BasicNameValuePair(UserRModel.JSON_ANDROID_SID, user.getAndroidSid()));
//				nvps.add(new BasicNameValuePair(UserRModel.JSON_SOFT_VERSION, user.getSoftVersion()));
//				nvps.add(new BasicNameValuePair(UserRModel.JSON_NICKNAME, user.getNickname()));
//				nvps.add(new BasicNameValuePair(UserRModel.JSON_EMAIL, user.getEmail()));
//				nvps.add(new BasicNameValuePair(UserRModel.JSON_CITY, user.getCity()));
//				nvps.add(new BasicNameValuePair(UserRModel.JSON_USER_PASSWARD, user.getUserPassward()));
//				nvps.add(new BasicNameValuePair(UserRModel.JSON_USER_CARDIDS, user.getUserCardIds()));
//				httpGet.setEntity(new UrlEncodedFormEntity(nvps, "utf-8"));
				
				httpResponse = httpClient.execute(httpGet);
				
			} catch (UnknownHostException e) {
				return null;
			} catch (SocketException e){
				return null;
			} catch (ConnectTimeoutException e){
				return "ConnectTimeout";
			}
			
			// request data
			HttpEntity httpEntity = httpResponse.getEntity();
			
			if(httpEntity != null){
				InputStream inputStream = httpEntity.getContent();
				data = convertStreamToString(inputStream);
				// cache the result
				if(requestCache != null){
					requestCache.put(url, data);
				}
			}
			
		} catch (ClientProtocolException e) {
			return null;
		} catch (IOException e) {
			return null;
		}
		
		Log.d(BinfenMemberActivity.LOGTAG, "Caller.doGet "+url);
		return data;
	}

	private static String convertStreamToString(InputStream is) {

		BufferedReader reader = new BufferedReader(new InputStreamReader(is));
		StringBuilder sb = new StringBuilder();

		String line = null;
		try {
			while ((line = reader.readLine()) != null) {
				sb.append(line + "\n");
			}
		} catch (IOException e) {
			e.printStackTrace();
		} finally {
			try {
				is.close();
			} catch (IOException e) {
				e.printStackTrace();
			}
		}

		return sb.toString();
	}

	public static void setRequestCache(RequestCache requestCache) {
		Caller.requestCache = requestCache;
	}
	
	public static String createStringFromIds(int[] ids){
		if(ids == null)
			return "";
		
		String query ="";
		
		for(int id : ids){
			query = query + id + "+";
		}
		
		return query;
	}

}
