package cc.binfen.android.member;

import java.io.File;
import java.io.FileOutputStream;
import java.io.InputStream;

import android.app.Activity;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.SharedPreferences.Editor;
import android.os.Bundle;
import android.os.Handler;
import android.preference.PreferenceManager;
import android.provider.Settings;
import android.util.Log;
import android.view.Gravity;
import android.view.KeyEvent;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup.LayoutParams;
import android.view.Window;
import android.view.animation.AccelerateInterpolator;
import android.view.animation.Animation;
import android.view.animation.ScaleAnimation;
import android.view.animation.TranslateAnimation;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;
import android.widget.ViewFlipper;
import cc.binfen.android.common.AbstractScreenModule;
import cc.binfen.android.common.Constant;
import cc.binfen.android.common.LIFOList;
import cc.binfen.android.common.service.UserDBService;
import cc.binfen.android.common.service.api.UserRModel;
import cc.binfen.android.common.service.api.util.IOUtil;
import cc.binfen.android.customview.ModelWindowPanel;
import cc.binfen.android.dbhelper.DatabaseHelper;

/**
 * @author Michael Intention：only activity in the app Description：in charge of
 *         transmit each of screen modules and interaction with system
 */
public class BinfenMemberActivity extends Activity {

	public final static String LOGTAG = "BinfenMemberActivity";

	private Handler mHandler;

	// root view
	private RelativeLayout rootView;
	// parent view of all of screen module
	private ViewFlipper flipper;
	// the title textview of header
	private TextView headTxt;
	// header view
	private RelativeLayout headerView;
	// footer view
	private LinearLayout footerView;
	// modal dialog
	private ModelWindowPanel modelPanel;

	// state of show/hide of modal dialog
	private boolean isShowModel = false;

	// flag if show splash screen
	private boolean isShowSplash = true;
	
	private boolean isShowGuide = true;
	
	// flag if check and prompt db update
	public boolean isPromptDBUpdate = true;
	
	// shopping center id
	private String centerId;

	// left-top button
	private Button returnBtn;
	// left-top App Name Text
	private TextView appNameTxt;
	// right-top button
	private Button mywalletBtn;

	// screen code of the current screen module, default is the main screen
	// module
	private int screen = 1; // 1 * 100 + 0 - main page ;
	// store the history of visit screen module
	private LIFOList lastScreen = new LIFOList(); // formular: item value =
													// screencode*100 + mode
	public UserRModel myInfo = null;

	/** Called when the activity is first created. */
	@Override
	public void onCreate(Bundle savedInstanceState) {
		Log.i(LOGTAG, "entering the main activity.");

		super.onCreate(savedInstanceState);
		requestWindowFeature(Window.FEATURE_NO_TITLE);
		setContentView(R.layout.main);
		
		mHandler = new Handler();

		Log.i(LOGTAG, "initiate db file.");
		initDBFile();

		Log.i(LOGTAG, "initiate all of field components.");
		flipper = (ViewFlipper) findViewById(R.id.flipper);
		rootView = (RelativeLayout) findViewById(R.id.rootView);
		headTxt = (TextView) findViewById(R.id.headTxt);
		headerView = (RelativeLayout) findViewById(R.id.headerView);
		footerView = (LinearLayout) findViewById(R.id.footerView);

		// initiate the modal dialog
		modelPanel = createModelWindow();

		// initiate all of buttons
		initButton();

		
		Log.i(LOGTAG, "start add card screen while first run.");
		checkFirstRun();
		
		if(myInfo==null){
			getMyUserInfo();
		}

	}

	/**
	 * 获取本机用户信息
	 */
	private void getMyUserInfo() {
		UserDBService userService = UserDBService.getInstance(this);
		myInfo = userService.findUserMessage();
	}

	@Override
	protected void onStart() {
		super.onStart();

	}

	@Override
	protected void onStop() {
		super.onStop();

//		AbstractScreenModule.close();
		
//		Intent intent = new Intent(Intent.ACTION_MAIN);
//		intent.addCategory(Intent.CATEGORY_HOME);
//		intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
//		startActivity(intent);
	}

	@Override
	public boolean onKeyDown(int keyCoder, KeyEvent event) {
		int keyCode = event.getKeyCode();
		switch (keyCode) {
		case KeyEvent.KEYCODE_BACK:
			// provide go back function while no modal dialog
			if (!isShowModel) {
				goPrevScreen();
			} else {
				closeModelWindow(modelPanel.getChildAt(0));
			}
			return true;
		default:
			return false;
		}
	}

	// jump to the first add card screen when user run app first
	private void checkFirstRun() {
		// clear map
		//TODO 可判断是否为返回
		AbstractScreenModule.close();

		Intent intent = getIntent();
		
		if (intent != null) {
			isShowSplash = intent.getBooleanExtra(Constant.INTENT_SHOWSPLASH,
					true);
		}

		if (isShowSplash) {
			// show help modal dialog
			// showModelWindow(new HelpWindowPanel(this));
			// Temporarily change a imageview
			intent = new Intent(this, SplashscreenActivity.class);
			this.startActivity(intent);
			this.finish();
		}else{
				//rebuild map
				AbstractScreenModule.rebuildMap();
				gotoScreen(1);

				boolean flag = false;
				
				flag = getPrefFirstFlag();
				if(flag){
					// go to the screen, add card into my wallet
					savePrefFirstFlag(false);
					gotoScreen(22);
					
					if(intent!=null){
						isShowGuide = intent.getBooleanExtra(Constant.INTENT_SHOWGUIDE, true);
					}
					if(isShowGuide){
						intent = new Intent(this,GuideScreenActivity.class);
						this.startActivity(intent);
					}
				}
				
		}
		
		
		
	}

	// for initiation of every screen display
	private void initPanel(AbstractScreenModule module) {
		module.setParameter2Screen(getPrefParameter(module.getScreenCode()));

		module.init();
		module.dealWithMode();

		// show/hide right-top button by footer.visible
		resetHeaderBtn();
	}

	// get the screen module by code and set the instance of activity
	public AbstractScreenModule getScreenByCode(int code) {
		AbstractScreenModule module = AbstractScreenModule
				.getScreenByCode(code);
		if (module.getContext() == null) {
			module.setActivity(this);
		}
		return module;
	}

	// For copy db file in assets into package/database/binfen.db
	private void initDBFile() {
		File dbfile = getDatabasePath(DatabaseHelper.dbName);
		// judge if there exists the db file
		if (!dbfile.exists()) {
			// no db file then copy init db file from assets
			InputStream in = null;
			try {
				in = getAssets().open(Constant.INIT_DBNAME);
				if (in != null) {
					dbfile.getParentFile().mkdirs();
					FileOutputStream writer =  new FileOutputStream(dbfile);
					IOUtil.write(in, writer);
				}
			} catch (Exception e) {
				Log.e(LOGTAG, "copy init db exception:" + e.getMessage());
			}
		}

	}

	// initiate buttons
	private void initButton() {
		returnBtn = (Button) findViewById(R.id.returnBtn);

		appNameTxt = (TextView) findViewById(R.id.appTxt);

		mywalletBtn = (Button) findViewById(R.id.myWalletBtn);

		// 搜索按钮
		TextView searchBtn = (TextView) findViewById(R.id.searchTxt);
		searchBtn.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View v) {
				gotoScreen(16);
			}
		});

		// written by kandy mywallet
		TextView walletBtn = (TextView) findViewById(R.id.walletTxt);
		walletBtn.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View v) {
				gotoScreen(19);
			}
		});

		/**
		 * 底部"更多"的点击事件
		 */
		TextView moreBtn = (TextView) findViewById(R.id.moreTxt);
		moreBtn.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View v) {
				// 跳转到更多界面
				gotoScreen(29, 0);
			}
		});
	}

	// show/hide right button if the footer hide/show, but a few special cases
	private void resetHeaderBtn() {
		// special case 1, the main screen module
		if (screen == 1) {
			returnBtn.setText(null);
			returnBtn.setVisibility(View.GONE);
			returnBtn.setOnClickListener(null);

			appNameTxt.setVisibility(View.VISIBLE);

			headTxt.setGravity(Gravity.LEFT);
			RelativeLayout.LayoutParams layout = new RelativeLayout.LayoutParams(RelativeLayout.LayoutParams.FILL_PARENT, RelativeLayout.LayoutParams.WRAP_CONTENT);
			layout.setMargins(103, 0, 0, 0);
			headTxt.setLayoutParams(layout);
			
			mywalletBtn.setText(R.string.shenzhen);
			mywalletBtn.setVisibility(View.VISIBLE);
			mywalletBtn.setOnClickListener(null);
		} else {
			returnBtn.setText(R.string.back);
			returnBtn.setVisibility(View.VISIBLE);

			appNameTxt.setVisibility(View.GONE);

			headTxt.setGravity(Gravity.CENTER);
			RelativeLayout.LayoutParams layout = new RelativeLayout.LayoutParams(RelativeLayout.LayoutParams.FILL_PARENT, RelativeLayout.LayoutParams.WRAP_CONTENT);
			layout.setMargins(0, 0, 0, 0);
			headTxt.setLayoutParams(layout);

			returnBtn.setOnClickListener(new OnClickListener() {

				@Override
				public void onClick(View v) {
					goPrevScreen();
				}
			});

			// check if the footer is shown or screen is my wallet or my business card
			if (footerView.getVisibility() == View.GONE && screen != 19 && screen != 34 && screen != 20) {
				mywalletBtn.setVisibility(View.VISIBLE);
				// special case 2, the history of browse business entity and my
				// card screen modules
				if (screen != 24 && screen != 20 && screen != 21) {
					mywalletBtn.setText(R.string.mywallet);
					mywalletBtn.setOnClickListener(new OnClickListener() {

						@Override
						public void onClick(View v) {
							// jump to the my wallet screen module
							gotoScreen(19);

						}
					});
				}
			} else {
				mywalletBtn.setVisibility(View.INVISIBLE);
			}
		}
	}

	// show a modal dialog above other views
	public void showModelWindow(View view) {
		isShowModel = true;
		modelPanel.addView(view);
		rootView.addView(modelPanel);
	}

	// hide the modal dialog
	public void closeModelWindow(View view) {
		isShowModel = false;
		modelPanel.removeAllViews();
		rootView.removeView(modelPanel);
	}

	// show/hide header
	public void setHeaderVisible(int visible) {
		headerView.setVisibility(visible);
	}

	// show/hide footer
	public void setFooterVisible(int visible) {
		footerView.setVisibility(visible);
	}

	// jump to the previous screen module by LIFO list or quit app
	public void goPrevScreen() {

		runOnUiThread(new Runnable() {

			@Override
			public void run() {
				// 1.get the current screen instance
				AbstractScreenModule module = getScreenByCode(screen);

				// 2.call finish before exit
				module.finish();
				savePrefParameter(screen, module.getScreenParameter());

				// 3.get last screen code
				Integer code = (Integer) lastScreen.pop();
				if (code != null) {
					// skip the screen of add comment
					if (code / 100 == 25) {
						code = (Integer) lastScreen.pop();
						if (code == null) {
							// 5. quit app
							BinfenMemberActivity.this.finish();
						}
					}
					
					//deal with the same screen in the different mode
					if(screen == code/100){
						int mode = code % 100;
						module.setMode(mode);
						initPanel(module);
						return;
					}

					// 4.1 change the current screen code
					screen = code / 100;
					int mode = code % 100;
					module = getScreenByCode(screen);
					module.setMode(mode);
					
					// 4.2 jump to previous screen by code
					setView4Screen(module, true);

				} else {
					// 5. quit app
					BinfenMemberActivity.this.finish();
				}

			}
		});

	}

	// like the above method, additional support to mode
	public void gotoScreen(final int code, final int... mode) {

		runOnUiThread(new Runnable() {
			
			@Override
			public void run() {
				// 1.get the current screen instance
				AbstractScreenModule module = null;

				// 3.save the current screen code, if it's not the startup
				// screen
				Log.i(LOGTAG, "lastScreen:"+lastScreen.isEmpty());
				if (lastScreen.isEmpty() && code == 1) {

				} else {

					module = getScreenByCode(screen);

					// 2.call finish before exit
					module.finish();
					savePrefParameter(screen, module.getScreenParameter());

					lastScreen.push(new Integer(screen * 100 + module.getMode()));
				}
				
				//deal with the same screen in the different mode
				if(screen == code && mode != null && mode.length > 0){
					module.setMode(mode[0]);
					initPanel(module);
					return;
				}

				// 5.change the current screen code
				screen = code;

				// get the current module
				module = getScreenByCode(code);

				// 6. change the mode value of screen module
				if (mode != null && mode.length > 0) {
					module.setMode(mode[0]);
				}

				// 6.jump to next screen by code
				setView4Screen(module, false);
			}
		});
		
	}

	private void setView4Screen(AbstractScreenModule module, boolean goprev) {

		View view = module.getView();

		if (view.getParent() == null) {

		} else {
			module.setView(null);
			view = module.getView();
		}

		// add the new screen
		flipper.addView(view);

		// view needs to be initiated.
		initPanel(module);

		if (goprev) {
			flipper.setInAnimation(null);
			flipper.setOutAnimation(outScaleAnimation());
		} else {
			flipper.setInAnimation(inScaleAnimation());
			flipper.setOutAnimation(null);

		}

		flipper.showNext();

		flipper.clearAnimation();
		// remove layout for screen
		if (flipper.getChildCount() > 1) {
			flipper.removeViewAt(0);
		}
	}

	// To set the title of every screen
	public void setHeaderTitle(int strResId, String... str) {
		String msg = this.getString(strResId, str);
		setHeaderTitle(msg);
	}

	// To set the title of every screen for string
	public void setHeaderTitle(String str) {
		headTxt.setText(getSubString_14Chars(str));
	}
	
	/**
	 * 获取14个字符长度的子串
	 * @param str
	 * @return 子字符串
	 */
	private String getSubString_14Chars(String str){
		String subStr = "";
		char c;
		int len = 0;
		for (int i = 0; i < str.length(); i++) {
			c = str.charAt(i);
			if   ((c   >=  '0'   &&   c   <=   '9')   ||   (c   >=   'a'   &&   c   <=   'z')	||   (c   >=   'A'  &&   c   <=   'Z'))   { 
				//字母,   数字 
				subStr += c;
				len++;
			}else{
				if   (Character.isLetter(c))   {   //中文 
					subStr += c;
					len   +=   2; 
				}   else   {   //符号或控制字符 
					subStr +=c;
					len++; 
				}
			}
			if(len>=14)break;
		}
		return subStr;
	}

	// to show a toast for recommendation to user
	public void toastMsg(int resId, String... args) {
		final String msg = this.getString(resId, args);
		runOnUiThread(new Runnable() {

			@Override
			public void run() {
				Toast.makeText(getApplicationContext(), msg, Toast.LENGTH_SHORT)
						.show();
			}
		});
	}
	
	public void toastMsg(final String msg ) {
		runOnUiThread(new Runnable() {

			@Override
			public void run() {
				Toast.makeText(getApplicationContext(), msg, Toast.LENGTH_SHORT)
						.show();
			}
		});
	}

	// save parameters into preference
	public void savePrefParameter(int code, String value) {
		SharedPreferences prefs = PreferenceManager
				.getDefaultSharedPreferences(this);
		Editor editor = prefs.edit();
		editor.putString(code + Constant.PREF_PARA_SUFFIX, value);
		editor.commit();
	}

	// get parameters by screen code from preference
	public String getPrefParameter(int code) {
		SharedPreferences prefs = PreferenceManager
				.getDefaultSharedPreferences(this);
		String res = prefs.getString(code + Constant.PREF_PARA_SUFFIX, null);
		Log.i(LOGTAG, "res:"+res);
		return res;
	}

	// save first run flag into preference
	public void savePrefFirstFlag(boolean value) {
		SharedPreferences prefs = PreferenceManager
				.getDefaultSharedPreferences(this);
		Editor editor = prefs.edit();
		editor.putBoolean(Constant.PREF_PARA_FIRST_RUN, value);
		editor.commit();
	}

	// get first run flag by screen code from preference
	public boolean getPrefFirstFlag() {
		SharedPreferences prefs = PreferenceManager
				.getDefaultSharedPreferences(this);
		boolean res = prefs.getBoolean(Constant.PREF_PARA_FIRST_RUN, true);
		return res;
	}
	
	// save show help flag into preference
	public void savePrefShowHelpFlag(boolean value) {
		SharedPreferences prefs = PreferenceManager
				.getDefaultSharedPreferences(this);
		Editor editor = prefs.edit();
		editor.putBoolean(Constant.PREF_PARA_SHOW_HELP, value);
		editor.commit();
	}

	// get show help flag by screen code from preference
	public boolean getPrefShowHelpFlag() {
		SharedPreferences prefs = PreferenceManager
				.getDefaultSharedPreferences(this);
		boolean res = prefs.getBoolean(Constant.PREF_PARA_SHOW_HELP, true);
		return res;
	}

	private Animation inFromRightAnimation() {

		Animation inFromRight = new TranslateAnimation(
				Animation.RELATIVE_TO_PARENT, +1.0f,
				Animation.RELATIVE_TO_PARENT, 0.0f,
				Animation.RELATIVE_TO_PARENT, 0.0f,
				Animation.RELATIVE_TO_PARENT, 0.0f);
		inFromRight.setDuration(Constant.ANIM_DURATION);
		inFromRight.setInterpolator(new AccelerateInterpolator());
		return inFromRight;
	}

	private Animation inScaleAnimation() {

		Animation inFromRight = new ScaleAnimation(0.2f, 1.0f,// X坐标开始缩放的大小和缩到的大小
				0.2f, 1.0f,// Y坐标开始缩放的大小和缩到的大小
				Animation.RELATIVE_TO_PARENT, 0.5f,// 缩放中心的X坐标类型和坐标
				Animation.RELATIVE_TO_PARENT, 0.5f);// 缩放中心的Y坐标的类型和坐标
		inFromRight.setDuration(Constant.ANIM_DURATION);
		inFromRight.setInterpolator(new AccelerateInterpolator());

		return inFromRight;
	}

	private Animation outScaleAnimation() {

		Animation outFromRight = new ScaleAnimation(1, 0.2f,// X坐标开始缩放的大小和缩到的大小
				1, 0.2f,// Y坐标开始缩放的大小和缩到的大小
				Animation.RELATIVE_TO_PARENT, 0.5f,// 缩放中心的X坐标类型和坐标
				Animation.RELATIVE_TO_PARENT, 0.5f);// 缩放中心的Y坐标的类型和坐标
		outFromRight.setDuration(Constant.ANIM_DURATION);
		outFromRight.setInterpolator(new AccelerateInterpolator());

		return outFromRight;
	}

	private Animation outToLeftAnimation() {
		Animation outtoLeft = new TranslateAnimation(
				Animation.RELATIVE_TO_PARENT, 0.0f,
				Animation.RELATIVE_TO_PARENT, -1.0f,
				Animation.RELATIVE_TO_PARENT, 0.0f,
				Animation.RELATIVE_TO_PARENT, 0.0f);
		outtoLeft.setDuration(Constant.ANIM_DURATION);
		outtoLeft.setInterpolator(new AccelerateInterpolator());
		return outtoLeft;
	}

	private Animation inFromLeftAnimation() {
		Animation inFromLeft = new TranslateAnimation(
				Animation.RELATIVE_TO_PARENT, -1.0f,
				Animation.RELATIVE_TO_PARENT, 0.0f,
				Animation.RELATIVE_TO_PARENT, 0.0f,
				Animation.RELATIVE_TO_PARENT, 0.0f);
		inFromLeft.setDuration(Constant.ANIM_DURATION);
		inFromLeft.setInterpolator(new AccelerateInterpolator());
		return inFromLeft;
	}

	private Animation outToRightAnimation() {
		Animation outtoRight = new TranslateAnimation(
				Animation.RELATIVE_TO_PARENT, 0.0f,
				Animation.RELATIVE_TO_PARENT, +1.0f,
				Animation.RELATIVE_TO_PARENT, 0.0f,
				Animation.RELATIVE_TO_PARENT, 0.0f);
		outtoRight.setDuration(Constant.ANIM_DURATION);
		outtoRight.setInterpolator(new AccelerateInterpolator());
		return outtoRight;
	}

	// to create a modal dialog
	private ModelWindowPanel createModelWindow() {
		ModelWindowPanel ll = new ModelWindowPanel(this);
		ll.setLayoutParams(new LinearLayout.LayoutParams(
				LayoutParams.FILL_PARENT, LayoutParams.FILL_PARENT));
		ll.setGravity(Gravity.CENTER);
		return ll;
	}

	@Override
	protected void onPause() {
		AbstractScreenModule module = getScreenByCode(screen);
		module.onPause();
		savePrefParameter(screen, module.getScreenParameter());
		super.onPause();
	}

	@Override
	protected void onResume() {
		AbstractScreenModule module = getScreenByCode(screen);
		module.onResume();
		super.onResume();

	}
	@Override
	protected void onRestart() {
		AbstractScreenModule module = getScreenByCode(screen);
		module.onRestart();
		super.onResume();

	}

	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		super.onCreateOptionsMenu(menu);
		MenuInflater mi = getMenuInflater();
		mi.inflate(R.menu.main_menu, menu);
		return true;
	}

	@Override
	public boolean onMenuItemSelected(int featureId, MenuItem item) {
		switch (item.getItemId()) {
		case R.id.menu_home:
			if(screen != 1){
			gotoScreen(1);
			}
			return true;
//		case R.id.menu_data_source:
//			// 打开数据源设置页面
//			startActivity(new Intent(this, EditPreferencesActivity.class));
//			return true;
//		case R.id.menu_update_data_source:
//			// 更新数据库
//			DBUpdate dbUpdate=new DBUpdate(this);
//			dbUpdate.checkUpdateAsync();
//			return true;	
		}
		return super.onMenuItemSelected(featureId, item);
	}

	@Override
	protected void onActivityResult(int requestCode, int resultCode,
			Intent intent) {
		AbstractScreenModule module = getScreenByCode(screen);
		module.onActivityResult(requestCode, resultCode, intent);
	}

	// get android device code, sid
	public String getUid() {
		return Settings.Secure.getString(getContentResolver(),
				Settings.Secure.ANDROID_ID);
	}
	public String getCenterId() {
		return centerId;
	}

	public void setCenterId(String centerId) {
		this.centerId = centerId;
	}

	public Handler getmHandler() {
		return mHandler;
	}

	public ViewFlipper getFlipper() {
		return flipper;
	}

	public boolean isPromptDBUpdate() {
		return isPromptDBUpdate;
	}

	public void setPromptDBUpdate(boolean isPromptDBUpdate) {
		this.isPromptDBUpdate = isPromptDBUpdate;
	}
}
