package cc.binfen.android.common.tools;

import java.text.SimpleDateFormat;
import java.util.Date;

import cc.binfen.android.common.Constant;

/**
 * 将毫秒值转换为制定格式的日期字符串
 * @author vints
 *
 */
public class DateFormatCovert {
	private static SimpleDateFormat dateTimeFormat = new SimpleDateFormat(Constant.DATETIME_FORMAT);
	private static SimpleDateFormat dateFormat = new SimpleDateFormat(Constant.DATE_FORMAT);
	
	/**
	 * 
	 * @param date 日期毫秒值
	 * @return 制定格式的字符串
	 */
	public static String dateTiemFormat(Long date){
		return dateTimeFormat.format(new Date(date));
	}
	
	/**
	 * 日期转换
	 * @param date 日期毫秒值
	 * @return
	 */
	public static String dateFormat(Long date){
		return dateFormat.format(new Date(date));
	}
}
