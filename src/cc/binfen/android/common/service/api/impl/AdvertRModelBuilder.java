package cc.binfen.android.common.service.api.impl;

import org.json.JSONException;
import org.json.JSONObject;

import cc.binfen.android.common.service.api.AdvertRModel;

public class AdvertRModelBuilder extends JSONBuilder<AdvertRModel>{

	@Override
	public AdvertRModel build(JSONObject jsonObject) throws JSONException {
		AdvertRModel advert = new AdvertRModel();
		advert.setAdvertContent(jsonObject.getString(root+AdvertRModel.JSON_ADVERT_CONTENT));
		advert.setAdvertLink(jsonObject.getString(root+AdvertRModel.JSON_ADVERT_LINK));
		return advert;
	}
}
