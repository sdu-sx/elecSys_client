package com.sdjxd.elecsysclient.model;

import java.io.Serializable;

/**
 * Classname:DeviceLite
 * Description: �������豸����
 * @author ������
 * @version 1.0
 * */
public class DeviceLite implements Serializable
{
	/**
	 * 
	 */
	private static final long serialVersionUID = -8655174754148807065L;

	/**
	 * �豸��
	 * */
	public String did;
	
	/**
	 * �豸��
	 * */
	public String dName;
	
	/**
	 * �豸����
	 * */
	public String dType;
	
	/**
	 * �豸��ַ
	 * */
	public String dAddress;
	
	/**
	 * �豸�Ƿ��Ѽ�� 
	 * */
	public boolean isCheck=false;
}
