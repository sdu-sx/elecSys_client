package com.sdjxd.elecsysclient.activity;

import com.sdjxd.elecsysclient.R;
import com.sdjxd.elecsysclient.service.ESClientService;
import com.sdjxd.elecsysclient.service.RequestFilter;

import android.app.Activity;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.SharedPreferences;
import android.content.SharedPreferences.Editor;
import android.os.Bundle;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.Window;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.CompoundButton;
import android.widget.CompoundButton.OnCheckedChangeListener;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.Toast;

public class LoginActivity extends Activity implements RequestFilter
{
	private static final String TAG="LoginActivity";
	private EditText userName, password;
	private CheckBox rem_pw, auto_login;
	private Button btn_login;
	private ImageButton btnQuit;
    private String userNameValue,passwordValue;
	private SharedPreferences sp;
	private LoginReceiver receiver; 

	public void onCreate(Bundle savedInstanceState) 
	{
		super.onCreate(savedInstanceState);
		ActivityManager.getInstance().addActivity(this);
		//去除标题
		setContentView(R.layout.activity_test);
        //获得实例对象
		sp = this.getSharedPreferences("userInfo", Context.MODE_PRIVATE);
		findViewById();
		startService();
		
		

		btn_login.setOnClickListener(new LoginListener());
		rem_pw.setOnCheckedChangeListener(new LoginListener());
		auto_login.setOnCheckedChangeListener(new LoginListener());
		btnQuit.setOnClickListener(new LoginListener());
		
		
	}
	protected void onStart()
	{
		super.onStart();
		//判断记住密码多选框的状态
        if(sp.getBoolean("ISCHECK", false))
        {
    	  //设置默认是记录密码状态
          rem_pw.setChecked(true);
       	  userName.setText(sp.getString(KEY_WID, ""));
       	  password.setText(sp.getString(KEY_PWD, ""));
       	  //判断自动登陆多选框状态
       	  if(sp.getBoolean("AUTO_ISCHECK", false))
       	  {
       		     //设置默认是自动登录状态
       		     auto_login.setChecked(true);
       		    
       		     if(sp.getBoolean("AUTO_ENABLE", false))
       		     {
       		    	 Intent intent= new Intent(LoginActivity.this,ESClientService.class);
       				 intent.setAction(ACTION_LOGIN);
       				 intent.putExtra(KEY_WID, userNameValue);
       				 intent.putExtra(KEY_PWD, passwordValue);
       				 LoginActivity.this.startService(intent);
       		     }
	            	
       	  }
        }
		receiver = new LoginReceiver();
		IntentFilter filter = new IntentFilter();
		filter.addAction(ACTION_LOGIN);
		registerReceiver(receiver, filter);
	}
	protected void onRestart()
	{
		super.onRestart();
		userName.setText("");
		password.setText("");
		rem_pw.setChecked(false);
		auto_login.setChecked(false);
	}
	private void startService()
	{
		Intent intent=new Intent(this,ESClientService.class);
		intent.setAction(ACTION_START);
		startService(intent);
	}
	private void findViewById()
	{
		userName = (EditText) findViewById(R.id.et_zh);
		password = (EditText) findViewById(R.id.et_mima);
        rem_pw = (CheckBox) findViewById(R.id.cb_mima);
		auto_login = (CheckBox) findViewById(R.id.cb_auto);
        btn_login = (Button) findViewById(R.id.btn_login);
        btnQuit = (ImageButton)findViewById(R.id.img_btn); 
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
	private class LoginListener implements OnClickListener,OnCheckedChangeListener
	{

		@Override
		public void onClick(View v) 
		{
			if(v.getId()==R.id.btn_login)
			{
				userNameValue = userName.getText().toString();
			    passwordValue = password.getText().toString();
			    Intent intent= new Intent(LoginActivity.this,ESClientService.class);
				intent.setAction(ACTION_LOGIN);
				intent.putExtra(KEY_WID, userNameValue);
				intent.putExtra(KEY_PWD, passwordValue);
				LoginActivity.this.startService(intent);
			}
			else if(v.getId()==R.id.img_btn)//退出
			{
				Intent intent=new Intent(LoginActivity.this,ESClientService.class);
				LoginActivity.this.stopService(intent);
				ActivityManager.exit();
			}
		}

		@Override
		public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) 
		{
			if(buttonView.getId()==R.id.cb_mima) //记住密码
			{
				if (isChecked) 
				{
					sp.edit().putBoolean("ISCHECK", true).commit();
					auto_login.setEnabled(true);
				}
				else 
				{
					sp.edit().putBoolean("ISCHECK", false).commit();
					auto_login.setChecked(false);
					auto_login.setEnabled(false);
					sp.edit().clear().commit();
				}
			}
			else if(buttonView.getId()==R.id.cb_auto) //自动登录
			{

				if (isChecked) 
				{
					sp.edit().putBoolean("AUTO_ISCHECK", true).commit();
				}
				else 
				{
					sp.edit().putBoolean("AUTO_ISCHECK", false).commit();
				}
			}
		}
		
	}
	private class LoginReceiver extends BroadcastReceiver
	{

		@Override
		public void onReceive(Context context, Intent intent)
		{
			String action=intent.getAction();
			if(action.equals(ACTION_LOGIN))
			{
				if(intent.getBooleanExtra(KEY_RESPONSE, false))
				{
					Editor editor=sp.edit();
					String wid=intent.getStringExtra(KEY_WID);
					String pwd=intent.getStringExtra(KEY_PWD);
					if(sp.getBoolean("ISCHECK", false))
					{
						editor.putString(KEY_WID, wid).commit();
						editor.putString(KEY_PWD, pwd).commit();
					}
					
					Intent in=new Intent(LoginActivity.this,LogoActivity.class);
					in.setAction(Intent.ACTION_VIEW);
					in.putExtra(KEY_WID, wid);
					in.putExtra(KEY_WNAME, intent.getStringExtra(KEY_WNAME));
					LoginActivity.this.startActivity(in);
				}
				else
				{
					String info=intent.getStringExtra(KEY_ERROR);
					Toast.makeText(context, info, Toast.LENGTH_LONG).show();
				}
			}
		}
		
	}
}