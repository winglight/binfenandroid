package cc.binfen.android.model;

/**
 * shoppingstreet table
 * @author vints
 *
 */
public class ShoppingStreetModel {
	//数据库字段
	private String id;		//购物街id	id  Integer
	private String streetName;		//购物街名称		str_name  text
	private Integer environment;	//环境星级	environment  Integer
	private Integer price;			//价格星级	price 	Integer
	private Integer variety;		//种类星级	variety		Integer
	private String desc;		//desc 		desc	text
	private String imageName;		//图片名称 	image_name  text
	
	public String getId() {
		return id;
	}
	public void setId(String id) {
		this.id = id;
	}
	public String getStreetName() {
		return streetName;
	}
	public void setStreetName(String streetName) {
		this.streetName = streetName;
	}
	public Integer getEnvironment() {
		return environment;
	}
	public void setEnvironment(Integer environment) {
		this.environment = environment;
	}
	public Integer getPrice() {
		return price;
	}
	public void setPrice(Integer price) {
		this.price = price;
	}
	public Integer getVariety() {
		return variety;
	}
	public void setVariety(Integer variety) {
		this.variety = variety;
	}
	public String getDesc() {
		return desc;
	}
	public void setDesc(String desc) {
		this.desc = desc;
	}
	public String getImageName() {
		return imageName;
	}
	public void setImageName(String imageName) {
		this.imageName = imageName;
	}
}
