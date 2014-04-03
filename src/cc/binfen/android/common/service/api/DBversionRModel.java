package cc.binfen.android.common.service.api;

import java.io.Serializable;

/**
 * 
 * @author vints
 *
 */
public class DBversionRModel implements Serializable {

	/**
	 * 
	 */
	private static final long serialVersionUID = -1767225122046229197L;
	
	public static final String JSON_URL="dbUrl";
	public static final String JSON_HASNEW="hasNew";
	
	private String dbUrl;	//获取数据库的url
	private boolean hasNew = false;	//是否有更新
	
	public String getDbUrl() {
		return dbUrl;
	}
	public void setDbUrl(String dbUrl) {
		this.dbUrl = dbUrl;
	}
	public boolean isHasNew() {
		return hasNew;
	}
	public void setHasNew(boolean hasNew) {
		this.hasNew = hasNew;
	}
}
