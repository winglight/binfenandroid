package cc.binfen.android.common.service;

import java.util.ArrayList;
import java.util.List;

import android.content.Context;
import cc.binfen.android.common.Constant;
import cc.binfen.android.common.service.api.FeedbackRModel;
import cc.binfen.android.common.service.api.UserRModel;
import cc.binfen.android.dao.BusinessDAO;
import cc.binfen.android.dao.CardsDAO;
import cc.binfen.android.dao.DistrictDAO;
import cc.binfen.android.dao.LatestCollectDAO;
import cc.binfen.android.dao.MetroDAO;
import cc.binfen.android.dao.MyBizCardDAO;
import cc.binfen.android.dao.MyCommentDAO;
import cc.binfen.android.dao.MywalletDAO;
import cc.binfen.android.dao.ShoppingStreetDAO;
import cc.binfen.android.model.CardsModel;
import cc.binfen.android.model.CodeTableModel;
import cc.binfen.android.model.CollectModel;
import cc.binfen.android.model.ErrorLogModel;
import cc.binfen.android.model.MyBizCardModel;
import cc.binfen.android.model.PromoteCommentModel;
import cc.binfen.android.model.UpOrDownModel;

public class UserDBService {
	private final Context context;
	private static UserDBService userDBService;
	private DistrictDAO districtDao;
	private MetroDAO metroDao;
	private ShoppingStreetDAO streetDao;
	private BusinessDAO businessDao;//商铺dao
	private MywalletDAO mywalletDao;
	private CardsDAO cardDao;
	private LatestCollectDAO latestCollectDao;
	private MyBizCardDAO myBizCardDao;
	private MyCommentDAO myCommentDao; 

	private UserDBService(Context context) {
		this.context = context;
		districtDao = DistrictDAO.getInstance(this.context);
		metroDao = MetroDAO.getInstance(this.context);
		streetDao = ShoppingStreetDAO.getInstance(this.context);
		businessDao = BusinessDAO.getInstance(this.context);
		cardDao = CardsDAO.getInstance(this.context);
		myBizCardDao=MyBizCardDAO.getInstance(this.context);
		latestCollectDao=LatestCollectDAO.getInstance(this.context);
		myCommentDao=MyCommentDAO.getInstance(this.context);
		mywalletDao=MywalletDAO.getInstance(this.context);
		}

	public static UserDBService getInstance(Context context) {
		if (userDBService == null) {
			userDBService = new UserDBService(context);
		}
		return userDBService;
	}
	public void closeAllUserDB(){
		businessDao.closeUserDB();
		cardDao.udbClose();
		myBizCardDao.closeUserDB();
		latestCollectDao.closeCommonDB();
		latestCollectDao.closeUserDB();
		myCommentDao.udbClose();
		mywalletDao.closeUserDB();
	}

	/**
	 * 查找用户信息
	 * @return 用户信息RModel
	 */
	public UserRModel findUserMessage(){
		return myBizCardDao.findUserMessage();
	}
	
	/**
	 * 更新我的昵称
	 * @param newName	新的昵称
	 * @return 是否更新成功
	 */
	public boolean updateMyNickName(String newName){
		myBizCardDao.openUserDB();
		boolean isSuccess = myBizCardDao.updateMyNickName(newName);
		myBizCardDao.closeUserDB();
		return isSuccess;
	}
	
	/**
	 * 获取用户（我）的昵称
	 * @return 我的昵称
	 */
	public String getMyNickName(){
		myBizCardDao.openUserDB();
		String nickName = myBizCardDao.findMyBizCardMsg().getNickname();
		myBizCardDao.closeUserDB();
		return nickName;
	}
	
	/**
	 * 获取我最近一次评论所使用的昵称
	 * @return 昵称
	 */
	public String getMyLastCommentNickName(){
		myBizCardDao.openUserDB();
		String nickName = myBizCardDao.getMyLastCommentNickName();
		myBizCardDao.closeUserDB();
		return nickName;
	}
	
	/**
	 * 查找用户卡的卡id集合
	 * 
	 * @return
	 */
	public List<String> findUserCardsIds() {
		List<String> cards = new ArrayList<String>();
		businessDao.openUserDB();
		String cids= businessDao.findUserCardsIdFromUserDB();
		businessDao.closeUserDB();
		List<String> cardList = new ArrayList<String>();
		businessDao.openCommonDB();
		cardList = businessDao.findCardsIdFromCommonDB();
		businessDao.closeCommonDB();
		
		//将从urser_info表查出来的卡id集合转换成String[]
		if(cids!=null && !"".equals(cids)){
			String[] cidsStr=cids.split(",");
			for (int i = 0; i < cidsStr.length; i++) {
				if(!cardList.contains(cidsStr[i])){
					continue;
				}
				cards.add(cidsStr[i]);
			}
		}		
		return cards;
	}
//	public int[] findCollectCount(){
//		 
//		
//	}

	/**
	 * 新增一行纪录到UP_OR_DOWN TABLE
	 * 
	 * @param model 顶/踩的model
	 * @return
	 */
	public long insertUpOrDown(UpOrDownModel model) {
		businessDao.openUserDB();
		long result = businessDao.insertUpOrDown(model);
		businessDao.closeUserDB();
		return result;
	}

	/**
	 * 查询该优惠最近一次的顶或踩时间
	 * 
	 * @param pid 优惠表id
	 * @return long 顶或踩的时间
	 */
	public long findLastUpOrDownTime(String pid) {
		businessDao.openUserDB();
		long result = businessDao.findLastUpOrDownTime(pid);
		businessDao.closeUserDB();
		return result;
	}

	/**
	 * 判断用户是否收藏过该商铺
	 * 
	 * @param bid 商铺id
	 * @return 是否已经收藏过该商铺
	 */
	public boolean checkBusinessIsCollect(String pid) {
		boolean result = false;
		businessDao.openUserDB();
		result = businessDao.checkBusinessIsCollect(pid);
		businessDao.closeUserDB();
		return result;
	}

	/**
	 * 新增一行收藏记录到COLLECT
	 * 
	 * @param model 收藏model
	 * @return
	 */
	public long insertCollect(CollectModel model) {
		businessDao.openUserDB();
		long result = businessDao.insertCollect(model);
		businessDao.closeUserDB();
		return result;
	}

	/**
	 * 新增一行报错记录到ERROR_LOG
	 * 
	 * @param model 错误信息model
	 * @return
	 */
	public long insertErrorLog(ErrorLogModel model) {
		businessDao.openUserDB();
		long result = businessDao.insertErrorLog(model);
		businessDao.closeUserDB();
		return result;
	}
	
	/**
	 * 查询用户所有可以使用的卡model list
	 * @return
	 */
	public List<CardsModel> findMyCardCanUse(){
		List<CardsModel> myCardList=new ArrayList();
		//获取用户表里面的用户卡id的集合
		businessDao.openUserDB();
		String userCardIds=businessDao.findUserCardsIdFromUserDB();
		businessDao.closeUserDB();
		businessDao.openCommonDB();
		List<String> cardList = new ArrayList<String>();
		cardList = businessDao.findCardsIdFromCommonDB();
		businessDao.closeCommonDB();
		//如果用户表里面的用户卡id不为空，则根据卡id去查询卡model
		if(userCardIds!=null && !"".equals(userCardIds)){
			String[] cids=userCardIds.split(",");
			cardDao.open();
			for (int i = 0; i < cids.length; i++) {
				//根据卡id去查询卡model
				if(!cardList.contains(cids[i])){
					continue;
				}
				CardsModel card=cardDao.findCardById(cids[i]);
				
				myCardList.add(card);
			}
			cardDao.close();
		}
		return myCardList;
	}
	
	/**
	 * 查询用户所有的卡model list,如果用户卡数量为0，则返回推荐卡
	 * @return
	 */
	public List<CardsModel> findMyCardOrAdvisedCard(){
		List<CardsModel> myCardList= findMyCardCanUse();
		if(myCardList.size() == 0){
			List<CodeTableModel> codelist = CommonDBService.getInstance(context).findCodeByCodeType(Constant.code_advised_card);
			cardDao.open();
			for (CodeTableModel codeTable : codelist) {
				String cardId = codeTable.getCodeName();
				//	根据卡id去查询卡model
				
				CardsModel card=cardDao.findCardById(cardId);
				
				myCardList.add(card);
			}
			cardDao.close();
		}
		return myCardList;
	}

	/**
	 * 新增一张卡到user_info.USER_CARD_IDS
	 * @param cardId 要添加的卡id
	 * @return
	 */
	public long insertCardToUserInfoUserCardIds(String cardId) {
		//获取用户卡的id集合
		businessDao.openUserDB();
		String userCardIds=businessDao.findUserCardsIdFromUserDB();
		businessDao.closeUserDB();
		
		//判断卡id集合是否为空
		if(userCardIds!=null && !"".equals(userCardIds)){//不为空：加","再加
			userCardIds+=","+cardId;
		}else{//如果空就直接加card id
			userCardIds=cardId;
		}
		
		cardDao.openUserDB();
		long result=cardDao.updateUserInfoUserCardIds(userCardIds);
		cardDao.udbClose();
		return result;
	}
	
	/**
	 * 删除一张卡到user_info.USER_CARD_IDS
	 * @param cardId 要添加的卡id
	 * @return
	 */
	public long deleteCardToUserInfoUserCardIds(String cardId) {
		//获取用户卡的id集合
		businessDao.openUserDB();
		String userCardIds=businessDao.findUserCardsIdFromUserDB();
		businessDao.closeUserDB();
		
		//判断卡id集合是否为空
		if(userCardIds!=null && !"".equals(userCardIds)){//不为空才进行删除操作
			//定义一个新的卡id集合字符串
			String newUserCardIds="";
			String[] cardIds=userCardIds.split(",");
			for (int i = 0; i < cardIds.length; i++) {
				//如果是和传进来的卡id相同的话就不放进新的卡id集合字符串
				if(cardIds[i].equals(cardId)){
					continue;
				}else{
					newUserCardIds+=("".equals(newUserCardIds)?"":",")+cardIds[i];
				}
			}
			//根据新的卡id集合字符串修改user_info.user_card_ids
			cardDao.openUserDB();
			long result=cardDao.updateUserInfoUserCardIds(newUserCardIds);
			cardDao.udbClose();
			return result;
		}
		return -1;
	}

	/**
	 * 判断用户卡里面的卡是否在卡表存在
	 * 
	 * @param cid 卡的id
	 * @return
	 */
	public boolean is_exitsUserCard(String cid) {
		return cardDao.is_exitsUserCard(cid);
	}
	
 /**
  * 添加一条评论
  */
 	public boolean addCommentToLocal(PromoteCommentModel comment){
 		return businessDao.addPromoteCommentToLocal(comment);
 	}
 
 	/**
 	 * 获取我的全部点评
 	 * @return 
 	 */
 	public List<PromoteCommentModel> getAllMyComments(){
 		return  businessDao.getAllMyComment();
 	}
 	/**
 	 *删除一条收藏
 	 * @param colect
 	 * @return
 	 */
 	public  long deleteCollect(CollectModel colect){
 		
		return latestCollectDao.deleteCollect(colect);
 	}
 	/**
 	 * 查询用户的收藏列表
 	 */
 	public List<CollectModel> findLatestCollectBusinessList(String type){
 		return latestCollectDao.findLatestCollectBusinessList(type);
 	}
 	/**
 	  * 添加一条反馈
 	  */
 	 	public boolean addFeedBackToServer(FeedbackRModel feedBackInfo){
 	 		return myBizCardDao.addFeedBackToServer(feedBackInfo);
 	 	}
 	 	//“我的钱包”计算用户共花了的消费额和节省的消费额
		 public double[] findUserCost(){ 
			 return myCommentDao.findUserCost();
		 }
		 
		 /**
		  * author:kandy  
		  * updateDate:2012.1.16
		  *   updateContent:查找收藏了多少条优惠*/
			public int findcollectPromoteCountFromComDB(){
				return mywalletDao.findCollectPromoteCountFromComDB();
			}
			 /*计算我的评论数**/
			public int findMyCommentCountFromUserDB(){
				return mywalletDao.findMyCommentCountFromUserDB();
			}
			
			//我的名片获取业务数据
			 public MyBizCardModel findMyBizCardMsg(){ 
				 return myBizCardDao.findMyBizCardMsg();
			 }
			 public  long updateMyBizCardMsg(MyBizCardModel mc){
				 return myBizCardDao.updateMyBizCardMsg(mc);
			 }
	
	//删除最近浏览的记录		 
	public long deleteLastestSkimRecords(String type){
		return latestCollectDao.deleteLastestSkimRecords(type);
	}
		 
}
