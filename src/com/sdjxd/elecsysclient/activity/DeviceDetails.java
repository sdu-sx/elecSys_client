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
import android.view.MenuItem;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

public class DeviceDetails extends Activity implements RequestFilter
{
	private static final String TAG="DeviceDetails";
	private static final int CACHE=0,EXIT=1;
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
		Log.d(TAG, state.toString());
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
//				Intent intent = new Intent(DeviceDetails.this,CaptureActivity.class);
//				intent.setAction(Intent.ACTION_VIEW);
//				DeviceDetails.this.startActivity(intent);
			}
			else if(view.getId()==R.id.device_topbutton)
			{
				LayoutInflater inflater = LayoutInflater.from(DeviceDetails.this);
				View v = inflater.inflate(R.layout.device_dialog, null);
				AlertDialog.Builder builder = new Builder(DeviceDetails.this);
				builder.setIcon(R.drawable.widget_edit_icon_selected);
				builder.setTitle(device.dName);
				TextView devicedialogdidText=(TextView) v.findViewById(R.id.device_dialog_did);
				devicedialogdidText.setText(device.did);
				TextView taskdialogstateText=(TextView) v.findViewById(R.id.device_dialog_state);
				Log.d(TAG, state.toString());
				taskdialogstateText.setText(state.toString());
				TextView devicedialogdaddressText=(TextView) v.findViewById(R.id.device_dialog_daddress);
				devicedialogdaddressText.setText(device.dAddress+"");
				TextView devicedialogdypeText=(TextView) v.findViewById(R.id.device_dialog_dype);
				devicedialogdypeText.setText(device.dType);
				builder.setView(v);
				builder.setNegativeButton("返回", null);
				builder.show();
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
				Intent start = new Intent(DeviceDetails.this,FailureHistory.class);
				start.setAction(Intent.ACTION_VIEW);
				start.putExtra(KEY_FAULT_HISTORY, fh);
				start.putExtra(KEY_TASKSTATE, state);
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
