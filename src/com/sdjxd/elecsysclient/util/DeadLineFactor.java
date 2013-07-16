package com.sdjxd.elecsysclient.util;

import java.util.Comparator;

import com.sdjxd.elecsysclient.model.TaskLite;

public class DeadLineFactor implements Comparator<TaskLite>
{

	@Override
	public int compare(TaskLite lhs, TaskLite rhs) 
	{
		int result;
		if(lhs.deadLine!=null&&rhs.deadLine!=null)
		{
			result=lhs.deadLine.compareTo(rhs.deadLine);
		}
		else if(lhs.deadLine==null&&rhs.deadLine!=null)
		{
			result=1;
		}
		else if(lhs.deadLine!=null&&rhs.deadLine==null)
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
