package com.sdjxd.elecsysclient.activity;







import java.util.ArrayList;
import java.util.HashMap;

import com.sdjxd.elecsysclient.R;

import android.app.Activity;
import android.app.AlertDialog;
import android.app.AlertDialog.Builder;
import android.app.Dialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.Window;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ListView;
import android.widget.SimpleAdapter;
import android.widget.Toast;


public class FailureHistory extends Activity{
	
	
	
	ArrayList<HashMap<String,Object>> data;
	SimpleAdapter adapter;
	String[] key = new String[]{"failure"};
	int[] value = new int[]{R.id.F_list_work};
	ListView listView ;
	@Override
	
	
	protected void onCreate(Bundle savedInstanceState) {
		
		super.onCreate(savedInstanceState);
		
		setContentView(R.layout.activity_failurehistory);
		
		
		
		
		data = new ArrayList<HashMap<String,Object>>();
		
		HashMap<String,Object> map = new HashMap<String,Object>();		
		
		map.put("failure", "µÆË¿¶ÌÂ·");
		data.add(map);
		
		
		
		adapter = new SimpleAdapter(this, data, R.layout.activity_failurehistory_listview,key,value);
		
		listView = (ListView) this.findViewById(R.id.listview_fahistory);
		
		listView.setAdapter(adapter);
		
		
		
		
		
		Button failureBackButton=(Button)findViewById(R.id.failure_backbutton);
		
		Button failureHomeButton=(Button)findViewById(R.id.failure_homebutton);
		
		Button failureAddButton=(Button)findViewById(R.id.failure_addbutton);
		
		failureBackButton.setOnClickListener(new FailureBackButtonListener());
		
		failureHomeButton.setOnClickListener(new FailureHomeButtonListener());
		
		failureAddButton.setOnClickListener(new FailureAddButtonListener());
		
		
		
	}
	
	class  FailureBackButtonListener implements OnClickListener{
		
		public void onClick(View v) {
			// TODO Auto-generated method stub
            Intent intent = new Intent(); 
            
            intent.setClass(FailureHistory.this, DeviceDetails.class);
            
            FailureHistory.this.startActivity(intent);
		}
	}
	class  FailureHomeButtonListener implements OnClickListener{
		
		public void onClick(View v) {
			// TODO Auto-generated method stub
            Intent intent = new Intent();
            
            intent.setClass(FailureHistory.this, HomeActivity.class);
            
            FailureHistory.this.startActivity(intent);
		}
	}
	class  FailureAddButtonListener implements OnClickListener{
		
		public void onClick(View v) {
			// TODO Auto-generated method stub
			LayoutInflater inflater = getLayoutInflater();
			
			View view = inflater.inflate(R.layout.dialog, null);
			
			AlertDialog.Builder builder = new Builder(FailureHistory.this);
			 		
			builder.setIcon(R.drawable.widget_edit_icon_selected);
			
			builder.setTitle(R.string.add_failure);
			
			builder.setView(view);
			
			builder.setPositiveButton("±£´æ", null);
			
			builder.setNegativeButton("·µ»Ø", null);
			
			builder.show();

		}
	}
	
}
	



