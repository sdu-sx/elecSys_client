package com.sdjxd.elecsysclient.model;

import java.io.Serializable;

/**
 * Classname:CheckCard
 * Description: ��׼��ҵ��������Ŀר�
 * @author ������
 * @version 1.0
 * */
public class CheckCard implements Serializable
{
	/**
	 * 
	 */
	private static final long serialVersionUID = 7031055530151987983L;

	/**
	 * ��Ŀ��
	 * */
	public String cid;
	
	/**
	 * ��Ŀ��
	 * */
	public String cName;
	
	/**
	 * ��Ŀ��¼ֵ
	 * */
	public String cValue;
	
	/**
	 * ��Ŀ�Ƿ�����
	 * */
	public boolean isCheck=false;
	
	public String toString()
	{
		String result="{"+cid+","+cName+","+cValue+","+isCheck+"}";
		return result;
	}
}
