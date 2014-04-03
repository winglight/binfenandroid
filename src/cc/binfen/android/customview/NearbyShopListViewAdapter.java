package cc.binfen.android.customview;

import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.xml.parsers.SAXParser;
import javax.xml.parsers.SAXParserFactory;

import org.json.JSONException;
import org.xml.sax.Attributes;
import org.xml.sax.InputSource;
import org.xml.sax.SAXException;
import org.xml.sax.XMLReader;
import org.xml.sax.helpers.DefaultHandler;

import android.location.Location;
import android.location.LocationManager;
import android.util.Log;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;
import cc.binfen.android.common.Constant;
import cc.binfen.android.common.service.api.Business4ListRModel;
import cc.binfen.android.common.service.api.WSError;
import cc.binfen.android.common.tools.GpsTask;
import cc.binfen.android.common.tools.GpsTask.GpsData;
import cc.binfen.android.common.tools.GpsTaskCallBack;
import cc.binfen.android.member.BinfenMemberActivity;
import cc.binfen.android.member.R;

/**
 * 附近玩玩商铺列表listView控件的适配器
 * 1.创建适配器实例
 * 2.调用父类restoreDefaultPage()方法
 * 3.更新滚轮参数updateWheelParams()
 * 4.调用initData()方法
 * 5.为listview控件添加适配器实例
 * @author vints
 *
 */
public class NearbyShopListViewAdapter extends ShopListViewAdapter {
	private final static String LOGTAG = "NearbyShopListViewAdapter";
    private LocationManager lm;
    private double longitude=0.0;//用户所在经度
    private double latitude=0.0;//用户所在纬度
    private static NearbyShopListViewAdapter nearbyAdapter = null;
    private String cityName;//当前城市
    private String gpsMessage=activity.getString(R.string.get_local);//获取gps坐标的提示信息
    private Location location;

	private NearbyShopListViewAdapter(BinfenMemberActivity activity,
			int distanceId,String cardId, String consumeId, String sortType) {
		super(activity, distanceId, cardId, consumeId, sortType);
	}
	
	public static NearbyShopListViewAdapter getNearbyShopAdapter(BinfenMemberActivity activity,
			int distanceId,String cardId, String consumeId, String sortType){
		if(nearbyAdapter==null){
			nearbyAdapter = new NearbyShopListViewAdapter(activity, distanceId, cardId, consumeId, sortType);
		}
		return nearbyAdapter;
	}

	@Override
	protected List<Business4ListRModel> loadShops() {
		// 远程获取shopList
		
		Map serachSearchMap = new HashMap();
		try {
			if(latitude!=0 && longitude!=0){
				serachSearchMap = remoteCallService.findBusinessListByDistanceConsumeTypeIdCardId(null, latitude+"", longitude+"", ++this.currentPage,this.perPageNum, Constant.DISTANCE_TYPE[distanceId], consumeId, cardId, sortType, null);
			}else{
				
			}
		} catch (JSONException e) {
			if(e.getMessage()==null){
				e.printStackTrace();
			}else{
				Log.e(LOGTAG, e.getMessage());
			}
		} catch (WSError e) {
			if(e.getMessage()==null){
				e.printStackTrace();
			}else{
				Log.e(LOGTAG, e.getMessage());
			}
		} catch (Exception e) {
			if(e.getMessage()==null){
				e.printStackTrace();
			}else{
				Log.e(LOGTAG, e.getMessage());
			}
		}
		
		List<Business4ListRModel> searchList=(List)serachSearchMap.get("BUSINESS_LIST");
		this.currentPage = (Integer)serachSearchMap.get("CURRENT_PAGE");
		return searchList;
	}
	
	/**
	 * 从GPS获取经度和纬度
	 */
	public void initGPS(){
		gpsMessage=activity.getString(R.string.get_local);
		nearbyAdapter.notifyDataSetChanged();
		
		GpsTask gpstask = new GpsTask(activity,
                new GpsTaskCallBack() {

                    @Override
                    public void gpsConnectedTimeOut() {
                    	gpsMessage=activity.getString(R.string.get_location_timeout);
                    	nearbyAdapter.notifyDataSetChanged();
                    }

                    @Override
                    public void gpsConnected(final cc.binfen.android.common.tools.GpsTask.GpsData gpsdata) {
                    	if (gpsdata != null) {
                    		new Thread(){
                    			@Override
                    			public void run(){
                    				cityName=(cityName==null || "".equals(cityName)) ? getCityNameByLocation(gpsdata):cityName;
                    			}
                    		}.start();
                    		if("深圳市".equals(cityName)){
                				longitude = gpsdata.getLongitude();// longitude
                				latitude = gpsdata.getLatitude();// latitude
                				gpsMessage="";
                				nearbyAdapter.notifyDataSetChanged();
                    		}else{
                    			gpsMessage=activity.getString(R.string.not_in_city);
                    			nearbyAdapter.notifyDataSetChanged();
                    		}
                				
                		} else {
                			longitude=0;
                			latitude=0;
                			gpsMessage=activity.getString(R.string.cannot_get_position);
                			nearbyAdapter.notifyDataSetChanged();
                			Log.w(activity.LOGTAG, "get longitude and latitude fail.");
                		}
                    }
                }, 60000);
	 gpstask.execute();
	}
	
	
	
	@Override
	public View getView(int position, View convertView, ViewGroup parent) {
		//获取到经纬度就查询
		if("".equals(gpsMessage)){
			return super.getView(position, convertView, parent);
		}else{//获取不到就显示提示信息
			TextView message=new TextView(activity);
			message.setText(gpsMessage);
			return message;
		}
	}

	@Override
	protected void saveDefaultWheel() {
		distanceId_default = distanceId; //默认距离
		consumeId_default = consumeId; // 默认消费类型
		cardId_default = cardId; // 默认卡类型
		sortType_default = sortType; // 默认排序类型
	}

	public void restoreDefaultWheel() {
		distanceId = distanceId_default; //默认距离
		consumeId = consumeId_default; // 默认消费类型
		cardId = cardId_default; // 默认卡类型
		sortType = sortType_default; // 默认排序类型
	}

	/**
	 * 更新滚轮选项
	 * @param distanceId
	 * @param cardId
	 * @param consumeId
	 * @param sortType
	 */
	public void updateWheelParams(Integer distanceId,String cardId, String consumeId, String sortType) {
		this.distanceId = distanceId;
		this.cardId = cardId;
		this.consumeId = consumeId;
		this.sortType = sortType;
	}

	/**
	 * NearbyShopListViewAdapter调用此方法不需要传入参数
	 */
	@Override
	public void initData(Object... params) {
		this.currentPage = 0;
		this.hasMore = true;
		this.isLoading = false;
		shopList.clear();
		shopList.add(new Business4ListRModel());
		initGPS();
	}
	
	public String getCityNameByLocation(GpsData gpsdata) {  
        String localityName = "";  
        HttpURLConnection connection = null;  
        URL serverAddress = null;  
  
        try {   
            serverAddress = new URL("http://maps.google.com/maps/geo?q="  
                    + Double.toString(gpsdata.getLatitude()) + ","  
                    + Double.toString(gpsdata.getLongitude())  
                    + "&output=xml&oe=utf8&sensor=true&key="  
                    + "057f7SAux-hr537Idia7NLevlsh8MuS0XVA-QJg");  
            connection = null;  
  
            // Set up the initial connection  
            connection = (HttpURLConnection) serverAddress.openConnection();  
            connection.setRequestMethod("GET");  
            connection.setDoOutput(true);  
            connection.setReadTimeout(10000);  
  
            connection.connect();  
  
            try {  
                InputStreamReader isr = new InputStreamReader(connection  
                        .getInputStream());  
                InputSource source = new InputSource(isr);  
                SAXParserFactory factory = SAXParserFactory.newInstance();  
                SAXParser parser = factory.newSAXParser();  
                XMLReader xr = parser.getXMLReader();  
                GoogleReverseGeocodeXmlHandler handler = new GoogleReverseGeocodeXmlHandler();  
  
                xr.setContentHandler(handler);  
                xr.parse(source);  
  
                localityName = handler.getLocalityName();  
                System.out.println("GetCity.reverseGeocode()"+localityName);  
            } catch (Exception ex) {  
                ex.printStackTrace();  
            }  
        } catch (Exception ex) {  
            ex.printStackTrace();  
            System.out.println("GetCity.reverseGeocode()"+ex);  
        }  
  
        return localityName;  
    }  
	
	/** 
     * The final piece of this puzzle is parsing the xml that is returned from 
     * google’s service. For this example I am using the java SAX (simple api 
     * for xml) parser. The final class to show here is 
     * GoogleReverseGeocodeXmlHandler. In my example, I only want the name of 
     * the city the user is in, so my XmlHandler class I’m about to show only 
     * parses that piece of information. If you want to grab more complete 
     * information (I’ll also give an example file that contains the XML 
     * returned by Google), you’ll have to add more to this class 
     *  
     * @author Administrator 
     *  
     */  
    public class GoogleReverseGeocodeXmlHandler extends DefaultHandler {  
        private boolean inLocalityName = false;  
        private boolean finished = false;  
        private StringBuilder builder;  
        private String localityName;  
  
        public String getLocalityName() {  
            return this.localityName;  
        }  
  
        @Override  
        public void characters(char[] ch, int start, int length)  
                throws SAXException {  
            super.characters(ch, start, length);  
            if (this.inLocalityName && !this.finished) {  
                if ((ch[start] != '\n') && (ch[start] != ' ')) {  
                    builder.append(ch, start, length);  
                }  
            }  
        }  
  
        @Override  
        public void endElement(String uri, String localName, String name)  
                throws SAXException {  
            super.endElement(uri, localName, name);  
  
            if (!this.finished) {  
                if (localName.equalsIgnoreCase("LocalityName")) {  
                    this.localityName = builder.toString();  
                    this.finished = true;  
                }  
  
                if (builder != null) {  
                    builder.setLength(0);  
                }  
            }  
        }  
  
        @Override  
        public void startDocument() throws SAXException {  
            super.startDocument();  
            builder = new StringBuilder();  
        }  
  
        @Override  
        public void startElement(String uri, String localName, String name,  
                Attributes attributes) throws SAXException {  
            super.startElement(uri, localName, name, attributes);  
  
            if (localName.equalsIgnoreCase("LocalityName")) {  
                this.inLocalityName = true;  
            }  
        }  
    }  
}
