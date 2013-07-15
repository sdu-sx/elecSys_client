package com.sdjxd.elecsysclient.model;

import java.io.Serializable;
import java.util.Date;
import java.util.Vector;

import com.sdjxd.elecsysclient.model.Task.TaskState;

/**
 * Classname:TaskList
 * Description: 任务列表
 * @author 许凌霄
 * @version 1.0
 * */
public class TaskList implements Serializable 
{
	

	/**
	 * 
	 */
	private static final long serialVersionUID = 697240328887232177L;

	/**
	 * 任务列表所属员工号
	 * */
	public String wid;
	
	/**
	 * 任务列表中的任务类型
	 * */
	public TaskState state;
	
	/**
	 * 任务列表
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
		result=wid+"的"+state+"任务："+tasks.toString();
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
		return result;
	}
//	public void sortlist()
//	{
//		if(this.tasks==null)
//		{
//			return;
//		}
//		if(this.state.toString().equals("OVERTIME"))
//		{
//		this.quicksort1(this.tasks,0,this.tasks.size());
//		}
//		else if(this.state.toString().equals("UNDO"))
//		{
//			this.quicksort2(this.tasks,0,this.tasks.size());
//
//		}
//		else if(this.state.toString().equals("DONE"))
//		{
//			this.quicksort3(this.tasks,0,this.tasks.size());
//
//		}
//	}
//	private void quicksort1(Vector<TaskLite> tasks,int l,int r)
//	{
//		if(l<r)
//		{
//			int i=l,j=r;
//			Date time=tasks.get(l).releaseTime;
//			TaskLite task=tasks.get(l);
//			while(i<j)
//			{
//				while(i<j&&tasks.get(j).releaseTime.compareTo(time)>=0)
//					{
//						j--;
//					}
//				if(i<j)
//				{
//					tasks.set(i++,tasks.get(j));
//				}
//				while(i<j&& tasks.get(i).releaseTime.compareTo(time)<0)
//				{
//					i++;
//				}
//				if(i<j)
//				{
//					tasks.set(i--,tasks.get(i));
//				}
//			}
//				tasks.set(i, task);
//				quicksort1(tasks, l,i-1);
//				quicksort1(tasks, i+1, r);
//				}
//			}
//	private void quicksort2(Vector<TaskLite> tasks,int l,int r)
//	{
//		if(l<r)
//		{
//			int i=l,j=r;
//			Date time=tasks.get(l).deadLine;
//			TaskLite task=tasks.get(l);
//			while(i<j)
//			{
//				while(i<j&&tasks.get(j).deadLine.compareTo(time)>=0)
//					{
//						j--;
//					}
//				if(i<j)
//				{
//					tasks.set(i++,tasks.get(j));
//				}
//				while(i<j&& tasks.get(i).deadLine.compareTo(time)<0)
//				{
//					i++;
//				}
//				if(i<j)
//				{
//					tasks.set(i--,tasks.get(i));
//				}
//			}
//				tasks.set(i, task);
//				quicksort2(tasks, l,i-1);
//				quicksort2(tasks, i+1, r);
//				}
//			}
//	private void quicksort3(Vector<TaskLite> tasks,int l,int r)
//	{
//		if(l<r)
//		{
//			int i=l,j=r;
//			Date time=tasks.get(l).finishTime;
//			TaskLite task=tasks.get(l);
//			while(i<j)
//			{
//				while(i<j&&tasks.get(j).finishTime.compareTo(time)>=0)
//					{
//						j--;
//					}
//				if(i<j)
//				{
//					tasks.set(i++,tasks.get(j));
//				}
//				while(i<j&& tasks.get(i).finishTime.compareTo(time)<0)
//				{
//					i++;
//				}
//				if(i<j)
//				{
//					tasks.set(i--,tasks.get(i));
//				}
//			}
//				tasks.set(i, task);
//				quicksort3(tasks, l,i-1);
//				quicksort3(tasks, i+1, r);
//				}
//			}
}
