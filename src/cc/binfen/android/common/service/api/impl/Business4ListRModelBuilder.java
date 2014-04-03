package cc.binfen.android.common.service.api.impl;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import cc.binfen.android.common.Constant;
import cc.binfen.android.common.service.api.Business4ListRModel;

public class Business4ListRModelBuilder extends JSONBuilder<Business4ListRModel> {

	@Override
	public Business4ListRModel build(JSONObject jsonObject) throws JSONException {
		Business4ListRModel business = new Business4ListRModel();
		business.setBid(jsonObject.getString(root+Business4ListRModel.JSON_BID));
		business.setbName(jsonObject.getString(root+Business4ListRModel.JSON_BNAME));
		business.setDescribe(jsonObject.getString(root+Business4ListRModel.JSON_DESCRIBE));
		try{
		business.setStars(jsonObject.getDouble(root+Business4ListRModel.JSON_STARS));
		}catch(Exception e){
			business.setStars(0);
		}
		
		//打折数
		double minValue=0.0;
		try{
			minValue=jsonObject.getDouble(root+Business4ListRModel.JSON_DISCOUNT_MIN_VALUE);
		}catch(Exception e){
		}
		double maxValue=minValue;
		try{
		 maxValue=jsonObject.getDouble(root+Business4ListRModel.JSON_DISCOUNT_MAX_VALUE);
		
		}catch(Exception e){
		}
		business.setDiscount(mergerDiscount(minValue,maxValue));
		
		//参与打折的卡名称集合
		try{
		business.setDisCardsName(getDiscountCardsName(jsonObject.getJSONArray(root+Business4ListRModel.JSON_CARD_MEMBER)));
		}catch(Exception e){
		}
		
		try{
			business.setDistance(jsonObject.getInt(root+Business4ListRModel.JSON_DISTANCE));
		}catch (Exception e) {
			// TODO: handle exception
		}
		
		return business;
	}
	
	//合并打折数
	public String mergerDiscount(double minValue,double maxValue){
		String discount="";
		
		if(minValue==maxValue){
			discount=maxValue+"折";
		}
		if(minValue==0.0 && maxValue!=0.0){
			discount=maxValue+"折";
		}
		if(minValue!=0.0 && maxValue==0.0){
			discount=minValue+"折";
		}
		if(minValue!=0.0 && maxValue!=0.0 && minValue!=maxValue){
			discount=minValue+Constant.LINE_CODE+maxValue+"折";
		}	
		if(minValue==0.0 && maxValue==0.0){
			discount="其他优惠";
		}
		
		return discount;
	}
	
	//获取参与打折的卡名称集合
	public static String getDiscountCardsName(JSONArray jsonArrayReviews) throws JSONException {
		int n = jsonArrayReviews.length();
		String cardsName="";
		//组合打折的卡名称
		for(int i=0; i < n; i++){
			JSONObject card=jsonArrayReviews.getJSONObject(i);
			cardsName+="".equals(cardsName) ? card.getString(Business4ListRModel.JSON_CARD_NAME):Constant.DUNHAO+card.getString(Business4ListRModel.JSON_CARD_NAME);
		}
		
		return cardsName;
	}

}
