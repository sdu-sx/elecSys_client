package com.sdjxd.elecsysclient.activity;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Vector;

import com.sdjxd.elecsysclient.R;
import com.sdjxd.elecsysclient.model.Fault;
import com.sdjxd.elecsysclient.model.FaultHistory;
import com.sdjxd.elecsysclient.model.Task.TaskState;
import com.sdjxd.elecsysclient.service.ESClientService;
import com.sdjxd.elecsysclient.service.RequestFilter;

import android.app.Activity;
import android.app.AlertDialog;
import android.app.AlertDialog.Builder;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.IntentFilter;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ListView;
import android.widget.SimpleAdapter;
import android.widget.Toast;


public class FailureHistory extends Activity implements RequestFilter
{
	private static final String TAG="FailureHistory";
	private static final int CACHE=0,EXIT=1;
	private ArrayList<HashMap<String,Object>> data;
	private FaultHistory history;
	private TaskState state;
	private SimpleAdapter adapter;
	private Button backBtn,addBtn;
	private ListView listView ;
	private EditText faultContent;
	private FailureReceiver receiver;
	@Override
	protected void onCreate(Bundle savedInstanceState) 
	{
		super.onCreate(savedInstanceState);
		ActivityManager.getInstance().addActivity(this);
		setContentView(R.layout.activity_failurehistory);
		Intent intent = getIntent();
		history=(FaultHistory) intent.getSerializableExtra(KEY_FAULT_HISTORY);
		state=(TaskState) intent.getSerializableExtra(KEY_TASKSTATE);
		
		backBtn = (Button) findViewById(R.id.failure_backbutton);
		addBtn = (Button) findViewById(R.id.add_fault);
		listView = (ListView) this.findViewById(R.id.listview_fahistory);
		
		data = new ArrayList<HashMap<String,Object>>();
		
		String[] key = new String[]{KEY_FAULT_CONTENT,KEY_FAULT_TIME};
		int[] value = new int[]{R.id.F_list_work,R.id.F_list_time};
		adapter = new SimpleAdapter(this, data, R.layout.activity_failurehistory_listview,key,value);
		listView.setAdapter(adapter);
		
		FailureListener listener = new FailureListener();
		backBtn.setOnClickListener(listener);
		addBtn.setOnClickListener(listener);
	}
	protected void onStart()
	{
		super.onStart();
		receiver = new FailureReceiver();
		IntentFilter filter = new IntentFilter();
		filter.addAction(ACTION_POST_FAULT);
		filter.addAction(ACTION_GET_FAULT_HISTORY);
		filter.addAction(ACTION_CLEAR_CACHE);
		this.registerReceiver(receiver, filter);
	}
	protected void onResume()
	{
		super.onResume();
		setData();
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
	private void setData()
	{
		data.clear();
		SimpleDateFormat df=new SimpleDateFormat("yyyy-mm-dd");
		HashMap<String,Object> map;
		Vector<Fault> faults = history.getFaults();
		Fault fault;
		for(int i=0;i<faults.size();i++)
		{
			map = new HashMap<String,Object>();
			fault = faults.get(i);
			map.put(KEY_FAULT_CONTENT, fault.content);
			map.put(KEY_FAULT_TIME, df.format(fault.date));
			data.add(map);
		}
		adapter.notifyDataSetChanged();
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
	private class FailureListener implements OnClickListener, DialogInterface.OnClickListener
	{

		@Override
		public void onClick(View view) 
		{
			if(view.getId()==R.id.failure_backbutton)
			{
				FailureHistory.this.finish();
			}
			else if(view.getId()==R.id.add_fault)
			{
				LayoutInflater inflater = getLayoutInflater();
				View v = inflater.inflate(R.layout.dialog, null);
				faultContent = (EditText) v.findViewById(R.id.dialog_edittext);
				AlertDialog.Builder builder = new Builder(FailureHistory.this);
				builder.setIcon(R.drawable.widget_edit_icon_selected);
				builder.setTitle(R.string.add_failure);
				builder.setView(v);
				builder.setPositiveButton("提交", this);
				builder.setNegativeButton("返回", null);
				builder.show();
			}
		}

		public void onClick(DialogInterface dialog, int pos) 
		{
			if(pos==dialog.BUTTON_POSITIVE)
			{
				String content=faultContent.getText().toString();
				if(content==null||content.length()==0)
				{
					Toast.makeText(FailureHistory.this, "输入内容不能为空", Toast.LENGTH_SHORT).show();
				}
				else
				{
					String did=history.did;
					Intent intent = new Intent(FailureHistory.this,ESClientService.class);
					intent.setAction(ACTION_POST_FAULT);
					intent.putExtra(KEY_DID,did);
					intent.putExtra(KEY_FAULT_CONTENT, content);
					FailureHistory.this.startService(intent);
				}
			}
		}
	}
	private class FailureReceiver extends BroadcastReceiver
	{

		@Override
		public void onReceive(Context context, Intent intent)
		{
			String action = intent.getAction();
			if(action.equals(ACTION_POST_FAULT))
			{
				showPostFault(intent);
			}
			else if(action.equals(ACTION_GET_FAULT_HISTORY))
			{
				showFaultHistory(intent);
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
			Toast.makeText(FailureHistory.this, result, Toast.LENGTH_LONG).show();
		}
		private void showFaultHistory(Intent intent) 
		{
			if(intent.getBooleanExtra(KEY_RESPONSE, false)==true)
			{
				history=(FaultHistory) intent.getSerializableExtra(KEY_FAULT_HISTORY);
				setData();
			}
			else
			{
				String error=intent.getStringExtra(KEY_ERROR);
				Toast.makeText(FailureHistory.this, error, Toast.LENGTH_SHORT).show();
			}
		}

		private void showPostFault(Intent intent)
		{
			String result;
			if(intent.getBooleanExtra(KEY_RESPONSE, false)==true)
			{
				result = intent.getStringExtra(KEY_REPLY);
				Intent nIntent = new Intent(FailureHistory.this,ESClientService.class);
				nIntent.setAction(ACTION_GET_FAULT_HISTORY);
				nIntent.putExtra(KEY_DID, history.did);
				FailureHistory.this.startService(nIntent);
			}
			else
			{
				result = intent.getStringExtra(KEY_ERROR);
			}
			Toast.makeText(FailureHistory.this, result, Toast.LENGTH_SHORT).show();
		}
		
	}
}
	



