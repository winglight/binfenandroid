package cc.binfen.android.member;

import java.util.List;

import android.app.AlertDialog;
import android.content.ActivityNotFoundException;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.location.LocationManager;
import android.provider.Settings;
import android.util.Log;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.TextView;
import cc.binfen.android.common.AbstractScreenModule;
import cc.binfen.android.common.remote.CoverflowCallback;
import cc.binfen.android.common.remote.DownloadUtil;
import cc.binfen.android.common.service.UserDBService;
import cc.binfen.android.common.tools.DBUpdate;
import cc.binfen.android.customview.CoverFlowView;
import cc.binfen.android.model.CardsModel;

/**
 * @author Michael
 * Intention：Main screen module
 * Description：a navigation UI for app
 */
public class MainScreenModule extends AbstractScreenModule {
	private final static String LOGTAG = "MainScreenModule";

//	private TextView mainAdvertTxt;//广告栏
//	private int lastSelectedImageView = -1; // for clear alpha of unselected
//											// imageview
	
	@Override
	public int getScreenCode() {
		return 1;
	}

	@Override
	public void init() {
		// 1.
			
			initCoverFlow();
			
			//TODO 暂时屏蔽
//			mainAdvertTxt=(TextView)activity.findViewById(R.id.mainAdvertTxt);

			//地区
			TextView zoneBtn = (TextView) this.activity
					.findViewById(R.id.zoneTxt);
			zoneBtn.setOnClickListener(new OnClickListener() {

				@Override
				public void onClick(View v) {
					activity.closeModelWindow(activity
							.findViewById(R.layout.main));
					activity.gotoScreen(10);
				}
			});
			/**
			 * 主界面  "卡" 的点击事件
			 */
			TextView cardBtn = (TextView) this.activity.findViewById(R.id.cardTxt);
			 cardBtn.setOnClickListener(new OnClickListener() {
				
				@Override
				public void onClick(View v) {
					// 主页面跳转到 "卡"的详情页面
					activity.gotoScreen(28);
				}
			});
			 
			 //around me
			TextView btnNearBy = (TextView) this.activity
					.findViewById(R.id.nearByTxt);
			btnNearBy.setOnClickListener(new OnClickListener() {
				@Override
				public void onClick(View v) {
					LocationManager locationManager = (LocationManager) getContext()
							.getSystemService(Context.LOCATION_SERVICE);
					if (locationManager
							.isProviderEnabled(LocationManager.GPS_PROVIDER)) {
						NearbyModule module = (NearbyModule) activity.getScreenByCode(4);
						module.setHasTurnOther(false);
						Log.i(LOGTAG, "GPS is open.");
						activity.gotoScreen(4);
					} else {
						Log.i(LOGTAG, "GPS is not open.");
						AlertDialog.Builder dialog = new AlertDialog.Builder(
								activity);
						dialog.setTitle(activity.getString(R.string.warm_title))
								.setMessage(
										activity.getString(R.string.warm_message))
								.setPositiveButton(
										activity.getString(R.string.ok),
										new DialogInterface.OnClickListener() {
											@Override
											public void onClick(
													DialogInterface dialog,
													int which) {
												Intent intent = new Intent();
												intent.setAction(Settings.ACTION_LOCATION_SOURCE_SETTINGS);
												intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
												try {
													getContext().startActivity(
															intent);
												} catch (ActivityNotFoundException ex) {
													intent.setAction(Settings.ACTION_SETTINGS);
													try {
														getContext()
																.startActivity(
																		intent);
													} catch (Exception e) {

													}
												}
											}
										})
								.setNegativeButton(
										activity.getString(R.string.cancel),
										new DialogInterface.OnClickListener() {
											public void onClick(
													DialogInterface dialog,
													int which) {
												dialog.cancel();
											}
										}).create().show();
					}

				}
			});

			//地铁
			TextView metroBtn = (TextView) this.activity
					.findViewById(R.id.metroTxt);
			metroBtn.setOnClickListener(new OnClickListener() {

				@Override
				public void onClick(View v) {
					activity.closeModelWindow(activity
							.findViewById(R.layout.main));
					activity.gotoScreen(12);
				}
			}); 
			
			//购物街
			TextView shoppingStreetBtn = (TextView) this.activity
					.findViewById(R.id.shoppingStreetTxt);
			shoppingStreetBtn.setOnClickListener(new OnClickListener() {

				@Override
				public void onClick(View v) {
//					activity.closeModelWindow(activity.findViewById(R.layout.main));
					ShoppingStreetModule module = (ShoppingStreetModule) activity.getScreenByCode(7);
					module.setIsEntryFromMain(true);
					activity.gotoScreen(7);
				}
			});
			
			//热门卡
			TextView hotCardBtn=(TextView)this.activity.findViewById(R.id.hotCardTxt);
			hotCardBtn.setOnClickListener(new OnClickListener() {
				@Override
				public void onClick(View v) {
					activity.gotoScreen(6);
				}
			});
			
		//设置广告栏内容
		//TODO 暂时屏蔽
//		mainAdvertTxt.setText(R.string.search_module_advert);
		
		activity.setHeaderTitle(R.string.homeTitle);
		
	}

	//initiate the coverflow view 
	private void initCoverFlow() {
		
		CoverFlowView mCoverflow = (CoverFlowView) this.activity
				.findViewById(R.id.coverFlowView1);
		
		final TextView titleTxt = (TextView) this.activity.findViewById(R.id.cardTitleTxt);

		// get card data from db
		final List<CardsModel> cardsList = UserDBService.getInstance(activity).findMyCardOrAdvisedCard();
		
//		mCoverflow.setAdapter(new AbstractCoverFlowImageAdapter(){
//
//
//			@Override
//			public int getCount() {
//				return cardsList.size();
//			}
//
//			@Override
//			protected Bitmap createBitmap(int position) {
//				Bitmap bitmap = DownloadUtil.decodeFile(cardsList.get(position).getPhoto(), null);
//				if(bitmap == null){
//					bitmap = BitmapFactory.decodeResource(activity.getResources(), R.drawable.no_image_48);
//				}
//				return convertBitmapSize(bitmap);
//			}
//			
//		});

		DownloadUtil.decodeFile(cardsList, new CoverflowCallback(mCoverflow, 200, 125), titleTxt);
	}
	
	@Override
	public void onResume() {
		if(!activity.getPrefFirstFlag()&&activity.isPromptDBUpdate){
			activity.setPromptDBUpdate(false);
			DBUpdate checkDB = new DBUpdate(activity);
			checkDB.checkUpdateAsync();
		}

		// If we cleared the coverflow in onPause, resurrect it
	}

	@Override
	public void onPause() {

		// Clear the coverflow to save memory
	}
	
	@Override
	public void onRestart(){
		
	}
	
	@Override
	public void finish() {
		activity.setHeaderTitle("");
	}

	@Override
	public void setParameter2Screen(String value) {
		// TODO Auto-generated method stub
		
	}

	@Override
	public String getScreenParameter() {
		// TODO Auto-generated method stub
		return null;
	}
 
	public String getLayoutName(){
		return "main_module";
	}
}
