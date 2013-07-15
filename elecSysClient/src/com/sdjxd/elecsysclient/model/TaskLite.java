package com.sdjxd.elecsysclient.model;

import java.io.Serializable;
import java.util.Date;

import com.sdjxd.elecsysclient.model.Task.TaskState;

/**
 * Classname:TaskLite
 * Description:轻量级任务对象
 * @author 许凌霄
 * @version 1.0
 * */
public class TaskLite implements Serializable
{
	/**
	 * 
	 */
	private static final long serialVersionUID = 2930155625791034986L;

	/**
	 * 任务号
	 * */
	public String tid;
	
	/**
	 * 任务名
	 * */
	public String tname;
	
	/**
	 * 任务期限
	 * */
	public Date deadLine;
	
	/**
	 * 任务发布时间
	 * */
	public Date releaseTime;
	
	/**
	 * 任务完成时间
	 * */
	public Date finishTime;
	
	/**
	 * 任务设备数
	 * */
	public int deviceNumber;
	
	/**
	 * 任务类型
	 * */
	public TaskState state;
	public TaskLite(String tid,String tname,Date releaseTime,Date finishTime,Date deadLine,int deviceNumber,TaskState state)
	{
		this.tid=tid;
		this.tname=tname;
		this.releaseTime=releaseTime;
		this.deadLine=deadLine;
		this.finishTime=finishTime;
		this.deviceNumber=deviceNumber;
		this.state=state;
	}
	public TaskLite()
	{
		// TODO Auto-generated constructor stub
	}
	public String toString()
	{
		String result;
		result="{"+tid+","+tname+","+deviceNumber+","+state+","+releaseTime+","+finishTime+","+deadLine+"}";
		return result;
	}
}
