package cc.binfen.android.common.service.api.impl;

import org.json.JSONException;
import org.json.JSONObject;

import cc.binfen.android.common.service.api.AccountInfoRModel;
import cc.binfen.android.common.service.api.NewActivitysRModel;

/**
 * @author Michael
 */
public class NewActivitysRModelBuilder extends JSONBuilder<NewActivitysRModel>{

	@Override
	public NewActivitysRModel build(JSONObject jsonObject) throws JSONException {
		NewActivitysRModel comment = new NewActivitysRModel();
		comment.setFirstPhoto(jsonObject.getString(root+AccountInfoRModel.JSON_ANDROID_SID));
		//TODO:add other fields setter
		return comment;
	}

}
