package com.sdjxd.elecsysclient.model;
import java.io.Serializable;
import java.util.Date;

/**
 * Classname:Fault
 * Description: 设备缺陷
 * @author 许凌霄
 * @version 1.0
 * */
public class Fault implements Serializable
{
	/**
	 * 
	 */
	private static final long serialVersionUID = -8015018608736463914L;
	/**
	 * 缺陷号
	 * */
	public String fid;
	/**
	 * 缺陷的设备号
	 * */
	public String did;
	
	/**
	 * 缺陷的添加日期
	 * */
	public Date date;
	
	/**
	 * 缺陷的内容
	 * */
	public String content;
	
	public Fault(String did,String content)
	{
		fid="";
		this.did=did;
		this.content=content;
		date=null;
	}
	public Fault(String fid,String did,String content,Date time)
	{
		this.fid=fid;
		this.did=did;
		this.content=content;
		date=time;
	}
	public Fault()
	{
		// TODO Auto-generated constructor stub
	}
	public String toString()
	{
		String result="{"+fid+","+did+","+content+","+date.toString()+"}";
		return result;
	}

	
}
