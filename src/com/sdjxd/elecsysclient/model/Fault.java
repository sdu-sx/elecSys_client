package com.sdjxd.elecsysclient.model;
import java.io.Serializable;
import java.util.Date;

/**
 * Classname:Fault
 * Description: �豸ȱ��
 * @author ������
 * @version 1.0
 * */
public class Fault implements Serializable,Comparable
{
	/**
	 * 
	 */
	private static final long serialVersionUID = -8015018608736463914L;
	/**
	 * ȱ�ݺ�
	 * */
	public String fid;
	/**
	 * ȱ�ݵ��豸��
	 * */
	public String did;
	
	/**
	 * ȱ�ݵ��������
	 * */
	public Date date;
	
	/**
	 * ȱ�ݵ�����
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
	@Override
	public int compareTo(Object other) 
	{
		if(other==null||!(other instanceof Fault))
		{
			throw new IllegalArgumentException();
		}
		int result;
		Fault fault=(Fault) other;
		if(date!=null&& fault.date!=null)
		{
			result=fault.date.compareTo(date);
		}
		else if(date==null&& fault.date!=null)
		{
			result=1;
		}
		else if(date!=null&& fault.date==null)
		{
			result=-1;
		}
		else
		{
			result=0;
		}
		return result;
	}

	
}
