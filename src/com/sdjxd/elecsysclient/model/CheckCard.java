package com.sdjxd.elecsysclient.model;

import java.io.Serializable;

/**
 * Classname:CheckCard
 * Description: 标准作业卡（即条目专项）
 * @author 许凌霄
 * @version 1.0
 * */
public class CheckCard implements Serializable
{
	/**
	 * 
	 */
	private static final long serialVersionUID = 7031055530151987983L;

	/**
	 * 条目号
	 * */
	public String cid;
	
	/**
	 * 条目名
	 * */
	public String cName;
	
	/**
	 * 条目抄录值
	 * */
	public String cValue;
	
	/**
	 * 条目是否已验
	 * */
	public boolean isCheck=false;
	
	public String toString()
	{
		String result="{"+cid+","+cName+","+cValue+","+isCheck+"}";
		return result;
	}
}
