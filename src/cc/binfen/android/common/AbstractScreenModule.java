package cc.binfen.android.common;

import java.io.IOException;
import java.lang.ref.SoftReference;
import java.util.Collections;
import java.util.HashMap;
import java.util.Map;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.ParserConfigurationException;

import org.w3c.dom.Document;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;
import org.xml.sax.SAXException;
import org.w3c.dom.Element;

import android.content.Context;
import android.content.Intent;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import cc.binfen.android.member.BinfenMemberActivity;

/**
 * @author Michael
 * Intention：As a super-class for every screen module of app
 * Description：Screen module means an implementation of every UI and not one-to-one but one-to-many that's reused 
 */
public abstract class AbstractScreenModule implements ScreenModule {
	
	//For store Screen Module instances by code
	protected static Map<Integer, SoftReference<AbstractScreenModule>> screenMap = Collections.synchronizedMap(new HashMap<Integer, SoftReference<AbstractScreenModule>>());
	
	//For store class names of Screen Module by code
	protected static Map<Integer, String> screenClassMap = Collections.synchronizedMap(new HashMap<Integer, String>());
	
	//Every screen module should have a reference to the instance of activity
	protected static BinfenMemberActivity activity;
	
	//For indicate the mode of screen module if it has multi UI
	protected int mode = 0;
	
	//View of this moudle
	protected View view;
	
	//initiate the field screenClassMap
	
	private static void registerAllScreen(){
		registerScreenClass(1, "cc.binfen.android.member.MainScreenModule");
		registerScreenClass(10, "cc.binfen.android.member.DistrictModule");
		registerScreenClass(11, "cc.binfen.android.member.DistrictPromoteModule");
		registerScreenClass(13, "cc.binfen.android.member.MetroMapModule");
		registerScreenClass(12, "cc.binfen.android.member.MetroModule");
		registerScreenClass(8, "cc.binfen.android.member.ShoppingCenterListModule");
		registerScreenClass(7, "cc.binfen.android.member.ShoppingStreetModule");
		registerScreenClass(9, "cc.binfen.android.member.ShoppingCenterShopslistModule");
		registerScreenClass(4, "cc.binfen.android.member.NearbyModule");
		registerScreenClass(3, "cc.binfen.android.member.BusinessPromoteDetailModule");
		registerScreenClass(23, "cc.binfen.android.member.BusinessMessageErrorModule");
		registerScreenClass(19, "cc.binfen.android.member.MywalletModule");
		registerScreenClass(20, "cc.binfen.android.member.MyCardModule"); 
		registerScreenClass(21, "cc.binfen.android.member.MyBizCardModule");
		registerScreenClass(22, "cc.binfen.android.member.Add_firstcardModule");
		registerScreenClass(24, "cc.binfen.android.member.LatestSkim_Module");
		registerScreenClass(5, "cc.binfen.android.member.CardPromoteSearchModule");
		registerScreenClass(16, "cc.binfen.android.member.SearchModule");
		registerScreenClass(25, "cc.binfen.android.member.NearbyPromoteCommentModule");
		registerScreenClass(27, "cc.binfen.android.member.NearbyPromoteCommentPictureEditModule");
		registerScreenClass(17, "cc.binfen.android.member.SearchBusinessListModule");
		registerScreenClass(28, "cc.binfen.android.member.Main_VipCardsModule");
		registerScreenClass(18, "cc.binfen.android.member.NewEventsModule");
		registerScreenClass(30, "cc.binfen.android.member.NewEventsDetailModule");
		registerScreenClass(29, "cc.binfen.android.member.MoreModule"); 
		registerScreenClass(33, "cc.binfen.android.member.CommentListModule");
		registerScreenClass(6, "cc.binfen.android.member.HotCardModule");
		registerScreenClass(34, "cc.binfen.android.member.LatestCollectModule");
		registerScreenClass(35, "cc.binfen.android.member.FeedbackModule");  
		registerScreenClass(36, "cc.binfen.android.member.MyBizCardEditModule"); 
	}
	
	//解析module.xml，注册每个module
//	public static void readModuleXML(){
//		DocumentBuilderFactory factory = DocumentBuilderFactory.newInstance();     
//		DocumentBuilder builder = null;
//		try {
//			builder = factory.newDocumentBuilder();
//			Document doc = null;
//			doc = builder.parse(activity.getAssets().open("module.xml"));
//			
//			Element root = doc.getDocumentElement();
//			 NodeList subScreenList = root.getElementsByTagName("screen");  
//			 for (int i = 0; i < subScreenList.getLength(); i++) {
//				 Node node =subScreenList.item(i);
//				 int code=Integer.parseInt(node.getAttributes().getNamedItem("code").getNodeValue());
//				 String moduleName=node.getAttributes().getNamedItem("classname").getNodeValue();
//				 registerScreenClass(code,moduleName);
//			}
//		} catch (SAXException e) {
//			e.printStackTrace();
//		} catch (IOException e) {
//			e.printStackTrace();
//		} catch (ParserConfigurationException e) {
//			e.printStackTrace();
//		}
//	}
	
	//register screen module instance into the field screenClassMap
	protected static void registerScreen(AbstractScreenModule screen){
				screenMap.put(screen.getScreenCode(), new SoftReference<AbstractScreenModule>(screen));
	}
	
	//register class name of screen module into the field screenClassMap
	protected static void registerScreenClass(int code, String classname){
		if(!screenClassMap.containsKey(code)){
			screenClassMap.put(code, classname);
		}
	}
	
	//For invoke by activity before the screen module was displayed
	public abstract void init();
	
	//For invoke by activity after the screen module was quit
	public abstract void finish();
	
	//return the instance of activity
	public Context getContext() {
		return activity;
	}
	
	//return screen module by code, if there's no instance yet, then create an instance by reflect
	public static AbstractScreenModule getScreenByCode(int code){
		AbstractScreenModule module = null;
		
		if(!screenMap.containsKey(code) || screenMap.get(code).get() == null ){
			String classname = screenClassMap.get(code);
			if(classname != null){
				try {
					Class<AbstractScreenModule> screenClass = (Class<AbstractScreenModule>) Class.forName(classname);
					module = screenClass.newInstance();
					registerScreen(module);
				} catch (ClassNotFoundException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				} catch (IllegalAccessException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				} catch (InstantiationException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				} catch (IllegalArgumentException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				} catch (SecurityException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				} 
			}
		}else{
			module = screenMap.get(code).get();
		}
		
		return module;
	}
	
	//for clear all instance of screen modules
	public static void close(){
		if(screenMap!=null){
			screenMap.clear();
			screenMap = null;
		}
		if(screenClassMap!=null){
			screenClassMap.clear();
			screenClassMap = null;
		}
		activity = null;
	}
	public static void rebuildMap(){
		if(screenMap==null){
			screenMap = Collections.synchronizedMap(new HashMap<Integer, SoftReference<AbstractScreenModule>>());
		}
		if(screenClassMap==null){
			screenClassMap = Collections.synchronizedMap(new HashMap<Integer, String>());
			registerAllScreen();
		}
	}

	//for set activity instance to screen module before display it
	public void setActivity(BinfenMemberActivity activity) {
		this.activity = activity;
	}
	
	//get parameters if exist, and send them to screen module before display it
	public void setParameter2Screen(String value){
		
	}
	
	//for save the parameters of screen module while it's quit
	public String getScreenParameter(){
		Log.i("Abstrs..", "at Abstract getScreenParam..");
		return null;
	}
	
	//for screen module, if it needs to deal with event onResume
	public void onResume(){
		
	}
	
//	for screen module, if it needs to deal with event onPause
	public void onPause(){
		
	}
	
	public void onRestart(){
		
	}
	
	//for screen module, if it needs to deal with event onActivityResult
	public void onActivityResult(int requestCode, int resultCode,
			Intent intent){
		
	}
	
	//for multi-mode, it'll be call at initPanel method
	public void  dealWithMode(){
		
	}
	
	//get layout name
	public abstract String getLayoutName();

	//get the mode of screen module
	public int getMode() {
		return mode;
	}

	//set the mode of screen module
	public void setMode(int mode) {
		this.mode = mode;
	}

	public View getView() {
		if (this.view == null) {
			int layoutId = activity.getResources().getIdentifier(getLayoutName(),
					"layout", activity.getPackageName());
			this.view = LayoutInflater.from(activity).inflate(layoutId, null);
		}
		return this.view;
	}

	public void setView(View view) {
		this.view = view;
	}

}
