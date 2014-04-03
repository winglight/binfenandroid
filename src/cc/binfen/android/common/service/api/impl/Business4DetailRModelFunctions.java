package cc.binfen.android.common.service.api.impl;

import org.json.JSONException;
import org.json.JSONObject;

import cc.binfen.android.common.service.api.Business4DetailRModel;

/**
 * @author Michael
 */
public class Business4DetailRModelFunctions {
	
	public static Business4DetailRModel getBusiness4Detail(JSONObject jsonObjectReviews) throws JSONException {
		Business4DetailRModelBuilder businessBuilder = new Business4DetailRModelBuilder();
		return businessBuilder.build(jsonObjectReviews);
	}		
}
