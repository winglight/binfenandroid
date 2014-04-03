package cc.binfen.android.common.service.api.impl;

import org.json.JSONException;
import org.json.JSONObject;

import cc.binfen.android.common.service.api.DBversionRModel;

/**
 * 
 * @author vints
 *
 */
public class DBversionRModelFuctions {
	
	public static DBversionRModel getDBversionRModel(JSONObject object) throws JSONException{
		DBversionRModelBuilder builder = new DBversionRModelBuilder();
		return builder.build(object);
	}
}
