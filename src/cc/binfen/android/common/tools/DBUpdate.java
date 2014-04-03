package cc.binfen.android.common.tools;

import java.io.BufferedInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.net.URL;
import java.net.URLConnection;
import java.util.ArrayList;

import org.json.JSONException;

import android.app.Activity;
import android.app.AlertDialog;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.DialogInterface.OnCancelListener;
import android.content.DialogInterface.OnClickListener;
import android.content.Intent;
import android.content.pm.PackageManager.NameNotFoundException;
import android.net.Uri;
import android.os.AsyncTask;
import android.util.Log;
import cc.binfen.android.common.service.CommonDBService;
import cc.binfen.android.common.service.RemoteCallService;
import cc.binfen.android.common.service.api.DBversionRModel;
import cc.binfen.android.common.service.api.UserRModel;
import cc.binfen.android.common.service.api.WSError;
import cc.binfen.android.common.service.api.impl.BinfenGet2ApiImpl;
import cc.binfen.android.member.BinfenMemberActivity;
import cc.binfen.android.member.R;

/**
 * 数据库更新类
 * @author vints
 *
 */
public class DBUpdate {

	private String currTimes = null;
//	private String VER_URL = "http://tiger2.dev.china-rewards.com/tiger2/restful/android/interact/check/db?cityCode=shenzhen&time=";
	private String VER_URL = "/interact/check/db?cityCode=shenzhen&time=";
	private String DB_URL = null;
	private CommonDBService commonService = null;

	private String LOCAL_DB_PATH = "/data/data/";
	private static final String TAG = "DBUpdate";
	
	private static final Integer CANCEL=0;
	private static final Integer CONNECT_ERROR=1;
	private static final Integer SUCCESS=2;

	BinfenMemberActivity activity;
	ArrayList<String> list = new ArrayList<String>();
	private Updater mWork;

	public DBUpdate(Context context) {
		this.activity = (BinfenMemberActivity) context;
		this.commonService = CommonDBService.getInstance(this.activity);
	}

	public void checkUpdateAsync() {
		if (mWork == null) {
			currTimes = commonService.findDBVersionDateTime();
			mWork = new Updater();
			mWork.start();
		} else {
			Log.w(TAG, "checkUpdateAsync has started a instance");
		}

	}

	void cancelUpdate() {
		if (mWork != null) {
			mWork.mCancel = true;
		}
	}

	class Updater extends Thread {
		boolean mCancel = false;
		boolean isFinishUpdate = false;

		@Override
		public void run() {
			checkUpdate();
		}

		void checkUpdate() {
			BufferedInputStream fin = null;
			DBversionRModel versionModel = null;
			try {
				
				//判断数据源
//				SharedPreferences prefs = PreferenceManager.getDefaultSharedPreferences(activity);
//				String dataSource = prefs.getString("data_source", null);
//				if("DEV".equals(dataSource)){
//					VER_URL=BinfenGet2ApiImpl.GET_API_DEV+VER_URL;
//				}else if("TEST".equals(dataSource)){
//					VER_URL=BinfenGet2ApiImpl.GET_API_TEST+VER_URL;
//				}else if("UAT".equals(dataSource)){
//					VER_URL=BinfenGet2ApiImpl.GET_API_UAT+VER_URL;
//				}else if("PROD".equals(dataSource)){
//					VER_URL=BinfenGet2ApiImpl.GET_API_PROD+VER_URL;
//				}else{
//					VER_URL=BinfenGet2ApiImpl.GET_API_TEST+VER_URL;
//				}
				
				VER_URL=BinfenGet2ApiImpl.GET_API_DEV+VER_URL;
				
				VER_URL += currTimes;
				
				RemoteCallService remoteService = RemoteCallService
						.getInstance(activity);
				
				//获取用户信息
				UserRModel user=activity.myInfo;
				//加上sid参数
				VER_URL+="&sid="+user.getAndroidSid();
				
				//加上app版本号
				int appVersion=0;
				try {
					appVersion = activity.getPackageManager().getPackageInfo(activity.getPackageName(), 0).versionCode;
					VER_URL+="&appversion="+appVersion;
				} catch (NameNotFoundException e) {
					e.printStackTrace();
				}
				
				//加上发布渠道编号
				String marketId="0000";
				String[] files;
				try {
					//判断assets文件夹下是否有.version文件，有就读取发布渠道
					files = activity.getAssets().list("");
					for (int i = 0; i < files.length; i++) {
						if(files[i].toUpperCase().endsWith(".version".toUpperCase())){
							marketId=files[i].substring(0, files[i].indexOf(".version"));
							break;
						}
						
					}
				} catch (IOException e) {
					e.printStackTrace();
				}
				VER_URL+="&marketid="+marketId;
				
				versionModel = remoteService.getDBversionInfo(VER_URL);

				if(versionModel==null)return;
				if (versionModel.isHasNew()) {
					// 提示是否更新
					Log.i(TAG, "dbUrl:"+versionModel.getDbUrl());
					alertDialogOnUI(versionModel.getDbUrl());
				}
			} catch (JSONException e) {
				if(e.getMessage()==null){
					e.printStackTrace();
				}else{
					Log.e(TAG, e.getMessage());
				}
			} catch (WSError e) {
				if(e.getMessage()==null){
					e.printStackTrace();
				}else{
					Log.e(TAG, e.getMessage());
				}
			} finally {
				if (fin != null)
					try {
						fin.close();
					} catch (IOException e) {
						e.printStackTrace();
					}
			}

		}
		
		private void alertDialogOnUI(String requestUrl){
			final String loadUrl = requestUrl;
			Runnable runnable = new Runnable() {
				
				@Override
				public void run() {
					new AlertDialog.Builder(
							activity).setMessage(activity.getString(R.string.dbupdate_hasnew_tip))
							.setCancelable(false)
							.setPositiveButton("是", new OnClickListener() {

								@Override
								public void onClick(DialogInterface dialog,
										int which) {
									// 提示正在下载，另起线程下载数据库
									Runnable progr = new Runnable() {
										
										@Override
										public void run() {

											final ProgressDialog updateProdlg = new ProgressDialog(activity);
											updateProdlg.setCancelable(true);
											updateProdlg.setMessage(activity.getString(R.string.dbupdate_downloading_tip));
											final UpdateDB updating = new UpdateDB(updateProdlg);
											updateProdlg.setOnCancelListener(new OnCancelListener() {
												
												@Override
												public void onCancel(DialogInterface dialog) {
													if(!mWork.isFinishUpdate){
														cancelUpdate();
														activity.toastMsg(R.string.dbupdate_download_cancel_tip);
														updating.cancel(true);
													}
													mWork.isFinishUpdate = false;
												}
											});
											updating.execute(loadUrl);

//											Thread updating = new Thread(
//													new Runnable() {
//		
//														@Override
//														public void run() {
//															downloadAndInstall(
//																	loadUrl);
//															updateProdlg.cancel();
//															activity.toastMsg(R.string.dbupdate_download_finish_tip);
//															Log.i(TAG, activity.getString(R.string.dbupdate_download_finish_tip));
//														}
//													});
//											updating.start();
//											
//											updateProdlg.show();
										}
									};
									
									activity.runOnUiThread(progr);
									
								}
							}).setNegativeButton("否", new OnClickListener() {

								@Override
								public void onClick(DialogInterface dialog,
										int which) {
									dialog.cancel();
									activity.toastMsg(R.string.dbupdate_download_cancel_tip);
									activity.savePrefFirstFlag(false);
								}
							}).show();
				}
			};
			activity.getmHandler().post(runnable);
		}
		
		private class UpdateDB extends AsyncTask<String, Integer, Object>{
			private ProgressDialog progress = null;
			
			public UpdateDB(ProgressDialog progress){
				super();
				this.progress = progress;
			}

			@Override
			protected void onCancelled() {
				super.onCancelled();
				if(this.isCancelled()){
					cancelUpdate();
				}
			}

			@Override
			protected void onPostExecute(Object result) {
				super.onPostExecute(result);
				//可以用result判断是否更新成功
				if(result==SUCCESS){
					activity.toastMsg(R.string.dbupdate_download_finish_tip);
				}else if(result==CONNECT_ERROR){
					activity.toastMsg(R.string.dbupdate_download_conerr_tip);
				}
				progress.cancel();
				mWork.isFinishUpdate = true;
			}

			@Override
			protected void onPreExecute() {
				super.onPreExecute();
				progress.show();
			}

			@Override
			protected void onProgressUpdate(Integer... values) {
				super.onProgressUpdate(values);
			}

			@Override
			protected Object doInBackground(String... params) {
				return downloadAndInstall(params[0]);
			}
			
		}

		/**
		 * @param urlString
		 * @param filename
		 */
		public Integer downloadAndInstall(String urlString) {
			InputStream is = null;
			OutputStream os = null;
			try {
				URL url = new URL(urlString);
				URLConnection con = url.openConnection();
				Log.i(TAG, "con time:"+con.getConnectTimeout());
				is = con.getInputStream();
				
				byte[] bs = new byte[1024];
				int len;

				LOCAL_DB_PATH +=activity.getPackageName()+"/databases/binfen";
				Log.i(TAG, activity.getPackageName());
				Log.i(TAG, LOCAL_DB_PATH);
				
//				if (is != null) {
//					FileWriter writer =  new FileWriter(LOCAL_DB_PATH);
//					IOUtils.write(IOUtils.toByteArray(is), writer);
//				}
				
				Log.i("DBUpdate", "cancel:"+mWork.mCancel);
				if (mWork.mCancel) {
					return CANCEL;
				}
				
				len = is.read(bs);
				if(len==-1){
					return CONNECT_ERROR;
				}
				os = new FileOutputStream(LOCAL_DB_PATH);
				os.write(bs, 0, len);
				while ((len = is.read(bs)) != -1) {
					os.write(bs, 0, len);
				}
				os.flush();
				os.close();
				is.close();

//				checkInstall(filename);

			} catch (Exception e) {
				e.printStackTrace();
			} finally {
				if (is != null) {
					try {
						is.close();
					} catch (IOException e) {
						e.printStackTrace();
					}
				}

				if (os != null) {
					try {
						os.close();
					} catch (IOException e) {
						e.printStackTrace();
					}
				}
			}
			return SUCCESS;
		}

		private void checkInstall(final String filename) {
			Runnable action = new Runnable() {
				@Override
				public void run() {

					AlertDialog.Builder build = new AlertDialog.Builder(
							activity);

					build.setTitle(R.string.new_ver_avaliable)
							.setMessage(R.string.check_if_install)
							.setPositiveButton(R.string.alert_dialog_ok,
									new DialogInterface.OnClickListener() {
										@Override
										public void onClick(
												DialogInterface dialog,
												int which) {
											Intent i = new Intent(
													Intent.ACTION_VIEW);
											i.setDataAndType(
													Uri.parse("file://"
															+ filename),
													"application/vnd.android.package-archive");
											activity.startActivity(i);
										}

									})
							.setNegativeButton(R.string.alert_dialog_cancel,
									new DialogInterface.OnClickListener() {
										@Override
										public void onClick(
												DialogInterface dialog,
												int which) {

										}

									});

					build.create().show();
				}
			};
			((Activity) activity).runOnUiThread(action);

		}

	};

}
