package com.sdjxd.elecsysclient.activity;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import com.Zxing.Demo.CaptureActivity;
import com.sdjxd.elecsysclient.R;
import com.sdjxd.elecsysclient.adapter.TaskAdapter;
import com.sdjxd.elecsysclient.model.Device;
import com.sdjxd.elecsysclient.model.Task;
import com.sdjxd.elecsysclient.model.Task.TaskState;
import com.sdjxd.elecsysclient.service.ESClientService;
import com.sdjxd.elecsysclient.service.RequestFilter;



import android.app.Activity;
import android.app.AlertDialog;
import android.app.AlertDialog.Builder;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.AdapterView.OnItemSelectedListener;
import android.widget.BaseAdapter;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.SimpleAdapter;
import android.widget.TextView;
import android.widget.Toast;


public class TaskDetails extends Activity implements RequestFilter
{
	private static final String TAG="TaskDetails";
	private static final int CACHE=0,EXIT=1;
	private TaskAdapter adapter;
	private Task task;	
	private Button backBtn,homeBtn,summitBtn,detailBtn;
	private ListView listview;
	private TaskReceiver receiver;
	private boolean[] hasCheck;
	@Override
	protected void onCreate(Bundle savedInstanceState) 
	{
		super.onCreate(savedInstanceState);
		ActivityManager.getInstance().addActivity(this);
		setContentView(R.layout.activity_taskdetails);
		Intent intent = getIntent();
		task=(Task) intent.getSerializableExtra(KEY_TASK);
		hasCheck = new boolean[task.deviceNum];
		Log.d(TAG, "taskname:"+task.tname);
		Log.d(TAG, "count:"+task.getDevices().size());
		adapter=new TaskAdapter(this,
								task.getDevices(),
								R.layout.activity_taskdetails_listview,
								R.id.T_list_device,
								R.id.T_list_type,
								R.id.T_list_address);
		findViewById();
		detailBtn.setText(task.tname);
		setListener();
		listview.setAdapter(adapter);
	}
	protected void onStart()
	{
		super.onStart();
		receiver=new TaskReceiver();
		IntentFilter filter=new IntentFilter();
		filter.addAction(ACTION_POST_RESULT);
		filter.addAction(ACTION_GET_DEVICE);
		filter.addAction(ACTION_CLEAR_CACHE);
		this.registerReceiver(receiver, filter);
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
	private void findViewById()
	{
		backBtn = (Button) findViewById(R.id.task_backbutton);
		homeBtn = (Button) findViewById(R.id.task_homebutton);
		summitBtn = (Button) findViewById(R.id.task_submitbutton);
		detailBtn = (Button) findViewById(R.id.task_topbutton);
		listview = (ListView) findViewById(R.id.listview_taskdetails);
	}
	private void setListener()
	{
		TaskListener listener = new TaskListener();
		backBtn.setOnClickListener(listener);
		homeBtn.setOnClickListener(listener);
		summitBtn.setOnClickListener(listener);
		detailBtn.setOnClickListener(listener);
		listview.setOnItemClickListener(listener);
	}
	private class TaskListener implements OnClickListener,OnItemClickListener
	{

		@Override
		public void onItemClick(AdapterView<?> parent, View view, int pos, long id) 
		{
			if(task.state.equals(TaskState.UNDO)&& hasCheck[pos]==false)
			{
				hasCheck[pos]=true;
				Intent intent = new Intent(TaskDetails.this,CaptureActivity.class);
				intent.setAction(Intent.ACTION_VIEW);
				intent.putExtra(KEY_DID, task.getDevices().get(pos).did);
				intent.putExtra(KEY_TID, task.tid);
				TaskDetails.this.startActivity(intent);
				
			}
			else
			{
				String tid=task.tid;
				String did=task.getDevices().get(pos).did;
				Intent service =new Intent(TaskDetails.this, ESClientService.class);
				service.setAction(ACTION_GET_DEVICE);
				service.putExtra(KEY_TID, tid);
				service.putExtra(KEY_DID, did);
				TaskDetails.this.startService(service);
			}
			
		}

		@Override
		public void onClick(View view)
		{
			if(view.getId()==R.id.task_backbutton)
			{
				TaskDetails.this.finish();
			}
			else if(view.getId()==R.id.task_homebutton)
			{
				ActivityManager.returnHome();
			}
			else if(view.getId()==R.id.task_submitbutton)
			{
				Intent intent = new Intent(TaskDetails.this,ESClientService.class);
				intent.setAction(ACTION_POST_RESULT);
				intent.putExtra(KEY_TASK, task);
				TaskDetails.this.startService(intent);
			}
			else if(view.getId()==R.id.task_topbutton)
			{
				LayoutInflater inflater = LayoutInflater.from(TaskDetails.this);
				SimpleDateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd");
				String taskdeline = dateFormat.format(task.deadLine);
				String taskfitimeline = dateFormat.format(task.finishTime);
				String taskretimeline = dateFormat.format(task.releaseTime);
				Log.d(TAG, taskfitimeline);
				View v = inflater.inflate(R.layout.task_dialog, null);
				AlertDialog.Builder builder = new Builder(TaskDetails.this);
				builder.setIcon(R.drawable.widget_edit_icon_selected);
				builder.setTitle(task.tname);
				TextView taskdialogidText=(TextView) v.findViewById(R.id.task_dialog_id);
				taskdialogidText.setText(task.tid);
				TextView taskdialogreleaseTimeText=(TextView) v.findViewById(R.id.task_dialog_releaseTime);
				taskdialogreleaseTimeText.setText(taskretimeline);
				TextView taskdialogstateText=(TextView) v.findViewById(R.id.task_dialog_state);
				taskdialogstateText.setText(task.state.toString());
				TextView taskdialogfinishTimeText=(TextView) v.findViewById(R.id.task_dialog_finishTime);
				taskdialogfinishTimeText.setText(taskfitimeline);
				TextView taskdialogdeviceNumText=(TextView) v.findViewById(R.id.task_dialog_deviceNum);
				taskdialogdeviceNumText.setText(task.deviceNum+"");
				TextView taskdialogdeadLineText=(TextView) v.findViewById(R.id.task_dialog_deadLine);
				taskdialogdeadLineText.setText(taskdeline);
				
				builder.setView(v);
				builder.setNegativeButton("返回", null);
				builder.show();
			}
		}
		
	}
	private class TaskReceiver extends BroadcastReceiver
	{

		@Override
		public void onReceive(Context context, Intent intent) 
		{
			String action = intent.getAction();
			if(action.equals(ACTION_GET_DEVICE))
			{
				showDevice(intent);
			}
			else if(action.equals(ACTION_POST_RESULT))
			{
				showPostResponse(intent);
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
			Toast.makeText(TaskDetails.this, result, Toast.LENGTH_LONG).show();
		}
		private void showPostResponse(Intent intent) 
		{
			String result;
			if(intent.getBooleanExtra(KEY_RESPONSE, false)==true)
			{
				Date ft =(Date) intent.getSerializableExtra(KEY_FINISH_TIME);
				SimpleDateFormat df=new SimpleDateFormat("yyyy-MM-dd");
				result="任务提交成功！提交时间："+df.format(ft);
				Toast.makeText(TaskDetails.this, result, Toast.LENGTH_LONG).show();
				TaskDetails.this.finish();
			}
			else
			{
				result=intent.getStringExtra(KEY_ERROR);
				Toast.makeText(TaskDetails.this, result, Toast.LENGTH_LONG).show();
			}
			
		}

		private void showDevice(Intent intent)
		{
			if(intent.getBooleanExtra(KEY_RESPONSE, false)==true)
			{
				Device device=(Device) intent.getSerializableExtra(KEY_DEVICE);
				Intent start =new Intent(TaskDetails.this,DeviceDetails.class);
				start.setAction(Intent.ACTION_VIEW);
				start.putExtra(KEY_DEVICE, device);
				Log.d(TAG, "taskstate:"+task.state);
				start.putExtra(KEY_TASKSTATE, task.state);
				start.putExtra(KEY_TID, task.tid);
				TaskDetails.this.startActivity(start);
			}
			else
			{
				String result=intent.getStringExtra(KEY_ERROR);
				Toast.makeText(TaskDetails.this, result, Toast.LENGTH_LONG).show();
			}
		}
		
	}
}
