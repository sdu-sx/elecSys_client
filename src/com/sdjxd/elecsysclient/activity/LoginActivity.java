package com.sdjxd.elecsysclient.activity;

import com.sdjxd.elecsysclient.R;
import com.sdjxd.elecsysclient.service.ESClientService;
import com.sdjxd.elecsysclient.service.RequestFilter;

import android.app.Activity;
import android.app.AlertDialog;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.SharedPreferences;
import android.content.SharedPreferences.Editor;
import android.os.Bundle;
import android.view.LayoutInflater;
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
	private Button settingBtn;
	private EditText editIp,editPort;
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

		LoginListener listener = new LoginListener();
		btn_login.setOnClickListener(listener);
		rem_pw.setOnCheckedChangeListener(listener);
		auto_login.setOnCheckedChangeListener(listener);
		btnQuit.setOnClickListener(listener);
		settingBtn.setOnClickListener(listener);
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
		filter.addAction(ACTION_CLEAR_CACHE);
		filter.addAction(ACTION_SET_HOST);
		registerReceiver(receiver, filter);
	}
//	protected void onRestart()
//	{
//		super.onRestart();
//		userName.setText("");
//		password.setText("");
//		rem_pw.setChecked(false);
//		auto_login.setChecked(false);
//	}
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
        settingBtn = (Button) findViewById(R.id.set);
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
	private void buildSettingDialog()
	{
		AlertDialog.Builder builder=new AlertDialog.Builder(this);
		LoginListener listener = new LoginListener();
		builder.setTitle(R.string.setting);
		LayoutInflater inflater=LayoutInflater.from(this);
		View v=inflater.inflate(R.layout.setting_dialog, null);
		editIp=(EditText) v.findViewById(R.id.setting_dialog_ip);
		editPort=(EditText) v.findViewById(R.id.setting_dialog_port);
		Button clearBtn = (Button) v.findViewById(R.id.setting_dialog_clear);
		clearBtn.setOnClickListener(listener);
		builder.setView(v);
		builder.setPositiveButton(R.string.ture,listener);
		builder.setNegativeButton(R.string.cancel, null);
		builder.show();
	}
	private class LoginListener implements OnClickListener,OnCheckedChangeListener, android.content.DialogInterface.OnClickListener
	{

		@Override
		public void onClick(View v) 
		{
			if(v.getId()==R.id.btn_login)
			{
				userNameValue = userName.getText().toString();
			    passwordValue = password.getText().toString();
//			    if(userNameValue.equals("debug")&&userNameValue.equals("debug"))
//			    {
//			    	Intent in=new Intent(LoginActivity.this,LogoActivity.class);
//					in.setAction(Intent.ACTION_VIEW);
//					in.putExtra(KEY_WID, "001");
//					in.putExtra(KEY_WNAME, "张三");
//					LoginActivity.this.startActivity(in);
//			    }
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
			else if(v.getId()==R.id.setting_dialog_clear)
			{
				Intent intent = new Intent(LoginActivity.this,ESClientService.class);
				intent.setAction(ACTION_CLEAR_CACHE);
				LoginActivity.this.startService(intent);
			}
			else if(v.getId()==R.id.set)
			{
				buildSettingDialog();
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

		@Override
		public void onClick(DialogInterface dialog, int which) 
		{
			String ip=editIp.getText().toString();
			String port = editPort.getText().toString();
			Intent intent = new Intent(LoginActivity.this,ESClientService.class);
			intent.setAction(ACTION_SET_HOST);
			intent.putExtra(KEY_IP, ip);
			intent.putExtra(KEY_PORT, port);
			LoginActivity.this.startService(intent);
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
				showLogin(intent);
			}
			else if(action.equals(ACTION_SET_HOST))
			{
				showSetHost(intent);
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
			Toast.makeText(LoginActivity.this, result, Toast.LENGTH_LONG).show();
		}
		private void showSetHost(Intent intent) 
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
			Toast.makeText(LoginActivity.this, result, Toast.LENGTH_SHORT).show();
		}

		private void showLogin(Intent intent) 
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
				Toast.makeText(LoginActivity.this, info, Toast.LENGTH_LONG).show();
			}
		}
		
	}
}