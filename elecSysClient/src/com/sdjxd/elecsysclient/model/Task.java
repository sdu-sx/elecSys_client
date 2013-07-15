package com.sdjxd.elecsysclient.model;

import java.io.Serializable;
import java.util.Date;
import java.util.Vector;

/**
 * Classname:Task
 * Description:任务对象
 * @author 许凌霄
 * @version 1.0
 * */
public class Task implements Serializable
{
	/**
	 * 
	 */
	private static final long serialVersionUID = -7939011584841660917L;

	/**
	 * 任务号
	 * */
	public String tid;
	
	/**
	 * 任务名
	 * */
	public String tname;
	
	/**
	 * 任务发布时间
	 * */
	public Date releaseTime;
	
	/**
	 * 任务截止日期
	 * */
	public Date deadLine;
	
	/**
	 * 任务完成日期
	 * */
	public Date finishTime;
	
	/**
	 * 任务设备数
	 * */
	public int deviceNum;
	
	/**
	 * 任务状态
	 * */
	public TaskState state;
	
	/**
	 * 任务设备列表
	 * */
	private Vector<DeviceLite> devices;
	
	/**
	 * 任务状态枚举类
	 * */
	public Task(String tid,String name,int deviceNumber,TaskState state, Date rt,Date ft,Date dl)
	{
		this.tid=tid;
		this.tname=name;
		this.deviceNum=deviceNumber;
		this.state=state;
		releaseTime=rt;
		finishTime=ft;
		deadLine=dl;
		devices=new Vector<DeviceLite>();
	}
	
	public Task() {
		// TODO Auto-generated constructor stub
	}

	public boolean addDevice(DeviceLite device)
	{
		boolean result=false;
		if(device!=null)
		{
			devices.add(device);
			result=true;
		}
		return result;
	}
	public String toString()
	{
		String result;
		result="{"+tid+","+tname+","+deviceNum+","+state.name()+","+releaseTime+","+finishTime+","+deadLine+","+getDevices().toString()+"}";
		return result;
	}
	/**
	 * @return the devices
	 */
	public Vector<DeviceLite> getDevices() 
	{
		return devices;
	}

	/**
	 * @param devices the devices to set
	 */
	public boolean setDevices(Vector<DeviceLite> devices) 
	{
		boolean result=false;
		if(devices!=null)
		{
			this.devices = devices;
			result=true;
		}
		return result;
	}
	
	public enum TaskState implements Serializable
	{
		DONE,UNDO,OVERTIME
	}
}
