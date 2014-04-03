/**
 * 
 */
package cc.binfen.android.model;

/**
 * @author sunny
 *	用途：顶/踩model，记录顶/踩相关属性
 *	修改内容：
 */
public class UpOrDownModel {
	
	private String id;			//UP_OR_DOWN.ID	integer
	private String pid;		//优惠表id			UP_OR_DOWN.pid	integer
	private String action;	//动作：1.顶；2.踩	UP_OR_DOWN.action	text
	private long actionTime;//顶或踩的日期		UP_OR_DOWN.action_time	long
	
	public String getId() {
		return id;
	}
	public void setId(String id) {
		this.id = id;
	}
	public String getPid() {
		return pid;
	}
	public void setPid(String pid) {
		this.pid = pid;
	}
	public String getAction() {
		return action;
	}
	public void setAction(String action) {
		this.action = action;
	}
	public long getActionTime() {
		return actionTime;
	}
	public void setActionTime(long actionTime) {
		this.actionTime = actionTime;
	}
}
