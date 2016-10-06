package com.wlib.core.database;

import java.lang.reflect.Constructor;
import java.lang.reflect.Field;
import java.lang.reflect.Method;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import com.wlib.q.L;

import android.annotation.SuppressLint;
import android.content.Context;
import android.database.Cursor;
import android.database.SQLException;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

public class DBHelper extends SQLiteOpenHelper{
	private static final String DB_NAME = "hwq.db";
	private static final int DB_VERSION = 1;
	private static final String TAG = DBHelper.class.getSimpleName();
	protected SQLiteDatabase db;
	protected static final String ORDER_SQL_BYID = " order by id desc";
	private String order_sql = "";
	public DBHelper(Context context) {
		super(context, DB_NAME, null, DB_VERSION);
	}
	
	public DBHelper(Context context,String dbName,int dbVersion) {
		super(context, dbName, null, dbVersion);
	}

	@Override
	public void onCreate(SQLiteDatabase db) {
		this.db = db;
		L.i(TAG, "onCreate(SQLiteDatabase)");
	}

	@Override
	public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
		L.i(TAG, "onUpgrade(SQLiteDatabase)");
		this.db = db;
	}
	
	/**
	 * 根据方法名获取字段名
	 * @param mName
	 * @return
	 */
	@SuppressLint("DefaultLocale")
	private static String getColumnName(String mName) {
		String fieldName = mName.substring(4, mName.length());  
        String firstLetter = mName.substring(3, 4).toLowerCase();
        fieldName = firstLetter+fieldName;
        return fieldName;
	}

	/** 
     * 解析出保存对象的sql语句 
     *  
     * @param object 
     *            ：需要保存的对象 
     * @return：保存对象的sql语句 
     */  
    @SuppressLint("DefaultLocale")
	@SuppressWarnings({ "rawtypes", "unchecked" })
	private String getSaveObjectSql(Object object) {  
        // 定义一个sql字符串  
        String sql = "insert into ";  
        // 得到对象的类  
        Class<?> c = object.getClass();  
        // 得到对象中所有的方法  
        Method[] methods = c.getMethods();  
        // 得到对象中所有的属性  
//        Field[] fields = c.getFields();  
        // 得到对象类的名字  
        String cName = c.getSimpleName();  
        // 从类的名字中解析出表名  
        String tableName = cName.substring(2,cName.length()).toLowerCase(); 
        sql += tableName + "("; 
        List mList = new ArrayList();  
        List vList = new ArrayList();  
        for (Method method : methods) {  
            String mName = method.getName();  
            if (mName.startsWith("get") && !mName.startsWith("getClass")) {  
            	String fieldName = getColumnName(mName);
                mList.add(fieldName);  
//                System.out.println("字段名字----->" + fieldName);  
                try {  
                    Object value = method.invoke(object); //执行方法返回的值
                    if (value instanceof String) {  
                        vList.add("\"" + value + "\""); //字段值 
                    } else {  
                        vList.add(value);  
                    }  
                } catch (Exception e) {  
                    e.printStackTrace();  
                }  
            }  
        }
        for (int i = 0; i < mList.size(); i++) {  
            if (i < mList.size() - 1) {  
                sql += mList.get(i) + ",";  
            } else {  
                sql += mList.get(i) + ") values(";  
            }  
        }  
        for (int i = 0; i < vList.size(); i++) {  
            if (i < vList.size() - 1) {  
                sql += vList.get(i) + ",";  
            } else {  
                sql += vList.get(i) + ")";  
            }  
        }  
  
        return sql;  
    }  
    
    /**
     * 插入一条记录
     * @param object
     */
    protected void insertRec(Object object) {
    	openDb();
    	String sql = getSaveObjectSql(object);
    	try {
    		db.execSQL(sql);
		} catch (SQLException e) {
			L.e(TAG, "e = "+e);
		}
    }
    /**
     * 替换一条数据
     * @param tbName 表名
     * @param map 影响列
     */
    protected void replaceRec(String tbName,Map<String, Object> map) {
		openDb();
		String refColumn = "";
		String refValue = "";
		for (String key : map.keySet()) {
			if (key.equals("id")) {
				continue;
			}
			refColumn += key +",";
			Object value = map.get(key);
			if (value instanceof String) {
				refValue +="\'"+value+"\'"+",";
			}else {
				refValue += value+",";
			}
		}
		if (!refColumn.contains("id")) {
			L.e(TAG,"replace without id");
		}
		refColumn = refColumn.substring(0, refColumn.length() - 1);
		refValue = refValue.substring(0,refValue.length() - 1);
		String sql = "replace into "+tbName+" ("+refColumn+") values ("+refValue+")";
		L.i("", "sql = "+sql);
		try {
    		db.execSQL(sql);
		} catch (SQLException e) {
			L.e(TAG, "e = "+e);
		}
	}
    
    /**
     * 遍历需要数据id
     * @param tbName
     * @param ids
     * @return
     */
    protected Cursor queryRecId(String tbName) {
    	return queryRecId(tbName, false);
	}
    
    /**
     * 遍历需要数据id
     * @param tbName
     * @param ids
     * @return
     */
    protected Cursor queryRecId(String tbName,boolean isOrder) {
    	openDb();
    	Cursor cursor = null;
    	String sql = "select id from "+tbName+ (isOrder?getOrder_sql():" ");
    	try {
    		cursor = db.rawQuery(sql, null);
		} catch (SQLException e) {
			L.e(TAG, "e = "+e);
		}
    	setOrder_sql("");
    	return cursor;
	}
    
    /**
     * 获取最大id
     * @param tbName
     * @param ids
     * @return
     */
    protected int queryRecMaxId(String tbName) {
    	openDb();
    	Cursor cursor = null;
    	String sql = "select max(id) from "+tbName;
    	try {
    		cursor = db.rawQuery(sql, null);
        	if (cursor!=null) {
    			while (cursor.moveToNext()) {
    				int id = cursor.getInt(0);
    				return id;
    			}
    		}
		} catch (SQLException e) {
			L.e(TAG, "e = "+e);
		}
    	return 0;
	}
    
    /**
     * 根据id获取记录
     * @param tbName
     * @param ids
     * @return
     */
    protected Cursor queryRecBYId(String tbName,int id) {
    	openDb();
    	Cursor cursor = null;
    	String sql = "select * from "+tbName+" where id = "+id;
    	try {
    		cursor = db.rawQuery(sql, null);
		} catch (SQLException e) {
			L.e(TAG, "e = "+e);
		}
    	return cursor;
	}
    
    /**
     * 遍历需要数据 ,暂时限量30
     * @param tbName
     * @param ids
     * @return
     */
    protected Cursor queryRec(String tbName,ArrayList<Integer> ids) {
    	return queryRec(tbName, ids, false);
	}
    
    /**
     * 遍历需要数据 ,暂时限量30
     * @param tbName
     * @param ids
     * @return
     */
    protected Cursor queryRec(String tbName,ArrayList<Integer> ids,boolean isOrder) {
    	openDb();
    	Cursor cursor = null;
    	String refId = "where ";
    	if (ids == null) {
    		refId = "";
		}else if (ids.size() == 1) {
			refId += " id = "+ids.get(0);
		}else if (ids.size() >= 1) {
			String temp = "";
			temp = "(";
			for (int id : ids) {
				temp += id+"," ;
			}
			temp =  temp.substring(0, temp.length()-1);
			temp +=")";
			refId += " id in "+temp;
		}
    	String sql = "select * from "+tbName+" "+refId +(isOrder?getOrder_sql():"");
    	try {
    		cursor = db.rawQuery(sql, null);
		} catch (SQLException e) {
			L.e(TAG, "e = "+e);
		}
    	setOrder_sql("");
    	return cursor;
	}
    
    /**
     * 获取对应数据数量
     * @param tbName
     * @param map 字段名、or/and 标识 、修改内容
     * @return
     */
    protected int getRecNum(String tbName) {
    	return getRecNum(tbName, null);
    }
    
    /**
     * 获取对应数据数量
     * @param tbName
     * @param map 字段名、or/and 标识 、修改内容
     * @return
     */
    protected int getRecNum(String tbName , Map<String, Map<Integer, Object>> mapMap) {
    	String whereStr = " where ";
    	String refColumn = "";
    	if (mapMap!=null) {
		for (String mapKey : mapMap.keySet()) {
			Map<Integer, Object> tempMap = mapMap.get(mapKey);
			for (Integer key : tempMap.keySet()) {
				refColumn +=  key == 0?" or  ":" and " + mapKey +" = "+ tempMap.get(key);
			}
		}
		}
		if (!refColumn.equals("")) {//加条件
			refColumn = refColumn.substring(5, refColumn.length());
			refColumn = whereStr + refColumn;
		}
		String sql = "select count(*) from "+tbName+refColumn;
		Cursor cursor = null;
		try {
    		cursor = db.rawQuery(sql, null);
		} catch (SQLException e) {
			L.e(TAG, "e = "+e);
		}
		if (cursor!=null) {
			while (cursor.moveToNext()) {
				Integer total = cursor.getInt(0);
				return total;
			}
		}
		
		cursor.close();
		return 0;
	}
    
    
    /** 
     * 把值设置进类属性里 
     *  
     * @param columnNames 
     * @param fields 
     * @param c 
     * @param obj 
     * @throws Exception 
     */  
    @SuppressWarnings("rawtypes")  
    protected Object setValues2Fields(Cursor c, Class clazz)  
            throws Exception {  
        String[] columnNames = c.getColumnNames();// 字段数组  
        Object obj = clazz.newInstance();  
        Field[] fields = clazz.getDeclaredFields();  
  
        for (Field field : fields) {  
            Class<? extends Object> typeClass = field.getType();// 属性类型  
            for (int j = 0; j < columnNames.length; j++) {  
                String columnName = columnNames[j];  
                typeClass = getBasicClass(typeClass);  
                boolean isBasicType = isBasicType(typeClass);  
  
                if (isBasicType) {  
                    if (columnName.equalsIgnoreCase(field.getName())) {// 是基本类型  
                        String str = c.getString(c.getColumnIndex(columnName));  
                        if (str == null) {  
                            break;  
                        }  
                        str = str == null ? "" : str;  
                        Constructor<? extends Object> cons = typeClass  
                                .getConstructor(String.class);  
                        Object attribute = cons.newInstance(str);  
                        field.setAccessible(true);  
                        field.set(obj, attribute);  
                        break;  
                    }  
                } else {  
                    Object obj2 = setValues2Fields(c, typeClass);// 递归  
                    field.set(obj, obj2);  
                    break;  
                }  
  
            }  
        }  
        return obj;  
    }  
    
    /** 
     * 判断是不是基本类型 
     *  
     * @param typeClass 
     * @return 
     */  
    @SuppressWarnings("rawtypes")  
    protected boolean isBasicType(Class typeClass) {  
        if (typeClass.equals(Integer.class) || typeClass.equals(Long.class)  
                || typeClass.equals(Float.class)  
                || typeClass.equals(Double.class)  
                || typeClass.equals(Boolean.class)  
                || typeClass.equals(Byte.class)  
                || typeClass.equals(Short.class)  
                || typeClass.equals(String.class)) {  
  
            return true;  
  
        } else {  
            return false;  
        }  
    }  
    
    @SuppressWarnings("rawtypes")  
    private static Map<Class, Class> basicMap = new HashMap<Class, Class>();  
    static {  
        basicMap.put(int.class, Integer.class);  
        basicMap.put(long.class, Long.class);  
        basicMap.put(float.class, Float.class);  
        basicMap.put(double.class, Double.class);  
        basicMap.put(boolean.class, Boolean.class);  
        basicMap.put(byte.class, Byte.class);  
        basicMap.put(short.class, Short.class);  
    }  
  
    /** 
     * 获得包装类 
     *  
     * @param typeClass 
     * @return 
     */  
    @SuppressWarnings("all")  
    protected Class<? extends Object> getBasicClass(Class typeClass) {  
        Class clazz = basicMap.get(typeClass);  
        if (clazz == null)  
            clazz = typeClass;  
        return clazz;  
    }  

	/**
	 * 根据id删除记录
	 * @param tbName
	 * @param ids
	 */
	protected void delRecByIds(String tbName,ArrayList<Integer> ids) {
		openDb();
		if (ids == null) {
			try {
				db.execSQL("delete from "+tbName);
			} catch (SQLException e) {
				L.e(TAG, "e = "+e);
			}
		}else if (ids.size() ==1) {
			try {
				db.execSQL("delete from "+tbName+" where id="+ids.get(0));
			} catch (SQLException e) {
				L.e(TAG, "e = "+e);
			}
		}else if (ids.size() >= 1) {
			String idsql = "";
			for (int id : ids) {
				idsql += id+"," ;
			}
			idsql =  idsql.substring(0, idsql.length()-1);
			idsql ="("+idsql+")";
			try {
				db.execSQL("delete from "+tbName+" where id in "+idsql);
			} catch (SQLException e) {
				L.e(TAG, "e = "+e);
			}
		}
	}
	
	/**
	 * 根据id更新map的内容
	 * @param tbName
	 * @param map
	 * @param ids
	 */
	protected void updateRecByIds(String tbName,Map<String, Object> map,ArrayList<Integer> ids){
		openDb();
		String refColumn = "";
		String refId = "";
		for (String key : map.keySet()) {
			refColumn += key +" = "+ map.get(key) +",";
		}
		refColumn = refColumn.substring(0, refColumn.length() - 1);
		if (ids.size() == 1) {
			refId = " id = "+ids.get(0);
		}else if (ids.size() >= 1) {
			refId = "(";
			for (int id : ids) {
				refId += id+"," ;
			}
			refId = refId.substring(0, refId.length()-1);
			refId +=")";
			refId = " id in "+refId;
		}
		try {
			db.execSQL("update "+tbName+" set "+refColumn+" where "+refId);
		} catch (SQLException e) {
			L.e(TAG, "e = "+e);
		}
	}
	
	/**
	 * 开启数据库
	 */
	public void openDb() {
		try {
		if (db ==null) {
			db = getWritableDatabase();
		}
		} catch (Exception e) {
			// TODO: handle exception
		}
	}
	
	/**
	 * 关闭数据库
	 */
	public void closeDb() {
		try {
			if (db!=null) {
				db.close();
			}
		} catch (Exception e) {
			// TODO: handle exception
		}
	}
	
	public boolean isDBopen() {
		if (db!=null) {
			return db.isOpen();
		}
		return false;
	}
	
	/**
	 * 事务开始
	 */
	protected void beginTransaction() {
		if (db!=null) {
			db.beginTransaction();
		}
	}
	
	/**
	 * 事务结束
	 * @param isSuc
	 */
	protected void endTransaction(boolean isSuc) {
		if (db==null) {
			return;
		}
		if (isSuc) {
			db.setTransactionSuccessful();
		}
		db.beginTransaction();
	}

	protected String getOrder_sql() {
		return order_sql;
	}

	protected void setOrder_sql(String order_sql) {
		this.order_sql = order_sql;
	}
}
