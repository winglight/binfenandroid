package cc.binfen.android.common.service.api.impl;

import org.json.JSONException;
import org.json.JSONObject;

import cc.binfen.android.common.service.api.BusinessMessageErrorRModel;

/**
 * @author Michael
 */
public class BusinessMessageErrorRModelBuilder extends JSONBuilder<BusinessMessageErrorRModel>{

	@Override
	public BusinessMessageErrorRModel build(JSONObject jsonObject) throws JSONException {
		BusinessMessageErrorRModel bmeModel = new BusinessMessageErrorRModel();
		bmeModel.setBid(jsonObject.getString(root+BusinessMessageErrorRModel.JSON_BID));
		bmeModel.setErrorType(jsonObject.getInt(root+BusinessMessageErrorRModel.JSON_ERROR_TYPE));
		bmeModel.setRemark(jsonObject.getString(root+BusinessMessageErrorRModel.JSON_REMARK));
		bmeModel.setEmail(jsonObject.getString(root+BusinessMessageErrorRModel.JSON_EMAIL));
		bmeModel.setCreateAt(jsonObject.getLong(root+BusinessMessageErrorRModel.JSON_CREATE_AT));
		bmeModel.setAndroidSid(jsonObject.getString(root+BusinessMessageErrorRModel.JSON_ANDROID_SID));
		bmeModel.setUserEmail(jsonObject.getString(root+BusinessMessageErrorRModel.JSON_USER_EMAIL));
		
		return bmeModel;
	}

}
