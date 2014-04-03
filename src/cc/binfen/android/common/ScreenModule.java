package cc.binfen.android.common;

import android.content.Context;
/**
 * @author Michael
 * Intention：an interface deprecated
 * Description：for Definition of screen module
 */
public interface ScreenModule {

	//to get the current screen code
	public int getScreenCode();
	
	//to get the main activity
	public Context getContext();
	
	
}
