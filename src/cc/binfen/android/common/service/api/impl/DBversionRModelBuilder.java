package cc.binfen.android.common.service.api.impl;

import org.json.JSONException;
import org.json.JSONObject;

import android.util.Log;
import cc.binfen.android.common.service.api.DBversionRModel;

/**
 * 
 * @author vints
 *
 */
public class DBversionRModelBuilder extends JSONBuilder<DBversionRModel> {
	private static final String LOGTAG = "DBversionRModelBuilder";

	@Override
	public DBversionRModel build(JSONObject jsonObject) throws JSONException {

		DBversionRModel dBversionRModel = new DBversionRModel();
		try{
			dBversionRModel.setDbUrl(jsonObject.getString(root+DBversionRModel.JSON_URL));
		}catch (Exception e) {
			if(e.getMessage()==null){
				e.printStackTrace();
			}else{
				Log.v(LOGTAG, e.getMessage());
			}
		}
		try{
			dBversionRModel.setHasNew(jsonObject.getBoolean(root+DBversionRModel.JSON_HASNEW));
		}catch (Exception e) {
			if(e.getMessage()==null){
				e.printStackTrace();
			}else{
				Log.v(LOGTAG, e.getMessage());
			}
		}
		return dBversionRModel;
	}

}
