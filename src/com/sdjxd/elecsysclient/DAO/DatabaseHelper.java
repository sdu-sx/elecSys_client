package com.sdjxd.elecsysclient.DAO;

import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteDatabase.CursorFactory;
import android.database.sqlite.SQLiteOpenHelper;

public class DatabaseHelper extends SQLiteOpenHelper {
	public static final int VERSION = 1;

	public DatabaseHelper(Context context, String name, CursorFactory factory,
			int version) {
		super(context, name, factory, version);
		// TODO Auto-generated constructor stub
	}

	public DatabaseHelper(Context context, String name, int version) {
		this(context, name, null, version);
	}

	public DatabaseHelper(Context context, String name) {
		this(context, name, VERSION);
	}

	@Override
	public void onCreate(SQLiteDatabase db) {
		// TODO Auto-generated method stub
		System.out.println("create a Database");
		String sql;
		//创建表--每个工人所对应的任务id
		sql = "create table taskofworker(wid varchar(15),tid varchar(15),atime datetime,ftime datetime)";
		db.execSQL(sql);
		//创建表--每个任务id的起始时间以及状态
		sql = "create table task (tid varchar(15) primary key,tname varchar(20),stime datetime,etime datetime,state varchar(10))";
		db.execSQL(sql);
		//创建表--每个设备所对应的条目id以及value值
		sql = "create table deviceclauseoftask (tid varchar(15),did varchar(15),cid varchar(15),value varchar(45))";
		db.execSQL(sql);
		//创建表--设备id所对应的设备名，设备地址，设备二维码，设备类型
		sql = "create table device (did varchar(15) primary key,dname varchar(45),twoDimensionCode varchar(100),type varchar(45),address varchar(100))";
		db.execSQL(sql);
		//创建表--员工的详细信息
		sql = "create table worker (wid varchar(15) primary key,wname varchar(45),pwd varchar(15))";
		db.execSQL(sql);
		//创建表--条目和设备的对应关系
		sql = "create table clauseofdevice (did varchar(15),cid varchar(15))";
		db.execSQL(sql);
		//创建表--条目id和条目名称的对应
		sql = "create table clause (cid varchar(15) primary key,cname varchar(45))";
		db.execSQL(sql);
		sql="create table fault (fid varchar(15),did varchar(15),content varcar(100),time datetime)";
		db.execSQL(sql);

	}

	@Override
	public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
		// TODO Auto-generated method stub
		System.out.println("updata database");

	}

}
