package com.sdjxd.elecsysclient.util;

import java.util.Comparator;

import android.util.Log;

import com.sdjxd.elecsysclient.model.TaskLite;

public class FinishTimeFactor implements Comparator<TaskLite>
{
	private static final String TAG="FinishTimeFactor";
	@Override
	public int compare(TaskLite lhs, TaskLite rhs)
	{
		int result;
		if(lhs.finishTime!=null&&rhs.finishTime!=null)
		{
			result=rhs.finishTime.compareTo(lhs.finishTime);
		}
		else if(lhs.finishTime==null&&rhs.finishTime!=null)
		{
			result=1;
		}
		else if(lhs.finishTime!=null&&rhs.finishTime==null)
		{
			result=-1;
		}
		else
		{
			result=0;
		}
		return result;
	}


}
