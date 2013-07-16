package com.sdjxd.elecsysclient.activity;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Vector;

import com.sdjxd.elecsysclient.R;
import com.sdjxd.elecsysclient.model.Task;
import com.sdjxd.elecsysclient.model.Task.TaskState;
import com.sdjxd.elecsysclient.model.TaskList;
import com.sdjxd.elecsysclient.model.TaskLite;
import com.sdjxd.elecsysclient.service.ESClientService;
import com.sdjxd.elecsysclient.service.RequestFilter;

import android.app.Activity;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.Button;
import android.widget.ListView;
import android.widget.SimpleAdapter;
import android.widget.TextView;
import android.widget.Toast;

public class HomeActivity extends Activity implements RequestFilter
{
	private static final String TAG="HomeActivity";
	private static final int CACHE=0,EXIT=1;
	private ArrayList<HashMap<String,Object>> data;
	private TaskList undo,done,overtime;
	private SimpleAdapter adapter;
	private Button undoBtn,doneBtn,overtimeBtn,fresh,logout;
	private TextView workerView;
	private ListView listView ;
	private String wid,wname;
	private HomeReceiver receiver;
	private TaskState activeState;
	
	protected void onCreate(Bundle savedInstanceState) 
	{
		super.onCreate(savedInstanceState);
		ActivityManager.getInstance().addActivity(this);
		ActivityManager.getInstance().setHome(this);
		setContentView(R.layout.activity_home);
		findViewById();
		
		String[] key = new String[]{KEY_TNAME,KEY_DEADLINE,KEY_DEVICE_NUM};
		int[] value = new int[]{R.id.H_list_work,R.id.H_list_time,R.id.H_list_device};
		data=new ArrayList<HashMap<String,Object>>();
		adapter = new SimpleAdapter(this, data, R.layout.activity_home_listview,key,value);
		listView.setAdapter(adapter);
		
		Intent intent = getIntent();
		undo = (TaskList) intent.getSerializableExtra(KEY_UNDOLIST);
		done = (TaskList) intent.getSerializableExtra(KEY_DONELIST);
		overtime = (TaskList) intent.getSerializableExtra(KEY_OVERTIMELIST);
		wid = intent.getStringExtra(KEY_WID);
		wname = intent.getStringExtra(KEY_WNAME);
		workerView.setText(wname);
		activeState=TaskState.UNDO;
		
		setListener();
	}
	protected void onStart()
	{
		super.onStart();
		setData(undo);
		receiver = new HomeReceiver();
		IntentFilter filter = new IntentFilter();
		filter.addAction(ACTION_GET_TASKLIST);
		filter.addAction(ACTION_GET_TASK);
		filter.addAction(ACTION_CLEAR_CACHE);
		this.registerReceiver(receiver, filter);
		fresh.performClick();
	}
	protected void onRestart()
	{
		super.onRestart();
	}
	protected void onStop()
	{
		super.onStop();
		this.unregisterReceiver(receiver);
	}
	protected void onDestroy()
	{
		super.onDestroy();
		ActivityManager.getInstance().removeActivity(this);
	}
	public boolean onCreateOptionsMenu(Menu menu) 
	{
		menu.add(Menu.NONE,CACHE,1,R.string.cache);
		menu.add(Menu.NONE,EXIT,2,R.string.exit);
		return super.onCreateOptionsMenu(menu);
	}
	
	@Override
	public boolean onOptionsItemSelected(MenuItem item) 
	{
		if(item.getItemId()==CACHE)
		{
			Intent intent = new Intent(this,ESClientService.class);
			intent.setAction(ACTION_CLEAR_CACHE);
			this.startService(intent);
		}
		else if(item.getItemId()==EXIT)
		{
			ActivityManager.exit();
		}
		return super.onOptionsItemSelected(item);
	}
	private void setData(TaskList tasklist)
	{
		data.clear();
		Vector<TaskLite> tasks=tasklist.getTasks();
		SimpleDateFormat dateFormat=new SimpleDateFormat("yyyy-mm-dd");
		TaskLite task;
		HashMap<String,Object> map;
		for(int i=0;i<tasks.size();i++)
		{
			task=tasks.get(i);
			map = new HashMap<String,Object>();
			map.put(KEY_TNAME, task.tname);
			map.put(KEY_DEADLINE, dateFormat.format(task.deadLine));
			map.put(KEY_DEVICE_NUM, task.deviceNumber);
			data.add(map);
		}
		adapter.notifyDataSetChanged();
	}
	private void setListener()
	{
		HomeListener listener=new HomeListener();
		logout.setOnClickListener(listener);
		fresh.setOnClickListener(listener);
		undoBtn.setOnClickListener(listener);
		doneBtn.setOnClickListener(listener);
		overtimeBtn.setOnClickListener(listener);
		listView.setOnItemClickListener(listener);
	}
	private void findViewById()
	{
		listView = (ListView) this.findViewById(R.id.listview_home);
		undoBtn=(Button) this.findViewById(R.id.home_unfinishedbutton);
		doneBtn = (Button) findViewById(R.id.home_completedbutton);
		overtimeBtn = (Button)findViewById(R.id.home_cancellationbutton);
		fresh = (Button) findViewById(R.id.home_refreshbutton);
		logout = (Button) findViewById(R.id.home_expiredbutton);
		workerView = (TextView) findViewById(R.id.home_worker_name);
	}
	private void startGetUndoList()
	{
		Intent intent=new Intent(this,ESClientService.class);
		intent.setAction(ACTION_GET_TASKLIST);
		intent.putExtra(KEY_WID, wid);
		intent.putExtra(KEY_TASKSTATE,Task.TaskState.UNDO.name());
		startService(intent);
	}
	private void startGetDoneList()
	{
		Intent intent=new Intent(this,ESClientService.class);
		intent.setAction(ACTION_GET_TASKLIST);
		intent.putExtra(KEY_WID, wid);
		intent.putExtra(KEY_TASKSTATE,Task.TaskState.DONE.name());
		startService(intent);
	}
	private void startGetOvertimeList()
	{
		Intent intent=new Intent(this,ESClientService.class);
		intent.setAction(ACTION_GET_TASKLIST);
		intent.putExtra(KEY_WID, wid);
		intent.putExtra(KEY_TASKSTATE,Task.TaskState.OVERTIME.name());
		startService(intent);
	}
	private class HomeListener implements OnClickListener,OnItemClickListener
	{

		@Override
		public void onItemClick(AdapterView<?> parent, View view, int position,long id)
		{
			TaskList tasklist;
			if(activeState.equals(TaskState.UNDO))
			{
				tasklist=undo;
			}
			else if(activeState.equals(TaskState.DONE))
			{
				tasklist=done;
			}
			else
			{
				tasklist=overtime;
			}
			TaskLite task=tasklist.getTask(position);
			
			Intent intent = new Intent(HomeActivity.this,ESClientService.class);
			intent.setAction(ACTION_GET_TASK);
			intent.putExtra(KEY_TID, task.tid);
			HomeActivity.this.startService(intent);
		}

		@Override
		public void onClick(View view) 
		{
			if(view.getId()==R.id.home_cancellationbutton)
			{
				SharedPreferences sp=HomeActivity.this.getSharedPreferences("userInfo", Context.MODE_PRIVATE);
				sp.edit().clear().commit();
				HomeActivity.this.finish();
			}
			else if(view.getId()==R.id.home_refreshbutton)
			{
//				Intent intent =new Intent(ACTION_GET_TASKLIST);
//				intent.setClass(HomeActivity.this, ESClientService.class);
//				intent.putExtra(KEY_WID, wid);
//				intent.putExtra(KEY_TASKSTATE, activeState.name());
//				HomeActivity.this.startService(intent);
				startGetUndoList();
				startGetDoneList();
				startGetOvertimeList();
			}
			else if(view.getId()==R.id.home_unfinishedbutton)
			{
				activeState=TaskState.UNDO;
				setData(undo);
			}
			else if(view.getId()==R.id.home_completedbutton)
			{
				activeState=TaskState.DONE;
				setData(done);
			}
			else
			{
				activeState=TaskState.OVERTIME;
				setData(overtime);
			}
		}
		
	}
	private class HomeReceiver extends BroadcastReceiver
	{

		@Override
		public void onReceive(Context context, Intent intent) 
		{
			String action = intent.getAction();
			if(action.equals(ACTION_GET_TASKLIST))
			{
				showTaskList(intent);
			}
			else if(action.equals(ACTION_GET_TASK))
			{
				showTask(intent);
			}
			else if(action.equals(ACTION_CLEAR_CACHE))
			{
				showClearCache(intent);
			}
		}

		private void showClearCache(Intent intent) 
		{
			String result;
			if(intent.getBooleanExtra(KEY_RESPONSE, false)==true)
			{
				result=intent.getStringExtra(KEY_REPLY);
			}
			else
			{
				result=intent.getStringExtra(KEY_ERROR);
			}
			Toast.makeText(HomeActivity.this, result, Toast.LENGTH_LONG).show();
		}

		private void showTask(Intent intent) 
		{
			if(intent.getBooleanExtra(KEY_RESPONSE, false)==true)
			{
				Task task=(Task) intent.getSerializableExtra(KEY_TASK);
				Intent start = new Intent(Intent.ACTION_VIEW);
				start.setClass(HomeActivity.this, TaskDetails.class);
				start.putExtra(KEY_TASK, task);
				HomeActivity.this.startActivity(start);
			}
			else
			{
				String error = intent.getStringExtra(KEY_ERROR);
				Toast.makeText(HomeActivity.this, error, Toast.LENGTH_LONG).show();
			}
		}

		private void showTaskList(Intent intent) 
		{
			if(intent.getBooleanExtra(KEY_RESPONSE, false)==true)
			{
				TaskList tasklist=(TaskList) intent.getSerializableExtra(KEY_TASKLIST);
				if(tasklist.state.equals(TaskState.UNDO))
				{
					undo=tasklist;
				}
				else if(tasklist.state.equals(TaskState.DONE))
				{
					done=tasklist;
				}
				else 
				{
					overtime=tasklist;
				}
				
				if(tasklist.state.equals(activeState))
				{
					setData(tasklist);
				}
			}
			else
			{
				String result=intent.getStringExtra(KEY_ERROR);
				Toast.makeText(HomeActivity.this, result, Toast.LENGTH_LONG).show();
			}
		}
		
	}
}