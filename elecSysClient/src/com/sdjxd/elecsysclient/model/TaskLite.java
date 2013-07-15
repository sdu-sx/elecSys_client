package com.sdjxd.elecsysclient.model;

import java.io.Serializable;
import java.util.Date;

import com.sdjxd.elecsysclient.model.Task.TaskState;

/**
 * Classname:TaskLite
 * Description:�������������
 * @author ������
 * @version 1.0
 * */
public class TaskLite implements Serializable
{
	/**
	 * 
	 */
	private static final long serialVersionUID = 2930155625791034986L;

	/**
	 * �����
	 * */
	public String tid;
	
	/**
	 * ������
	 * */
	public String tname;
	
	/**
	 * ��������
	 * */
	public Date deadLine;
	
	/**
	 * ���񷢲�ʱ��
	 * */
	public Date releaseTime;
	
	/**
	 * �������ʱ��
	 * */
	public Date finishTime;
	
	/**
	 * �����豸��
	 * */
	public int deviceNumber;
	
	/**
	 * ��������
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
