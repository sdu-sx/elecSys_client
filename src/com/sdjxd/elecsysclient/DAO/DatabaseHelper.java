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
		//������--ÿ����������Ӧ������id
		sql = "create table taskofworker(wid varchar(15),tid varchar(15),atime datetime,ftime datetime)";
		db.execSQL(sql);
		//������--ÿ������id����ʼʱ���Լ�״̬
		sql = "create table task (tid varchar(15) primary key,tname varchar(20),stime datetime,etime datetime,state varchar(10))";
		db.execSQL(sql);
		//������--ÿ���豸����Ӧ����Ŀid�Լ�valueֵ
		sql = "create table deviceclauseoftask (tid varchar(15),did varchar(15),cid varchar(15),value varchar(45))";
		db.execSQL(sql);
		//������--�豸id����Ӧ���豸�����豸��ַ���豸��ά�룬�豸����
		sql = "create table device (did varchar(15) primary key,dname varchar(45),twoDimensionCode varchar(100),type varchar(45),address varchar(100))";
		db.execSQL(sql);
		//������--Ա������ϸ��Ϣ
		sql = "create table worker (wid varchar(15) primary key,wname varchar(45),pwd varchar(15))";
		db.execSQL(sql);
		//������--��Ŀ���豸�Ķ�Ӧ��ϵ
		sql = "create table clauseofdevice (did varchar(15),cid varchar(15))";
		db.execSQL(sql);
		//������--��Ŀid����Ŀ���ƵĶ�Ӧ
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
