package com.sdjxd.elecsysclient.activity;


import java.util.Vector;

import com.sdjxd.elecsysclient.R;
import com.sdjxd.elecsysclient.adapter.DeviceAdapter;
import com.sdjxd.elecsysclient.model.CheckCard;
import com.sdjxd.elecsysclient.model.Device;
import com.sdjxd.elecsysclient.model.FaultHistory;
import com.sdjxd.elecsysclient.model.Task.TaskState;
import com.sdjxd.elecsysclient.service.ESClientService;
import com.sdjxd.elecsysclient.service.RequestFilter;

import android.app.Activity;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.os.Bundle;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.ListView;
import android.widget.Toast;

public class DeviceDetails extends Activity implements RequestFilter
{
	private static final String TAG="DeviceDetails";
	private DeviceAdapter adapter;
	private Device device;
	private TaskState state;
	private String tid;
	private Button backBtn,detailBtn,homeBtn,scanBtn,saveBtn,faultsBtn;
	private ListView listView;
	private DeviceReceiver receiver;
		
	protected void onCreate(Bundle savedInstanceState) 
	{
		super.onCreate(savedInstanceState);
		ActivityManager.getInstance().addActivity(this);
		setContentView(R.layout.activity_devicedetails);
		Intent intent=getIntent();
		device=(Device) intent.getSerializableExtra(KEY_DEVICE);
		state = (TaskState) intent.getSerializableExtra(KEY_TASKSTATE);
		tid=intent.getStringExtra(KEY_TID);
		adapter = new DeviceAdapter(this,
									device.getCheckCards(),
									state,
									R.layout.activity_devicedetails_listview,
									R.id.D_list_work,
									R.id.D_list_editText,
									R.id.D_list_checkBox);
		findViewById();
		detailBtn.setText(device.dName);
		listView.setAdapter(adapter);
		setListener();
	}
	protected void onStart()
	{
		super.onStart();
		receiver=new DeviceReceiver();
		IntentFilter filter = new IntentFilter();
		filter.addAction(ACTION_SAVE_DEVICE_RESULT);
		filter.addAction(ACTION_GET_FAULT_HISTORY);
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
	private void findViewById()
	{
		backBtn = (Button) findViewById(R.id.devive_backbutton);
		detailBtn = (Button) findViewById(R.id.device_topbutton);
		homeBtn = (Button) findViewById(R.id.device_homebutton);
		scanBtn = (Button) findViewById(R.id.device_dimensionalbutton);
		saveBtn = (Button) findViewById(R.id.device_savebutton);
		faultsBtn = (Button) findViewById(R.id.device_failurebutton);
		listView = (ListView) findViewById(R.id.listview_devicedetails);
	}
	private void setListener()
	{
		DeviceListener listener= new DeviceListener();
		backBtn.setOnClickListener(listener);
		detailBtn.setOnClickListener(listener);
		homeBtn.setOnClickListener(listener);
		scanBtn.setOnClickListener(listener);
		saveBtn.setOnClickListener(listener);
		faultsBtn.setOnClickListener(listener);
	}
	private class DeviceListener implements OnClickListener
	{
		@Override
		public void onClick(View view) 
		{
			if(view.getId()==R.id.devive_backbutton)
			{
				DeviceDetails.this.finish();
			}
			else if(view.getId()==R.id.device_homebutton)
			{
				ActivityManager.returnHome();
			}
			else if(view.getId()==R.id.device_savebutton)
			{
				boolean flag=true;
				Vector<CheckCard> cards=device.getCheckCards();
				for(int i=0;i<cards.size();i++)
				{
					if(!cards.get(i).isCheck)
					{
						flag=false;
						break;
					}
				}
				if(flag)
				{
					Intent intent = new Intent(DeviceDetails.this,ESClientService.class);
					intent.setAction(ACTION_SAVE_DEVICE_RESULT);
					intent.putExtra(KEY_DEVICE, device);
					intent.putExtra(KEY_TID, tid);
					DeviceDetails.this.startService(intent);
				}
				else
				{
					String result="请填好所有抄录值并标记完成";
					Toast.makeText(DeviceDetails.this, result, Toast.LENGTH_LONG).show();
				}
			}
			else if(view.getId()==R.id.device_dimensionalbutton)
			{
				
			}
			else if(view.getId()==R.id.device_topbutton)
			{
				
			}
			else if(view.getId()==R.id.device_failurebutton)
			{
				Intent intent = new Intent(DeviceDetails.this,ESClientService.class);
				intent.setAction(ACTION_GET_FAULT_HISTORY);
				intent.putExtra(KEY_DID, device.did);
				DeviceDetails.this.startService(intent);
			}
		}
	}
	private class DeviceReceiver extends BroadcastReceiver
	{

		@Override
		public void onReceive(Context context, Intent intent) 
		{
			String action=intent.getAction();
			if(action.equals(ACTION_SAVE_DEVICE_RESULT))
			{
				showSaveResult(intent);
			}
			else if(action.equals(ACTION_GET_FAULT_HISTORY))
			{
				showFaultHistory(intent);
			}
		}

		private void showFaultHistory(Intent intent)
		{
			if(intent.getBooleanExtra(KEY_RESPONSE, false)==true)
			{
				FaultHistory fh=(FaultHistory) intent.getSerializableExtra(KEY_FAULT_HISTORY);
				Intent start = new Intent(DeviceDetails.this,FaultHistory.class);
				start.setAction(Intent.ACTION_VIEW);
				start.putExtra(KEY_FAULT_HISTORY, fh);
				DeviceDetails.this.startActivity(start);
			}
			else
			{
				String result=intent.getStringExtra(KEY_ERROR);
				Toast.makeText(DeviceDetails.this, result, Toast.LENGTH_LONG).show();
			}
		}

		private void showSaveResult(Intent intent)
		{
			String result;
			if(intent.getBooleanExtra(KEY_RESPONSE, false)==true)
			{
				result=intent.getStringExtra(KEY_REPLY);
				Toast.makeText(DeviceDetails.this, result, Toast.LENGTH_LONG).show();
				DeviceDetails.this.finish();
			}
			else
			{
				result=intent.getStringExtra(KEY_ERROR);
				Toast.makeText(DeviceDetails.this, result, Toast.LENGTH_LONG).show();
			}
		}	
		
	}
}
