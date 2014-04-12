package com.xcorpio.db;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileOutputStream;
import java.io.FileReader;
import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;

import com.xcorpio.entity.User;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.os.Environment;
import android.util.Log;
import android.widget.Toast;

public class DBHelper extends SQLiteOpenHelper{
	private static Context context;
	public static final String DB_NAME="contact";
	public static final int Version=1;
	private static final String DB_TABLENAME = "user";
	
	private static DBHelper instance=null;
	private SQLiteDatabase db;
	
	/** 保存数据 */
	public boolean save(User user){
		openDatabase();
		ContentValues value=new ContentValues();
		value.put("name", user.name);
		value.put("mobilephone", user.mobilePhone);
		value.put("familyphone", user.familyPhone);
		value.put("officephone", user.officePhone);
		value.put("address", user.address);
		value.put("position", user.position);
		value.put("company", user.company);
		value.put("email", user.email);
		value.put("zipcode", user.zipCode);
		value.put("othercontact", user.otherContact);
		value.put("remark", user.remark);
		value.put("imageid", user.imageID);
		Log.i("db", user.name+" : "+user.mobilePhone+"  "+user.officePhone);
		if(db.insert("user", null, value)==-1){
			return false;
		}else{
			return true;
		}
	}
	
	public static synchronized DBHelper getInstance(Context c){
		context=c;
		if(instance==null){
			instance=new DBHelper(context);
		}
		return instance;
	}
	
	private void openDatabase(){
		if(db==null){
			db=this.getWritableDatabase();
		}
	}
	
	private DBHelper(Context context) {
		super(context, DB_NAME, null, Version);
	}

	@Override
	public void onCreate(SQLiteDatabase db) {
		 //创建数据库是建表，只会执行一次
		Log.i("sql", "onCreate");
		StringBuffer tableCreate=new StringBuffer();
		tableCreate.append("create table user ( _id integer primary key autoincrement,")
					.append("name text,")
					.append("mobilephone text,")
					.append("familyphone text,")
					.append("officephone text,")
					.append("address text,")
					.append("position text,")
					.append("company text,")
					.append("email text,")
					.append("zipcode text,")
					.append("othercontact text,")
					.append("remark text,")
					.append("imageid int)");
		db.execSQL(tableCreate.toString());
	}

	@Override
	public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
		Log.i("sql", "upgrade");
		String sql="drop table if exists user";
		db.execSQL(sql);
		onCreate(db);
	}

	public ArrayList<HashMap<String, Object>> getUserList() {
		// TODO Auto-generated method stub
		openDatabase();
		Cursor cursor=db.query("user", null, null, null, null, null, null);
		ArrayList<HashMap<String, Object>> list=new ArrayList<HashMap<String,Object>>();
		while(cursor.moveToNext()){
			HashMap<String, Object> map=new HashMap<String, Object>();
			map.put("imageid", cursor.getInt(cursor.getColumnIndex("imageid")));
			map.put("name", cursor.getString(cursor.getColumnIndex("name")));
			map.put("mobilephone", cursor.getString(cursor.getColumnIndex("mobilephone")));
			map.put("_id", cursor.getInt(cursor.getColumnIndex("_id")));
			map.put("familyphone", cursor.getString(cursor.getColumnIndex("familyphone")));
			map.put("officephone", cursor.getString(cursor.getColumnIndex("officephone")));
			map.put("address", cursor.getString(cursor.getColumnIndex("address")));
			map.put("position", cursor.getString(cursor.getColumnIndex("position")));
			map.put("company", cursor.getString(cursor.getColumnIndex("company")));
			map.put("email", cursor.getString(cursor.getColumnIndex("email")));
			map.put("zipcode", cursor.getString(cursor.getColumnIndex("zipcode")));
			map.put("othercontact", cursor.getString(cursor.getColumnIndex("othercontact")));
			map.put("remark", cursor.getString(cursor.getColumnIndex("remark")));
			list.add(map);
		}
		return list;
	}

	public void modify(User user) {
		ContentValues values = new ContentValues();
		values.put("name", user.name);
		values.put("mobilephone", user.mobilePhone);
		values.put("officephone", user.officePhone);
		values.put("familyphone", user.familyPhone);
		values.put("address", user.address);
		values.put("othercontact", user.otherContact);
		values.put("email",user.email);
		values.put("position", user.position);
		values.put("company", user.company);
		values.put("zipcode", user.zipCode);
		values.put("remark", user.remark);
		values.put("imageid", user.imageID);
		openDatabase();
		db.update(DB_TABLENAME, values, "_id=?", new String[]{String.valueOf(user._id)});
	}
	
	public void delete(int _id) {
		openDatabase();
		db.delete(DB_TABLENAME, "_id=?", new String[]{String.valueOf(_id)});
	}
	
	public void deleteMarked(ArrayList<Integer> list){
		StringBuffer strDel=new StringBuffer();
		strDel.append("_id=");
		for(int i=0;i<list.size();++i){
			if(i!=list.size()-1) {
				strDel.append(list.get(i) + " or _id=");
			} else {
				strDel.append(list.get(i));
			}
		}
		openDatabase();
		db.delete(DB_TABLENAME, strDel.toString(), null);
	}
	
	private void saveDataToFile(String strData, boolean privacy) {
		String fileName = "";
		if (privacy) {
			fileName = "priv_data.bk";
		} else {
			fileName = "comm_data.bk";
		}
		try {
			String SDPATH = Environment.getExternalStorageDirectory() + "/";
			File fileParentPath = new File(SDPATH + "zpContactData/");
			fileParentPath.mkdirs();
			File file = new File(SDPATH + "zpContactData/" + fileName);
			System.out.println("the file previous path = "
					+ file.getAbsolutePath());

			file.createNewFile();
			System.out
					.println("the file next path = " + file.getAbsolutePath());
			FileOutputStream fos = new FileOutputStream(file);

			fos.write(strData.getBytes());
			fos.flush();
		} catch (IOException e) {
			e.printStackTrace();
		}
	}

	public void backupData(boolean privacy) {
		StringBuffer sqlBackup = new StringBuffer();
		Cursor cursor = null;
		openDatabase();
		if(privacy) {
			cursor = db.query(DB_TABLENAME, 
					new String[]{"_id","name","mobilephone","officephone","familyphone","address","othercontact","email","position","company","zipcode","remark","imageid,privacy"}, 
					"privacy=1", 
					null, 
					null, 
					null, 
					null);
		} else {
			cursor = db.query(DB_TABLENAME, 
					new String[]{"_id","name","mobilephone","officephone","familyphone","address","othercontact","email","position","company","zipcode","remark","imageid,privacy"}, 
					"privacy=0",
					null, 
					null, 
					null, 
					null);
		}
		 
		
		while(cursor.moveToNext()) {
			sqlBackup.append("insert into " + DB_TABLENAME + "(name,mobilephone,officephone,familyphone,address,othercontact,email,position,company,zipcode,remark,imageid,privacy)")
			.append(" values ('")
			.append(cursor.getString(cursor.getColumnIndex("name"))).append("','")
			.append(cursor.getString(cursor.getColumnIndex("mobilephone"))).append("','")
			.append(cursor.getString(cursor.getColumnIndex("officephone"))).append("','")
			.append(cursor.getString(cursor.getColumnIndex("familyphone"))).append("','")
			.append(cursor.getString(cursor.getColumnIndex("address"))).append("','")
			.append(cursor.getString(cursor.getColumnIndex("othercontact"))).append("','")
			.append(cursor.getString(cursor.getColumnIndex("email"))).append("','")
			.append(cursor.getString(cursor.getColumnIndex("position"))).append("','")
			.append(cursor.getString(cursor.getColumnIndex("company"))).append("','")
			.append(cursor.getString(cursor.getColumnIndex("zipcode"))).append("','")
			.append(cursor.getString(cursor.getColumnIndex("remark"))).append("',")
			.append(cursor.getInt(cursor.getColumnIndex("imageid"))).append(",")
			.append(cursor.getInt(cursor.getColumnIndex("privacy")))
			.append(");").append("\n");
		}
		saveDataToFile(sqlBackup.toString(),privacy);
		
	}
	
	public void restoreData(String fileName) {
		try {
			openDatabase();
			String SDPATH = Environment.getExternalStorageDirectory() + "/";
			File file = null;
			if (fileName.endsWith(".bk")) {
				file = new File(SDPATH + "zpContactData/" + fileName);
			} else {
				file = new File(SDPATH + "zpContactData/" + fileName + ".bk");
			}
			BufferedReader br = new BufferedReader(new FileReader(file));
			String str = "";
			while ((str = br.readLine()) != null) {
				System.out.println(str);
				db.execSQL(str);
			}

		} catch (Exception e) {
			e.printStackTrace();
		}
	}
	
	public boolean findFile(String fileName) {
		String SDPATH = Environment.getExternalStorageDirectory() + "/";
		File file = null;
		if(fileName.endsWith(".bk")) {
			file = new File(SDPATH + "zpContact/"+fileName);
		} else {
			file = new File(SDPATH + "zpContact/"+fileName + ".bk");
		}
		if(file.exists()) {
			return true;
		} else {
			return false;
		}
	}
	
	public int getTotalCount() {
		openDatabase();
		Cursor cursor = db.query(DB_TABLENAME, new String[]{"count(*)"}, null, null, null, null, null);
		cursor.moveToNext();
		return cursor.getInt(0);
	}
	
	/** 搜索  */ 
	public ArrayList<HashMap<String, Object>> getUsers(String condition) {
		ArrayList<HashMap<String, Object>> list = new ArrayList<HashMap<String, Object>>();
		String sql = "select * from " + DB_TABLENAME + " where 1=1 and (name like '%" + condition + "%' " +
				"or mobilephone like '%" + condition + "%' or familyphone like '%" + condition + "%' " +
						"or officephone like '%" + condition + "%')";
		openDatabase();
		Cursor cursor = db.rawQuery(sql, null);
		while(cursor.moveToNext()) {
			HashMap<String, Object> item = new HashMap<String, Object>();
			item.put("_id", cursor.getInt(cursor.getColumnIndex("_id")));
			item.put("name", cursor.getString(cursor.getColumnIndex("name")));
			item.put("mobilephone", cursor.getString(cursor.getColumnIndex("mobilephone")));
			item.put("officephone", cursor.getString(cursor.getColumnIndex("officephone")));
			item.put("familyphone", cursor.getString(cursor.getColumnIndex("familyphone")));
			item.put("address", cursor.getString(cursor.getColumnIndex("address")));
			item.put("othercontact", cursor.getString(cursor.getColumnIndex("othercontact")));
			item.put("email", cursor.getString(cursor.getColumnIndex("email")));
			item.put("position", cursor.getString(cursor.getColumnIndex("position")));
			item.put("company", cursor.getString(cursor.getColumnIndex("company")));
			item.put("zipcode", cursor.getString(cursor.getColumnIndex("zipcode")));
			item.put("remark", cursor.getString(cursor.getColumnIndex("remark")));
			item.put("imageid", cursor.getInt(cursor.getColumnIndex("imageid")));
			list.add(item);
		}
		return list;
	}
}
