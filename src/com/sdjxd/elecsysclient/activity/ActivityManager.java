package com.sdjxd.elecsysclient.activity;

import java.util.Vector;

import android.app.Activity;
import android.app.Application;
import android.content.Context;
import android.content.SharedPreferences;
import android.content.SharedPreferences.Editor;
import android.util.Log;

public class ActivityManager
{
	private static final String TAG="ActivityManager";
	private Vector<Activity> activities;
	private static ActivityManager manager;
	private Activity home;
	private ActivityManager()
	{
		activities = new Vector<Activity>();
	}
	public static ActivityManager getInstance()
	{
		if(manager==null)
		{
			manager=new ActivityManager(); 
		}
		return manager;
	}
	public boolean addActivity(Activity activity)
	{
		boolean result=false;
		if(activity!=null)
		{
			result=activities.add(activity);
		}
		return result;
		
	}
	public boolean removeActivity(Activity activity)
	{
		boolean result=false;
		if(activity!=null)
		{
			result=activities.remove(activity);
		}
		return result;
	}
	public void setHome(Activity activity)
	{
		home=activity;
	}
	public static void returnHome()
	{
		Activity activity;
		for(int i=manager.activities.size()-1;i>=0;i--)
		{
			activity=manager.activities.remove(i);
			if(activity.equals(manager.home))
			{
				manager.activities.add(activity);
			}
			else
			{
				activity.finish();
			}
		}
	}
	public static void exit()
	{
		SharedPreferences sp ;
		Activity act;
		for(int i=manager.activities.size()-1;i>=0;i--)
		{
			act=manager.activities.remove(i);
			if(i==0)
			{
				sp = act.getSharedPreferences("userInfo",Context.MODE_PRIVATE);
				Editor editor=sp.edit();
				if(sp.getBoolean("AUTO_ISCHECK", false))
				{
					editor.putBoolean("AUTO_ENABLE", true).commit();
				}
			}
			act.finish();
		}
		System.exit(0);
	}
}
