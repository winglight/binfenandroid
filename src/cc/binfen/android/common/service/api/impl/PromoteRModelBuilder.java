package cc.binfen.android.common.service.api.impl;

import java.util.Map;

import org.json.JSONException;
import org.json.JSONObject;

import cc.binfen.android.common.service.api.PromoteRModel;

public class PromoteRModelBuilder extends JSONBuilder<PromoteRModel>{

	@Override
	public PromoteRModel build(JSONObject jsonObject) throws JSONException {
		PromoteRModel promote = new PromoteRModel();
		//优惠id
		promote.setPid(jsonObject.getString(root+PromoteRModel.JSON_PID));
		//优惠描述
		try{
			promote.setDiscountDescribe(jsonObject.getString(root+PromoteRModel.JSON_DISCOUNT_DESCRIBE));
		}catch (Exception e) {
		}	
		//优惠最小折扣
		try{
			promote.setDiscountMinValue(jsonObject.getDouble(root+PromoteRModel.JSON_DISCOUNT_MIN_VALUE));
		}catch (Exception e) {
		}
		//优惠最大折扣
		try{
			promote.setDiscountMaxValue(jsonObject.getDouble(root+PromoteRModel.JSON_DISCOUNT_MAX_VALUE));
		}catch (Exception e) {
		}
		//优惠单位
		try{
			promote.setDiscountUnits(jsonObject.getString(root+PromoteRModel.JSON_DISCOUNT_UNITS));
		}catch (Exception e) {
		}
		//优惠时间(起)
		try{
			promote.setDiscountStartAt(jsonObject.getString(root+PromoteRModel.JSON_DISCOUNT_START_AT));
		}catch (Exception e) {
		}
		//优惠时间(迄)
		try{
			promote.setDiscountEndAt(jsonObject.getString(root+PromoteRModel.JSON_DISCOUNT_END_AT));
		}catch (Exception e) {
		}
		//优惠被顶的总数
		try{
			promote.setUpCount(jsonObject.getInt(root+PromoteRModel.JSON_UP_COUNT));
		}catch (Exception e) {
		}
		//优惠被踩的总数
		try{
			promote.setDownCount(jsonObject.getInt(root+PromoteRModel.JSON_DOWN_COUNT));
		}catch (Exception e) {
		}
		
		//获取参与优惠的卡的id、图片、名称
		try{
			Map<String,String[]> cardMap=PromoteRModelFunctions.getDiscountCardsMessage(jsonObject.getJSONArray(root+PromoteRModel.JSON_CARD_MEMBER));
			//写入参与优惠的卡id集合
			promote.setCardsId(cardMap.get("CARD_IDS"));
			//写入参与优惠的卡图片集合
			promote.setCardsPhoto(cardMap.get("CARD_PHOTOS"));
			//写入参与优惠的卡名称集合
			promote.setCardsName(cardMap.get("CARD_NAMES"));
		}catch (Exception e) {
		}
		
		return promote;
	}

}
