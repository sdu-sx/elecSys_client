package com.sdjxd.elecsysclient.DAO;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.*;

import com.sdjxd.elecsysclient.model.*;
import com.sdjxd.elecsysclient.model.Task.TaskState;
import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.SQLException;
import android.database.sqlite.SQLiteDatabase;
import android.renderscript.ProgramFragmentFixedFunction.Builder.Format;

public class SqlSupport {
	final String dbname = "projectdatebase";

	/******************************************************************************************************************/
	// 根据taskstate获取轻量级的任务列表，并且返回最晚的任务时间
	public Date getTasklist(Context context, TaskList tasklist,
			TaskState state, String wid) {
		String taskstate = state.name();
		String releaseTime, deadline, etime;
		TaskLite task;
		SimpleDateFormat format = new SimpleDateFormat("yyyy-MM-dd");
		Date redate = null;
		Date edate = null;
		Date ddate = null;
		Vector<TaskLite> list = new Vector<TaskLite>();
		DatabaseHelper dbHelper = new DatabaseHelper(context, dbname);
		SQLiteDatabase db = dbHelper.getReadableDatabase();
		Cursor cursor = db.query("taskofworker,task", new String[] { "wid",
				"task.tid", "ftime", "state", "atime", "tname", "etime" },
				"wid=? and task.tid=taskofworker.tid and state =?",
				new String[] { wid, taskstate }, null, null, "atime ASC");
		while (cursor.moveToNext()) {
			task = new TaskLite();
			task.tid = cursor.getString(cursor.getColumnIndex("tid"));
			deadline = cursor.getString(cursor.getColumnIndex("ftime"));
			releaseTime = cursor.getString(cursor.getColumnIndex("atime"));
			task.tname = cursor.getString(cursor.getColumnIndex("tname"));
			etime = cursor.getString(cursor.getColumnIndex("etime"));

			try {
				if (deadline != null) {
					ddate = format.parse(deadline);
				}
				task.deadLine = ddate;
				if (releaseTime != null) {
					redate = format.parse(releaseTime);
				}
				task.releaseTime = redate;
				if (etime != null) {
					edate = format.parse(etime);
				}
			} catch (ParseException e) {
				// TODO Auto-generated catch block
				System.out.println("parse error---------------");
				e.printStackTrace();
			}
			task.state = state;
			list.add(task);
		}
		cursor.close();
		for (int i = 0; i < list.size(); i++) {
			String tid = list.get(i).tid;
			Cursor cursor_1 = db.query("deviceclauseoftask",
					new String[] { "count(DISTINCT  did) as devicenum" },
					"tid=?", new String[] { tid }, "tid", null, null);
			if (cursor_1.moveToNext()) {
				list.get(i).deviceNumber = cursor_1.getInt(cursor_1
						.getColumnIndex("devicenum"));
			}
			cursor_1.close();
		}
		tasklist.setTasks(list);
		db.close();
		if (taskstate.equals("DONE")) {
			if (edate != null) {
				return edate;
			}
		} else if (taskstate.equals("UNDO")) {
			if (redate != null) {
				return redate;
			}
		} else if (taskstate.equals("OVERTIME")) {
			if (ddate != null) {
				return ddate;
			}
		}
		return null;
	}

	// 向本地数据库插入任务
	public boolean SetTasklist(Context context, TaskList tasklist) {
		boolean result = true;
		String wid, state;
		String tid, stime = null, atime = null, ftime = null;
		TaskLite task;
		SimpleDateFormat format = new SimpleDateFormat("yyyy-MM-dd");
		Date date = null;

		Vector<TaskLite> list = tasklist.getTasks();
		wid = tasklist.wid;

		DatabaseHelper dbHelper = new DatabaseHelper(context, dbname);
		SQLiteDatabase db = dbHelper.getWritableDatabase();
		for (int i = 0; i < list.size(); i++) {
			task = list.get(i);
			tid = task.tid;

			date = task.releaseTime;
			if (date != null) {
				atime = format.format(date);
			}
			date = task.deadLine;
			state = task.state.name();
			if (date != null) {
				ftime = format.format(date);
			}
			stime = atime;
			ContentValues value = new ContentValues();
			value.put("wid", wid);
			value.put("tid", tid);
			value.put("atime", atime);
			value.put("ftime", ftime);
			db.execSQL("delete from taskofworker where tid=\"" + tid
					+ "\" and wid=\"" + wid + "\"");
			long res;
			try {
				res = db.replace("taskofworker", null, value);
				if (res < 0) {
					result = false;
				}
			} catch (Exception e) {
				// TODO: handle exception
				System.out.println("重复插入");
			}

			value.clear();
			value.put("tid", tid);
			value.put("stime", stime);
			value.put("state", state);
			value.put("tname", task.tname);
			try {
				res = db.replace("task", null, value);
				value.clear();
				if (res < 0) {
					result = false;
				}

			} catch (Exception e) {
				// TODO: handle exception
				System.out.println("重复插入");
			}

		}
		db.close();
		return result;
	}

	// 获取任务详细信息，包含轻量级的设备列表
	public Task getTask(Context context, String tid) {
		Task task = new Task();
		Vector<DeviceLite> list = new Vector<DeviceLite>();
		DeviceLite device;
		String stime, ftime,deadline;
		String taskState;
		int Devicenum = 0;
		SimpleDateFormat format = new SimpleDateFormat("yyyy-MM-dd");
		DatabaseHelper dbHelper = new DatabaseHelper(context, dbname);
		SQLiteDatabase db = dbHelper.getReadableDatabase();
		Cursor cursor_1 = db.query("task,taskofworker", new String[] {
				"task.tid", "tname", "stime", "etime", "atime", "ftime","stime",
				"state" }, "task.tid=? and task.tid=taskofworker.tid",
				new String[] { tid }, null, null, null);
		if (cursor_1.getCount() == 0) {
			cursor_1.close();
			db.close();
			return null;
		} else
			while (cursor_1.moveToNext()) {
				task.tid = tid;

				try {
					stime = cursor_1
							.getString(cursor_1.getColumnIndex("atime"));
					task.releaseTime = format.parse(stime);
					ftime = cursor_1
							.getString(cursor_1.getColumnIndex("ftime"));
					task.finishTime = format.parse(ftime);
					task.tname = cursor_1.getString(cursor_1
							.getColumnIndex("tname"));
					deadline=cursor_1.getString(cursor_1.getColumnIndex("ftime"));
					task.deadLine=format.parse(deadline);

				} catch (ParseException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}
				taskState = cursor_1
						.getString(cursor_1.getColumnIndex("state"));
				if (taskState.equals("DONE")) {
					task.state = TaskState.DONE;
				} else if (taskState.equals("UNDO")) {
					task.state = TaskState.UNDO;
				} else if (taskState.equals("OVERTIME")) {
					task.state = TaskState.OVERTIME;
				}
			}
		cursor_1.close();
		Cursor cursor_2 = db
				.query("deviceclauseoftask,device",
						new String[] { "distinct deviceclauseoftask.did,dname,type,address,twoDimensionCode" },
						"tid=? and deviceclauseoftask.did = device.did",
						new String[] { tid }, null, null, null);
		if (cursor_2.getCount() == 0) {
			cursor_2.close();
			db.close();
			return null;
		} else {
			while (cursor_2.moveToNext()) {
				device = new DeviceLite();
				device.did = cursor_2.getString(cursor_2.getColumnIndex("did"));
				device.dName = cursor_2.getString(cursor_2
						.getColumnIndex("dname"));
				device.dType = cursor_2.getString(cursor_2
						.getColumnIndex("type"));
				device.dAddress = cursor_2.getString(cursor_2
						.getColumnIndex("address"));
				device.isCheck=this.ischecked(context, tid, device.did);
				
				list.add(device);
				Devicenum++;
			}
			task.setDevices(list);
			task.deviceNum = Devicenum;
		}
		cursor_2.close();
		db.close();
		return task;
	}

	// 向本地数据库跟新任务详细信息和任务的设备列表
	public boolean setTask(Context context, Task task) {
		DatabaseHelper dbHelper = new DatabaseHelper(context, dbname);
		SQLiteDatabase db = dbHelper.getWritableDatabase();
		SimpleDateFormat format = new SimpleDateFormat("yyyy-MM-dd");
		Date date = null;

		Vector<DeviceLite> list = task.getDevices();

		ContentValues value = new ContentValues();
		value.put("tid", task.tid);
		value.put("state", task.state.name());
		date = task.finishTime;
		if (date != null) 
		{	
			value.put("etime", format.format(date));
		}
		value.put("tname", task.tname);
		db.replace("task", null, value);
		value.clear();
		for (int i = 0; i < list.size(); i++) {
			value.put("did", list.get(i).did);
			value.put("dname", list.get(i).dName);
			value.put("type", list.get(i).dType);
			value.put("address", list.get(i).dAddress);
			db.replace("device", null, value);
			value.clear();
			value.put("tid", task.tid);
			value.put("did", list.get(i).did);
			db.execSQL("delete from deviceclauseoftask where tid=\"" + task.tid
					+ "\" and did=\"" + list.get(i).did + "\"");
			db.insert("deviceclauseoftask", null, value);
			value.clear();

		}
		db.close();

		return true;
	}

	// 跟新任务完成状态
	public boolean setTaskstate(Context context, TaskState state, String tid) {

		DatabaseHelper dbHelper = new DatabaseHelper(context, dbname);
		SQLiteDatabase db = dbHelper.getWritableDatabase();
		String sql = "update task set state=\"" + state.name()
				+ "\" where tid=\"" + tid + "\"";
		db.execSQL(sql);
		db.close();

		return true;
	}

	// 设置任务开始做的时间
	public boolean setStarttime(Context context, String tid, Date time) {
		DatabaseHelper dbHelper = new DatabaseHelper(context, dbname);
		SQLiteDatabase db = dbHelper.getWritableDatabase();
		SimpleDateFormat format = new SimpleDateFormat("yyyy-MM-dd");

		String sql = "update task set stime=\"" + format.format(time)
				+ "\" where tid=\"" + tid + "\"";

		db.close();

		return true;
	}

	public void setEtime(Context context, String tid, Date time) {
		DatabaseHelper dbHelper = new DatabaseHelper(context, dbname);
		SQLiteDatabase db = dbHelper.getWritableDatabase();
		SimpleDateFormat format = new SimpleDateFormat("yyyy-MM-dd");

		String sql = "update task set etime=\"" + format.format(time)
				+ "\" where tid=\"" + tid + "\"";

		db.close();
	}

	// 实现记住密码功能，根据wid获得pwd
	public Worker getWorker(Context context, String wid) {
		DatabaseHelper dbHelper = new DatabaseHelper(context, dbname);
		SQLiteDatabase db = dbHelper.getReadableDatabase();
		Worker worker = new Worker();
		Cursor cursor = db.query("worker",
				new String[] { "wid", "wname", "pwd" }, "wid=?",
				new String[] { wid }, null, null, null);
		if (cursor.getCount() == 0) {
			worker = null;
		} else {
			while (cursor.moveToNext()) {
				worker.wid = wid;
				worker.pwd = cursor.getString(cursor.getColumnIndex("wid"));
				worker.wname = cursor.getString(cursor.getColumnIndex("wname"));
			}
		}
		cursor.close();
		db.close();

		return worker;
	}

	// 获取设备的详细信息，包括该设备的选项卡列表
	public Device getDevice(Context context, String did, String tid) {
		DatabaseHelper dbHelper = new DatabaseHelper(context, dbname);
		SQLiteDatabase db = dbHelper.getReadableDatabase();
		Device device = new Device();
		device.did = did;

		Cursor cursor = db.query("device", new String[] { "did", "dname",
				"twoDimensionCode", "type", "address" }, "did=?",
				new String[] { did }, null, null, null);
		while (cursor.moveToNext()) {
			device.dName = cursor.getString(cursor.getColumnIndex("dname"));
			device.dType = cursor.getString(cursor.getColumnIndex("type"));
			device.dAddress = cursor
					.getString(cursor.getColumnIndex("address"));
			device.QRCode = cursor.getString(cursor
					.getColumnIndex("twoDimensionCode"));

		}
		cursor.close();
		// 获取选项卡列表信息
		Vector<CheckCard> Checklist = new Vector<CheckCard>();
		CheckCard checkcard;
		Cursor cursor_2 = db.query("deviceclauseoftask,clause", new String[] {
				"clause.cid", "cname", "value" },
				"tid=? and did=? and deviceclauseoftask.cid=clause.cid",
				new String[] { tid, did }, null, null, null);
		if (cursor_2.getCount() == 0) {
			cursor_2.close();
			db.close();
			return null;
		} else {
			while (cursor_2.moveToNext()) {
				checkcard = new CheckCard();
				checkcard.cid = cursor_2.getString(cursor_2
						.getColumnIndex("cid"));
				checkcard.cName = cursor_2.getString(cursor_2
						.getColumnIndex("cname"));
				checkcard.cValue = cursor_2.getString(cursor_2
						.getColumnIndex("value"));
				if(checkcard.cValue==null)
				{
					checkcard.isCheck=false;
				}
				else
				{
					checkcard.isCheck=true;
				}
				Checklist.add(checkcard);
			}
			device.setCheckCards(Checklist);
		}
		cursor_2.close();
		db.close();
		return device;
	}

	// 向本地数据库插入二维码信息，并且跟新deviceclauseoftask表，将设备对应的条目项录入
	public boolean setDevice(Context context, Device device, String tid) {
		String qrcode;
		String sql;
		boolean result = true;
		long res;
		DatabaseHelper dbHelper = new DatabaseHelper(context, dbname);
		SQLiteDatabase db = dbHelper.getWritableDatabase();
		qrcode = device.QRCode;
		sql = "update device set twoDimensionCode=\"" + qrcode
				+ "\" where did=\"" + device.did + "\"";
		db.execSQL(sql);
		sql = "delete from deviceclauseoftask where tid=\"" + tid
				+ "\" and did=\"" + device.did + "\"";
		db.execSQL(sql);
		ContentValues value_1 = new ContentValues();
		ContentValues value_2 = new ContentValues();
		for (int i = 0; i < device.getCheckCards().size(); i++) {
			value_1.put("tid", tid);
			value_1.put("did", device.did);
			value_1.put("cid", device.getCheckCards().get(i).cid);
			value_1.put("value", device.getCheckCards().get(i).cValue);
			value_2.put("cid", device.getCheckCards().get(i).cid);
			value_2.put("cname", device.getCheckCards().get(i).cName);
			res = db.insert("deviceclauseoftask", null, value_1);
			if(device.getCheckCards().get(i).cValue==null)
			{
				device.getCheckCards().get(i).isCheck=false;
			}
			else
			{
				device.getCheckCards().get(i).isCheck=true;
			}
			if (res < 0) {
				result = false;
			}
			res = db.replace("clause", null, value_2);
			if (res < 0) {
				result = false;
			}
			value_1.clear();
			value_2.clear();
		}
		db.close();

		return result;
	}

	// 在本地数据库记录条目值
	public boolean saveResult(Context context, String tid, Device device) {
		String sql;
		DatabaseHelper dbHelper = new DatabaseHelper(context, dbname);
		SQLiteDatabase db = dbHelper.getWritableDatabase();
		Vector<CheckCard> list;
		list = device.getCheckCards();
		try {

			for (int i = 0; i < list.size(); i++) {
				sql = "update deviceclauseoftask set value=\""
						+ list.get(i).cValue + "\" where tid=\"" + tid
						+ "\" and did=\"" + device.did + "\" and cid=\""
						+ list.get(i).cid + "\"";
				db.execSQL(sql);

			}
		} catch (SQLException e) {
			// TODO: handle exception
			System.out.println("sql exception----------");
			return false;
		}
		db.close();
		return true;
	}

	// 获取缺陷历史
	public Date getFaultHistory(Context context, FaultHistory faulthistory,
			String did) {
		String time = null;
		Date date = null;
		DatabaseHelper dbHelper = new DatabaseHelper(context, dbname);
		SQLiteDatabase db = dbHelper.getReadableDatabase();
		faulthistory.setFaults(new Vector<Fault>());
		Fault fault;
		SimpleDateFormat format = new SimpleDateFormat("yyyy-MM-dd");
		Cursor cursor = db.query("fault", new String[] { "fid", "did",
				"content", "time" }, "did=?", new String[] { did }, null, null,
				"time ASC");
		faulthistory.did = did;
		while (cursor.moveToNext()) {
			fault = new Fault();
			fault.fid = cursor.getString(cursor.getColumnIndex("fid"));
			fault.did = did;
			fault.content = cursor.getString(cursor.getColumnIndex("content"));
			time = cursor.getString(cursor.getColumnIndex("time"));
			try {
				fault.date = format.parse(time);
			} catch (ParseException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
			faulthistory.getFaults().add(fault);

		}
		if (time == null) {
			cursor.close();
			db.close();
			return null;
		}
		try {
			date = (Date) format.parse(time);
		} catch (ParseException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		cursor.close();
		db.close();
		return date;
	}

	// 记录缺陷
	public boolean addFault(Context context, Fault fault, String did) {
		DatabaseHelper dbHelper = new DatabaseHelper(context, dbname);
		SQLiteDatabase db = dbHelper.getWritableDatabase();
		String time;
		SimpleDateFormat format = new SimpleDateFormat("yyyy-MM-dd");
		time = format.format(fault.date);
		ContentValues value = new ContentValues();
		value.put("fid", fault.fid);
		value.put("did", fault.did);
		value.put("content", fault.content);
		value.put("time", time);
		try {
			db.insert("fault", null, value);

		} catch (SQLException e) {
			// TODO: handle exception
			System.out.println("addfault sqlexception");
			db.close();
			return false;
		}
		db.close();
		return true;
	}

	public boolean ReState(Context context) {

		Date currentdate = new Date();
		String tid, deadline;
		DatabaseHelper dbHelper = new DatabaseHelper(context, dbname);
		SQLiteDatabase db = dbHelper.getWritableDatabase();
		SimpleDateFormat format = new SimpleDateFormat("yyyy-MM-dd");
		Cursor cursor = db.query("taskofworker",
				new String[] { "tid", "ftime" }, null, null, null, null, null);
		while (cursor.moveToNext()) {
			deadline = cursor.getString(cursor.getColumnIndex("ftime"));
			try {
				if (deadline != null) {
					if (format.parse(deadline).compareTo(currentdate) < 0) {
						tid = cursor.getString(cursor.getColumnIndex("tid"));
						String sql = "update task set state=\"OUTDATED\" where tid=\""
								+ tid + "\"";
					}
				}
			} catch (ParseException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
		}
		cursor.close();
		db.close();
		return false;
	}

	public boolean SetWorker(Context context, Worker worker) {
		DatabaseHelper dbHelper = new DatabaseHelper(context, dbname);
		SQLiteDatabase db = dbHelper.getWritableDatabase();
		ContentValues value = new ContentValues();
		value.put("wid", worker.wid);
		value.put("wname", worker.wname);
		value.put("pwd", worker.pwd);
		long res = db.replace("worker", null, value);
		db.close();
		if (res < 0) {
			db.close();
			return false;
		}
		db.close();
		return true;

	}

	public void Setendtime(Context context, String tid, Date etime) {
		DatabaseHelper dbHelper = new DatabaseHelper(context, dbname);
		SQLiteDatabase db = dbHelper.getWritableDatabase();
		SimpleDateFormat format = new SimpleDateFormat("yyyy-MM-dd");

		String sql = "update task set etime=\"" + format.format(etime)
				+ "\" where tid=\"" + tid + "\"";
		db.execSQL(sql);
		db.close();

	}

	public String getwid(Context context, String tid) {
		String wid = null;
		DatabaseHelper dbHelper = new DatabaseHelper(context, dbname);
		SQLiteDatabase db = dbHelper.getWritableDatabase();
		Cursor cursor = db.query("taskofworker", new String[] { "wid" },
				"tid=?", new String[] { tid }, null, null, null);
		if (cursor.moveToNext()) {
			wid = cursor.getString(cursor.getColumnIndex("wid"));
		}
		cursor.close();
		db.close();
		return wid;
	}

	public void Emptydata(Context context) {
		String sql;
		DatabaseHelper dbHelper = new DatabaseHelper(context, dbname);
		SQLiteDatabase db = dbHelper.getWritableDatabase();
		sql = "delete from taskofworker";
		db.execSQL(sql);
		sql = "delete from task";
		db.execSQL(sql);
		sql = "delete from deviceclauseoftask";
		db.execSQL(sql);
		sql = "delete from device";
		db.execSQL(sql);
		sql = "delete from worker";
		db.execSQL(sql);
		sql = "delete from clauseofdevice";
		db.execSQL(sql);
		sql = "delete from clause";
		db.execSQL(sql);
		sql = "delete from fault";
		db.execSQL(sql);
		db.close();

	}

	private boolean ischecked(Context context, String tid, String did) {
		boolean ischecked = true;
		DatabaseHelper dbHelper = new DatabaseHelper(context, dbname);
		SQLiteDatabase db = dbHelper.getWritableDatabase();
		Cursor cursor = db.query("deviceclauseoftask", new String[] { "tid",
				"did", "cid" }, "tid=? and did=?", new String[] { tid, did },
				null, null, null);
		if(cursor.getCount()==0)
		{
			cursor.close();
			db.close();
			return false;
		}
		else 
			while(cursor.moveToNext())
		{
			if(cursor.getString(cursor.getColumnIndex("cid"))==null)
			{
				ischecked=false;
			}
		}
		cursor.close();
		db.close();
		return ischecked;
	}

}
