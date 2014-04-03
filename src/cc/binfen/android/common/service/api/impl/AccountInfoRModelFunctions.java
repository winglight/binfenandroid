package cc.binfen.android.common.service.api.impl;

import java.util.ArrayList;
import java.util.List;

import org.json.JSONArray;
import org.json.JSONException;

import cc.binfen.android.common.service.api.AccountInfoRModel;

/**
 * @author Michael
 */
public class AccountInfoRModelFunctions {
	
	public static List<AccountInfoRModel> getAccountInfoRModels(JSONArray jsonArrayAccountInfoRModels) throws JSONException {
		int n = jsonArrayAccountInfoRModels.length();
		List<AccountInfoRModel> albums = new ArrayList();
		AccountInfoRModelBuilder albumBuilder = new AccountInfoRModelBuilder();
		
		for(int i=0; i < n; i++){
			albums.add(albumBuilder.build(jsonArrayAccountInfoRModels.getJSONObject(i)));
		}
		
		return albums;
	}
	
	public static String[] getUserCardList(JSONArray jsonArrayUerCardList) throws JSONException {
		int n = jsonArrayUerCardList.length();
		String[] userCardList = new String[n];
		
		for(int i=0; i < n; i++){
			userCardList[i] = jsonArrayUerCardList.getString(i);
		}
		
		return userCardList;
	}
}
