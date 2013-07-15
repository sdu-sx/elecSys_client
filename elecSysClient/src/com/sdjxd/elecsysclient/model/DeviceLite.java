package com.sdjxd.elecsysclient.model;

import java.io.Serializable;

/**
 * Classname:DeviceLite
 * Description: 轻量级设备对象
 * @author 许凌霄
 * @version 1.0
 * */
public class DeviceLite implements Serializable
{
	/**
	 * 
	 */
	private static final long serialVersionUID = -8655174754148807065L;

	/**
	 * 设备号
	 * */
	public String did;
	
	/**
	 * 设备名
	 * */
	public String dName;
	
	/**
	 * 设备类型
	 * */
	public String dType;
	
	/**
	 * 设备地址
	 * */
	public String dAddress;
	
	/**
	 * 设备是否已检查 
	 * */
	public boolean isCheck=false;
}
