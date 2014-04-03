package cc.binfen.android.model;

/**
 * @author sunny
 * 用途：错误信息model，记录错误信息相关属性
 * 修改内容：
 */
public class ErrorLogModel {
	
	//错误信息module
	private String id;				//ID		ERROR_LOG.ID integer
	private String bid;			//报错商铺id	ERROR_LOG.BID	integer
	private int errorType;		//报错类型	ERROR_LOG.ERROR_TYPE	integer
	private String remark;		//错误描述	ERROR_LOG.REMARK text
	private String email;		//报错用户email ERROR_LOG.EMAIL	text
	private long createAt;		//报错时间	ERROR_LOG.CREATE_AT	long		
	
	public String getId() {
		return id;
	}
	public void setId(String id) {
		this.id = id;
	}
	public String getBid() {
		return bid;
	}
	public void setBid(String bid) {
		this.bid = bid;
	}
	public int getErrorType() {
		return errorType;
	}
	public void setErrorType(int errorType) {
		this.errorType = errorType;
	}
	public String getRemark() {
		return remark;
	}
	public void setRemark(String remark) {
		this.remark = remark;
	}
	public String getEmail() {
		return email;
	}
	public void setEmail(String email) {
		this.email = email;
	}
	public long getCreateAt() {
		return createAt;
	}
	public void setCreateAt(long createAt) {
		this.createAt = createAt;
	}
}
