package cc.binfen.android.dao;

import java.util.ArrayList;
import java.util.List;

import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteException;
import android.util.Log;
import cc.binfen.android.dbhelper.DatabaseHelper;
import cc.binfen.android.model.CodeTableModel;

public class CodeTableDAO {
	private SQLiteDatabase db;//db对象
	private final Context context;//上下文对象
	private static CodeTableDAO instance;//codeDao实例对象
	public DatabaseHelper sdbHelper;//helper类对象
	
	private CodeTableDAO(Context context){
		this.context = context;
		this.sdbHelper=new DatabaseHelper(this.context);
	}
	
	/**
	 * 打开数据库方法
	 * @throws SQLiteException
	 */
	public void openCommonDB() throws SQLiteException {
		try {
			db = sdbHelper.getWritableDatabase();
		} catch (SQLiteException ex) {
			Log.v("Open database exception caught", ex.getMessage());
			db = sdbHelper.getReadableDatabase();
		}
	}
	
	/**
	 * 关闭数据库方法
	 */
	public void closeCommonDB() {
		if(db!=null){
			db.close();
		}	
	}
	
	/**
	 * 获取dao实例对象
	 * @param c 上下文对象
	 * @return dao实例对象
	 */
	public static CodeTableDAO getInstance(Context c) {
		if (instance == null) {
			instance = new CodeTableDAO(c);
		}
		return instance;
	}
	
	/**
	 * 根据code_type获取code
	 * @param codeType
	 * @return
	 */
	public List<CodeTableModel> findCodeByCodeType(String codeType){
		List<CodeTableModel> list=new ArrayList();
		Cursor cursor=db.query(DatabaseHelper.CODE_TABLE_NAME, new String[]{DatabaseHelper.CODE_TYPE,
				DatabaseHelper.CODE_NAME,DatabaseHelper.CODE_VALUE,DatabaseHelper.EXTRA_VALUE1,
				DatabaseHelper.EXTRA_VALUE2}, DatabaseHelper.CODE_TYPE+"=?", new String[]{codeType}, null, null, null);
		return changeCursorToCodeTableModeleList(cursor);
	}
	
	/**
	 * 查询手动排序在前面的热门卡
	 * @param codeType
	 * @return
	 */
	public List<CodeTableModel> findHeadHotCards(String codeType){
		List<CodeTableModel> list=new ArrayList();
		Cursor cursor=db.query(DatabaseHelper.CODE_TABLE_NAME, new String[]{DatabaseHelper.CODE_TYPE,
				DatabaseHelper.CODE_NAME,DatabaseHelper.CODE_VALUE,DatabaseHelper.EXTRA_VALUE1,
				DatabaseHelper.EXTRA_VALUE2}, DatabaseHelper.CODE_TYPE+"=?", new String[]{codeType},
				null, null, DatabaseHelper.CODE_VALUE+" asc");
		return changeCursorToCodeTableModeleList(cursor);
	}
	
	/**
	 * 根据编码类型和编码id查询code model
	 * @param codeType 编码类型
	 * @param codeName 编码id
	 * @return
	 */
	public CodeTableModel findCodeByCodeTypeAndCodeName(String codeType,String codeName){
		Cursor cursor=db.query(DatabaseHelper.CODE_TABLE_NAME, new String[]{DatabaseHelper.CODE_TYPE,
				DatabaseHelper.CODE_NAME,DatabaseHelper.CODE_VALUE,DatabaseHelper.EXTRA_VALUE1,
				DatabaseHelper.EXTRA_VALUE2}, DatabaseHelper.CODE_TYPE+"=? and "+DatabaseHelper.CODE_NAME+"=?", new String[]{codeType,codeName},
				null, null, DatabaseHelper.CODE_VALUE+" asc");
		return changeCursorToCodeTableModeleList(cursor).get(0);
	}
	
	/**
	 * 将查询出来的code cursor转换成code model list
	 * @param cursor
	 * @return
	 */
	public List<CodeTableModel> changeCursorToCodeTableModeleList(Cursor cursor){
		List<CodeTableModel> list=new ArrayList();
		while (cursor.moveToNext()) {
			CodeTableModel code=new CodeTableModel();
			if(cursor.getColumnIndex(DatabaseHelper.CODE_TYPE)!=-1)
				code.setCodeType(cursor.getString(cursor.getColumnIndex(DatabaseHelper.CODE_TYPE)));
			if(cursor.getColumnIndex(DatabaseHelper.CODE_NAME)!=-1)
				code.setCodeName(cursor.getString(cursor.getColumnIndex(DatabaseHelper.CODE_NAME)));
			if(cursor.getColumnIndex(DatabaseHelper.CODE_VALUE)!=-1)
				code.setCodeValue(cursor.getString(cursor.getColumnIndex(DatabaseHelper.CODE_VALUE)));
			if(cursor.getColumnIndex(DatabaseHelper.EXTRA_VALUE1)!=-1)
				code.setExtraValue1(cursor.getString(cursor.getColumnIndex(DatabaseHelper.EXTRA_VALUE1)));
			if(cursor.getColumnIndex(DatabaseHelper.EXTRA_VALUE2)!=-1)
				code.setExtraValue2(cursor.getString(cursor.getColumnIndex(DatabaseHelper.EXTRA_VALUE2)));
			list.add(code);
		}
		cursor.close();
		return list;
	}
}
