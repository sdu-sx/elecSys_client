package com.sdjxd.elecsysclient.model;

import java.io.Serializable;
import java.util.Date;
import java.util.Vector;

/**
 * Classname:Task
 * Description:�������
 * @author ������
 * @version 1.0
 * */
public class Task implements Serializable
{
	/**
	 * 
	 */
	private static final long serialVersionUID = -7939011584841660917L;

	/**
	 * �����
	 * */
	public String tid;
	
	/**
	 * ������
	 * */
	public String tname;
	
	/**
	 * ���񷢲�ʱ��
	 * */
	public Date releaseTime;
	
	/**
	 * �����ֹ����
	 * */
	public Date deadLine;
	
	/**
	 * �����������
	 * */
	public Date finishTime;
	
	/**
	 * �����豸��
	 * */
	public int deviceNum;
	
	/**
	 * ����״̬
	 * */
	public TaskState state;
	
	/**
	 * �����豸�б�
	 * */
	private Vector<DeviceLite> devices;
	
	/**
	 * ����״̬ö����
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
