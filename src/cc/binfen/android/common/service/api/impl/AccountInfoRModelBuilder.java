package cc.binfen.android.common.service.api.impl;

import org.json.JSONException;
import org.json.JSONObject;

import cc.binfen.android.common.service.api.AccountInfoRModel;

/**
 * @author Michael
 */
public class AccountInfoRModelBuilder extends JSONBuilder<AccountInfoRModel> {

	@Override
	public AccountInfoRModel build(JSONObject jsonObject) throws JSONException {
		AccountInfoRModel accountInfoRModel = new AccountInfoRModel();
		accountInfoRModel.setAndroid_sid(jsonObject.getString(root+AccountInfoRModel.JSON_ANDROID_SID));
		accountInfoRModel.setSoft_version(jsonObject.getString(root+AccountInfoRModel.JSON_SOFT_VERSION));
		accountInfoRModel.setCity(jsonObject.getString(root+AccountInfoRModel.JSON_CITY));
		accountInfoRModel.setEmailAddress(jsonObject.getString(root+AccountInfoRModel.JSON_EMAIL));
		//accountInfoRModel.setUserCardIdList(jsonObject.get(root+AccountInfoRModel.JSON_USERCARDIDLIST)); //暂定list对象处理
		return accountInfoRModel;
	}

}
