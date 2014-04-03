package cc.binfen.android.common.service.api.impl;

import org.json.JSONException;
import org.json.JSONObject;

import cc.binfen.android.common.service.api.UpOrDownRModel;

/**
 * @author Michael
 */
public class UpOrDownRModelBuilder extends JSONBuilder<UpOrDownRModel>{

	@Override
	public UpOrDownRModel build(JSONObject jsonObject) throws JSONException {
		UpOrDownRModel udModel = new UpOrDownRModel();
		udModel.setPid(jsonObject.getString(root+UpOrDownRModel.JSON_PID));
		udModel.setAction(jsonObject.getString(root+UpOrDownRModel.JSON_ACTION));
		udModel.setActionTime(jsonObject.getLong(root+UpOrDownRModel.JSON_ACTIONTIME));
		udModel.setAndroid_sid(jsonObject.getString(root+UpOrDownRModel.JSON_ANDROID_SID));
		udModel.setEmailAddress(jsonObject.getString(root+UpOrDownRModel.JSON_EMAILADDRESS));
		
		return udModel;
	}

}
