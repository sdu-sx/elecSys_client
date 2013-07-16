package com.sdjxd.elecsysclient.model;

import java.io.Serializable;
import java.util.Collections;
import java.util.Date;
import java.util.Vector;

import com.sdjxd.elecsysclient.model.Task.TaskState;
import com.sdjxd.elecsysclient.util.DeadLineFactor;
import com.sdjxd.elecsysclient.util.FinishTimeFactor;

/**
 * Classname:TaskList
 * Description: �����б�
 * @author ������
 * @version 1.0
 * */
public class TaskList implements Serializable 
{
	

	/**
	 * 
	 */
	private static final long serialVersionUID = 697240328887232177L;

	/**
	 * �����б�����Ա����
	 * */
	public String wid;
	
	/**
	 * �����б��е���������
	 * */
	public TaskState state;
	
	/**
	 * �����б�
	 * */
	private Vector<TaskLite> tasks;
	
	public TaskList(String wid,TaskState state)
	{
		this.wid=wid;
		this.state=state;
		tasks=new Vector<TaskLite>();
	}
	public String toString()
	{
		String result;
		result=wid+"��"+state+"����"+tasks.toString();
		return result;
	}
	public boolean addTask(TaskLite task)
	{
		boolean result=false;
		if(task!=null)
		{
			tasks.add(task);
			result=true;
		}
		return result;
	}
	public TaskLite getTask(int pos)
	{
		return tasks.get(pos);
	}
	public void setTasks(Vector<TaskLite> list)
	{
		tasks=list;
	}
	public Vector<TaskLite> getTasks()
	{
		return tasks;
	}
	public boolean append(TaskList list)
	{
		boolean result=false;
		if(list!=null&&wid.equals(list.wid)&&state.equals(list.state))
		{
			result=tasks.addAll(list.getTasks());
		}
		sortlist();
		return result;
	}
	private void sortlist()
	{
		if(this.tasks==null)
		{
			return;
		}
		if(state.equals(TaskState.OVERTIME)||state.equals(TaskState.UNDO))
		{
			Collections.sort(tasks, new DeadLineFactor());
		}
		else 
		{
			Collections.sort(tasks, new FinishTimeFactor());

		}
	}
}
