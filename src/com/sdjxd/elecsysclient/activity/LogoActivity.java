package com.sdjxd.elecsysclient.activity;

import java.util.Timer;
import java.util.TimerTask;
import com.sdjxd.elecsysclient.R;
import com.sdjxd.elecsysclient.model.Task;
import com.sdjxd.elecsysclient.model.Task.TaskState;
import com.sdjxd.elecsysclient.model.TaskList;
import com.sdjxd.elecsysclient.service.ESClientService;
import com.sdjxd.elecsysclient.service.RequestFilter;

import android.app.Activity;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.os.Bundle;
import android.util.Log;
import android.widget.ProgressBar;
import android.widget.Toast;

public class LogoActivity extends Activity implements RequestFilter
{
	private static final String TAG="LogoActivity";
	BroadcastReceiver receiver;
	private ProgressBar progressBar;
	private TaskList undo,done,overtime;
	private String wid,wname;
	private int progressState=0;
	private final int MAX=300;

	protected void onCreate(Bundle savedInstanceState) 
	{
		super.onCreate(savedInstanceState);
		ActivityManager.getInstance().addActivity(this);
		setContentView(R.layout.logo);
		
		progressBar = (ProgressBar) findViewById(R.id.logo_pgBar);
		progressBar.setIndeterminate(true);
		wid=this.getIntent().getStringExtra(KEY_WID);
		wname=this.getIntent().getStringExtra(KEY_WNAME);
		Log.d(TAG, "wid:"+wid);
		Log.d(TAG, "wname:"+wname);
	}
	protected void onStart()
	{
		super.onStart();
		receiver = new BroadcastReceiver()
		{
			@Override
			public void onReceive(Context context, Intent intent) 
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
					progressState+=100;
					if(progressState==MAX)
					{
						Intent startIntent=new Intent(LogoActivity.this,HomeActivity.class);
						intent.setAction(Intent.ACTION_VIEW);
						startIntent.putExtra(KEY_WID,wid);
						startIntent.putExtra(KEY_WNAME, wname);
						startIntent.putExtra(KEY_UNDOLIST, undo);
						startIntent.putExtra(KEY_DONELIST, done);
						startIntent.putExtra(KEY_OVERTIMELIST, overtime);
						startActivity(startIntent);
					}
				}
				else
				{
					String result=intent.getStringExtra(KEY_ERROR);
					Toast.makeText(LogoActivity.this, result, Toast.LENGTH_LONG).show();
				}		
			}
		};
		IntentFilter filter=new IntentFilter();
		filter.addAction(ACTION_GET_TASKLIST);
		this.registerReceiver(receiver, filter);
		
		startGetUndoList();
		startGetDoneList();
		startGetOvertimeList();
	}
	protected void onStop()
	{
		super.onStop();
		this.unregisterReceiver(receiver);
		finish();
	}
	protected void onDestroy()
	{
		ActivityManager.getInstance().removeActivity(this);
		super.onDestroy();
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
	
}
